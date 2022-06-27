package cz.edu.upce.fei.datacollector.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class ActionOutput {
    private String address;
    private OutputType outputType;
}
