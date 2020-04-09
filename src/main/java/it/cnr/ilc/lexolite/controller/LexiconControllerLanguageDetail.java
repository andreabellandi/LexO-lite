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
public class LexiconControllerLanguageDetail extends BaseController implements Serializable {

    @Inject
    private PropertyValue propertyValue;
    @Inject
    private LoginController loginController;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LexiconControllerTabViewList lexiconCreationControllerTabViewList;

    private String langName;
    private String uriCode;
    private String linguisticCatalog;
    private String description;
    private String creator;

    public String getLangName() {
        return langName;
    }

    public void setLangName(String langName) {
        this.langName = langName;
    }

    public String getUriCode() {
        return uriCode;
    }

    public void setUriCode(String uriCode) {
        this.uriCode = uriCode;
    }

    public String getLinguisticCatalog() {
        return linguisticCatalog;
    }

    public void setLinguisticCatalog(String linguisticCatalog) {
        this.linguisticCatalog = linguisticCatalog;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ArrayList<String> getLingCatList() {
        return propertyValue.getLingCatList();
    }

    public void langNameKeyupEvent(AjaxBehaviorEvent e) {
        String value = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE language name to " + value);
    }

    public void langUriKeyupEvent(AjaxBehaviorEvent e) {
        String value = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE language uri to " + value);
    }

    public void langDescriptionKeyupEvent(AjaxBehaviorEvent e) {
        String value = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE language description to " + value);
    }

    public void langCreatorKeyupEvent(AjaxBehaviorEvent e) {
        String value = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE language creator to " + value);
    }

    public void save() throws IOException, OWLOntologyStorageException {
        if (!langName.isEmpty()) {
            lexiconManager.saveNewLanguage(langName, uriCode, linguisticCatalog, description, creator);
            log(Level.INFO, loginController.getAccount(), "SAVE new language " + langName);
            lexiconCreationControllerTabViewList.updateLexiconLanguagesList();
            FacesMessage message = new FacesMessage("Successful", "language " + langName + " was created.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } else {
            log(Level.INFO, loginController.getAccount(), "Negated creation of an empty language ");
            warn("template.message.emptyLangCreation.summary", "template.message.emptyLangCreation.description");
            FacesMessage message = new FacesMessage("Successful", "Negated creation of an empty language");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void clear() {
        this.creator = "";
        this.description = "";
        this.langName = "";
        this.uriCode = "";
        this.linguisticCatalog = "";
    }

}
