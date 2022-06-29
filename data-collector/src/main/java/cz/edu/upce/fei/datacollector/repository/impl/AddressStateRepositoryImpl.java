package cz.edu.upce.fei.datacollector.repository.impl;

import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.model.OutputType;
import cz.edu.upce.fei.datacollector.repository.AddressStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AddressStateRepositoryImpl implements AddressStateRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Action> getAllAddressStates() {
        String query = "SELECT address, output_type, value FROM address_state";
        List<Action> actionList = new ArrayList<>();

        jdbcTemplate.execute(query, (PreparedStatementCallback<Action>) ps -> {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Action action = new Action();
                action.setAddress(rs.getString("address"));
                action.setOutputType(OutputType.valueOf(rs.getString("output_type")));
                action.setValue(rs.getString("value"));

                actionList.add(action);
            }
            return null;
        });
        return actionList;
    }

    @Override
    public void setAddressStates(List<Action> actionList) {
        String sql = "INSERT INTO address_state (address, output_type, value) VALUES (?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                Action action = actionList.get(i);
                ps.setString(1, action.getAddress());
                ps.setString(2, action.getOutputType().name());
                ps.setString(3, action.getValue());
            }

            @Override
            public int getBatchSize() {
                return actionList.size();
            }
        });
    }

    @Override
    public void removeAllAddressStates() {
        String sql = "DELETE FROM address_state";
        jdbcTemplate.update(sql);
    }
}
