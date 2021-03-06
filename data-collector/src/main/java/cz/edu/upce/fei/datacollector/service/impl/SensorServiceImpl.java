package cz.edu.upce.fei.datacollector.service.impl;

import cz.edu.upce.fei.datacollector.model.Sensor;
import cz.edu.upce.fei.datacollector.model.SensorData;
import cz.edu.upce.fei.datacollector.repository.SensorRepository;
import cz.edu.upce.fei.datacollector.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {

    private final SensorRepository sensorRepository;

    @Override
    public void validateSensorsExistOrCreate(List<SensorData> dataList) {
        dataList.stream().mapToLong(SensorData::getSensorId).forEach(sensorId -> {
                    if (!sensorRepository.sensorExists(sensorId)) {
                        sensorRepository.createSensor(new Sensor(sensorId, "UNKNOWN", null));
                    }
                }
        );
    }
}
