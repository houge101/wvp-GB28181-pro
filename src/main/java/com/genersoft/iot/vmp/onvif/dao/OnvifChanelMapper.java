package com.genersoft.iot.vmp.onvif.dao;

import com.genersoft.iot.vmp.onvif.bean.OnvifDeviceChannel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OnvifChanelMapper {

    @Insert("INSERT INTO wvp_onvif_device_channel (" +
            "device_id, " +
            "name, " +
            "status, " +
            "ip, " +
            "port, " +
            "username, " +
            "password, " +
            "manufacturer, " +
            "model, " +
            "firmware_version, " +
            "serial_number, " +
            "hardware_id, " +
            "date, " +
            "live_stream_tcp, " +
            "live_stream_udp, " +
            "live_stream_multicast, " +
            "replay_stream, " +
            "create_time, " +
            "update_time " +
            ") VALUES (" +
            "#{deviceId}," +
            "#{name}," +
            "#{status}," +
            "#{ip}," +
            "#{port}," +
            "#{username}," +
            "#{password}," +
            "#{manufacturer}," +
            "#{model}," +
            "#{firmwareVersion}," +
            "#{serialNumber}," +
            "#{hardwareId}," +
            "#{date}," +
            "#{liveStreamTcp}," +
            "#{liveStreamUdp}," +
            "#{liveStreamMulticast}," +
            "#{replayStream}," +
            "#{createTime}," +
            "#{updateTime}" +
            ")")
    void add(OnvifDeviceChannel onvifDeviceChannel);


    @Update(value = {" <script>" +
            "UPDATE wvp_onvif_device_channel " +
            "SET update_time=#{updateTime}" +
            "<if test=\"name != null\">, name=#{name}</if>" +
            "<if test=\"status != null\">, status=#{status}</if>" +
            "<if test=\"enableAudio != null\">, enable_audio=#{enableAudio}</if>" +
            "<if test=\"enableMp4 != null\">, enable_mp4=#{enableMp4}</if>" +
            "<if test=\"username != null\">, username=#{username}</if>" +
            "<if test=\"password != null\">, password=#{password}</if>" +
            "<if test=\"manufacturer != null\">, manufacturer=#{manufacturer}</if>" +
            "<if test=\"model != null\">, model=#{model}</if>" +
            "<if test=\"firmwareVersion != null\">, firmware_version=#{firmwareVersion}</if>" +
            "<if test=\"serialNumber != null\">, serial_number=#{serialNumber}</if>" +
            "<if test=\"hardwareId != null\">, hardware_id=#{hardwareId}</if>" +
            "<if test=\"date != null\">, date=#{date}</if>" +
            "<if test=\"liveStreamTcp != null\">, live_stream_tcp=#{liveStreamTcp}</if>" +
            "<if test=\"liveStreamUdp != null\">, live_stream_udp=#{liveStreamUdp}</if>" +
            "<if test=\"liveStreamMulticast != null\">, live_stream_multicast=#{liveStreamMulticast}</if>" +
            "<if test=\"replayStream != null\">, replay_stream=#{replayStream}</if>" +
            "WHERE ip=#{ip} and port=#{port}"+
            " </script>"})
    void updateByIpAndPort(OnvifDeviceChannel onvifDeviceChannel);

    @Select("select * from wvp_onvif_device_channel")
    List<OnvifDeviceChannel> getAll();
    @Select("select * from wvp_onvif_device_channel where id = #{id}")
    OnvifDeviceChannel getOne(int id);

    @Update(value = {" <script>" +
            "UPDATE wvp_onvif_device_channel " +
            "SET update_time=#{updateTime}" +
            "<if test=\"name != null\">, name=#{name}</if>" +
            "<if test=\"status != null\">, status=#{status}</if>" +
            "<if test=\"username != null\">, username=#{username}</if>" +
            "<if test=\"password != null\">, password=#{password}</if>" +
            "<if test=\"manufacturer != null\">, manufacturer=#{manufacturer}</if>" +
            "<if test=\"model != null\">, model=#{model}</if>" +
            "<if test=\"ip != null\">, ip=#{ip}</if>" +
            "<if test=\"port != null\">, port=#{port}</if>" +
            "<if test=\"firmwareVersion != null\">, firmware_version=#{firmwareVersion}</if>" +
            "<if test=\"serialNumber != null\">, serial_number=#{serialNumber}</if>" +
            "<if test=\"hardwareId != null\">, hardware_id=#{hardwareId}</if>" +
            "<if test=\"date != null\">, date=#{date}</if>" +
            "<if test=\"liveStreamTcp != null\">, live_stream_tcp=#{liveStreamTcp}</if>" +
            "<if test=\"liveStreamUdp != null\">, live_stream_udp=#{liveStreamUdp}</if>" +
            "<if test=\"liveStreamMulticast != null\">, live_stream_multicast=#{liveStreamMulticast}</if>" +
            "<if test=\"replayStream != null\">, replay_stream=#{replayStream}</if>" +
            "WHERE id=#{id} "+
            " </script>"})
    void update(OnvifDeviceChannel onvifDeviceChannel);

    @Select("select * from wvp_onvif_device_channel where ip = #{ip} and port = #{port}")
    OnvifDeviceChannel getOneByIpAndPort(String ip, int port);

    @Delete("DELETE from wvp_onvif_device_channel WHERE id=#{id}")
    void delete(int id);
}
