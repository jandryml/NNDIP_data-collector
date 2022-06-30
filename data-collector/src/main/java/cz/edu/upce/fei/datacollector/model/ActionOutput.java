package cz.edu.upce.fei.datacollector.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionOutput {
    private String address;
    private OutputType outputType;
}
