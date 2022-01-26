package cz.edu.upce.fei.datacollector.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.util.Properties;

public class Props {
    private static final Logger logger = LogManager.getLogger();
    private static final Properties defaultProps = new Properties();

    static {
        logger.debug("Start of properties loading.");
        try {
            FileInputStream in = new FileInputStream("db.properties");
            defaultProps.load(in);
            in.close();
        } catch (Exception e) {
            logger.error("Error during loading properties!", e);
        }
        logger.debug("Loading of properties finished successfully.");
    }

    public static String getProperty(String key) {
        return defaultProps.getProperty(key);
    }
}
