package cz.edu.upce.fei.datacollector.repository.impl;

import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.model.ActionOutput;
import cz.edu.upce.fei.datacollector.model.OutputType;
import cz.edu.upce.fei.datacollector.repository.AddressStateRepository;
import cz.edu.upce.fei.datacollector.service.impl.DataReactionServiceImpl.VerboseAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddressStateRepositoryImpl implements AddressStateRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Action> getAllAddressStates() {
        log.trace("Fetching all handled outputs state");

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
        log.trace("Finished");

        return actionList;
    }

    @Override
    public void setAddressState(VerboseAction verboseAction) {
        String sql = "INSERT INTO address_state (address, output_type, value, action_name, plan_name) VALUES (?,?,?,?,?)";

        jdbcTemplate.execute(sql, (PreparedStatementCallback<Action>) ps -> {
            ps.setString(1, verboseAction.getAction().getAddress());
            ps.setString(2, verboseAction.getAction().getOutputType().name());
            ps.setString(3, verboseAction.getAction().getValue());
            ps.setString(4, verboseAction.getAction().getName());
            ps.setString(5, verboseAction.getPlanName());

            ps.execute();
            return null;
        });
        log.trace("Finished");
    }

    @Override
    public void removeAddressStateById(ActionOutput actionOutput) {
        log.trace("Removing handled output state by id {}", actionOutput);
        String sql = "DELETE FROM address_state WHERE address = ? AND output_type = ?";
        jdbcTemplate.update(sql, actionOutput.getAddress(), actionOutput.getOutputType());
        log.trace("Finished");
    }
}
