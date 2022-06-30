package cz.edu.upce.fei.datacollector.service.communication.impl;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.model.OutputType;
import cz.edu.upce.fei.datacollector.model.plan.gpio.ManualGpioPlan;
import cz.edu.upce.fei.datacollector.repository.PlanRepository;
import cz.edu.upce.fei.datacollector.service.communication.RaspberryPiCommService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RaspberryPiCommServiceImpl implements RaspberryPiCommService {

    private final PlanRepository planRepository;

    private final List<GpioPin> subscribedListeners = new ArrayList<>();

    @Override
    public void writeValue(Action action) {
        log.trace("Writing value to GPIO");
        try {
            if (action.getOutputType().equals(OutputType.RASPBERRY_PIN)) {
                final GpioController gpioController = GpioFactory.getInstance();

                Pin raspiPin = RaspiPin.getPinByAddress(Integer.parseInt(action.getAddress()));
                GpioPinDigitalMultipurpose gpioPin = gpioController.provisionDigitalMultipurposePin(
                        raspiPin, PinMode.DIGITAL_OUTPUT);

                // Sets it as an Output pin.
                gpioPin.setMode(PinMode.DIGITAL_OUTPUT);

                boolean setHigh = Integer.parseInt(action.getValue()) != 0;
                log.debug("Gpio output: Setting {} to {} ", setHigh, raspiPin.getName());
                if (setHigh) {
                    // Sets the state to "high".
                    gpioPin.high();
                } else {
                    // Sets the state to "low".
                    gpioPin.low();
                }

            } else {
                log.error("Invalid output type {}", action.getOutputType());
            }
        } catch (Exception e) {
            log.error("Error during writing to rPi pin: {}", e.getMessage());
        }
        log.trace("Writing value to GPIO: Finished");
    }

    @Override
    @Scheduled(cron = "${refreshGpioListenersPeriod}")
    public void registerListeners() {
        log.trace("Registering GPIO listeners");
        List<ManualGpioPlan> manualGpioPlans = planRepository.getEnabledManualGpioPlans();

        final GpioController gpio = GpioFactory.getInstance();

        gpio.removeAllListeners();
        unsubscribeAllListeners(gpio);
        manualGpioPlans.forEach(plan -> {
            final GpioPinDigitalInput gpioInput = gpio.provisionDigitalInputPin(plan.getAddress());
            gpioInput.setMode(PinMode.DIGITAL_INPUT);
            gpioInput.setShutdownOptions(true);

            gpioInput.addListener((GpioPinListenerDigital) event -> {
                // display pin state on console
                log.debug("Gpio state change: {} = {}", event.getPin(), event.getState());
                planRepository.setManualGpioPlanActiveState(plan.getId(), event.getState().isHigh());
            });
            subscribedListeners.add(gpioInput);
        });
        log.trace("Registering GPIO listeners: Finished");
    }

    private void unsubscribeAllListeners(GpioController gpio) {
        subscribedListeners.forEach(gpio::unprovisionPin);
        subscribedListeners.clear();
    }
}
