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
    private ArrayList<String> lexicalCategory;
    private ArrayList<String> senseCategory;
    private ArrayList<String> translationCategory;

    private ArrayList<String> synFrameType;
    private ArrayList<String> synArgType;

    private ArrayList<String> lexicalAspects;

    private ArrayList<Ontology> taxonomy;

    private ArrayList<String> lingCatList;

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

    public ArrayList<String> getLexicalCategory() {
        return lexicalCategory;
    }

    public void setLexicalCategory(ArrayList<String> lexicalCategory) {
        this.lexicalCategory = lexicalCategory;
    }

    public ArrayList<String> getSenseCategory() {
        return senseCategory;
    }

    public void setSenseCategory(ArrayList<String> senseCategory) {
        this.senseCategory = senseCategory;
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
        lingCatList.add("http://www.lexinfo.net/ontologies/2.0/lexinfo");

    }

    private void loadLexicalCategories() {
        lexicalCategory = new ArrayList();
        lexicalCategory.add("optionalElement");
        lexicalCategory.add("infix");
        lexicalCategory.add("wordElement");
        lexicalCategory.add("affix");
        lexicalCategory.add("prefix");
        lexicalCategory.add("morphologicalElement");
        lexicalCategory.add("syllable");
        lexicalCategory.add("radical");
        lexicalCategory.add("inflectionElement");
        lexicalCategory.add("baseElement");
        lexicalCategory.add("suffix");
        lexicalCategory.add("initialism");
        lexicalCategory.add("logicalExpression");
        lexicalCategory.add("idiom");
        lexicalCategory.add("entryTerm");
        lexicalCategory.add("internationalism");
        lexicalCategory.add("compound");
        lexicalCategory.add("shortForm");
        lexicalCategory.add("contraction");
        lexicalCategory.add("equation");
        lexicalCategory.add("acronym");
        lexicalCategory.add("abbreviation");
        lexicalCategory.add("internationalScientificTerm");
        lexicalCategory.add("commonName");
        lexicalCategory.add("symbol");
        lexicalCategory.add("productName");
        lexicalCategory.add("transcribedForm");
        lexicalCategory.add("partNumber");
        lexicalCategory.add("string");
        lexicalCategory.add("phraseologicalUnit");
        lexicalCategory.add("proverb");
        lexicalCategory.add("fullForm");
        lexicalCategory.add("appellation");
        lexicalCategory.add("formula");
        lexicalCategory.add("setPhrase");
        lexicalCategory.add("standardText");
        lexicalCategory.add("clippedTerm");
        lexicalCategory.add("expression");
        lexicalCategory.add("abbreviatedForm");
        lexicalCategory.add("stringCategory");
        lexicalCategory.add("nucleus");
        lexicalCategory.add("initialism");
        lexicalCategory.add("contraction");
        lexicalCategory.add("acronym");
        lexicalCategory.add("clippedTerm");
        lexicalCategory.add("abbreviation");
        lexicalCategory.add("abbreviatedForm");
    }

    private void loadSenseCategories() {
        senseCategory = new ArrayList();
        senseCategory.add("commonlyUsed");
        senseCategory.add("infrequentlyUsed");
        senseCategory.add("rarelyUsed");
        senseCategory.add("admittedTerm");
        senseCategory.add("deprecatedTerm");
        senseCategory.add("supersededTerm");
        senseCategory.add("preferredTerm");
        senseCategory.add("legalTerm");
        senseCategory.add("standardizedTerm");
        senseCategory.add("regulatedTerm");
        senseCategory.add("tabooRegister");
        senseCategory.add("technicalRegister");
        senseCategory.add("bench-levelRegister");
        senseCategory.add("vulgarRegister");
        senseCategory.add("neutralRegister");
        senseCategory.add("formalRegister");
        senseCategory.add("facetiousRegister");
        senseCategory.add("slangRegister");
        senseCategory.add("dialectRegister");
        senseCategory.add("ironicRegister");
        senseCategory.add("inHouseRegister");
        senseCategory.add("outdatedForm");
        senseCategory.add("archaicForm");
        senseCategory.add("obsoleteForm");
    }

    private void loadLexicalRelationTypes() {
        lexicalRelType = new ArrayList();
        lexicalRelType.add("lexicalRel");
        lexicalRelType.add("translatableAs");
        lexicalRelType.add("derivedForm");
        lexicalRelType.add("clippedTermFor");
        lexicalRelType.add("contractionFor");
        lexicalRelType.add("abbreviationFor");
        lexicalRelType.add("acronymFor");
        lexicalRelType.add("initialismFor");
        lexicalRelType.add("shortFormFor");
        lexicalRelType.add("participleFormOf");
        lexicalRelType.add("etymologicalRoot");
        lexicalRelType.add("fullFormFor");
        lexicalRelType.add("geographicalVariant");
        lexicalRelType.add("head");
        lexicalRelType.add("homograph");
        lexicalRelType.add("homonym");
        lexicalRelType.add("homophone");
        lexicalRelType.add("root");
    }

    private void loadSenseRelationTypes() {
        senselRelType = new ArrayList();
        senselRelType.add("senseRelation");
        senselRelType.add("translation");
        senselRelType.add("approximate");
        senselRelType.add("approximateSynonym");
        senselRelType.add("hypernym");
        senselRelType.add("causallyRelatedConcept");
        senselRelType.add("collocation");
        senselRelType.add("exact");
        senselRelType.add("synonym");
        senselRelType.add("antonym");
        senselRelType.add("hyponym");
        senselRelType.add("holonymTerm");
        senselRelType.add("memberHolonym");
        senselRelType.add("partHolonym");
        senselRelType.add("substanceHolonym");
        senselRelType.add("meronymTerm");
        senselRelType.add("memberMeronym");
        senselRelType.add("partMeronym");
        senselRelType.add("substanceMeronym");
        senselRelType.add("pertainsTo");
        senselRelType.add("relatedTerm");
        senselRelType.add("associativeRelation");
        senselRelType.add("coordinateConcept");
    }

    private void loadSynFrameType() {
        synFrameType = new ArrayList();
        synFrameType.add("AdjectiveFrame");
        synFrameType.add("NounFrame");
        synFrameType.add("PrepositionFrame");
        synFrameType.add("PrepositionalFrame");
        synFrameType.add("VerbFrame");
    }

    private void loadSynArgType() {
        synArgType = new ArrayList();
        synArgType.add("adjunct");
        synArgType.add("attributiveArgument");
        synArgType.add("clausalArgument");
        synArgType.add("complement");
        synArgType.add("object");
        synArgType.add("postPositiveArgument ");
        synArgType.add("subject");
        synArgType.add("synArg");
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
