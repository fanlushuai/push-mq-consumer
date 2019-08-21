package com.auh.open.mq.consumer.config;

import brave.Tracing;
import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.spring.rabbit.SpringRabbitTracing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import zipkin2.reporter.Reporter;

@Slf4j
@Configuration
@EnableRabbit
public class RabbitConfig implements RabbitListenerConfigurer {

    public final static String ACK_AUTO = "AcknowledgeMode.AUTO";

    public final static String ACK_MANNUL = "AcknowledgeMode.MANUAL";

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

    @Bean
    MessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setMessageConverter(new MappingJackson2MessageConverter());
        return messageHandlerMethodFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, SpringRabbitTracing springRabbitTracing) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //Jackson2JsonMessageConverter的操作如下，  具体可分析源码rabbitTemplate.convertAndSend(ExchangeName.TOPIC, RouteKey.PUSH, pushDTO);
        // 1、在Message的headers上添加__TypeId__=类包名
        // 2、在Message的properties上添加 content_type=application/json
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        springRabbitTracing.decorateRabbitTemplate(rabbitTemplate);
        return rabbitTemplate;
    }

    /**
     * 默认的工厂
     */
    @Primary
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory, SpringRabbitTracing springRabbitTracing) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        springRabbitTracing.decorateSimpleRabbitListenerContainerFactory(factory);
        LogListenerAdvice.decorateSimpleRabbitListenerContainerFactory(factory);
        return factory;
    }

    /**
     * 手动确认工厂
     */
    @Bean(ACK_MANNUL)
    public RabbitListenerContainerFactory<?> manualAckRabbitListenerContainerFactory(ConnectionFactory connectionFactory, SpringRabbitTracing springRabbitTracing) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        springRabbitTracing.decorateSimpleRabbitListenerContainerFactory(factory);
        //打印所有的接收到的消息
        LogListenerAdvice.decorateSimpleRabbitListenerContainerFactory(factory);
        return factory;
    }

    /**
     * 自动确认的工厂
     */
    @Bean(ACK_AUTO)
    public RabbitListenerContainerFactory<?> autoAckRabbitListenerContainerFactory(ConnectionFactory connectionFactory, SpringRabbitTracing springRabbitTracing) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //自动确认模式不重新入队。不管什么异常
        factory.setDefaultRequeueRejected(false);
        springRabbitTracing.decorateSimpleRabbitListenerContainerFactory(factory);
        LogListenerAdvice.decorateSimpleRabbitListenerContainerFactory(factory);
        return factory;
    }

    @Bean
    public Tracing tracing() {
        return Tracing.newBuilder()
                .localServiceName("rabbitmq")
                .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
                        .addScopeDecorator(MDCScopeDecorator.create()).build())
                .spanReporter(Reporter.NOOP)
                .build();
    }

    @Bean
    public SpringRabbitTracing springRabbitTracing(Tracing tracing) {
        return SpringRabbitTracing.newBuilder(tracing)
                .writeB3SingleFormat(true)
                .build();
    }

    //todo 开发环境队列生命自动删除

}

