package com.kylan.hotel.config;

import com.kylan.hotel.common.DataScopeContext;
import com.kylan.hotel.common.DataScopeContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlDataScopeInterceptorTest {

    private final SqlDataScopeInterceptor interceptor = new SqlDataScopeInterceptor();

    @AfterEach
    void tearDown() {
        DataScopeContextHolder.clear();
    }

    @Test
    void shouldInjectGroupBrandPropertyScopeExpressions() {
        DataScopeContextHolder.set(DataScopeContext.builder()
                .allAccess(false)
                .groupIds(List.of(1L))
                .brandIds(List.of(11L, 12L))
                .propertyIds(List.of(101L))
                .build());

        String scoped = (String) ReflectionTestUtils.invokeMethod(
                interceptor,
                "applyScope",
                "SELECT * FROM hotel_order WHERE /*DS_GROUP:group_id*/ 1=1 " +
                        "AND /*DS_BRAND:brand_id*/ 1=1 AND /*DS_PROPERTY:property_id*/ 1=1"
        );

        assertTrue(scoped.contains("group_id IN (1)"));
        assertTrue(scoped.contains("brand_id IN (11,12)"));
        assertTrue(scoped.contains("property_id IN (101)"));
        assertFalse(scoped.contains("/*DS_GROUP:group_id*/"));
    }

    @Test
    void shouldFallbackToDenyWhenScopeIdsAreEmpty() {
        DataScopeContextHolder.set(DataScopeContext.builder()
                .allAccess(false)
                .groupIds(List.of())
                .brandIds(List.of())
                .propertyIds(List.of())
                .build());

        String scoped = (String) ReflectionTestUtils.invokeMethod(
                interceptor,
                "applyScope",
                "SELECT * FROM audit_log_record WHERE /*DS_PROPERTY:property_id*/ 1=1"
        );

        assertTrue(scoped.contains("1=0"));
    }
}
