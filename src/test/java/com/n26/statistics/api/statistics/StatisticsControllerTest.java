package com.n26.statistics.api.statistics;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.statistics.api.statistics.dto.StatisticsDto;
import com.n26.statistics.service.StatisticsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class StatisticsControllerTest {

    private final StatisticsDto statisticsDto = StatisticsDto.builder()
            .avg(BigDecimal.TEN)
            .max(BigDecimal.TEN)
            .min(BigDecimal.TEN)
            .sum(BigDecimal.TEN)
            .count(10L)
            .build();
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StatisticsService statisticsService;

    @Before
    public void before() {
        doReturn(statisticsDto).when(statisticsService).getStatistics();
    }

    @Test
    public void should_return_statistics() throws Exception {
        mockMvc.perform(get("/statistics"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().json(objectMapper.writeValueAsString(statisticsDto)));
        verify(statisticsService).getStatistics();
    }
}