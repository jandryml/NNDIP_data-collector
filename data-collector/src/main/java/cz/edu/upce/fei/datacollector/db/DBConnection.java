package cz.edu.upce.fei.datacollector.db;

import cz.edu.upce.fei.datacollector.util.Props;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final Logger logger = LogManager.getLogger();

    public static Connection getConnection() {
        Connection con = null;
        logger.debug("Creating new DB connection.");
        try {
            // create the connection now
            con = DriverManager.getConnection(Props.getProperty("DB_URL"),
                    Props.getProperty("DB_USERNAME"),
                    Props.getProperty("DB_PASSWORD"));
        } catch (SQLException e) {
            logger.error("Error during creating DB connection!", e);
        }
        logger.debug("New DB connection created successfully!");
        return con;
    }
}