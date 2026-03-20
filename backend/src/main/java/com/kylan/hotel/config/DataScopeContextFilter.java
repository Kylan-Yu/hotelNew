package com.kylan.hotel.config;

import com.kylan.hotel.common.DataScopeContext;
import com.kylan.hotel.common.DataScopeContextHolder;
import com.kylan.hotel.common.SecurityUtils;
import com.kylan.hotel.mapper.HotelPropertyMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataScopeContextFilter extends OncePerRequestFilter {

    private final HotelPropertyMapper hotelPropertyMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            boolean all = false;
            List<Long> propertyIds = List.of();
            List<Long> brandIds = List.of();
            List<Long> groupIds = List.of();
            try {
                propertyIds = SecurityUtils.propertyScopes();
                if (!propertyIds.isEmpty()) {
                    brandIds = hotelPropertyMapper.findBrandIdsByPropertyIds(propertyIds);
                    groupIds = hotelPropertyMapper.findGroupIdsByPropertyIds(propertyIds);
                }
            } catch (Exception ignore) {
            }

            DataScopeContextHolder.set(DataScopeContext.builder()
                    .allAccess(all)
                    .propertyIds(propertyIds)
                    .brandIds(brandIds)
                    .groupIds(groupIds)
                    .build());
            filterChain.doFilter(request, response);
        } finally {
            DataScopeContextHolder.clear();
        }
    }
}
