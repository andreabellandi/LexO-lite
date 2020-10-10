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
import javax.faces.model.SelectItem;

public class LexicalFunctionComparator implements Comparator<SelectItem> {

    public LexicalFunctionComparator() {
    }

    @Override
    public int compare(SelectItem lf1, SelectItem lf2) {
        String string1 = Normalizer.normalize(lf1.getValue().toString().toLowerCase(), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}+]", "");
        String string2 = Normalizer.normalize(lf2.getValue().toString().toLowerCase(), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}+]", "");
        return string1.compareTo(string2);
    }
   
}
