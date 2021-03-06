package cz.edu.upce.fei.datacollector.service;

import cz.edu.upce.fei.datacollector.model.SensorData;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface DataProcessService {

    @Scheduled(cron = "${dataProcessingTask}")
    void handleData();

    void addData(byte[] rawMessage);

    List<SensorData> processData();
}
