package cz.edu.upce.fei.datacollector.service.communication;

import cz.edu.upce.fei.datacollector.model.Action;

public interface ModbusCommService {

    void writeToCoil(Action action);

    void writeToRegister(Action action);
}
