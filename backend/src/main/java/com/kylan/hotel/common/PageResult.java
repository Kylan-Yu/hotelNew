package com.kylan.hotel.common;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResult<T> {
    private Long total;
    private Integer pageNo;
    private Integer pageSize;
    private List<T> records;

    public static <T> PageResult<T> of(Long total, Integer pageNo, Integer pageSize, List<T> records) {
        return PageResult.<T>builder()
                .total(total)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .records(records)
                .build();
    }
}
