package com.auh.open.mq.consumer.config;

import brave.spring.rabbit.SpringRabbitTracing;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;

/**
 * 拦截打印收到msg的日志。
 * 使用时注意顺序，应该放在TracingRabbitListenerAdvice之后，这样才会有trancId
 * 参考{@link brave.spring.rabbit.TracingRabbitListenerAdvice}
 */
@Slf4j
public class LogListenerAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            Message message = (Message) methodInvocation.getArguments()[1];
            if (message != null && message.getMessageProperties() != null) {
                if (MessageProperties.CONTENT_TYPE_JSON.equals(message.getMessageProperties().getContentType())) {

                    log.info("queue : {} receive :{}",
                            message.getMessageProperties().getConsumerQueue(),
                            new String(message.getBody())
                    );
                }
            }
        } catch (Exception e) {
            log.error("打印队列收到消息拦截失败");
        }

        return methodInvocation.proceed();
    }

    /**
     * 参考{@link SpringRabbitTracing#decorateSimpleRabbitListenerContainerFactory(SimpleRabbitListenerContainerFactory)}
     */
    public static SimpleRabbitListenerContainerFactory decorateSimpleRabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactory factory
    ) {
        Advice[] chain = factory.getAdviceChain();

        LogListenerAdvice tracingAdvice = new LogListenerAdvice();
        // If there are no existing advice, return only the tracing one
        if (chain == null) {
            factory.setAdviceChain(tracingAdvice);
            return factory;
        }

        // If there is an existing tracing advice return
        for (Advice advice : chain) {
            if (advice instanceof LogListenerAdvice) {
                return factory;
            }
        }

        // Otherwise, add ours and return
        Advice[] newChain = new Advice[chain.length + 1];
        System.arraycopy(chain, 0, newChain, 0, chain.length);
        newChain[chain.length] = tracingAdvice;
        factory.setAdviceChain(newChain);
        return factory;
    }
}
