package com.auh.open.mq.common.dto.push;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PushByTokenDTO extends BasePushDTO {

    /**
     * 最多1000 个token
     * token字符串长度不能超过64
     */
    Set<String> pushTokens;

    @Builder
    public PushByTokenDTO(String title, String content, String desc, String link, Long createTime, Long sendTime, Long expiredTime, String platform, String pushId, Custom custom, Set<String> pushTokens) {
        super(title, content, desc, link, createTime, sendTime, expiredTime, platform, pushId, custom);
        this.pushTokens = pushTokens;
    }

}
