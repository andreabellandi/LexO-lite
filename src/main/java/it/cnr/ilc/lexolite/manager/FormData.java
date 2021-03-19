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
public class FormData implements Serializable {

    private boolean saveButtonDisabled;
    private boolean deleteButtonDisabled;
    private boolean addFormdisabled;

    private String formWrittenRepr;
    private String formPhoneticRep;
    private String language;
    private String individual;
    private String note;

    private ArrayList<LemmaData.MorphoTrait> morphoTraits = new ArrayList();

    // attribute extension
    private ArrayList<LemmaData.ExtensionAttributeIstance> extensionAttributeInstances = new ArrayList();

    public FormData() {
        this.saveButtonDisabled = true;
        this.deleteButtonDisabled = false;
        this.note = "";
        this.language = "";
        this.individual = "";
        this.note = "";
        this.morphoTraits.clear();
        this.formPhoneticRep = "No entry found";
    }

    public ArrayList<LemmaData.ExtensionAttributeIstance> getExtensionAttributeInstances() {
        return extensionAttributeInstances;
    }

    public void setExtensionAttributeInstances(ArrayList<LemmaData.ExtensionAttributeIstance> extensionAttributeInstances) {
        this.extensionAttributeInstances = extensionAttributeInstances;
    }

    public ArrayList<LemmaData.MorphoTrait> getMorphoTraits() {
        return morphoTraits;
    }

    public void setMorphoTraits(ArrayList<LemmaData.MorphoTrait> morphoTraits) {
        this.morphoTraits = morphoTraits;
    }

    public String getIndividual() {
        return individual;
    }

    public void setIndividual(String individual) {
        this.individual = individual;
    }

    public String getFormPhoneticRep() {
        return formPhoneticRep;
    }

    public void setFormPhoneticRep(String formPhoneticRep) {
        this.formPhoneticRep = formPhoneticRep;
    }

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
