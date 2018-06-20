package com.n26.statistics.repository;

import com.n26.statistics.config.AppConfiguration;
import com.n26.statistics.repository.dbo.StatisticsDatum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

import static com.n26.statistics.config.Constants.NUMBER_OF_MILLIS_IN_SECOND;

@Component
public class StatisticsData {

    private volatile StatisticsDatum[] data;
    private volatile Long lastShiftEpochTime = System.currentTimeMillis();

    StatisticsData(AppConfiguration.Window windowConfiguration) {
        this.data = new StatisticsDatum[windowConfiguration.getSizeSeconds()];
    }

    synchronized void insert(int bucketIndex, BigDecimal amount) {
        data = shiftAndGetCopy();
        lastShiftEpochTime = System.currentTimeMillis();
        if (Objects.isNull(data[bucketIndex])) {
            data[bucketIndex] = new StatisticsDatum();
        }
        data[bucketIndex].update(amount);
    }

    StatisticsDatum[] getActualData() {
        return shiftAndGetCopy();
    }

    private StatisticsDatum[] shiftAndGetCopy() {
        final StatisticsDatum[] result = new StatisticsDatum[data.length];
        int indexesNumberToShift = getIndexesNumberToShift();
        System.arraycopy(data, 0, result, indexesNumberToShift, data.length - indexesNumberToShift);
        return result;
    }

    private int getIndexesNumberToShift() {
        long timeDelta = System.currentTimeMillis() - lastShiftEpochTime;
        int result = Math.toIntExact(timeDelta / NUMBER_OF_MILLIS_IN_SECOND);
        return result > data.length ? data.length : result;
    }
}
