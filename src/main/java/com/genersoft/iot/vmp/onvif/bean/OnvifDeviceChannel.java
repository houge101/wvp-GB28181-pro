package com.genersoft.iot.vmp.onvif.bean;

/**
 * onvif检索出来的通道信息
 */
public class OnvifDeviceChannel {

    /**
     * 所属的onvif设备的ID
     */
    private int deviceId;

    /**
     * 通道名称
     */
    private String name;

    /**
     * 状态， 一般参考onvif设备的状态
     */
    private boolean status;

    /**
     * 国标ID， 后续可以扩展后通过国标级联发送给其他平台
     */
    private String gbId;

    /**
     * 设备相机IP
     */
    private String ip;

    /**
     * 摄相机端口号
     */
    private int port;

    /**
     * 摄相机onvif 用户名
     */
    private String username;

    /**
     * 摄相机onvif 密码
     */
    private String password;

    /**
     * 摄相机 制造商
     */
    private String manufacturer;

    /**
     * 摄相机 型号
     */
    private String model;

    /**
     * 摄相机 固件版本
     */
    private String firmwareVersion;

    /**
     * 摄相机 序列号
     */
    private String serialNumber;

    /**
     * 摄相机 硬件Id
     */
    private String hardwareId;

    /**
     * 摄相机 日期
     */
    private String date;

    /**
     * 摄相机onvif tcp实时流
     */
    private String liveStreamTcp;

    /**
     * 摄相机onvif udp实时流
     */
    private String liveStreamUdp;

    /**
     * 摄相机onvif 多播实时流
     */
    private String liveStreamMulticast;

    /**
     * 摄相机onvif 回放流
     */
    private String replayStream;

    /**
     * 摄相机onvif 录像列表
     */
    private String recordings;

    /**
     * 经度
     */
    private double longitude;

    /**
     * 纬度
     */
    private double latitude;

    private String createTime;

    private String updateTime;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getGbId() {
        return gbId;
    }

    public void setGbId(String gbId) {
        this.gbId = gbId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getHardwareId() {
        return hardwareId;
    }

    public void setHardwareId(String hardwareId) {
        this.hardwareId = hardwareId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLiveStreamTcp() {
        return liveStreamTcp;
    }

    public void setLiveStreamTcp(String liveStreamTcp) {
        this.liveStreamTcp = liveStreamTcp;
    }

    public String getLiveStreamUdp() {
        return liveStreamUdp;
    }

    public void setLiveStreamUdp(String liveStreamUdp) {
        this.liveStreamUdp = liveStreamUdp;
    }

    public String getLiveStreamMulticast() {
        return liveStreamMulticast;
    }

    public void setLiveStreamMulticast(String liveStreamMulticast) {
        this.liveStreamMulticast = liveStreamMulticast;
    }

    public String getReplayStream() {
        return replayStream;
    }

    public void setReplayStream(String replayStream) {
        this.replayStream = replayStream;
    }

    public String getRecordings() {
        return recordings;
    }

    public void setRecordings(String recordings) {
        this.recordings = recordings;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
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
}
