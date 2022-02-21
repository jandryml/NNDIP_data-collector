package cz.edu.upce.fei.datacollector.service;

import cz.edu.upce.fei.datacollector.model.SensorData;

import java.util.List;

public interface SensorService {

    void validateSensorsExistOrCreate(List<SensorData> dataList);
}
