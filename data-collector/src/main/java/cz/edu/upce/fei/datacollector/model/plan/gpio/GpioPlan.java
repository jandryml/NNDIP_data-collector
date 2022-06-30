package cz.edu.upce.fei.datacollector.model.plan.gpio;

import com.pi4j.io.gpio.Pin;
import cz.edu.upce.fei.datacollector.model.plan.Plan;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.pi4j.io.gpio.PinState;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class GpioPlan extends Plan {
    protected Pin address;
    protected PinState defaultState;
}
