package cz.edu.upce.fei.datacollector.repository;

import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.model.ActionOutput;
import cz.edu.upce.fei.datacollector.service.impl.DataReactionServiceImpl.VerboseAction;

import java.util.List;

public interface AddressStateRepository {

    List<Action> getAllAddressStates();

    void setAddressState(VerboseAction verboseAction);

    void removeAddressStateById(ActionOutput actionOutput);
}
