/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite;

import it.cnr.ilc.lexolite.constant.Label;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrea
 */
public class LexOliteProperty {

    private static Properties properties = new Properties();

    public static Properties getProperties() {
        return properties;
    }

    public static void setProperties(Properties properties) {
        LexOliteProperty.properties = properties;
    }

    public static void load() {
        try ( InputStream input = new FileInputStream(System.getProperty("user.home") + Label.LEXO_FOLDER + Label.LEXO_PROPERTIES_FILE_NAME)) {
            properties.load(input);
            input.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public static void save() throws FileNotFoundException, IOException {
        try ( OutputStream output = new FileOutputStream(System.getProperty("user.home") + Label.LEXO_FOLDER + Label.LEXO_PROPERTIES_FILE_NAME)) {
            properties.store(output, null);
            output.close();
        }
    }

}
