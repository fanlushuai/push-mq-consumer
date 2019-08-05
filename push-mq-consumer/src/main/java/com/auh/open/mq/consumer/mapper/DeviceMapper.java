package com.auh.open.mq.consumer.mapper;

import com.auh.open.mq.consumer.entity.Device;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {

    @Select({"SELECT push_token FROM t_device where platform=#{platform} order by id desc"})
    List<String> listPushToken(@Param("platform") String platform);

}
