/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.constant;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author andreabellandi
 */
public class LexicalFunctionMapping {

    public static final Map<String, String> lexicalFunctionMap;

    static {
        lexicalFunctionMap = new HashMap<>();
        lexicalFunctionMap.put("ar01", "Intro to Map");
        lexicalFunctionMap.put("ar02", "Some article");
    }

}
