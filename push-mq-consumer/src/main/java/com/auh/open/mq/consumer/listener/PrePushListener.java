package com.auh.open.mq.consumer.listener;

import com.auh.open.mq.common.consts.ExchangeName;
import com.auh.open.mq.common.consts.QueueName;
import com.auh.open.mq.common.consts.RouteKey;
import com.auh.open.mq.common.dto.push.PushAllDTO;
import com.auh.open.mq.common.dto.push.PushByTagDTO;
import com.auh.open.mq.common.dto.push.PushByTokenDTO;
import com.auh.open.mq.consumer.config.RabbitConfig;
import com.auh.open.mq.consumer.service.PushService;
import com.auh.open.mq.consumer.util.AckUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 推送预处理队列
 * 应用程序发送，按tokens、tags、all、都会转化为push token来处理。
 * 目的是不希望，tokens队列数据太多。影响tags，和all队列的消费。
 */
@RabbitListener(
        bindings = @QueueBinding(
                exchange = @Exchange(value = ExchangeName.TOPIC, type = ExchangeTypes.TOPIC),
                key = RouteKey.PRE_PUSH,
                value = @Queue(value = QueueName.PRE_PUSH)
        ),
        concurrency = "1-5",
        containerFactory = RabbitConfig.ACK_MANNUL
)
@Slf4j
@Service
public class PrePushListener {

    @Autowired
    private PushService pushService;

    @RabbitHandler
    public void pushAll(PushAllDTO pushAllDTO, Message message, Channel channel) {
        pushService.push(pushAllDTO);
        AckUtil.ack(message, channel);
    }

    @RabbitHandler
    public void pushByTag(PushByTagDTO pushByTagDTO, Message message, Channel channel) {
        pushService.push(pushByTagDTO);
        AckUtil.ack(message, channel);
    }


}
