package com.auh.open.mq.consumer.vo;

import lombok.Data;

@Data
public class PushSettingsVo {

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
