package cz.edu.upce.fei.datacollector.model;

import lombok.Data;

@Data
public class Sensor {
    private final long id;
    private final String name;
    private final Long deviceId;
}
