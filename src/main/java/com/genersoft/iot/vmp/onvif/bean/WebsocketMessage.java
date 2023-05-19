package com.genersoft.iot.vmp.onvif.bean;

import com.alibaba.fastjson2.JSONObject;

public class WebsocketMessage {

    private WebsocketMessageType messageType;
    private JSONObject data;

    public WebsocketMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(WebsocketMessageType messageType) {
        this.messageType = messageType;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
