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
    private boolean verified;
    private ArrayList<Word> seeAlso = new ArrayList();
    private ArrayList<Word> multiword = new ArrayList();

    public LemmaData() {
        this.saveButtonDisabled = true;
        this.deleteButtonDisabled = false;
        this.verified = false;
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
}
