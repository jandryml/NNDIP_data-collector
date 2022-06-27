package cz.edu.upce.fei.datacollector.repository.impl;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.model.plan.ManualPlan;
import cz.edu.upce.fei.datacollector.model.plan.PlanType;
import cz.edu.upce.fei.datacollector.model.plan.TimePlan;
import cz.edu.upce.fei.datacollector.model.plan.gpio.ManualGpioPlan;
import cz.edu.upce.fei.datacollector.model.plan.gpio.TimeGpioPlan;
import cz.edu.upce.fei.datacollector.model.plan.limit.LimitPlan;
import cz.edu.upce.fei.datacollector.model.plan.limit.LimitPlanType;
import cz.edu.upce.fei.datacollector.model.plan.limit.YearPeriodType;
import cz.edu.upce.fei.datacollector.repository.ActionRepository;
import cz.edu.upce.fei.datacollector.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlanRepositoryImpl implements PlanRepository {

    private final JdbcTemplate jdbcTemplate;

    private final ActionRepository actionRepository;

    @Override
    public List<ManualGpioPlan> getEnabledManualGpioPlans() {
        String query = "SELECT p.id, p.name, p.enabled, p.priority, p.event_id, gp.pin_address, gp.default_state, mgp.active FROM plan p " +
                "INNER JOIN gpio_plan gp ON p.id = gp.id " +
                "INNER JOIN manual_gpio_plan mgp ON gp.id = mgp.id " +
                "where p.plan_type = ? and enabled";

        List<ManualGpioPlan> resultList = new ArrayList<>();

        jdbcTemplate.execute(query, (PreparedStatementCallback<ManualGpioPlan>) ps -> {
            ps.setString(1, PlanType.MANUAL_GPIO_PLAN.name());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                List<Action> actionList = actionRepository.findActionsByEventId(rs.getLong("event_id"));

                ManualGpioPlan plan = new ManualGpioPlan();
                plan.setId(rs.getLong("id"));
                plan.setName(rs.getString("name"));
                plan.setEnabled(rs.getBoolean("enabled"));
                plan.setActionList(actionList);
                plan.setPriority(rs.getInt("priority"));
                plan.setPlanType(PlanType.MANUAL_GPIO_PLAN);
                plan.setAddress(RaspiPin.getPinByAddress(rs.getInt("pin_address")));
                plan.setDefaultState(PinState.valueOf(rs.getString("default_state")));
                plan.setActive(rs.getBoolean("active"));

                resultList.add(plan);
            }
            return null;
        });
        return resultList;
    }

    @Override
    public List<TimeGpioPlan> getEnabledTimeGpioPlans() {
        String query = "SELECT p.id, p.name, p.enabled, p.priority, p.event_id, gp.address, gp.default_state, tgp.duration, tgp.last_triggered FROM plan p " +
                "INNER JOIN gpio_plan gp ON p.id = gp.id " +
                "INNER JOIN time_gpio_plan tgp ON gp.id = tgp.id " +
                "where p.plan_type = ? and enabled";

        List<TimeGpioPlan> resultList = new ArrayList<>();

        jdbcTemplate.execute(query, (PreparedStatementCallback<TimeGpioPlan>) ps -> {
            ps.setString(1, PlanType.TIME_GPIO_PLAN.name());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                List<Action> actionList = actionRepository.findActionsByEventId(rs.getLong("event_id"));

                TimeGpioPlan plan = new TimeGpioPlan();
                plan.setId(rs.getLong("id"));
                plan.setName(rs.getString("name"));
                plan.setEnabled(rs.getBoolean("enabled"));
                plan.setActionList(actionList);
                plan.setPriority(rs.getInt("priority"));
                plan.setPlanType(PlanType.TIME_GPIO_PLAN);
                plan.setAddress(RaspiPin.getPinByAddress(rs.getInt("pin_address")));
                plan.setDefaultState(PinState.valueOf(rs.getString("default_state")));
                plan.setDuration(rs.getInt("duration"));
                plan.setLastTriggered(rs.getTimestamp("last_triggered").toLocalDateTime());

                resultList.add(plan);
            }
            return null;
        });
        return resultList;
    }

    @Override
    public List<ManualPlan> getEnabledManualPlans() {
        String query = "SELECT id, name, enabled, priority, event_id FROM plan " +
                "where plan_type = ? and enabled";

        List<ManualPlan> resultList = new ArrayList<>();

        jdbcTemplate.execute(query, (PreparedStatementCallback<ManualPlan>) ps -> {
            ps.setString(1, PlanType.MANUAL_PLAN.name());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                List<Action> actionList = actionRepository.findActionsByEventId(rs.getLong("event_id"));

                ManualPlan plan = new ManualPlan();
                plan.setId(rs.getLong("id"));
                plan.setName(rs.getString("name"));
                plan.setEnabled(rs.getBoolean("enabled"));
                plan.setActionList(actionList);
                plan.setPriority(rs.getInt("priority"));
                plan.setPlanType(PlanType.MANUAL_PLAN);

                resultList.add(plan);
            }
            return null;
        });

        return resultList;
    }

    @Override
    public List<TimePlan> getEnabledTimePlans() {
        String query = "SELECT p.id, p.name, p.enabled, p.priority, p.event_id, tp.from_time, tp.to_time FROM plan p " +
                "INNER JOIN time_plan tp ON p.id = tp.id " +
                "where p.plan_type = ? and enabled";

        List<TimePlan> resultList = new ArrayList<>();

        jdbcTemplate.execute(query, (PreparedStatementCallback<TimePlan>) ps -> {
            ps.setString(1, PlanType.TIME_PLAN.name());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                List<Action> actionList = actionRepository.findActionsByEventId(rs.getLong("event_id"));

                TimePlan plan = new TimePlan();
                plan.setId(rs.getLong("id"));
                plan.setName(rs.getString("name"));
                plan.setEnabled(rs.getBoolean("enabled"));
                plan.setActionList(actionList);
                plan.setPriority(rs.getInt("priority"));
                plan.setPlanType(PlanType.TIME_PLAN);
                plan.setFromTime(rs.getTimestamp("from_time").toLocalDateTime().toLocalTime());
                plan.setToTime(rs.getTimestamp("to_time").toLocalDateTime().toLocalTime());

                resultList.add(plan);
            }
            return null;
        });

        return resultList;
    }

    @Override
    public List<LimitPlan> getEnabledLimitPlans() {
        String query = "SELECT p.id, p.name, p.enabled, p.priority, p.event_id, lp.value_type, lp.optimal_value, lp.threshold_value, yp.name as period_name, lp.active, lp.last_triggered FROM plan p " +
                "INNER JOIN limit_plan lp ON p.id = lp.id " +
                "INNER JOIN year_period yp ON lp.year_period_id = yp.id " +
                "where p.plan_type = ? and p.enabled and yp.active";

        List<LimitPlan> resultList = new ArrayList<>();

        jdbcTemplate.execute(query, (PreparedStatementCallback<LimitPlan>) ps -> {
            ps.setString(1, PlanType.LIMIT_PLAN.name());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                List<Action> actionList = actionRepository.findActionsByEventId(rs.getLong("event_id"));

                LimitPlan plan = new LimitPlan();
                plan.setId(rs.getLong("id"));
                plan.setName(rs.getString("name"));
                plan.setEnabled(rs.getBoolean("enabled"));
                plan.setActionList(actionList);
                plan.setPriority(rs.getInt("priority"));
                plan.setPlanType(PlanType.LIMIT_PLAN);
                plan.setValueType(LimitPlanType.valueOf(rs.getString("value_type")));
                plan.setOptimalValue(rs.getDouble("optimal_value"));
                plan.setThresholdValue(rs.getDouble("threshold_value"));
                plan.setActive(rs.getBoolean("active"));
                plan.setLastTriggered(rs.getTimestamp("last_triggered").toLocalDateTime());
                plan.setPeriodType(YearPeriodType.valueOf(rs.getString("period_name")));

                resultList.add(plan);
            }
            return null;
        });

        return resultList;
    }

    @Override
    public void updateLimitPlan(LimitPlan limitPlan) {
        String sql = "UPDATE limit_plan SET active = ?, last_triggered = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                limitPlan.isActive(),
                limitPlan.getLastTriggered().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                limitPlan.getId()
        );
    }
}
