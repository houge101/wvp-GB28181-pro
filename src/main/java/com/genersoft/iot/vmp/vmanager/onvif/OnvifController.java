package com.genersoft.iot.vmp.vmanager.onvif;

import com.genersoft.iot.vmp.common.StreamInfo;
import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.gb28181.transmit.callback.DeferredResultHolder;
import com.genersoft.iot.vmp.gb28181.transmit.callback.RequestMessage;
import com.genersoft.iot.vmp.onvif.bean.ConstantHolder;
import com.genersoft.iot.vmp.onvif.bean.OnvifDevice;
import com.genersoft.iot.vmp.onvif.bean.OnvifDeviceChannel;
import com.genersoft.iot.vmp.onvif.service.IOnvifService;
import com.genersoft.iot.vmp.utils.DateUtil;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import com.genersoft.iot.vmp.vmanager.bean.StreamContent;
import com.genersoft.iot.vmp.vmanager.bean.WVPResult;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;

@Tag(name = "ONVIF设备管理")

@RestController
@RequestMapping("/api/onvif")
public class OnvifController {

    private final static Logger logger = LoggerFactory.getLogger(OnvifController.class);

    @Autowired
    private IOnvifService onvifService;

    @Autowired
    private DeferredResultHolder resultHolder;

    /**
     *  分页查询ONVIF设备
     *
     * @param page 当前页
     * @param count 每页查询数量
     * @return
     */
    @Operation(summary = "分页查询ONVIF设备")
    @Parameter(name = "page",description = "当前页",required = true)
    @Parameter(name = "count",description = "每页查询数量",required = true)
    @GetMapping("/device/all")
    public PageInfo<OnvifDevice> getAll(
            @RequestParam int page,
            @RequestParam int count
    ) {
       return onvifService.getAll(page, count);
    }

    /**
     *  分页查询ONVIF通道
     *
     * @param deviceId onvif设备Id
     * @param page 当前页
     * @param count 每页查询数量
     * @return
     */
    @Operation(summary = "分页查询ONVIF通道")
    @Parameter(name = "deviceId",description = "onvif设备Id",required = true)
    @Parameter(name = "page",description = "当前页",required = true)
    @Parameter(name = "count",description = "每页查询数量",required = true)
    @GetMapping("/channel/all")
    public PageInfo<OnvifDeviceChannel> getAllChannels(
            @RequestParam int deviceId,
            @RequestParam int page,
            @RequestParam int count
    ) {
        return onvifService.getAllChannels(deviceId, page, count);
    }

    /**
     *  扫描ONVIF通道
     *
     * @param deviceId onvif设备Id
     * @param clear 是否清空现有通道
     */
    @Operation(summary = "扫描ONVIF通道")
    @Parameter(name = "deviceId",description = "onvif设备Id",required = true)
    @Parameter(name = "clear",description = "是否清空现有通道",required = false)
    @GetMapping("/device/discovery")
    public void discovery(
            @RequestParam Integer deviceId,
            @RequestParam(required = false) Boolean clear
    ) {
        if (clear == null) {
            clear = false;
        }
        onvifService.discovery(deviceId, clear);
    }

    /**
     *  获取通道详情
     *
     * @param deviceId onvif设备Id
     * @param id onvif通道Id
     * @param username 用户名
     * @param password 密码
     */
    @Operation(summary = "获取通道详情")
    @Parameter(name = "deviceId",description = "onvif设备Id",required = true)
    @Parameter(name = "id",description = "onvif通道Id",required = true)
    @Parameter(name = "username",description = "用户名",required = true)
    @Parameter(name = "password",description = "密码",required = true)
    @GetMapping("/channel/info")
    public DeferredResult<OnvifDeviceChannel> queryChannelInfo(
            @RequestParam int deviceId,
            @RequestParam int id,
            @RequestParam String username,
            @RequestParam String password
    ) {

        RequestMessage requestMessage = new RequestMessage();
        String key = ConstantHolder.QUERY_CHANNEL_INFO + deviceId + id;
        requestMessage.setKey(key);
        String uuid = UUID.randomUUID().toString();
        requestMessage.setId(uuid);
        DeferredResult<OnvifDeviceChannel> result = new DeferredResult<>();

        result.onTimeout(()->{
            logger.info("[Onvif HTTP请求] 获取通道详情超时, 设备Id：{}， 通道I的： {}", deviceId, id);
            // 释放rtpServer
            WVPResult<StreamInfo> wvpResult = new WVPResult<>();
            wvpResult.setCode(ErrorCode.ERROR100.getCode());
            wvpResult.setMsg("获取通道详情超时");
            requestMessage.setData(wvpResult);
            resultHolder.invokeResult(requestMessage);
        });

        // 录像查询以channelId作为deviceId查询
        resultHolder.put(key, uuid, result);

        onvifService.queryChannelInfo(deviceId, id, username, password);
        return result;
    }

    /**
     *  编辑通道详情
     *
     * @param deviceChannel 通道详情
     */
    @Operation(summary = "编辑通道详情")
    @Parameter(name = "deviceChannel",description = "通道详情",required = true)
    @PostMapping("/channel/update")
    public DeferredResult<OnvifDeviceChannel> updateChannel(
            OnvifDeviceChannel deviceChannel
    ) {

//        if (deviceChannel.getId() == 0 || deviceChannel.getDeviceId() == 0) {
//            throw new ControllerException(ErrorCode.ERROR400);
//        }

        OnvifDeviceChannel deviceChannelInDb = onvifService.getChannel(deviceChannel.getId());

        if (deviceChannelInDb == null) {
            throw new ControllerException(ErrorCode.ERROR400);
        }

        RequestMessage requestMessage = new RequestMessage();
        String key = ConstantHolder.QUERY_CHANNEL_INFO + deviceChannel.getDeviceId() + deviceChannel.getId();
        requestMessage.setKey(key);
        String uuid = UUID.randomUUID().toString();
        requestMessage.setId(uuid);
        DeferredResult<OnvifDeviceChannel> result = new DeferredResult<>();

        result.onTimeout(()->{
            logger.info("[Onvif HTTP请求] 获取通道详情超时, 设备Id：{}， 通道I的： {}", deviceChannel.getDeviceId(), deviceChannel.getId());
            // 释放rtpServer
            WVPResult<StreamInfo> wvpResult = new WVPResult<>();
            wvpResult.setCode(ErrorCode.ERROR100.getCode());
            wvpResult.setMsg("获取通道详情超时");
            requestMessage.setData(wvpResult);
            resultHolder.invokeResult(requestMessage);
        });

        // 录像查询以channelId作为deviceId查询
        resultHolder.put(key, uuid, result);

        deviceChannel.setUpdateTime(DateUtil.getNow());
        onvifService.stop(deviceChannel.getId());
        onvifService.updateChannel(deviceChannel);

        if (deviceChannel.getUsername() != null && deviceChannel.getPassword() != null) {
            onvifService.queryChannelInfo(deviceChannel.getDeviceId(), deviceChannel.getId(),
                    deviceChannel.getUsername(), deviceChannel.getPassword());
        }else {
            // 释放请求
            resultHolder.invokeResult(requestMessage);
        }

        return result;
    }

    /**
     *  播放
     *
     * @param id onvif通道Id
     */
    @Operation(summary = "播放")
    @Parameter(name = "deviceId",description = "onvif通道Id",required = true)
    @GetMapping("/channel/play")
    public DeferredResult<StreamContent> play( int id ) {


        RequestMessage requestMessage = new RequestMessage();
        String key = ConstantHolder.PLAY_ONVIF_CHANNEL + id;
        requestMessage.setKey(key);
        String uuid = UUID.randomUUID().toString();
        requestMessage.setId(uuid);
        DeferredResult<StreamContent> result = new DeferredResult<>();

        result.onTimeout(()->{
            logger.info("[Onvif HTTP请求] 播放超时, 通道ID： {}", id);
            // 释放rtpServer
            WVPResult<StreamContent> wvpResult = new WVPResult<>();
            wvpResult.setCode(ErrorCode.ERROR100.getCode());
            wvpResult.setMsg("获取通道详情超时");
            requestMessage.setData(wvpResult);
            resultHolder.invokeResult(requestMessage);
        });

        // 录像查询以channelId作为deviceId查询
        resultHolder.put(key, uuid, result);

        onvifService.play(id);

        return result;
    }

    /**
     *  停止
     *
     * @param id onvif通道Id
     */
    @Operation(summary = "停止")
    @Parameter(name = "id",description = "onvif通道Id",required = true)
    @GetMapping("/channel/stop")
    public void stop( int id ) {
        onvifService.stop(id);
    }

    /**
     *  删除
     *
     * @param id onvif通道Id
     */
    @Operation(summary = "删除")
    @Parameter(name = "id",description = "onvif通道Id",required = true)
    @DeleteMapping("/channel/delete")
    public void delete( int id ) {
        onvifService.stop(id);
        onvifService.deleteChannel(id);
    }
}
