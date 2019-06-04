/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author andrea
 */
public class SenseData implements Serializable {

    private boolean saveButtonDisabled;
    private boolean deleteButtonDisabled;
    private String name;
    private String note;
    // if view button is true a reference has been specified, else it is false
    private Openable OWLClass;
//    private ArrayList<Openable> scientificName;
    private String definition;
    // translation refers to french translations
    private ArrayList<Openable> translation;
//    private ArrayList<Openable> englishTranslation;
//    private ArrayList<Openable> englishTranslationOf;
    private ArrayList<Openable> synonym;
    private ArrayList<Openable> approximateSynonym;
    private ArrayList<Openable> antonym;
    private ArrayList<Openable> hypernym;
    private ArrayList<Openable> hyponym;
//    private ArrayList<Openable> correspondence;
//    private ArrayList<Triple> invalidRelations;
    private int filedMaxLenght;

    public SenseData() {
        this.saveButtonDisabled = true;
        this.deleteButtonDisabled = false;
        this.name = "";
        this.note = "";
        this.filedMaxLenght = 0;
        this.OWLClass = new Openable();
        this.definition = "";
        this.synonym = new ArrayList();
        this.translation = new ArrayList();
//        this.englishTranslation = new ArrayList();
//        this.correspondence = new ArrayList();
//        this.englishTranslationOf = new ArrayList();
        this.approximateSynonym = new ArrayList();
        this.synonym = new ArrayList();
        this.antonym = new ArrayList();
        this.hypernym = new ArrayList();
        this.hyponym = new ArrayList();
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public ArrayList<Openable> getAntonym() {
        return antonym;
    }

    public void setAntonym(ArrayList<Openable> antonym) {
        this.antonym = antonym;
    }

    public ArrayList<Openable> getHypernym() {
        return hypernym;
    }

    public void setHypernym(ArrayList<Openable> hypernym) {
        this.hypernym = hypernym;
    }

    public ArrayList<Openable> getHyponym() {
        return hyponym;
    }

    public void setHyponym(ArrayList<Openable> hyponym) {
        this.hyponym = hyponym;
    }

    public boolean isSaveButtonDisabled() {
        return saveButtonDisabled;
    }

    public void setSaveButtonDisabled(boolean saveButtonDisabled) {
        this.saveButtonDisabled = saveButtonDisabled;
    }

    public boolean isDeleteButtonDisabled() {
        return deleteButtonDisabled;
    }

    public void setDeleteButtonDisabled(boolean deleteButtonDisabled) {
        this.deleteButtonDisabled = deleteButtonDisabled;
    }

    public Openable getOWLClass() {
        return OWLClass;
    }

    public void setOWLClass(Openable OWLClass) {
        this.OWLClass = OWLClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<Openable> getApproximateSynonym() {
        return approximateSynonym;
    }

    public void setApproximateSynonym(ArrayList<Openable> approximateSynonym) {
        this.approximateSynonym = approximateSynonym;
    }

//    public ArrayList<Openable> getEnglishTranslationOf() {
//        return englishTranslationOf;
//    }
//
//    public void setEnglishTranslationOf(ArrayList<Openable> englishTranslationOf) {
//        this.englishTranslationOf = englishTranslationOf;
//    }
//
//    public ArrayList<Openable> getEnglishTranslation() {
//        return englishTranslation;
//    }
//
//    public void setEnglishTranslation(ArrayList<Openable> englishTranslation) {
//        this.englishTranslation = englishTranslation;
//    }
//
//    public ArrayList<Openable> getScientificName() {
//        return scientificName;
//    }
//
//    public void setScientificName(ArrayList<Openable> scientificName) {
//        this.scientificName = scientificName;
//    }
    public ArrayList<Openable> getTranslation() {
        return translation;
    }

    public void setTranslation(ArrayList<Openable> translation) {
        this.translation = translation;
    }

    public ArrayList<Openable> getSynonym() {
        return synonym;
    }

    public void setSynonym(ArrayList<Openable> synonym) {
        this.synonym = synonym;
    }

//    public ArrayList<Openable> getCorrespondence() {
//        return correspondence;
//    }
//
//    public void setCorrespondence(ArrayList<Openable> correspondence) {
//        this.correspondence = correspondence;
//    }
    public int getFiledMaxLenght() {
        return filedMaxLenght;
    }

    public void setFiledMaxLenght(int filedMaxLenght) {
        this.filedMaxLenght = filedMaxLenght;
    }

    public static class Openable {

        private boolean viewButtonDisabled;
        private boolean deleteButtonDisabled;
        private String name;

        public Openable() {
            this.viewButtonDisabled = false;
            this.deleteButtonDisabled = false;
            this.name = "";
        }

        public boolean isViewButtonDisabled() {
            return viewButtonDisabled;
        }

        public void setViewButtonDisabled(boolean viewButtonDisabled) {
            this.viewButtonDisabled = viewButtonDisabled;
        }

        public boolean isDeleteButtonDisabled() {
            return deleteButtonDisabled;
        }

        public void setDeleteButtonDisabled(boolean deleteButtonDisabled) {
            this.deleteButtonDisabled = deleteButtonDisabled;
        }

        public String getName() {
            name = name.replaceAll("_APOS_", "'");
            name = name.replaceAll("OB_", "(");
            name = name.replaceAll("_CB", ")");
            name = name.replaceAll("OSB_", "[");
            name = name.replaceAll("_CSB", "]");
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
