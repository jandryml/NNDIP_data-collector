package cz.edu.upce.fei.datacollector.service.impl;

import cz.edu.upce.fei.datacollector.config.ReactionConfig;
import cz.edu.upce.fei.datacollector.model.LimitValues;
import cz.edu.upce.fei.datacollector.model.SensorData;
import cz.edu.upce.fei.datacollector.service.DataReactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataReactionServiceImpl implements DataReactionService {

    private final ReactionConfig reactionDefaultConfig;

    private LimitValues defaultLimitValues;
    private Map<Long, LimitValues> sensorsLimitValuesMap;


    @PostConstruct
    public void initSetup() {
        fetchLimitValues();
    }


    @Override
    @Scheduled(cron = "${limitValuesFetch}")
    public void fetchLimitValues() {
        //TODO try to fetch config from DB
        // if not successful use defaults
        defaultLimitValues = reactionDefaultConfig.getDefaultLimits();

        //TODO might use map, record per sensor, or default one

    }

    @Override
    public void handleData(List<SensorData> dataList) {
        log.info("Start sensor data analysis");
        dataList.forEach(it -> {

            LimitValues limitValues = getSensorLimitValuesOrDefault(it);

            handleTemperature(it, limitValues);
            handleHumidity(it, limitValues);
            handleCo2(it, limitValues);

        });

        log.info("End data analysis");
    }

    private LimitValues getSensorLimitValuesOrDefault(SensorData it) {
        return sensorsLimitValuesMap != null ? sensorsLimitValuesMap.getOrDefault(it.getSensorId(), defaultLimitValues) : defaultLimitValues;
    }

    private void handleTemperature(SensorData sensorData, LimitValues limitValues) {
        Double fetchedValue = getAvailableTemperature(sensorData);
        if (fetchedValue != null) {
            if (fetchedValue > limitValues.getTemperatureMax()) {
                log.info("Sensor {}: temperature too high", sensorData.getSensorId());
            } else if (fetchedValue < limitValues.getTemperatureMin()) {
                log.info("Sensor {}: temperature too low", sensorData.getSensorId());
                //TODO remove later, wont be used
            } else {
                log.info("Sensor {}: temperature is OK", sensorData.getSensorId());
            }
        } else {
            log.info("Sensor {}: temperature not available", sensorData.getSensorId());
        }
    }

    private Double getAvailableTemperature(SensorData sensorData) {
        if (sensorData.getTemperature1() != null) {
            return sensorData.getTemperature1();
        } else if (sensorData.getTemperature2() != null) {
            return sensorData.getTemperature2().doubleValue();
        } else {
            return null;
        }
    }

    private void handleCo2(SensorData sensorData, LimitValues limitValues) {
        Integer fetchedValue = getAvailableCo2(sensorData);
        if (fetchedValue != null) {
            if (fetchedValue > limitValues.getCo2Max()) {
                log.info("Sensor {}: co2 too high", sensorData.getSensorId());
            } else if (fetchedValue < limitValues.getCo2Min()) {
                log.info("Sensor {}: co2 too low", sensorData.getSensorId());
                //TODO remove later, wont be used
            } else {
                log.info("Sensor {}: co2 is OK", sensorData.getSensorId());
            }
        } else {
            log.info("Sensor {}: co2 not available", sensorData.getSensorId());
        }
    }

    private Integer getAvailableCo2(SensorData sensorData) {
        if (sensorData.getCo2_1() != null) {
            return sensorData.getCo2_1();
        } else if (sensorData.getCo2_2() != null) {
            return sensorData.getCo2_2();
        } else {
            return null;
        }
    }

    private void handleHumidity(SensorData sensorData, LimitValues limitValues) {
        Double fetchedValue = sensorData.getHumidity();
        if (fetchedValue != null) {
            if (fetchedValue > limitValues.getHumidityMax()) {
                log.info("Sensor {}: humidity too high", sensorData.getSensorId());
            } else if (fetchedValue < limitValues.getHumidityMin()) {
                log.info("Sensor {}: humidity too low", sensorData.getSensorId());
                //TODO remove later, wont be used
            } else {
                log.info("Sensor {}: humidity is OK", sensorData.getSensorId());
            }
        } else {
            log.info("Sensor {}: humidity not available", sensorData.getSensorId());
        }
    }
}
