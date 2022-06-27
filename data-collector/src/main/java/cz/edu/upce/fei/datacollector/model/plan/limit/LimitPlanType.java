package cz.edu.upce.fei.datacollector.model.plan.limit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LimitPlanType {
    TEMPERATURE_LOW("Low temperature plan"),
    TEMPERATURE_HIGH("High temperature plan"),
    CO2("Co2 plan");

    private final String prettyName;

}
