package cz.edu.upce.fei.datacollector.model.plan.limit;

import cz.edu.upce.fei.datacollector.model.plan.Plan;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class LimitPlan extends Plan {
    private LimitPlanType valueType;
    private Double optimalValue;
    private Double thresholdValue;
    private YearPeriodType periodType;
    private boolean active;
    private LocalDateTime lastTriggered;

    @Override
    public String toString() {
        return "LimitPlan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", actionList=" + actionList +
                ", priority=" + priority +
                ", planType=" + planType +
                ", valueType=" + valueType +
                ", optimalValue=" + optimalValue +
                ", thresholdValue=" + thresholdValue +
                ", periodType=" + periodType +
                ", active=" + active +
                ", lastTriggered=" + lastTriggered +
                '}';
    }
}
