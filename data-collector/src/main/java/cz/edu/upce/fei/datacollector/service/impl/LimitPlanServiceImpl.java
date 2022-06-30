package cz.edu.upce.fei.datacollector.service.impl;

import cz.edu.upce.fei.datacollector.model.SensorData;
import cz.edu.upce.fei.datacollector.model.plan.limit.LimitPlan;
import cz.edu.upce.fei.datacollector.repository.DataRepository;
import cz.edu.upce.fei.datacollector.repository.PlanRepository;
import cz.edu.upce.fei.datacollector.service.LimitPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LimitPlanServiceImpl implements LimitPlanService {

    private final DataRepository dataRepository;
    private final PlanRepository planRepository;

    @Value("${limitPlan.dataFetching.maxAgeInMinutes:5}")
    private int maxMinutesAgeOfDataAllowed;

    @Override
    public List<LimitPlan> getActiveLimitPlans() {
        log.trace("Getting active limit plans from db.");
        List<LimitPlan> planList = planRepository.getEnabledLimitPlans();
        return evaluateActivePlans(planList);
    }

    @Override
    public List<LimitPlan> getActiveLimitPlansFromProvided(List<LimitPlan> limitPlans) {
        log.trace("Getting active limit plans from param (properties file).");
        return evaluateActivePlans(limitPlans);
    }

    private List<LimitPlan> evaluateActivePlans(List<LimitPlan> providedLimitPlans) {
        List<SensorData> sensorData = dataRepository.getLatestDataNoOlderThan(maxMinutesAgeOfDataAllowed);

        if (sensorData.isEmpty()) {
            log.warn("No data not older than {} minutes found! Limit plans wont be activated!", maxMinutesAgeOfDataAllowed);
            return Collections.emptyList();
        } else {
            List<LimitPlan> limitPlans = providedLimitPlans.stream()
                    .filter(it -> isLimitPlanActive(it, sensorData))
                    .collect(Collectors.toList());
            log.debug("Found {} active plan(s).", limitPlans.size());
            if (log.isDebugEnabled()) {
                log.debug(limitPlans.stream().map(LimitPlan::toString).collect(Collectors.joining(",")));
            }
            return limitPlans;
        }
    }

    private boolean isLimitPlanActive(LimitPlan limitPlan, List<SensorData> sensorData) {
        boolean isActive = false;

        switch (limitPlan.getValueType()) {
            case TEMPERATURE_HIGH: {
                isActive = resolveHighTemperaturePlanStatus(limitPlan, sensorData);
                break;
            }
            case TEMPERATURE_LOW: {
                isActive = resolveLowTemperaturePlanStatus(limitPlan, sensorData);
                break;
            }
            case CO2: {
                isActive = resolveCo2PlanStatus(limitPlan, sensorData);
                break;
            }
        }

        log.trace("Plan {} is considered active: {}", limitPlan.getName(), isActive);
        limitPlan.setActive(isActive);
        planRepository.updateLimitPlan(limitPlan);

        return isActive;
    }

    private boolean resolveHighTemperaturePlanStatus(LimitPlan limitPlan, List<SensorData> sensorData) {
        OptionalDouble temperature = sensorData.stream().mapToDouble(SensorData::getTemperature).max();
        if (temperature.isPresent()) {
            if (limitPlan.isActive()) {
                return limitPlan.getOptimalValue() < temperature.getAsDouble();
            } else {
                boolean isActivated = limitPlan.getThresholdValue() <= temperature.getAsDouble();
                updateLimitPlan(limitPlan, temperature.getAsDouble(), isActivated);
                return isActivated;
            }
        }
        return false;
    }

    private boolean resolveLowTemperaturePlanStatus(LimitPlan limitPlan, List<SensorData> sensorData) {
        OptionalDouble temperature = sensorData.stream().mapToDouble(SensorData::getTemperature).min();
        if (temperature.isPresent()) {
            if (limitPlan.isActive()) {
                return limitPlan.getOptimalValue() > temperature.getAsDouble();
            } else {
                boolean isActivated = limitPlan.getThresholdValue() >= temperature.getAsDouble();
                updateLimitPlan(limitPlan, temperature.getAsDouble(), isActivated);
                return isActivated;
            }
        }
        return false;
    }

    private boolean resolveCo2PlanStatus(LimitPlan limitPlan, List<SensorData> sensorData) {
        OptionalInt co2 = sensorData.stream().mapToInt(SensorData::getCo2).max();
        if (co2.isPresent()) {
            if (limitPlan.isActive()) {
                return limitPlan.getOptimalValue() < co2.getAsInt();
            } else {
                boolean isActivated = limitPlan.getThresholdValue() <= co2.getAsInt();
                updateLimitPlan(limitPlan, co2.getAsInt(), isActivated);
                return isActivated;
            }
        }
        return false;
    }

    private void updateLimitPlan(LimitPlan limitPlan, Number value, boolean isActivated) {
        if (isActivated) {
            limitPlan.setLastTriggered(LocalDateTime.now());
            log.debug("{} has been activated.", limitPlan.getValueType().getPrettyName());
            log.trace("Actual value: {}", value);
            if (log.isDebugEnabled()) {
                log.debug(limitPlan.toString());
            }
        }
    }
}
