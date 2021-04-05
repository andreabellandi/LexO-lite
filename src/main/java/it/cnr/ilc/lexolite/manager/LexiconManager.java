/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.constant.OntoLexEntity;
import it.cnr.ilc.lexolite.controller.BaseController;
import it.cnr.ilc.lexolite.controller.LoginController;
import it.cnr.ilc.lexolite.domain.ExtensionAttribute;
import it.cnr.ilc.lexolite.manager.LemmaData.Word;
import it.cnr.ilc.lexolite.manager.OntologyData.LinguisticReference;
import it.cnr.ilc.lexolite.manager.SenseData.Openable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.rdf.rdfxml.renderer.OWLOntologyXMLNamespaceManager;

/**
 *
 * @author andrea
 */
@ApplicationScoped
public class LexiconManager extends BaseController implements Serializable {

    @Inject
    private LoginController loginController;
    @Inject
    private PropertyValue propertyValue;

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
        if (lexiconQuery == null) {
            throw new NullPointerException("lexiconQuery is null!!");
        }
        cnlq = new CNLQuery(lexiconModel);
        lexiconLocker = new LexiconLocker();
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

    public String getLexiconNamespace() {
        if (lexiconModel != null) {
            //if (!lexiconModel.getOntology().getOntologyID().getOntologyIRI().isEmpty()) {
            //   return lexiconModel.getOntology().getOntologyID().getOntologyIRI().get().toURI().toString();
            // } else {
            OWLDocumentFormat format = lexiconModel.getManager().getOntologyFormat(lexiconModel.getOntology());
            OWLOntologyXMLNamespaceManager nsManager = new OWLOntologyXMLNamespaceManager(lexiconModel.getOntology(), format);
            return nsManager.getDefaultNamespace();
            //}
        } else {
            return "";
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

    public synchronized AttestationRenaming saveLemmaWithIRIRenaming(LemmaData oldLemma, LemmaData newLemma) throws IOException, OWLOntologyStorageException {
        AttestationRenaming renamings = lexiconModel.updateLemmaWithRenaming(oldLemma, newLemma);
        lexiconModel.persist();
        return renamings;
    }

    public synchronized AttestationRenaming saveMultiwordLemmaWithIRIRenaming(LemmaData oldLemma, LemmaData newLemma) throws IOException, OWLOntologyStorageException {
        AttestationRenaming renamings = lexiconModel.updateMultiwordLemmaWithRenaming(oldLemma, newLemma);
        lexiconModel.persist();
        return renamings;
    }

    public synchronized void updateLemma(LemmaData oldLemma, LemmaData newLemma) throws IOException, OWLOntologyStorageException {
        lexiconModel.updateLemma(oldLemma, newLemma);
        lexiconModel.persist();
    }

    public synchronized void updateLemmaForMultiword(LemmaData oldLemma, LemmaData newLemma) throws IOException, OWLOntologyStorageException {
        lexiconModel.updateMultiwordLemma(oldLemma, newLemma);
        lexiconModel.persist();
    }

    public synchronized void updateLemmaVarTrans(LemmaData oldLemma, LemmaData newLemma) throws IOException, OWLOntologyStorageException {
        lexiconModel.updateLemmaVarTrans(oldLemma, newLemma);
        lexiconModel.persist();
    }

    public synchronized void updateSenseVarTrans(ArrayList<SenseData> oldSense, ArrayList<SenseData> newSense) throws IOException, OWLOntologyStorageException {
        lexiconModel.updateSenseVarTrans(oldSense, newSense);
        lexiconModel.persist();
    }

    public synchronized String createSyntacticFrame(String lemma, LemmaData.SynFrame synFrame) throws IOException, OWLOntologyStorageException {
        String sfName = lexiconModel.createSyntacticFrame(lemma, synFrame);
        lexiconModel.persist();
        return sfName;
    }

    public synchronized void updateSyntacticFrame(LemmaData.SynFrame oldSynFrame, LemmaData.SynFrame newSynFrame) throws IOException, OWLOntologyStorageException {
        lexiconModel.updateSyntacticFrame(oldSynFrame, newSynFrame);
        lexiconModel.persist();
    }

    public synchronized void updateSenseSynSem(SenseData oldSense, SenseData newSense) throws IOException, OWLOntologyStorageException {
        lexiconModel.updateSenseSynSem(oldSense, newSense);
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

    public synchronized String saveFormWithIRIRenaming(FormData oldForm, FormData newForm, LemmaData ld) throws IOException, OWLOntologyStorageException {
        String newInstanceName = lexiconModel.addFormWithRenaming(oldForm, newForm, ld);
        lexiconModel.persist();
        return newInstanceName;
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
        if (wordType.equals(OntoLexEntity.Class.WORD.getLabel()) || wordType.equals(OntoLexEntity.Class.AFFIX.getLabel())) {
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

    public synchronized void deleteOntologyReferences() throws IOException, OWLOntologyStorageException {
        lexiconModel.deleteOntologyreferences();
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

    public synchronized ArrayList<String> languageDescription(String lang) {
        return lexiconQuery.getLanguageDescription(lang);
    }

    public synchronized ArrayList<String> languageCreator(String lang) {
        return lexiconQuery.getLanguageCreator(lang);
    }

    // invoked in order to get lemma attributes of a specific lemma
    public synchronized LemmaData getLemmaAttributes(String lemma, Set<String> morphoTraits, ArrayList<ExtensionAttribute> alea) {
        return lexiconQuery.getLemmaAttributes(lemma, morphoTraits, alea);
    }

    public synchronized LemmaData getLemmaAttributes(String lemma) {
        return lexiconQuery.getLemmaAttributes(lemma, propertyValue.getMorphoTrait(), null);
    }

    // invoked in order to retrieve the data of the lemma involved in sublemma or collocation relation
    public synchronized Word getLemma(String lemma, String lang) {
        return lexiconQuery.getLemma(lemma, lang);
    }

    // invoked in order to get a lemma and its attributes of a specific form
    public synchronized LemmaData getLemmaEntry(String form, Set<String> morphoTraits, ArrayList<ExtensionAttribute> alea) {
        return lexiconQuery.getLemmaEntry(form, morphoTraits, alea);
    }

    // invoked in order to get a lemma and its attributes of a specific sense
    public synchronized LemmaData getLemmaOfSense(String sense, Set<String> morphoTraits) {
        return lexiconQuery.getLemmaOfSense(sense, morphoTraits);
    }

    public synchronized LemmaData getLemmaOfSense(String sense) {
        return lexiconQuery.getLemmaOfSense(sense);
    }

    public synchronized ArrayList<FormData> getFormsOfLemma(String lemma, String lang, Set<String> morphoTraits, ArrayList<ExtensionAttribute> alea) {
        return lexiconQuery.getFormsOfLemma(lemma, lang, morphoTraits, alea);
    }

    public synchronized ArrayList<FormData> getFormsOfLemma(String lemma, String lang, ArrayList<ExtensionAttribute> alea) {
        return lexiconQuery.getFormsOfLemma(lemma, lang, propertyValue.getMorphoTrait(), alea);
    }

    public synchronized ArrayList<SenseData> getSensesOfLemma(String lemma, ImageManager imageManager, List<ReferenceMenuTheme> l, ArrayList<ExtensionAttribute> alea) {
        return lexiconQuery.getSensesOfLemma(lemma, l, imageManager, alea);
    }

    public synchronized ArrayList<SenseData> getSensesOfForm(String form, ImageManager imageManager, List<ReferenceMenuTheme> l, ArrayList<ExtensionAttribute> alea) {
        return lexiconQuery.getSensesOfForm(form, l, imageManager, alea);
    }

    public synchronized ArrayList<SenseData> getSenses(String sense, ImageManager imageManager, List<ReferenceMenuTheme> l, ArrayList<ExtensionAttribute> alea) {
        return lexiconQuery.getOtherSenses(sense, l, imageManager, alea);
    }

    public synchronized List<SelectItem> getSensesByLanguage(String sense, String lang) {
        return lexiconQuery.getSensesByLanguage(lang);
    }

    public synchronized void persist() throws IOException, OWLOntologyStorageException {
        if (lexiconModel != null) {
            lexiconModel.persist();
        }
    }

    // invoked by advanced filter
    public synchronized List<Map<String, String>> advancedFilter_lemmas() {
        return lexiconQuery.advancedFilter_lemmas();
    }

    // CNL panel queries //
    public synchronized List<Map<String, String>> ontoQueryGroup_1_lemmas(String ontoClass) {
        return cnlq.ontoQueryGroup_1_lemmas(ontoClass);
    }

    public synchronized List<Map<String, String>> ontoQueryGroup_1_forms(String ontoClass) {
        return cnlq.ontoQueryGroup_1_forms(ontoClass);
    }

    // query for varTrans module //
    // invoked in order to add lemma varTrans attributes to a specific lemma
    public synchronized LemmaData getVarTransAttributes(String lemma) {
        return lexiconQuery.getVarTransAttributes(lemma.replace("_lemma", "_entry"));
    }

    public synchronized ArrayList<SenseData> getSensesVarTransAttributesOfLemma(ArrayList<SenseData> asd) {
        return lexiconQuery.getSensesVarTransAttributesOfLemma(asd);
    }

    // query for symSem module //
    // invoked in order to add lemma synsem attributes to a specific lemma
    public synchronized LemmaData getSynSemAttributes(String lemma) {
        return lexiconQuery.getSynSemAttributes(lemma.replace("_lemma", "_entry"));
    }

    public synchronized ArrayList<SenseData> getSensesSynSemAttributesOfSense(ArrayList<SenseData> asd) {
        return lexiconQuery.getSensesSynSemAttributesOfSense(asd);
    }

    public ArrayList<LinguisticReference> getReferencingByOntology(String clazz, LinguisticReference.ReferenceType type) {
        return lexiconQuery.getReferencingByOntology(clazz, type);
    }
}
