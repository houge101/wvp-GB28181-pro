package com.genersoft.iot.vmp.onvif.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.onvif.WebsocketSessionManger;
import com.genersoft.iot.vmp.onvif.bean.OnvifDevice;
import com.genersoft.iot.vmp.onvif.bean.OnvifDeviceChannel;
import com.genersoft.iot.vmp.onvif.bean.WebsocketMessage;
import com.genersoft.iot.vmp.onvif.bean.WebsocketMessageType;
import com.genersoft.iot.vmp.onvif.dao.OnvifChanelMapper;
import com.genersoft.iot.vmp.onvif.dao.OnvifDeviceMapper;
import com.genersoft.iot.vmp.onvif.service.IOnvifService;
import com.genersoft.iot.vmp.utils.DateUtil;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
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
    public void discovery(int deviceId) {
        logger.info("[onvif 请求扫描设备] id: {}", deviceId);
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
        JSONObject data = new JSONObject();
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
}
