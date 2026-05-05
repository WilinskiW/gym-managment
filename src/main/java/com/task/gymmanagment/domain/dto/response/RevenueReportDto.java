package com.task.gymmanagment.domain.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RevenueReportDto(String gymName,
                               BigDecimal amount,
                               String currency) {
}
