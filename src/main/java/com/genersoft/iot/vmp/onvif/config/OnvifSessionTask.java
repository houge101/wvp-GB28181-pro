package com.genersoft.iot.vmp.onvif.config;

import com.genersoft.iot.vmp.onvif.service.IOnvifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value=15)
public class OnvifSessionTask implements CommandLineRunner {

    @Autowired
    private IOnvifService onvifService;

    @Override
    public void run(String... args) throws Exception {
        // 重置所有onvif设备的状态
        onvifService.initDeviceStatus();
    }
}
