package cz.edu.upce.fei.datacollector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.OutputStream;
import java.io.PrintStream;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class Main implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        turnOffOutStream();
        log.info("Starting the application");
    }

    private void turnOffOutStream() {
        PrintStream dummyStream = new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        });
        System.setOut(dummyStream);
    }
}