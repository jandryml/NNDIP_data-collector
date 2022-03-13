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
                new SensorData(1, Timestamp.valueOf(LocalDateTime.now()), 15, 10.0, 10.0, 10, 10, 10),
                new SensorData(2, Timestamp.valueOf(LocalDateTime.now()), 15, 20.0, 20.0, 20, 20, 20),
                new SensorData(3, Timestamp.valueOf(LocalDateTime.now()), 25, 25.0, 25.0, 25, 25, 25)
        );

        reactionService.handleData(result);
    }

    @Test
    void testReaction2() {
        List<SensorData> result = Collections.singletonList(
                new SensorData(1, Timestamp.valueOf(LocalDateTime.now()), 15, 10.0, 10.0, 10, 10, 10)
        );

        reactionService.handleData(result);
    }
}