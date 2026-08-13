package com.saattech.scheduler;

import com.saattech.service.LicenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LicenseExpirationScheduler {
    private final LicenseService licenseService;

    @Scheduled(cron = "0 41 17 * * ?")
    public void runLicenseExpirationJob() {
        log.info("====== CRONJOB STARTED ======");
        try {
            licenseService.processExpiredLicenses();
            log.info("====== CRONJOB FINISHED ======");
        } catch (Exception e) {
            log.error("An error occurred while CronJob was running: {}", e.getMessage(), e);
        }
    }
}
