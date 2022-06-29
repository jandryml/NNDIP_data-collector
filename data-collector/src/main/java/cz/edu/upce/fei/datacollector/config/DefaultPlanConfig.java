package cz.edu.upce.fei.datacollector.config;

import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.model.plan.limit.LimitPlan;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "reaction")
public class DefaultPlanConfig {
    private List<Action> defaultActions;
    private List<LimitPlan> defaultLimitPlans;
}
