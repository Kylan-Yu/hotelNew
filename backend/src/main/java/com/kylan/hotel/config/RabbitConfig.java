package com.kylan.hotel.config;

import com.kylan.hotel.common.MqTopics;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange bizExchange() {
        return new TopicExchange(MqTopics.EXCHANGE_BIZ, true, false);
    }

    @Bean
    public Queue orderEventQueue() {
        return new Queue("hms.order.event.queue", true);
    }

    @Bean
    public Queue inventoryWarningQueue() {
        return new Queue("hms.inventory.warning.queue", true);
    }

    @Bean
    public Binding orderEventBinding() {
        return BindingBuilder.bind(orderEventQueue()).to(bizExchange()).with(MqTopics.ROUTING_ORDER_CREATED);
    }

    @Bean
    public Binding inventoryWarningBinding() {
        return BindingBuilder.bind(inventoryWarningQueue()).to(bizExchange()).with(MqTopics.ROUTING_INVENTORY_WARNING);
    }

    @Bean
    public Queue otaCallbackRetryDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", MqTopics.EXCHANGE_BIZ);
        args.put("x-dead-letter-routing-key", MqTopics.ROUTING_OTA_CALLBACK_RETRY_PROCESS);
        return QueueBuilder.durable("hms.ota.callback.retry.delay.queue").withArguments(args).build();
    }

    @Bean
    public Queue otaCallbackRetryProcessQueue() {
        return QueueBuilder.durable("hms.ota.callback.retry.process.queue").build();
    }

    @Bean
    public Queue otaCallbackRetryDlxQueue() {
        return QueueBuilder.durable("hms.ota.callback.retry.dlx.queue").build();
    }

    @Bean
    public Binding otaCallbackRetryDelayBinding() {
        return BindingBuilder.bind(otaCallbackRetryDelayQueue()).to(bizExchange()).with(MqTopics.ROUTING_OTA_CALLBACK_RETRY_DELAY);
    }

    @Bean
    public Binding otaCallbackRetryProcessBinding() {
        return BindingBuilder.bind(otaCallbackRetryProcessQueue()).to(bizExchange()).with(MqTopics.ROUTING_OTA_CALLBACK_RETRY_PROCESS);
    }

    @Bean
    public Binding otaCallbackRetryDlxBinding() {
        return BindingBuilder.bind(otaCallbackRetryDlxQueue()).to(bizExchange()).with(MqTopics.ROUTING_OTA_CALLBACK_RETRY_DLX);
    }
}
