package com.n26.statistics.repository.dbo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Setter
@Getter
public class StatisticsDatum {
    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal max = BigDecimal.ZERO;
    private BigDecimal min;
    private long count;

    public void update(BigDecimal amount) {
        sum = amount.add(sum);
        max = amount.max(max);
        min = amount.min(min != null ? min : amount);
        count++;
    }

    @Override
    public String toString() {
        return String.format("{ Sum: %s, Avg: %s, Max: %s, Min: %s, Count: %s }",
                sum.toPlainString(),
                count != 0 ? sum.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP).toPlainString() : 0,
                max.toPlainString(),
                min != null ? min.toPlainString() : 0,
                count);
    }
}
