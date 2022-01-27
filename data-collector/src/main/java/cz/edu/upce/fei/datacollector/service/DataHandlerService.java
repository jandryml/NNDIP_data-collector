package cz.edu.upce.fei.datacollector.service;

import cz.edu.upce.fei.datacollector.model.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DataHandlerService {
    private final Logger logger = LogManager.getLogger();

    private final JdbcTemplate jdbcTemplate;
    private final List<String> dataBuffer = new ArrayList<>();

    @Autowired
    public DataHandlerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(cron = "${dataProcessingTask}")
    public void handleData() {
        logger.info("hello");

        // Split up the array of whole names into an array of first/last names

        // Use a Java 8 stream to print out each tuple of the list

        // Uses JdbcTemplate's batchUpdate operation to bulk load data
//
//        jdbcTemplate.query(
//                "SELECT id, name FROM device",
//                (rs, rowNum) -> new Data(rs.getLong("id"), rs.getString("name"))
//        ).forEach(customer -> logger.info(customer.toString()));

    }

    public void addData(byte[] rawMessage) {
        String message = new String(rawMessage);
        logger.debug("Data received: {}", message);
        dataBuffer.add(message);
    }

    public List<Data> processData() {
        logger.info("Start of data processing.");

        Map<Long, List<Data>> sensorDataMap = new HashMap<>();

        normalizeData(sensorDataMap);

        List<Data> resultData = sumUpData(sensorDataMap);

        //TODO change this behaviour
        logger.info("Result of data processing:");
        for (Data data : resultData) {
            logger.info(data);
        }
        logger.info("Data processing ended.");
        return resultData;
    }

    private List<Data> sumUpData(Map<Long, List<Data>> sensorDataMap) {
        logger.debug("Start of data summarising");

        List<Data> resultData = new ArrayList<>();
        sensorDataMap.forEach((key, value) -> {

            // counting average on each field
            OptionalDouble avgTemper1 = value.stream().filter(it -> it.getTemperature1() != null)
                    .mapToDouble(Data::getTemperature1).average();
            OptionalDouble avgHumidity = value.stream().filter(it -> it.getHumidity() != null)
                    .mapToDouble(Data::getHumidity).average();
            OptionalDouble avgCo2_1 = value.stream().filter(it -> it.getCo2_1() != null)
                    .mapToInt(Data::getCo2_1).average();
            OptionalDouble avgCo2_2 = value.stream().filter(it -> it.getCo2_2() != null)
                    .mapToInt(Data::getCo2_2).average();
            OptionalDouble avgTemper2 = value.stream().filter(it -> it.getTemperature2() != null)
                    .mapToInt(Data::getTemperature2).average();

            // creating summarized Data entity for each message
            resultData.add(new Data(Long.MIN_VALUE, key,
                    avgTemper1.isPresent() ? avgTemper1.getAsDouble() : null,
                    avgHumidity.isPresent() ? avgHumidity.getAsDouble() : null,
                    avgCo2_1.isPresent() ? (int) avgCo2_1.getAsDouble() : null,
                    avgCo2_2.isPresent() ? (int) avgCo2_2.getAsDouble() : null,
                    avgTemper2.isPresent() ? (int) avgTemper2.getAsDouble() : null,
                    LocalDateTime.now(),
                    value.size()));
        });
        logger.debug("Data summarising ended.");
        return resultData;
    }

    private void normalizeData(Map<Long, List<Data>> sensorDataMap) {
        logger.debug("Start of data normalising");
        for (String message : dataBuffer) {
            // removing zero byte (end of byte array from c++)
            message = message.replace("\0", "");

            if (validateMessageFormat(message)) continue;

            String[] strings = message.split(";", 7);
            long sensorId = Long.parseLong(strings[0]);

            // separating data by sensor ID
            List<Data> dataList = sensorDataMap.computeIfAbsent(sensorId, k -> new ArrayList<>());
            dataList.add(transferRawMessageToData(strings, sensorId));
        }
        dataBuffer.clear();
        logger.debug("Data normalised.");
    }

    private Data transferRawMessageToData(String[] strings, long sensorId) {
        return new Data(Long.MIN_VALUE, sensorId,
                strings[1].isEmpty() ? null : Double.parseDouble(strings[1]),
                strings[2].isEmpty() ? null : Double.parseDouble(strings[2]),
                strings[3].isEmpty() ? null : Integer.parseInt(strings[3]),
                strings[4].isEmpty() ? null : Integer.parseInt(strings[4]),
                strings[5].isEmpty() ? null : Integer.parseInt(strings[5]),
                // those values are not used in those record
                null, null);
    }

    private boolean validateMessageFormat(String message) {
        if (!message.matches("^\\d+;((-?\\d+\\.\\d{0,2})|);((\\d+\\.\\d{0,2})|);((\\d{1,4})|);((\\d{1,4})|);((-?\\d+)|);$")) {
            logger.warn("Message doesn't matches expected format: {}", message);
            return true;
        }
        return false;
    }
}
