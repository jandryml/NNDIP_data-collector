package cz.edu.upce.fei.datacollector.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class LimitValue {
    private String name;
    private Double value;
    private List<ThresholdAction> actionList = Collections.emptyList();
}
