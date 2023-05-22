package com.genersoft.iot.vmp.onvif.dao;

import com.genersoft.iot.vmp.onvif.bean.OnvifDevice;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OnvifDeviceMapper {


    @Insert("INSERT INTO wvp_onvif_device (" +
            "id, " +
            "name, " +
            "direct_connection, " +
            "status, " +
            "create_time, " +
            "update_time " +
            ") VALUES (" +
            "#{id}," +
            "#{name}," +
            "#{directConnection}," +
            "#{status}," +
            "#{createTime}," +
            "#{createTime}" +
            ")")
    void add(OnvifDevice onvifDevice);

    @Update(value = {" <script>" +
            "UPDATE wvp_onvif_device " +
            "SET update_time=#{updateTime}" +
            "<if test=\"name != null\">, name=#{name}</if>" +
            "<if test=\"directConnection != null\">, direct_connection=#{directConnection}</if>" +
            "<if test=\"status != null\">, status=#{status}</if>" +
            " WHERE id=#{id}"+
            " </script>"})
    void update(OnvifDevice onvifDevice);

    @Select("select * from wvp_onvif_device")
    List<OnvifDevice> getAll();

    @Update({"<script>" +
            "<foreach collection='onvifDevices' item='item' separator=';'>" +
            " UPDATE" +
            " wvp_onvif_device" +
            " SET update_time=#{item.updateTime}" +
            ", status=#{item.status}" +
            " WHERE id=#{item.id}" +
            "</foreach>" +
            "</script>"})
    void batchUpdateStatus(List<OnvifDevice> onvifDevices);

    @Select("select * from wvp_onvif_device where id = #{id}")
    OnvifDevice getOne(int id);
}
