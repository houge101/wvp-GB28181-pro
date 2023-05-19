package com.genersoft.iot.vmp.onvif.bean;

/**
 * onvif 的流信息
 */
public class OnvifStream {

    /**
     * 流地址
     */
    private String uri;

    /**
     * 连接后是否失效
     */
    private boolean invalidAfterConnect;

    /**
     * 重启后是否失效
     */
    private boolean invalidAfterReboot;

    /**
     * 超时
     */
    private String timeout;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isInvalidAfterConnect() {
        return invalidAfterConnect;
    }

    public void setInvalidAfterConnect(boolean invalidAfterConnect) {
        this.invalidAfterConnect = invalidAfterConnect;
    }

    public boolean isInvalidAfterReboot() {
        return invalidAfterReboot;
    }

    public void setInvalidAfterReboot(boolean invalidAfterReboot) {
        this.invalidAfterReboot = invalidAfterReboot;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }
}
