package com.n26.statistics.repository;

import com.n26.statistics.config.AppConfiguration;
import com.n26.statistics.repository.dbo.StatisticsDatum;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {StatisticsRepository.class, AppConfiguration.class})
public class StatisticsRepositoryTest {

    @Autowired
    private AppConfiguration.Window windowConfiguration;

    @MockBean
    private StatisticsData statisticsData;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Before
    public void before() {
        StatisticsDatum[] stubData = new StatisticsDatum[]{
                getStatisticDatumDbo(BigDecimal.ZERO),
                getStatisticDatumDbo(BigDecimal.ONE),
                getStatisticDatumDbo(BigDecimal.TEN),
        };
        doReturn(stubData).when(statisticsData).getActualData();
        doNothing().when(statisticsData).insert(anyInt(), any(BigDecimal.class));
    }

    private StatisticsDatum getStatisticDatumDbo(BigDecimal value) {
        StatisticsDatum result = new StatisticsDatum();
        result.setSum(value);
        result.setMax(value);
        result.setMin(value);
        result.setCount(1);
        return result;
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_null_amount() {
        statisticsRepository.insert(0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_negative_bucket() {
        statisticsRepository.insert(-1, BigDecimal.TEN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_huge_bucket() {
        statisticsRepository.insert(windowConfiguration.getSizeSeconds(), BigDecimal.TEN);
    }

    @Test
    public void should_insert_statistics_data() {
        statisticsRepository.insert(windowConfiguration.getSizeSeconds() - 1, BigDecimal.TEN);

        verify(statisticsData).insert(anyInt(), any(BigDecimal.class));
    }

    @Test
    public void should_return_expected_statistics() {
        StatisticsDatum statistics = statisticsRepository.getStatistics();
        Assertions.assertThat(statistics.getSum()).isEqualTo(BigDecimal.valueOf(11));
        Assertions.assertThat(statistics.getMax()).isEqualTo(BigDecimal.TEN);
        Assertions.assertThat(statistics.getMin()).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(statistics.getCount()).isEqualTo(3L);
    }
}