/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.constant;

/**
 *
 * @author andrea
 */
public class LexicalQuery {

    // query prefixes
    public static final String PREFIXES
            = "PREFIX lexinfo: <" + Namespace.LEXINFO + ">\n"
            + "PREFIX rdfs: <" + Namespace.RDFS + ">\n"
            + "PREFIX skos: <" + Namespace.SKOS + ">\n"
            + "PREFIX ditmaolemon: <" + Namespace.DITMAO_LEMON_NS + ">\n"
            + "PREFIX lexicon: <" + Namespace.LEXICON + ">\n"
            + "PREFIX ontolex: <" + Namespace.ONTOLEX + ">\n"
            + "PREFIX decomp: <" + Namespace.DECOMP + ">\n"
            + "PREFIX rdf: <" + Namespace.RDF + ">\n"
            + "PREFIX lime: <" + Namespace.LIME + ">\n"
            + "PREFIX dct: <" + Namespace.DCT + ">\n"
            + "PREFIX onto: <" + Namespace.DOMAIN_ONTOLOGY + ">\n";

    // queries to lexifon for setting properties values
    public static final String PoS_CLASS = "SELECT ?posClass WHERE {"
            + " DirectSubClassOf(?posClass, lexinfo:PartOfSpeech) }";

    public static final String PoS = "SELECT ?pos WHERE {"
            + " DirectType(?pos, lexinfo:POS_CLASS) }";

    public static final String GENDER = "SELECT ?g WHERE {"
            + " Type(?g, lexinfo:Gender) }";

    public static final String NUMBER = "SELECT ?n WHERE {"
            + " Type(?n, lexinfo:Number) }";

    // query for getting lexicon values
    public static final String LEXICON = "SELECT ?lexicon WHERE {"
            + " PropertyValue(?lexicon, lime:language, \"_LANG_\") }";

    public static final String SENSES_BY_LANGUAGE = "SELECT ?sense WHERE {"
            + " Type(?l, lime:Lexicon), "
            + " PropertyValue(?l, lime:language, \"_LANG_\"),"
            + " PropertyValue(?l, lime:entry, ?le),"
            + " PropertyValue(?le, ontolex:sense, ?sense) }";

    public static final String LANGUAGES = "SELECT ?lang WHERE {"
            + " Type(?l, lime:Lexicon),"
            + " PropertyValue(?l, lime:language, ?lang) }";

    public static final String FORM_INSTANCES_OF_LEMMA = "SELECT ?of WHERE {"
            + " PropertyValue(?l, ontolex:canonicalForm, lexicon:_LEMMA_),"
            + " PropertyValue(?l, ontolex:otherForm, ?of) }";

    public static final String FORM_REPRESENTATION = "SELECT ?wr WHERE {"
            + " PropertyValue(lexicon:_FORM_, ontolex:writtenRep, ?wr) }";

    public static final String FORM_POS = "SELECT ?pos WHERE {"
            + " PropertyValue(lexicon:_FORM_, lexinfo:partOfSpeech, ?pos) }";

    public static final String FORM_GENDER = "SELECT ?g WHERE {"
            + " PropertyValue(lexicon:_FORM_, lexinfo:gender, ?g) }";

    public static final String FORM_PERSON = "SELECT ?p WHERE {"
            + " PropertyValue(lexicon:_FORM_, lexinfo:person, ?p) }";

    public static final String FORM_MOOD = "SELECT ?m WHERE {"
            + " PropertyValue(lexicon:_FORM_, lexinfo:mood, ?m) }";

    public static final String FORM_VOICE = "SELECT ?v WHERE {"
            + " PropertyValue(lexicon:_FORM_, lexinfo:voice, ?v) }";

    public static final String FORM_NUMBER = "SELECT ?n WHERE {"
            + " PropertyValue(lexicon:_FORM_, lexinfo:number, ?n) }";

    public static final String SENSES_OF_LEMMA = "SELECT ?s WHERE {"
            + " PropertyValue(?l, ontolex:canonicalForm, lexicon:_LEMMA_),"
            + " PropertyValue(?l, ontolex:sense, ?s) }";

    public static final String LEMMA_INSTANCE_OF_FORM = "SELECT ?cf WHERE {"
            + " PropertyValue(?l, ontolex:otherForm, lexicon:_FORM_),"
            + " PropertyValue(?l, ontolex:canonicalForm, ?cf) }";

    public static final String LEMMA_REPRESENTATION = "SELECT ?wr WHERE {"
            + " PropertyValue(lexicon:_LEMMA_, ontolex:writtenRep, ?wr) }";

    public static final String ENTRY_VALID = "SELECT ?v WHERE {"
            + " PropertyValue(lexicon:_ENTRY_, dct:valid, ?v) }";

    public static final String LEMMA_POS = "SELECT ?pos WHERE {"
            + " PropertyValue(lexicon:_LEMMA_, lexinfo:partOfSpeech, ?pos) }";

    public static final String LEMMA_GENDER = "SELECT ?g WHERE {"
            + " PropertyValue(lexicon:_LEMMA_, lexinfo:gender, ?g) }";

    public static final String LEMMA_PERSON = "SELECT ?p WHERE {"
            + " PropertyValue(lexicon:_LEMMA_, lexinfo:person, ?p) }";

    public static final String LEMMA_VOICE = "SELECT ?v WHERE {"
            + " PropertyValue(lexicon:_LEMMA_, lexinfo:voice, ?v) }";

    public static final String LEMMA_MOOD = "SELECT ?m WHERE {"
            + " PropertyValue(lexicon:_LEMMA_, lexinfo:mood, ?m) }";

    public static final String LEMMA_NUMBER = "SELECT ?n WHERE {"
            + " PropertyValue(lexicon:_LEMMA_, lexinfo:number, ?n) }";

    public static final String SENSES_OF_FORM = "SELECT ?s WHERE {"
            + " PropertyValue(?l, ontolex:otherForm, lexicon:_FORM_),"
            + " PropertyValue(?l, ontolex:sense, ?s) }";

    public static final String LEMMA_INSTANCE_OF_SENSE = "SELECT ?cf WHERE {"
            + " PropertyValue(?l, ontolex:sense, lexicon:_SENSE_),"
            + " PropertyValue(?l, ontolex:canonicalForm, ?cf) }";

    public static final String OTHER_INSTANCES_OF_SENSES = "SELECT ?s WHERE {"
            + " PropertyValue(?l, ontolex:sense, lexicon:_SENSE_),"
            + " PropertyValue(?l, ontolex:sense, ?s) }";

    public static final String SENSE_DEFINITION = "SELECT ?def WHERE {"
            + " PropertyValue(lexicon:_SENSE_, skos:definition, ?def) }";

    public static final String SENSE_TRANSLATION = "SELECT ?tr WHERE {"
            + " PropertyValue(lexicon:_SENSE_, ditmaolemon:frenchTranslation, ?tr) }";

    public static final String SENSE_TRANSLATION_OF = "SELECT ?tr WHERE {"
            + " PropertyValue(lexicon:_SENSE_, ditmaolemon:frenchTranslationOf, ?tr) }";

    public static final String SENSE_RELATION = "SELECT ?s WHERE {"
            + " PropertyValue(lexicon:_SENSE_, lexinfo:_RELATION_, ?s) }";

    public static final String SENSE_REFERENCE = "SELECT ?ref WHERE {"
            + " PropertyValue(lexicon:_SENSE_, ontolex:reference, ?ref) }";

    public static final String LEMMA_BASIC = "SELECT DISTINCT ?writtenRep ?individual ?lang ?type ?verified ?pos WHERE {"
            + " PropertyValue(?l, lime:language, ?lang), "
            + " PropertyValue(?l, lime:entry, ?le), "
            + " DirectType(?le, ?type), "
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " PropertyValue(?le, ontolex:canonicalForm, ?individual), "
            + " PropertyValue(?individual, lexinfo:partOfSpeech, ?pos), "
            + " PropertyValue(?individual, ontolex:writtenRep, ?writtenRep) }";

//    public static final String LEMMA_BASIC = "SELECT ?lang WHERE {"
//            + " PropertyValue(?l, lime:language, ?lang) }";
    public static final String LEMMA_BASIC_BY_LANGUAGE = "SELECT DISTINCT ?writtenRep ?individual ?lang ?type ?verified ?pos WHERE {"
            + " PropertyValue(?l, lime:language, \"_LANG_\"), "
            + " PropertyValue(?l, lime:language, ?lang), "
            + " PropertyValue(?l, lime:entry, ?le), "
            + " DirectType(?le, ?type), "
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " PropertyValue(?le, ontolex:canonicalForm, ?individual), "
            + " PropertyValue(?individual, ontolex:writtenRep, ?writtenRep), "
            + " PropertyValue(?individual, lexinfo:partOfSpeech, ?pos)  }";

    public static final String FORM_BASIC = "SELECT ?writtenRep ?individual ?lang ?lemma ?verified WHERE {"
            + " PropertyValue(?l, lime:language, ?lang), "
            + " PropertyValue(?l, lime:entry, ?le), "
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " PropertyValue(?le, ontolex:otherForm, ?individual), "
            + " PropertyValue(?le, ontolex:canonicalForm, ?lemma), "
            + " PropertyValue(?individual, ontolex:writtenRep, ?writtenRep) }";

    public static final String FORM_BASIC_BY_LANGUAGE = "SELECT ?writtenRep ?individual ?lang ?lemma ?verified WHERE {"
            + " PropertyValue(?l, lime:language, \"_LANG_\"), "
            + " PropertyValue(?l, lime:language, ?lang), "
            + " PropertyValue(?l, lime:entry, ?le), "
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " PropertyValue(?le, ontolex:otherForm, ?individual), "
            + " PropertyValue(?le, ontolex:canonicalForm, ?lemma), "
            + " PropertyValue(?individual, ontolex:writtenRep, ?writtenRep) }";

    public static final String SENSE_BASIC = "SELECT ?writtenRep ?lang ?verified WHERE {"
            + " PropertyValue(?l, lime:language, ?lang), "
            + " PropertyValue(?l, lime:entry, ?le), "
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " PropertyValue(?le, ontolex:sense, ?writtenRep) }";

    public static final String SENSE_BASIC_BY_LANGUAGE = "SELECT ?writtenRep ?lang ?verified WHERE {"
            + " PropertyValue(?l, lime:language, \"_LANG_\"), "
            + " PropertyValue(?l, lime:language, ?lang), "
            + " PropertyValue(?l, lime:entry, ?le), "
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " PropertyValue(?le, ontolex:sense, ?writtenRep) }";

    public static final String LEMMA_TYPE = "SELECT ?t WHERE {"
            + " PropertyValue(?le, ontolex:canonicalForm, lexicon:_LEMMA_), "
            + " DirectType(?le, ?t) }";

    public static final String LEMMA_NOTE = "SELECT ?note WHERE {"
            + " PropertyValue(lexicon:_LEMMA_, rdfs:comment, ?note) } ";

    public static final String LEXICAL_RELATION_WORD = "SELECT ?individual WHERE {"
            + " PropertyValue(?l, lime:language, \"_LANG_\"), "
            + " PropertyValue(?l, lime:entry, ?le), "
            + " PropertyValue(?le, ontolex:canonicalForm, ?individual), "
            + " PropertyValue(?individual, ontolex:writtenRep, \"_LEMMA_\") }";

    public static final String LEMMA_SEEALSO = "SELECT ?individual ?lang ?writtenRep WHERE { "
            + "PropertyValue(?_le, ontolex:canonicalForm, lexicon:_LEMMA_),   "
            + "PropertyValue(?_le, rdf:seeAlso, ?le),   "
            + "PropertyValue(?le, ontolex:canonicalForm, ?individual),  "
            + "PropertyValue(?individual, ontolex:writtenRep, ?writtenRep) }";

    public static final String CONSTITUENTS = "SELECT ?constituent ?position WHERE { "
            + "PropertyValue(lexicon:_ENTRY_, decomp:constituent, ?constituent), "
            + "PropertyValue(?constituent, rdfs:comment, ?position) }";

    public static final String CONSTITUENT_AT_POSITION = "SELECT ?constituent WHERE { "
            + "PropertyValue(lexicon:_ENTRY_, decomp:constituent, ?constituent), "
            + "PropertyValue(?constituent, rdfs:comment, \"_POSITION_\") }";

    public static final String WORDS_OF_MULTIWORD = "SELECT ?individual ?lang ?writtenRep WHERE { "
            + "PropertyValue(lexicon:_CONST_, decomp:correspondsTo, ?le), "
            + "PropertyValue(?l, lime:language, ?lang), "
            + "PropertyValue(?l, lime:entry, ?le), "
            + "PropertyValue(?le, ontolex:canonicalForm, ?individual), "
            + "PropertyValue(?individual, ontolex:writtenRep, ?writtenRep) }";

    public static final String SENSE_NOTE = "SELECT ?note WHERE {"
            + " PropertyValue(lexicon:_SENSE_, rdfs:comment, ?note) } ";

    public static final String FORM_NOTE = "SELECT ?note WHERE {"
            + " PropertyValue(lexicon:_FORM_, rdfs:comment, ?note) } ";

    public static final String LEXICALIZATIONS = "SELECT ?r WHERE {"
            + " PropertyValue(lexicon:_ENTRY_, ontolex:sense, ?s),"
            + " PropertyValue(?s, ontolex:reference, ?r) }";

    // query for filter panel
    public static final String ADVANCED_FILTER_LEMMA = "SELECT ?le ?individual ?writtenRep ?sense ?verified ?type ?pos WHERE {"
            + " PropertyValue(?le, ontolex:canonicalForm, ?individual), "
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " PropertyValue(?individual, lexinfo:partOfSpeech, ?pos), "
            + " PropertyValue(?individual, ontolex:writtenRep, ?writtenRep), "
            + " DirectType(?le, ?type), "
            + " PropertyValue(?le, ontolex:sense, ?sense) }";

    public static final String ADVANCED_FILTER_FORM = "SELECT ?le ?individual ?writtenRep ?verified ?type ?pos ?a ?sn WHERE {"
            + " PropertyValue(?le, lemon:otherForm, ?individual), "
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " DirectType(?le, ?type), "
            + " PropertyValue(?individual, lemon:writtenRep, ?writtenRep), "
            + " PropertyValue(?le, lemon:sense, ?sense) }"
            + " OR WHERE { "
            + " PropertyValue(?le, lemon:otherForm, ?individual), "
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " DirectType(?le, ?type), "
            + " PropertyValue(?individual, lemon:writtenRep, ?writtenRep), "
            + " PropertyValue(?le, lemon:sense, ?sense), "
            + " PropertyValue(?sense, ditmaolemon:scientificName, ?sn) }"
            + " OR WHERE { "
            + " PropertyValue(?le, lemon:otherForm, ?individual), "
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " DirectType(?le, ?type), "
            + " PropertyValue(?individual, lemon:writtenRep, ?writtenRep), "
            + " PropertyValue(?individual, lexinfo:partOfSpeech, ?pos), "
            + " PropertyValue(?le, lemon:sense, ?sense) }"
            + " OR WHERE { "
            + " PropertyValue(?le, lemon:otherForm, ?individual), "
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " DirectType(?le, ?type), "
            + " PropertyValue(?individual, lemon:writtenRep, ?writtenRep), "
            + " PropertyValue(?individual, ditmaolemon:hasAlphabet, ?a), "
            + " PropertyValue(?le, lemon:sense, ?sense) }";

}
