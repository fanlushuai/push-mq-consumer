package com.auh.open.mq.consumer.service;

import com.auh.open.mq.common.consts.ExchangeName;
import com.auh.open.mq.common.consts.PlatForm;
import com.auh.open.mq.common.consts.RouteKey;
import com.auh.open.mq.common.dto.push.PushByTokenDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class MqService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(PushByTokenDTO pushByTokenDTO) {
        if (CollectionUtils.isEmpty(pushByTokenDTO.getPushTokens())) {
            log.error("tokenlist is empty");
            return;
        }
        try {
            if (PlatForm.IOS.equals(pushByTokenDTO.getPlatform())) {
                rabbitTemplate.convertAndSend(ExchangeName.TOPIC, RouteKey.PUSH_IOS, pushByTokenDTO);
            } else if (PlatForm.ANDROID.equals(pushByTokenDTO.getPlatform())) {
                rabbitTemplate.convertAndSend(ExchangeName.TOPIC, RouteKey.PUSH_ANDROID, pushByTokenDTO);
            } else {
                log.error("unknown platform type {}", pushByTokenDTO);
            }
        } catch (Exception e) {
            log.error("send mq e {}", e.getCause());
        }

    }
}
