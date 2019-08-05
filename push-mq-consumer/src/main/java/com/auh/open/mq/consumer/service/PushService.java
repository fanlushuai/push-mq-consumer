package com.auh.open.mq.consumer.service;

import com.auh.open.mq.common.consts.PlatForm;
import com.auh.open.mq.common.dto.push.*;
import com.auh.open.mq.consumer.mapper.DeviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PushService {

    @Autowired
    private MqService mqService;

    @Autowired
    private XingeService xingeService;

    @Autowired
    private IOSPushService iosPushService;

    @Autowired
    private DeviceMapper deviceMapper;

    private boolean isBothPlatform(BasePushDTO basePushDTO) {
        return PlatForm.IOS_AND_ANDROID.equals(basePushDTO.getPlatform());
    }

    public void push(PushByTagDTO pushByTagDTO) {
        //平台参数的处理
        if (isBothPlatform(pushByTagDTO)) {
            pushByTagDTO.setPlatform(PlatForm.IOS);
            push(pushByTagDTO);
            pushByTagDTO.setPlatform(PlatForm.ANDROID);
            push(pushByTagDTO);
            return;
        }

        //安卓的tag推送，直接发至信鸽
        if (PlatForm.ANDROID.equals(pushByTagDTO.getPlatform())) {
            xingeService.pushByTags(pushByTagDTO);
            return;
        }

        //ios的本地转发，自建推送队列
        PushDTO pushDTO = PushDTO.builder().build();
        BeanUtils.copyProperties(pushByTagDTO, pushDTO);

        //todo 查询tag对应的所有的token
        log.error("因为业务不需要。所以先不实现");
        //分页查询出所有tag绑定的设备。进行推送。
        //检索出来的所有设备做去重操作
        List<String> pushTokens = new ArrayList<>();

        for (String pushToken : pushTokens) {
            pushDTO.setToken(pushToken);
            mqService.send(pushDTO);
        }

    }

    public void push(PushByTokenDTO pushByTokenDTO) {
        //平台参数的处理
        //注意，生产者，尽可能的指明平台。因为在token场景下。只能有一个平台
        if (isBothPlatform(pushByTokenDTO)) {
            pushByTokenDTO.setPlatform(PlatForm.IOS);
            push(pushByTokenDTO);
            pushByTokenDTO.setPlatform(PlatForm.ANDROID);
            push(pushByTokenDTO);
            return;
        }

        PushDTO pushDTO = PushDTO.builder().build();
        BeanUtils.copyProperties(pushByTokenDTO, pushDTO);

        for (String pushToken : pushByTokenDTO.getPushTokens()) {
            pushDTO.setToken(pushToken);
            //发送到push队列
            mqService.send(pushDTO);
        }
    }

    public void push(PushAllDTO pushAllDTO) {
        //平台参数的处理
        if (isBothPlatform(pushAllDTO)) {
            pushAllDTO.setPlatform(PlatForm.IOS);
            push(pushAllDTO);
            pushAllDTO.setPlatform(PlatForm.ANDROID);
            push(pushAllDTO);
            return;
        }

        //安卓的全量推送，直接发至信鸽
        if (PlatForm.ANDROID.equals(pushAllDTO.getPlatform())) {
            xingeService.pushAll(pushAllDTO);
            return;
        }

        //iOS的自建推送
        PushDTO pushDTO = PushDTO.builder().build();
        BeanUtils.copyProperties(pushAllDTO, pushDTO);

        //todo 分页查询出所有tokens;因为数据是动态的所以分页查询不好搞,当然也可以，但是貌似没必要
        List<String> pushTokens = deviceMapper.listPushToken(PlatForm.IOS);
        forward(pushDTO, pushTokens);
    }

    private void forward(PushDTO pushDTO, List<String> pushTokens) {
        for (String pushToken : pushTokens) {
            pushDTO.setToken(pushToken);
            mqService.send(pushDTO);
        }
    }

    public void push(PushDTO pushDTO) {
        if (PlatForm.IOS_AND_ANDROID.equals(pushDTO.getPlatform())) {
            log.error("pushBytoken未指定platfrom {}", pushDTO);
            return;
        }

        if (PlatForm.ANDROID.equals(pushDTO.getPlatform())) {
            xingeService.pushByToken(pushDTO);
            return;
        }

        iosPushService.push(pushDTO);
    }


}
