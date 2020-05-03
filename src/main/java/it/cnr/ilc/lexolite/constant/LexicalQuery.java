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
            //+ "PREFIX lexicon: <" + Namespace.LEXICON + ">\n"
            + "PREFIX ontolex: <" + Namespace.ONTOLEX + ">\n"
            + "PREFIX lemon: <" + Namespace.LEMON + ">\n"
            + "PREFIX decomp: <" + Namespace.DECOMP + ">\n"
            + "PREFIX rdf: <" + Namespace.RDF + ">\n"
            + "PREFIX lime: <" + Namespace.LIME + ">\n"
            + "PREFIX dct: <" + Namespace.DCT + ">\n"
            + "PREFIX vartrans: <" + Namespace.VARTRANS + ">\n"
            + "PREFIX synsem: <" + Namespace.SYNSEM + ">\n"
            + "PREFIX trcat: <" + Namespace.TRCAT + ">\n";
    //+ "PREFIX onto: <" + Namespace.DOMAIN_ONTOLOGY + ">\n";

    // queries to lexinfo for setting properties values
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
            + " PropertyValue(lexicon:_ENTRY_, lexinfo:partOfSpeech, ?pos) }";

    public static final String ENTRY_DENOTES = "SELECT ?d WHERE {"
            + " PropertyValue(lexicon:_ENTRY_, ontolex:denotes, ?d) }";

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
            + " PropertyValue(?le, lexinfo:partOfSpeech, ?pos), "
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
            + " PropertyValue(?le, lexinfo:partOfSpeech, ?pos)  }";

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

    public static final String LEMMA_MORPHO_TRAITS = "SELECT ?trait ?value WHERE { "
            + "PropertyValue(lexicon:_LEMMA_, ?trait, ?value) }";

    public static final String FORM_MORPHO_TRAITS = "SELECT ?trait ?value WHERE { "
            + "PropertyValue(lexicon:_FORM_, ?trait, ?value) }";

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

    public static final String LANGUAGE_DESCRIPTION = "SELECT ?desc WHERE {"
            + " PropertyValue(?lex, lime:language, \"_LANG_\"), "
            + " PropertyValue(?lex, dct:description, ?desc) }";

    public static final String LANGUAGE_CREATOR = "SELECT ?creator WHERE {"
            + " PropertyValue(?lex, lime:language, \"_LANG_\"), "
            + " PropertyValue(?lex, dct:creator, ?creator) }";

    // query for varTrans module
    public static final String DIRECT_LEXICAL_RELATION = "SELECT ?individual ?rel ?writtenRep ?lang WHERE {"
            + " PropertyValue(lexicon:_ENTRY_, ?rel, ?individual), "
            + " PropertyValue(?individual, ontolex:canonicalForm, ?cf), "
            + " PropertyValue(?cf, ontolex:writtenRep, ?writtenRep), "
            + " PropertyValue(?lex, lime:entry, ?individual), "
            + " PropertyValue(?lex, lime:language, ?lang) }";

    public static final String INDIRECT_LEXICAL_RELATION = "SELECT ?cat ?trgwr ?trglang ?trgind WHERE {"
            + " PropertyValue(?lexRel, vartrans:source, lexicon:_ENTRY_), "
            + " PropertyValue(?lexRel, vartrans:target, ?trgind), "
            + " PropertyValue(?lexRel, vartrans:category, ?cat), "
            + " PropertyValue(?trgind, ontolex:canonicalForm, ?cf), "
            + " PropertyValue(?cf, ontolex:writtenRep, ?trgwr), "
            + " PropertyValue(?lex, lime:entry, ?trgind), "
            + " PropertyValue(?lex, lime:language, ?trglang) }";

    public static final String DIRECT_SENSE_RELATION = "SELECT ?sense ?rel ?lang WHERE {"
            + " PropertyValue(lexicon:_SENSE_, ?rel, ?sense), "
            + " PropertyValue(?entry, ontolex:sense, ?sense), "
            + " PropertyValue(?lex, lime:entry, ?entry), "
            + " PropertyValue(?lex, lime:language, ?lang) }";

    public static final String TERMINOLOGICAL_SENSE_RELATION = "SELECT ?cat ?trglang ?entry WHERE {"
            + " DirectType(?senseRel, vartrans:" + OntoLexEntity.Class.TERMINOLOGICALRELATION.getLabel() + "), "
            + " PropertyValue(?senseRel, vartrans:source, lexicon:_SENSE_), "
            + " PropertyValue(?senseRel, vartrans:target, ?trgind), "
            + " PropertyValue(?senseRel, vartrans:category, ?cat), "
            + " PropertyValue(?entry, ontolex:sense, ?trgind), "
            + " PropertyValue(?lex, lime:entry, ?entry), "
            + " PropertyValue(?lex, lime:language, ?trglang) }";

    public static final String TRANSLATION_SENSE_RELATION = "SELECT ?cat ?trglang ?entry ?conf WHERE {"
            + " DirectType(?senseRel, vartrans:" + OntoLexEntity.Class.TRANSLATION.getLabel() + "), "
            + " PropertyValue(?senseRel, vartrans:source, lexicon:_SENSE_), "
            + " PropertyValue(?senseRel, vartrans:target, ?trgind), "
            + " PropertyValue(?senseRel, vartrans:category, ?cat), "
            + " PropertyValue(?senseRel, lexinfo:translationConfidence, ?conf), "
            + " PropertyValue(?entry, ontolex:sense, ?trgind), "
            + " PropertyValue(?lex, lime:entry, ?entry), "
            + " PropertyValue(?lex, lime:language, ?trglang) }";

    // query for synsem module
    public static final String SYNTACTIC_FRAME = "SELECT ?frameName WHERE {"
            + " PropertyValue(lexicon:_ENTRY_, synsem:synBehavior, ?frameName) }";

    public static final String SYNTACTIC_FRAME_TYPE = "SELECT ?type WHERE {"
            + " Type(lexicon:_FRAME_, ?type) }";

    public static final String SYNTACTIC_FRAME_ARGS = "SELECT ?type ?synArg WHERE {"
            + " PropertyValue(lexicon:_FRAME_, ?type, ?synArg) }";

    public static final String SYNTACTIC_ARG_PROPS = "SELECT ?rel ?value WHERE {"
            + " PropertyValue(lexicon:_ARG_, ?rel, ?value) }";

    public static final String SYNTACTIC_ONTO_MAPPINGS = "SELECT ?rel ?value WHERE {"
            + " PropertyValue(lexicon:_ARG_, ?rel, ?value) }";

    public static final String ONTO_MAPPING_ISA = "SELECT ?frame ?isA WHERE {"
            + " PropertyValue(lexicon:_SENSE_, synsem:isA, ?isA), "
            + " PropertyValue(?le, ontolex:sense, lexicon:_SENSE_), "
            + " PropertyValue(?le, synsem:synBehavior, ?frame), "
            + " PropertyValue(lexicon:_SENSE_, synsem:isA, ?isA) }";

    public static final String ONTO_MAPPING_SUBOBJ = "SELECT ?frame ?subjOfProp ?objOfProp WHERE {"
            + " PropertyValue(?le, ontolex:sense, lexicon:_SENSE_), "
            + " PropertyValue(?le, synsem:synBehavior, ?frame), "
            + " PropertyValue(lexicon:_SENSE_, synsem:subjOfProp, ?subjOfProp), "
            + " PropertyValue(lexicon:_SENSE_, synsem:objOfProp, ?objOfProp)  }";

    // query for filter panel
    public static final String ADVANCED_FILTER_LEMMA = "SELECT ?le ?individual ?writtenRep ?sense ?verified ?type ?pos WHERE {"
            + " PropertyValue(?le, ontolex:canonicalForm, ?individual), "
            + " PropertyValue(?le, dct:valid, ?verified), "
            + " PropertyValue(?le, lexinfo:partOfSpeech, ?pos), "
            + " PropertyValue(?individual, ontolex:writtenRep, ?writtenRep), "
            + " DirectType(?le, ?type), "
            + " PropertyValue(?le, ontolex:sense, ?sense) }";
}
