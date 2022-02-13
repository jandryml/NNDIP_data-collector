package cz.edu.upce.fei.datacollector.service;

import cz.edu.upce.fei.datacollector.model.SensorData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class DataHandlerService {

    private final JdbcTemplate jdbcTemplate;
    private final Collection<String> dataBuffer = Collections.synchronizedCollection(new ArrayList<>());

    @Autowired
    public DataHandlerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(cron = "${dataProcessingTask}")
    public void handleData() {
        List<SensorData> processedData = processData();

        processedData.stream().mapToLong(SensorData::getSensorId).forEach(value -> {
                    int res = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM sensor WHERE id = " + value
                            , Integer.class);
                    if (res == 0) {
                        // TODO change device id to generic one
                        jdbcTemplate.execute("INSERT INTO sensor VALUES (" + value + ", 'UNKNOWN', 1)");
                    }
                }
        );


        String sql = "INSERT INTO data (sensor_id, data_time, hits, temperature_1, humidity, co2_1, co2_2, temperature_2)"
                + " VALUES(?,?,?,?,?,?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                SensorData user = processedData.get(i);
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
                return processedData.size();
            }
        });
    }

    public void addData(byte[] rawMessage) {
        String message = new String(rawMessage);
        log.debug("Data received: {}", message);
        dataBuffer.add(message);
    }

    public List<SensorData> processData() {
        log.info("Start of data processing.");

        Map<Long, List<SensorData>> sensorDataMap = new HashMap<>();

        normalizeData(sensorDataMap);

        List<SensorData> resultData = sumUpData(sensorDataMap);

        //TODO change this behaviour
        log.info("Result of data processing:");
        for (SensorData sensorData : resultData) {
            log.info("{}", sensorData);
        }
        log.info("Data processing ended.");
        return resultData;
    }

    private List<SensorData> sumUpData(Map<Long, List<SensorData>> sensorDataMap) {
        log.debug("Start of data summarising");

        List<SensorData> resultData = new ArrayList<>();
        sensorDataMap.forEach((key, value) -> {

            // counting average on each field
            OptionalDouble avgTemper1 = value.stream().filter(it -> it.getTemperature1() != null)
                    .mapToDouble(SensorData::getTemperature1).average();
            OptionalDouble avgHumidity = value.stream().filter(it -> it.getHumidity() != null)
                    .mapToDouble(SensorData::getHumidity).average();
            OptionalDouble avgCo2_1 = value.stream().filter(it -> it.getCo2_1() != null)
                    .mapToInt(SensorData::getCo2_1).average();
            OptionalDouble avgCo2_2 = value.stream().filter(it -> it.getCo2_2() != null)
                    .mapToInt(SensorData::getCo2_2).average();
            OptionalDouble avgTemper2 = value.stream().filter(it -> it.getTemperature2() != null)
                    .mapToInt(SensorData::getTemperature2).average();

            // creating summarized Data entity for each message
            resultData.add(new SensorData(key, Timestamp.valueOf(LocalDateTime.now()), value.size(),
                    avgTemper1.isPresent() ? avgTemper1.getAsDouble() : null,
                    avgHumidity.isPresent() ? avgHumidity.getAsDouble() : null,
                    avgCo2_1.isPresent() ? (int) avgCo2_1.getAsDouble() : null,
                    avgCo2_2.isPresent() ? (int) avgCo2_2.getAsDouble() : null,
                    avgTemper2.isPresent() ? (int) avgTemper2.getAsDouble() : null
            ));
        });
        log.debug("Data summarising ended.");
        return resultData;
    }

    private void normalizeData(Map<Long, List<SensorData>> sensorDataMap) {
        log.debug("Start of data normalising");
        for (String message : dataBuffer) {
            // removing zero byte (end of byte array from c++)
            message = message.replace("\0", "");

            if (validateMessageFormat(message)) continue;

            String[] strings = message.split(";", 7);
            long sensorId = Long.parseLong(strings[0]);

            // separating data by sensor ID
            List<SensorData> sensorDataList = sensorDataMap.computeIfAbsent(sensorId, k -> new ArrayList<>());
            sensorDataList.add(transferRawMessageToData(strings, sensorId));
        }
        dataBuffer.clear();
        log.debug("Data normalised.");
    }

    private SensorData transferRawMessageToData(String[] strings, long sensorId) {
        // hits and timestamp is unused
        return new SensorData(sensorId, null, 0,
                strings[1].isEmpty() ? null : Double.parseDouble(strings[1]),
                strings[2].isEmpty() ? null : Double.parseDouble(strings[2]),
                strings[3].isEmpty() ? null : Integer.parseInt(strings[3]),
                strings[4].isEmpty() ? null : Integer.parseInt(strings[4]),
                strings[5].isEmpty() ? null : Integer.parseInt(strings[5]));
    }

    private boolean validateMessageFormat(String message) {
        if (!message.matches("^\\d+;((-?\\d+\\.\\d{0,2})|);((\\d+\\.\\d{0,2})|);((\\d{1,4})|);((\\d{1,4})|);((-?\\d+)|);$")) {
            log.warn("Message doesn't matches expected format: {}", message);
            return true;
        }
        return false;
    }
}
