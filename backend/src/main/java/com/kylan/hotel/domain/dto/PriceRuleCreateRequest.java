package com.kylan.hotel.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceRuleCreateRequest {
    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotBlank(message = "ruleName is required")
    private String ruleName;

    @NotBlank(message = "ruleType is required")
    private String ruleType;

    @NotBlank(message = "ruleValue is required")
    private String ruleValue;

    private BigDecimal deltaAmount;
    private Integer enabled;
}
