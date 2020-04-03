package it.cnr.ilc.lexolite;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Properties;

/**
 *
 * @author oakgen
 */
public class LexOliteProperties {

    private static final Properties PROPERTIES = new Properties();

    static {
        load();
    }

    public static final void load() {
        try (InputStream input = LexOliteProperties.class.getResourceAsStream("/lexolite.properties")) {
            PROPERTIES.load(input);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static Number getPropertyAsNumber(String key) {
        try {
            String value = PROPERTIES.getProperty(key);
            return value == null ? null : NumberFormat.getInstance().parse(value);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Number getPropertyAsNumber(String key, String defaultValue) {
        try {
            String value = PROPERTIES.getProperty(key, defaultValue);
            return NumberFormat.getInstance().parse(value);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void setProperty(String key, String value) {
        PROPERTIES.setProperty(key, value);
    }
}
