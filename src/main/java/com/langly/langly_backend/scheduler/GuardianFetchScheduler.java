package com.langly.langly_backend.scheduler;

import com.langly.langly_backend.service.GuardianFetchService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GuardianFetchScheduler {

    private final GuardianFetchService guardianFetchService;

    public GuardianFetchScheduler(GuardianFetchService guardianFetchService) {
        this.guardianFetchService = guardianFetchService;
    }

    @Scheduled(fixedRateString = "${guardian.fetch.interval}", initialDelay = 5000)
    public void fetch() {
        guardianFetchService.fetchAndSave();
    }
}