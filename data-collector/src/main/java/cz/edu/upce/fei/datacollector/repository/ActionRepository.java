package cz.edu.upce.fei.datacollector.repository;

import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.model.ActionOutput;

import java.util.List;

public interface ActionRepository {

    List<Action> findActionsByEventId(long eventId);

    List<ActionOutput> getAllOutputs();

    List<Action> getAllDefaultActions();
}
