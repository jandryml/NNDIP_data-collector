package cz.edu.upce.fei.datacollector.service;

import cz.edu.upce.fei.datacollector.service.impl.DataReactionServiceImpl.VerboseAction;

import java.util.List;

public interface CommService {

    void writeToExternalDevices(List<VerboseAction> valuesList);
}
