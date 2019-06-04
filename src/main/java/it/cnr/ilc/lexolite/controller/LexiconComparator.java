/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

/**
 *
 * @author andrea
 */
import java.text.Normalizer;
import java.util.Comparator;
import java.util.Map;

public class LexiconComparator implements Comparator<Map<String, String>> {

    private final String orderingField;
    
    public LexiconComparator(String orderingField) {
        this.orderingField = orderingField;
    }

    @Override
    public int compare(Map<String, String> o1, Map<String, String> o2) {
        String string1 = Normalizer.normalize(o1.get(orderingField).toLowerCase(), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}+]", "");
        String string2 = Normalizer.normalize(o2.get(orderingField).toLowerCase(), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}+]", "");
        return string1.compareTo(string2);
    }
   
}
