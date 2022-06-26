package cz.edu.upce.fei.datacollector.model.plan;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TimePlan extends Plan {
    private LocalTime fromTime;
    private LocalTime toTime;

    @Override
    public String toString() {
        return "TimePlan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", actionList=" + actionList +
                ", priority=" + priority +
                ", planType=" + planType +
                ", fromTime=" + fromTime +
                ", toTime=" + toTime +
                '}';
    }
}
