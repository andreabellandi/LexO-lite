/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Level;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerLexicalEntryDetail extends BaseController implements Serializable {

    @Inject
    private PropertyValue propertyValue;
    @Inject
    private LoginController loginController;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LexiconControllerTabViewList lexiconCreationControllerTabViewList;

    private String entryType;
    private String language;
    private String lelabel;
    private String writtenRep;
    private String pos;
    private String formType;
    private boolean createSense;

    private boolean saveDisabled = true;

    public boolean isSaveDisabled() {
        return saveDisabled;
    }

    public void setSaveDisabled(boolean saveDisabled) {
        this.saveDisabled = saveDisabled;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLelabel() {
        return lelabel;
    }

    public void setLelabel(String lelabel) {
        this.lelabel = lelabel;
    }

    public String getWrittenRep() {
        return writtenRep;
    }

    public void setWrittenRep(String writtenRep) {
        this.writtenRep = writtenRep;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public boolean isCreateSense() {
        return createSense;
    }

    public void setCreateSense(boolean createSense) {
        this.createSense = createSense;
    }

    public ArrayList<String> getLingCatList() {
        return propertyValue.getLingCatList();
    }

    public ArrayList<String> getWPoSList() {
        if (propertyValue != null) {
            return propertyValue.getPoS(entryType);
        } else {
            return new ArrayList<>();
        }
    }

    public ArrayList<String> getLexicaEntryTypeList() {
        return propertyValue.getLexicalEntryType();
    }

    public void languageChanged(AjaxBehaviorEvent e) {
        String lang = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE language of new entry to " + lang);
        language = lang;
        ckeckLexicalEntrySavability();
    }

    public void entryTypeChanged(AjaxBehaviorEvent e) {
        String type = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE type of new lexical entry to " + type);
        entryType = type;
        ckeckLexicalEntrySavability();
    }

    public void posChanged(AjaxBehaviorEvent e) {
        String lePos = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE pos of new lexical entry to " + lePos);
        pos = lePos;
        ckeckLexicalEntrySavability();
    }

    private void ckeckLexicalEntrySavability() {
        if (!entryType.isEmpty() && !language.isEmpty() && !writtenRep.isEmpty() && !pos.isEmpty()) {
            saveDisabled = false;
        } else {
            saveDisabled = true;
        }
    }

    public void lexicalEntryLabelKeyupEvent(AjaxBehaviorEvent e) {
        String label = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE label of new lexical entry to " + label);
    }

    public ArrayList<String> getLexicaLanguages() {
        ArrayList<String> al = new ArrayList();
        for (String lang : lexiconManager.lexicaLanguagesList()) {
            al.add(lang);
        }
        Collections.sort(al);
        return al;
    }

    public void save() throws IOException, OWLOntologyStorageException {
//        if (!langName.isEmpty()) {
//            lexiconManager.saveNewLanguage(langName, uriCode, linguisticCatalog, description, creator);
//            log(Level.INFO, loginController.getAccount(), "SAVE new language " + langName);
//            lexiconCreationControllerTabViewList.updateLexiconLanguagesList();
//            FacesMessage message = new FacesMessage("Successful", "language " + langName + " was created.");
//            FacesContext.getCurrentInstance().addMessage(null, message);
//        } else {
//            log(Level.INFO, loginController.getAccount(), "Negated creation of an empty language ");
//            warn("template.message.emptyLangCreation.summary", "template.message.emptyLangCreation.description");
//            FacesMessage message = new FacesMessage("Successful", "Negated creation of an empty language");
//            FacesContext.getCurrentInstance().addMessage(null, message);
//        }
    }

    public void clear() {
        this.createSense = false;
        this.entryType = "";
        this.formType = "";
        this.language = "";
        this.lelabel = "";
        this.pos = "";
        this.writtenRep = "";
    }

}
