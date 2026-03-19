package com.kylan.hotel.common;

public final class RoomTimelineCodes {
    public static final String NODE_RESERVED = "RESERVED";
    public static final String NODE_PENDING_CHECKIN = "PENDING_CHECKIN";
    public static final String NODE_CHECKED_IN = "CHECKED_IN";
    public static final String NODE_EXTENDED = "EXTENDED";
    public static final String NODE_CHANGE_ROOM = "CHANGE_ROOM";
    public static final String NODE_PENDING_CHECKOUT = "PENDING_CHECKOUT";
    public static final String NODE_CHECKED_OUT = "CHECKED_OUT";
    public static final String NODE_CANCELED = "CANCELED";

    public static final String ACTION_CREATE = "CREATE";
    public static final String ACTION_WAITING = "WAITING";
    public static final String ACTION_CHECK_IN = "CHECK_IN";
    public static final String ACTION_EXTEND_STAY = "EXTEND_STAY";
    public static final String ACTION_CHANGE_ROOM = "CHANGE_ROOM";
    public static final String ACTION_CHECKOUT = "CHECKOUT";
    public static final String ACTION_CANCEL = "CANCEL";

    public static final String REMARK_ORDER_CREATED = "ORDER_CREATED";
    public static final String REMARK_WAITING_CHECKIN = "WAITING_CHECKIN";
    public static final String REMARK_CHECKIN_COMPLETED = "CHECKIN_COMPLETED";
    public static final String REMARK_STAY_EXTENDED = "STAY_EXTENDED";
    public static final String REMARK_ROOM_CHANGED = "ROOM_CHANGED";
    public static final String REMARK_PENDING_CHECKOUT = "PENDING_CHECKOUT";
    public static final String REMARK_CHECKOUT_COMPLETED = "CHECKOUT_COMPLETED";
    public static final String REMARK_ORDER_CANCELED = "ORDER_CANCELED";

    private RoomTimelineCodes() {
    }
}
