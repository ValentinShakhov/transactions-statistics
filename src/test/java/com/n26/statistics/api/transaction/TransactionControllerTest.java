package com.n26.statistics.api.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.statistics.api.transaction.dto.TransactionDto;
import com.n26.statistics.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Before
    public void before() {
        doNothing().when(transactionService).save(any());
    }

    @Test
    public void should_fail_null_dto_amount() throws Exception {
        final TransactionDto transactionDto = new TransactionDto();

        transactionDto.setAmount(null);
        transactionDto.setTimestamp(System.currentTimeMillis());

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().is4xxClientError());
        verify(transactionService, atMost(0)).save(any(TransactionDto.class));
    }

    @Test
    public void should_fail_negative_dto_amount() throws Exception {
        final TransactionDto transactionDto = new TransactionDto();

        transactionDto.setAmount(BigDecimal.TEN.negate());
        transactionDto.setTimestamp(System.currentTimeMillis());

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().is4xxClientError());
        verify(transactionService, atMost(0)).save(any(TransactionDto.class));
    }

    @Test
    public void should_fail_invalid_dto_amount() throws Exception {
        final TransactionDto transactionDto = new TransactionDto();

        transactionDto.setAmount(BigDecimal.valueOf(0.123));
        transactionDto.setTimestamp(System.currentTimeMillis());

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().is4xxClientError());
        verify(transactionService, atMost(0)).save(any(TransactionDto.class));
    }

    @Test
    public void should_fail_negative_dto_timestamp() throws Exception {
        final TransactionDto transactionDto = new TransactionDto();

        transactionDto.setAmount(BigDecimal.TEN);
        transactionDto.setTimestamp(-1L);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().is4xxClientError());
        verify(transactionService, atMost(0)).save(any(TransactionDto.class));
    }

    @Test
    public void should_fail_null_dto_timestamp() throws Exception {
        final TransactionDto transactionDto = new TransactionDto();

        transactionDto.setAmount(BigDecimal.TEN);
        transactionDto.setTimestamp(null);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().is4xxClientError());
        verify(transactionService, atMost(0)).save(any(TransactionDto.class));
    }

    @Test
    public void should_pass_validation() throws Exception {
        final TransactionDto transactionDto = new TransactionDto();

        transactionDto.setAmount(BigDecimal.valueOf(12.3));
        transactionDto.setTimestamp(1478192204000L);

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().is(HttpStatus.CREATED.value()));
        verify(transactionService).save(any(TransactionDto.class));
    }
}