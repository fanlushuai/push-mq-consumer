package com.auh.open.mq.consumer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 设备表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_device")
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 推送token
     */
    @TableField("push_token")
    private String pushToken;

    /**
     * 设备唯一编号
     */
    @TableField("imei")
    private String imei;

    /**
     * 设备品牌
     */
    @TableField("platform")
    private String platform;

    /**
     * 用户编号
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 设备型号
     */
    @TableField("model")
    private String model;

    /**
     * 抽屉版本
     */
    @TableField("version")
    private String version;

    /**
     * 系统名称
     */
    @TableField("os")
    private String os;

    /**
     * 系统信息
     */
    @TableField("os_info")
    private String osInfo;

    /**
     * 设备语言
     */
    @TableField("language")
    private String language;

    /**
     * 时区
     */
    @TableField("timezone")
    private String timezone;

    /**
     * 添加时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Long updateTime;

    /**
     * 设备的推送设置，当设备无用户登录的时候，使用
     */
    @TableField("device_push_settings")
    private Long devicePushSettings;


}
