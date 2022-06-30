package cz.edu.upce.fei.datacollector.repository.impl;

import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.model.ActionOutput;
import cz.edu.upce.fei.datacollector.model.OutputType;
import cz.edu.upce.fei.datacollector.repository.ActionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActionRepositoryImpl implements ActionRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Action> findActionsByEventId(long eventId) {
        log.trace("Fetching actions by event id: {}", eventId);

        String query = "SELECT id, name, address, output_type, value " +
                "FROM action a INNER JOIN event_actions ea " +
                "ON a.id = ea.action_id " +
                "WHERE ea.event_id = ?";
        List<Action> actionList = new ArrayList<>();

        jdbcTemplate.execute(query, (PreparedStatementCallback<Action>) ps -> {
            ps.setLong(1, eventId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Action action = new Action();
                action.setId(rs.getLong("id"));
                action.setName(rs.getString("name"));
                action.setAddress(rs.getString("address"));
                action.setOutputType(OutputType.valueOf(rs.getString("output_type")));
                action.setValue(rs.getString("value"));
                actionList.add(action);
            }
            return null;
        });

        if (log.isTraceEnabled()) {
            log.trace("Result: ");
            actionList.forEach(action -> log.trace(String.valueOf(action)));
        }

        return actionList;
    }

    @Override
    public List<ActionOutput> getAllOutputs() {
        log.trace("Fetching all handled outputs");

        String query = "SELECT output_type, address FROM action GROUP BY output_type, address;";

        List<ActionOutput> actionOutputs = new ArrayList<>();

        jdbcTemplate.execute(query, (PreparedStatementCallback<ActionOutput>) ps -> {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                actionOutputs.add(new ActionOutput(
                        rs.getString("address"),
                        OutputType.valueOf(rs.getString("output_type")))
                );
            }
            return null;
        });

        if (log.isTraceEnabled()) {
            log.trace("Result: ");
            actionOutputs.forEach(actionOutput -> log.trace(String.valueOf(actionOutput)));
        }

        return actionOutputs;
    }
}
