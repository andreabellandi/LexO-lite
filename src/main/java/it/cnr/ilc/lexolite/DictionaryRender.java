/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite;

import it.cnr.ilc.lexolite.constant.Label;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andrea
 */
public class DictionaryRender {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryRender.class);

    private static final Map<String, DictFeature> dictionaryFetauresTable = new HashMap<>();

    public static Map<String, DictFeature> getDictionaryFetauresTable() {
        return dictionaryFetauresTable;
    }

    private static Properties properties = new Properties();

    public static Properties getProperties() {
        return properties;
    }

    public static void setProperties(Properties properties) {
        DictionaryRender.properties = properties;
    }

    public static void load() {
        String filename = System.getProperty("user.home") + Label.LEXO_FOLDER + Label.DICTIONARY_FEATURES_FOLDER + Label.DICTIONARY_FEATURES_FILE_NAME;
        try (InputStream input = new FileInputStream(filename)) {
            properties.load(input);
            input.close();
            setUp();
        } catch (IOException ex) {
            // Dictionary features file is not present
            logger.error("Dictionary file %s not found ", filename);
        }
    }

    private static void setUp() {
        Set<Object> keys = properties.keySet();
        keys.forEach((k) -> {
            String p[] = properties.getProperty((String) k).split("-");
            dictionaryFetauresTable.put((String) k, new DictFeature(Integer.parseInt(p[0]), p[1]));
        });
    }

    public static class DictFeature {

        private int position;
        private String label;

        public DictFeature(int position, String label) {
            this.position = position;
            this.label = label;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

    }

}
