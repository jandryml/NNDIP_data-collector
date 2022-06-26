package cz.edu.upce.fei.datacollector.repository;

import cz.edu.upce.fei.datacollector.model.Action;

import java.util.List;

public interface ActionRepository {
    List<Action> findActionsByEventId(long eventId);
}
