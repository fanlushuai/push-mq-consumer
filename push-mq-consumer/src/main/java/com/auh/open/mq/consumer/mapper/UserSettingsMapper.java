package com.auh.open.mq.consumer.mapper;

import com.auh.open.mq.consumer.entity.UserSettings;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSettingsMapper extends BaseMapper<UserSettings> {

    @Select("select user_push_settings from t_user_settings where jid=#{jid} limit 1")
    Long getUserPushSettings(@Param("jid") String jid);

    /**
     * 与运算,查询设置匹配的用户
     */

    @Select("select jid from t_user_settings where user_push_settings & #{user_push_settings}")
    Page<String> listByUserPushSettings(IPage page, @Param("user_push_settings") Long userPushSettings);

    @Update("update t_user_settings set user_push_settings=#{user_push_settings},update_time=UNIX_TIMESTAMP()*1000 where jid=#{jid}")
    int updateUserPushSettings(@Param("jid") String jid, @Param("user_push_settings") Long userPushSettings);

    /**
     * 与运算,查询设置匹配的用户
     */
    @Select("select jid from t_user_settings where user_push_settings & #{user_push_settings}")
    List<String> listByUserPushSettings(@Param("user_push_settings") Long userPushSettings);


}
