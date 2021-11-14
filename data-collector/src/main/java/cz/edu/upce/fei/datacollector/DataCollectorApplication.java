package cz.edu.upce.fei.datacollector;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@SpringBootApplication
public class DataCollectorApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(DataCollectorApplication.class, args);
//    }

    public static void main(String[] args) {
        Scanner scanner = null;
        try {
            File file = new File("/dev/ttyUSB0");

            scanner = new Scanner(file);

            while (true) {
                if (scanner.hasNextLine()) {
                    System.out.println("Has next");
                    System.out.println(scanner.nextLine());
                } else {
                    System.out.println("Don't have next");
                }
                Thread.sleep(1000);
            }
        } catch (FileNotFoundException | InterruptedException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}
