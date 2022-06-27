package cz.edu.upce.fei.datacollector.service.impl;

import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.model.ActionOutput;
import cz.edu.upce.fei.datacollector.model.plan.Plan;
import cz.edu.upce.fei.datacollector.repository.ActionRepository;
import cz.edu.upce.fei.datacollector.repository.AddressStateRepository;
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

    // TODO check and remove
//    private final ReactionConfig reactionDefaultConfig;
//    private LimitValuesConfig defaultLimitConfig;
//    private Map<Long, LimitValuesConfig> sensorsLimitValuesMap;

    private final PlanService planService;
    private final ActionRepository actionRepository;
    private final CommService commService;
    private final AddressStateRepository addressStateRepository;


    // TODO check and remove
//    @PostConstruct
//    public void initSetup() {
//        fetchLimitValues();
//    }

//    @Override
//    @Scheduled(cron = "${planValuesFetch}")
//    public void fetchLimitValues() {
//        //TODO try to fetch config from DB
//        // if not successful use defaults
////        defaultLimitConfig = reactionDefaultConfig.getDefaultLimits();
//
//        //TODO might use map, record per sensor, or default one
//    }

    @Override
    @Scheduled(cron = "${planReactionPeriod}")
    // TODO rename
    public void handleData() {
        log.info("Start sensor plan analysis");

        List<ActionOutput> actionOutputs = actionRepository.getAllOutputs();
        Map<ActionOutput, MapValue> resultMap = prepareEmptyResultMap(actionOutputs);

        List<Plan> planList = planService.getAllActivePlans();
        planList.sort(Comparator.comparing(Plan::getPriority));

        for (Plan plan : planList) {
            plan.getActionList().forEach(actualAction ->
                    resultMap.putIfAbsent(transferAction(actualAction), new MapValue(plan.getPriority(), actualAction)));
        }

        // TODO fill data from local application.properties?

        List<Action> resultActions = fillEmptyRecordsAndTransfer(resultMap);
        // write to registers
        writeToRegisters(resultActions);
        // write to modbus/rPi
        commService.writeToExternalDevices(resultActions);

        log.info("End plan analysis");
    }

    private void writeToRegisters(List<Action> resultActions) {
        addressStateRepository.removeAllAddressStates();
        addressStateRepository.setAddressStates(resultActions);
    }

    private ActionOutput transferAction(Action action) {
        return new ActionOutput().outputType(action.outputType()).address(action.address());
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
            resultMap.putIfAbsent(actionOutput, new MapValue(
                    0,
                    new Action()
                            .name("Generated zero value action")
                            .address(actionOutput.address())
                            .outputType(actionOutput.outputType())
                            .value("0")
            ));
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
