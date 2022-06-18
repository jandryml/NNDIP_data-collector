package cz.edu.upce.fei.datacollector.service.impl;

import cz.edu.upce.fei.datacollector.config.ReactionConfig;
import cz.edu.upce.fei.datacollector.model.LimitValue;
import cz.edu.upce.fei.datacollector.model.LimitValuesConfig;
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

    private LimitValuesConfig defaultLimitConfig;
    private Map<Long, LimitValuesConfig> sensorsLimitValuesMap;

    @PostConstruct
    public void initSetup() {
        fetchLimitValues();
    }

    @Override
    @Scheduled(cron = "${limitValuesFetch}")
    public void fetchLimitValues() {
        //TODO try to fetch config from DB
        // if not successful use defaults
        defaultLimitConfig = reactionDefaultConfig.getDefaultLimits();

        //TODO might use map, record per sensor, or default one

    }

    @Override
    public void handleData(List<SensorData> dataList) {
        log.info("Start sensor data analysis");
        dataList.forEach(it -> {
            LimitValuesConfig limitConfig = getSensorLimitValuesOrDefault(it);

            handleTemperature(it, limitConfig);
            handleHumidity(it, limitConfig);
            handleCo2(it, limitConfig);
        });

        log.info("End data analysis");
    }

    private LimitValuesConfig getSensorLimitValuesOrDefault(SensorData it) {
        return sensorsLimitValuesMap != null ? sensorsLimitValuesMap.getOrDefault(it.getSensorId(), defaultLimitConfig) : defaultLimitConfig;
    }

    private void handleTemperature(SensorData sensorData, LimitValuesConfig limitConfig) {
        Double fetchedValue = sensorData.getTemperature();
        if (fetchedValue != null) {
            if (isLimitConfigured(limitConfig.getTemperatureMax()) && fetchedValue > limitConfig.getTemperatureMax().getValue()) {
                log.info("Sensor {}: temperature too high", sensorData.getSensorId());
            } else if (isLimitConfigured(limitConfig.getTemperatureMin()) && fetchedValue < limitConfig.getTemperatureMin().getValue()) {
                log.info("Sensor {}: temperature too low", sensorData.getSensorId());
                //TODO remove later, wont be used
            } else {
                log.info("Sensor {}: temperature is OK", sensorData.getSensorId());
            }
        } else {
            log.info("Sensor {}: temperature not available", sensorData.getSensorId());
        }
    }

    private void handleCo2(SensorData sensorData, LimitValuesConfig limitConfig) {
        Integer fetchedValue = sensorData.getCo2();
        if (fetchedValue != null) {
            if (isLimitConfigured(limitConfig.getCo2Max()) && fetchedValue > limitConfig.getCo2Max().getValue()) {
                log.info("Sensor {}: co2 too high", sensorData.getSensorId());
            } else if (isLimitConfigured(limitConfig.getCo2Min()) && fetchedValue < limitConfig.getCo2Min().getValue()) {
                log.info("Sensor {}: co2 too low", sensorData.getSensorId());
                //TODO remove later, wont be used
            } else {
                log.info("Sensor {}: co2 is OK", sensorData.getSensorId());
            }
        } else {
            log.info("Sensor {}: co2 not available", sensorData.getSensorId());
        }
    }

    private void handleHumidity(SensorData sensorData, LimitValuesConfig limitConfig) {
        Double fetchedValue = sensorData.getHumidity();
        if (fetchedValue != null) {
            if (isLimitConfigured(limitConfig.getHumidityMax()) && fetchedValue > limitConfig.getHumidityMax().getValue()) {
                log.info("Sensor {}: humidity too high", sensorData.getSensorId());
            } else if (isLimitConfigured(limitConfig.getHumidityMin()) && fetchedValue < limitConfig.getHumidityMin().getValue()) {
                log.info("Sensor {}: humidity too low", sensorData.getSensorId());
                //TODO remove later, wont be used
            } else {
                log.info("Sensor {}: humidity is OK", sensorData.getSensorId());
            }
        } else {
            log.info("Sensor {}: humidity not available", sensorData.getSensorId());
        }
    }

    private boolean isLimitConfigured(LimitValue limitValue) {
        return limitValue != null && limitValue.getValue() != null && limitValue.getActionList() != null;
    }
}
