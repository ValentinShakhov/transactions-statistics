package com.n26.statistics.api.transaction;

import com.n26.statistics.api.transaction.dto.TransactionDto;
import com.n26.statistics.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transactions")
    public ResponseEntity postTransaction(@Valid @RequestBody TransactionDto transactionDto) {
        transactionService.save(transactionDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
