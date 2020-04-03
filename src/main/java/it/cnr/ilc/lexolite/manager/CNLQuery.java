/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.LexOliteProperty;
import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.constant.LexicalQuery;
import it.cnr.ilc.lexolite.constant.Namespace;
import java.util.List;
import java.util.Map;

/**
 *
 * @author andrea
 */
public class CNLQuery extends LexiconQuery {

    private String TEMPLATE_CONCEPT_GROUP_1_LEMMA = "SELECT ?le ?individual ?writtenRep ?sense ?type ?ontoClass ?verified WHERE {"
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " PropertyValue(?le, ontolex:canonicalForm, ?individual), "
            + " PropertyValue(?individual, ontolex:writtenRep, ?writtenRep), "
            + " DirectType(?le, ?type), "
            + " PropertyValue(?le, ontolex:sense, ?sense), "
            + " PropertyValue(?sense, ontolex:reference, ?ontoClass) } ";

    private String TEMPLATE_CONCEPT_GROUP_1_FORM = "SELECT ?le ?individual ?writtenRep ?ontoClass ?verified WHERE {"
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " PropertyValue(?le, ontolex:otherForm, ?individual), "
            + " PropertyValue(?individual, ontolex:writtenRep, ?writtenRep), "
            + " PropertyValue(?le, ontolex:sense, ?sense), "
            + " PropertyValue(?sense, ontolex:reference, ?ontoClass) } ";

    public CNLQuery(LexiconModel lm) {
        super(lm);
    }

    public List<Map<String, String>> ontoQueryGroup_1_lemmas(String ontoClass) {
        return processQuery(LexicalQuery.PREFIXES + "PREFIX onto: <" + LexOliteProperty.getProperty(Label.ONTOLOGY_NAMESPACE_KEY) + ">\n"
                + "PREFIX lexicon: <" + LexOliteProperty.getProperty(Label.LEXICON_NAMESPACE_KEY) + ">\n"
                + TEMPLATE_CONCEPT_GROUP_1_LEMMA);
    }

    public List<Map<String, String>> ontoQueryGroup_1_forms(String ontoClass) {
        return processQuery(LexicalQuery.PREFIXES + "PREFIX onto: <" + LexOliteProperty.getProperty(Label.ONTOLOGY_NAMESPACE_KEY) + ">\n" 
                + "PREFIX lexicon: <" + LexOliteProperty.getProperty(Label.LEXICON_NAMESPACE_KEY) + ">\n"
                + TEMPLATE_CONCEPT_GROUP_1_FORM);
    }

}
