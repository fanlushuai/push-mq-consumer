CREATE TABLE `t_device`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `push_token`  varchar(64)                  DEFAULT NULL COMMENT '推送token',
    `imei`        varchar(40)                  DEFAULT NULL COMMENT '设备唯一编号',
    `platform`    varchar(30)                  DEFAULT NULL COMMENT '设备品牌',
    `user_id`     bigint(11)          NOT NULL DEFAULT '0' COMMENT '用户编号',
    `model`       varchar(50)                  DEFAULT NULL COMMENT '设备型号',
    `version`     varchar(10)                  DEFAULT NULL COMMENT 'app版本',
    `os`          varchar(10)                  DEFAULT NULL COMMENT '系统名称',
    `os_info`     varchar(30)                  DEFAULT NULL COMMENT '系统信息',
    `language`    varchar(30)                  DEFAULT NULL COMMENT '设备语言',
    `timezone`    varchar(64)                  DEFAULT NULL COMMENT '时区',
    `create_time` datetime                     DEFAULT NULL COMMENT '添加时间',
    `update_time` bigint(13)                   DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `imei` (`imei`) USING BTREE,
    KEY `uid` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='设备表';