package cz.edu.upce.fei.datacollector.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Action {
    private Long id;
    private String name;
    private String address;
    private OutputType outputType;
    private String value;
}
