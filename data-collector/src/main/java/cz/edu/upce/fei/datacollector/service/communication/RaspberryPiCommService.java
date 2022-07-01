package cz.edu.upce.fei.datacollector.service.communication;

import cz.edu.upce.fei.datacollector.model.Action;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Handle communication to RaspberryPi GPIO interface.
 */
public interface RaspberryPiCommService {

    /**
     * Writes value to GPIO pin.
     * All necessary data (address and value) are derived from provided action param.
     *
     * Changes the GPIO pin to OUTPUT mode.
     */
    void writeValue(Action action);

    /**
     * Removes all present GPIO listeners
     * then fetches all active GPIO listeners and registers them.
     *
     * Changes the GPIO pin to INPUT mode.
     */
    @Scheduled(cron = "${refreshGpioListenersPeriod}")
    void registerListeners();
}
