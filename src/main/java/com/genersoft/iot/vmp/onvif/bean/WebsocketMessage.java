package com.genersoft.iot.vmp.onvif.bean;

import com.alibaba.fastjson2.JSONObject;

public class WebsocketMessage {

    private WebsocketMessageType messageType;

    private int channelId;

    private String sn;

    private boolean success;

    private String msg;

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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }
}
