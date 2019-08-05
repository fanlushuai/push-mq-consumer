package com.auh.open.mq.common.dto.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

public class JacksonMessage {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {

        }
        return null;
    }

    /**
     * rabbit-web-ui手动push的时候
     * 在headers上添加__TypeId__=完整包名
     * 在properties上添加 content_type=application/json
     */
    public static Message toMessage(Object o) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        messageProperties.getHeaders().put("__TypeId__", o.getClass().getName());
        return new Message(toJson(o).getBytes(), messageProperties);
    }


}
