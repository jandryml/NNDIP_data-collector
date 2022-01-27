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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataHandlerTask {
    private final Logger logger = LogManager.getLogger();

    private final JdbcTemplate jdbcTemplate;
    private final List<String> dataBuffer = new ArrayList<>();

    @Autowired
    public DataHandlerTask(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void hello() {
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

    @Scheduled(cron = "${dataProcessingTask}")
    public void processData() {
        Map<Long, List<Data>> sensorDataMap = new HashMap<>();

        normalizeData(sensorDataMap);

        List<Data> resultData = sumUpData(sensorDataMap);


        logger.info("Result of data processing:");
        for (Data data : resultData) {
            logger.info(data);
        }
        logger.info("Data processing ended.");
    }

    private List<Data> sumUpData(Map<Long, List<Data>> sensorDataMap) {
        List<Data> resultData = new ArrayList<>();
        sensorDataMap.forEach((key, value) -> {
            float temperature1 = 0.0f, humidity = 0.0f;
            int co2_1 = 0, co2_2 = 0, temperature2 = 0, hits = 0;

            for (Data data : value) {
                temperature1 += data.getTemperature1();
                humidity += data.getHumidity();
                co2_1 += data.getCo2_1();
                co2_2 += data.getCo2_2();
                temperature2 += data.getTemperature2();
                hits++;
            }
            // TODO try to rewrite this magic schmuck
            resultData.add(new Data(Long.MIN_VALUE, key,
                    (float) (Math.round((temperature1 / hits) * 100) / 100),
                    (float) (Math.round((humidity / hits) * 100) / 100),
                    co2_1 / hits,
                    co2_2 / hits,
                    temperature2 / hits,
                    LocalDateTime.now(),
                    hits));
        });
        return resultData;
    }

    private void normalizeData(Map<Long, List<Data>> sensorDataMap) {
        logger.debug("Start of data normalising");
        for (String message : dataBuffer) {
            // removing zero byte (end of byte array from c++)
            message = message.replace("\0", "");

            if (validateMessageFormat(message)) continue;

            // that is the place where magic happen ;( ... handling empty values
            // TODO try to rewrite this magic schmuck
            String[] strings = message.replace(";", " ;").split(";");
            long sensorId = Long.parseLong(strings[0].trim());

            List<Data> dataList = sensorDataMap.computeIfAbsent(sensorId, k -> new ArrayList<>());
            dataList.add(transferMessageToData(strings, sensorId));
        }
        logger.debug("Data normalised.");
    }

    private boolean validateMessageFormat(String message) {
        if (!message.matches("^\\d+;((-?\\d+\\.\\d{0,2})|);((\\d+\\.\\d{0,2})|);((\\d{1,4})|);((\\d{1,4})|);((-?\\d+)|);$")) {
            logger.warn("Message doesn't matches expected format: {}", message);
            return true;
        }
        return false;
    }

    private Data transferMessageToData(String[] strings, long sensorId) {
        return new Data(Long.MIN_VALUE, sensorId,
                strings[1].equals(" ") ? 0 : Float.parseFloat(strings[1].trim()),
                strings[2].equals(" ") ? 0 : Float.parseFloat(strings[2].trim()),
                strings[3].equals(" ") ? 0 : Integer.parseInt(strings[3].trim()),
                strings[4].equals(" ") ? 0 : Integer.parseInt(strings[4].trim()),
                strings[5].equals(" ") ? 0 : Integer.parseInt(strings[5].trim()),
                null, 0);
    }
}
