package cz.edu.upce.fei.datacollector.util;

public class Init {

    public static boolean init() {
        try {
            // load the DB Driver Class
            Class.forName(Props.getProperty("DB_DRIVER_CLASS"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
