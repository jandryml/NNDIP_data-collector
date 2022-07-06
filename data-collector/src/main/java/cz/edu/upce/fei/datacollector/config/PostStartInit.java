package cz.edu.upce.fei.datacollector.config;

import cz.edu.upce.fei.datacollector.service.communication.RaspberryPiCommService;
import cz.edu.upce.fei.datacollector.task.ConnectionHandlerTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class PostStartInit {

    private final RaspberryPiCommService raspberryPiCommService;

    private final ConnectionHandlerTask connectionHandlerTask;

    @PostConstruct
    private void postConstruct() {
        raspberryPiCommService.registerListeners();
        connectionHandlerTask.run();
    }
}
