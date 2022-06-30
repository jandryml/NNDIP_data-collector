package cz.edu.upce.fei.datacollector.service.impl;

import cz.edu.upce.fei.datacollector.config.DefaultPlanConfig;
import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.model.ActionOutput;
import cz.edu.upce.fei.datacollector.model.plan.Plan;
import cz.edu.upce.fei.datacollector.repository.ActionRepository;
import cz.edu.upce.fei.datacollector.service.CommService;
import cz.edu.upce.fei.datacollector.service.DataReactionService;
import cz.edu.upce.fei.datacollector.service.PlanService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataReactionServiceImpl implements DataReactionService {

    private final PlanService planService;
    private final ActionRepository actionRepository;
    private final CommService commService;
    private final DefaultPlanConfig defaultPlanConfig;

    @Override
    @Scheduled(cron = "${planReactionPeriod}")
    public void resolvePlanResults() {
        log.info("Resolving active plans outputs");

        // creating map of handled outputs
        List<ActionOutput> actionOutputs = actionRepository.getAllOutputs();
        Map<ActionOutput, MapValue> resultMap = prepareEmptyResultMap(actionOutputs);

        log.trace("Fetching all active plans from db and sort it by priority");
        List<Plan> planList = planService.getAllActivePlans();
        planList.sort(Comparator.comparing(Plan::getPriority));

        log.trace("Filling output plan according to active plans");
        planList.forEach(plan ->
                fillMissingActionsToResultMap(resultMap, plan.getPriority(), plan.getActionList()));

        log.trace("Filling with default Limit plans from application properties");
        planService.getActiveDefaultLimitPlan().forEach(plan ->
                fillMissingActionsToResultMap(resultMap, plan.getPriority(), plan.getActionList()));

        log.trace("Filling missing values with default values from application properties");
        fillMissingActionsToResultMap(resultMap, -1, defaultPlanConfig.getDefaultActions());

        log.trace("Filling not filled outputs with 0 value");
        List<Action> resultActions = fillEmptyRecordsAndTransfer(resultMap);

        if (log.isTraceEnabled()) {
            log.trace("Printing result actions list that will be filled: ");
            resultActions.forEach(action -> log.trace(String.valueOf(action)));
        }

        // write to modbus/rPi
        commService.writeToExternalDevices(resultActions);

        log.trace("Resolving active plans outputs: Finished");
    }

    private void fillMissingActionsToResultMap(Map<ActionOutput, MapValue> resultMap, int priority, List<Action> actionList) {
        actionList.forEach(action ->
                resultMap.putIfAbsent(
                        transferAction(action),
                        new MapValue(priority, action))
        );
    }

    private ActionOutput transferAction(Action action) {
        return new ActionOutput(action.getAddress(), action.getOutputType());
    }

    private Map<ActionOutput, MapValue> prepareEmptyResultMap(List<ActionOutput> actionOutputs) {
        Map<ActionOutput, MapValue> resultMap = new HashMap<>();
        for (ActionOutput actionOutput : actionOutputs) {
            resultMap.put(actionOutput, null);
        }
        return resultMap;
    }

    private List<Action> fillEmptyRecordsAndTransfer(Map<ActionOutput, MapValue> resultMap) {
        for (ActionOutput actionOutput : resultMap.keySet()) {
            Action action = new Action();
            action.setName("Generated zero value action");
            action.setAddress(actionOutput.getAddress());
            action.setOutputType(actionOutput.getOutputType());
            action.setValue("0");

            resultMap.putIfAbsent(actionOutput, new MapValue(0, action));
        }
        return resultMap.values().stream().map(MapValue::getAction).collect(Collectors.toList());
    }

    @Getter
    @RequiredArgsConstructor
    private class MapValue {
        private final int priority;
        private final Action action;
    }
}
