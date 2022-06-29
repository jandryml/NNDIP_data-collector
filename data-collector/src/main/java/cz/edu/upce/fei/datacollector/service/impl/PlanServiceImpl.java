package cz.edu.upce.fei.datacollector.service.impl;

import cz.edu.upce.fei.datacollector.config.DefaultPlanConfig;
import cz.edu.upce.fei.datacollector.model.plan.ManualPlan;
import cz.edu.upce.fei.datacollector.model.plan.Plan;
import cz.edu.upce.fei.datacollector.model.plan.TimePlan;
import cz.edu.upce.fei.datacollector.model.plan.gpio.ManualGpioPlan;
import cz.edu.upce.fei.datacollector.model.plan.gpio.TimeGpioPlan;
import cz.edu.upce.fei.datacollector.model.plan.limit.LimitPlan;
import cz.edu.upce.fei.datacollector.repository.PlanRepository;
import cz.edu.upce.fei.datacollector.service.LimitPlanService;
import cz.edu.upce.fei.datacollector.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final LimitPlanService limitPlanService;
    private final DefaultPlanConfig defaultPlanConfig;

    @Override
    public List<Plan> getAllActivePlans() {
        List<Plan> planList = new ArrayList<>();
        planList.addAll(getActiveManualPlan());
        planList.addAll(getActiveTimePlan());
        planList.addAll(getActiveLimitPlan());

        planList.addAll(getActiveManualGpioPlan());
        planList.addAll(getActiveTimeGpioPlan());

        return planList;
    }

    @Override
    public List<ManualGpioPlan> getActiveManualGpioPlan() {
        return planRepository.getEnabledManualGpioPlans().stream()
                .filter(ManualGpioPlan::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeGpioPlan> getActiveTimeGpioPlan() {
        return planRepository.getEnabledTimeGpioPlans().stream()
                .filter(it -> it.getLastTriggered().plusMinutes(it.getDuration()).isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LimitPlan> getActiveLimitPlan() {
        return limitPlanService.getActiveLimitPlans();
    }

    @Override
    public List<LimitPlan> getActiveDefaultLimitPlan() {
        return limitPlanService.getActiveLimitPlansFromProvided(defaultPlanConfig.getDefaultLimitPlans());
    }

    @Override
    public List<TimePlan> getActiveTimePlan() {
        return planRepository.getEnabledTimePlans().stream()
                .filter(this::isTimePlanActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<ManualPlan> getActiveManualPlan() {
        return planRepository.getEnabledManualPlans();
    }

    private boolean isTimePlanActive(TimePlan timePlan) {
        LocalDateTime actual = LocalDateTime.now();
        LocalTime fromTime = timePlan.getFromTime();
        LocalTime toTime = timePlan.getToTime();

        // used when timePlan is spanning over midnight (e.g.: 23:00 - 1:00)
        if (fromTime.isAfter(toTime)) {
            // time before midnight (e.g.: 23:37)
            if (actual.toLocalTime().isAfter(fromTime)) {
                 return true;
                // time after midnight (e.g.: 00:37)
            } else {
                return actual.toLocalTime().isBefore(toTime);
            }
        } else {
            return fromTime.isBefore(actual.toLocalTime()) && toTime.isAfter(actual.toLocalTime());
        }
    }
}
