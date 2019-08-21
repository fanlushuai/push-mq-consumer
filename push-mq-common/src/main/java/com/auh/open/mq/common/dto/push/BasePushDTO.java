package com.auh.open.mq.common.dto.push;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class BasePushDTO implements Serializable {

    public String title;

    public String content;

    String desc;

    String link;

    Long createTime;

    Long sendTime;

    Long expiredTime;

    String platform;

    String pushId;

    Map<String, String> custom;

}
