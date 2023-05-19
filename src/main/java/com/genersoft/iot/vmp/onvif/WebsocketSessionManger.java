package com.genersoft.iot.vmp.onvif;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理连接的Onvif节点的session
 */
@Component
public class WebsocketSessionManger {

    private final static Logger logger = LoggerFactory.getLogger(WebsocketSessionManger.class);

    private final Map<Integer, Session> sessionMap = new ConcurrentHashMap<>();

    public void addSession(int id, Session session) {
        sessionMap.put(id, session);
    }

    public void removeSession(int id) {
        if (sessionMap.get(id) != null) {
            Session session = sessionMap.get(id);
            try {
                session.close();
            } catch (IOException e) {
                logger.warn("[节点Session] 关闭失败，", e);
            }
            sessionMap.remove(id);
        }
    }

    public void removeSessionCatch(int id) {
        if (sessionMap.get(id) != null) {
            sessionMap.remove(id);
        }
    }

    public Session getSession(int id) {
        if (sessionMap.get(id) == null) {
            return null;
        }
        return sessionMap.get(id);
    }
}
