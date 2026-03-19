package com.kylan.hotel.common;

public final class RedisKeys {
    private static final String REFRESH_PREFIX = "auth:refresh:";
    private static final String ACCESS_BLACKLIST_PREFIX = "auth:blacklist:access:";
    private static final String OTA_CALLBACK_IDEMPOTENT_PREFIX = "ota:callback:idempotent:";

    private RedisKeys() {
    }

    public static String refreshToken(Long userId, String tokenId) {
        return REFRESH_PREFIX + userId + ":" + tokenId;
    }

    public static String accessTokenBlacklist(String tokenId) {
        return ACCESS_BLACKLIST_PREFIX + tokenId;
    }

    public static String otaCallbackIdempotent(String idempotentKey) {
        return OTA_CALLBACK_IDEMPOTENT_PREFIX + idempotentKey;
    }
}
