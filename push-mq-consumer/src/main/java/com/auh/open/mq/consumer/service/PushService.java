package com.auh.open.mq.consumer.service;

import com.auh.open.mq.common.consts.PlatForm;
import com.auh.open.mq.common.dto.push.*;
import com.auh.open.mq.consumer.mapper.DeviceMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
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

    /**
     * 基于tag的推送。将消息转化为token推送，转发到'pushByTokens'队列
     */
    public void push(PushByTagDTO pushByTagDTO) {
        //平台参数的处理
        if (isBothPlatform(pushByTagDTO)) {
            pushByTagDTO.setPlatform(PlatForm.IOS);
            push(pushByTagDTO);
            pushByTagDTO.setPlatform(PlatForm.ANDROID);
            push(pushByTagDTO);
            return;
        }

        //不采用信鸽安卓的tag。因为不稳定
        if (PlatForm.ANDROID.equals(pushByTagDTO.getPlatform())) {
            //采用1000个token一个列表，根据信鸽限制
            pushByTokensFromPushByTagDTO(pushByTagDTO, 1000, PlatForm.ANDROID);
            return;
        }

        //ios的本地转发，自建推送队列
        if (PlatForm.IOS.equals(pushByTagDTO.getPlatform())) {
            //500将会在单个消费者消费循环中执行，数量可以调整
            pushByTokensFromPushByTagDTO(pushByTagDTO, 500, PlatForm.IOS);
            return;
        }

        log.error("unknown platform {}", pushByTagDTO);
    }

    private void pushByTokensFromPushByTagDTO(PushByTagDTO pushByTagDTO, int pageSize, String platform) {
        PushByTokenDTO pushByTokenDTO = new PushByTokenDTO();
        BeanUtils.copyProperties(pushByTagDTO, pushByTokenDTO);
        //pushTag会给用户订阅的推，和设备订阅的推

        //不区分有无用户在线。无用户设备的device_push_settings会变成初始状态,或者无用户状态下设置的状态。有用户设备的device_push_settings会被更新为用户的user_push_settings
        IPage page = new Page();
        page.setSize(pageSize);
        page.setCurrent(0);
        Page<String> pushTokensPage;
        do {
            page.setCurrent(page.getCurrent() + 1);
            pushTokensPage = deviceMapper.listPushTokenByPushSettings(page, platform, 15L);
            pushByTokenDTO.setPushTokens(new HashSet<>(pushTokensPage.getRecords()));
            mqService.send(pushByTokenDTO);
            log.info("page current {} suc", page.getCurrent());
        } while (pushTokensPage.hasNext());
        log.info("pushByTag 发送完成 {}", pushByTagDTO);
    }

    private void pushByTokensFromPushByAllDTO(PushAllDTO pushAllDTO, int pageSize, String platform) {
        PushByTokenDTO pushByTokenDTO = new PushByTokenDTO();
        BeanUtils.copyProperties(pushAllDTO, pushByTokenDTO);
        IPage page = new Page();
        page.setSize(pageSize);
        //不能使用new Page(0,pageSize); 内部会忽略小于1的数值
        page.setCurrent(0);
        Page<String> pushTokensPage;
        do {
            page.setCurrent(page.getCurrent() + 1);
            //pushAll会给所有用户推，不管订阅与否
            pushTokensPage = deviceMapper.listPushToken(page, platform);
            pushByTokenDTO.setPushTokens(new HashSet<>(pushTokensPage.getRecords()));
            mqService.send(pushByTokenDTO);
            log.info("page current {} suc", page.getCurrent());
        } while (pushTokensPage.hasNext());
        log.info("{} pushAll 发送完成", pushAllDTO);
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
        //500将会在单个消费者消费循环中执行，数量可以调整
        pushByTokensFromPushByAllDTO(pushAllDTO, 500, PlatForm.IOS);
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

        if (PlatForm.ANDROID.equals(pushByTokenDTO.getPlatform())) {
            xingeService.pushByTokens(pushByTokenDTO);
            return;
        }
        if (PlatForm.IOS.equals(pushByTokenDTO.getPlatform())) {
            iosPushService.push(pushByTokenDTO);
            return;
        }

        log.error("unknow platform type {}", pushByTokenDTO);
    }

}
