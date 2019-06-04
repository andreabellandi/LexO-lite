/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import java.io.Serializable;

/**
 *
 * @author andreabellandi
 */
public class FormData implements Serializable {

    private boolean saveButtonDisabled;
    private boolean deleteButtonDisabled;
    private boolean addFormdisabled;

    private String formWrittenRepr;
//    private String PoS;
    private String gender;
    private String number;
    private String person;
    private String mood;
    private String voice;
//    private String transliteration;
//    private String transilterationType;
//    private String alphabet;
    private String language;
    private String individual;
    private String note;
//    private String usedIn;
//    private LemmaData.Word linguisticTypeEntry = new Word();

    // form types are curently hard-coded values!! TODO: generalize !!
//    private boolean alphabeticalType;
//    private boolean graphicType;
//    private boolean morphologicalType;
//    private boolean graphophoneticType;
//    private boolean morphosyntacticType;
//    private boolean bilingualType;
//    private boolean unspecifiedType;
//    private boolean linguisticType;

//    private ArrayList<LemmaData.Word> documentedIn = new ArrayList();
//    private ArrayList<InternalAttestationData> internalAttestation = new ArrayList();

    public FormData() {
        this.saveButtonDisabled = true;
        this.deleteButtonDisabled = false;
//        this.alphabeticalType = false;
//        this.graphophoneticType = false;
//        this.morphologicalType = false;
//        this.graphophoneticType = false;
//        this.bilingualType = false;
//        this.morphosyntacticType = false;
//        this.unspecifiedType = false;
//        this.linguisticType = false;
//        this.transilterationType = "none";
//        this.transliteration = "";
        this.note = "";
        this.language = "";
        this.number = "";
        this.gender = "";
        this.person = "";
        this.mood = "";
        this.voice = "";
        this.individual = "";
        this.note = "";
//        this.usedIn = "";
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

//    public LemmaData.Word getLinguisticTypeEntry() {
//        return linguisticTypeEntry;
//    }
//
//    public void setLinguisticTypeEntry(LemmaData.Word linguisticTypeEntry) {
//        this.linguisticTypeEntry = linguisticTypeEntry;
//    }
//
//    public ArrayList<InternalAttestationData> getInternalAttestation() {
//        return internalAttestation;
//    }
//
//    public void setInternalAttestation(ArrayList<InternalAttestationData> internalAttestation) {
//        this.internalAttestation = internalAttestation;
//    }
//
//    public String getTransliteration() {
//        return transliteration;
//    }
//
//    public void setTransliteration(String transliteration) {
//        this.transliteration = transliteration;
//    }
//
//    public boolean isLinguisticType() {
//        return linguisticType;
//    }
//
//    public void setLinguisticType(boolean linguisticType) {
//        this.linguisticType = linguisticType;
//    }

    public String getIndividual() {
        return individual;
    }

    public void setIndividual(String individual) {
        this.individual = individual;
    }

//    public String getTransilterationType() {
//        return transilterationType;
//    }
//
//    public void setTransilterationType(String transilterationType) {
//        this.transilterationType = transilterationType;
//    }
//
//    public ArrayList<LemmaData.Word> getDocumentedIn() {
//        return documentedIn;
//    }
//
//    public void setDocumentedIn(ArrayList<LemmaData.Word> documentedIn) {
//        this.documentedIn = documentedIn;
//    }
//
//    public String getAlphabet() {
//        return alphabet;
//    }
//
//    public void setAlphabet(String alphabet) {
//        this.alphabet = alphabet;
//    }
//
//    public String getUsedIn() {
//        return usedIn;
//    }
//
//    public void setUsedIn(String usedIn) {
//        this.usedIn = usedIn;
//    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isAddFormdisabled() {
        return addFormdisabled;
    }

    public void setAddFormdisabled(boolean addFormdisabled) {
        this.addFormdisabled = addFormdisabled;
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

//    public String getPoS() {
//        return PoS;
//    }
//
//    public void setPoS(String PoS) {
//        this.PoS = PoS;
//    }

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

//    public boolean isAlphabeticalType() {
//        return alphabeticalType;
//    }
//
//    public void setAlphabeticalType(boolean alphabeticalType) {
//        this.alphabeticalType = alphabeticalType;
//    }
//
//    public boolean isGraphicType() {
//        return graphicType;
//    }
//
//    public void setGraphicType(boolean graphicType) {
//        this.graphicType = graphicType;
//    }
//
//    public boolean isMorphologicalType() {
//        return morphologicalType;
//    }
//
//    public void setMorphologicalType(boolean morphologicalType) {
//        this.morphologicalType = morphologicalType;
//    }
//
//    public boolean isGraphophoneticType() {
//        return graphophoneticType;
//    }
//
//    public void setGraphophoneticType(boolean graphophoneticType) {
//        this.graphophoneticType = graphophoneticType;
//    }
//
//    public boolean isMorphosyntacticType() {
//        return morphosyntacticType;
//    }
//
//    public void setMorphosyntacticType(boolean morphosyntacticType) {
//        this.morphosyntacticType = morphosyntacticType;
//    }
//
//    public boolean isBilingualType() {
//        return bilingualType;
//    }
//
//    public void setBilingualType(boolean bilingualType) {
//        this.bilingualType = bilingualType;
//    }
//
//    public boolean isUnspecifiedType() {
//        return unspecifiedType;
//    }
//
//    public void setUnspecifiedType(boolean unspecifiedType) {
//        this.unspecifiedType = unspecifiedType;
//    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
