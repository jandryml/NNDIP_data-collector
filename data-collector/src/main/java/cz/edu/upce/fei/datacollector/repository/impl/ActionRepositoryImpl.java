package cz.edu.upce.fei.datacollector.repository.impl;

import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.model.OutputType;
import cz.edu.upce.fei.datacollector.repository.ActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ActionRepositoryImpl implements ActionRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Action> findActionsByEventId(long eventId) {
        String query = "SELECT id, name, address, output_type, value " +
                "FROM action a INNER JOIN event_actions ea " +
                "ON a.id = ea.action_id " +
                "WHERE ea.event_id = ?";
        List<Action> actionList = new ArrayList<>();

        jdbcTemplate.execute(query, (PreparedStatementCallback<Action>) ps -> {
            ps.setLong(1, eventId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                actionList.add(new Action()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .address(rs.getString("address"))
                        .outputType(OutputType.valueOf(rs.getString("output_type")))
                        .value(rs.getString("value"))
                );
            }
            return null;
        });

        return actionList;
    }
}
