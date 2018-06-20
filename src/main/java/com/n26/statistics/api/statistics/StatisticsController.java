package com.n26.statistics.api.statistics;

import com.n26.statistics.api.statistics.dto.StatisticsDto;
import com.n26.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/statistics")
    public StatisticsDto getStatistics() {
        return statisticsService.getStatistics();
    }
}
