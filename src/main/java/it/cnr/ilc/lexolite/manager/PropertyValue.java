/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author andrea
 */
@Named
@ApplicationScoped
public class PropertyValue {

    @Inject
    private LexiconManager lexiconManager;

    private ArrayList<String> lemmaInfo;
    private ArrayList<String> alphabet;
    private ArrayList<String> gender;
    private ArrayList<String> number;
    private ArrayList<String> person;
    private ArrayList<String> mood;
    private ArrayList<String> voice;
    private ArrayList<String> variantType;
    private ArrayList<String> transliterationType;
//    private List<SelectItem> PoS;
    private ArrayList<String> PoS;
    private ArrayList<String> multiwordType;

    private ArrayList<Ontology> taxonomy;

    private ArrayList<String> docGroupList;
    private ArrayList<String> typeDocList;
    private ArrayList<String> abbrTitleList;

    private ArrayList<String> lingCatList;

    public ArrayList<String> getLingCatList() {
        return lingCatList;
    }

    public void setLingCatList(ArrayList<String> lingCatList) {
        this.lingCatList = lingCatList;
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

    public ArrayList<String> getVariantType() {
        return variantType;
    }

    public ArrayList<String> getTransliterationType() {
        return transliterationType;
    }

    public ArrayList<Ontology> getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(ArrayList<Ontology> taxonomy) {
        this.taxonomy = taxonomy;
    }

    public ArrayList<String> getReferenceType() {
        return typeDocList;
    }

    public ArrayList<String> getReferenceAbbrTitle(String type) {
        abbrTitleList.clear();
        switch (type) {
            case "Dictionary":
                abbrTitleList.add("DAO");
                abbrTitleList.add("LR");
                abbrTitleList.add("PSW");
                abbrTitleList.add("FEW");
                break;
            case "Edition":
                abbrTitleList.add("RM");
                abbrTitleList.add("RMA");
                abbrTitleList.add("RMM");
                abbrTitleList.add("RPA");
                break;
        }
        return abbrTitleList;
    }

    public ArrayList<String> getReferenceDocGroup(String type) {
        docGroupList.clear();
        switch (type) {
            case "Dictionary":
                docGroupList.add("abbr dict.");
                docGroupList.add("abbr dict.");
                docGroupList.add("abbr dict.");
                docGroupList.add("abbr dict.");
                break;
            case "Edition":
                docGroupList.add("abbr ed.");
                docGroupList.add("abbr ed.");
                docGroupList.add("abbr ed.");
                docGroupList.add("abbr ed.");
                break;
        }
        return docGroupList;
    }

    @PostConstruct
    public void load() {
        // properties and values are loaded "by hands", but they should be retrieved from lexinfo ontology !
//        lemmaInfo = lexiconManager.getLemmaInfo();
//        PoS = lexiconManager.getPoS();
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
//        alphabet = lexiconManager.getAlphabets();
//        gender = lexiconManager.getGenders();
//        number = lexiconManager.getNumbers();
//        variantType = lexiconManager.getVariantTypes();
//        multiwordType = new ArrayList();
//        multiwordType.add("Collocation");
//        multiwordType.add("Sublemma");
//        transliterationType = lexiconManager.getTransliterationTypes();
//        transliterationType.add(0, "none");
//        typeDocList = new ArrayList();
//        typeDocList.add("Dictionary");
//        typeDocList.add("Other text");
//        typeDocList.add("Edition");
//        typeDocList.add("Manuscript");
//        docGroupList = new ArrayList();
//        abbrTitleList = new ArrayList();

        lingCatList = new ArrayList();
        lingCatList.add("http://www.lexinfo.net/ontologies/2.0/lexinfo");

//        loadReferences();
        loadTaxonomy();
    }

    private void loadTaxonomy() {
//        taxonomy = new ArrayList<Ontology>();
//        taxonomy.add(new Ontology("Entité", "Entité"));
//        taxonomy.add(new Ontology("----Animal", "Animal"));
//        taxonomy.add(new Ontology("--------Être humain", "Être humain"));
//        taxonomy.add(new Ontology("------------Femelle", "Femelle"));
//        taxonomy.add(new Ontology("------------Machile", "Machile"));
//        taxonomy.add(new Ontology("--------Mammifère", "Mammifère"));
//        taxonomy.add(new Ontology("--------Quadrupède", "Quadrupède"));
//        taxonomy.add(new Ontology("----Cap", "Cap"));
//        taxonomy.add(new Ontology("----Humeurs de l'œil", "Humeurs de l'œil"));
//        taxonomy.add(new Ontology("----Monde minéral", "Monde minéral"));
//        taxonomy.add(new Ontology("----Nerf", "Nerf"));
//        taxonomy.add(new Ontology("--------Nervis sensitif", "Nervis sensitif"));
//        taxonomy.add(new Ontology("----Organe de purge", "Organe de purge"));
//        taxonomy.add(new Ontology("----Organisme", "Organisme"));
//        taxonomy.add(new Ontology("----Partie du corps", "Partie du corps"));
//        taxonomy.add(new Ontology("----Plante", "Plante"));
//        taxonomy.add(new Ontology("----Squelette", "Squelette"));
//        taxonomy.add(new Ontology("----Système des sens et le mouvement", "Système des sens et le mouvement"));
//        taxonomy.add(new Ontology("----Tunicas", "Tunicas"));
//        taxonomy.add(new Ontology("----Uelh", "Uelh"));
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
