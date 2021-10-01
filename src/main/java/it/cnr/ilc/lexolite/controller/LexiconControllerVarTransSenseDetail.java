/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.MelchuckModelExtension;
import it.cnr.ilc.lexolite.manager.LemmaData;
import javax.faces.model.SelectItem;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import it.cnr.ilc.lexolite.manager.SenseData;
import it.cnr.ilc.lexolite.manager.SenseData.LexicalFunction;
import it.cnr.ilc.lexolite.manager.SenseData.ReifiedSenseRelation;
import it.cnr.ilc.lexolite.manager.SenseData.ReifiedTranslationRelation;
import it.cnr.ilc.lexolite.manager.SenseData.SenseRelation;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.event.Level;
import org.primefaces.event.SlideEndEvent;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerVarTransSenseDetail extends BaseController implements Serializable {

    @Inject
    private LexiconControllerSenseDetail lexiconControllerSenseDetail;
    @Inject
    private LexiconControllerTabViewList lexiconControllerTabViewList;
    @Inject
    private LexiconControllerFormDetail lexiconControllerFormDetail;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LoginController loginController;
    @Inject
    private PropertyValue propertyValue;
    @Inject
    private LexiconControllerDictionary lexiconControllerDictionary;

    private boolean senseVarTransRendered = false;

    public ArrayList<SenseData> getSensesVarTransCopy() {
        return sensesVarTransCopy;
    }

    public void setSensesVarTransCopy(ArrayList<SenseData> sensesVarTransCopy) {
        this.sensesVarTransCopy = sensesVarTransCopy;
    }

    private ArrayList<SenseData> sensesVarTrans = new ArrayList<>();
    private ArrayList<SenseData> sensesVarTransCopy = new ArrayList<>();

    public List<SenseData> getSensesVarTrans() {
        return sensesVarTrans;
    }

    public SenseData getSenseVarTrans(String sense) {
        for (SenseData sd : sensesVarTrans) {
            if (sd.getName().equals(sense)) {
                return sd;
            }
        }
        return null;
    }

    public boolean isSenseVarTransRendered() {
        return senseVarTransRendered;
    }

    public void setSenseVarTransRendered(boolean senseVarTransRendered) {
        this.senseVarTransRendered = senseVarTransRendered;
    }

    public void addSenseRelations() {
        this.sensesVarTrans = lexiconManager.getSensesVarTransAttributesOfLemma((ArrayList<SenseData>) lexiconControllerSenseDetail.getSenses());
        createSensesCopy();
    }

    // for keeping track of modifies
    private void createSensesCopy() {
        sensesVarTransCopy.clear();
        for (SenseData sd : sensesVarTrans) {
            sensesVarTransCopy.add(copyVarTransSenseData(sd));
        }
    }

    private SenseData copyVarTransSenseData(SenseData sd) {
        SenseData _sd = new SenseData();
        _sd.setName(sd.getName());
        _sd.setSenseRels(copySenseRelationData(sd.getSenseRels()));
        _sd.setReifiedSenseRels(copyReifiedSenseRelsData(sd.getReifiedSenseRels()));
        _sd.setReifiedTranslationRels(copyReifiedTranslationRelsData(sd.getReifiedTranslationRels()));
        _sd.setLexicalFunctions(copyLexicalFunctionData(sd.getLexicalFunctions()));
        return _sd;
    }

    private ArrayList<SenseRelation> copySenseRelationData(ArrayList<SenseRelation> alw) {
        ArrayList<SenseRelation> _alw = new ArrayList();
        for (SenseRelation w : alw) {
            SenseRelation _w = new SenseRelation();
            _w.setLanguage(w.getLanguage());
            _w.setWrittenRep(w.getWrittenRep());
            _w.setRelation(w.getRelation());
            _alw.add(_w);
        }
        return _alw;
    }

    private ArrayList<ReifiedSenseRelation> copyReifiedSenseRelsData(ArrayList<ReifiedSenseRelation> alw) {
        ArrayList<ReifiedSenseRelation> _alw = new ArrayList();
        for (ReifiedSenseRelation w : alw) {
            ReifiedSenseRelation _w = new ReifiedSenseRelation();
            _w.setCategory(w.getCategory());
            _w.setSource(w.getSource());
            _w.setSourceLanguage(w.getSourceLanguage());
            _w.setSource(w.getSource());
            _w.setSourceWrittenRep(w.getSourceWrittenRep());
            _w.setTarget(w.getTarget());
            _w.setTargetLanguage(w.getTargetLanguage());
            _w.setTarget(w.getTarget());
            _w.setTargetWrittenRep(w.getTargetWrittenRep());
            _alw.add(_w);
        }
        return _alw;
    }

    private ArrayList<SenseData.ReifiedTranslationRelation> copyReifiedTranslationRelsData(ArrayList<SenseData.ReifiedTranslationRelation> alw) {
        ArrayList<SenseData.ReifiedTranslationRelation> _alw = new ArrayList();
        for (SenseData.ReifiedTranslationRelation w : alw) {
            SenseData.ReifiedTranslationRelation _w = new SenseData.ReifiedTranslationRelation();
            _w.setCategory(w.getCategory());
            _w.setSource(w.getSource());
            _w.setSourceLanguage(w.getSourceLanguage());
            _w.setSource(w.getSource());
            _w.setSourceWrittenRep(w.getSourceWrittenRep());
            _w.setTarget(w.getTarget());
            _w.setTargetLanguage(w.getTargetLanguage());
            _w.setTarget(w.getTarget());
            _w.setTargetWrittenRep(w.getTargetWrittenRep());
            _alw.add(_w);
        }
        return _alw;
    }

    private ArrayList<SenseData.LexicalFunction> copyLexicalFunctionData(ArrayList<SenseData.LexicalFunction> alw) {
        ArrayList<SenseData.LexicalFunction> _alw = new ArrayList();
        for (SenseData.LexicalFunction w : alw) {
            SenseData.LexicalFunction _w = new SenseData.LexicalFunction();
            _w.setSource(w.getSource());
            _w.setSourceWrittenRep(w.getSourceWrittenRep());
            _w.setTarget(w.getTarget());
            _w.setLexFunName(w.getLexFunName());
            _w.setTarget(w.getTarget());
            _w.setTargetWrittenRep(w.getTargetWrittenRep());
            _alw.add(_w);
        }
        return _alw;
    }

    public void saveSenseRelation(SenseData sd) throws IOException, OWLOntologyStorageException {
//        log(Level.INFO, loginController.getAccount(), "SAVE Sense " + sd.getName());
//        int order = lexiconControllerSenseDetail.getSenses().indexOf(sd);
//        lexiconManager.saveSenseRelation(sensesCopy.get(order), sd);
//        updateSenseCopy(sd, order);
//        sd.setSaveButtonDisabled(true);
//        setSenseToolbarRendered(true);
//        info("template.message.saveSenseRelation.summary", "template.message.saveSenseRelation.description", sd.getName());
        sd.setSaveButtonDisabled(true);
        log(Level.INFO, loginController.getAccount(), "SAVE updated Sense vartrans part");
        lexiconManager.updateSenseVarTrans(sensesVarTransCopy, sensesVarTrans);
        createSensesCopy();
        if (lexiconControllerDictionary != null) {
            lexiconControllerDictionary.clearCaches();
        }
    }

    public void addSenseDirectRelation(SenseData sd) {
        log(Level.INFO, loginController.getAccount(), "ADD empty sense direct relation to " + sd.getName());
        SenseData.SenseRelation sr = new SenseRelation();
        sr.setViewButtonDisabled(true);
        sd.getSenseRels().add(sr);
    }

    public void addIndirectTranslation(SenseData sd) {
        log(Level.INFO, loginController.getAccount(), "ADD empty indirect translation to " + sd.getName());
        SenseData.ReifiedTranslationRelation rtr = new SenseData.ReifiedTranslationRelation();
        rtr.setViewButtonDisabled(true);
        sd.getReifiedTranslationRels().add(rtr);
    }

    public void addTerminologicalRelation(SenseData sd) {
        log(Level.INFO, loginController.getAccount(), "ADD empty terminological relation to " + sd.getName());
        SenseData.ReifiedSenseRelation rsr = new ReifiedSenseRelation();
        rsr.setViewButtonDisabled(true);
        sd.getReifiedSenseRels().add(rsr);
    }

    public void addLexicalFunction(SenseData sd, String lfType) {
        log(Level.INFO, loginController.getAccount(), "ADD empty " + lfType + "lexical function to " + sd.getName());
        SenseData.LexicalFunction lf = new SenseData.LexicalFunction();
        lf.setViewButtonDisabled(true);
        lf.setType(lfType);
        sd.getLexicalFunctions().add(lf);
    }

    public void onSenseRelSelect(SenseData s, SenseRelation sr) {
        setSenseRelationEntry(sr);
        s.setSaveButtonDisabled(false);
        sr.setDeleteButtonDisabled(false);
        sr.setViewButtonDisabled(false);
        s.setSaveButtonDisabled(false);
        log(Level.INFO, loginController.getAccount(), "ADD direct sense relation with " + sr.getWrittenRep());
    }

    public void translationCategoryChanged(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        ReifiedTranslationRelation rtr = (ReifiedTranslationRelation) component.getAttributes().get("senseReifRel");
        SenseData sd = (SenseData) component.getAttributes().get("sense");
        String cat = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE translation category of " + sd.getName() + " to " + cat);
        sd.setSaveButtonDisabled(sd.isSaveButtonDisabled() || rtr.getCategory().isEmpty());

    }

    public void lexicalFunctionChanged(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        LexicalFunction lf = (LexicalFunction) component.getAttributes().get("lf");
        SenseData sd = (SenseData) component.getAttributes().get("sense");
        String selectedLf = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE lexical function of " + sd.getName() + " to " + selectedLf);
        sd.setSaveButtonDisabled(sd.isSaveButtonDisabled() || lf.getLexFunName().isEmpty());

    }

    public void onTranslationRelationSelect(SenseData s, SenseData.ReifiedTranslationRelation slr) {
        s.setSaveButtonDisabled(false);
        setSenseRelationEntry(slr);
        slr.setDeleteButtonDisabled(false);
        slr.setViewButtonDisabled(false);
        s.setSaveButtonDisabled(false);
        slr.setSource(s.getName());
        log(Level.INFO, loginController.getAccount(), "ADD tanslation relation with " + slr.getTarget() + " to the sense " + s.getName());
    }

    public void onLexicalFunctionSelect(SenseData s, SenseData.LexicalFunction lf) {
        s.setSaveButtonDisabled(false);
        setSenseRelationEntry(lf);
        lf.setDeleteButtonDisabled(false);
        lf.setViewButtonDisabled(false);
        s.setSaveButtonDisabled(false);
        lf.setSource(s.getName());
        log(Level.INFO, loginController.getAccount(), "ADD lexical function " + lf.getLexFunName() + "with " + lf.getTarget() + " to the sense " + s.getName());
    }

    public void onTerminologicalRelationSelect(SenseData s, SenseData.ReifiedSenseRelation slr) {
        s.setSaveButtonDisabled(false);
        setSenseRelationEntry(slr);
        slr.setDeleteButtonDisabled(false);
        slr.setViewButtonDisabled(false);
        s.setSaveButtonDisabled(false);
        slr.setSource(s.getName());
        log(Level.INFO, loginController.getAccount(), "ADD terminological relation with " + slr.getTarget() + " to the sense " + s.getName());
    }

    public void removeSenseRelation(SenseData sd, SenseData.SenseRelation sr) {
        log(Level.INFO, loginController.getAccount(), "REMOVE direct sense relation " + (sr.getWrittenRep().isEmpty() ? " empty direct sense relation" : sr.getRelation())
                + " from " + lexiconControllerFormDetail.getLemma().getFormWrittenRepr());
        sd.getSenseRels().remove(sr);
        if (!sr.getWrittenRep().isEmpty()) {
            sd.setSaveButtonDisabled(false);
        }
    }

    public void removeReifSenseRelation(SenseData sd, SenseData.ReifiedSenseRelation rsr) {
        log(Level.INFO, loginController.getAccount(), "REMOVE sense relation " + (rsr.getTarget().isEmpty() ? " empty sense relation" : rsr.getCategory())
                + " from " + lexiconControllerFormDetail.getLemma().getFormWrittenRepr());
        sd.getReifiedSenseRels().remove(rsr);
        if (!rsr.getTarget().isEmpty()) {
            sd.setSaveButtonDisabled(false);
        }
    }

    public void removeTranslationSenseRelation(SenseData sd, SenseData.ReifiedTranslationRelation rsr) {
        log(Level.INFO, loginController.getAccount(), "REMOVE sense relation " + (rsr.getTarget().isEmpty() ? " empty sense relation" : rsr.getCategory())
                + " from " + lexiconControllerFormDetail.getLemma().getFormWrittenRepr());
        sd.getReifiedTranslationRels().remove(rsr);
        if (!rsr.getTarget().isEmpty()) {
            sd.setSaveButtonDisabled(false);
        }
    }

    public void removeLexicalFunction(SenseData sd, SenseData.LexicalFunction lf) {
        log(Level.INFO, loginController.getAccount(), "REMOVE lexical function " + (lf.getTarget().isEmpty() ? " empty lexical function" : lf.getLexFunName())
                + " from " + lexiconControllerFormDetail.getLemma().getFormWrittenRepr());
        sd.getLexicalFunctions().remove(lf);
        if (!lf.getTarget().isEmpty()) {
            sd.setSaveButtonDisabled(false);
        }
    }

    private void setSenseRelationEntry(SenseData.SenseRelation sr) {
        String splitted[] = sr.getWrittenRep().split("@");
        String sense = splitted[0];
        String lang = splitted[1];
        sr.setWrittenRep(sense);
        sr.setLanguage(lang);
    }

    private void setSenseRelationEntry(SenseData.ReifiedSenseRelation slr) {
        String splitted[] = slr.getTarget().split("@");
        String sense = splitted[0];
        String lang = splitted[1];
        slr.setTargetWrittenRep(sense);
        slr.setTarget(sense);
        slr.setTargetLanguage(lang);
    }

    private void setSenseRelationEntry(SenseData.ReifiedTranslationRelation slr) {
        String splitted[] = slr.getTarget().split("@");
        String sense = splitted[0];
        String lang = splitted[1];
        slr.setTargetWrittenRep(sense);
        slr.setTarget(sense);
        slr.setTargetLanguage(lang);
    }

    private void setSenseRelationEntry(SenseData.LexicalFunction lf) {
        String splitted[] = lf.getTarget().split("@");
        String sense = splitted[0];
        lf.setTargetWrittenRep(sense);
        lf.setTarget(sense);
    }

    public int getSize(String senseIndividual) {
        return senseIndividual.length() + 3;
    }
    
    public List<String> completeText(String sense) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        String currentSense = (String) component.getAttributes().get("currentSense");
        List<Map<String, String>> al = null;
        al = lexiconManager.sensesList("All languages");
        return getFilteredList(al, sense, currentSense, " ");
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

    public void addEntryOfLexicalFunction(SenseData.LexicalFunction lf) {
        info("todo.title", "todo.description");
    }

    public String getPropertyName(String s) {
        String ret = s.trim();
        if (ret.contains(" ")) {
            String[] _ret = ret.split(" ");
            ret = _ret[0];
            for (int i = 1; i < _ret.length; i++) {
                ret = ret + _ret[i].substring(0, 1).toUpperCase() + _ret[i].substring(1);
            }
        }
        return ret;
    }

    public void resetSenseDetails() {
        sensesVarTrans.clear();
        sensesVarTransCopy.clear();
    }

    public ArrayList<SelectItem> getSenseCategories() {
        return propertyValue.getSenseCategory();
    }

    public ArrayList<String> getSenseRelationTypes() {
        return propertyValue.getSenselRelType();
    }

    public ArrayList<String> getTranslationCategories() {
        return propertyValue.getTranslationCategory();
    }

    public boolean isMelchuckActive() {
//        return (PropertyValue.getSyntagmaticLexicalFunctions().size() > 0 || PropertyValue.getParadigmaticLexicalFunctions().size() > 0);
        return (MelchuckModelExtension.getParadigmaticLexicalFunctions().size() > 0 || MelchuckModelExtension.getSyntagmaticLexicalFunctions().size() > 0);
    }

    public ArrayList<SelectItem> getLexicalFunctions(LexicalFunction lf) {
        if (lf.getType().isEmpty()) {
            if (MelchuckModelExtension.getSyntagmaticRenderingTable().get(lf.getLexFunName()) != null) {
//            if (contains(MelchuckModelExtension.getSyntagmaticLexicalFunctions(), lf.getLexFunName())) {
                return MelchuckModelExtension.getSyntagmaticLexicalFunctions();
            } else {
                return MelchuckModelExtension.getParadigmaticLexicalFunctions();
            }
        } else {
            if (lf.getType().equals("paradigmatic")) {
                return MelchuckModelExtension.getParadigmaticLexicalFunctions();
            } else {
                return MelchuckModelExtension.getSyntagmaticLexicalFunctions();
            }
        }
    }

//    private boolean contains(ArrayList<SelectItem> lfList, String lfName) {
//        for (SelectItem si : lfList) {
//            if (si.getValue().equals(lfName)) {
//                return true;
//            }
//        }
//        return false;
//    }
}
