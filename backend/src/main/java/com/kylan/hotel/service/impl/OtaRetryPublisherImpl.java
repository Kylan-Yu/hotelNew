package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.MqTopics;
import com.kylan.hotel.domain.vo.OtaCallbackRetryMessage;
import com.kylan.hotel.service.OtaRetryPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtaRetryPublisherImpl implements OtaRetryPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishRetry(OtaCallbackRetryMessage message, long delaySeconds) {
        rabbitTemplate.convertAndSend(MqTopics.EXCHANGE_BIZ, MqTopics.ROUTING_OTA_CALLBACK_RETRY_DELAY, message, msg -> {
            Message messageBody = msg;
            messageBody.getMessageProperties().setExpiration(String.valueOf(Math.max(delaySeconds, 1) * 1000));
            return messageBody;
        });
    }

    @Override
    public void publishDeadLetter(OtaCallbackRetryMessage message) {
        rabbitTemplate.convertAndSend(MqTopics.EXCHANGE_BIZ, MqTopics.ROUTING_OTA_CALLBACK_RETRY_DLX, message);
    }
}
