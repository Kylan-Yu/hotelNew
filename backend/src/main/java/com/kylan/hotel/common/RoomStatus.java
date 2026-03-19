package com.kylan.hotel.common;

import java.util.Set;

public final class RoomStatus {
    public static final String VACANT_CLEAN = "VACANT_CLEAN";
    public static final String OCCUPIED = "OCCUPIED";
    public static final String VACANT_DIRTY = "VACANT_DIRTY";
    public static final String MAINTENANCE = "MAINTENANCE";
    public static final String LOCKED = "LOCKED";

    private static final Set<String> ALLOWED = Set.of(
            VACANT_CLEAN, OCCUPIED, VACANT_DIRTY, MAINTENANCE, LOCKED
    );

    private RoomStatus() {
    }

    public static boolean isAllowed(String status) {
        return ALLOWED.contains(status);
    }
}
