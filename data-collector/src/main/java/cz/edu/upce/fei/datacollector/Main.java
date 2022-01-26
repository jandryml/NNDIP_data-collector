package cz.edu.upce.fei.datacollector;

import cz.edu.upce.fei.datacollector.db.DBConnection;
import cz.edu.upce.fei.datacollector.tasks.ConnectionHandlerTask;
import cz.edu.upce.fei.datacollector.util.Init;
import cz.edu.upce.fei.datacollector.util.Props;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    private final static int THREAD_COUNT = 1;

    public static void main(String[] args) throws SQLException{
        if(!Init.init()) {
            return;
        }
//        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
//
//        CompletableFuture<Void> myTask = CompletableFuture.runAsync(new ConnectionHandlerTask(), executorService)
//                .exceptionally(t -> {
//                            System.out.println("Chybka");
//                            return null;
//                        }
//                );
//
//        myTask.join();
//
//        System.out.println(myTask.isDone());
//
//        executorService.shutdown();

        logger.info("Connected to db");


        //create connection for a server installed in localhost, with a user "root" with no password
        try (Connection conn = DBConnection.getConnection()) {
            // create a Statement
            try (Statement stmt = conn.createStatement()) {
                //execute query
                try (ResultSet rs = stmt.executeQuery("SELECT * from person")) {
                    //position result to first
                    while (rs.next()) {
                        System.out.print(rs.getString(1) + "; ");
                        System.out.print(rs.getString(2) + "; ");
                        System.out.println(rs.getString(3) + "; ");
                    }
                }
            }
        }
    }
}
