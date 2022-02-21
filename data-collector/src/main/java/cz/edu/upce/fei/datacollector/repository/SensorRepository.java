package cz.edu.upce.fei.datacollector.repository;

import cz.edu.upce.fei.datacollector.model.Sensor;

public interface SensorRepository {

    boolean sensorExists(long id);

    void createSensor(Sensor sensor);
}
