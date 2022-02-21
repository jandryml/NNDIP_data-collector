package cz.edu.upce.fei.datacollector.repository;

import cz.edu.upce.fei.datacollector.model.SensorData;

import java.util.List;

public interface DataRepository {

    void saveData(List<SensorData> data);
}
