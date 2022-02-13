package cz.edu.upce.fei.datacollector.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class SensorData {
    private final long sensorId;
    private final Timestamp timestamp;
    private final int hits;
    private final Double temperature1;
    private final Double humidity;
    private final Integer co2_1;
    private final Integer co2_2;
    private final Integer temperature2;
}
