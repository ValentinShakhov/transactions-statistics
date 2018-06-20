package com.n26.statistics.repository;

import com.n26.statistics.config.AppConfiguration;
import com.n26.statistics.repository.dbo.StatisticsDatum;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class StatisticsRepository {

    private final AppConfiguration.Window windowConfiguration;
    private final StatisticsData statisticsData;

    public void insert(int bucketIndex, @NonNull BigDecimal amount) {
        if (bucketIndex < 0 || bucketIndex >= windowConfiguration.getSizeSeconds()) {
            throw new IllegalArgumentException("Invalid bucket index");
        }
        statisticsData.insert(bucketIndex, amount);
    }

    public StatisticsDatum getStatistics() {
        final StatisticsDatum[] actualData = statisticsData.getActualData();
        return combine(actualData);
    }

    private StatisticsDatum combine(StatisticsDatum[] actualData) {
        return Stream.of(actualData).filter(Objects::nonNull).reduce(new StatisticsDatum(), (result, source) -> {
            final BigDecimal resultMin = result.getMin() != null ? result.getMin() : source.getMin();

            result.setSum(source.getSum().add(result.getSum()));
            result.setMax(source.getMax().max(result.getMax()));
            result.setMin(source.getMin().min(resultMin));
            result.setCount(source.getCount() + result.getCount());

            return result;
        });
    }
}
