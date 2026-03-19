package com.kylan.hotel.common;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DataScopeContext {
    private boolean allAccess;
    private List<Long> propertyIds;
    private List<Long> brandIds;
    private List<Long> groupIds;
}
