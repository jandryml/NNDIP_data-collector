package cz.edu.upce.fei.datacollector.repository.impl;

import cz.edu.upce.fei.datacollector.model.SensorData;
import cz.edu.upce.fei.datacollector.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


@Component
@RequiredArgsConstructor
public class DataRepositoryImpl implements DataRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveData(List<SensorData> sensorData) {
        String sql = "INSERT INTO data (sensor_id, data_time, hits, temperature_1, humidity, co2_1, co2_2, temperature_2)"
                + " VALUES(?,?,?,?,?,?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                SensorData user = sensorData.get(i);
                ps.setLong(1, user.getSensorId());
                ps.setTimestamp(2, user.getTimestamp());
                ps.setInt(3, user.getHits());
                ps.setObject(4, user.getTemperature1());
                ps.setObject(5, user.getHumidity());
                ps.setObject(6, user.getCo2_1());
                ps.setObject(7, user.getCo2_2());
                ps.setObject(8, user.getTemperature2());
            }

            @Override
            public int getBatchSize() {
                return sensorData.size();
            }
        });
    }
}
