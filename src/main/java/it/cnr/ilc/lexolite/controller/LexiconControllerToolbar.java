/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.LexOliteProperty;
import it.cnr.ilc.lexolite.constant.OntoLexEntity;
import it.cnr.ilc.lexolite.domain.AccountType;
import it.cnr.ilc.lexolite.manager.AccountManager;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.OntologyManager;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Level;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import it.cnr.ilc.lexolite.constant.Label;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerToolbar extends BaseController implements Serializable {

    @Inject
    private LexiconControllerOntologyDetail lexiconCreationOntologyDetailController;
    @Inject
    private LexiconControllerVarTransFormDetail lexiconCreationControllerVarTransFormDetail;
    @Inject
    private LexiconControllerVarTransSenseDetail lexiconCreationControllerVarTransSenseDetail;
    @Inject
    private LexiconControllerSynSemFormDetail lexiconCreationControllerSynSemFormDetail;
    @Inject
    private LexiconControllerSynSemSenseDetail lexiconCreationControllerSynSemSenseDetail;
    @Inject
    private LexiconControllerLexicalAspect lexiconControllerLexicalAspect;
    @Inject
    private LexiconControllerFormDetail lexiconCreationControllerFormDetail;
    @Inject
    private LexiconControllerSenseDetail lexiconCreationControllerSenseDetail;
    @Inject
    private LexiconControllerLanguageDetail lexiconControllerLanguageDetail;
    @Inject
    private LexiconControllerTabViewList lexiconCreationControllerTabViewList;
    @Inject
    private AccountManager accountManager;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private OntologyManager ontologyManager;
    @Inject
    private LoginController loginController;

    private boolean deleteLexiconReferences;

    public boolean isDeleteLexiconReferences() {
        return deleteLexiconReferences;
    }

    public void setDeleteLexiconReferences(boolean deleteLexiconReferences) {
        this.deleteLexiconReferences = deleteLexiconReferences;
    }

    public void newLanguage() {
        lexiconControllerLanguageDetail.clear();
        log(Level.INFO, loginController.getAccount(), "NEW Language panel opened ");
    }

    public void ontologyImport() {
        //lexiconControllerLanguageDetail.clear();
        log(Level.INFO, loginController.getAccount(), "IMPORT Ontology request ");
    }

    public void lexiconImport() {
        //lexiconControllerLanguageDetail.clear();
        log(Level.INFO, loginController.getAccount(), "IMPORT Lexicon request ");
    }

    public void newLemma(String lemmaType) {
        lexiconManager.unlock();
        lexiconCreationControllerFormDetail.setLocked(false);
        log(Level.INFO, loginController.getAccount(), "NEW Lemma " + lemmaType);
        // check if the lexical entry has to be saved
        resetLexicalEntry();
        lexiconControllerLexicalAspect.setLexicalAspectActive("Core");
        if (lemmaType.equals("MultiwordExpression")) {
            lexiconCreationControllerFormDetail.getLemma().setType(OntoLexEntity.Class.MULTIWORD.getLabel());
        } else {
            if (lemmaType.equals("Affix")) {
                lexiconCreationControllerFormDetail.getLemma().setType(OntoLexEntity.Class.AFFIX.getLabel());
            } else {
                lexiconCreationControllerFormDetail.getLemma().setType(OntoLexEntity.Class.WORD.getLabel());
            }
        }
        lexiconCreationControllerFormDetail.setNewAction(true);
    }

    public void resetLexicalEntry() {
        lexiconCreationControllerSenseDetail.resetSenseDetails();
        lexiconCreationControllerSenseDetail.setAddSenseButtonDisabled(false);
        lexiconCreationControllerSenseDetail.setSenseToolbarRendered(false);
        //lexiconCreationControllerSenseDetail.addSense();
        lexiconCreationControllerFormDetail.resetFormDetails();
        lexiconCreationControllerFormDetail.setFormAlreadyExists(false);
        lexiconCreationControllerFormDetail.setLemmAlreadyExists(false);
        lexiconCreationControllerFormDetail.setIsAdmissibleLemma(true);
        lexiconCreationControllerFormDetail.setLemmaRendered(true);
        lexiconCreationControllerFormDetail.setAddFormButtonDisabled(true);
        lexiconCreationControllerFormDetail.getLemma().clear();
        lexiconCreationControllerFormDetail.getLemmaCopy().clear();
        lexiconCreationControllerFormDetail.getLemma().setIndividual("");
        lexiconCreationControllerFormDetail.getLemmaCopy().setIndividual("");

        lexiconCreationOntologyDetailController.setOntologyClassRendered(false);
        lexiconCreationControllerVarTransFormDetail.setVarTransRendered(false);
        lexiconCreationControllerVarTransFormDetail.resetFormDetails();
        lexiconCreationControllerVarTransSenseDetail.setSenseVarTransRendered(false);
        lexiconCreationControllerVarTransSenseDetail.resetSenseDetails();
        lexiconCreationControllerSynSemFormDetail.setSynSemRendered(false);
        lexiconCreationControllerSynSemFormDetail.resetFormDetails();
        lexiconCreationControllerSynSemSenseDetail.setSenseSynSemRendered(false);
        lexiconCreationControllerSynSemSenseDetail.resetSenseDetails();
    }

    public void openLexicon(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("Successful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
        lexiconManager.loadLexicon(event);
        lexiconCreationControllerTabViewList.initLexicaMenu();
        lexiconCreationControllerTabViewList.setLexiconLanguage("All languages");
        lexiconCreationControllerTabViewList.initLemmaTabView("All languages");
        lexiconCreationControllerTabViewList.initFormTabView("All languages");
        lexiconCreationControllerTabViewList.setEnabledFilter(true);
    }

    public StreamedContent exportLexicon(String format) {
        log(Level.INFO, loginController.getAccount(), "EXPORT lexicon in " + format);
        return lexiconManager.exportOWLLexicon(format);
    }

    public boolean isToolbarEnabled() {
        return accountManager.hasPermission(AccountType.Permission.WRITE_ALL, AccountManager.Access.LEXICON_EDITOR, loginController.getAccount());
    }

    public synchronized void handleOntologyUpload(FileUploadEvent event) throws IOException, OWLOntologyStorageException {
        String msg = "is uploaded.";
        //String previousNamespace = LexOliteProperty.getProperty(Label.ONTOLOGY_NAMESPACE_KEY);
        ontologyManager.loadDomainOntology(event);
        LexOliteProperty.setProperty(Label.ONTOLOGY_NAMESPACE_KEY, ontologyManager.getOntologyID() + "#");
        LexOliteProperty.setProperty(Label.ONTOLOGY_FILE_NAME_KEY, event.getFile().getFileName());
        //String currentNamespace = ontologyManager.getOntologyID() + "#";
        try {
            LexOliteProperty.save();
        } catch (IOException ex) {
            Logger.getLogger(LexiconControllerToolbar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        lexiconCreationControllerTabViewList.initDomainOntologyTabView();
        //if (previousNamespace != null) {
        //if (!previousNamespace.equals(currentNamespace) && deleteLexiconReferences) {
        if (deleteLexiconReferences) {
            lexiconManager.deleteOntologyReferences();
            msg = "is uploaded. References deleted.";
        }

        //}
        //}
        LexOliteProperty.load();
        FacesMessage message = new FacesMessage("Successful", event.getFile().getFileName() + msg);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public synchronized void handleLexiconUpload(FileUploadEvent event) throws IOException, OWLOntologyStorageException {
        lexiconManager.persist();
        lexiconManager.loadLexicon(event);
        LexOliteProperty.setProperty(Label.LEXICON_NAMESPACE_KEY, lexiconManager.getLexiconNamespace().contains("#") ? lexiconManager.getLexiconNamespace() : lexiconManager.getLexiconNamespace() + "#");
        LexOliteProperty.setProperty(Label.LEXICON_FILE_NAME_KEY, event.getFile().getFileName());
        try {
            LexOliteProperty.save();
            LexOliteProperty.load();
        } catch (IOException ex) {
            Logger.getLogger(LexiconControllerToolbar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        lexiconCreationControllerTabViewList.initLexicaMenu();
        lexiconCreationControllerTabViewList.setLexiconLanguage("All languages");
        lexiconCreationControllerTabViewList.initLemmaTabView("All languages");
        lexiconCreationControllerTabViewList.initFormTabView("All languages");
        lexiconCreationControllerTabViewList.setEnabledFilter(true);

        FacesMessage message = new FacesMessage("Successful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

}
