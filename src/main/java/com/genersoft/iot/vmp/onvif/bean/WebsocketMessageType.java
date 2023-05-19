package com.genersoft.iot.vmp.onvif.bean;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WebsocketMessageType {

    DISCOVERY(1, "discovery"),
    CAMERA_INFO(2, "camera_info"),
    PTZ(3, "ptz"),
    INFO(4, "info"),
    ;

    private int code;
    private String describe;
    WebsocketMessageType(int code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
