/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.constant.LexicalQuery;
import java.util.List;
import java.util.Map;

/**
 *
 * @author andrea
 */
public class CNLQuery extends LexiconQuery {

    private String TEMPLATE_CONCEPT_GROUP_1_LEMMA = "SELECT ?le ?individual ?writtenRep ?sense ?type ?ontoClass ?verified WHERE {"
            + " PropertyValue(?le, ditmaolemon:verified, ?verified), "
            + " PropertyValue(?le, lemon:canonicalForm, ?individual), "
            + " PropertyValue(?individual, lemon:writtenRep, ?writtenRep), "
            + " DirectType(?le, ?type), "
            + " PropertyValue(?le, lemon:sense, ?sense), "
            + " PropertyValue(?sense, lemon:reference, ?ontoClass) } ";

    private String TEMPLATE_CONCEPT_GROUP_1_FORM = "SELECT ?le ?individual ?writtenRep ?ontoClass ?verified WHERE {"
            + " PropertyValue(?le, ditmaolemon:verified, ?verified), "
            + " PropertyValue(?le, lemon:otherForm, ?individual), "
            + " PropertyValue(?individual, lemon:writtenRep, ?writtenRep), "
            + " PropertyValue(?le, lemon:sense, ?sense), "
            + " PropertyValue(?sense, lemon:reference, ?ontoClass), "
            + " PropertyValue(?individual, ditmaolemon:hasAlphabet, ?a) } ";

    public CNLQuery(LexiconModel lm) {
        super(lm);
    }

    public List<Map<String, String>> ontoQueryGroup_1_lemmas(String ontoClass) {
        return processQuery(LexicalQuery.PREFIXES + TEMPLATE_CONCEPT_GROUP_1_LEMMA);
    }

    public List<Map<String, String>> ontoQueryGroup_1_forms(String ontoClass) {
        return processQuery(LexicalQuery.PREFIXES + TEMPLATE_CONCEPT_GROUP_1_FORM);
    }

}
