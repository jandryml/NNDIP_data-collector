package cz.edu.upce.fei.datacollector.model;

import lombok.Data;

@Data
public class LimitValue {
    private String name;
    private Double value;
    private ThresholdAction action;
}
