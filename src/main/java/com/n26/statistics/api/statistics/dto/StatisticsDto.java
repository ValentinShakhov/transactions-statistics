package com.n26.statistics.api.statistics.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class StatisticsDto {
    BigDecimal sum;
    BigDecimal avg;
    BigDecimal max;
    BigDecimal min;
    Long count;
}
