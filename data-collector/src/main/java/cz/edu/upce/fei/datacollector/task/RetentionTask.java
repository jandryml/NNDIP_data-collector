package cz.edu.upce.fei.datacollector.task;


import cz.edu.upce.fei.datacollector.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetentionTask {

    @Value("${sensordata.retention.days:30}")
    private int sensorDataRetentionDays;

    private final DataRepository dataRepository;

    @Scheduled(cron = "${dataRetentionInvokePeriod}")
    public void sensorDataRetention() {
        LocalDateTime retentionLimit = LocalDateTime.now().minusDays(sensorDataRetentionDays);
        log.info("Removing sensor data older than {} days", sensorDataRetentionDays);
        dataRepository.deleteDataOlderThan(retentionLimit);
    }
}
