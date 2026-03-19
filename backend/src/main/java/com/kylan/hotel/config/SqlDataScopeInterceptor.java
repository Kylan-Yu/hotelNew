package com.kylan.hotel.config;

import com.kylan.hotel.common.DataScopeContext;
import com.kylan.hotel.common.DataScopeContextHolder;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class SqlDataScopeInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        String rawSql = statementHandler.getBoundSql().getSql();
        String sql = applyScope(rawSql);
        if (!sql.equals(rawSql)) {
            var field = statementHandler.getBoundSql().getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(statementHandler.getBoundSql(), sql);
        }
        return invocation.proceed();
    }

    private String applyScope(String sql) {
        if (sql == null || sql.isBlank()) {
            return sql;
        }
        DataScopeContext ctx = DataScopeContextHolder.get();
        if (ctx == null) {
            return replaceAll(sql, "1=1", "1=1", "1=1");
        }
        String groupExpr = buildExpr(ctx.isAllAccess(), ctx.getGroupIds(), "group");
        String brandExpr = buildExpr(ctx.isAllAccess(), ctx.getBrandIds(), "brand");
        String propertyExpr = buildExpr(ctx.isAllAccess(), ctx.getPropertyIds(), "property");
        return replaceAll(sql, groupExpr, brandExpr, propertyExpr);
    }

    private String replaceAll(String sql, String groupExpr, String brandExpr, String propertyExpr) {
        return sql
                .replace("/*DS_GROUP:group_id*/ 1=1", groupExpr)
                .replace("/*DS_BRAND:brand_id*/ 1=1", brandExpr)
                .replace("/*DS_PROPERTY:property_id*/ 1=1", propertyExpr);
    }

    private String buildExpr(boolean allAccess, List<Long> ids, String dim) {
        if (allAccess) {
            return "1=1";
        }
        if (ids == null || ids.isEmpty()) {
            return "1=0";
        }
        String joined = ids.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("0");
        return switch (dim) {
            case "group" -> "group_id IN (" + joined + ")";
            case "brand" -> "brand_id IN (" + joined + ")";
            default -> "property_id IN (" + joined + ")";
        };
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
