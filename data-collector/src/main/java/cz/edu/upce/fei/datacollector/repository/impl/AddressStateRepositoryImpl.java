package cz.edu.upce.fei.datacollector.repository.impl;

import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.repository.AddressStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AddressStateRepositoryImpl implements AddressStateRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void setAddressStates(List<Action> actionList) {
        String sql = "INSERT INTO address_state (address, output_type, value) VALUES (?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                Action action = actionList.get(i);
                ps.setString(1, action.address());
                ps.setString(2, action.outputType().name());
                ps.setString(3, action.value());
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
