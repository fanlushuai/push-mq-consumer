package com.auh.open.mq.common.dto.push;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class PushAllDTO extends BasePushDTO {

    @Builder
    public PushAllDTO(String title, String content, String desc, String link, Long createTime, Long sendTime, Long expiredTime, String platform, String pushId, Custom custom) {
        super(title, content, desc, link, createTime, sendTime, expiredTime, platform, pushId, custom);
    }
}
