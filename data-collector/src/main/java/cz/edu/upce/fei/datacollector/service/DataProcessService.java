package cz.edu.upce.fei.datacollector.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface DataProcessService {

    @Scheduled(cron = "${dataProcessingTask}")
    void handleData();

    void addData(byte[] rawMessage);
}
