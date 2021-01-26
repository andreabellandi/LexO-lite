/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.constant;

import it.cnr.ilc.lexolite.controller.BaseController;

/**
 *
 * @author andrea
 */
public class Label extends BaseController {

    public static final String NO_ENTRY_FOUND = "No entry found";
    public static final String UNSPECIFIED_POS = "unspecified";
    public static final String LEXICON_FILE_NAME_KEY = "lexiconFileName";
    public static final String LEXICON_NAMESPACE_KEY = "lexiconNamespace";
    public static final String ONTOLOGY_FILE_NAME_KEY = "domainOntologyFileName";
    public static final String ONTOLOGY_NAMESPACE_KEY = "domainOntologyNamespace";
    public static final String LEXO_FOLDER = "/.LexO-lite/";
    public static final String MODELS_FOLDER = "models/";
//    public static final String LEXO_FOLDER = "/.LexO-lite-VQ/";
//        public static final String LEXO_FOLDER = "/.LexO-lite-Theresa/";
    public static final String LEXO_PROPERTIES_FILE_NAME = "lexolite.properties";
    public static final String LEXICAL_FUNCTIONS_FILE_NAME = "melchuck.lexicalFunctions";

    // morpho menu items label
    public static final String MORPHO_ANIMACY_LABEL = "animacy";
    public static final String MORPHO_ASPECT_LABEL = "aspect";
    public static final String MORPHO_CASE_LABEL = "case";
    public static final String MORPHO_CLITICNESS_LABEL = "cliticness";
    public static final String MORPHO_DATING_LABEL = "dating";
    public static final String MORPHO_DEFINITENESS_LABEL = "definiteness";
    public static final String MORPHO_DEGREE_LABEL = "degree";
    public static final String MORPHO_FINITENESS_LABEL = "finiteness";
    public static final String MORPHO_FREQUENCY_LABEL = "frequency";
    public static final String MORPHO_GENDER_LABEL = "gender";
    public static final String MORPHO_MOOD_LABEL = "mood";
    public static final String MORPHO_NEGATIVE_LABEL = "negative";
    public static final String MORPHO_NUMBER_LABEL = "number";
    public static final String MORPHO_PERSON_LABEL = "person";
    public static final String MORPHO_TENSE_LABEL = "tense";
    public static final String MORPHO_TERM_ELEMENT_LABEL = "termElement";
    public static final String MORPHO_TERM_TYPE_LABEL = "termType";
    public static final String MORPHO_VERB_FORM_MOOD_LABEL = "verbFormMood";
    public static final String MORPHO_VOICE_LABEL = "voice";
    
    public static enum ClickProvenance {
        LEMMA_LIST_VIEW, FORM_LIST_VIEW, DICTIONARY_VIEW
    }

}
