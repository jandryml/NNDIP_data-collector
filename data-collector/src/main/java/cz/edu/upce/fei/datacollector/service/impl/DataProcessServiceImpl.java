package cz.edu.upce.fei.datacollector.service.impl;

import cz.edu.upce.fei.datacollector.model.SensorData;
import cz.edu.upce.fei.datacollector.repository.DataRepository;
import cz.edu.upce.fei.datacollector.service.DataProcessService;
import cz.edu.upce.fei.datacollector.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataProcessServiceImpl implements DataProcessService {

    private final DataRepository dataRepository;
    private final SensorService sensorService;

    private final List<String> dataBuffer = Collections.synchronizedList(new ArrayList<>());

    @Override
    @Scheduled(cron = "${dataProcessingTask}")
    public void handleData() {
        List<SensorData> processedData = processData();

        // create new sensor if not existing;
        sensorService.validateSensorsExistOrCreate(processedData);

        dataRepository.saveData(processedData);
    }

    @Override
    public void addData(byte[] rawMessage) {
        String message = new String(rawMessage);
        log.debug("Data received: {}", message);
        dataBuffer.add(message);
    }

    @Override
    public List<SensorData> processData() {
        log.info("Start of data processing.");

        Map<Long, List<SensorData>> sensorDataMap = new HashMap<>();

        normalizeData(sensorDataMap);

        List<SensorData> resultData = sumUpData(sensorDataMap);

        log.debug("Result of data processing:");
        for (SensorData sensorData : resultData) {
            log.debug("{}", sensorData);
        }
        log.debug("Data processing ended.");
        return resultData;
    }

    private void normalizeData(Map<Long, List<SensorData>> sensorDataMap) {
        log.trace("Start of data normalising");

        synchronized (dataBuffer) {
            dataBuffer.forEach(it -> {
                String [] messages = it.split("\r\n");
                Arrays.stream(messages).forEach(message -> {
                    if (validateMessageFormat(message)) return;

                    String[] strings = message.split(";", 7);
                    long sensorId = Long.parseLong(strings[0]);

                    // separating data by sensor ID
                    List<SensorData> rawSensorDataList = sensorDataMap.computeIfAbsent(sensorId, k -> new ArrayList<>());
                    rawSensorDataList.add(transferRawMessageToData(strings, sensorId));
                });
            });
            dataBuffer.clear();
        }

        log.trace("Data normalised.");
    }

    private SensorData transferRawMessageToData(String[] strings, long sensorId) {
        // hits and timestamp is unused
        return SensorData.builder()
                .sensorId(sensorId)
                .timestamp(null)
                .hits(0)
                .temperature(strings[1].isEmpty() ? null : Double.parseDouble(strings[1]))
                .humidity(strings[2].isEmpty() ? null : Double.parseDouble(strings[2]))
                .co2(strings[3].isEmpty() ? null : Integer.parseInt(strings[3]))
        .build();
    }

    private List<SensorData> sumUpData(Map<Long, List<SensorData>> sensorDataMap) {
        log.trace("Start of data summarising");

        List<SensorData> resultData = new ArrayList<>();
        sensorDataMap.forEach((key, value) -> {

            // counting average on each field
            OptionalDouble avgTemper = value.stream().filter(it -> it.getTemperature() != null)
                    .mapToDouble(SensorData::getTemperature).average();
            OptionalDouble avgHumidity = value.stream().filter(it -> it.getHumidity() != null)
                    .mapToDouble(SensorData::getHumidity).average();
            OptionalDouble avgCo2 = value.stream().filter(it -> it.getCo2() != null)
                    .mapToInt(SensorData::getCo2).average();

            // creating summarized Data entity for each message
            resultData.add(SensorData.builder()
                    .sensorId(key)
                    .timestamp(Timestamp.valueOf(LocalDateTime.now()))
                    .hits(value.size())
                    .temperature(avgTemper.isPresent() ? formatNumber(avgTemper.getAsDouble()).doubleValue() : null)
                    .humidity(avgHumidity.isPresent() ? formatNumber(avgHumidity.getAsDouble()).doubleValue() : null)
                    .co2(avgCo2.isPresent() ? formatNumber(avgCo2.getAsDouble()).intValue() : null)
                    .build());
        });
        log.trace("Data summarising ended.");
        return resultData;
    }

    private BigDecimal formatNumber(double number) {
        return BigDecimal.valueOf(number).setScale(2, RoundingMode.HALF_UP);
    }

    private boolean validateMessageFormat(String message) {
        // (int)node_id;(float)(AM2120 - teplota);(float)(AM2120 - vlhost);(int)(MH-Z19 - UART - CO2);(int)(MH-Z19 - PWM - CO2);(int)(MH-Z19 - teplota);
        if (!message.matches("^\\d+;((-?\\d+\\.\\d{0,2})|);((\\d+\\.\\d{0,2})|);((\\d{1,4})|);((\\d{1,4})|);((-?\\d+)|);$")) {
            log.warn("Message doesn't matches expected format: {}", message);
            return true;
        }
        return false;
    }
}
