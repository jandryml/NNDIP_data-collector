package cz.edu.upce.fei.datacollector.service;

import cz.edu.upce.fei.datacollector.model.LimitValue;

public interface ModbusCommService {
    void handleLimit(LimitValue limitValue);
}
