package cz.edu.upce.fei.datacollector.model.plan.gpio;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ManualGpioPlan extends GpioPlan {
    private boolean turnedOn;

    @Override
    public String toString() {
        return "ManualGpioPlan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", actionList=" + actionList +
                ", priority=" + priority +
                ", planType=" + planType +
                ", address=" + address +
                ", defaultState=" + defaultState +
                ", turnedOn=" + turnedOn +
                '}';
    }
}
