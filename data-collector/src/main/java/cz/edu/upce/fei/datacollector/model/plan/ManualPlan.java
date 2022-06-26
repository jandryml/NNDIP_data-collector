package cz.edu.upce.fei.datacollector.model.plan;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ManualPlan extends Plan {

    @Override
    public String toString() {
        return "ManualPlan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", actionList=" + actionList +
                ", priority=" + priority +
                ", planType=" + planType +
                '}';
    }
}
