package cz.edu.upce.fei.datacollector.db;

import cz.edu.upce.fei.datacollector.util.Props;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;
        try {
            // create the connection now
            con = DriverManager.getConnection(Props.getProperty("DB_URL"),
                    Props.getProperty("DB_USERNAME"),
                    Props.getProperty("DB_PASSWORD"));
        } catch (SQLException e) {
            // TODO handle errors
            e.printStackTrace();
        }
        return con;
    }
}