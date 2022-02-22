package cz.edu.upce.fei.datacollector.model;

import lombok.Data;

@Data
public class LimitValues {
    private String name;
    private Integer sensorId;
    private Double temperatureMax;
    private Double temperatureMin;
    private Double humidityMax;
    private Double humidityMin;
    private Double co2Max;
    private Double co2Min;
}
