package cz.edu.upce.fei.datacollector.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface DataReactionService {

    // TODO check and remove
//    @Scheduled(cron = "${planValuesFetch}")
//    void fetchLimitValues();

    @Scheduled(cron = "${planReactionPeriod}")
    void handleData();
}
