/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.constant.OntoLexEntity;
import it.cnr.ilc.lexolite.domain.AccountType;
import it.cnr.ilc.lexolite.manager.AccountManager;
import it.cnr.ilc.lexolite.manager.FormData;
import it.cnr.ilc.lexolite.manager.LemmaData;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.LexiconQuery;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import it.cnr.ilc.lexolite.manager.ReferenceMenuTheme;
import it.cnr.ilc.lexolite.manager.SenseData;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Level;
import org.primefaces.event.TabCloseEvent;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author andreabellandi
 */
@ViewScoped
@Named
public class LexiconControllerLinkedLexicalEntryDetail extends BaseController implements Serializable {

    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private PropertyValue propertyValue;
    @Inject
    private LoginController loginController;
    @Inject
    private LexiconControllerTabViewList lexiconCreationControllerTabViewList;
    @Inject
    private AccountManager accountManager;

    private LemmaData lemma = new LemmaData();
    private LemmaData lemmaCopy = new LemmaData();

    private final ArrayList<FormData> forms = new ArrayList<>();
    private final ArrayList<FormData> formsCopy = new ArrayList();

    private final ArrayList<SenseData> senses = new ArrayList<>();
    private final ArrayList<SenseData> sensesCopy = new ArrayList();

    private int activeTab = 0;

    private boolean senseToolbarRendered = false;
    private boolean addButtonsDisabled = true;
    private boolean relationLemmaRendered = false;

    private boolean locked = false;
    private String locker = "";

    private final String MULTIWORD_COMPONENT_REGEXP = "([0-9]*[#|\\*]*)*";
    private boolean addFormButtonDisabled = true;

    // stores the OWL name of the lexical entry currently opened in the relation tabview
    private String currentLexicalEntry = "";

    public boolean isSenseToolbarRendered() {
        return senseToolbarRendered;
    }

    public void setSenseToolbarRendered(boolean senseToolbarRendered) {
        this.senseToolbarRendered = senseToolbarRendered;
    }

    public boolean isUserEnable() {
        return loginController.getAccount().getType().getName().equals(AccountManager.ADMINISTRATOR);
    }

    public LemmaData getLemmaCopy() {
        return lemmaCopy;
    }

    public ArrayList<FormData> getFormsCopy() {
        return formsCopy;
    }

    public ArrayList<SenseData> getSensesCopy() {
        return sensesCopy;
    }

    public boolean isAddFormButtonDisabled() {
        return addFormButtonDisabled;
    }

    public void setAddFormButtonDisabled(boolean addFormButtonDisabled) {
        this.addFormButtonDisabled = addFormButtonDisabled;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getLocker() {
        return locker;
    }

    public void setLocker(String locker) {
        this.locker = locker;
    }

    public boolean isRelationLemmaRendered() {
        return relationLemmaRendered;
    }

    public void setRelationLemmaRendered(boolean relationLemmaRendered) {
        this.relationLemmaRendered = relationLemmaRendered;
    }

    public String getCurrentLexicalEntry() {
        return currentLexicalEntry;
    }

    public void setCurrentLexicalEntry(String currentLexicalEntry) {
        this.currentLexicalEntry = currentLexicalEntry;
    }

    public int getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(int activeTab) {
        this.activeTab = activeTab;
    }

    public LexiconManager getLexiconManager() {
        return lexiconManager;
    }

    public void setLexiconManager(LexiconManager lexiconManager) {
        this.lexiconManager = lexiconManager;
    }

    public LemmaData getLemma() {
        return lemma;
    }

    public void setLemma(LemmaData lemma) {
        this.lemma = lemma;
    }

    public List<FormData> getForms() {
        return forms;
    }

    public List<SenseData> getSenses() {
        return senses;
    }

    public String emptyMessage(String text, String emptyMessage) {
        return emptyMessage(text, text, emptyMessage);
    }

    public String emptyMessage(String test, String text, String emptyMessage) {
        return test == null || test.equals("") ? emptyMessage : text;
    }

    public void openNote() {
        log(Level.INFO, loginController.getAccount(), "OPEN note of Lemma " + lemma.getFormWrittenRepr());
    }

    public void openNote(String form) {
        log(Level.INFO, loginController.getAccount(), "OPEN note of Form " + form);
    }

    public void openSenseNote(String senseName) {
        log(Level.INFO, loginController.getAccount(), "OPEN note of Sense " + senseName);
    }

    public String getCommentIcon() {
        if (lemma.getNote().length() > 0) {
            return "fa fa-comment";
        } else {
            return "fa fa-comment-o";
        }
    }

    public String getCommentIcon(FormData fd) {
        if (fd.getNote().length() > 0) {
            return "fa fa-comment";
        } else {
            return "fa fa-comment-o";
        }
    }

    public String getCommentIcon(SenseData sd) {
        if (sd.getNote().length() > 0) {
            return "fa fa-comment";
        } else {
            return "fa fa-comment-o";
        }
    }

    public void unlockRelationalEntry() {
        boolean unlocked = lexiconManager.unlock(lemma.getIndividual());
        if (unlocked) {
            log(Level.INFO, loginController.getAccount(), "UNLOCK the lexical entry related to " + lemma.getIndividual());
            setLocker("");
            setLocked(false);
        }
    }

    // invoked by an user that adds a new form of a lemma
    public void addForm() {
        FormData fd = new FormData();
        forms.add(fd);
    }

    // invoked by an user that adds a new sense of a lemma
    public void addSense() {
        SenseData sd = new SenseData();
        ArrayList<SenseData.Openable> sdo = new ArrayList();
        SenseData.Openable _sdo = new SenseData.Openable();
        _sdo.setDeleteButtonDisabled(true);
        _sdo.setViewButtonDisabled(true);
        _sdo.setName(Label.NO_ENTRY_FOUND);
        sdo.add(_sdo);
        senses.add(sd);
    }

    // for keeping track of modifies
    private void createLemmaCopy() {
        this.lemmaCopy.setFormWrittenRepr(lemma.getFormWrittenRepr());
        this.lemmaCopy.setMorphoTraits(copyMorphData(lemma.getMorphoTraits()));
        this.lemmaCopy.setLanguage(lemma.getLanguage());
        this.lemmaCopy.setPoS(lemma.getPoS());
        this.lemmaCopy.setIndividual(lemma.getIndividual());
        this.lemmaCopy.setType(lemma.getType());
        this.lemmaCopy.setNote(lemma.getNote());
        this.lemmaCopy.setVerified(lemma.isVerified());
        this.lemmaCopy.setSeeAlso(copyWordData(lemma.getSeeAlso()));
        this.lemmaCopy.setMultiword(copyWordData(lemma.getMultiword()));
        this.lemmaCopy.setExtensionAttributeInstances(copyExtensionAttributeInstances(lemma.getExtensionAttributeInstances()));
        this.lemmaCopy.setValid(lemma.getValid());
    }

    private ArrayList<LemmaData.MorphoTrait> copyMorphData(ArrayList<LemmaData.MorphoTrait> almt) {
        ArrayList<LemmaData.MorphoTrait> _almt = new ArrayList<>();
        for (LemmaData.MorphoTrait mt : almt) {
            LemmaData.MorphoTrait _mt = new LemmaData.MorphoTrait();
            _mt.setName(mt.getName());
            _mt.setSchema(mt.getSchema());
            _mt.setValue(mt.getValue());
            _almt.add(_mt);
        }
        return _almt;
    }

    private ArrayList<LemmaData.ExtensionAttributeIstance> copyExtensionAttributeInstances(ArrayList<LemmaData.ExtensionAttributeIstance> aleai) {
        ArrayList<LemmaData.ExtensionAttributeIstance> _aleai = new ArrayList();
        for (LemmaData.ExtensionAttributeIstance eai : aleai) {
            LemmaData.ExtensionAttributeIstance _eai = new LemmaData.ExtensionAttributeIstance();
            _eai.setName(eai.getName());
            _eai.setType(eai.getType());
            _eai.setValue(eai.getValue());
            _aleai.add(_eai);
        }
        return _aleai;
    }

    private ArrayList<LemmaData.Word> copyWordData(ArrayList<LemmaData.Word> alw) {
        ArrayList<LemmaData.Word> _alw = new ArrayList();
        for (LemmaData.Word w : alw) {
            LemmaData.Word _w = new LemmaData.Word();
            _w.setOWLName(w.getOWLName());
            _w.setLanguage(w.getLanguage());
            _w.setWrittenRep(w.getWrittenRep());
            _w.setOWLComp(w.getOWLComp());
            _w.setLabel(w.getLabel());
            _alw.add(_w);
        }
        return _alw;
    }

    private void updateFormCopy(FormData fd, int order) {
        formsCopy.remove(order);
        formsCopy.add(order, copyFormData(fd));
    }

    private void addFormCopy(ArrayList<FormData> fdList) {
        formsCopy.clear();
        for (FormData fd : fdList) {
            formsCopy.add(copyFormData(fd));
        }
    }

    private FormData copyFormData(FormData fd) {
        FormData _fd = new FormData();
        _fd.setFormWrittenRepr(fd.getFormWrittenRepr());
        _fd.setMorphoTraits(copyFormMorphoTraits(fd.getMorphoTraits()));
        _fd.setNote(fd.getNote());
        _fd.setLanguage(fd.getLanguage());
        _fd.setIndividual(fd.getIndividual());
        return _fd;
    }

    private ArrayList<LemmaData.MorphoTrait> copyFormMorphoTraits(ArrayList<LemmaData.MorphoTrait> almt) {
        ArrayList<LemmaData.MorphoTrait> _almt = new ArrayList<>();
        for (LemmaData.MorphoTrait mt : almt) {
            LemmaData.MorphoTrait _mt = new LemmaData.MorphoTrait();
            _mt.setName(mt.getName());
            _mt.setSchema(mt.getValue());
            _mt.setValue(mt.getValue());
            _almt.add(_mt);
        }
        return _almt;
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
        ArrayList<SenseData.Openable> syns = new ArrayList();
        ArrayList<SenseData.Openable> translations = new ArrayList();
        ArrayList<SenseData.Openable> translationsOf = new ArrayList();
//        SenseData.Openable _OWLClass = new SenseData.Openable();
//        _OWLClass.setDeleteButtonDisabled(sd.getOWLClass().isDeleteButtonDisabled());
//        _OWLClass.setViewButtonDisabled(sd.getOWLClass().isViewButtonDisabled());
//        _OWLClass.setName(sd.getOWLClass().getName());
//        _sd.setOWLClass(_OWLClass);
        ReferenceMenuTheme rmt = new ReferenceMenuTheme();
        rmt.setId(sd.getThemeOWLClass().getId());
        rmt.setName(sd.getThemeOWLClass().getName());
        rmt.setType((sd.getThemeOWLClass().getName().isEmpty()) ? null : ReferenceMenuTheme.itemType.valueOf(sd.getThemeOWLClass().getType()));
        _sd.setThemeOWLClass(rmt);
        _sd.setName(sd.getName());
        _sd.setNote(sd.getNote());
        return _sd;
    }

    public void setEntryOfLexicalRelation(String r) {
        clearRelationPanel();
        lemma = lexiconManager.getLemmaAttributes(r, propertyValue.getMorphoTrait(), null);
        createLemmaCopy();
        forms.addAll(lexiconManager.getFormsOfLemma(lemma.getIndividual(), lemma.getLanguage(), propertyValue.getMorphoTrait()));
        addFormCopy(forms);
        senses.addAll(lexiconManager.getSensesOfLemma(lemma.getIndividual(), null));
        addSenseCopy(senses);
    }

    public void removeForm(FormData fd) {
        forms.remove(fd);
    }

    public void removeSense(SenseData sd) {
        senses.remove(sd);
    }

    public boolean isAddButtonsDisabled() {
        return addButtonsDisabled;
    }

    public void setAddButtonsDisabled(boolean addButtonsDisabled) {
        this.addButtonsDisabled = addButtonsDisabled;
    }

    public void setEntryOfSenseRelation(String r) {
        lemma = null;
        forms.clear();
        senses.clear();
        lemma = lexiconManager.getLemmaOfSense(r, propertyValue.getMorphoTrait());
        createLemmaCopy();
        forms.addAll(lexiconManager.getFormsOfLemma(lemma.getIndividual(), lemma.getLanguage(), propertyValue.getMorphoTrait()));
        addFormCopy(forms);
        senses.addAll(lexiconManager.getSensesOfLemma(lemma.getIndividual(), null));
        addSenseCopy(senses);
    }

    private void clearRelationPanel() {
        lemma = null;
        relationLemmaRendered = false;
        forms.clear();
        senses.clear();
    }

    public void resetRelationDetails() {
        clearRelationPanel();
    }

    public void senseKeyupEvent(SenseData sd) {
    }

    public void formKeyupEvent(FormData fd) {
    }

    public void lemmaKeyupEvent() {
    }

    public void removeLemma() {
        clearRelationPanel();
    }

    public ArrayList<String> getPoS() {
        return propertyValue.getPoS(lemma.getType());
    }

    public List<String> completeComponents(String currentComponent) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        LemmaData.Word w = (LemmaData.Word) component.getAttributes().get("currentComponent");
        List<Map<String, String>> al = lexiconManager.lemmasList("All languages");
        return getFilteredComponentList(al, w.getWrittenRep());
    }

    private List<String> getFilteredComponentList(List<Map<String, String>> list, String writtenRep) {
        List<String> filteredList = new ArrayList();
        String[] splitted = getNormalizedHomonym(writtenRep);
        for (Map<String, String> m : list) {
            String wr = m.get("writtenRep");
            if ((wr.matches(splitted[0] + MULTIWORD_COMPONENT_REGEXP)) && (!wr.isEmpty())) {
                filteredList.add(wr + "@" + m.get("lang"));
            }
        }
        return filteredList;
    }

    private String[] getNormalizedHomonym(String wr) {
        String[] splitted = new String[10];
        if (wr.matches(".*\\d.*")) {
            // contains a number
            splitted = wr.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        } else {
            // does not contain a number
            splitted[0] = wr;
        }
        return splitted;
    }

    public void onWordSelect(LemmaData.Word word) {
        log(Level.INFO, loginController.getAccount(), "UPDATE the word " + word.getWrittenRep() + " of the Multiword " + lemma.getFormWrittenRepr());
        // false instead of isSavableMultiwordLemma(), since a user can not modify the lemma name
        setMultiwordComponentButtons(word);
        lemma.setSaveButtonDisabled(false);
        setWordOfMultiword(word);
        word.setDeleteButtonDisabled(false);
        word.setViewButtonDisabled(false);
    }

    private void setMultiwordComponentButtons(LemmaData.Word comp) {
        for (LemmaData.Word w : lemma.getMultiword()) {
            if (comp.getWrittenRep().equals(w.getWrittenRep()) && comp.getLanguage().equals(w.getLanguage()) || w.getWrittenRep().isEmpty()) {
                w.setViewButtonDisabled(true);
            } else {
                w.setViewButtonDisabled(false);
            }
        }
    }

    // it queries the lexicon in order to get the name of the individual
    private void setWordOfMultiword(LemmaData.Word w) {
        String splitted[] = w.getLabel().split("@");
        String lemma = splitted[0];
        String lang = splitted[1];
        LemmaData.Word wd = lexiconManager.getLemma(lemma, lang);
        w.setWrittenRep(wd.getWrittenRep());
        w.setOWLName(wd.getOWLName().replace("_lemma", "_entry"));
        w.setLanguage(wd.getLanguage());
        w.setOWLComp(wd.getOWLComp());
        w.setLabel(wd.getWrittenRep() + "@" + wd.getLanguage());
    }

    public void verifiedChanged() {
        log(Level.INFO, loginController.getAccount(), "UPDATE Verified field of " + lemma.getFormWrittenRepr() + " to " + lemma.isVerified());
        addFormButtonDisabled = true;
        updateEntryValidity();
        lemma.setSaveButtonDisabled(false);
    }

    private void updateEntryValidity() {
        if (lemma.isVerified()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            lemma.setValid("Validated by " + loginController.getAccount().getName() + " at " + dateFormat.format(date));
        } else {
            lemma.setValid("false");
        }
    }

    public void lemmaPoSChanged(AjaxBehaviorEvent e) {
        String pos = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma Part-of-Speech of " + lemma.getFormWrittenRepr() + " to " + pos);
        lemma.setPoS(pos);
        addFormButtonDisabled = true;
        lemma.setSaveButtonDisabled(false);
    }

    private List<String> getFilteredList(List<Map<String, String>> list, String keyFilter, String currentLemma, boolean onlyMW) {
        List<String> filteredList = new ArrayList();
        Collections.sort(list, new LexiconComparator("writtenRep"));
        for (Map<String, String> m : list) {
            String wr = m.get("writtenRep");
            if ((wr.startsWith(keyFilter)) && (!wr.isEmpty())) {
                if (!wr.equals(currentLemma)) {
                    if (onlyMW) {
                        if (wr.contains(" ")) {
                            filteredList.add(wr + "@" + m.get("lang"));
                        }
                    } else {
                        filteredList.add(wr + "@" + m.get("lang"));
                    }
                }
            }
        }
        return filteredList;
    }

    public void onTabClose(TabCloseEvent event) {
        lexiconManager.unlock(lemma.getIndividual());
        System.out.println(lemma.getIndividual() + " unlocked.");
    }

    public void onReferenceSelect(LemmaData.Word reference) {
        log(Level.INFO, loginController.getAccount(), "ADD Reference (seeAlso) " + reference.getWrittenRep() + " to the Lemma " + lemma.getFormWrittenRepr());
        lemma.setSaveButtonDisabled(false);
        setLexicalRelationEntry(reference);
        reference.setDeleteButtonDisabled(false);
        reference.setViewButtonDisabled(false);
    }

    // it queries the lexicon in order to get the name of the individual
    private void setLexicalRelationEntry(LemmaData.Word w) {
        String splitted[] = w.getWrittenRep().split("@");
        String lemma = splitted[0];
        String lang = splitted[1];
        LemmaData.Word wd = lexiconManager.getLemma(lemma, lang);
        w.setWrittenRep(wd.getWrittenRep());
        w.setOWLName(wd.getOWLName());
        w.setLanguage(wd.getLanguage());
    }

    public List<String> completeTextOnlyForMW(String _lemma) {
        List<Map<String, String>> al = lexiconManager.lemmasList("All languages");
        return getFilteredList(al, _lemma, lemma.getFormWrittenRepr(), true);
    }

    public List<String> completeText(String _lemma) {
        List<Map<String, String>> al = lexiconManager.lemmasList("All languages");
        return getFilteredList(al, _lemma, lemma.getFormWrittenRepr(), false);
    }

    public void removeReference(LemmaData.Word reference) {
        log(Level.INFO, loginController.getAccount(), "REMOVE reference (seeAlso) " + (reference.getWrittenRep().isEmpty() ? " empty see also" : reference.getWrittenRep()) + " from " + lemma.getFormWrittenRepr());
        lemma.getSeeAlso().remove(reference);
        lemma.setSaveButtonDisabled(false);
    }

    // invoked by controller after an user selected add a reference (seeAlso) to lemma
    public void addReference() {
        log(Level.INFO, loginController.getAccount(), "ADD empty reference (seeAlso) to lemma " + lemma.getFormWrittenRepr());
        LemmaData.Word reference = new LemmaData.Word();
        reference.setViewButtonDisabled(true);
        lemma.getSeeAlso().add(reference);
    }

    public void saveLemma() throws IOException, OWLOntologyStorageException {
        lemma.setSaveButtonDisabled(true);
        lemma.setDeleteButtonDisabled(false);
        addFormButtonDisabled = false;
        // modification only is allowed
        saveLemma(false, false);
        createLemmaCopy();
    }

    private void saveLemma(boolean renaming, boolean newAction) throws IOException, OWLOntologyStorageException {
        info("template.message.saveLemma.summary", "template.message.saveLemma.description", lemma.getFormWrittenRepr());
        if (renaming) {
            // it never goes here
            saveForUpdateWithRenameAction();
        } else {
            if (newAction) {
                // it never goes here
                saveForNewAction();
            } else {
                // it always goes here
                saveForUpdateAction();
            }
        }
    }

    private void saveForNewAction() throws IOException, OWLOntologyStorageException {
    }

    private void saveForUpdateWithRenameAction() throws IOException, OWLOntologyStorageException {
    }

    private void saveForUpdateAction() throws IOException, OWLOntologyStorageException {
        if (lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel())) {
            // word case
            log(Level.INFO, loginController.getAccount(), "UPDATE Lemma " + lemma.getFormWrittenRepr());
            lexiconManager.updateLemma(lemmaCopy, lemma);
        } else {
            // multiword case
            log(Level.INFO, loginController.getAccount(), "UPDATE Multiword Lemma " + lemma.getFormWrittenRepr());
            // it is possible that some word has an empty writtenRep, 
            // so the deletion of the related fields (OWLName and lang) it is recommended
            lexiconManager.updateLemmaForMultiword(lemmaCopy, lemma);
        }
    }

    public void saveNote() throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "EDIT note of Lemma " + lemma.getFormWrittenRepr() + " in " + lemma.getNote());
        lexiconManager.saveLemmaNote(lemma, lemmaCopy.getNote());
        lemmaCopy.setNote(lemma.getNote());
        info("template.message.saveLemmaNote.summary", "template.message.saveLemmaNote.description");
    }

    public void saveNote(FormData fd) throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "EDIT note of Form " + fd.getFormWrittenRepr() + " in " + fd.getNote());
        int order = forms.indexOf(fd);
        lexiconManager.saveFormNote(fd, formsCopy.get(order).getNote());
        formsCopy.get(order).setNote(fd.getNote());
        info("template.message.saveFormNote.summary", "template.message.saveformNote.description");
    }

    public void closeNote() {
        log(Level.INFO, loginController.getAccount(), "CLOSE note of Lemma " + lemma.getFormWrittenRepr());
    }

    public void closeNote(String form) {
        log(Level.INFO, loginController.getAccount(), "CLOSE note of Form " + form);
    }

    public void saveForm(FormData fd) throws IOException, OWLOntologyStorageException {
        fd.setSaveButtonDisabled(true);
        fd.setDeleteButtonDisabled(false);
        int order = forms.indexOf(fd);
        if (formsCopy.get(order).getFormWrittenRepr() == null) {
            // saving due to new form action
        } else {
            // saving due to a form modification action
            if (isSameWrittenRep(fd.getFormWrittenRepr(), order)) {
                // modification does not concern form written representation
                log(Level.INFO, loginController.getAccount(), "SAVE updated Form " + fd.getFormWrittenRepr());
                lexiconManager.updateForm(formsCopy.get(order), fd, lemma);
            } else {
                // modification concerns also form written representation
            }
        }
        info("template.message.saveForm.summary", "template.message.saveForm.description", fd.getFormWrittenRepr());
        updateFormCopy(fd, order);
    }

    private boolean isSameWrittenRep(String wr, int order) {
        if (formsCopy.get(order).getFormWrittenRepr().equals(wr)) {
            return true;
        } else {
            return false;
        }
    }

    // SENSES 
    public void onRelationTargetSelect(SenseData sd, SenseData.Openable sdo) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        String relType = (String) component.getAttributes().get("Relation");
        log(Level.INFO, loginController.getAccount(), "ADD Sense " + sdo.getName() + " as " + relType + " of the sense " + sd.getName());
        sd.setSaveButtonDisabled(false);
        sdo.setDeleteButtonDisabled(false);
        sdo.setViewButtonDisabled(false);
    }

    public void removeSenseRelation(SenseData sd, SenseData.Openable sdo, String relType) {
        switch (relType) {
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
    }

    public void onOntologyReferenceSelect(SenseData sd) {
        log(Level.INFO, loginController.getAccount(), "ADD ontological reference " + sd.getOWLClass().getName() + " to the sense " + sd.getName());
        sd.setSaveButtonDisabled(false);
    }

    public void addOntoReference(SenseData sd) {
        log(Level.INFO, loginController.getAccount(), "ADD empty ontological reference to " + sd.getName());
        sd.getOWLClass().setViewButtonDisabled(true);
    }

    // invoked by sense box
    public void saveSenseRelation(SenseData sd) throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "SAVE Sense " + sd.getName());
        int order = senses.indexOf(sd);
        lexiconManager.saveSenseRelation(sensesCopy.get(order), sd);
        updateSenseCopy(sd, order);
        sd.setSaveButtonDisabled(true);
        setSenseToolbarRendered(true);
        info("template.message.saveSenseRelation.summary", "template.message.saveSenseRelation.description", sd.getName());
    }

}
