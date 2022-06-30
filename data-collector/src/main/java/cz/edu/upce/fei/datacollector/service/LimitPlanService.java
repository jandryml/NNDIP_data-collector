package cz.edu.upce.fei.datacollector.service;

import cz.edu.upce.fei.datacollector.model.plan.limit.LimitPlan;

import java.util.List;

public interface LimitPlanService {

    List<LimitPlan> getActiveLimitPlans();

    List<LimitPlan> getActiveLimitPlansFromProvided(List<LimitPlan> limitPlans);
}
