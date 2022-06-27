package cz.edu.upce.fei.datacollector.service.impl;

import cz.edu.upce.fei.datacollector.model.SensorData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class DataReactionServiceImplTest {

    @Autowired
    private DataReactionServiceImpl reactionService;

    @Test
    void testReaction_NoData() {
        List<SensorData> result = Collections.emptyList();
        reactionService.handleData(result);
    }

    @Test
    void testReaction() {
        List<SensorData> result = Arrays.asList(
                buildSensorData(1, Timestamp.valueOf(LocalDateTime.now()), 15, 10.0, 10.0, 10),
                buildSensorData(2, Timestamp.valueOf(LocalDateTime.now()), 15, 20.0, 20.0, 20),
                buildSensorData(3, Timestamp.valueOf(LocalDateTime.now()), 25, 25.0, 25.0, 25)
        );

        reactionService.handleData(result);
    }

    @Test
    void testReaction2() {
        List<SensorData> result = Collections.singletonList(
                buildSensorData(1, Timestamp.valueOf(LocalDateTime.now()), 15, 10.0, 10.0, 10)
        );

        reactionService.handleData(result);
    }

    private SensorData buildSensorData(int sensorId, Timestamp timestamp, int hits, double temperature, double humidity, int co2) {
        return SensorData.builder()
                .sensorId(sensorId)
                .timestamp(timestamp)
                .hits(hits)
                .temperature(temperature)
                .humidity(humidity)
                .co2(co2)
                .build();
    }
}