package cz.edu.upce.fei.datacollector.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class SensorData {
    private final long sensorId;
    private final Timestamp timestamp;
    private final int hits;
    private final Double temperature;
    private final Double humidity;
    private final Integer co2;
}
