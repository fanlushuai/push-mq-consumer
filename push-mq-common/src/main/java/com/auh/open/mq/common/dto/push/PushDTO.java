package com.auh.open.mq.common.dto.push;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PushDTO extends BasePushDTO {

    String msgId;

    String token;

    @Builder
    public PushDTO(String title, String content, String desc, String link, Long createTime, Long sendTime, Long expiredTime, String platform, String pushId, Custom custom, String msgId, String token) {
        super(title, content, desc, link, createTime, sendTime, expiredTime, platform, pushId, custom);
        this.msgId = msgId;
        this.token = token;
    }
}
