package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.MqTopics;
import com.kylan.hotel.domain.vo.BizEventVO;
import com.kylan.hotel.service.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherImpl implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishOrderCreated(BizEventVO event) {
        rabbitTemplate.convertAndSend(MqTopics.EXCHANGE_BIZ, MqTopics.ROUTING_ORDER_CREATED, event);
    }

    @Override
    public void publishInventoryWarning(BizEventVO event) {
        rabbitTemplate.convertAndSend(MqTopics.EXCHANGE_BIZ, MqTopics.ROUTING_INVENTORY_WARNING, event);
    }
}
