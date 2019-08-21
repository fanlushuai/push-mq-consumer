package com.auh.open.mq.consumer.mapper;

import com.auh.open.mq.consumer.entity.Device;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceMapper extends BaseMapper<Device> {

    @Select({"SELECT push_token FROM t_device where platform=#{platform}"})
    List<String> listPushToken(@Param("platform") String platform);

    @Select({"SELECT push_token FROM t_device where platform=#{platform}"})
    Page<String> listPushToken(IPage page, @Param("platform") String platform);

    @Select({"SELECT push_token FROM t_device where platform=#{platform} and device_push_settings & #{device_push_settings}"})
    Page<String> listPushTokenByPushSettings(IPage page, @Param("platform") String platform, @Param("device_push_settings") Long devicePushSettings);

    @Select({"select * from t_device where imei=#{imei} limit 1"})
    Device getByImei(@Param("imei") String imei);

    @Update({"update t_device set device_push_settings=#{device_push_settings},update_time=UNIX_TIMESTAMP()*1000 where imei=#{imei}"})
    int updateDevicePushSettings(@Param("imei") String imei, @Param("device_push_settings") Long devicePushSettings);

    @Select({"select * from t_device where jid=#{jid}"})
    List<Device> listByJId(@Param("jid") String jid);

    @Select({"select id,imei,device_push_settings from t_device where jid=#{jid}"})
    List<Device> listDevicePushSettings(@Param("jid") String jid);

    @Select({"select * from t_device where jid=#{jid} order by update_time desc limit 3"})
    List<Device> listTargetPushDevice(@Param("jid") String jid);


}
