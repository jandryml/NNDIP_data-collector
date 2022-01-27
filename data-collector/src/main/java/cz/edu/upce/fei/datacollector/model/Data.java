package cz.edu.upce.fei.datacollector.model;

import java.time.LocalDateTime;

public class Data {
    private long id;
    private long sensorId;
    private Float temperature1;
    private Float humidity;
    private Integer co2_1;
    private Integer co2_2;
    private Integer temperature2;
    private LocalDateTime data_time;
    private Integer hits;

    public Data(long id, long sensorId, Float temperature1, Float humidity, Integer co2_1, Integer co2_2, Integer temperature2, LocalDateTime data_time, Integer hits) {
        this.id = id;
        this.sensorId = sensorId;
        this.temperature1 = temperature1;
        this.humidity = humidity;
        this.co2_1 = co2_1;
        this.co2_2 = co2_2;
        this.temperature2 = temperature2;
        this.data_time = data_time;
        this.hits = hits;
    }

    public Float getTemperature1() {
        return temperature1;
    }

    public Float getHumidity() {
        return humidity;
    }

    public Integer getCo2_1() {
        return co2_1;
    }

    public Integer getCo2_2() {
        return co2_2;
    }

    public Integer getTemperature2() {
        return temperature2;
    }

    public Integer getHits() {
        return hits;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", sensorId=" + sensorId +
                ", temperature1=" + temperature1 +
                ", humidity=" + humidity +
                ", co2_1=" + co2_1 +
                ", co2_2=" + co2_2 +
                ", temperature2=" + temperature2 +
                ", data_time=" + data_time +
                ", hits=" + hits +
                '}';
    }
}
