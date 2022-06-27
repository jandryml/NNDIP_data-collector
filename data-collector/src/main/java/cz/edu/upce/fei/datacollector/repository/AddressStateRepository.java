package cz.edu.upce.fei.datacollector.repository;

import cz.edu.upce.fei.datacollector.model.Action;

import java.util.List;

public interface AddressStateRepository {

    void setAddressStates(List<Action> actionList);

    void removeAllAddressStates();
}
