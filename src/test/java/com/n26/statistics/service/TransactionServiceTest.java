package com.n26.statistics.service;

import com.n26.statistics.api.transaction.dto.TransactionDto;
import com.n26.statistics.config.AppConfiguration;
import com.n26.statistics.exception.FutureTransactionException;
import com.n26.statistics.exception.ObsoleteTransactionException;
import com.n26.statistics.repository.StatisticsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static com.n26.statistics.config.Constants.NUMBER_OF_MILLIS_IN_SECOND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {TransactionService.class, AppConfiguration.class})
@RunWith(SpringRunner.class)
public class TransactionServiceTest {

    @Autowired
    private AppConfiguration.Window windowConfiguration;
    @Autowired
    private TransactionService transactionService;
    @MockBean
    private StatisticsRepository statisticsRepository;
    @Captor
    private ArgumentCaptor<Integer> captor;

    @Before
    public void before() {
        doNothing().when(statisticsRepository).insert(anyInt(), any(BigDecimal.class));
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_null_transaction() {
        transactionService.save(null);
    }

    @Test(expected = ObsoleteTransactionException.class)
    public void should_fail_obsolete_transaction() {
        final TransactionDto transactionDto = new TransactionDto();

        transactionDto.setAmount(BigDecimal.TEN);
        transactionDto.setTimestamp(System.currentTimeMillis() - windowConfiguration.getSizeSeconds() * NUMBER_OF_MILLIS_IN_SECOND);

        transactionService.save(transactionDto);
    }

    @Test(expected = FutureTransactionException.class)
    public void should_fail_future_transaction() {
        final TransactionDto transactionDto = new TransactionDto();

        transactionDto.setAmount(BigDecimal.TEN);
        transactionDto.setTimestamp(System.currentTimeMillis() + 1L);

        transactionService.save(transactionDto);
    }

    @Test
    public void should_call_repository_max_bucket() {
        final int expectedBucketIndex = windowConfiguration.getSizeSeconds() - 1;
        final TransactionDto transactionDto = new TransactionDto();

        transactionDto.setAmount(BigDecimal.TEN);
        transactionDto.setTimestamp(System.currentTimeMillis() - windowConfiguration.getSizeSeconds() * NUMBER_OF_MILLIS_IN_SECOND + 1);

        transactionService.save(transactionDto);

        verify(statisticsRepository, atLeastOnce()).insert(captor.capture(), any(BigDecimal.class));
        assertThat(captor.getValue()).isEqualTo(expectedBucketIndex);
    }

    @Test
    public void should_call_repository_min_bucket() {
        final int expectedBucketIndex = 0;
        final TransactionDto transactionDto = new TransactionDto();

        transactionDto.setAmount(BigDecimal.TEN);
        transactionDto.setTimestamp(System.currentTimeMillis());

        transactionService.save(transactionDto);

        verify(statisticsRepository, atLeastOnce()).insert(captor.capture(), any(BigDecimal.class));
        assertThat(captor.getValue()).isEqualTo(expectedBucketIndex);
    }

    @Test
    public void should_call_repository_second_bucket() {
        final int expectedBucketIndex = 1;
        final TransactionDto transactionDto = new TransactionDto();

        transactionDto.setAmount(BigDecimal.TEN);
        transactionDto.setTimestamp(System.currentTimeMillis() - NUMBER_OF_MILLIS_IN_SECOND);

        transactionService.save(transactionDto);

        verify(statisticsRepository, atLeastOnce()).insert(captor.capture(), any(BigDecimal.class));
        assertThat(captor.getValue()).isEqualTo(expectedBucketIndex);
    }
}