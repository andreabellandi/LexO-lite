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
 * @author andreabellandi
 */
public class LemmaData implements Serializable {

    private boolean saveButtonDisabled;
    private boolean deleteButtonDisabled;
    private String formWrittenRepr;
    private String PoS;
    private String language;
    private String gender;
    private String number;
    private String person;
    private String mood;
    private String voice;
    private String individual;
    private String type;
    private String note;
    private String valid;
    // if view button is true a reference has been specified, else it is false
    private Openable OWLClass = null;
    private ReferenceMenuTheme themeOWLClass;
    private boolean verified;
    private ArrayList<Word> seeAlso = new ArrayList();
    private ArrayList<Word> multiword = new ArrayList();

    // modulo vartrans
    private ArrayList<LexicalRelation> lexRels = new ArrayList();
    private ArrayList<ReifiedLexicalRelation> reifiedLexRels = new ArrayList();

    // modulo synsem
    private ArrayList<SynFrame> synFrames = new ArrayList();

    public LemmaData() {
        this.saveButtonDisabled = true;
        this.deleteButtonDisabled = false;
        this.verified = false;
    }

    public ReferenceMenuTheme getThemeOWLClass() {
        return themeOWLClass;
    }

    public void setThemeOWLClass(ReferenceMenuTheme themeOWLClass) {
        this.themeOWLClass = themeOWLClass;
    }

    public Openable getOWLClass() {
        return OWLClass;
    }

    public void setOWLClass(Openable OWLClass) {
        this.OWLClass = OWLClass;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIndividual() {
        return individual;
    }

    public void setIndividual(String individual) {
        this.individual = individual;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<Word> getMultiword() {
        return multiword;
    }

    public void setMultiword(ArrayList<Word> multiword) {
        this.multiword = multiword;
    }

    public ArrayList<Word> getSeeAlso() {
        return seeAlso;
    }

    public void setSeeAlso(ArrayList<Word> seeAlso) {
        this.seeAlso = seeAlso;
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

    public String getFormWrittenRepr() {
        return formWrittenRepr;
    }

    public void setFormWrittenRepr(String formWrittenRepr) {
        this.formWrittenRepr = formWrittenRepr;
    }

    public String getPoS() {
        return PoS;
    }

    public void setPoS(String PoS) {
        this.PoS = PoS;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ArrayList<LexicalRelation> getLexRels() {
        return lexRels;
    }

    public void setLexRels(ArrayList<LexicalRelation> lexRels) {
        this.lexRels = lexRels;
    }

    public ArrayList<ReifiedLexicalRelation> getReifiedLexRels() {
        return reifiedLexRels;
    }

    public void setReifiedLexRels(ArrayList<ReifiedLexicalRelation> reifiedLexRels) {
        this.reifiedLexRels = reifiedLexRels;
    }

    public ArrayList<SynFrame> getSynFrames() {
        return synFrames;
    }

    public void setSynFrames(ArrayList<SynFrame> synFrames) {
        this.synFrames = synFrames;
    }

    public void clear() {
        this.saveButtonDisabled = true;
        this.deleteButtonDisabled = false;
        this.formWrittenRepr = "";
        this.PoS = "";
        this.language = "";
        this.number = "";
        this.gender = "";
        this.person = "";
        this.mood = "";
        this.voice = "";
        this.type = "";
        this.individual = "";
        this.note = "";
        this.verified = false;
        this.valid = "false";
        this.multiword.clear();
        this.seeAlso.clear();
        this.lexRels.clear();
        this.reifiedLexRels.clear();
        this.synFrames.clear();
        this.OWLClass = null;
    }

    public static class Word {

        private boolean viewButtonDisabled;
        private boolean deleteButtonDisabled;
        private String writtenRep;
        private String OWLName;
        private String language;
        private String OWLComp;
        private String label;
        private ArrayList<CandidateWord> candidates = new ArrayList();

        public Word() {
            this.viewButtonDisabled = false;
            this.deleteButtonDisabled = false;
            this.writtenRep = "";
            this.OWLName = "";
            this.language = "";
            this.OWLComp = "";
            this.label = "";
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

        public String getOWLName() {
            return OWLName;
        }

        public void setOWLName(String OWLName) {
            this.OWLName = OWLName;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getOWLComp() {
            return OWLComp;
        }

        public void setOWLComp(String OWLComp) {
            this.OWLComp = OWLComp;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public ArrayList<CandidateWord> getCandidates() {
            return candidates;
        }

        public void setCandidates(ArrayList<CandidateWord> candidates) {
            this.candidates = candidates;
        }

    }

    public static class CandidateWord {

        private String writtenRep;
        private String OWLName;
        private String language;

        public CandidateWord() {
            this.writtenRep = "";
            this.OWLName = "";
            this.language = "";
        }

        public String getWrittenRep() {
            return writtenRep;
        }

        public void setWrittenRep(String writtenRep) {
            this.writtenRep = writtenRep;
        }

        public String getOWLName() {
            return OWLName;
        }

        public void setOWLName(String OWLName) {
            this.OWLName = OWLName;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }

    public static class LexicalRelation {

        private boolean viewButtonDisabled;
        private boolean deleteButtonDisabled;
        private String writtenRep;
        private String language;
        private String relation;
        private String OWLName;

        public LexicalRelation() {
            this.viewButtonDisabled = false;
            this.deleteButtonDisabled = false;
            this.writtenRep = "";
            this.language = "";
            this.relation = "";
            this.OWLName = "";
        }

        public String getOWLName() {
            return OWLName;
        }

        public void setOWLName(String OWLName) {
            this.OWLName = OWLName;
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

    public static class ReifiedLexicalRelation {

        private boolean deleteButtonDisabled;
        private boolean viewButtonDisabled;
        private String source;
        private String target;
        private String sourceWrittenRep;
        private String sourceLanguage;
        private String targetWrittenRep;
        private String targetLanguage;
        private String category;
        private String sourceOWLName;
        private String targetOWLName;

        public ReifiedLexicalRelation() {
            this.deleteButtonDisabled = false;
            this.viewButtonDisabled = false;
            this.source = "";
            this.target = "";
            this.category = "";
            this.sourceLanguage = "";
            this.sourceWrittenRep = "";
            this.targetLanguage = "";
            this.targetWrittenRep = "";
            this.sourceOWLName = "";
            this.targetOWLName = "";
        }

        public boolean isViewButtonDisabled() {
            return viewButtonDisabled;
        }

        public void setViewButtonDisabled(boolean viewButtonDisabled) {
            this.viewButtonDisabled = viewButtonDisabled;
        }

        public String getSourceOWLName() {
            return sourceOWLName;
        }

        public void setSourceOWLName(String sourceOWLName) {
            this.sourceOWLName = sourceOWLName;
        }

        public String getTargetOWLName() {
            return targetOWLName;
        }

        public void setTargetOWLName(String targetOWLName) {
            this.targetOWLName = targetOWLName;
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

    public static class SynFrame {

        private String type;
        private String name;
        private ArrayList<SynArg> synArgs = new ArrayList();
        private boolean saveButtonDisabled = true;
        private boolean newFrame = true;

        public SynFrame() {
            this.type = "";
            this.name = "";
            this.synArgs.clear();
        }

        public boolean isNewFrame() {
            return newFrame;
        }

        public void setNewFrame(boolean newFrame) {
            this.newFrame = newFrame;
        }

        public boolean isSaveButtonDisabled() {
            return saveButtonDisabled;
        }

        public void setSaveButtonDisabled(boolean saveButtonDisabled) {
            this.saveButtonDisabled = saveButtonDisabled;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<SynArg> getSynArgs() {
            return synArgs;
        }

        public void setSynArgs(ArrayList<SynArg> synArgs) {
            this.synArgs = synArgs;
        }

    }

    public static class SynArg {

        private int number;
        private String type;
        private String marker;
        private boolean optional;
        private String name;

        public SynArg() {
            this.number = 0;
            this.type = "";
            this.marker = "";
            this.optional = false;
            this.name = "";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMarker() {
            return marker;
        }

        public void setMarker(String marker) {
            this.marker = marker;
        }

        public boolean isOptional() {
            return optional;
        }

        public void setOptional(boolean optional) {
            this.optional = optional;
        }

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
