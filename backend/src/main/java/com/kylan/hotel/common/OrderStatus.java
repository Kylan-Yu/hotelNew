package com.kylan.hotel.common;

public final class OrderStatus {
    public static final String PENDING_CONFIRM = "PENDING_CONFIRM";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String CHECKED_IN = "CHECKED_IN";
    public static final String CHECKED_OUT = "CHECKED_OUT";
    public static final String CANCELED = "CANCELED";

    private OrderStatus() {
    }
}
