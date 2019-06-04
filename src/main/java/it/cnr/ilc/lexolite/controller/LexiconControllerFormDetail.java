/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.constant.OntoLexEntity;
import it.cnr.ilc.lexolite.manager.AccountManager;
import it.cnr.ilc.lexolite.manager.FormData;
import it.cnr.ilc.lexolite.manager.LemmaData;
import it.cnr.ilc.lexolite.manager.LemmaData.Word;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import it.cnr.ilc.lexolite.util.LexiconUtil;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.faces.component.UIComponent;
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
public class LexiconControllerFormDetail extends BaseController implements Serializable {

    @Inject
    private PropertyValue propertyValue;
    @Inject
    private LexiconControllerTabViewList lexiconCreationControllerTabViewList;
    @Inject
    private LexiconControllerSenseDetail lexiconCreationViewSenseDetail;
    @Inject
    private LexiconControllerLinkedLexicalEntryDetail lexiconCreationControllerRelationDetail;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LoginController loginController;

    private LemmaData lemma = new LemmaData();
    private LemmaData lemmaCopy = new LemmaData();

    private final ArrayList<FormData> forms = new ArrayList();
    private final ArrayList<FormData> formsCopy = new ArrayList();

    private final String LemmaOfMultiwordNotFound = "";

    private boolean lemmaRendered = false;
    private boolean newAction = false;
    private boolean lemmAlreadyExists = false;
    private boolean isAdmissibleLemma = true;
    private boolean formAlreadyExists = false;
    private boolean addFormButtonDisabled = true;

    private boolean verified = false;

    private boolean locked = false;
    private String locker = "";

    private final String MULTIWORD_COMPONENT_REGEXP = "([0-9]*[#|\\*]*)*";
//    private final String ADMISSIBLE_WORD_REGEXP = "^[aA-zZ]+[0-9]*"; 
    //    private final String ADMISSIBLE_MULTIWORD_REGEXP = "^[aA-zZ]+";
    //private final String ADMISSIBLE_WORD_REGEXP = "^[^\\*\\.\\#\\(\\)\\[\\]\\{\\};:,\\/=\\+\\-_\\\\\\?\\!\"%&0-9\\s]+[0-9]*$";
    private final String ADMISSIBLE_WORD_REGEXP = "^[^\\^\\°\\|\\*\\.\\#\\(\\)\\[\\]\\{\\};:,\\/=\\+\\-_\\\\\\?\\!\"%&\\d\\s]+\\d*$";
    private final String ADMISSIBLE_MULTIWORD_REGEXP = "^[^\\^\\°\\|\\*\\.\\#\\(\\)\\[\\]\\{\\};:,\\/=\\+\\-_\\\\\\?\\!\"%&\\d]+$";

    public boolean isUserEnable() {
        return loginController.getAccount().getType().getName().equals(AccountManager.ADMINISTRATOR);
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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public List<FormData> getForms() {
        return forms;
    }

    public ArrayList<FormData> getFormsCopy() {
        return formsCopy;
    }

    public LemmaData getLemma() {
        return lemma;
    }

    public LemmaData getLemmaCopy() {
        return lemmaCopy;
    }

    public boolean isNewAction() {
        return newAction;
    }

    public void setNewAction(boolean newAction) {
        this.newAction = newAction;
    }

    public boolean isLemmAlreadyExists() {
        return lemmAlreadyExists;
    }

    public void setLemmAlreadyExists(boolean lemmAlreadyExists) {
        this.lemmAlreadyExists = lemmAlreadyExists;
    }

    public boolean isIsAdmissibleLemma() {
        return isAdmissibleLemma;
    }

    public void setIsAdmissibleLemma(boolean isAdmissibleLemma) {
        this.isAdmissibleLemma = isAdmissibleLemma;
    }

    public boolean isFormAlreadyExists() {
        return formAlreadyExists;
    }

    public void setFormAlreadyExists(boolean formAlreadyExists) {
        this.formAlreadyExists = formAlreadyExists;
    }

    public boolean isAddFormButtonDisabled() {
        return addFormButtonDisabled;
    }

    public void setAddFormButtonDisabled(boolean addFormButtonDisabled) {
        this.addFormButtonDisabled = addFormButtonDisabled;
    }

    public boolean isLemmaRendered() {
        return lemmaRendered;
    }

    public void setLemmaRendered(boolean lemmaRendered) {
        this.lemmaRendered = lemmaRendered;
    }

    public String getLemmaOfMultiwordNotFound() {
        return LemmaOfMultiwordNotFound;
    }

    public String getEntryErrorLabel() {
        if (lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel()) && (lemma.getFormWrittenRepr().contains(" "))) {
            return "Word can not contain spaces";
        } else {
            if (!isAdmissibleLemma) {
                return "Only letters are admissible. <br/> For homonyms you can concatenate a number <br/> to the written representation";
            } else {
                return "Lemma already exists";
            }
        }
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

    public boolean isRenderedEntryErrorLabel() {
        if ((lemmAlreadyExists)
                || (lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel()))
                && (lemma.getFormWrittenRepr().contains(" "))
                || (!isAdmissibleLemma)) {
            return true;
        }
        return false;
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

    // invoked by the controller after an user selected a lemma in the tabview
    public void addLemma(String lemma) {
        this.lemma.clear();
        this.lemma = lexiconManager.getLemmaAttributes(lemma);
        ArrayList<FormData> al = lexiconManager.getFormsOfLemma(lemma, this.lemma.getLanguage());
        forms.clear();
        forms.addAll(al);
        createLemmaCopy();
        addFormCopy(al);
    }

    // for keeping track of modifies
    private void createLemmaCopy() {
        this.lemmaCopy.setFormWrittenRepr(lemma.getFormWrittenRepr());
        this.lemmaCopy.setGender(lemma.getGender());
        this.lemmaCopy.setPerson(lemma.getPerson());
        this.lemmaCopy.setMood(lemma.getMood());
        this.lemmaCopy.setVoice(lemma.getVoice());
        this.lemmaCopy.setLanguage(lemma.getLanguage());
        this.lemmaCopy.setNumber(lemma.getNumber());
        this.lemmaCopy.setPoS(lemma.getPoS());
        this.lemmaCopy.setIndividual(lemma.getIndividual());
        this.lemmaCopy.setType(lemma.getType());
        this.lemmaCopy.setNote(lemma.getNote());
        this.lemmaCopy.setVerified(lemma.isVerified());
        this.lemmaCopy.setSeeAlso(copyWordData(lemma.getSeeAlso()));
        this.lemmaCopy.setMultiword(copyWordData(lemma.getMultiword()));
        this.lemmaCopy.setValid(lemma.getValid());
    }

    private ArrayList<Word> copyWordData(ArrayList<Word> alw) {
        ArrayList<Word> _alw = new ArrayList();
        for (Word w : alw) {
            Word _w = new Word();
            _w.setOWLName(w.getOWLName());
            _w.setLanguage(w.getLanguage());
            _w.setWrittenRep(w.getWrittenRep());
            _w.setOWLComp(w.getOWLComp());
            _w.setLabel(w.getLabel());
            _alw.add(_w);
        }
        return _alw;
    }

    public void checkForDocInModification(LemmaData ld) {
        log(Level.INFO, loginController.getAccount(), "EDIT DocumentedIn of " + ld.getFormWrittenRepr());
        ld.setSaveButtonDisabled(false);
    }

    public void checkForDocInModification(FormData fd) {
        log(Level.INFO, loginController.getAccount(), "EDIT DocumentedIn of " + fd.getFormWrittenRepr());
        fd.setSaveButtonDisabled(false);
    }

    private void addFormCopy(FormData fd) {
        formsCopy.add(0, copyFormData(fd));
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

    private void removeFormCopy(int order) {
        formsCopy.remove(order);
    }

    private FormData copyFormData(FormData fd) {
        FormData _fd = new FormData();
        _fd.setFormWrittenRepr(fd.getFormWrittenRepr());
        _fd.setGender(fd.getGender());
        _fd.setPerson(fd.getPerson());
        _fd.setMood(fd.getMood());
        _fd.setVoice(fd.getVoice());
        _fd.setNote(fd.getNote());
        _fd.setLanguage(fd.getLanguage());
        _fd.setNumber(fd.getNumber());
        _fd.setIndividual(fd.getIndividual());
        return _fd;
    }

    // invoked by an user that adds a new form of a lemma
    public void addForm() {
        log(Level.INFO, loginController.getAccount(), "ADD empty Form box");
        FormData fd = new FormData();
        setFormDefaultValues(fd);
        forms.add(0, fd);
        addFormCopy(fd);
    }

    private void setFormDefaultValues(FormData fd) {
        fd.setLanguage(lemma.getLanguage());
    }

    // invoked by the controller after an user selected a form in the tabview
    public void addForm(String form) {
        this.lemma.clear();
        this.lemma = lexiconManager.getLemmaEntry(form);
        ArrayList<FormData> al = lexiconManager.getFormsOfLemma(getLemma().getIndividual(), this.lemma.getLanguage());
        forms.clear();
        forms.addAll(al);
        createLemmaCopy();
        addFormCopy(al);
    }

    // invoked by the controller after an user selected a sense in the tabview
    public void addForms(String sense) {
        this.lemma.clear();
        this.lemma = lexiconManager.getLemmaOfSense(sense);
        ArrayList<FormData> al = lexiconManager.getFormsOfLemma(getLemma().getIndividual(), this.lemma.getLanguage());
        forms.clear();
        forms.addAll(al);
        createLemmaCopy();
        addFormCopy(al);
    }

    // invoked by the controller after an user selected add sense to lemma
    public void addSense() throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "ADD empty Sense box");
        lexiconCreationViewSenseDetail.saveSense(lemma);
        lexiconCreationViewSenseDetail.setSenseToolbarRendered(true);
    }

    public void removeForm(FormData fd) throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "DELETE Form " + fd.getFormWrittenRepr());
        lexiconManager.deleteForm(fd);
        info("template.message.deleteForm.summary", "template.message.deleteForm.description", fd.getFormWrittenRepr());
        // remove the form from forms and copyForms
        int order = forms.indexOf(fd);
        forms.remove(fd);
        removeFormCopy(order);
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        lexiconCreationControllerTabViewList.initFormTabView(currentLanguage);
    }

    public void saveForm(FormData fd) throws IOException, OWLOntologyStorageException {
        fd.setSaveButtonDisabled(true);
        fd.setDeleteButtonDisabled(false);
        int order = forms.indexOf(fd);
        if (formsCopy.get(order).getFormWrittenRepr() == null) {
            // saving due to new form action
            log(Level.INFO, loginController.getAccount(), "SAVE new Form " + fd.getFormWrittenRepr());
            lexiconManager.saveForm(fd, lemma);
        } else {
            // saving due to a form modification action
            if (isSameWrittenRep(fd.getFormWrittenRepr(), order)) {
                // modification does not concern form written representation
                log(Level.INFO, loginController.getAccount(), "SAVE updated Form " + fd.getFormWrittenRepr());
                lexiconManager.updateForm(formsCopy.get(order), fd, lemma);
            } else {
                // modification concerns also form written representation
                log(Level.INFO, loginController.getAccount(), "SAVE updated Form with renaming from " + formsCopy.get(order).getFormWrittenRepr() + " to " + fd.getFormWrittenRepr());
                lexiconManager.saveFormWithIRIRenaming(formsCopy.get(order), fd, lemma);
            }
        }
        info("template.message.saveForm.summary", "template.message.saveForm.description", fd.getFormWrittenRepr());
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        lexiconCreationControllerTabViewList.initFormTabView(currentLanguage);
        updateFormCopy(fd, order);
    }

    private boolean isSameWrittenRep(String wr, int order) {
        if (formsCopy.get(order).getFormWrittenRepr().equals(wr)) {
            return true;
        } else {
            return false;
        }
    }

    public void formNameKeyupEvent(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        FormData fd = (FormData) component.getAttributes().get("form");
        String formPart = ((String) e.getComponent().getAttributes().get("value"));
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        List<Map<String, String>> formList = lexiconManager.formsList(currentLanguage);
        if (contains(formList, formPart, fd.getLanguage())
                && (isSameLemma(formList, formPart))
                && (!isSameForm(fd, formPart))) {
            formAlreadyExists = true;
            fd.setSaveButtonDisabled(true);
        } else {
            formAlreadyExists = false;
            fd.setSaveButtonDisabled(false);
        }
        log(Level.INFO, loginController.getAccount(), "UPDATE Form name from " + fd.getFormWrittenRepr() + " to " + formPart);
        fd.setFormWrittenRepr(formPart);
        fd.setSaveButtonDisabled(isSavableForm(fd));
    }

    private boolean isSameForm(FormData fd, String part) {
        int order = forms.indexOf(fd);
        if (formsCopy.get(order).getFormWrittenRepr() == null) {
            return false;
        } else {
            if (!formsCopy.get(order).getFormWrittenRepr().equals(part)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public void lemmaNameKeyupEvent(String lemmaPart, List<Map<String, String>> lemmaList) {
        if (contains(lemmaList, lemmaPart, lemma.getLanguage()) && (!lemmaPart.equals(lemmaCopy.getFormWrittenRepr()))) {
            lemmAlreadyExists = true;
            /**/ isAdmissibleLemma = true;
            lemma.setSaveButtonDisabled(true);
        } else {
            /**/ if (isAdmissibleLemma(lemmaPart, lemma.getType())) {
                lemmAlreadyExists = false;
                /**/ isAdmissibleLemma = true;
                lemma.setSaveButtonDisabled(false);
                /**/            } else {
                /**/ isAdmissibleLemma = false;
                /**/ lemma.setSaveButtonDisabled(true);
                /**/            }
        }
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma name from " + lemma.getFormWrittenRepr() + " to " + lemmaPart);
        lemma.setSaveButtonDisabled(isSavableLemma() || (lemma.getFormWrittenRepr().contains(" ")));
        lemma.setFormWrittenRepr(lemmaPart);
    }

    private boolean isAdmissibleLemma(String w, String type) {
        if (type.equals(OntoLexEntity.Class.WORD.getLabel())) {
            return isAdmissibleWord(w);
        } else {
            return isAdmissibleMultiwordWord(w);
        }
    }

    private boolean isAdmissibleWord(String w) {
        if (w.matches(ADMISSIBLE_WORD_REGEXP) || w.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isAdmissibleMultiwordWord(String mw) {
        for (String comp : mw.split(" ")) {
            if (!(comp.matches(ADMISSIBLE_MULTIWORD_REGEXP) || mw.isEmpty())) {
                return false;
            }
        }
        return true;
    }

    public void lemmaMultiwordKeyupEvent(String lemmaPart, List<Map<String, String>> lemmaList) {
        if (!lemmaPart.isEmpty()) {
            int spacesNumber = getSpaceCount(lemmaPart, " ");
            lemma.getMultiword().clear();
            String[] splitted = lemmaPart.split(" ");
            for (int i = 0; i < spacesNumber; i++) {
                setMultiword(splitted, i, lemmaList);
            }
            if (spacesNumber > 0) {
                if ((splitted.length == spacesNumber + 1) && (!splitted[spacesNumber].isEmpty())) {
                    setMultiword(splitted, spacesNumber, lemmaList);
                }
            }
            lemmaNameKeyupEvent(lemmaPart, lemmaList);
        } else {
            lemma.getMultiword().clear();
        }
        lemma.setSaveButtonDisabled(isSavableLemmaMultiword());
    }

    private boolean isSavableLemmaMultiword() {
        if (isSavableLemma() || lemma.getFormWrittenRepr().split(" ").length <= 1) {
            return true;
        } else {
            return false;
        }
    }

    private void setMultiword(String[] s, int i, List<Map<String, String>> lemmaList) {
        Word w = getComponent(s, i, lemmaList);
        lemma.getMultiword().add(w);
    }

    private Word getComponent(String[] s, int i, List<Map<String, String>> lemmaList) {
        Word w = new Word();
        w.setWrittenRep(s[i] + " not found");
        w.setViewButtonDisabled(true);
        Collections.sort(lemmaList, new LexiconComparator("writtenRep"));
        for (Map<String, String> m : lemmaList) {
            if ((s[i].equals(m.get("writtenRep")) || (m.get("writtenRep").matches(Pattern.quote(s[i] + MULTIWORD_COMPONENT_REGEXP)))) && (!m.get("writtenRep").isEmpty())) {
                setComponent(m, w, i);
                break;
            }
        }
        return w;
    }

    private void setComponent(Map<String, String> m, Word w, int position) {
        w.setWrittenRep(m.get("writtenRep"));
        w.setOWLName(m.get("individual"));
        w.setLanguage(m.get("lang"));
        w.setLabel(w.getWrittenRep() + "@" + w.getLanguage());
        w.setViewButtonDisabled(false);
        if (!isNewAction()) {
            String OWLComp = lexiconManager.getComponentAtPosition(lemma.getIndividual(), Integer.toString(position));
            w.setOWLComp(OWLComp);
        }
    }

    public void lemmaNameKeyupEvent(AjaxBehaviorEvent e) {
        String lemmaPart = (String) e.getComponent().getAttributes().get("value");
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        List<Map<String, String>> lemmaList = lexiconManager.lemmasList(currentLanguage);
        if (lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel())) {
            lemmaNameKeyupEvent(lemmaPart, lemmaList);
        } else {
            lemmaMultiwordKeyupEvent(lemmaPart, lemmaList);
        }
    }

    private int getSpaceCount(String str, String toFind) {
        String s = str.replaceAll("\\s+", " ");
        int lastIndex = 0;
        int count = 0;
        while (lastIndex != -1) {
            lastIndex = s.indexOf(toFind, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += toFind.length();
            }
        }
        return count;
    }

    // returns true if the lemma (or form) already exists in a specific lexicon
    private boolean contains(List<Map<String, String>> l, String form, String lang) {
        boolean contains = false;
        for (Map<String, String> m : l) {
            if (!lang.isEmpty()) {
                if (m.get("writtenRep").equals(form.trim()) && (m.get("lang").equals(lang))) {
                    contains = true;
                    break;
                }
            } else {
                if (m.get("writtenRep").equals(form.trim())) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    private boolean isSameLemma(List<Map<String, String>> l, String formPart) {
        boolean same = false;
        for (Map<String, String> m : l) {
            if (m.get("writtenRep").equals(formPart) && (m.get("lemma").equals(lemma.getIndividual()))) {
                same = true;
                break;
            }
        }
        return same;
    }

    private void lemmaNameKeyupEvent() {
        String lemmaPart = lemma.getFormWrittenRepr();
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        List<Map<String, String>> lemmaList = lexiconManager.lemmasList(currentLanguage);
        if (contains(lemmaList, lemmaPart, lemma.getLanguage()) && (!lemmaPart.equals(lemmaCopy.getFormWrittenRepr()))) {
            lemmAlreadyExists = true;
            lemma.setSaveButtonDisabled(true);
        } else {
            /**/ if (isAdmissibleLemma(lemmaPart, lemma.getType())) {
                lemmAlreadyExists = false;
                /**/ isAdmissibleLemma = true;
                lemma.setSaveButtonDisabled(false);
                /**/            } else {
                /**/ isAdmissibleLemma = false;
                /**/ lemma.setSaveButtonDisabled(true);
                /**/            }
        }
        lemma.setSaveButtonDisabled(isSavableLemma() || (lemma.getFormWrittenRepr().contains(" ")));
        lemma.setFormWrittenRepr(lemmaPart);
    }

    private void ckeckLemmaSavability() {
        if (!lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel())) {
            lemma.setSaveButtonDisabled(isSavableLemma() || (!lemma.getFormWrittenRepr().contains(" ")));
        } else {
            if (isSavableLemma()) {
                lemma.setSaveButtonDisabled(true);
            } else {
                if (lemma.getFormWrittenRepr().contains(" ")) {
                    lemma.setSaveButtonDisabled(true);
                } else {
                    lemma.setSaveButtonDisabled(false);
                }
            }
        }
    }

    public void languageChanged(AjaxBehaviorEvent e) {
        String lang = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma language of " + lemma.getFormWrittenRepr() + " to " + lang);
        lemma.setLanguage(lang);
        // check again if the lemma already exists in the new language
        lemmaNameKeyupEvent();
        ckeckLemmaSavability();
    }

    public void verifiedChanged() {
        log(Level.INFO, loginController.getAccount(), "UPDATE Verified field of " + lemma.getFormWrittenRepr() + " to " + lemma.isVerified());
        addFormButtonDisabled = true;
        updateEntryValidity();
        ckeckLemmaSavability();
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

    public void lemmaGenderChanged(AjaxBehaviorEvent e) {
        String gender = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma gender of " + lemma.getFormWrittenRepr() + " to " + gender);
        lemma.setGender(gender);
        addFormButtonDisabled = true;
        ckeckLemmaSavability();
    }

    public void lemmaPersonChanged(AjaxBehaviorEvent e) {
        String person = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma person of " + lemma.getFormWrittenRepr() + " to " + person);
        lemma.setPerson(person);
        addFormButtonDisabled = true;
        lemma.setSaveButtonDisabled(false);
    }

    public void lemmaMoodChanged(AjaxBehaviorEvent e) {
        String mood = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma mood of " + lemma.getFormWrittenRepr() + " to " + mood);
        lemma.setMood(mood);
        addFormButtonDisabled = true;
        lemma.setSaveButtonDisabled(false);
    }

    public void lemmaVoiceChanged(AjaxBehaviorEvent e) {
        String voice = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma voice of " + lemma.getFormWrittenRepr() + " to " + voice);
        lemma.setVoice(voice);
        addFormButtonDisabled = true;
        lemma.setSaveButtonDisabled(false);
    }

    public void lemmaNumberChanged(AjaxBehaviorEvent e) {
        String number = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma number of " + lemma.getFormWrittenRepr() + " to " + number);
        lemma.setNumber(number);
        addFormButtonDisabled = true;
        ckeckLemmaSavability();
    }

    public void lemmaPoSChanged(AjaxBehaviorEvent e) {
        String pos = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma Part-of-Speech of " + lemma.getFormWrittenRepr() + " to " + pos);
        lemma.setPoS(pos);
        addFormButtonDisabled = true;
        ckeckLemmaSavability();
    }

    public void formNumberChanged(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        FormData fd = (FormData) component.getAttributes().get("form");
        String number = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Form number of " + fd.getFormWrittenRepr() + " to " + number);
        fd.setNumber(number);
        fd.setSaveButtonDisabled(isSavableForm(fd));
    }

    public void formGenderChanged(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        FormData fd = (FormData) component.getAttributes().get("form");
        String gender = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Form gender of " + fd.getFormWrittenRepr() + " to " + gender);
        fd.setGender(gender);
        fd.setSaveButtonDisabled(isSavableForm(fd));
    }

    public void formPersonChanged(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        FormData fd = (FormData) component.getAttributes().get("form");
        String person = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Form person of " + fd.getFormWrittenRepr() + " to " + person);
        fd.setPerson(person);
        fd.setSaveButtonDisabled(isSavableForm(fd));
    }

    public void formMoodChanged(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        FormData fd = (FormData) component.getAttributes().get("form");
        String mood = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Form mood of " + fd.getFormWrittenRepr() + " to " + mood);
        fd.setMood(mood);
        fd.setSaveButtonDisabled(isSavableForm(fd));
    }

    public void formVoiceChanged(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        FormData fd = (FormData) component.getAttributes().get("form");
        String voice = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Form voice of " + fd.getFormWrittenRepr() + " to " + voice);
        fd.setVoice(voice);
        fd.setSaveButtonDisabled(isSavableForm(fd));
    }

    private boolean isSavableLemma() {
        if ((lemma.getFormWrittenRepr().isEmpty()) || (lemma.getLanguage().isEmpty()) || (lemma.getPoS().isEmpty())) {
            return true;
        } else {
            if ((lemma.getFormWrittenRepr().replaceAll("\\s", "").length() > 0) && (!lemmAlreadyExists)
                    /**/ && (isAdmissibleLemma)
                    && (lemma.getLanguage().length() > 0)
                    && (lemma.getPoS().length() > 0)) {
                return false;
            } else {
                return true;
            }
        }
    }

    private boolean isSavableForm(FormData fd) {
        if ((fd.getFormWrittenRepr() == null)) {
            return true;
        } else {
            if ((fd.getFormWrittenRepr().replaceAll("\\s", "").length() > 0)
                    && (!formAlreadyExists)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public void removeLemma() throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "DELETE Lemma " + lemma.getFormWrittenRepr());
        lexiconManager.deleteLemma(lemmaCopy, forms, lexiconCreationViewSenseDetail.getSenses());
        info("template.message.deleteLemma.summary", "template.message.deleteLemma.description", lemma.getFormWrittenRepr());
        forms.clear();
        formsCopy.clear();
        lemma.clear();
        lemmaCopy.clear();
        lemmaRendered = false;
        addFormButtonDisabled = true;
        lexiconCreationViewSenseDetail.getSenses().clear();
        lexiconCreationViewSenseDetail.setAddSenseButtonDisabled(true);
        lexiconCreationControllerRelationDetail.resetRelationDetails();
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        lexiconCreationControllerTabViewList.initLemmaTabView(currentLanguage);
        lexiconCreationControllerTabViewList.initFormTabView(currentLanguage);
    }

    public void resetFormDetails() {
        forms.clear();
        formsCopy.clear();
        lemma.clear();
        lemmaCopy.clear();
    }

    public void saveLemma() throws IOException, OWLOntologyStorageException {
        lemma.setSaveButtonDisabled(true);
        lemma.setDeleteButtonDisabled(false);
        addFormButtonDisabled = false;
        if (lemmaCopy.getFormWrittenRepr().isEmpty()) {
            // saving due to a new lemma action
            saveLemma(false, true);
            String entry = LexiconUtil.getIRI(lemma.getFormWrittenRepr(), lemma.getLanguage(), "lemma");
            // check if the lexical entry is available and lock it
            lexiconManager.lock(entry);
            setLocked(false);
            lexiconCreationViewSenseDetail.setLocked(false);
            log(Level.INFO, loginController.getAccount(), "LOCK the lexical entry related to " + entry);
        } else {
            // saving due to a lemma modification action
            if (lemma.getFormWrittenRepr().equals(lemmaCopy.getFormWrittenRepr())) {
                // modification does not concern lemma written representation
                saveLemma(false, false);
            } else {
                // modification concerns also lemma written representation
                saveLemma(true, false);
            }
        }
        createLemmaCopy();
        newAction = false;
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        lexiconCreationControllerTabViewList.initLemmaTabView(currentLanguage);
        lexiconCreationViewSenseDetail.setSenseRendered(true);
    }

    private void saveLemma(boolean renaming, boolean newAction) throws IOException, OWLOntologyStorageException {
        info("template.message.saveLemma.summary", "template.message.saveLemma.description", lemma.getFormWrittenRepr());
        if (renaming) {
            saveForUpdateWithRenameAction();
        } else {
            if (newAction) {
                saveForNewAction();
            } else {
                saveForUpdateAction();
            }
        }
        updateTabViewLists();
    }

    private void saveForNewAction() throws IOException, OWLOntologyStorageException {
        if (lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel())) {
            // word case
            log(Level.INFO, loginController.getAccount(), "SAVE new Lemma and its default sense " + lemma.getFormWrittenRepr());
            lexiconCreationViewSenseDetail.saveDefaultSense(lemma, OntoLexEntity.Class.WORD.getLabel());
        } else {
            // multiword case
            log(Level.INFO, loginController.getAccount(), "SAVE new Multiword and its default sense " + lemma.getFormWrittenRepr());
            lexiconCreationViewSenseDetail.saveDefaultSense(lemma, OntoLexEntity.Class.MULTIWORD.getLabel());
        }
        lexiconCreationViewSenseDetail.setSenseToolbarRendered(true);
        setNewAction(false);
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

    private void saveForUpdateWithRenameAction() throws IOException, OWLOntologyStorageException {
        if (lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel())) {
            // word case
            log(Level.INFO, loginController.getAccount(), "SAVE updated Lemma with renaming from " + lemmaCopy.getFormWrittenRepr() + " to " + lemma.getFormWrittenRepr());
            lexiconManager.saveLemmaWithIRIRenaming(lemmaCopy, lemma);
            lexiconCreationViewSenseDetail.addSense(lemma.getIndividual(), "Lemma");
        } else {
            // multiword case
            log(Level.INFO, loginController.getAccount(), "SAVE updated Multiword Lemma with renaming from " + lemmaCopy.getFormWrittenRepr() + " to " + lemma.getFormWrittenRepr());
            lexiconManager.saveMultiwordLemmaWithIRIRenaming(lemmaCopy, lemma);
            lexiconCreationViewSenseDetail.addSense(lemma.getIndividual(), "Lemma");
        }

    }

    private void updateTabViewLists() {
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        lexiconCreationControllerTabViewList.initLemmaTabView(currentLanguage);
        lexiconCreationControllerTabViewList.initFormTabView(currentLanguage);
    }

    public ArrayList<String> getLexicaLanguages() {
        ArrayList<String> al = new ArrayList();
        for (String lang : lexiconManager.lexicaLanguagesList()) {
            al.add(lang);
        }
        Collections.sort(al);
        return al;
    }

    // invoked when lemma is a multiword in order to get the languages according to the multiword type
    public ArrayList<String> getLexicaLanguages(String mwType) {
        ArrayList<String> al = new ArrayList();
        for (String lang : lexiconManager.lexicaLanguagesList()) {
            if (mwType.equals("bilingual")) {
                if (lang.length() > 4) {
                    al.add(lang);
                }
            } else {
                if (lang.length() <= 4) {
                    al.add(lang);
                }
            }
        }
        return al;
    }

    public List<String> completeTextOnlyForMW(String _lemma) {
        List<Map<String, String>> al = lexiconManager.lemmasList("All languages");
        return getFilteredList(al, _lemma, lemma.getFormWrittenRepr(), true, false);
    }

    public List<String> completeText(String _lemma) {
        List<Map<String, String>> al = lexiconManager.lemmasList("All languages");
        return getFilteredList(al, _lemma, lemma.getFormWrittenRepr(), false, false);
    }

    private List<String> getFilteredList(List<Map<String, String>> list, String keyFilter, String currentLemma, boolean onlyMW, boolean seeAOc) {
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
                        if (seeAOc) {
                            if (m.get("lang").equals("aoc")) {
                                filteredList.add(wr + "@aoc");
                            }
                        } else {
                            filteredList.add(wr + "@" + m.get("lang"));
                        }
                    }
                }
            }
        }
        return filteredList;
    }

    private List<String> getFilteredListForCorr(List<Map<String, String>> list, String keyFilter, String currentLemma) {
        List<String> filteredList = new ArrayList();
        Collections.sort(list, new LexiconComparator("writtenRep"));
        for (Map<String, String> m : list) {
            String wr = m.get("writtenRep");
            if (m.get("corr").equals("true")) {
                if ((wr.startsWith(keyFilter)) && (!wr.isEmpty())) {
                    if (!wr.equals(currentLemma)) {
                        filteredList.add(wr + "@" + m.get("lang"));
                    }
                }
            }
        }
        return filteredList;
    }

    public List<String> completeComponents(String currentComponent) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        Word w = (Word) component.getAttributes().get("currentComponent");
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

    public void onReferenceSelect(Word reference) {
        log(Level.INFO, loginController.getAccount(), "ADD Reference (seeAlso) " + reference.getWrittenRep() + " to the Lemma " + lemma.getFormWrittenRepr());
        lemma.setSaveButtonDisabled(false);
        setLexicalRelationEntry(reference);
        reference.setDeleteButtonDisabled(false);
        reference.setViewButtonDisabled(false);
    }

    // it queries the lexicon in order to get the name of the individual
    private void setLexicalRelationEntry(Word w) {
        String splitted[] = w.getWrittenRep().split("@");
        String lemma = splitted[0];
        String lang = splitted[1];
        Word wd = lexiconManager.getLemma(lemma, lang);
        w.setWrittenRep(wd.getWrittenRep());
        w.setOWLName(wd.getOWLName());
        w.setLanguage(wd.getLanguage());
    }

    public void onWordSelect(Word word) {
        log(Level.INFO, loginController.getAccount(), "UPDATE the word " + word.getWrittenRep() + " of the Multiword " + lemma.getFormWrittenRepr());
        setMultiwordComponentButtons(word);
        lemma.setSaveButtonDisabled(isSavableLemmaMultiword());
        setWordOfMultiword(word);
        word.setDeleteButtonDisabled(false);
        word.setViewButtonDisabled(false);
    }

    // it queries the lexicon in order to get the name of the individual
    private void setWordOfMultiword(Word w) {
        String splitted[] = w.getLabel().split("@");
        String lemma = splitted[0];
        String lang = splitted[1];
        Word wd = lexiconManager.getLemma(lemma, lang);
        w.setWrittenRep(wd.getWrittenRep());
        w.setOWLName(wd.getOWLName().replace("_lemma", "_entry"));
        w.setLanguage(wd.getLanguage());
        w.setOWLComp(wd.getOWLComp());
        w.setLabel(wd.getWrittenRep() + "@" + wd.getLanguage());
    }

    // invoked by the lemma box in order to get the details of a component of a multiword
    public void addEntryOfMultiwordComponent(Word lemma, String relType) {
        log(Level.INFO, loginController.getAccount(), "VIEW Deatils of multiword component " + lemma.getOWLName() + " of " + lemma.getWrittenRep());
        setMultiwordComponentButtons(lemma);
        lemma.setViewButtonDisabled(true);
        lexiconCreationControllerRelationDetail.resetRelationDetails();
        lexiconCreationControllerRelationDetail.setAddButtonsDisabled(false);
        lexiconCreationControllerRelationDetail.setEntryOfLexicalRelation(lemma.getOWLName().replace("_entry", "_lemma"));
        checkForLock(lemma.getOWLName());
        lexiconManager.getLexiconLocker().print();
        lexiconCreationControllerRelationDetail.setRelationLemmaRendered(true);
    }

    private void setMultiwordComponentButtons(Word comp) {
        for (Word w : lemma.getMultiword()) {
            if (comp.getWrittenRep().equals(w.getWrittenRep()) && comp.getLanguage().equals(w.getLanguage()) || w.getWrittenRep().isEmpty()) {
                w.setViewButtonDisabled(true);
            } else {
                w.setViewButtonDisabled(false);
            }
        }
    }

    // invoked by the lemma box in order to get the details of the lexical relation
    public void addEntryOfLexicalRelation(Word w, String relType) {
        log(Level.INFO, loginController.getAccount(), "VIEW Deatils of " + w.getWrittenRep() + " by " + relType + " relation of Lemma " + lemma.getFormWrittenRepr());
        lexiconCreationControllerRelationDetail.resetRelationDetails();
        lexiconCreationControllerRelationDetail.setAddButtonsDisabled(false);
        lexiconCreationControllerRelationDetail.setEntryOfLexicalRelation(w.getOWLName());
        checkForLock(w.getOWLName());
        lexiconManager.getLexiconLocker().print();
        lexiconCreationControllerRelationDetail.setRelationLemmaRendered(true);
        lexiconCreationControllerRelationDetail.setCurrentLexicalEntry(w.getOWLName());
        lexiconCreationControllerRelationDetail.setActiveTab(2);
    }

    public void setLexicalRelationButtons(boolean b) {
        for (Word w : lemma.getSeeAlso()) {
            w.setViewButtonDisabled(b);
            w.setDeleteButtonDisabled(b);
        }
    }

    // invoked by controller after an user selected add a reference (seeAlso) to lemma
    public void addReference() {
        log(Level.INFO, loginController.getAccount(), "ADD empty reference (seeAlso) to lemma " + lemma.getFormWrittenRepr());
        Word reference = new Word();
        reference.setViewButtonDisabled(true);
        lemma.getSeeAlso().add(reference);
    }

    public void removeReference(Word reference) {
        log(Level.INFO, loginController.getAccount(), "REMOVE reference (seeAlso) " + (reference.getWrittenRep().isEmpty() ? " empty see also" : reference.getWrittenRep()) + " from " + lemma.getFormWrittenRepr());
        lemma.getSeeAlso().remove(reference);
        lemma.setSaveButtonDisabled(false);
        relationPanelCheck(reference.getOWLName());
    }

    private void relationPanelCheck(String OWLName) {
        if (lexiconCreationControllerRelationDetail.getCurrentLexicalEntry().equals(OWLName)) {
            lexiconCreationControllerRelationDetail.resetRelationDetails();
            lexiconCreationControllerRelationDetail.setCurrentLexicalEntry("");
        }
    }

    public ArrayList<String> getPoS() {
        return propertyValue.getPoS(lemma.getType());
    }

    public ArrayList<String> getNumber() {
        return propertyValue.getNumber();
    }

    public ArrayList<String> getGender() {
        return propertyValue.getGender();
    }

    public ArrayList<String> getPerson() {
        return propertyValue.getPerson();
    }

    public ArrayList<String> getMood() {
        return propertyValue.getMood();
    }

    public ArrayList<String> getVoice() {
        return propertyValue.getVoice();
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
