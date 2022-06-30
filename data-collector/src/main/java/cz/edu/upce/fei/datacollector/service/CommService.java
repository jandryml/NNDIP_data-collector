package cz.edu.upce.fei.datacollector.service;

import cz.edu.upce.fei.datacollector.model.Action;

import java.util.List;

public interface CommService {

    void writeToExternalDevices(List<Action> valuesList);
}
