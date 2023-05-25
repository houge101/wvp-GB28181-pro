package com.genersoft.iot.vmp.onvif.service;

import com.genersoft.iot.vmp.onvif.bean.OnvifDevice;
import com.genersoft.iot.vmp.onvif.bean.OnvifDeviceChannel;
import com.github.pagehelper.PageInfo;

public interface IOnvifService {
    void addDevice(OnvifDevice onvifDevice);

    void updateDevice(OnvifDevice onvifDevice);

    void addChannel(OnvifDeviceChannel onvifDeviceChannel);

    void updateChannel(OnvifDeviceChannel onvifDeviceChannel1);

    PageInfo<OnvifDevice> getAll(int page, int count);

    PageInfo<OnvifDeviceChannel> getAllChannels(int deviceId, int page, int count);

    void discovery(int deviceId, boolean clear);

    void queryChannelInfo(int deviceId, int id, String username, String password);

    OnvifDeviceChannel getChannelByIpAndPort(String ip, int port);

    void initDeviceStatus();

    OnvifDevice getDevice(int id);

    OnvifDeviceChannel getChannel(int id);

    void play(int channelId);

    void stop(int channelId);

    void deleteChannel(int channelId);
}
