package com.kylan.hotel.common;

public final class DataScopeContextHolder {
    private static final ThreadLocal<DataScopeContext> HOLDER = new ThreadLocal<>();

    private DataScopeContextHolder() {
    }

    public static void set(DataScopeContext context) {
        HOLDER.set(context);
    }

    public static DataScopeContext get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
