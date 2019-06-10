/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.constant.OntoLexEntity;
import it.cnr.ilc.lexolite.controller.BaseController;
import it.cnr.ilc.lexolite.controller.LoginController;
import it.cnr.ilc.lexolite.manager.LemmaData.Word;
import it.cnr.ilc.lexolite.manager.SenseData.Openable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author andrea
 */
@ApplicationScoped
public class LexiconManager extends BaseController implements Serializable {

    @Inject
    private LoginController loginController;

    private static LexiconModel lexiconModel;
    private static LexiconQuery lexiconQuery;
    private CNLQuery cnlq;
    private static LexiconLocker lexiconLocker;

    private static LexiconManager instance;

    public static LexiconManager getInstance() {
        if (instance == null) {
            instance = new LexiconManager();
            instance.deafult_loadLexicon();
        }
        return instance;
    }

    public void loadLexicon(FileUploadEvent f) {
        lexiconModel = new LexiconModel(f);
        lexiconQuery = new LexiconQuery(lexiconModel);
    }

    public void deafult_loadLexicon() {
        if (lexiconModel == null) {
            lexiconModel = new LexiconModel();
            lexiconQuery = new LexiconQuery(lexiconModel);
            if (lexiconQuery == null) {
                throw new NullPointerException("lexiconQuery is null!!");
            }
            cnlq = new CNLQuery(lexiconModel);
            lexiconLocker = new LexiconLocker();
        } else {
        }
    }

    public LexiconQuery getLexiconQuery() {
        return lexiconQuery;
    }

    public CNLQuery getCnlq() {
        return cnlq;
    }

    public LexiconLocker getLexiconLocker() {
        return lexiconLocker;
    }

    public synchronized boolean checkForLock(String uri) {
        if (lexiconLocker.isLocked(uri)) {
            return true;
        } else {
            lexiconLocker.lock(loginController.getAccount().getUsername(), uri);
            return false;
        }
    }

    public synchronized void lock(String uri) {
        lexiconLocker.lock(loginController.getAccount().getUsername(), uri);
    }

    public synchronized String getLockingUser(String uri) {
        return lexiconLocker.getUser(uri);
    }

    public synchronized boolean unlock() {
        return lexiconLocker.unlock(loginController.getAccount().getUsername());
    }

    public synchronized boolean unlock(String le) {
        return lexiconLocker.unlock(loginController.getAccount().getUsername(), le);
    }

    public synchronized Map<String, String> getLexiconLockTable() {
        return lexiconLocker.getLexiconLockTable();
    }

    public synchronized StreamedContent exportOWLLexicon(String format) {
        return lexiconModel.export(format);
    }

    public synchronized void saveNewLanguage(String... params) throws IOException, OWLOntologyStorageException {
        lexiconModel.addNewLangLexicon(params);
        lexiconModel.persist();
    }

    public synchronized void saveLemmaNote(LemmaData ld, String oldNote) throws IOException, OWLOntologyStorageException {
        lexiconModel.saveLemmaNote(ld, oldNote);
        lexiconModel.persist();
    }

    public synchronized void saveLemma(LemmaData ld) throws IOException, OWLOntologyStorageException {
        String lexiconInstance = lexiconQuery.getLexicon(ld.getLanguage());
        lexiconModel.addLemma(ld, lexiconInstance);
        lexiconModel.persist();
    }

    public synchronized void saveLemmaWithIRIRenaming(LemmaData oldLemma, LemmaData newLemma) throws IOException, OWLOntologyStorageException {
        lexiconModel.updateLemmaWithRenaming(oldLemma, newLemma);
        lexiconModel.persist();
    }

    public synchronized void saveMultiwordLemmaWithIRIRenaming(LemmaData oldLemma, LemmaData newLemma) throws IOException, OWLOntologyStorageException {
        lexiconModel.updateMultiwordLemmaWithRenaming(oldLemma, newLemma);
        lexiconModel.persist();
    }

    public synchronized void updateLemma(LemmaData oldLemma, LemmaData newLemma) throws IOException, OWLOntologyStorageException {
        lexiconModel.updateLemma(oldLemma, newLemma);
        lexiconModel.persist();
    }

    public synchronized void updateLemmaForMultiword(LemmaData oldLemma, LemmaData newLemma) throws IOException, OWLOntologyStorageException {
        lexiconModel.updateMultiwordLemma(oldLemma, newLemma);
        lexiconModel.persist();
    }

    // invoked in order to retrieve the component of a multiword at a specific position
    public synchronized String getComponentAtPosition(String entry, String position) {
        return lexiconQuery.getComponentAtPosition(entry.replace("_lemma", "_entry"), position);
    }

    // save the new multiword lemma
    public synchronized void saveLemmaForNewAction(LemmaData ld) throws IOException, OWLOntologyStorageException {
        String lexiconInstance = lexiconQuery.getLexicon(ld.getLanguage());
        lexiconModel.addMultiwordLemma(ld, lexiconInstance);
        lexiconModel.persist();
    }

    public synchronized void saveFormNote(FormData fd, String oldNote) throws IOException, OWLOntologyStorageException {
        lexiconModel.saveFormNote(fd, oldNote);
        lexiconModel.persist();
    }

    public synchronized void saveForm(FormData fd, LemmaData ld) throws IOException, OWLOntologyStorageException {
        lexiconModel.addForm(fd, ld);
        lexiconModel.persist();
    }

    public synchronized void saveFormWithIRIRenaming(FormData oldForm, FormData newForm, LemmaData ld) throws IOException, OWLOntologyStorageException {
        lexiconModel.addFormWithRenaming(oldForm, newForm, ld);
        lexiconModel.persist();
    }

    public synchronized void updateForm(FormData oldForm, FormData newForm, LemmaData ld) throws IOException, OWLOntologyStorageException {
        lexiconModel.updateForm(oldForm, newForm, ld);
        lexiconModel.persist();
    }

    public synchronized void saveSenseNote(SenseData sd, String oldNote) throws IOException, OWLOntologyStorageException {
        lexiconModel.saveSenseNote(sd, oldNote);
        lexiconModel.persist();
    }

    public synchronized void saveSense(SenseData sd, LemmaData ld) throws IOException, OWLOntologyStorageException {
        lexiconModel.addSense(sd, ld);
        lexiconModel.persist();
    }

    public synchronized void saveLemmaAndDefaultSense(SenseData sd, LemmaData ld, String wordType) throws IOException, OWLOntologyStorageException {
        String lexiconInstance = lexiconQuery.getLexicon(ld.getLanguage());
        if (wordType.equals(OntoLexEntity.Class.WORD.getLabel())) {
            lexiconModel.addLemma(ld, lexiconInstance);
        } else {
            lexiconModel.addMultiwordLemma(ld, lexiconInstance);
        }
        lexiconModel.addSense(sd, ld);
        lexiconModel.persist();
    }

    public synchronized void saveSenseRelation(SenseData oldSense, SenseData newSense) throws IOException, OWLOntologyStorageException {
        lexiconModel.addSenseRelation(oldSense, newSense);
        lexiconModel.persist();
    }

    public synchronized void deleteSense(SenseData sd) throws IOException, OWLOntologyStorageException {
        lexiconModel.deleteSense(sd);
        lexiconModel.persist();
    }

    public synchronized void deleteForm(FormData fd) throws IOException, OWLOntologyStorageException {
        lexiconModel.deleteForm(fd);
        lexiconModel.persist();
    }

    public synchronized void deleteLemma(LemmaData ld, List<FormData> forms, List<SenseData> senses) throws IOException, OWLOntologyStorageException {
        for (FormData fd : forms) {
            log(Level.INFO, loginController.getAccount(), "DELETE Form " + fd.getFormWrittenRepr() + " of lemma " + ld.getFormWrittenRepr());
            lexiconModel.deleteForm(fd);
        }
        for (SenseData sd : senses) {
            log(Level.INFO, loginController.getAccount(), "DELETE Sense " + sd.getName() + " of lemma " + ld.getFormWrittenRepr());
            lexiconModel.deleteSense(sd);
        }
        lexiconModel.deleteLemma(ld);
        lexiconModel.persist();
    }

    public synchronized List<Map<String, String>> lemmasList(String lang) {
        return lexiconQuery.getLemmas(lang);
    }

    public synchronized List<Map<String, String>> formsList(String lang) {
        return lexiconQuery.getForms(lang);
    }

    public synchronized List<Map<String, String>> sensesList(String lang) {
        return lexiconQuery.getSenses(lang);
    }

    public synchronized ArrayList<String> lexicaLanguagesList() {
        return lexiconQuery.getLexicaLanguages();
    }

    public synchronized ArrayList<String> lexicalizazions(String entry) {
        return lexiconQuery.getLexicalizations(entry);
    }

    // invoked in order to get lemma attributes of a specific lemma
    public synchronized LemmaData getLemmaAttributes(String lemma) {
        return lexiconQuery.getLemmaAttributes(lemma);
    }

    // invoked in order to retrieve the data of the lemma involved in sublemma or collocation relation
    public synchronized Word getLemma(String lemma, String lang) {
        return lexiconQuery.getLemma(lemma, lang);
    }

    // invoked in order to get a lemma and its attributes of a specific form
    public synchronized LemmaData getLemmaEntry(String form) {
        return lexiconQuery.getLemmaEntry(form);
    }

    // invoked in order to get a lemma and its attributes of a specific sense
    public synchronized LemmaData getLemmaOfSense(String sense) {
        return lexiconQuery.getLemmaOfSense(sense);
    }

    public synchronized ArrayList<FormData> getFormsOfLemma(String lemma, String lang) {
        return lexiconQuery.getFormsOfLemma(lemma, lang);
    }

    public synchronized ArrayList<SenseData> getSensesOfLemma(String lemma) {
        return lexiconQuery.getSensesOfLemma(lemma);
    }

    public synchronized ArrayList<SenseData> getSensesOfForm(String form) {
        return lexiconQuery.getSensesOfForm(form);
    }

    public synchronized ArrayList<SenseData> getSenses(String sense) {
        return lexiconQuery.getOtherSenses(sense);
    }

    public synchronized List<SelectItem> getSensesByLanguage(String sense, String lang) {
        return lexiconQuery.getSensesByLanguage(lang);
    }

    // invoked by PropertyValue bean in order to get PoS values from lexinfo
    public synchronized List<SelectItem> getPoS() {
        return lexiconQuery.getPoS();
    }

    // invoked by PropertyValue bean in order to get gender values from lexinfo
    public synchronized ArrayList<String> getGenders() {
        return lexiconQuery.getGenders();
    }

    // invoked by PropertyValue bean in order to get number values from lexinfo
    public synchronized ArrayList<String> getNumbers() {
        return lexiconQuery.getNumbers();
    }

    // invoked by advanced filter
    public synchronized List<Map<String, String>> advancedFilter_lemmas() {
        return lexiconQuery.advancedFilter_lemmas();
    }

    // invoked by advanced filter
    public synchronized List<Map<String, String>> advancedFilter_forms() {
        return lexiconQuery.advancedFilter_forms();
    }

    // CNL panel queries //
    public synchronized List<Map<String, String>> ontoQueryGroup_1_lemmas(String ontoClass) {
        return cnlq.ontoQueryGroup_1_lemmas(ontoClass);
    }

    public synchronized List<Map<String, String>> ontoQueryGroup_1_forms(String ontoClass) {
        return cnlq.ontoQueryGroup_1_forms(ontoClass);
    }

}
