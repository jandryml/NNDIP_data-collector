package cz.edu.upce.fei.datacollector.repository;

import cz.edu.upce.fei.datacollector.model.plan.ManualPlan;
import cz.edu.upce.fei.datacollector.model.plan.TimePlan;
import cz.edu.upce.fei.datacollector.model.plan.gpio.ManualGpioPlan;
import cz.edu.upce.fei.datacollector.model.plan.gpio.TimeGpioPlan;
import cz.edu.upce.fei.datacollector.model.plan.limit.LimitPlan;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanRepository {
    List<ManualGpioPlan> getEnabledManualGpioPlans();
    void setManualGpioPlanActiveState(long planId, boolean isOn);

    void setTimeGpioPlanActualTime(long planId, LocalDateTime localDateTime);

    List<TimeGpioPlan> getEnabledTimeGpioPlans();
    List<ManualPlan> getEnabledManualPlans();
    List<TimePlan> getEnabledTimePlans();
    List<LimitPlan> getEnabledLimitPlans();
    void updateLimitPlan(LimitPlan limitPlan);
}
