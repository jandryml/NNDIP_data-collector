package cz.edu.upce.fei.datacollector.service;

import cz.edu.upce.fei.datacollector.model.plan.ManualPlan;
import cz.edu.upce.fei.datacollector.model.plan.Plan;
import cz.edu.upce.fei.datacollector.model.plan.TimePlan;
import cz.edu.upce.fei.datacollector.model.plan.gpio.ManualGpioPlan;
import cz.edu.upce.fei.datacollector.model.plan.gpio.TimeGpioPlan;
import cz.edu.upce.fei.datacollector.model.plan.limit.LimitPlan;

import java.util.List;

public interface PlanService {

    List<Plan> getAllActivePlans();

    List<ManualGpioPlan> getActiveManualGpioPlan();

    List<TimeGpioPlan> getActiveTimeGpioPlan();

    List<LimitPlan> getActiveLimitPlan();

    List<LimitPlan> getActiveDefaultLimitPlan();

    List<TimePlan> getActiveTimePlan();

    List<ManualPlan> getActiveManualPlan();
}
