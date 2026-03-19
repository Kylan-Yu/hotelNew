package com.kylan.hotel.service.impl;

import com.kylan.hotel.domain.vo.BizEventVO;
import com.kylan.hotel.domain.vo.OtaCallbackRetryMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BizEventConsumer {

    @RabbitListener(queues = "hms.order.event.queue")
    public void onOrderCreated(BizEventVO event) {
        log.info("receive order created event: {}", event);
    }

    @RabbitListener(queues = "hms.inventory.warning.queue")
    public void onInventoryWarning(BizEventVO event) {
        log.warn("receive inventory warning event: {}", event);
    }

    @RabbitListener(queues = "hms.ota.callback.retry.dlx.queue")
    public void onOtaRetryDeadLetter(OtaCallbackRetryMessage message) {
        log.error("receive ota callback retry dead letter: {}", message);
    }
}
