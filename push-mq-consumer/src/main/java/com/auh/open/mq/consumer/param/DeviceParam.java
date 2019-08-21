package com.auh.open.mq.consumer.param;

import lombok.Data;

@Data
public class DeviceParam {

    private String pushToken;

    /**
     * 设备唯一编号
     */
    private String imei;

    /**
     * 设备品牌
     */
    private String platform;

    /**
     * 用户编号
     */
    private String jid;

    /**
     * 设备型号
     */
    private String model;

    /**
     * app版本
     */
    private String version;

    /**
     * 系统名称
     */
    private String os;

    /**
     * 系统信息
     */
    private String osInfo;

    /**
     * 设备语言
     */
    private String language;

    /**
     * 时区
     */
    private String timezone;

}
