package cz.edu.upce.fei.datacollector.repository.impl;

import cz.edu.upce.fei.datacollector.model.Sensor;
import cz.edu.upce.fei.datacollector.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SensorRepositoryImpl implements SensorRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean sensorExists(long id) {
        Long res = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sensor WHERE id = " + id
                , Long.class);
        return res != null && res > 0;
    }

    @Override
    public void createSensor(Sensor sensor) {
        String sql = "INSERT INTO sensor VALUES (?,?,?)";
        jdbcTemplate.execute(sql, (PreparedStatementCallback<Sensor>) ps -> {
            ps.setLong(1, sensor.getId());
            ps.setString(2, sensor.getName());

            if (sensor.getDeviceId() != null) {
                ps.setLong(3, sensor.getDeviceId());
            } else {
                ps.setNull(3, 4);
            }
            ps.execute();
            return sensor;
        });

    }
}
