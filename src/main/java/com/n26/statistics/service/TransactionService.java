package com.n26.statistics.service;

import com.n26.statistics.api.transaction.dto.TransactionDto;
import com.n26.statistics.config.AppConfiguration;
import com.n26.statistics.exception.FutureTransactionException;
import com.n26.statistics.exception.ObsoleteTransactionException;
import com.n26.statistics.repository.StatisticsRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.n26.statistics.config.Constants.NUMBER_OF_MILLIS_IN_SECOND;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AppConfiguration.Window windowConfiguration;
    private final StatisticsRepository statisticsRepository;

    public void save(@NonNull TransactionDto transactionDto) {
        if (isOld(transactionDto.getTimestamp())) {
            throw new ObsoleteTransactionException();
        }
        if (isFuture(transactionDto.getTimestamp())) {
            throw new FutureTransactionException();
        }

        statisticsRepository.insert(getBucketIndex(transactionDto.getTimestamp()), transactionDto.getAmount());
    }

    private boolean isFuture(long transactionTimestamp) {
        return transactionTimestamp > System.currentTimeMillis();
    }

    private boolean isOld(long transactionTimestamp) {
        return transactionTimestamp <= System.currentTimeMillis() - windowConfiguration.getSizeSeconds() * NUMBER_OF_MILLIS_IN_SECOND;
    }

    private int getBucketIndex(long transactionTimestamp) {
        long timeDelta = System.currentTimeMillis() - transactionTimestamp;

        return Math.toIntExact(timeDelta / NUMBER_OF_MILLIS_IN_SECOND);
    }
}
