package cz.edu.upce.fei.datacollector.service.impl;

import cz.edu.upce.fei.datacollector.config.ActionExclusionConfig;
import cz.edu.upce.fei.datacollector.config.DefaultPlanConfig;
import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.model.ActionOutput;
import cz.edu.upce.fei.datacollector.model.ExclusionRule;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class DataReactionServiceImpl implements DataReactionService {

    private final PlanService planService;
    private final ActionRepository actionRepository;
    private final CommService commService;
    private final DefaultPlanConfig defaultPlanConfig;
    private final ActionExclusionConfig actionExclusionConfig;

    @Override
    @Scheduled(cron = "${planReactionPeriod}")
    public void resolvePlanResults() {
        log.info("Resolving active plans outputs");

        // creating map of handled outputs
        List<ActionOutput> actionOutputs = actionRepository.getAllOutputs();
        Map<ActionOutput, VerboseAction> resultMap = prepareEmptyResultMap(actionOutputs);

        log.trace("Fetching all active plans from db and sort it by priority");
        List<Plan> planList = planService.getAllActivePlans();
        planList.sort(Comparator.comparing(Plan::getPriority).reversed());

        log.trace("Filling output plan according to active plans");
        planList.forEach(plan ->
                fillMissingActionsToResultMap(resultMap, plan.getPriority(), plan.getActionList(), plan.getName()));

        log.trace("Filling with default Limit plans from application properties");
        planService.getActiveDefaultLimitPlan().forEach(plan ->
                fillMissingActionsToResultMap(resultMap, plan.getPriority(), plan.getActionList(), plan.getName()));

        log.trace("Filling missing values with default values from application properties");
        fillMissingActionsToResultMap(resultMap, -1, defaultPlanConfig.getDefaultActions(), "Default actions from properties file");

        log.trace("Filling not filled outputs with 0 value");
        List<VerboseAction> resultActions = fillEmptyRecordsAndTransfer(resultMap);

        if (log.isTraceEnabled()) {
            log.trace("Printing result actions list that will be filled: ");
            resultActions.forEach(action -> log.trace(String.valueOf(action)));
        }

        // write to modbus/rPi
        commService.writeToExternalDevices(resultActions);

        log.trace("Resolving active plans outputs: Finished");
    }

    private void fillMissingActionsToResultMap(Map<ActionOutput, VerboseAction> resultMap, int priority, List<Action> actionList, String planName) {
        actionList.forEach(action -> {
                    resolveActionExclusion(resultMap, action);

                    resultMap.putIfAbsent(
                            transferAction(action),
                            new VerboseAction(priority, action, planName));
                }
        );
    }

    private void resolveActionExclusion(Map<ActionOutput, VerboseAction> resultMap, Action actualAction) {
        for (ExclusionRule exclusionRule : actionExclusionConfig.getActionExclusionRules()) {
            ActionOutput exclusionActionOutput = new ActionOutput(exclusionRule.getAddress(), exclusionRule.getOutputType());
            VerboseAction actualVerboseActionInMap = resultMap.get(exclusionActionOutput);

            if (actualVerboseActionInMap == null) {
                continue;
            }

            String actualValueInMap = actualVerboseActionInMap.action.getValue();

            if (Objects.equals(actualValueInMap, exclusionRule.getValue())) {
                for (String higherPrioValues : exclusionRule.getHigherPrioValues()) {
                    if (Objects.equals(actualAction.getValue(), higherPrioValues)){
                        resultMap.remove(exclusionActionOutput);
                        break;
                    }
                }
            }
        }
    }

    private ActionOutput transferAction(Action action) {
        return new ActionOutput(action.getAddress(), action.getOutputType());
    }

    private Map<ActionOutput, VerboseAction> prepareEmptyResultMap(List<ActionOutput> actionOutputs) {
        Map<ActionOutput, VerboseAction> resultMap = new HashMap<>();
        for (ActionOutput actionOutput : actionOutputs) {
            resultMap.put(actionOutput, null);
        }
        return resultMap;
    }

    private List<VerboseAction> fillEmptyRecordsAndTransfer(Map<ActionOutput, VerboseAction> resultMap) {
        List<Action> defaultActions = actionRepository.getAllDefaultActions();

        defaultActions.forEach(action -> {
            ActionOutput actionOutput = new ActionOutput(action.getAddress(), action.getOutputType());
            resultMap.putIfAbsent(actionOutput, new VerboseAction(-2, action, "Default action"));
        });

        for (ActionOutput actionOutput : resultMap.keySet()) {
            Action action = new Action();
            action.setName("Generated zero value action");
            action.setAddress(actionOutput.getAddress());
            action.setOutputType(actionOutput.getOutputType());
            action.setValue("0");

            resultMap.putIfAbsent(actionOutput, new VerboseAction(-2, action, "Default 0 values"));
        }
        return new ArrayList<>(resultMap.values());
    }

    @Getter
    @RequiredArgsConstructor
    public class VerboseAction {
        private final int priority;
        private final Action action;
        private final String planName;
    }
}
