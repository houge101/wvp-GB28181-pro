package com.genersoft.iot.vmp.onvif.service.impl;

import com.alibaba.fastjson2.JSON;
import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.onvif.WebsocketSessionManger;
import com.genersoft.iot.vmp.onvif.bean.OnvifDevice;
import com.genersoft.iot.vmp.onvif.bean.OnvifDeviceChannel;
import com.genersoft.iot.vmp.onvif.bean.WebsocketMessage;
import com.genersoft.iot.vmp.onvif.bean.WebsocketMessageType;
import com.genersoft.iot.vmp.onvif.dao.OnvifChanelMapper;
import com.genersoft.iot.vmp.onvif.dao.OnvifDeviceMapper;
import com.genersoft.iot.vmp.onvif.service.IOnvifService;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
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

        WebsocketMessage message = new WebsocketMessage();
        message.setMessageType(WebsocketMessageType.DISCOVERY);
        asyncRemote.sendText(JSON.toJSONString(message));
    }

    @Override
    public void queryChannelInfo(int deviceId, int id, String username, String password) {

    }
}
