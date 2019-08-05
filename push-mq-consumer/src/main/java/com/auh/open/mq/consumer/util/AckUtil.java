package com.auh.open.mq.consumer.util;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import java.io.IOException;

/**
 * 注意：reject和nack功能上都一样。但是nack可以批量确认，reject只能单条。所以我们暂时没有应用场景
 * //todo ack异常重试机制
 */
@Slf4j
public class AckUtil {

    /**
     * 通知队列，删除消息，消费成功
     */
    public static void ack(Message message, Channel channel) {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.error("ack error {}", message.getMessageProperties().getMessageId());
        }
    }

    /**
     * 通知队列，删除消息，不想消费
     * 如、消息格式问题
     */
    public static void rejectNotRequeue(Message message, Channel channel) {
        try {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.error("ack error {}", message.getMessageProperties().getMessageId());
        }
    }

    /**
     * 通知队列，再次入队，没有消费成功，需要再次消费
     * 如、多级服务出现网络异常。需要重试的场景(重试场景需要考虑死循环)
     */
    public static void rejectRequeue(Message message, Channel channel) {
        try {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.error("ack error {}", message.getMessageProperties().getMessageId());
        }
    }

}
