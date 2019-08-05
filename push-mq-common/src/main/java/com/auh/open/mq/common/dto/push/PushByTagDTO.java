package com.auh.open.mq.common.dto.push;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PushByTagDTO extends BasePushDTO {

    Set<String> tags;

    @Builder
    public PushByTagDTO(String title, String content, String desc, String link, Long createTime, Long sendTime, Long expiredTime, String platform, String pushId, Custom custom, Set<String> tags) {
        super(title, content, desc, link, createTime, sendTime, expiredTime, platform, pushId, custom);
        this.tags = tags;
    }
}
