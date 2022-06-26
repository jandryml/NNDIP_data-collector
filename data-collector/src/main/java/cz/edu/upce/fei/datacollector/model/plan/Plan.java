package cz.edu.upce.fei.datacollector.model.plan;

import cz.edu.upce.fei.datacollector.model.Action;
import lombok.Data;

import java.util.List;

@Data
public abstract class Plan  {
    protected long id;
    protected String name;
    protected boolean enabled;
    protected List<Action> actionList;
    protected int priority;
    protected PlanType planType;
}
