package cz.edu.upce.fei.datacollector.model;

import cz.edu.upce.fei.datacollector.model.LimitValue;
import lombok.Data;

@Data
public class LimitValuesConfig {
    private String name;
    private Integer sensorId;
    private LimitValue temperatureMax;
    private LimitValue temperatureMin;
    private LimitValue humidityMax;
    private LimitValue humidityMin;
    private LimitValue co2Max;
    private LimitValue co2Min;
}
