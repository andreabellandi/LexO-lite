/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.util;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author andrea
 */
public class LexiconUtil {

    // constructor of uri individuals
    public static String getIRI(String... params) {
        StringBuilder iri = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            iri.append(sanitize(params[i]));
            if (i < (params.length - 1)) {
                iri.append("_");
            }
        }
        return iri.toString();
    }

    public static String sanitize(String wr) {
        String instance = wr.trim();
//        instance = instance.replaceAll("#", "_SHARP_");
//        instance = instance.replaceAll("\\*", "_STAR_");
        instance = instance.replaceAll("\\(", "OB_");
        instance = instance.replaceAll("\\)", "_CB");
        instance = instance.replaceAll("\\[", "OSB_");
        instance = instance.replaceAll("\\]", "_CSB");
//        instance = instance.replaceAll("\\{", "OSB_");
//        instance = instance.replaceAll("\\}", "_CSB");
//        instance = instance.replaceAll("\\?", "_QUEST_");
        instance = instance.replaceAll("#", "");
        instance = instance.replaceAll("\\*", "");
//        instance = instance.replaceAll("\\(", "");
//        instance = instance.replaceAll("\\)", "");
//        instance = instance.replaceAll("\\[", "");
//        instance = instance.replaceAll("\\]", "");
        instance = instance.replaceAll("\\{", "");
        instance = instance.replaceAll("\\}", "");
        instance = instance.replaceAll("\\?", "");
        instance = instance.replaceAll("\\.", "");
        instance = instance.replaceAll("\\,", "");
        instance = instance.replaceAll("\\:", "");
        instance = instance.replaceAll("\\;", "");
        instance = instance.replaceAll("\\!", "");
        instance = instance.replaceAll("\\'", "_APOS_");
        instance = instance.replaceAll("\\‛", "_APOS2_");
//        instance = instance.replaceAll("\\'", "_");
        instance = instance.replaceAll("\\’", "");
        instance = instance.replaceAll("\\‘", "");
        instance = instance.replaceAll("\\s+", "_");
        instance = instance.replaceAll("/", "_");
        instance = instance.replaceAll("<", "OSB_");
        instance = instance.replaceAll(">", "_CSB");
        instance = instance.replaceAll(" ", " ");
        instance = instance.replaceAll(" +(\\d)", "$1");
        return instance;
    }

    public static Color getRandomColor() {
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        return new Color(r, g, b);
    }

}
