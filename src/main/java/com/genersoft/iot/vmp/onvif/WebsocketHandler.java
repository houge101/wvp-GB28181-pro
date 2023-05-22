package com.genersoft.iot.vmp.onvif;

import com.genersoft.iot.vmp.conf.UserSetting;
import com.genersoft.iot.vmp.onvif.bean.OnvifDevice;
import com.genersoft.iot.vmp.onvif.service.IOnvifService;
import com.genersoft.iot.vmp.utils.DateUtil;
import org.junit.jupiter.api.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@Component
@ServerEndpoint("/onvif/{serverId}/{id}")
@Order(1)
public class WebsocketHandler {

    private final static Logger logger = LoggerFactory.getLogger(WebsocketHandler.class);

    private static UserSetting userSetting;
    private static WebsocketMessageHandler messageHandler;
    private static WebsocketSessionManger sessionManger;
    private static IOnvifService onvifService;


    @Autowired
    public void setOnvifService(IOnvifService onvifService) {
        WebsocketHandler.onvifService = onvifService;
    }

    @Autowired
    public void setUserSetting(UserSetting userSetting) {
        WebsocketHandler.userSetting = userSetting;
    }

    @Autowired
    public void setMessageHandler(WebsocketMessageHandler messageHandler) {
        WebsocketHandler.messageHandler = messageHandler;
    }

    @Autowired
    public void setSessionManger(WebsocketSessionManger sessionManger) {
        WebsocketHandler.sessionManger = sessionManger;
    }

    @OnOpen // 标注的打开websocket链接的方法
    public void onOpen(@PathParam("serverId") String serverId, @PathParam("id") int id, Session session){
        logger.info("[Onvif节点] 收到新连接: id: {}", id);
        if (!userSetting.getServerId().equals(serverId)) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NOT_CONSISTENT, "鉴权失败"));
                logger.info("[Onvif节点] 鉴权失败: id: {}", id);
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        sessionManger.addSession(id, session);
        OnvifDevice onvifDevice = onvifService.getDevice(id);
        if (onvifDevice == null) {
            onvifDevice = new OnvifDevice();
            onvifDevice.setId(id);
            onvifDevice.setCreateTime(DateUtil.getNow());
            onvifDevice.setUpdateTime(DateUtil.getNow());
            onvifService.addDevice(onvifDevice);
        }else {
            onvifDevice.setStatus(true);
            onvifDevice.setUpdateTime(DateUtil.getNow());
            onvifService.updateDevice(onvifDevice);
        }

    }

    @OnMessage // 标注的监听websocket消息的方法
    public void onMessage(@PathParam("id") int id,String msg){
        logger.info("[Onvif节点] 消息: id: {}, 内容： {}", id, msg);
        messageHandler.handMessage(id, msg);
    }

    @OnError  // 标注的websocket异常的方法
    public void onError(@PathParam("id") int id,Session session,Throwable error) throws IOException {
        logger.error("[Onvif节点] 收到错误: id: {}", id, error);
        sessionManger.removeSession(id);
    }

    @OnClose // 标注的websocket关闭的方法
    public void onClose(Session session,@PathParam("id") int id) throws IOException {
        logger.error("[Onvif节点] 连接关闭: id: {}", id);
        sessionManger.removeSession(id);
    }
}
