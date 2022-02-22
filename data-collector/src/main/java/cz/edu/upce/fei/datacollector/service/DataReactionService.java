package cz.edu.upce.fei.datacollector.service;

import cz.edu.upce.fei.datacollector.model.SensorData;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface DataReactionService {

    @Scheduled(cron = "${limitValuesFetch}")
    void fetchLimitValues();

    void handleData(List<SensorData> dataList);
}
