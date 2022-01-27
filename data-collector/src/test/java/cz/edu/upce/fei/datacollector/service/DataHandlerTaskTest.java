package cz.edu.upce.fei.datacollector.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DataHandlerTaskTest {

    @Autowired
    DataHandlerTask dataHandlerTask;

    @Test
    void addData() {
//        dataHandlerTask.addData("1;;;;;;".getBytes());
        dataHandlerTask.addData("1;0.00;10.00;10;10;10;".getBytes());
        dataHandlerTask.addData("1;0.00;20.00;20;20;20;".getBytes());
        dataHandlerTask.addData("2;30.00;30.00;30;30;30;".getBytes());
        dataHandlerTask.addData("3;40.00;40.00;40;40;40;".getBytes());
        dataHandlerTask.addData("1;10.00;35.00;600;300;24;asdads".getBytes());

        dataHandlerTask.processData();
    }
}
