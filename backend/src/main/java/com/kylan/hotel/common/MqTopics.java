package com.kylan.hotel.common;

public final class MqTopics {
    public static final String EXCHANGE_BIZ = "hms.biz.exchange";
    public static final String ROUTING_ORDER_CREATED = "order.created";
    public static final String ROUTING_INVENTORY_WARNING = "inventory.warning";
    public static final String ROUTING_OTA_CALLBACK_RETRY_DELAY = "ota.callback.retry.delay";
    public static final String ROUTING_OTA_CALLBACK_RETRY_PROCESS = "ota.callback.retry.process";
    public static final String ROUTING_OTA_CALLBACK_RETRY_DLX = "ota.callback.retry.dlx";

    private MqTopics() {
    }
}
