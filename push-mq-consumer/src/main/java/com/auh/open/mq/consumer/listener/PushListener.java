package com.auh.open.mq.consumer.listener;

import com.auh.open.mq.common.consts.ExchangeName;
import com.auh.open.mq.common.consts.QueueName;
import com.auh.open.mq.common.consts.RouteKey;
import com.auh.open.mq.common.dto.push.PushDTO;
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

/**
 * 根据token来推送。单独一个消费队列
 * 解决：
 * 1、因为token推送消息过多，影响push all 或者tag
 * 2、能单独增加处理消费者数量
 */
@RabbitListener(
        bindings = @QueueBinding(
                exchange = @Exchange(value = ExchangeName.TOPIC, type = ExchangeTypes.TOPIC),
                key = RouteKey.PUSH,
                value = @Queue(value = QueueName.PUSH)
        ),
        concurrency = "10-15",
        containerFactory = RabbitConfig.ACK_MANNUL
)
@Slf4j
@Service
public class PushListener {

    @Autowired
    private PushService pushService;

    @RabbitHandler
    public void push(PushDTO pushDTO, Message message, Channel channel) {
        //进行消息序列化。转发到串行队列持久化。通过redis统计点击次数，通过延迟队列进行次数统计入库
        log.info("{}", pushDTO);
        pushService.push(pushDTO);
        AckUtil.ack(message, channel);
    }

}
