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
    public void saveData(List<SensorData> sensorDataList) {
        String sql = "INSERT INTO data (sensor_id, data_timestamp, hits, temperature, humidity, co2)"
                + " VALUES(?,?,?,?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                SensorData sensorData = sensorDataList.get(i);
                ps.setLong(1, sensorData.getSensorId());
                ps.setTimestamp(2, sensorData.getTimestamp());
                ps.setInt(3, sensorData.getHits());
                ps.setObject(4, sensorData.getTemperature());
                ps.setObject(5, sensorData.getHumidity());
                ps.setObject(6, sensorData.getCo2());
            }

            @Override
            public int getBatchSize() {
                return sensorDataList.size();
            }
        });
    }
}
