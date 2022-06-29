package cz.edu.upce.fei.datacollector.service.communication;

import cz.edu.upce.fei.datacollector.model.Action;
import org.springframework.scheduling.annotation.Scheduled;

public interface RaspberryPiCommService {

    void writeValue(Action action);

    @Scheduled(cron = "${refreshGpioListenersPeriod}")
    void registerListeners();
}
