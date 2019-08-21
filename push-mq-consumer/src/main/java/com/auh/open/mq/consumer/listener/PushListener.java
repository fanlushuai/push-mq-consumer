package com.auh.open.mq.consumer.listener;

import com.auh.open.mq.common.consts.ExchangeName;
import com.auh.open.mq.common.consts.QueueName;
import com.auh.open.mq.common.consts.RouteKey;
import com.auh.open.mq.common.dto.push.PushByTokenDTO;
import com.auh.open.mq.consumer.config.RabbitConfig;
import com.auh.open.mq.consumer.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 根据token来推送。单独一个消费队列
 * 解决：
 * 1、因为token推送消息过多，影响push all 或者tag
 * 2、能单独增加处理消费者数量
 */
@Slf4j
@Service
public class PushListener {

    @Autowired
    private PushService pushService;

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(value = ExchangeName.TOPIC, type = ExchangeTypes.TOPIC),
                    key = RouteKey.PUSH_IOS,
                    value = @Queue(value = QueueName.PUSH_IOS)
            ),
            concurrency = "1-20",
            containerFactory = RabbitConfig.ACK_AUTO
    )
    public void pushIOS(PushByTokenDTO pushByTokenDTO, Message message) {
        //进行消息序列化。转发到串行队列持久化。通过redis统计点击次数，通过延迟队列进行次数统计入库
        pushService.push(pushByTokenDTO);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(value = ExchangeName.TOPIC, type = ExchangeTypes.TOPIC),
                    key = RouteKey.PUSH_ANDROID,
                    value = @Queue(value = QueueName.PUSH_ANDROID)
            ),
            concurrency = "1-20",
            containerFactory = RabbitConfig.ACK_AUTO
    )
    public void pushAndroid(PushByTokenDTO pushDTO, Message message) {
        //进行消息序列化。转发到串行队列持久化。通过redis统计点击次数，通过延迟队列进行次数统计入库
        pushService.push(pushDTO);
    }

}
