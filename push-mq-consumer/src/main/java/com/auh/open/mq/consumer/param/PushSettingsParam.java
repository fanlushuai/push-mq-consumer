package com.auh.open.mq.consumer.param;

import lombok.Data;

@Data
public class PushSettingsParam {

    /**
     * 用户id 。和deviceId 必须存在任意一个
     */
    String jid;

    /**
     * 用户设备唯一id。在无用户登录的情况下的设置
     */
    String deviceId;

    /**
     * 新闻推送状态 1开启 0关闭
     */
    Integer newsPush;

    /**
     * 评论推送状态 1开启 0关闭
     */
    Integer commentPush;

    /**
     * 聊天推送状态 1开启 0关闭
     */
    Integer chatPush;

    /**
     * 夜间模式推送状态 1开启 0关闭
     */
    Integer nightPush;

    /**
     * 推送提示音
     */
    Integer pushNotifyVoice;


}
