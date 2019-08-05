package com.auh.open.mq.common.dto.push;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class BasePushDTO {

    public String title;

    public String content;

    String desc;

    String link;

    Long createTime;

    Long sendTime;

    Long expiredTime;

    String platform;

    String pushId;

    Custom custom;

    static class Custom {

    }

}
