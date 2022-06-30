package cz.edu.upce.fei.datacollector.repository.impl;

import cz.edu.upce.fei.datacollector.model.SensorData;
import cz.edu.upce.fei.datacollector.repository.DataRepository;
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
public class DataRepositoryImpl implements DataRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveData(List<SensorData> sensorDataList) {
        log.trace("Inserting new sensor data");
        if (log.isTraceEnabled()) {
            sensorDataList.forEach(sensorData -> log.trace(String.valueOf(sensorData)));
        }

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
        log.trace("Finished");
    }

    @Override
    public List<SensorData> getLatestDataNoOlderThan(int minutes) {
        log.trace("Getting sensor data not older than {}", minutes);

        String query = "SELECT d.id, d.hits, d.sensor_id, d.data_timestamp, d.co2, d.humidity, d.temperature\n" +
                "FROM data d\n" +
                "INNER JOIN (\n" +
                "  SELECT sensor_id, max(data_timestamp) AS latest_date\n" +
                "  FROM data\n" +
                "  WHERE data_timestamp > DATE_SUB(NOW(),INTERVAL ? MINUTE)\n" +
                "  GROUP BY sensor_id\n" +
                "  ) gd ON gd.sensor_id = d.sensor_id AND gd.latest_date = d.data_timestamp;";

        List<SensorData> resultList = new ArrayList<>();

        jdbcTemplate.execute(query, (PreparedStatementCallback<SensorData>) ps -> {
            ps.setInt(1, minutes);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultList.add(SensorData.builder()
                        .sensorId(rs.getLong("sensor_id"))
                        .timestamp(rs.getTimestamp("data_timestamp"))
                        .hits(rs.getInt("hits"))
                        .temperature(rs.getDouble("temperature"))
                        .humidity(rs.getDouble("humidity"))
                        .co2(rs.getInt("co2"))
                        .build());
            }
            return null;
        });

        if (log.isTraceEnabled()) {
            resultList.forEach(sensorData -> log.trace(String.valueOf(sensorData)));
        }

        return resultList;
    }
}
