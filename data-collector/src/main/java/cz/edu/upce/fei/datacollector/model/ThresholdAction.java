package cz.edu.upce.fei.datacollector.model;

import lombok.Data;

@Data
public class ThresholdAction {
    // TODO integrate!!
    private String name;
    private String value;
    private RegisterType registerType;
    private String pin;
}
