package cz.edu.upce.fei.datacollector.model;

import lombok.Data;

@Data
public class Action {
    private Long id;
    private String name;
    private String address;
    private OutputType outputType;
    private String value;
}
