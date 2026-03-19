package com.kylan.hotel.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogQueryRequest {
    private Long groupId;
    private Long brandId;
    private Long propertyId;
    private String moduleCode;
    private String operator;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean preferCurrent = true;
    private Integer pageNo = 1;
    private Integer pageSize = 20;

    public int offset() {
        int p = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int s = pageSize == null || pageSize < 1 ? 20 : pageSize;
        return (p - 1) * s;
    }

    public int limit() {
        return pageSize == null || pageSize < 1 ? 20 : pageSize;
    }
}
