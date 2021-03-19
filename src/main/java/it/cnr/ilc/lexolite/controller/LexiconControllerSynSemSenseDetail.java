/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.manager.LemmaData.SynArg;
import it.cnr.ilc.lexolite.manager.LemmaData.SynFrame;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import it.cnr.ilc.lexolite.manager.ReferenceMenuTheme;
import it.cnr.ilc.lexolite.manager.SenseData;
import it.cnr.ilc.lexolite.manager.SenseData.OntoMap;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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
public class LexiconControllerSynSemSenseDetail extends BaseController implements Serializable {

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
    private LexiconControllerSynSemFormDetail lexiconControllerSynSemFormDetail;

    private boolean senseSynSemRendered = false;
    private boolean ontoMapArgDisabled = false;

    private ArrayList<SenseData> sensesSynSem = new ArrayList<>();
    private ArrayList<SenseData> sensesSynSemCopy = new ArrayList<>();

    private String selectedFrame = null;

    public boolean isOntoMapArgDisabled() {
        return ontoMapArgDisabled;
    }

    public void setOntoMapArgDisabled(boolean ontoMapArgDisabled) {
        this.ontoMapArgDisabled = ontoMapArgDisabled;
    }

    public boolean isSenseSynSemRendered() {
        return senseSynSemRendered;
    }

    public void setSenseSynSemRendered(boolean senseSynSemRendered) {
        this.senseSynSemRendered = senseSynSemRendered;
    }

    public ArrayList<SenseData> getSensesSynSem() {
        return sensesSynSem;
    }

    public void setSensesSynSem(ArrayList<SenseData> sensesSynSem) {
        this.sensesSynSem = sensesSynSem;
    }

    public ArrayList<SenseData> getSensesSynSemCopy() {
        return sensesSynSemCopy;
    }

    public void setSensesSynSemCopy(ArrayList<SenseData> sensesSynSemCopy) {
        this.sensesSynSemCopy = sensesSynSemCopy;
    }

    public void addSemantics() {
        this.sensesSynSem = lexiconManager.getSensesSynSemAttributesOfSense((ArrayList<SenseData>) lexiconControllerSenseDetail.getSenses());
        createSensesCopy();
    }

    // for keeping track of modifies
    private void createSensesCopy() {
        sensesSynSemCopy.clear();
        for (SenseData sd : sensesSynSem) {
            SenseData _sd = new SenseData();
            _sd.setName(sd.getName());
            //_sd.setOWLClass(sd.getOWLClass());
            ReferenceMenuTheme rmt = new ReferenceMenuTheme();
            rmt.setId(sd.getThemeOWLClass().getId());
            rmt.setName(sd.getThemeOWLClass().getName());
         rmt.setType((sd.getThemeOWLClass().getName().isEmpty()) ? null : ReferenceMenuTheme.itemType.valueOf(sd.getThemeOWLClass().getType()));
            _sd.setThemeOWLClass(rmt);
            _sd.setDefinition(sd.getDefinition());
            if (sd.getOntoMap() != null) {
                _sd.setOntoMap(copySynSemOntoMaps(sd.getOntoMap()));
            } else {
                _sd.setOntoMap(null);
            }
            sensesSynSemCopy.add(_sd);
        }
    }

    private OntoMap copySynSemOntoMaps(OntoMap om) {
        OntoMap _om = new OntoMap();
        _om.setFrame(om.getFrame());
        _om.setIsA(om.getIsA());
        _om.setObjOfProp(om.getObjOfProp());
        _om.setReference(om.getReference());
        _om.setSubjOfProp(om.getSubjOfProp());
        return _om;
    }

    public ArrayList<String> getFrames() {
        ArrayList<String> frames = new ArrayList<>();
        for (SynFrame frame : lexiconControllerSynSemFormDetail.getLemmaSynSem().getSynFrames()) {
            if (frame.getName().length() > 0) {
                frames.add(frame.getType() + " - " + frame.getName());
            }
        }
        if ((selectedFrame == null) && frames.size() > 0) {
            selectedFrame = frames.get(0).split(" - ")[1];
        }
        return frames.isEmpty() ? null : frames;
    }

    private void setSenseSaveButton(SenseData sd) {
        boolean enabled = !sd.getOntoMap().getFrame().isEmpty()
                || (sd.getOntoMap().getIsA().isEmpty() && !sd.getOntoMap().getObjOfProp().isEmpty() && !sd.getOntoMap().getSubjOfProp().isEmpty())
                || (!sd.getOntoMap().getIsA().isEmpty() && sd.getOntoMap().getObjOfProp().isEmpty() && sd.getOntoMap().getSubjOfProp().isEmpty());
        sd.setSaveButtonDisabled(!enabled);
    }

    public void setSelectedFrame(SenseData sd) {
        selectedFrame = sd.getOntoMap().getFrame().split(" - ")[1];
        sd.getOntoMap().setFrame(selectedFrame);
        setSenseSaveButton(sd);
    }

    public ArrayList<String> getSynArgs(SenseData s) {
        ArrayList<String> synArgs = new ArrayList<>();
        for (SynFrame frame : lexiconControllerSynSemFormDetail.getLemmaSynSem().getSynFrames()) {
            if (selectedFrame.equals(frame.getName())) {
                for (SynArg sa : frame.getSynArgs()) {
                    synArgs.add(sa.getName());
                }
            }
        }
        return synArgs;
    }

    public void isAChanged(AjaxBehaviorEvent e) {
        String arg = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE isA value of " + lexiconControllerSynSemFormDetail.getLemmaSynSem().getFormWrittenRepr() + " to " + arg);
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        SenseData sd = (SenseData) component.getAttributes().get("sense");
        setSenseSaveButton(sd);
    }

    public void subjPropChanged(AjaxBehaviorEvent e) {
        String arg = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE subjPropOf value of " + lexiconControllerSynSemFormDetail.getLemmaSynSem().getFormWrittenRepr() + " to " + arg);
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        SenseData sd = (SenseData) component.getAttributes().get("sense");
        setSenseSaveButton(sd);
    }

    public void objPropChanged(AjaxBehaviorEvent e) {
        String arg = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE objPropOf value of " + lexiconControllerSynSemFormDetail.getLemmaSynSem().getFormWrittenRepr() + " to " + arg);
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        SenseData sd = (SenseData) component.getAttributes().get("sense");
        setSenseSaveButton(sd);
    }

    public void addOntoMapping(SenseData sd) {
        log(Level.INFO, loginController.getAccount(), "ADD empty onto mapping to " + sd.getName());
        OntoMap om = new OntoMap();
        sd.setOntoMap(om);
    }

    public boolean ontoMappingDisabled(SenseData sd) {
        return (lexiconControllerSynSemFormDetail.getLemmaSynSem().getSynFrames().isEmpty())
                || (sd.getThemeOWLClass().getName().isEmpty()) || (sd.getOntoMap() != null);
    }

    public boolean ontoSubMappingDisabled(SenseData sd) {
        return (lexiconControllerSynSemFormDetail.getLemmaSynSem().getSynFrames().isEmpty())
                || (sd.getThemeOWLClass().getName().isEmpty()) || (!sd.getSubOntoMap().isEmpty())
                || (sd.getOntoMap() != null);
    }

    public void saveOntologyMapping(SenseData sd) throws IOException, OWLOntologyStorageException {
        sd.setSaveButtonDisabled(true);
        log(Level.INFO, loginController.getAccount(), "SAVE updated Sense synsem part");
        int order = sensesSynSem.indexOf(sd);
        lexiconManager.updateSenseSynSem(sensesSynSemCopy.get(order), sd);
        updateSenseCopy(sd, order);
        info("template.message.saveSenseRelation.summary", "template.message.saveSenseRelation.description", sd.getName());
    }

    private void updateSenseCopy(SenseData sd, int order) {
        SenseData updatedSense = copySenseData(sd);
        sensesSynSemCopy.remove(order);
        sensesSynSemCopy.add(order, updatedSense);
    }

    private SenseData copySenseData(SenseData sd) {
        SenseData _sd = new SenseData();
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
        _sd.setDefinition(sd.getDefinition());
        _sd.setName(sd.getName());
        _sd.setNote(sd.getNote());
        OntoMap om = new OntoMap();
        om.setFrame(sd.getOntoMap().getFrame());
        om.setIsA(sd.getOntoMap().getIsA());
        om.setObjOfProp(sd.getOntoMap().getObjOfProp());
        om.setSubjOfProp(sd.getOntoMap().getSubjOfProp());
        om.setReference(sd.getOWLClass().getName());
        _sd.setOntoMap(om);
        return _sd;
    }

    public void resetSenseDetails() {
        sensesSynSem.clear();
        sensesSynSemCopy.clear();
    }

}
