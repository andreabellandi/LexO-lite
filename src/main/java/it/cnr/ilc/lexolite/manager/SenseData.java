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
    private ReferenceMenuTheme themeOWLClass;
    // private ArrayList<Openable> scientificName;
    private String definition;
    private int filedMaxLenght;

    // modulo vartrans
    private ArrayList<SenseData.SenseRelation> senseRels;
    private ArrayList<SenseData.ReifiedSenseRelation> reifiedSenseRels;
    private ArrayList<SenseData.ReifiedTranslationRelation> reifiedTranslationRels;

    // modulo synsem
    private SenseData.OntoMap ontoMap;
    private ArrayList<SenseData.OntoMap> subOntoMap;

    public SenseData() {
        this.saveButtonDisabled = true;
        this.deleteButtonDisabled = false;
        this.name = "";
        this.note = "";
        this.filedMaxLenght = 0;
        this.OWLClass = new Openable();
        this.themeOWLClass = new ReferenceMenuTheme();
        this.themeOWLClass.setName("");
        this.definition = "";
        this.senseRels = new ArrayList();
        this.reifiedSenseRels = new ArrayList();
        this.reifiedTranslationRels = new ArrayList();
        this.ontoMap = null;
        this.subOntoMap = new ArrayList();
    }

    public ReferenceMenuTheme getThemeOWLClass() {
        return themeOWLClass;
    }

    public void setThemeOWLClass(ReferenceMenuTheme themeOWLClass) {
        this.themeOWLClass = themeOWLClass;
    }

    public ArrayList<OntoMap> getSubOntoMap() {
        return subOntoMap;
    }

    public void setSubOntoMap(ArrayList<OntoMap> subOntoMap) {
        this.subOntoMap = subOntoMap;
    }

    public OntoMap getOntoMap() {
        return ontoMap;
    }

    public void setOntoMap(OntoMap ontoMap) {
        this.ontoMap = ontoMap;
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
        private double confidence;

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
            this.confidence = 1;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
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

        private String frame;
        private String reference;
        private String isA;
        private String subjOfProp;
        private String objOfProp;

        public OntoMap() {
            this.frame = "";
            this.reference = "";
            this.isA = "";
            this.subjOfProp = "";
            this.objOfProp = "";
        }

        public String getFrame() {
            return frame;
        }

        public void setFrame(String frame) {
            this.frame = frame;
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
