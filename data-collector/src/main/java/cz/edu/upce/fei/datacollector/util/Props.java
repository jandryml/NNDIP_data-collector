package cz.edu.upce.fei.datacollector.util;

import java.io.FileInputStream;
import java.util.Properties;

public class Props {
    private static final Properties defaultProps = new Properties();

    static {
        try {
            FileInputStream in = new FileInputStream("db.properties");
            defaultProps.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return defaultProps.getProperty(key);
    }
}
