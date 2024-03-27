package com.cj.flood.func.modular.homePage.scheduler;


import com.cj.flood.func.modular.homePage.service.FloodHomePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "flood.schedule", name = "enabled", havingValue = "true", matchIfMissing = true)
public class HomePageTask {
    private final FloodHomePageService floodHomePageService;

    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void waterFlowAlarmEventPolling() {
        floodHomePageService.waterStorageOverviewSchedule(new Date());
        floodHomePageService.rainfallSchedule(new Date());
    }

    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void floodRetentionCapacitySchedule() {
        floodHomePageService.calcLzzFloodRetention();
        floodHomePageService.calcTthFloodRetention();
    }
}
