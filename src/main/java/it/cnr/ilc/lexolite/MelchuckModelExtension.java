/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite;

import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.controller.LexicalFunctionComparator;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.faces.model.SelectItem;

/**
 *
 * @author andrea
 */
public class MelchuckModelExtension {

    private static final ArrayList<SelectItem> syntagmaticLexicalFunctions = new ArrayList();
    private static final ArrayList<SelectItem> paradigmaticLexicalFunctions = new ArrayList();
    private static final Map<String, String> syntagmaticRenderingTable = new HashMap<>();
    private static final Map<String, String> paradigmaticRenderingTable = new HashMap<>();

    public static ArrayList<SelectItem> getSyntagmaticLexicalFunctions() {
        return syntagmaticLexicalFunctions;
    }

    public static ArrayList<SelectItem> getParadigmaticLexicalFunctions() {
        return paradigmaticLexicalFunctions;
    }

    public static Map<String, String> getSyntagmaticRenderingTable() {
        return syntagmaticRenderingTable;
    }

    public static Map<String, String> getParadigmaticRenderingTable() {
        return paradigmaticRenderingTable;
    }

    private static Properties properties = new Properties();

    public static Properties getProperties() {
        return properties;
    }

    public static void setProperties(Properties properties) {
        MelchuckModelExtension.properties = properties;
    }

    public static void load() {
        try (InputStream input = new FileInputStream(System.getProperty("user.home") + Label.LEXO_FOLDER + Label.MODELS_FOLDER + Label.LEXICAL_FUNCTIONS_FILE_NAME)) {
            properties.load(input);
            input.close();
            setUp();
        } catch (IOException ex) {
            // Melchuck model extension is not present
        }
    }

    private static void setUp() {
        Set<Object> keys = properties.keySet();
        keys.forEach((k) -> {
            String[] key = ((String) k).split("\\.");
            if (key[0].equals("syntagmatic")) {
                syntagmaticLexicalFunctions.add(new SelectItem(key[1], properties.getProperty((String) k), null, false, false));
                syntagmaticRenderingTable.put(key[1], properties.getProperty((String) k));
            } else {
                paradigmaticLexicalFunctions.add(new SelectItem(key[1], properties.getProperty((String) k), null, false, false));
                paradigmaticRenderingTable.put(key[1], properties.getProperty((String) k));
            }
        });
        Collections.sort(syntagmaticLexicalFunctions, new LexicalFunctionComparator());
        Collections.sort(paradigmaticLexicalFunctions, new LexicalFunctionComparator());
    }

}
