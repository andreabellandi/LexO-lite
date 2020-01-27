/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.manager.LemmaData;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.OntologyManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import it.cnr.ilc.lexolite.manager.PropertyValue.Ontology;
import it.cnr.ilc.lexolite.manager.SenseData;
import it.cnr.ilc.lexolite.manager.SenseData.Openable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
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
public class LexiconControllerSenseDetail extends BaseController implements Serializable {

    @Inject
    private LexiconControllerFormDetail lexiconCreationControllerFormDetail;
    @Inject
    private LexiconControllerLinkedLexicalEntryDetail lexiconCreationControllerRelationDetail;
    @Inject
    private LexiconControllerTabViewList lexiconControllerTabViewList;
    @Inject
    private LexiconControllerVarTransSenseDetail lexiconControllerVarTransSenseDetail;
    @Inject
    private OntologyManager ontologyManager;
    @Inject
    private PropertyValue propertyValue;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LoginController loginController;

    private final List<SenseData> senses = new ArrayList<>();
    private final List<SenseData> sensesCopy = new ArrayList<>();
    private String senseOpenedInRelation;
    private boolean addSenseButtonDisabled = true;
    private boolean senseToolbarRendered = false;
    private boolean undeletableSense = true;

    private boolean verified = false;

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    private boolean senseRendered = false;
    private boolean locked = false;

    public boolean isSenseRendered() {
        return senseRendered;
    }

    public void setSenseRendered(boolean senseRendered) {
        this.senseRendered = senseRendered;
    }

    public List<SenseData> getSenses() {
        return senses;
    }

    public boolean isAddSenseButtonDisabled() {
        return addSenseButtonDisabled;
    }

    public void setAddSenseButtonDisabled(boolean addSenseButtonDisabled) {
        this.addSenseButtonDisabled = addSenseButtonDisabled;
    }

    public boolean isSenseToolbarRendered() {
        return senseToolbarRendered;
    }

    public void setSenseToolbarRendered(boolean senseToolbarRendered) {
        this.senseToolbarRendered = senseToolbarRendered;
    }

    public boolean isUndeletableSense() {
        return undeletableSense;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isNewAction() {
        return lexiconCreationControllerFormDetail.isNewAction();
    }

    public String emptyMessage(String text, String emptyMessage) {
        return emptyMessage(text, text, emptyMessage);
    }

    public String emptyMessage(String test, String text, String emptyMessage) {
        return test == null || test.equals("") ? emptyMessage : text;
    }

    public void openNote(String senseName) {
        log(Level.INFO, loginController.getAccount(), "OPEN note of Sense " + senseName);
    }

    public void saveNote(SenseData sd) throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "EDIT note of Sense " + sd.getName() + " in " + sd.getNote());
        int order = senses.indexOf(sd);
        lexiconManager.saveSenseNote(sd, sensesCopy.get(order).getNote());
        sensesCopy.get(order).setNote(sd.getNote());
        info("template.message.saveSenseNote.summary", "template.message.saveSenseNote.description");
    }

    public void closeNote(String senseName) {
        log(Level.INFO, loginController.getAccount(), "CLOSE note of Sense " + senseName);
    }

    public String getCommentIcon(SenseData sd) {
        if (sd.getNote().length() > 0) {
            return "fa fa-comment";
        } else {
            return "fa fa-comment-o";
        }
    }

    public List<SenseData> getSensesCopy() {
        return sensesCopy;
    }

    private void addSenseCopy(SenseData sd) {
        sensesCopy.add(copySenseData(sd));
    }

    private void updateSenseCopy(SenseData sd, int order) {
        SenseData updatedSense = copySenseData(sd);
        sensesCopy.remove(order);
        sensesCopy.add(order, updatedSense);
    }

    private void addSenseCopy(ArrayList<SenseData> sdList) {
        sensesCopy.clear();
        for (SenseData sd : sdList) {
            sensesCopy.add(copySenseData(sd));
        }
    }

    private SenseData copySenseData(SenseData sd) {
        SenseData _sd = new SenseData();
        ArrayList<Openable> syns = new ArrayList();
        ArrayList<Openable> appSyns = new ArrayList();
        ArrayList<Openable> hypes = new ArrayList();
        ArrayList<Openable> hypos = new ArrayList();
        ArrayList<Openable> ants = new ArrayList();
        ArrayList<Openable> translations = new ArrayList();
        Openable _OWLClass = new Openable();
        _OWLClass.setDeleteButtonDisabled(sd.getOWLClass().isDeleteButtonDisabled());
        _OWLClass.setViewButtonDisabled(sd.getOWLClass().isViewButtonDisabled());
        _OWLClass.setName(sd.getOWLClass().getName());
        _sd.setOWLClass(_OWLClass);
        _sd.setName(sd.getName());
        _sd.setNote(sd.getNote());
        for (Openable syn : sd.getSynonym()) {
            Openable _syn = new Openable();
            _syn.setName(syn.getName());
            syns.add(_syn);
        }
        for (Openable trans : sd.getTranslation()) {
            Openable _trans = new Openable();
            _trans.setName(trans.getName());
            translations.add(_trans);
        }
        for (Openable ant : sd.getAntonym()) {
            Openable _ant = new Openable();
            _ant.setName(ant.getName());
            ants.add(_ant);
        }
        for (Openable hypo : sd.getHyponym()) {
            Openable _hypo = new Openable();
            _hypo.setName(hypo.getName());
            hypos.add(_hypo);
        }
        for (Openable hype : sd.getHypernym()) {
            Openable _hype = new Openable();
            _hype.setName(hype.getName());
            hypes.add(_hype);
        }
        for (Openable appSyn : sd.getApproximateSynonym()) {
            Openable _appSyn = new Openable();
            _appSyn.setName(appSyn.getName());
            appSyns.add(_appSyn);
        }
        _sd.setSynonym(syns);
        _sd.setAntonym(ants);
        _sd.setApproximateSynonym(appSyns);
        _sd.setHypernym(hypes);
        _sd.setHyponym(hypos);
        _sd.setTranslation(translations);
        return _sd;
    }

    public void senseDefinitionKeyupEvent(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        SenseData sd = (SenseData) component.getAttributes().get("sense");
        String sensePart = ((String) e.getComponent().getAttributes().get("value"));
        log(Level.INFO, loginController.getAccount(), "UPDATE Sense definition of " + sd.getName() + " in " + sensePart);
        if (!sensePart.isEmpty()) {
            sd.setSaveButtonDisabled(false);
        }
    }

    public void removeDefinition(SenseData sd) {
        log(Level.INFO, loginController.getAccount(), "REMOVE " + sd.getDefinition() + " (scientific name of " + sd.getName() + ")");
        sd.setDefinition("");
        sd.setSaveButtonDisabled(false);
    }

    public void addSense() {
        SenseData sd = new SenseData();
        sd.setName("automatically set");
        sd.setSaveButtonDisabled(false);
        senses.add(sd);
    }

    public void addSenseRelation(SenseData sd, String relType) {
        log(Level.INFO, loginController.getAccount(), "ADD empty " + relType + " relation to " + sd.getName());
        SenseData.Openable sdo = new SenseData.Openable();
        sdo.setViewButtonDisabled(true);
        switch (relType) {
            case "synonym":
                sd.getSynonym().add(sdo);
                break;
            case "translation":
                sd.getTranslation().add(sdo);
                break;
            case "antonym":
                sd.getAntonym().add(sdo);
                break;
            case "hypernym":
                sd.getHypernym().add(sdo);
                break;
            case "hyponym":
                sd.getHyponym().add(sdo);
                break;
            case "approximateSynonym":
                sd.getApproximateSynonym().add(sdo);
                break;
            case "reference":
                sd.setOWLClass(sdo);
                break;
            default:
        }
    }

    public void addSense(String entry, String entryType) {
        senses.clear();
        sensesCopy.clear();
        ArrayList<SenseData> al = null;
        switch (entryType) {
            case "Lemma":
                al = lexiconManager.getSensesOfLemma(entry);
                break;
            case "Form":
                al = lexiconManager.getSensesOfForm(entry);
                break;
            // it never happens because currently the "Sense" tab panel does not exist
            case "Sense":
                al = lexiconManager.getSenses(entry);
                break;
        }
        senses.addAll(al);
        addSenseCopy(al);
    }

    public void removeSense(SenseData sd) throws IOException, OWLOntologyStorageException {
        if ((senses.indexOf(sd) == 0) && (senses.indexOf(sd) != -1)) {
            // default sense can't be deleted !
            log(Level.INFO, loginController.getAccount(), "Negated Deletion of Default Sense " + sd.getName());
            warn("template.message.deleteSenseDenied.summary", "template.message.deleteSenseDenied.description", sd.getName());
        } else {
            if ((senses.indexOf(sd) > 0) && (senses.indexOf(sd) != -1)) {
                // it sets all sense relations as invalid (to be deleted)
                log(Level.INFO, loginController.getAccount(), "DELETE Sense " + sd.getName());
                lexiconManager.deleteSense(sd);
                info("template.message.deleteSense.summary", "template.message.deleteSense.description", sd.getName());
                if (senseOpenedInRelation != null) {
                    if (sd.getName().equals(senseOpenedInRelation)) {
                        lexiconCreationControllerRelationDetail.resetRelationDetails();
                        lexiconCreationControllerRelationDetail.setRelationLemmaRendered(false);
                    }
                }
                // remove the sense box
                senses.remove(sd);
            }
        }
    }

    public List<String> completeText(String sense) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        String relType = (String) component.getAttributes().get("Relation");
        String currentSense = (String) component.getAttributes().get("currentSense");
        List<Map<String, String>> al = null;

        if (relType.equals("synonym") || relType.equals("antonym") || relType.equals("hypernym")
                || relType.equals("hyponym") || relType.equals("approximateSynonym")) {
            al = lexiconManager.sensesList("All languages");
            return getFilteredList(al, sense, currentSense, relType);
        } else {
            if (relType.equals("translation")) {
                al = lexiconManager.sensesList("All languages");
                return getFilteredList(al, sense, currentSense, relType);
            } else {
                if (relType.equals("ontoRef")) {
                    return getFilteredClasses(ontologyManager.classesList(), sense);
                }
            }
        }

        return null;
    }

    public void onRelationTargetSelect(SenseData sd, SenseData.Openable sdo) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        String relType = (String) component.getAttributes().get("Relation");
        log(Level.INFO, loginController.getAccount(), "ADD Sense " + sdo.getName() + " as " + relType + " of the sense " + sd.getName());
        sd.setSaveButtonDisabled(false);
        sdo.setDeleteButtonDisabled(false);
        sdo.setViewButtonDisabled(false);
    }

    private List<String> getFilteredList(List<Map<String, String>> list, String keyFilter, String currentSense, String relType) {
        List<String> filteredList = new ArrayList();
        Collections.sort(list, new LexiconComparator("writtenRep"));
        for (Map<String, String> m : list) {
            String wr = m.get("writtenRep");
            if ((wr.startsWith(keyFilter)) && (!wr.isEmpty())) {
                if (relType.equals("translation")) {
                    if (!wr.equals(currentSense) && (!lexiconControllerTabViewList.getLexiconLanguage().equals(m.get("lang")))) {
                        filteredList.add(wr + "@" + m.get("lang"));
                    }
                } else {
                    if (!wr.equals(currentSense)) {
                        filteredList.add(wr + "@" + m.get("lang"));
                    }
                }
            }
        }
        return filteredList;
    }

    private List<String> getFilteredClasses(List<String> list, String keyFilter) {
        List<String> filteredList = new ArrayList();
        Collections.sort(list);
        for (String clazz : list) {
            if (clazz.toLowerCase().startsWith(keyFilter.toLowerCase())) {
                filteredList.add(clazz);
            }
        }
        return filteredList;
    }

    public void saveSenseRelation(SenseData sd) throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "SAVE Sense " + sd.getName());
        int order = senses.indexOf(sd);
        lexiconManager.saveSenseRelation(sensesCopy.get(order), sd);
        updateSenseCopy(sd, order);
        sd.setSaveButtonDisabled(true);
        setSenseToolbarRendered(true);
        info("template.message.saveSenseRelation.summary", "template.message.saveSenseRelation.description", sd.getName());
    }

    public void removeSenseRelation(SenseData sd, SenseData.Openable sdo, String relType) {
        switch (relType) {
            case "synonym":
                log(Level.INFO, loginController.getAccount(), "REMOVE " + (sdo.getName().isEmpty() ? " empty synonym" : sdo.getName()) + " (synonym of " + sd.getName() + ")");
                sd.getSynonym().remove(sdo);
                break;
            case "translation":
                log(Level.INFO, loginController.getAccount(), "REMOVE " + (sdo.getName().isEmpty() ? " empty translation" : sdo.getName()) + " (translation of " + sd.getName() + ")");
                sd.getTranslation().remove(sdo);
                break;
            case "antonym":
                log(Level.INFO, loginController.getAccount(), "REMOVE " + (sdo.getName().isEmpty() ? " empty antonym" : sdo.getName()) + " (antonym of " + sd.getName() + ")");
                sd.getAntonym().remove(sdo);
                break;
            case "hypernym":
                log(Level.INFO, loginController.getAccount(), "REMOVE " + (sdo.getName().isEmpty() ? " empty hypernym" : sdo.getName()) + " (hypernym of " + sd.getName() + ")");
                sd.getHypernym().remove(sdo);
                break;
            case "hyponym":
                log(Level.INFO, loginController.getAccount(), "REMOVE " + (sdo.getName().isEmpty() ? " empty hyponym" : sdo.getName()) + " (hyponym of " + sd.getName() + ")");
                sd.getHyponym().remove(sdo);
                break;
            case "approximateSynonym":
                log(Level.INFO, loginController.getAccount(), "REMOVE " + (sdo.getName().isEmpty() ? " empty approximateSynonym" : sdo.getName()) + " (approximateSynonym of " + sd.getName() + ")");
                sd.getApproximateSynonym().remove(sdo);
                break;
            case "reference":
                log(Level.INFO, loginController.getAccount(), "REMOVE " + (sdo.getName().isEmpty() ? " empty reference" : sdo.getName()) + " (reference of " + sd.getName() + ")");
                sd.getOWLClass().setName("");
                sd.getOWLClass().setViewButtonDisabled(false);
                break;
            default:
        }
        if (!sdo.getName().isEmpty() || (relType.equals("reference"))) {
            sd.setSaveButtonDisabled(false);
        }
        relationPanelCheck(sdo.getName());
    }

    public void onOntologyReferenceSelect(SenseData sd) {
        log(Level.INFO, loginController.getAccount(), "ADD ontological reference " + sd.getOWLClass().getName() + " to the sense " + sd.getName());
        sd.setSaveButtonDisabled(false);
    }

    private void relationPanelCheck(String OWLName) {
        if (lexiconCreationControllerRelationDetail.getCurrentLexicalEntry().equals(OWLName)) {
            lexiconCreationControllerRelationDetail.resetRelationDetails();
            lexiconCreationControllerRelationDetail.setCurrentLexicalEntry("");
        }
    }

    // invoked by new sense button of lemma box
    public void saveSense(LemmaData ld) throws IOException, OWLOntologyStorageException {
        SenseData sd = new SenseData();
        sd.setSaveButtonDisabled(true);
        lexiconManager.saveSense(sd, ld);
        senses.add(sd);
        addSenseCopy(sd);
        info("template.message.saveSense.summary", "template.message.saveSense.description", sd.getName());
    }

    // invoked by new lemma
    public void saveDefaultSense(LemmaData ld, String wordType) throws IOException, OWLOntologyStorageException {
        setDefaultSense();
        lexiconManager.saveLemmaAndDefaultSense(senses.get(0), ld, wordType);
        addSenseCopy(senses.get(0));
    }

    private void setDefaultSense() {
        senses.clear();
        sensesCopy.clear();
        SenseData defaultSense = new SenseData();
        defaultSense.setSaveButtonDisabled(true);
        defaultSense.setDeleteButtonDisabled(false);
        senses.add(defaultSense);
    }

    public void ontoClassChanged(SenseData sense) {
        log(Level.INFO, loginController.getAccount(), "UPDATE OntoClass to " + sense.getName() + " of " + sense.getName());
        sense.setSaveButtonDisabled(false);
    }

    public void addEntryOfSenseRelation(SenseData.Openable sdo, String relType, SenseData sd) {
        log(Level.INFO, loginController.getAccount(), "VIEW Deatils of the " + sdo.getName() + " " + relType + " of " + sd.getName());
        String relationValue = sdo.getName().split("@")[0];
        senseOpenedInRelation = relationValue;
        lexiconCreationControllerRelationDetail.resetRelationDetails();
        lexiconCreationControllerRelationDetail.setAddButtonsDisabled(false);
        lexiconCreationControllerRelationDetail.setEntryOfSenseRelation(relationValue);
        checkForLock(getEntryLock(sdo.getName()));
        lexiconManager.getLexiconLocker().print();
        lexiconCreationControllerRelationDetail.setRelationLemmaRendered(true);
        lexiconCreationControllerRelationDetail.setCurrentLexicalEntry(sdo.getName());
        lexiconCreationControllerFormDetail.setLexicalRelationButtons(false);
        lexiconCreationControllerRelationDetail.setActiveTab(2);
    }

    private String getEntryLock(String sense) {
        String s = sense.split("_sense")[0];
        return s + "_lemma";
    }

    public void resetSenseDetails() {
        senses.clear();
        senseRendered = false;
    }

    public void addOntoReference(SenseData sd) {
        log(Level.INFO, loginController.getAccount(), "ADD empty ontological reference to " + sd.getName());
        sd.getOWLClass().setViewButtonDisabled(true);
    }

    public List<SelectItem> getSensesByLanguage(String sense, boolean filterByLanguage) {
        String filter = "";
        if (!filter.isEmpty()) {
            return lexiconManager.getSensesByLanguage(sense, filter);
        } else {
            return lexiconManager.getSensesByLanguage(sense, null);
        }
    }

    public ArrayList<Ontology> getTaxonomy() {
        return propertyValue.getTaxonomy();
    }

    private void checkForLock(String entry) {
        // check if the lexical entry is available and lock it
        boolean locked = lexiconManager.checkForLock(entry);
        if (locked) {
            lexiconCreationControllerRelationDetail.setLocked(true);
            lexiconCreationControllerRelationDetail.setLocker(lexiconManager.getLockingUser(entry) + " is working ... ");
            log(Level.INFO, loginController.getAccount(), "ACCESS TO THE LOCKED lexical entry related to " + entry);
        } else {
            lexiconCreationControllerRelationDetail.setLocked(false);
            lexiconCreationControllerRelationDetail.setLocker("");
            log(Level.INFO, loginController.getAccount(), "LOCK the lexical entry related to " + entry);
        }
    }

}
