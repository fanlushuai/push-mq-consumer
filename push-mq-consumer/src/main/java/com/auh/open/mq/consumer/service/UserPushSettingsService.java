package com.auh.open.mq.consumer.service;

import com.auh.open.mq.common.consts.PlatForm;
import com.auh.open.mq.common.dto.push.PushByTokenDTO;
import com.auh.open.mq.consumer.entity.Device;
import com.auh.open.mq.consumer.mapper.DeviceMapper;
import com.auh.open.mq.consumer.mapper.UserSettingsMapper;
import com.auh.open.mq.consumer.param.DeviceParam;
import com.auh.open.mq.consumer.param.PushSettingsParam;
import com.auh.open.mq.consumer.util.ObjectUtil;
import com.auh.open.mq.consumer.vo.PushSettingsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 客户端注册设备
 * 服务端推送设备
 */
@Slf4j
@Service
public class UserPushSettingsService {

    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private UserSettingsMapper userSettingsMapper;

    @Autowired
    private MqService mqPushService;

    /**
     * 添加或者更新设备
     */
    public void addOrUpdateDevice(DeviceParam deviceParam) {
        Assert.hasLength(deviceParam.getTimezone(), "getTimezone required");
        Assert.hasLength(deviceParam.getLanguage(), "getLanguage required");
        Assert.hasLength(deviceParam.getOs(), "os required");
        Assert.hasLength(deviceParam.getOsInfo(), "getOsInfo required");
        Assert.hasLength(deviceParam.getModel(), "getModel required");
        Assert.hasLength(deviceParam.getImei(), "imei required");
        Assert.hasLength(deviceParam.getPlatform(), "getPlatform required");
        Assert.hasLength(deviceParam.getPushToken(), "getPushToken required");
        Assert.hasLength(deviceParam.getVersion(), "getVersion required");

        Device device = deviceMapper.getByImei(deviceParam.getImei());
        if (device == null) {
            Device newDevice = new Device();
            BeanUtils.copyProperties(deviceParam, newDevice);
            newDevice.setCreateTime(LocalDateTime.now());
            newDevice.setUpdateTime(System.currentTimeMillis());
            //imei 唯一键控制并发
            deviceMapper.insert(newDevice);
        } else if (ObjectUtil.isDiff(deviceParam, device)) {
            BeanUtils.copyProperties(deviceParam, device);
            device.setUpdateTime(System.currentTimeMillis());
            //忽略并发处理
            deviceMapper.updateById(device);
        }
    }

    /**
     * 设置推送设置
     */
    public void setPushSettings(PushSettingsParam pushSettingsParam) {
        PushSettingsVo pushSettingsVo = new PushSettingsVo();
        BeanUtils.copyProperties(pushSettingsParam, pushSettingsVo);
        Long pushSettings = PUSH_SETTINGS_TRANSFER.transfer(pushSettingsVo);

        if (StringUtils.hasLength(pushSettingsParam.getJid())) {
            //更新用户的设置
            userSettingsMapper.updateUserPushSettings(pushSettingsParam.getJid(), pushSettings);
            //更新用户的设备的设置
            List<Device> devicePushSettings = deviceMapper.listByJId(pushSettingsParam.getJid());
            for (Device devicePushSetting : devicePushSettings) {
                if (!pushSettings.equals(devicePushSetting.getDevicePushSettings())) {
                    devicePushSetting.setUpdateTime(System.currentTimeMillis());
                    devicePushSetting.setDevicePushSettings(pushSettings);
                    deviceMapper.updateById(devicePushSetting);
                }
            }
            return;
        } else if (StringUtils.hasLength(pushSettingsParam.getDeviceId())) {
            deviceMapper.updateDevicePushSettings(pushSettingsParam.getDeviceId(), pushSettings);
            return;
        }

        log.info("jid or deviceId required");
        return;
    }

    /**
     * 获取推送设置
     */
    public PushSettingsVo getPushSettings(String jid, String deviceId) {
        if (StringUtils.isEmpty(jid) && StringUtils.isEmpty(deviceId)) {
            log.info("jid or deviceId required");
            return new PushSettingsVo();
        }

        Long pushSettings = null;
        if (StringUtils.hasLength(jid)) {
            pushSettings = userSettingsMapper.getUserPushSettings(jid);
        } else if (StringUtils.hasLength(deviceId)) {
            Device device = deviceMapper.getByImei(deviceId);
            if (device != null) {
                pushSettings = device.getDevicePushSettings();
            }
        }
        return PUSH_SETTINGS_TRANSFER.transfer(pushSettings);
    }

    public List<Device> listTargetPushDevice(String jid) {
        return deviceMapper.listTargetPushDevice(jid);
    }

    /**
     * 推送给用户
     */
    public void push2User(String jid, String title, String content, Map<String, String> custom) {
        if (StringUtils.isEmpty(jid)) {
            log.error("jid is empty");
            return;
        }
        //推送用户最近更新的N个设备 (因为用户注册之后无法失效，存在总是发送一些用户不会使用的设备的情况。)
        List<Device> userDevice = deviceMapper.listTargetPushDevice(jid);
        if (CollectionUtils.isEmpty(userDevice)) {
            log.info("此用户无在线设备{} {} {}", jid, title, content);
            return;
        }
        push2Device(userDevice, title, content, custom);
    }

    /**
     * 推送给设备
     */
    public void push2Device(List<Device> userDevice, String title, String content, Map<String, String> custom) {
        if (CollectionUtils.isEmpty(userDevice)) {
            log.info("device list is empty");
            return;
        }
        Set<String> androidPushTokens = userDevice.stream().filter(device -> PlatForm.ANDROID.equals(device.getPlatform()))
                .map(Device::getPushToken).collect(Collectors.toSet());

        Set<String> iosPushTokens = userDevice.stream().filter(device -> PlatForm.IOS.equals(device.getPlatform()))
                .map(Device::getPushToken).collect(Collectors.toSet());

        push2Device(PlatForm.IOS, iosPushTokens, title, content, custom);
        push2Device(PlatForm.ANDROID, androidPushTokens, title, content, custom);
    }

    /**
     * 推送给设备
     */
    public void push2Device(String platForm, Set<String> pushTokens, String title, String content, Map<String, String> custom) {
        if (CollectionUtils.isEmpty(pushTokens)) {
            log.info("pushTokens is empty");
            return;
        }

        if (!PlatForm.ANDROID.equals(platForm) && !PlatForm.IOS.equals(platForm)) {
            log.error("not support platform : {}", platForm);
            return;
        }

        try {
            mqPushService.send(PushByTokenDTO.builder().platform(platForm)
                    .content(content).title(title).pushTokens(pushTokens).custom(custom).build());
        } catch (Exception e) {
            log.error("push2Device 异常 {} {} {},{}", platForm, pushTokens, title, content);
        }

    }

    private final static PushSettingsTransfer PUSH_SETTINGS_TRANSFER = new PushSettingsTransfer();

    /**
     * 推送设置与具体的推送转化类
     */
    public static class PushSettingsTransfer {

        final static Long NEWS_PUSH_BIT = 1L;//0000 0000 0000 0001

        final static Long COMMENT_PUSH_BIT = 2L;//0000 0000 0000 0010

        final static Long CHAT_PUSH_BIT = 4L;//0000 0000 0000 0100

        final static Long NIGHT_PUSH_BIT = 8L;//0000 0000 0000 1000

        final static Long NOTIFY_VOICE_PUSH_BIT = 16L;//0000 0000 0001 0000

        final static Long ALL_CLOSE_BIT = 0L;

        public PushSettingsVo transfer(Long pushSettings) {
            if (pushSettings == null) {
                //返回默认值
                PushSettingsVo pushSettingsVo = new PushSettingsVo();
                pushSettingsVo.setChatPush(1);
                pushSettingsVo.setCommentPush(1);
                pushSettingsVo.setNewsPush(1);
                pushSettingsVo.setNightPush(1);
                pushSettingsVo.setPushNotifyVoice(1);
                return pushSettingsVo;
            }

            PushSettingsVo pushSettingsVo = new PushSettingsVo();
            pushSettingsVo.setChatPush((int) (pushSettings & CHAT_PUSH_BIT) == CHAT_PUSH_BIT ? 1 : 0);
            pushSettingsVo.setCommentPush((int) (pushSettings & COMMENT_PUSH_BIT) == COMMENT_PUSH_BIT ? 1 : 0);
            pushSettingsVo.setNewsPush((int) (pushSettings & NEWS_PUSH_BIT) == NEWS_PUSH_BIT ? 1 : 0);
            pushSettingsVo.setNightPush((int) (pushSettings & NIGHT_PUSH_BIT) == NIGHT_PUSH_BIT ? 1 : 0);
            pushSettingsVo.setPushNotifyVoice((int) (pushSettings & NOTIFY_VOICE_PUSH_BIT) == NOTIFY_VOICE_PUSH_BIT ? 1 : 0);
            return pushSettingsVo;
        }

        public Long transfer(PushSettingsVo pushSettingsVo) {
            Long pushSetings = ALL_CLOSE_BIT;
            if (pushSettingsVo.getChatPush() == null || pushSettingsVo.getChatPush() == 1L) {
                pushSetings = pushSetings | CHAT_PUSH_BIT;
            }
            if (pushSettingsVo.getCommentPush() == null || pushSettingsVo.getCommentPush() == 1L) {
                pushSetings = pushSetings | COMMENT_PUSH_BIT;
            }
            if (pushSettingsVo.getNewsPush() == null || pushSettingsVo.getNewsPush() == 1L) {
                pushSetings = pushSetings | NEWS_PUSH_BIT;
            }
            if (pushSettingsVo.getPushNotifyVoice() == null || pushSettingsVo.getPushNotifyVoice() == 1L) {
                pushSetings = pushSetings | NOTIFY_VOICE_PUSH_BIT;
            }
            if (pushSettingsVo.getNightPush() == null || pushSettingsVo.getNightPush() == 1L) {
                pushSetings = pushSetings | NIGHT_PUSH_BIT;
            }
            return pushSetings;
        }

    }

}
