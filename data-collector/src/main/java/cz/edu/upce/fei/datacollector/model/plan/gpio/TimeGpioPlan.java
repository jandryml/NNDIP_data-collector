package cz.edu.upce.fei.datacollector.model.plan.gpio;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TimeGpioPlan extends GpioPlan {
    private int duration;
    private LocalDateTime lastTriggered;

    @Override
    public String toString() {
        return "TimeGpioPlan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", actionList=" + actionList +
                ", priority=" + priority +
                ", planType=" + planType +
                ", duration=" + duration +
                ", address=" + address +
                ", defaultState=" + defaultState +
                ", lastTriggered=" + lastTriggered +
                '}';
    }
}
