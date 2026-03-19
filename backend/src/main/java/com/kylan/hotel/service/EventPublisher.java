package com.kylan.hotel.service;

import com.kylan.hotel.domain.vo.BizEventVO;

public interface EventPublisher {
    void publishOrderCreated(BizEventVO event);

    void publishInventoryWarning(BizEventVO event);
}
