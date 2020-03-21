/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

/**
 *
 * @author andrea
 */
@Named
@ApplicationScoped
public class PropertyValue {

    private LexiconManager lexiconManager = LexiconManager.getInstance();

    private ArrayList<String> lemmaInfo;
    private ArrayList<String> alphabet;
    private ArrayList<String> gender;
    private ArrayList<String> number;
    private ArrayList<String> person;
    private ArrayList<String> mood;
    private ArrayList<String> voice;
    private ArrayList<String> PoS;
    private ArrayList<String> multiwordType;

    private ArrayList<String> lexicalRelType;
    private ArrayList<String> senselRelType;
    private ArrayList<SelectItem> lexicalCategory;
    private ArrayList<SelectItem> senseCategory;
    private ArrayList<String> translationCategory;

    private ArrayList<String> synFrameType;
    private ArrayList<String> synArgType;

    private ArrayList<String> lexicalAspects;

    private ArrayList<Ontology> taxonomy;

    private ArrayList<String> lingCatList;

    public ArrayList<SelectItem> getSenseCategory() {
        return senseCategory;
    }

    public void setSenseCategory(ArrayList<SelectItem> senseCategory) {
        this.senseCategory = senseCategory;
    }

    public ArrayList<String> getTranslationCategory() {
        return translationCategory;
    }

    public void setTranslationCategory(ArrayList<String> translationCategory) {
        this.translationCategory = translationCategory;
    }

    public ArrayList<String> getLingCatList() {
        return lingCatList;
    }

    public void setLingCatList(ArrayList<String> lingCatList) {
        this.lingCatList = lingCatList;
    }

    public ArrayList<String> getSynFrameType() {
        return synFrameType;
    }

    public void setSynFrameType(ArrayList<String> synFrameType) {
        this.synFrameType = synFrameType;
    }

    public ArrayList<String> getSynArgType() {
        return synArgType;
    }

    public void setSynArgType(ArrayList<String> synArgType) {
        this.synArgType = synArgType;
    }

    public ArrayList<String> getLemmaInfo() {
        return lemmaInfo;
    }

    public ArrayList<String> getAlphabet() {
        return alphabet;
    }

    public ArrayList<String> getGender() {
        return gender;
    }

    public ArrayList<String> getNumber() {
        return number;
    }

    public ArrayList<String> getPerson() {
        return person;
    }

    public void setPerson(ArrayList<String> person) {
        this.person = person;
    }

    public ArrayList<String> getMood() {
        return mood;
    }

    public void setMood(ArrayList<String> mood) {
        this.mood = mood;
    }

    public ArrayList<String> getVoice() {
        return voice;
    }

    public void setVoice(ArrayList<String> voice) {
        this.voice = voice;
    }

    public ArrayList<String> getMultiwordType() {
        return multiwordType;
    }

    public void setMultiwordType(ArrayList<String> multiwordType) {
        this.multiwordType = multiwordType;
    }

    public ArrayList<String> getPoS(String lemmaType) {
        if (lemmaType.equals("Word")) {
            return PoS;
        } else {
            return multiwordType;
        }
    }

    public LexiconManager getLexiconManager() {
        return lexiconManager;
    }

    public void setLexiconManager(LexiconManager lexiconManager) {
        this.lexiconManager = lexiconManager;
    }

    public ArrayList<String> getLexicalRelType() {
        return lexicalRelType;
    }

    public void setLexicalRelType(ArrayList<String> lexicalRelType) {
        this.lexicalRelType = lexicalRelType;
    }

    public ArrayList<String> getSenselRelType() {
        return senselRelType;
    }

    public void setSenselRelType(ArrayList<String> senselRelType) {
        this.senselRelType = senselRelType;
    }

    public ArrayList<SelectItem> getLexicalCategory() {
        return lexicalCategory;
    }

    public void setLexicalCategory(ArrayList<SelectItem> lexicalCategory) {
        this.lexicalCategory = lexicalCategory;
    }

    public ArrayList<Ontology> getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(ArrayList<Ontology> taxonomy) {
        this.taxonomy = taxonomy;
    }

    public ArrayList<String> getLexicalAspects() {
        return lexicalAspects;
    }

    public void setLexicalAspects(ArrayList<String> lexicalAspects) {
        this.lexicalAspects = lexicalAspects;
    }

    @PostConstruct
    public void load() {
        lexicalAspects = new ArrayList();
        lexicalAspects.add("Core");
        lexicalAspects.add("Variation and Translation");
        lexicalAspects.add("Syntax and Semantics");

        // properties and values are loaded "by hands", but they should be retrieved from lexinfo ontology !
        PoS = new ArrayList<String>();
        PoS.add("adjective");
        PoS.add("article");
        PoS.add("fusedPrepositionDeterminer");
        PoS.add("fusedPrepositionPronoun");
        PoS.add("participleAdjective");
        PoS.add("preposition");
        PoS.add("noun");
        PoS.add("verb");
        multiwordType = new ArrayList<String>();
        multiwordType.add("nounPhrase");
        multiwordType.add("verbPhrase");
        multiwordType.add("adjectivePhrase");
        gender = new ArrayList<String>();
        gender.add("feminine");
        gender.add("masculine");
        gender.add("neuter");
        number = new ArrayList<String>();
        number.add("singular");
        number.add("plural");
        number.add("dual");
        person = new ArrayList<String>();
        person.add("firstPerson");
        person.add("secondPerson");
        person.add("thirdPerson");
        mood = new ArrayList<String>();
        mood.add("subjunctive");
        mood.add("imperative");
        mood.add("indicative");
        voice = new ArrayList<String>();
        voice.add("activeVoice");
        voice.add("passiveVoice");
        voice.add("middleVoice");

        loadLexicalRelationTypes();
        loadSenseRelationTypes();
        loadLexicalCategories();
        loadSenseCategories();

        translationCategory = new ArrayList();
        translationCategory.add("directEquivalent");
        translationCategory.add("culturalEquivalent");
        translationCategory.add("lexicalEquivalent");

        loadSynFrameType();
        loadSynArgType();

        lingCatList = new ArrayList();
        lingCatList.add("http://www.lexinfo.net/ontologies/3.0/lexinfo");

    }

    private void loadLexicalCategories() {
        lexicalCategory = new ArrayList<>();
        SelectItemGroup g1 = new SelectItemGroup("Dating");
        g1.setSelectItems(new SelectItem[]{new SelectItem("modern", "modern"),
            new SelectItem("old", "old")});

        SelectItemGroup g2 = new SelectItemGroup("Term element");
        g2.setSelectItems(new SelectItem[]{new SelectItem("morphologicalElement", "morphological element"),
            new SelectItem("affix", "affix"),
            new SelectItem("baseElement", "base element"),
            new SelectItem("infix", "infix"),
            new SelectItem("inflectionElement", "inflection element"),
            new SelectItem("optionalElement", "optional element"),
            new SelectItem("prefix", "prefix"),
            new SelectItem("radical", "radical"),
            new SelectItem("suffix", "suffix"),
            new SelectItem("syllable", "syllable"),
            new SelectItem("wordElement", "word element")});

        SelectItemGroup g3 = new SelectItemGroup("Term element");
        g3.setSelectItems(new SelectItem[]{new SelectItem("abbreviatedForm", "abbreviated form"),
            new SelectItem("clippedTerm", "clipped term"),
            new SelectItem("commonName", "common name"),
            new SelectItem("entryTerm", "entry term"),
            new SelectItem("fullForm", "full form"),
            new SelectItem("internationalScientificTerm", "international scientific term"),
            new SelectItem("logicalExpression", "logical expression"),
            new SelectItem("partNumber", "part number"),
            new SelectItem("phraseologicalUnit", "phraseological unit"),
            new SelectItem("setPhrase", "set phrase"),
            new SelectItem("shortForm", "short form"),
            new SelectItem("standardText", "standard text"),
            new SelectItem("transcribedForm", "transcribed form"),
            new SelectItem("abbreviation", "abbreviation"),
            new SelectItem("acronym", "acronym"),
            new SelectItem("appellation", "appellation"),
            new SelectItem("compound", "compound"),
            new SelectItem("contraction", "contraction"),
            new SelectItem("equation", "equation"),
            new SelectItem("expression", "expression"),
            new SelectItem("formula", "formula"),
            new SelectItem("idiom", "idiom"),
            new SelectItem("initialism", "initialism"),
            new SelectItem("internationalism", "internationalism"),
            new SelectItem("nucleus", "nucleus"),
            new SelectItem("productName", "product name"),
            new SelectItem("proverb", "proverb"),
            new SelectItem("sku", "sku"),
            new SelectItem("string", "string"),
            new SelectItem("stringCategory", "string category"),
            new SelectItem("symbol", "symbol")});
        
        lexicalCategory.add(g1);
        lexicalCategory.add(g2);
        lexicalCategory.add(g3);
    }

    private void loadSenseCategories() {
        senseCategory = new ArrayList<>();
        SelectItemGroup g1 = new SelectItemGroup("Frquency");
        g1.setSelectItems(new SelectItem[]{new SelectItem("commonlyUsed", "commonly used"),
            new SelectItem("rarelyUsed", "rarely used"),
            new SelectItem("infrequentlyUsed", "infrequently used")});

        SelectItemGroup g2 = new SelectItemGroup("Normative Authorization");
        g2.setSelectItems(new SelectItem[]{
            new SelectItem("admittedTerm", "admitted term"),
            new SelectItem("deprecatedTerm", "deprecated term"),
            new SelectItem("legalTerm", "legal term"),
            new SelectItem("preferredTerm", "preferred term"),
            new SelectItem("regulatedTerm", "regulated term"),
            new SelectItem("standardizedTerm", "standardized term"),
            new SelectItem("supersededTerm", "superseded term")});

        SelectItemGroup g3 = new SelectItemGroup("Register");
        g3.setSelectItems(new SelectItem[]{
            new SelectItem("benchLeveLRegister", "bench-level"),
            new SelectItem("inHouseRegister", "in house"),
            new SelectItem("neutralRegister", "neutral"),
            new SelectItem("slangRegister", "slang"),
            new SelectItem("technicalRegister", "technical"),
            new SelectItem("vulgarRegister", "vulgar"),
            new SelectItem("dialectRegister", "dialect"),
            new SelectItem("facetiousRegister", "facetious"),
            new SelectItem("formalRegister", "formal"),
            new SelectItem("ironicRegister", "ironic"),
            new SelectItem("tabooRegister", "taboo")});

        SelectItemGroup g4 = new SelectItemGroup("Temporal Qualifier");
        g4.setSelectItems(new SelectItem[]{
            new SelectItem("archaicForm", "archaic form"),
            new SelectItem("obsoleteForm", "obsolete form"),
            new SelectItem("outDatedForm", "outdated form")});

        senseCategory.add(g1);
        senseCategory.add(g2);
        senseCategory.add(g3);
        senseCategory.add(g4);
    }

    // properties
    private void loadLexicalRelationTypes() {
        lexicalRelType = new ArrayList();
        lexicalRelType.add("lexicalRel");
        lexicalRelType.add("   derivedForm");
        lexicalRelType.add("      clippedTermFor");
        lexicalRelType.add("      contractionFor");
        lexicalRelType.add("         abbreviationFor");
        lexicalRelType.add("         acronymFor");
        lexicalRelType.add("         initialismFor");
        lexicalRelType.add("         shortFormFor");
        lexicalRelType.add("   participleFormOf");
        lexicalRelType.add("   etymologicalRoot");
        lexicalRelType.add("   fullFormFor");
        lexicalRelType.add("   geographicalVariant");
        lexicalRelType.add("   head");
        lexicalRelType.add("   homograph");
        lexicalRelType.add("   homonym");
        lexicalRelType.add("   homophone");
        lexicalRelType.add("   root");
        lexicalRelType.add("   translatableAs");
    }

    // properties
    private void loadSenseRelationTypes() {
        senselRelType = new ArrayList();
        senselRelType.add("senseRelation");
        senselRelType.add("   antonym");
        senselRelType.add("   approximate");
        senselRelType.add("   approximate synonym");
        senselRelType.add("   hypernym");
        senselRelType.add("   hyponym");
        senselRelType.add("   causally related concept");
        senselRelType.add("   collocation");
        senselRelType.add("   exact");
        senselRelType.add("   partitive relation");
        senselRelType.add("      holonym term");
        senselRelType.add("         member holonym");
        senselRelType.add("         part holonym");
        senselRelType.add("         substance holonym");
        senselRelType.add("      meronym term");
        senselRelType.add("         member meronym");
        senselRelType.add("         part meronym");
        senselRelType.add("         substance meronym");
        senselRelType.add("   pertains to");
        senselRelType.add("   related term");
        senselRelType.add("      associativeRelation");
        senselRelType.add("      coordinateConcept");
        senselRelType.add("   quasi equivalent");
        senselRelType.add("   synonym");
        senselRelType.add("   translation");
    }

    // classes
    private void loadSynFrameType() {
        synFrameType = new ArrayList();
        synFrameType.add("adjective frame");
        synFrameType.add("   adjective attributive frame");
        synFrameType.add("      adjective predicate frame");
        synFrameType.add("   adjective comparative frame");
        synFrameType.add("   adjective post positive frame");
        synFrameType.add("      adjective accusative post positive frame");
        synFrameType.add("      adjective dative post positive frame");
        synFrameType.add("      adjective genitive post positive frame");
        synFrameType.add("   adjective pp frame");
        synFrameType.add("   adjective predicate frame");
        synFrameType.add("   adjective predicative frame");
        synFrameType.add("      adjective pp frame");
        synFrameType.add("      adjective predicate frame");
        synFrameType.add("   adjective scale frame");
        synFrameType.add("   adjective superlative frame");
        synFrameType.add("control");
        synFrameType.add("   arbitrary control");
        synFrameType.add("      gerund ac frame");
        synFrameType.add("      intransitive infinitive ac frame");
        synFrameType.add("      transitive infinitive ac frame");
        synFrameType.add("   object control");
        synFrameType.add("      gerund oc frame");
        synFrameType.add("      transitive infinitive oc frame");
        synFrameType.add("   raising subject");
        synFrameType.add("      intransitive infinitive rs frame");
        synFrameType.add("   subject control");
        synFrameType.add("      gerund sc frame");
        synFrameType.add("      intransitive infinitive sc frame");
        synFrameType.add("      transitive infinitive sc frame");
        synFrameType.add("noun frame");
        synFrameType.add("   noun possessive frame");
        synFrameType.add("   noun pp frame");
        synFrameType.add("   noun predicate frame");
        synFrameType.add("      noun pp frame");
        synFrameType.add("preposition frame");
        synFrameType.add("   prepositional phrase frame");
        synFrameType.add("prepositional frame");
        synFrameType.add("   adjective pp frame");
        synFrameType.add("   impersonal intransitive pp frame");
        synFrameType.add("   noun pp frame");
        synFrameType.add("   pp frame");
        synFrameType.add("      intransitive pp frame");
        synFrameType.add("         intransitive pp declarative frame");
        synFrameType.add("      subjectless intransitive pp frame");
        synFrameType.add("      subjectless transitive pp frame");
        synFrameType.add("      transitive pp frame");
        synFrameType.add("         reflexive transitive pp frame");
        synFrameType.add("verb frame");
        synFrameType.add("   adjectival complement frame");
        synFrameType.add("      transitive adjectival complement frame");
        synFrameType.add("   adverbial complement frame");
        synFrameType.add("      transitive adverbial complement frame");
        synFrameType.add("   dative transitive frame");
        synFrameType.add("      reflexive dative transitive frame");
        synFrameType.add("   declarative frame");
        synFrameType.add("      intransitive declarative frame");
        synFrameType.add("      intransitive pp declarative frame");
        synFrameType.add("      transitive declarative frame");
        synFrameType.add("   ditransitive double accusative frame");
        synFrameType.add("   ditransitive frame");
        synFrameType.add("      ditransitive frame_ for");
        synFrameType.add("      ditransitive frame_ to");
        synFrameType.add("      reflexive ditransitive frame");
        synFrameType.add("   genitive ditransitive frame");
        synFrameType.add("   genitive transitive frame");
        synFrameType.add("   gerund frame");
        synFrameType.add("      gerund ac frame");
        synFrameType.add("      gerund oc frame");
        synFrameType.add("      gerund sc frame");
        synFrameType.add("   impersonal frame");
        synFrameType.add("      impersonal intransitive frame");
        synFrameType.add("         impersonal intransitive pp frame");
        synFrameType.add("      impersonal transitive frame");
        synFrameType.add("   infinitive frame");
        synFrameType.add("      intransitive infinitive ac frame");
        synFrameType.add("      intransitive infinitive rs frame");
        synFrameType.add("      intransitive infinitive sc frame");
        synFrameType.add("      transitive infinitive ac frame");
        synFrameType.add("      transitive infinitive oc frame");
        synFrameType.add("      transitive infinitive sc frame");
        synFrameType.add("   interrogative frame");
        synFrameType.add("      intransitive interrogative frame");
        synFrameType.add("      transitive interrogative frame");
        synFrameType.add("   interrogative infinitive frame");
        synFrameType.add("      intransitive interrogative infinitive frame");
        synFrameType.add("      transitive interrogative infinitive frame");
        synFrameType.add("   intransitive frame");
        synFrameType.add("      intransitive adjectival complement frame");
        synFrameType.add("      intransitive adverbial complement frame");
        synFrameType.add("      intransitive declarative frame");
        synFrameType.add("      intransitive infinitive ac frame");
        synFrameType.add("      intransitive infinitive rs frame");
        synFrameType.add("      intransitive infinitive sc frame");
        synFrameType.add("      intransitive interrogative frame");
        synFrameType.add("      intransitive interrogative infinitive frame");
        synFrameType.add("      intransitive nominal complement frame");
        synFrameType.add("      intransitive pp frame");
        synFrameType.add("         intransitive pp declarative frame");
        synFrameType.add("      intransitive sentential frame");
        synFrameType.add("   nominal complement frame");
        synFrameType.add("      transitive nominal complement frame");
        synFrameType.add("   pp frame");
        synFrameType.add("   intransitive pp frame");
        synFrameType.add("      intransitive pp declarative frame");
        synFrameType.add("   subjectless intransitive pp frame");
        synFrameType.add("   subjectless transitive pp frame");
        synFrameType.add("   transitive pp frame");
        synFrameType.add("   reflexive transitive pp frame");
        synFrameType.add("   prepositional interrogative frame");
        synFrameType.add("   reciprocal frame");
        synFrameType.add("      reflexive reciprocal frame");
        synFrameType.add("   reflexive frame");
        synFrameType.add("      reflexive dative transitive frame");
        synFrameType.add("      reflexive ditransitive frame");
        synFrameType.add("      reflexive reciprocal frame");
        synFrameType.add("      reflexive transitive frame");
        synFrameType.add("      reflexive transitive pp frame");
        synFrameType.add("   sentential frame");
        synFrameType.add("      intransitive sentential frame");
        synFrameType.add("      transitive sentential frame");
        synFrameType.add("   subjectless frame");
        synFrameType.add("      subjectless intransitive frame");
        synFrameType.add("         subjectless intransitive pp frame");
        synFrameType.add("      subjectless transitive frame");
        synFrameType.add("         subjectless transitive pp frame");
        synFrameType.add("   transitive frame");
        synFrameType.add("      reflexive transitive frame");
        synFrameType.add("      transitive adjectival complement frame");
        synFrameType.add("      transitive adverbial complement frame");
        synFrameType.add("      transitive declarative frame");
        synFrameType.add("      transitive infinitive ac frame");
        synFrameType.add("      transitive infinitive oc frame");
        synFrameType.add("      transitive infinitive sc frame");
        synFrameType.add("      transitive interrogative frame");
        synFrameType.add("      transitive nominal complement frame");
        synFrameType.add("      transitive pp frame");
        synFrameType.add("         reflexive transitive pp frame");
        synFrameType.add("      transitive sentential frame");
    }

    // properties
    private void loadSynArgType() {
        synArgType = new ArrayList();
        synArgType.add("syn arg");
        synArgType.add("   adjunct");
        synArgType.add("      possessive adjunct");
        synArgType.add("      predicative adjunct");
        synArgType.add("         comparative adjunct");
        synArgType.add("         superlative adjunct");
        synArgType.add("      prepositional adjunct");
        synArgType.add("   adpositional object");
        synArgType.add("      prepositional object");
        synArgType.add("   adverbial complement");
        synArgType.add("   attributive arg");
        synArgType.add("   clausal arg");
        synArgType.add("      declarative clause");
        synArgType.add("      gerund clause");
        synArgType.add("      infinitive clause");
        synArgType.add("      interrogative clause");
        synArgType.add("      interrogative infinitive clause");
        synArgType.add("      possessive infinitive clause");
        synArgType.add("      prepositional gerund clause");
        synArgType.add("      prepositional interrogative clause");
        synArgType.add("      sentential clause");
        synArgType.add("      subjunctive clause");
        synArgType.add("   comparative adjunct");
        synArgType.add("   complement");
        synArgType.add("      adverbial complement");
        synArgType.add("      object complement");
        synArgType.add("      predicative adjective");
        synArgType.add("      predicative adverb");
        synArgType.add("      predicative nominative");
        synArgType.add("   copulative arg");
        synArgType.add("      copulative subject");
        synArgType.add("   copulative subject");
        synArgType.add("   declarative clause");
        synArgType.add("   direct object");
        synArgType.add("   genitive object");
        synArgType.add("   gerund object");
        synArgType.add("   indirect object");
        synArgType.add("   infinitive clause");
        synArgType.add("   interrogative infinitive clause");
        synArgType.add("   object");
        synArgType.add("      adpositional object");
        synArgType.add("         prepositional object");
        synArgType.add("      direct object");
        synArgType.add("      genitive object");
        synArgType.add("      indirect object");
        synArgType.add("      reflexive object");
        synArgType.add("   object complement");
        synArgType.add("   possessive adjunct");
        synArgType.add("   possessive infinitve clause");
        synArgType.add("   post positive arg");
        synArgType.add("   predicative adjective");
        synArgType.add("   predicative adjunct");
        synArgType.add("      comparative adjunct");
        synArgType.add("      superlative adjunct");
        synArgType.add("   predicative adverb");
        synArgType.add("   predicative nominative");
        synArgType.add("   prepositional adjunct");
        synArgType.add("   prepositional gerund clause");
        synArgType.add("   prepositional interrogative clause");
        synArgType.add("   prepositional objectt");
        synArgType.add("   reflexive object");
        synArgType.add("   sentential clause");
        synArgType.add("   subject");
        synArgType.add("      copulative subject");
        synArgType.add("   subjunctive clause");
        synArgType.add("   superlative adjunct");
    }

    public static class Ontology {

        private String label;
        private String OWLclass;

        public Ontology(String label, String OWLClass) {
            this.label = label;
            this.OWLclass = OWLClass;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getOWLclass() {
            return OWLclass;
        }

        public void setOWLclass(String OWLclass) {
            this.OWLclass = OWLclass;
        }
    }
}
