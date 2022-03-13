package cz.edu.upce.fei.datacollector.service.impl;

import cz.edu.upce.fei.datacollector.model.SensorData;
import cz.edu.upce.fei.datacollector.service.DataProcessService;
import cz.edu.upce.fei.datacollector.service.DataReactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

@SpringBootTest
class DataProcessServiceImplTest {

    @Autowired
    private DataProcessService dataProcessServiceImpl;
    @Autowired
    private DataReactionService reactionService;

    @Test
    void noData() {
        List<SensorData> result = dataProcessServiceImpl.processData();
        Assertions.assertEquals(0, result.size());
    }

    @Test
    void addData() {
        dataProcessServiceImpl.addData("1;-10.00;10.00;10;10;10;".getBytes());
        dataProcessServiceImpl.addData("1;10.00;20.00;20;20;20;".getBytes());
        dataProcessServiceImpl.addData("2;30.00;30.00;30;30;30;".getBytes());
        dataProcessServiceImpl.addData("3;40.00;40.00;40;40;40;".getBytes());
        dataProcessServiceImpl.addData("1;10.00;35.00;600;300;24;asdads".getBytes());

        List<SensorData> result = dataProcessServiceImpl.processData();
        assertDataRecord(result, 1, 0.0, 15.0, 15, 15, 15, 2);
        assertDataRecord(result, 2, 30.0, 30.0, 30, 30, 30, 1);
        assertDataRecord(result, 3, 40.0, 40.0, 40, 40, 40, 1);
        reactionService.handleData(result);
    }

    @Test
    void processingLoadTest() {
        for (int i = 0; i < 1000; i++) {
            dataProcessServiceImpl.addData("1;0.00;0.00;0;0;0;".getBytes());
            dataProcessServiceImpl.addData("1;10.00;10.00;10;10;10;".getBytes());
            dataProcessServiceImpl.addData("2;20.00;20.00;20;20;20;".getBytes());
            dataProcessServiceImpl.addData("3;30.00;30.00;30;30;30;".getBytes());
            dataProcessServiceImpl.addData("1;10.00;35.00;600;300;24;asdads".getBytes());
        }

        List<SensorData> result = dataProcessServiceImpl.processData();
        assertDataRecord(result, 1, 5.0, 5.0, 5, 5, 5, 2000);
        assertDataRecord(result, 2, 20.0, 20.0, 20, 20, 20, 1000);
        assertDataRecord(result, 3, 30.0, 30.0, 30, 30, 30, 1000);
    }

    @Test
    void noValueProcessTest() {
        dataProcessServiceImpl.addData("1;;;;;;".getBytes());
        dataProcessServiceImpl.addData("1;;;;;;".getBytes());
        dataProcessServiceImpl.addData("2;;;;;;".getBytes());

        List<SensorData> result = dataProcessServiceImpl.processData();
        assertDataRecord(result, 1, null, null, null, null, null, 2);
        assertDataRecord(result, 2, null, null, null, null, null, 1);
    }

    @Test
    void oneEmptyRecordProcessTest() {
        dataProcessServiceImpl.addData("1;;;;;;".getBytes());
        dataProcessServiceImpl.addData("1;10.00;10.00;10;10;10;".getBytes());
        dataProcessServiceImpl.addData("1;20.00;20.00;20;20;20;".getBytes());

        List<SensorData> result = dataProcessServiceImpl.processData();
        assertDataRecord(result, 1, 15.0, 15.0, 15, 15, 15, 3);
    }

    @Test
    void realDataProcessTest() {
        dataProcessServiceImpl.addData("1;;;;;;".getBytes());
        dataProcessServiceImpl.addData("1;;;10;10;10;".getBytes());
        dataProcessServiceImpl.addData("1;;;20;20;20;".getBytes());
        dataProcessServiceImpl.addData("2;;;;;;".getBytes());
        dataProcessServiceImpl.addData("2;10.00;10.00;;;;".getBytes());
        dataProcessServiceImpl.addData("2;20.00;20.00;;;;".getBytes());

        List<SensorData> result = dataProcessServiceImpl.processData();
        assertDataRecord(result, 1, null, null, 15, 15, 15, 3);
        assertDataRecord(result, 2, 15.0, 15.0, null, null, null, 3);
        reactionService.handleData(result);
    }

    private void assertDataRecord(List<SensorData> data, long exSensorId, Double exTemper1, Double exHumidity, Integer exCo2_1, Integer exCo2_2, Integer exTemper2, Integer hits) {
        Assertions.assertEquals(
                data.stream().filter(it -> it.getSensorId() == exSensorId && Objects.equals(it.getTemperature1(), exTemper1)
                        && Objects.equals(it.getHumidity(), exHumidity) && Objects.equals(it.getCo2_1(), exCo2_1)
                        && Objects.equals(it.getCo2_2(), exCo2_2) && Objects.equals(it.getTemperature2(), exTemper2)
                        && hits.equals(it.getHits())
                ).count(), 1);
    }
}
