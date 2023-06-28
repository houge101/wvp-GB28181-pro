package com.genersoft.iot.vmp.onvif.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.genersoft.iot.vmp.common.StreamInfo;
import com.genersoft.iot.vmp.conf.DynamicTask;
import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.gb28181.transmit.callback.DeferredResultHolder;
import com.genersoft.iot.vmp.gb28181.transmit.callback.RequestMessage;
import com.genersoft.iot.vmp.media.zlm.ZLMRESTfulUtils;
import com.genersoft.iot.vmp.media.zlm.ZLMServerFactory;
import com.genersoft.iot.vmp.media.zlm.ZlmHttpHookSubscribe;
import com.genersoft.iot.vmp.media.zlm.dto.HookSubscribeFactory;
import com.genersoft.iot.vmp.media.zlm.dto.HookSubscribeForStreamChange;
import com.genersoft.iot.vmp.media.zlm.dto.MediaServerItem;
import com.genersoft.iot.vmp.media.zlm.dto.hook.OnStreamChangedHookParam;
import com.genersoft.iot.vmp.onvif.WebsocketSessionManger;
import com.genersoft.iot.vmp.onvif.bean.*;
import com.genersoft.iot.vmp.onvif.dao.OnvifChanelMapper;
import com.genersoft.iot.vmp.onvif.dao.OnvifDeviceMapper;
import com.genersoft.iot.vmp.onvif.service.IOnvifService;
import com.genersoft.iot.vmp.service.IMediaServerService;
import com.genersoft.iot.vmp.service.IMediaService;
import com.genersoft.iot.vmp.storager.IRedisCatchStorage;
import com.genersoft.iot.vmp.utils.DateUtil;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import com.genersoft.iot.vmp.vmanager.bean.StreamContent;
import com.genersoft.iot.vmp.vmanager.bean.WVPResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OnvifServiceImpl implements IOnvifService {

    private final static Logger logger = LoggerFactory.getLogger(OnvifServiceImpl.class);

    @Autowired
    private OnvifDeviceMapper onvifDeviceMapper;

    @Autowired
    private OnvifChanelMapper onvifChanelMapper;

    @Autowired
    private WebsocketSessionManger websocketSessionManger;

    @Autowired
    private ZLMServerFactory zlmServerFactory;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private IMediaService mediaService;

    @Autowired
    private DeferredResultHolder resultHolder;

    @Autowired
    private IRedisCatchStorage redisCatchStorage;

    @Autowired
    private ZLMRESTfulUtils zlmresTfulUtils;

    @Autowired
    private ZlmHttpHookSubscribe subscribe;

    @Autowired
    private DynamicTask dynamicTask;


    @Override
    public void addDevice(OnvifDevice onvifDevice) {
        if (onvifDevice.getId() == null) {
            return;
        }
        onvifDeviceMapper.add(onvifDevice);
    }

    @Override
    public void updateDevice(OnvifDevice onvifDevice) {
        if (onvifDevice.getId() == null) {
            return;
        }
        onvifDeviceMapper.update(onvifDevice);
    }

    @Override
    public void addChannel(OnvifDeviceChannel onvifDeviceChannel) {
        if (onvifDeviceChannel.getIp() == null || onvifDeviceChannel.getPort() == 0) {
            return;
        }
        onvifChanelMapper.add(onvifDeviceChannel);
    }

    @Override
    public void updateChannel(OnvifDeviceChannel onvifDeviceChannel) {
        if (onvifDeviceChannel.getIp() == null || onvifDeviceChannel.getPort() == 0) {
            return;
        }
        onvifChanelMapper.updateByIpAndPort(onvifDeviceChannel);
    }


    @Override
    public PageInfo<OnvifDevice> getAll(int page, int count) {
        PageHelper.startPage(page, count);
        List<OnvifDevice> all = onvifDeviceMapper.getAll();
        return new PageInfo<>(all);
    }

    @Override
    public PageInfo<OnvifDeviceChannel> getAllChannels(int deviceId, int page, int count) {
        PageHelper.startPage(page, count);
        List<OnvifDeviceChannel> all = onvifChanelMapper.getAll();
        return new PageInfo<>(all);
    }

    @Override
    public void discovery(int deviceId, boolean clear) {
        logger.info("[onvif 请求扫描设备] id: {}, 是否清空：{}", deviceId, clear);
        if (clear) {
            onvifChanelMapper.clearByDeviceId(deviceId);
        }
        RemoteEndpoint.Async asyncRemote = getRemoteEndpoint(deviceId);

        WebsocketMessage message = new WebsocketMessage();
        message.setMessageType(WebsocketMessageType.DISCOVERY);
        asyncRemote.sendText(JSON.toJSONString(message));
    }

    @Override
    public void queryChannelInfo(int deviceId, int id, String username, String password) {
        logger.info("[onvif 查询设备信息] deviceId：{}，id: {}, username: {}, password: ****",
                deviceId, id, username);
        OnvifDeviceChannel onvifDeviceChannel = onvifChanelMapper.getOne(id);
        if (onvifDeviceChannel == null) {
            logger.info("[onvif 查询设备信息] 参数不全，id: {}", deviceId);
            throw new ControllerException(ErrorCode.ERROR400);
        }
        if (username == null) {
            username = onvifDeviceChannel.getUsername();
        }
        if (password == null) {
            password = onvifDeviceChannel.getPassword();
        }
        if (username == null || password == null) {
            logger.info("[onvif 查询设备信息] 用户名或密码为空，id: {}", deviceId);
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "用户名或密码为空");
        }

        if (!username.equals(onvifDeviceChannel.getUsername())
                || !password.equals(onvifDeviceChannel.getPassword()) ) {
            onvifDeviceChannel.setUsername(username);
            onvifDeviceChannel.setPassword(password);
            onvifDeviceChannel.setUpdateTime(DateUtil.getNow());
            onvifChanelMapper.update(onvifDeviceChannel);
        }

        RemoteEndpoint.Async asyncRemote = getRemoteEndpoint(deviceId);

        WebsocketMessage message = new WebsocketMessage();
        message.setMessageType(WebsocketMessageType.CAMERA_INFO);
        message.setChannelId(id);
        JSONObject data = new JSONObject();
        data.put("channelId", onvifDeviceChannel.getId());
        data.put("ip", onvifDeviceChannel.getIp());
        data.put("port", onvifDeviceChannel.getPort());
        data.put("username", username);
        data.put("password", password);
        message.setData(data);
        asyncRemote.sendText(JSON.toJSONString(message));

    }

    @Override
    public OnvifDeviceChannel getChannelByIpAndPort(String ip, int port) {
        return onvifChanelMapper.getOneByIpAndPort(ip, port);
    }

    @Override
    public void initDeviceStatus() {
        List<OnvifDevice> allDevice = onvifDeviceMapper.getAll();
        if (allDevice.size() == 0) {
            return;
        }
        List<OnvifDevice> updateOnvifDeviceAll = new ArrayList<>();
        for (OnvifDevice onvifDevice : allDevice) {
            Session session = websocketSessionManger.getSession(onvifDevice.getId());
            if (session == null || !session.isOpen()) {
                if (onvifDevice.isStatus()) {
                    onvifDevice.setStatus(false);
                    onvifDevice.setUpdateTime(DateUtil.getNow());
                    updateOnvifDeviceAll.add(onvifDevice);
                }
            }else {
                if (!onvifDevice.isStatus()) {
                    onvifDevice.setStatus(true);
                    onvifDevice.setUpdateTime(DateUtil.getNow());
                    updateOnvifDeviceAll.add(onvifDevice);
                }
            }
        }
        // 批量更新状态
        if (updateOnvifDeviceAll.size() > 0) {
            int limitCount = 300;
            if (updateOnvifDeviceAll.size() > limitCount) {
                for (int i = 0; i < updateOnvifDeviceAll.size(); i += limitCount) {
                    int toIndex = i + limitCount;
                    if (i + limitCount > updateOnvifDeviceAll.size()) {
                        toIndex = updateOnvifDeviceAll.size();
                    }
                    onvifDeviceMapper.batchUpdateStatus(updateOnvifDeviceAll.subList(i, toIndex));
                }
            }else {
                onvifDeviceMapper.batchUpdateStatus(updateOnvifDeviceAll);
            }
        }
    }

    private RemoteEndpoint.Async getRemoteEndpoint(int deviceId) {
        Session session = websocketSessionManger.getSession(deviceId);
        if (session == null) {
            logger.info("[onvif 请求扫描设备] 失败， id: {}, 设备尚未连接", deviceId);
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "设备尚未连接");
        }
        if (!session.isOpen()) {
            logger.info("[onvif 请求扫描设备] 失败， id: {}, 设备连接已关闭", deviceId);
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "设备连接已关闭");
        }

        RemoteEndpoint.Async asyncRemote = session.getAsyncRemote();
        if (asyncRemote ==null) {
            logger.info("[onvif 请求扫描设备] 消息发送失败，id: {}, 消息发送失败", deviceId);
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "消息发送失败");
        }
        return asyncRemote;
    }

    @Override
    public OnvifDevice getDevice(int id) {
        return onvifDeviceMapper.getOne(id);
    }

    @Override
    public OnvifDeviceChannel getChannel(int id) {
        return onvifChanelMapper.getOne(id);
    }

    @Override
    public void play( int channelId) {
        logger.info("[播放ONVIF] 通道ID：{}", channelId);
        OnvifDeviceChannel channel = onvifChanelMapper.getOne(channelId);
        if (channel == null) {
            throw new ControllerException(ErrorCode.ERROR400);
        }

        RequestMessage requestMessage = new RequestMessage();
        String key = ConstantHolder.PLAY_ONVIF_CHANNEL + channelId;
        requestMessage.setKey(key);

        String type = "onvif";
        String app = "onvif";
        String stream = channel.getId() + "";
        OnStreamChangedHookParam streamInfo = redisCatchStorage.getStreamInfo(app, stream, null);
        if (streamInfo != null) {
            String mediaServerId = streamInfo.getMediaServerId();
            MediaServerItem mediaServerItem = mediaServerService.getOne(mediaServerId);
            if (mediaServerItem == null) {
                redisCatchStorage.removeStream(mediaServerId, type, app, stream);
            }else {
                Boolean streamReady = zlmServerFactory.isStreamReady(mediaServerItem, app, stream);
                if (streamReady == null || !streamReady) {
                    redisCatchStorage.removeStream(mediaServerId, type, app, stream);
                }else {
                    logger.info("[播放ONVIF] 已拉起，直接返回，通道ID：{}", channelId);
                    StreamInfo resultStreamInfo = mediaService.getStreamInfoByAppAndStream(mediaServerItem, app, stream, null, null);

                    requestMessage.setData(new StreamContent(resultStreamInfo));
                    resultHolder.invokeAllResult(requestMessage);
                    return;
                }
            }
        }
        MediaServerItem mediaServerItem = mediaServerService.getMediaServerForMinimumLoad(null);
        if (mediaServerItem == null) {
            logger.info("[播放ONVIF] 失败，没有可用的ZLM，通道ID：{}", channelId);
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "没有可用的ZLM");
        }
        // tcp拉流
        String rtpType = "0";
        String uriStr = channel.getLiveStreamTcp();
        if (uriStr == null) {
            if (channel.getLiveStreamUdp() != null) {
                // udp拉流
                rtpType = "1";
                uriStr = channel.getLiveStreamUdp();
            }else if (channel.getLiveStreamMulticast() != null) {
                // 组播拉流
                rtpType = "2";
                uriStr = channel.getLiveStreamMulticast();
            }else {
                logger.info("[播放ONVIF] 失败，没有可用的播放地址，通道ID：{}", channelId);
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "没有可用的播放地址");
            }
        }
        String rtspUri = getPlayUriByUsernameAndPassword(uriStr, channel.getUsername(), channel.getPassword());
        if (rtspUri == null) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "流地址解析失败");
        }
        JSONObject jsonObject = zlmresTfulUtils.addStreamProxy(mediaServerItem, app, stream, rtspUri,
                channel.isEnableAudio(), channel.isEnableMp4(), rtpType);
        System.out.println(jsonObject);
        if (jsonObject == null) {
            logger.info("[播放ONVIF] 失败，结果为空，通道ID：{}", channelId);
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "拉流失败，结果为空");
        }
        HookSubscribeForStreamChange hookSubscribe = HookSubscribeFactory.on_stream_changed(app, stream, true, "rtsp", mediaServerItem.getId());
        // 添加订阅
        subscribe.addSubscribe(hookSubscribe, (mediaServerItemInUse, hookParam) -> {
            logger.info("[播放ONVIF] 成功，通道ID：{}", channelId);
            subscribe.removeSubscribe(hookSubscribe);
            StreamInfo resultStreamInfo = mediaService.getStreamInfoByAppAndStream(mediaServerItem, app, stream, null, null);
            requestMessage.setData(new StreamContent(resultStreamInfo));
            resultHolder.invokeAllResult(requestMessage);
        });

        dynamicTask.startDelay(key, ()->{
            logger.info("[播放ONVIF] 超时，通道ID：{}", channelId);
            subscribe.removeSubscribe(hookSubscribe);
            stop(channelId);
            requestMessage.setData(WVPResult.fail(ErrorCode.ERROR100.getCode(), "播放超时"));
            resultHolder.invokeAllResult(requestMessage);
        }, 10000);

        if (jsonObject.getInteger("code") != 0) {
            subscribe.removeSubscribe(hookSubscribe);
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "拉流失败，错误码：" + jsonObject.getInteger("code"));
        }
    }

    @Override
    public void stop(int channelId) {
        logger.info("[停止ONVIF] 通道ID：{}", channelId);
        OnvifDeviceChannel channel = onvifChanelMapper.getOne(channelId);
        if (channel == null) {
            throw new ControllerException(ErrorCode.ERROR400);
        }
        String type = "onvif";
        String app = "onvif";
        String stream = channel.getId() + "";
        OnStreamChangedHookParam streamInfo = redisCatchStorage.getStreamInfo(app, stream, null);
        if (streamInfo != null) {
            String mediaServerId = streamInfo.getMediaServerId();
            if (mediaServerId != null) {
                MediaServerItem mediaServerItem = mediaServerService.getOne(mediaServerId);
                if (mediaServerItem != null) {
                    zlmresTfulUtils.closeStreams(mediaServerItem, app, stream);
                }
            }
            redisCatchStorage.removeStream(streamInfo.getMediaServerId(), type, app, stream);

        }
    }

    private String getPlayUriByUsernameAndPassword(String uriStr, String username, String password ){
        try {
            if (username != null || password != null) {
                URIBuilder uriBuilder = new URIBuilder(uriStr).setUserInfo(username, password);
                return uriBuilder.toString();
            }else {
                return uriStr;
            }
        } catch (URISyntaxException e) {
            logger.error("[ONVIF] 解析URI({})异常, ", uriStr, e);
            return null;
        }
    }

    @Override
    public void deleteChannel(int channelId) {
        onvifChanelMapper.delete(channelId);
    }
}
