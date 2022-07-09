package cz.edu.upce.fei.datacollector.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExclusionRule {
    private String address;
    private OutputType outputType;
    private String value;
    private List<String> higherPrioValues = new ArrayList<>();
}
