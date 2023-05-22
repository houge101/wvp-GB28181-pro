package com.genersoft.iot.vmp.onvif.bean;

/**
 * onvif设备 这是一个虚拟的概念，代表一个局域网中配置的onvif服务
 */
public class OnvifDevice {

    /**
     * 设备ID
     */
    private Integer id;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 是否是直连模式 直连模式作为wvp的onvif模块使用，直接给出流地址，
     * 后续扩展局域网模式， 本服务配合zlm可以将流推送到公网wvp流
     */
    private boolean directConnection;

    /**
     * 在线状态
     */
    private boolean status;

    private String createTime;

    private String updateTime;


    public OnvifDevice() {
    }

    public OnvifDevice(int id, String name, boolean directConnection) {
        this.id = id;
        this.name = name;
        this.directConnection = directConnection;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirectConnection() {
        return directConnection;
    }

    public void setDirectConnection(boolean directConnection) {
        this.directConnection = directConnection;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
