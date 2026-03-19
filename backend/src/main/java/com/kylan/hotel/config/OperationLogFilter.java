package com.kylan.hotel.config;

import com.kylan.hotel.common.SecurityUtils;
import com.kylan.hotel.domain.entity.OperationLogRecord;
import com.kylan.hotel.domain.entity.HotelProperty;
import com.kylan.hotel.mapper.HotelPropertyMapper;
import com.kylan.hotel.mapper.OperationLogRecordMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class OperationLogFilter extends OncePerRequestFilter {

    private final OperationLogRecordMapper operationLogRecordMapper;
    private final HotelPropertyMapper hotelPropertyMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long begin = System.currentTimeMillis();
        Exception ex = null;
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            if (request.getRequestURI().startsWith("/api/")) {
                OperationLogRecord log = new OperationLogRecord();
                log.setModuleCode(resolveModule(request.getRequestURI()));
                log.setOperation(request.getMethod() + " " + request.getRequestURI());
                log.setRequestUri(request.getRequestURI());
                log.setRequestMethod(request.getMethod());
                String operator;
                try {
                    operator = SecurityUtils.currentUsername();
                } catch (Exception e) {
                    operator = "anonymous";
                }
                log.setOperator(operator);
                log.setSuccessFlag(ex == null ? "Y" : "N");
                log.setMessage(ex == null ? ("costMs=" + (System.currentTimeMillis() - begin)) : ex.getMessage());
                try {
                    Long currentPropertyId = SecurityUtils.currentPropertyId();
                    if (currentPropertyId != null) {
                        HotelProperty property = hotelPropertyMapper.findById(currentPropertyId);
                        if (property != null) {
                            log.setPropertyId(property.getId());
                            log.setBrandId(property.getBrandId());
                            log.setGroupId(property.getGroupId());
                        }
                    }
                } catch (Exception ignore) {
                    // 忽略属性范围获取异常
                }
                operationLogRecordMapper.insert(log);
            }
        }
    }

    private String resolveModule(String uri) {
        String[] segments = uri.split("/");
        return segments.length > 2 ? segments[2].toUpperCase() : "SYSTEM";
    }
}
