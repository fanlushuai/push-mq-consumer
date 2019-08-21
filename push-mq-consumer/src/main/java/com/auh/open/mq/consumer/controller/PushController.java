package com.auh.open.mq.consumer.controller;

import com.auh.open.mq.consumer.param.DeviceParam;
import com.auh.open.mq.consumer.param.PushSettingsParam;
import com.auh.open.mq.consumer.service.UserPushSettingsService;
import com.auh.open.mq.consumer.vo.PushSettingsVo;
import com.auh.open.mq.consumer.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.http.Body;

@RestController("/api/push")
@Slf4j
public class PushController {

    @Autowired
    private UserPushSettingsService userPushSettingsService;

    @PostMapping("/devices")
    public Result addOrUpdateDevice(@Body DeviceParam deviceParam) {
        userPushSettingsService.addOrUpdateDevice(deviceParam);
        return Result.ok();
    }

    @GetMapping("/settings")
    public Result<PushSettingsVo> getPushSettings(String jid, String deviceId) {
        return Result.ok(userPushSettingsService.getPushSettings(jid, deviceId));
    }

    @PostMapping("/settings")
    public Result setPushSettings(@Body PushSettingsParam pushSettingsParam) {
        userPushSettingsService.setPushSettings(pushSettingsParam);
        return Result.ok();
    }

}
