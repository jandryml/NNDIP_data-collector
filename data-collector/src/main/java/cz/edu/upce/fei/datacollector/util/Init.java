package cz.edu.upce.fei.datacollector.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Init {
    private static final Logger logger = LogManager.getLogger();

    public static boolean init() {
        try {
            // load the DB Driver Class
            logger.debug("Start of init procedure.");
            Class.forName(Props.getProperty("DB_DRIVER_CLASS"));
            logger.debug("Init procedure completed successfully!");
            return true;
        } catch (Exception e) {
            logger.error("Error during init procedure!", e);
            return false;
        }
    }
}
