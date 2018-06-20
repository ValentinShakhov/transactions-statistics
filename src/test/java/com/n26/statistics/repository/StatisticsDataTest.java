package com.n26.statistics.repository;

import com.n26.statistics.config.AppConfiguration;
import com.n26.statistics.repository.dbo.StatisticsDatum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {AppConfiguration.class, StatisticsData.class})
@RunWith(SpringRunner.class)
public class StatisticsDataTest {

    @Autowired
    private AppConfiguration.Window windowConfiguration;

    @Autowired
    private StatisticsData statisticsData;

    @Test
    public void should_shift_2_buckets() throws InterruptedException {
        statisticsData.insert(5, BigDecimal.TEN);
        Thread.sleep(2_000L);
        statisticsData.insert(5, BigDecimal.ONE);

        final StatisticsDatum[] data = statisticsData.getActualData();
        assertThat(data[7].getSum()).isEqualTo(BigDecimal.TEN);
        assertThat(data[5].getSum()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    public void should_shift_first_2_buckets() throws InterruptedException {
        statisticsData.insert(0, BigDecimal.TEN);
        Thread.sleep(2_000L);
        statisticsData.insert(0, BigDecimal.ONE);

        final StatisticsDatum[] data = statisticsData.getActualData();
        assertThat(data[2].getSum()).isEqualTo(BigDecimal.TEN);
        assertThat(data[0].getSum()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    public void should_shift_and_dismiss_first_insertion() throws InterruptedException {
        statisticsData.insert(windowConfiguration.getSizeSeconds() - 2, BigDecimal.TEN);
        Thread.sleep(2_000L);
        statisticsData.insert(windowConfiguration.getSizeSeconds() - 2, BigDecimal.ONE);

        final StatisticsDatum[] data = statisticsData.getActualData();
        assertThat(data.length).isEqualTo(windowConfiguration.getSizeSeconds());
        assertThat(data[windowConfiguration.getSizeSeconds() - 2].getSum()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    public void should_shift_on_get() throws InterruptedException {
        statisticsData.insert(0, BigDecimal.TEN);
        Thread.sleep(2_000L);

        final StatisticsDatum[] data = statisticsData.getActualData();
        assertThat(data[2].getSum()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    public void should_update_statistics_on_same_bucket() throws InterruptedException {
        statisticsData.insert(0, BigDecimal.TEN);
        Thread.sleep(2_000L);
        statisticsData.insert(2, BigDecimal.ONE);
        statisticsData.insert(2, BigDecimal.ZERO);

        final StatisticsDatum[] data = statisticsData.getActualData();
        assertThat(data[2].getSum()).isEqualTo(BigDecimal.valueOf(11));
        assertThat(data[2].getMax()).isEqualTo(BigDecimal.TEN);
        assertThat(data[2].getMin()).isEqualTo(BigDecimal.ZERO);
        assertThat(data[2].getCount()).isEqualTo(3L);
    }
}