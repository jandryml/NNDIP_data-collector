package cz.edu.upce.fei.datacollector.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface DataReactionService {
    @Scheduled(cron = "${planReactionPeriod}")
    void resolvePlanResults();
}
