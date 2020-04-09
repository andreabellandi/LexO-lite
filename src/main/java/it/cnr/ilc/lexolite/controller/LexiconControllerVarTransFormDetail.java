/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.manager.LemmaData;
import it.cnr.ilc.lexolite.manager.LemmaData.LexicalRelation;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import it.cnr.ilc.lexolite.util.LexiconUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Level;
import javax.faces.model.SelectItem;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerVarTransFormDetail extends BaseController implements Serializable {

    @Inject
    private LexiconControllerFormDetail lexiconControllerFormDetail;
    @Inject
    private LexiconControllerLinkedLexicalEntryDetail lexiconControllerLinkedLexicalEntryDetail;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LoginController loginController;
    @Inject
    private PropertyValue propertyValue;

    private boolean varTransRendered = false;
    private boolean saveButtonDisabled = true;

    private LemmaData lemmaVarTrans = new LemmaData();
    private LemmaData lemmaVarTransCopy = new LemmaData();

    public LemmaData getLemmaVarTrans() {
        return lemmaVarTrans;
    }

    public void setLemmaVarTrans(LemmaData lemmaVarTrans) {
        this.lemmaVarTrans = lemmaVarTrans;
    }

    public LemmaData getLemmaVarTransCopy() {
        return lemmaVarTransCopy;
    }

    public void setLemmaVarTransCopy(LemmaData lemmaVarTransCopy) {
        this.lemmaVarTransCopy = lemmaVarTransCopy;
    }

    public boolean isSaveButtonDisabled() {
        return saveButtonDisabled;
    }

    public void setSaveButtonDisabled(boolean saveButtonDisabled) {
        this.saveButtonDisabled = saveButtonDisabled;
    }

    public boolean isVarTransRendered() {
        return varTransRendered;
    }

    public void setVarTransRendered(boolean varTransRendered) {
        this.varTransRendered = varTransRendered;
    }

    // for keeping track of modifies
    private void createLemmaCopy() {
        this.lemmaVarTransCopy.setLexRels(copyLexicalRelationData(lemmaVarTrans.getLexRels()));
        this.lemmaVarTransCopy.setReifiedLexRels(copyReifLexicalRelationData(lemmaVarTrans.getReifiedLexRels()));
    }

    private ArrayList<LexicalRelation> copyLexicalRelationData(ArrayList<LexicalRelation> alw) {
        ArrayList<LexicalRelation> _alw = new ArrayList();
        for (LexicalRelation w : alw) {
            LexicalRelation _w = new LexicalRelation();
            _w.setOWLName(w.getOWLName());
            _w.setLanguage(w.getLanguage());
            _w.setWrittenRep(w.getWrittenRep());
            _w.setRelation(w.getRelation());
            _alw.add(_w);
        }
        return _alw;
    }

    private ArrayList<LemmaData.ReifiedLexicalRelation> copyReifLexicalRelationData(ArrayList<LemmaData.ReifiedLexicalRelation> alw) {
        ArrayList<LemmaData.ReifiedLexicalRelation> _alw = new ArrayList();
        for (LemmaData.ReifiedLexicalRelation w : alw) {
            LemmaData.ReifiedLexicalRelation _w = new LemmaData.ReifiedLexicalRelation();
            _w.setCategory(w.getCategory());
            _w.setSource(w.getSource());
            _w.setSourceLanguage(w.getSourceLanguage());
            _w.setSourceOWLName(w.getSourceOWLName());
            _w.setSourceWrittenRep(w.getSourceWrittenRep());
            _w.setTarget(w.getTarget());
            _w.setTargetLanguage(w.getTargetLanguage());
            _w.setTargetOWLName(w.getTargetOWLName());
            _w.setTargetWrittenRep(w.getTargetWrittenRep());
            _alw.add(_w);
        }
        return _alw;
    }

    public void onLexRelSelect(LemmaData.LexicalRelation lr) {
        lemmaVarTrans.setSaveButtonDisabled(false);
        setLexicalRelationEntry(lr);
        lr.setDeleteButtonDisabled(false);
        lr.setViewButtonDisabled(false);
        log(Level.INFO, loginController.getAccount(), "ADD direct lexical relation " + lr.getRelation() + " with " + lr.getWrittenRep() + " to the Lemma " + lemmaVarTrans.getFormWrittenRepr());
    }

    public void onReifLexRelSelect(LemmaData.ReifiedLexicalRelation rlr) {
        lemmaVarTrans.setSaveButtonDisabled(false);
        setLexicalRelationEntry(rlr);
        rlr.setDeleteButtonDisabled(false);
        rlr.setViewButtonDisabled(false);
        log(Level.INFO, loginController.getAccount(), "ADD indirect lexical relation  with " + rlr.getTarget() + " to the Lemma " + lemmaVarTrans.getFormWrittenRepr() + " with category " + rlr.getCategory());
    }

    // it queries the lexicon in order to get the name of the individual
    private void setLexicalRelationEntry(LemmaData.LexicalRelation w) {
        String splitted[] = w.getWrittenRep().split("@");
        String lemma = splitted[0].split("\\([aA-zZ]+\\)")[0].trim();
        String lang = splitted[1];
        LemmaData.Word wd = lexiconManager.getLemma(lemma, lang);
        w.setWrittenRep(wd.getWrittenRep());
        w.setOWLName(wd.getOWLName());
        w.setLanguage(wd.getLanguage());
    }

    private void setLexicalRelationEntry(LemmaData.ReifiedLexicalRelation w) {
        String splitted[] = w.getTargetWrittenRep().split("@");
        String lemma = splitted[0].split("\\([aA-zZ]+\\)")[0].trim();
        String lang = splitted[1];
        LemmaData.Word wd = lexiconManager.getLemma(lemma, lang);
        w.setTargetWrittenRep(wd.getWrittenRep());
        w.setTargetLanguage(wd.getLanguage());
        //w.setTargetOWLName(LexiconUtil.getIRI(w.getTargetWrittenRep(), w.getTargetLanguage(), "entry"));
        w.setTargetOWLName(wd.getOWLName().replace("_lemma", "_entry"));
        w.setSourceWrittenRep(lexiconControllerFormDetail.getLemma().getFormWrittenRepr());
        w.setSourceLanguage(lexiconControllerFormDetail.getLemma().getLanguage());
        w.setSourceOWLName(lexiconControllerFormDetail.getLemma().getIndividual().replace("_lemma", "_entry"));
    }

    public void addDirectLexicalRelation() {
        log(Level.INFO, loginController.getAccount(), "ADD empty direct lexical relation of " + lemmaVarTrans.getFormWrittenRepr());
        LemmaData.LexicalRelation lr = new LemmaData.LexicalRelation();
        lr.setViewButtonDisabled(true);
        setSaveButtonDisabled(false);
        lemmaVarTrans.getLexRels().add(lr);
    }

    public void addIndirectLexicalRelation() {
        log(Level.INFO, loginController.getAccount(), "ADD empty indirect lexical relation of " + lemmaVarTrans.getFormWrittenRepr());
        LemmaData.ReifiedLexicalRelation rlr = new LemmaData.ReifiedLexicalRelation();
        rlr.setViewButtonDisabled(true);
        setSaveButtonDisabled(false);
        lemmaVarTrans.getReifiedLexRels().add(rlr);
    }

    // invoked by the controller after an user selected the varTrans tab
    public void addLexicalRelations() {
        this.lemmaVarTrans = lexiconManager.getVarTransAttributes(lemmaVarTrans.getIndividual());
        createLemmaCopy();
    }

    public void saveLemma() throws IOException, OWLOntologyStorageException {
        setSaveButtonDisabled(true);
        log(Level.INFO, loginController.getAccount(), "SAVE updated Lemma vartrans part");
        lexiconManager.updateLemmaVarTrans(lemmaVarTransCopy, lemmaVarTrans);
        createLemmaCopy();
    }

    // invoked by the lemma box in order to get the details of the lexical relation
    public void addEntryOfLexicalRelation(LemmaData.LexicalRelation lr) {
        log(Level.INFO, loginController.getAccount(), "VIEW Deatils of " + lr.getWrittenRep() + " by " + lr.getRelation()
                + " relation of Lemma " + lexiconControllerFormDetail.getLemma().getFormWrittenRepr());
        lexiconControllerLinkedLexicalEntryDetail.resetRelationDetails();
        lexiconControllerLinkedLexicalEntryDetail.setAddButtonsDisabled(false);
        lexiconControllerLinkedLexicalEntryDetail.setEntryOfLexicalRelation(lr.getOWLName().replace("_entry", "_lemma"));
        lexiconControllerFormDetail.checkForLock(lr.getOWLName().replace("_entry", "_lemma"));
        lexiconManager.getLexiconLocker().print();
        lexiconControllerLinkedLexicalEntryDetail.setRelationLemmaRendered(true);
        lexiconControllerLinkedLexicalEntryDetail.setCurrentLexicalEntry(lr.getOWLName().replace("_entry", "_lemma"));
        lexiconControllerLinkedLexicalEntryDetail.setActiveTab(2);
    }

    public void addEntryOfReifLexicalRelation(LemmaData.ReifiedLexicalRelation lr) {
        log(Level.INFO, loginController.getAccount(), "VIEW Deatils of " + lr.getSourceWrittenRep() + " by " + lr.getCategory()
                + " relation of Lemma " + lexiconControllerFormDetail.getLemma().getFormWrittenRepr());
        lexiconControllerLinkedLexicalEntryDetail.resetRelationDetails();
        lexiconControllerLinkedLexicalEntryDetail.setAddButtonsDisabled(false);
        lexiconControllerLinkedLexicalEntryDetail.setEntryOfLexicalRelation(lr.getTargetOWLName().replace("_entry", "_lemma"));
        lexiconControllerFormDetail.checkForLock(lr.getTargetOWLName());
        lexiconManager.getLexiconLocker().print();
        lexiconControllerLinkedLexicalEntryDetail.setRelationLemmaRendered(true);
        lexiconControllerLinkedLexicalEntryDetail.setCurrentLexicalEntry(lr.getTargetOWLName());
        lexiconControllerLinkedLexicalEntryDetail.setActiveTab(2);
    }

    public void removeLexicalRelation(LemmaData.LexicalRelation lr) {
        log(Level.INFO, loginController.getAccount(), "REMOVE lexical relation " + (lr.getWrittenRep().isEmpty() ? " empty lexical relation" : lr.getWrittenRep())
                + " from " + lexiconControllerFormDetail.getLemma().getFormWrittenRepr());
        lemmaVarTrans.getLexRels().remove(lr);
        lemmaVarTrans.setSaveButtonDisabled(false);
        saveButtonDisabled = false;
        lexiconControllerFormDetail.relationPanelCheck(lr.getOWLName());
    }

    public void removeReifLexicalRelation(LemmaData.ReifiedLexicalRelation rlr) {
        log(Level.INFO, loginController.getAccount(), "REMOVE lexical relation " + (rlr.getTargetWrittenRep().isEmpty() ? " empty lexical relation" : rlr.getTargetWrittenRep())
                + " from " + lexiconControllerFormDetail.getLemma().getFormWrittenRepr());
        lemmaVarTrans.getReifiedLexRels().remove(rlr);
        lemmaVarTrans.setSaveButtonDisabled(false);
        saveButtonDisabled = false;
        lexiconControllerFormDetail.relationPanelCheck(rlr.getTargetOWLName());
    }
    
    public void resetFormDetails() {
        lemmaVarTrans.clear();
        lemmaVarTransCopy.clear();
    }

    public ArrayList<String> getLexicalRelationTypes() {
        return propertyValue.getLexicalRelType();
    }

    public ArrayList<SelectItem> getLexicalCategories() {
        return propertyValue.getLexicalCategory();
    }
    
    public String getPropertyName(String s) {
        String ret = s.trim();
        if (ret.contains(" ")) {
            String[] _ret = ret.split(" ");
            ret = "";
            for (int i = 0; i < _ret.length; i++ ) {
                String upper = "";
                if (i != 0) {
                   upper = _ret[i].substring(0, 1).toUpperCase() + _ret[i].substring(1);
                }
                ret = ret + upper;
            }
        }
        return ret;
    }
    
       public String getClassName(String s) {
        String ret = s.trim();
        if (ret.contains(" ")) {
            String[] _ret = ret.split(" ");
            ret = "";
            for (int i = 0; i < _ret.length; i++ ) {
                if (_ret[i].equals("ac") || _ret[i].equals("to") || _ret[i].equals("for") || _ret[i].equals("rs") || _ret[i].equals("sc") ||
                        _ret[i].equals("oc") || _ret[i].equals("pp")) {
                   ret = ret + _ret[i].toUpperCase();
                } else {
                    ret = ret + _ret[i].substring(0, 1).toUpperCase() + _ret[i].substring(1);
                }
            }
        } else {
            ret = ret.substring(0, 1).toUpperCase() + ret.substring(1);
        }
        return ret;
    }

}
