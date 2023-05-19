package com.genersoft.iot.vmp.onvif;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.genersoft.iot.vmp.onvif.bean.OnvifDevice;
import com.genersoft.iot.vmp.onvif.bean.OnvifDeviceChannel;
import com.genersoft.iot.vmp.onvif.bean.WebsocketMessage;
import com.genersoft.iot.vmp.onvif.service.IOnvifService;
import com.genersoft.iot.vmp.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 处理websocket消息
 */
@Component
public class WebsocketMessageHandler {

    private final static Logger logger = LoggerFactory.getLogger(WebsocketMessageHandler.class);

    @Autowired
    private IOnvifService onvifService;

    public void handMessage(int id, String message) {
        WebsocketMessage wsMsg = JSON.parseObject(message, WebsocketMessage.class);
        JSONObject data = wsMsg.getData();
        switch (wsMsg.getMessageType()) {
            case INFO:
                OnvifDevice onvifDevice = JSON.to(OnvifDevice.class, data);
                String name = data.getString("name");
                Boolean directConnection = data.getBoolean("directConnection");
                logger.info("[onvif 消息处理] 收到节点信息： id: {}, 名称：{}， 直连：{} ", id, name, directConnection);
                onvifDevice.setId(id);
                onvifDevice.setCreateTime(DateUtil.getNow());
                onvifDevice.setUpdateTime(DateUtil.getNow());
                onvifService.updateDevice(onvifDevice);
                break;
            case DISCOVERY:
                OnvifDeviceChannel onvifDeviceChannel = JSON.to(OnvifDeviceChannel.class, data);
                logger.info("[onvif 消息处理] 发现摄像头：id: {}, IP:{}, 端口：{} ", id, onvifDeviceChannel.getIp(), onvifDeviceChannel.getPort());
                onvifDeviceChannel.setDeviceId(id);
                onvifDeviceChannel.setCreateTime(DateUtil.getNow());
                onvifDeviceChannel.setUpdateTime(DateUtil.getNow());
                onvifService.addChannel(onvifDeviceChannel);
                break;
            case CAMERA_INFO:
                logger.info("[onvif 消息处理] 摄像头详情：id: {}, 详情：{} ", id, data);
                OnvifDeviceChannel onvifChannel = JSON.to(OnvifDeviceChannel.class, data);
                if (onvifChannel.getIp() == null || onvifChannel.getPort() == 0) {
                    logger.warn("[onvif 消息处理] 内容中缺少IP以及端口");
                    break;
                }
                onvifChannel.setDeviceId(id);
                onvifChannel.setUpdateTime(DateUtil.getNow());
                onvifService.updateChannel(onvifChannel);
                break;
            default:
                break;
        }
    }
}
