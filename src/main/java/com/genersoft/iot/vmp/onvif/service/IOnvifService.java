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

    void discovery(int deviceId);

    void queryChannelInfo(int deviceId, int id, String username, String password);
}
