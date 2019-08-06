package com.auh.open.mq.consumer.service;

import com.auh.open.mq.common.consts.ExchangeName;
import com.auh.open.mq.common.consts.RouteKey;
import com.auh.open.mq.common.dto.push.PushDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(PushDTO pushDTO) {
        rabbitTemplate.convertAndSend(ExchangeName.TOPIC, RouteKey.PUSH, pushDTO);
    }

}
