/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.constant;

import it.cnr.ilc.lexolite.LexOliteProperties;

/**
 *
 * @author andrea
 */
public class Namespace {

    public static final String LEXICON = LexOliteProperties.getProperty("lexiconNamespace");
    public static final String DOMAIN_ONTOLOGY = LexOliteProperties.getProperty("domainOntologyNamespace");

    public static final String ONTOLEX = "http://www.w3.org/ns/lemon/ontolex#";
    public static final String LEMON = "http://lemon-model.net/lemon#";
    public static final String LIME = "http://www.w3.org/ns/lemon/lime#";
    public static final String DECOMP = "http://www.w3.org/ns/lemon/decomp#";
    public static final String DCT = "http://purl.org/dc/terms/";
    public static final String LEXINFO = "https://www.lexinfo.net/ontology/2.0/lexinfo#";
    public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String SKOS = "http://www.w3.org/2004/02/skos/core#";
    public static final String VARTRANS = "http://www.w3.org/ns/lemon/vartrans#";
    public static final String SYNSEM = "http://www.w3.org/ns/lemon/synsem#";
    public static final String TRCAT = "http://purl.org/net/translation-categories#";

}
