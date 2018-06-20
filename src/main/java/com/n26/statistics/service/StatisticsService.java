package com.n26.statistics.service;

import com.n26.statistics.api.statistics.dto.StatisticsDto;
import com.n26.statistics.repository.StatisticsRepository;
import com.n26.statistics.repository.dbo.StatisticsDatum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    public StatisticsDto getStatistics() {
        final StatisticsDatum statistics = statisticsRepository.getStatistics();
        return StatisticsDto.builder()
                .sum(statistics.getSum())
                .avg(getAvg(statistics.getSum(), statistics.getCount()))
                .max(statistics.getMax())
                .min(statistics.getMin())
                .count(statistics.getCount())
                .build();
    }

    private BigDecimal getAvg(BigDecimal sum, long count) {
        return count != 0 ? sum.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }
}
