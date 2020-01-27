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
    private ArrayList<Openable> synonym;
    private ArrayList<Openable> approximateSynonym;
    private ArrayList<Openable> antonym;
    private ArrayList<Openable> hypernym;
    private ArrayList<Openable> hyponym;
    private int filedMaxLenght;

    // modulo vartrans
    private ArrayList<SenseData.SenseRelation> senseRels;
    private ArrayList<SenseData.ReifiedSenseRelation> reifiedSenseRels;
    private ArrayList<SenseData.ReifiedTranslationRelation> reifiedTranslationRels;

    // modulo vartrans
    private ArrayList<SenseData.OntoMap> ontoMaps;

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
        this.approximateSynonym = new ArrayList();
        this.synonym = new ArrayList();
        this.antonym = new ArrayList();
        this.hypernym = new ArrayList();
        this.hyponym = new ArrayList();
        this.senseRels = new ArrayList();
        this.reifiedSenseRels = new ArrayList();
        this.reifiedTranslationRels = new ArrayList();
        this.ontoMaps = new ArrayList();
    }

    public ArrayList<OntoMap> getOntoMaps() {
        return ontoMaps;
    }

    public void setOntoMaps(ArrayList<OntoMap> ontoMaps) {
        this.ontoMaps = ontoMaps;
    }

    public ArrayList<ReifiedTranslationRelation> getReifiedTranslationRels() {
        return reifiedTranslationRels;
    }

    public void setReifiedTranslationRels(ArrayList<ReifiedTranslationRelation> reifiedTranslationRels) {
        this.reifiedTranslationRels = reifiedTranslationRels;
    }

    public ArrayList<SenseRelation> getSenseRels() {
        return senseRels;
    }

    public void setSenseRels(ArrayList<SenseRelation> senseRels) {
        this.senseRels = senseRels;
    }

    public ArrayList<ReifiedSenseRelation> getReifiedSenseRels() {
        return reifiedSenseRels;
    }

    public void setReifiedSenseRels(ArrayList<ReifiedSenseRelation> reifiedSenseRels) {
        this.reifiedSenseRels = reifiedSenseRels;
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

    public static class SenseRelation {

        private boolean viewButtonDisabled;
        private boolean deleteButtonDisabled;
        private String writtenRep;
        private String language;
        private String relation;

        public SenseRelation() {
            this.viewButtonDisabled = false;
            this.deleteButtonDisabled = false;
            this.writtenRep = "";
            this.language = "";
            this.relation = "";
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

        public String getWrittenRep() {
            return writtenRep;
        }

        public void setWrittenRep(String writtenRep) {
            this.writtenRep = writtenRep;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }

    }

    public static class ReifiedSenseRelation {

        private boolean viewButtonDisabled;
        private boolean deleteButtonDisabled;
        private String source;
        private String target;
        private String sourceWrittenRep;
        private String sourceLanguage;
        private String targetWrittenRep;
        private String targetLanguage;
        private String category;

        public ReifiedSenseRelation() {
            this.viewButtonDisabled = false;
            this.deleteButtonDisabled = false;
            this.source = "";
            this.target = "";
            this.category = "";
            this.sourceLanguage = "";
            this.sourceWrittenRep = "";
            this.targetLanguage = "";
            this.targetWrittenRep = "";
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

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getSourceWrittenRep() {
            return sourceWrittenRep;
        }

        public void setSourceWrittenRep(String sourceWrittenRep) {
            this.sourceWrittenRep = sourceWrittenRep;
        }

        public String getSourceLanguage() {
            return sourceLanguage;
        }

        public void setSourceLanguage(String sourceLanguage) {
            this.sourceLanguage = sourceLanguage;
        }

        public String getTargetWrittenRep() {
            return targetWrittenRep;
        }

        public void setTargetWrittenRep(String targetWrittenRep) {
            this.targetWrittenRep = targetWrittenRep;
        }

        public String getTargetLanguage() {
            return targetLanguage;
        }

        public void setTargetLanguage(String targetLanguage) {
            this.targetLanguage = targetLanguage;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

    }

    public static class ReifiedTranslationRelation {

        private boolean viewButtonDisabled;
        private boolean deleteButtonDisabled;
        private String source;
        private String target;
        private String sourceWrittenRep;
        private String sourceLanguage;
        private String targetWrittenRep;
        private String targetLanguage;
        private String category;

        public ReifiedTranslationRelation() {
            this.viewButtonDisabled = false;
            this.deleteButtonDisabled = false;
            this.source = "";
            this.target = "";
            this.category = "";
            this.sourceLanguage = "";
            this.sourceWrittenRep = "";
            this.targetLanguage = "";
            this.targetWrittenRep = "";
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

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getSourceWrittenRep() {
            return sourceWrittenRep;
        }

        public void setSourceWrittenRep(String sourceWrittenRep) {
            this.sourceWrittenRep = sourceWrittenRep;
        }

        public String getSourceLanguage() {
            return sourceLanguage;
        }

        public void setSourceLanguage(String sourceLanguage) {
            this.sourceLanguage = sourceLanguage;
        }

        public String getTargetWrittenRep() {
            return targetWrittenRep;
        }

        public void setTargetWrittenRep(String targetWrittenRep) {
            this.targetWrittenRep = targetWrittenRep;
        }

        public String getTargetLanguage() {
            return targetLanguage;
        }

        public void setTargetLanguage(String targetLanguage) {
            this.targetLanguage = targetLanguage;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

    }

    public static class OntoMap {

        private String sense;
        private String reference;
        private String isA;
        private String subjOfProp;
        private String objOfProp;

        public OntoMap() {
            this.sense = "";
            this.reference = "";
            this.isA = "";
            this.subjOfProp = "";
            this.objOfProp = "";
        }

        public String getSense() {
            return sense;
        }

        public void setSense(String sense) {
            this.sense = sense;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getIsA() {
            return isA;
        }

        public void setIsA(String isA) {
            this.isA = isA;
        }

        public String getSubjOfProp() {
            return subjOfProp;
        }

        public void setSubjOfProp(String subjOfProp) {
            this.subjOfProp = subjOfProp;
        }

        public String getObjOfProp() {
            return objOfProp;
        }

        public void setObjOfProp(String objOfProp) {
            this.objOfProp = objOfProp;
        }

    }
}
