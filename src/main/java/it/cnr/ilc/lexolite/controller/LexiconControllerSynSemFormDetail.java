/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.manager.LemmaData;
import it.cnr.ilc.lexolite.manager.LemmaData.SynArg;
import it.cnr.ilc.lexolite.manager.LemmaData.SynFrame;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import it.cnr.ilc.lexolite.manager.SenseData;
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
public class LexiconControllerSynSemFormDetail extends BaseController implements Serializable {

    @Inject
    private LexiconControllerFormDetail lexiconControllerFormDetail;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LoginController loginController;
    @Inject
    private PropertyValue propertyValue;

    private boolean synSemRendered = false;

    private LemmaData lemmaSynSem = new LemmaData();
    private LemmaData lemmaSynSemCopy = new LemmaData();

    public LemmaData getLemmaSynSem() {
        return lemmaSynSem;
    }

    public void setLemmaSynSem(LemmaData lemmaSynSem) {
        this.lemmaSynSem = lemmaSynSem;
    }

    public LemmaData getLemmaSynSemCopy() {
        return lemmaSynSemCopy;
    }

    public void setLemmaSynSemCopy(LemmaData lemmaSynSemCopy) {
        this.lemmaSynSemCopy = lemmaSynSemCopy;
    }

    public boolean isSynSemRendered() {
        return synSemRendered;
    }

    public void setSynSemRendered(boolean synSemRendered) {
        this.synSemRendered = synSemRendered;
    }

    // invoked by the controller after an user selected the varTrans tab
    public void addSyntax() {
        this.lemmaSynSem = lexiconManager.getSynSemAttributes(lemmaSynSem.getIndividual());
        createLemmaCopy();
    }

    // for keeping track of modifies
    private void createLemmaCopy() {
        this.lemmaSynSemCopy.setSynFrames(copySyntacticData(lemmaSynSem.getSynFrames()));
    }

    private ArrayList<SynFrame> copySyntacticData(ArrayList<SynFrame> alw) {
        ArrayList<SynFrame> _alw = new ArrayList();
        for (SynFrame w : alw) {
            SynFrame _w = new SynFrame();
            _w.setName(w.getName());
            _w.setNewFrame(w.isNewFrame());
            _w.setSaveButtonDisabled(w.isSaveButtonDisabled());
            _w.setType(_w.getType());
            _w.setSynArgs(copyOfArgs(w.getSynArgs()));
            _alw.add(_w);
        }
        return _alw;
    }

    private ArrayList<SynArg> copyOfArgs(ArrayList<SynArg> alw) {
        ArrayList<SynArg> _alw = new ArrayList();
        for (SynArg w : alw) {
            SynArg _w = new SynArg();
            _w.setMarker(w.getMarker());
            _w.setNumber(w.getNumber());
            _w.setOptional(w.isOptional());
            _w.setType(w.getType());
            _alw.add(_w);
        }
        return _alw;
    }

    public void addSyntacticFrame() {
        log(Level.INFO, loginController.getAccount(), "ADD empty Syntactic frame box");
        SynFrame sf = new LemmaData.SynFrame();
        lemmaSynSem.getSynFrames().add(0, sf);
        updateLemmaSynSemCopy();
    }

    public void addSyntacticArgument(SynFrame sf) {
        SynArg sa = new SynArg();
        sf.setSaveButtonDisabled(false);
        sa.setNumber(sf.getSynArgs().size() + 1);
        sf.getSynArgs().add(sa);
    }

    public void saveSynFrame(SynFrame sf) throws IOException, OWLOntologyStorageException {
        int order = lemmaSynSem.getSynFrames().indexOf(sf);
        sf.setSaveButtonDisabled(true);
        log(Level.INFO, loginController.getAccount(), "SAVE updated Syntactic frame of " + lexiconControllerFormDetail.getLemma().getFormWrittenRepr());
        if (sf.isNewFrame() && sf.getSynArgs().isEmpty()) {
            sf.setName(lexiconManager.createSyntacticFrame(lexiconControllerFormDetail.getLemma().getIndividual(), sf));
        } else {
            lexiconManager.updateSyntacticFrame(lemmaSynSemCopy.getSynFrames().get(order), sf);
        }
        sf.setNewFrame(false);
        updateLemmaSynSemCopy();
    }

    private void updateLemmaSynSemCopy() {
        lemmaSynSemCopy.setSynFrames(copySynFrames());
    }

    private ArrayList<SynFrame> copySynFrames() {
        ArrayList<SynFrame> sfl = new ArrayList<SynFrame>();
        for (SynFrame sf : lemmaSynSem.getSynFrames()) {
            SynFrame _sf = new SynFrame();
            _sf.setName(sf.getName());
            _sf.setNewFrame(_sf.isNewFrame());
            _sf.setSynArgs(copySynArgs(sf.getSynArgs()));
            _sf.setType(sf.getType());
            sfl.add(_sf);
        }
        return sfl;
    }

    private ArrayList<SynArg> copySynArgs(ArrayList<SynArg> alsa) {
        ArrayList<SynArg> _alsa = new ArrayList<SynArg>();
        for (SynArg sa : alsa) {
            SynArg _sa = new SynArg();
            _sa.setMarker(sa.getMarker());
            _sa.setNumber(sa.getNumber());
            _sa.setOptional(sa.isOptional());
            _sa.setType(sa.getType());
            _alsa.add(_sa);
        }
        return _alsa;
    }

    public void synFrameTypeChanged(AjaxBehaviorEvent e) {
        String type = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE SynFrame type of " + lexiconControllerFormDetail.getLemma().getFormWrittenRepr() + " to " + type);
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        SynFrame sf = (SynFrame) component.getAttributes().get("frame");
        sf.setSaveButtonDisabled(false);
    }

    public void synArgTypeChanged(AjaxBehaviorEvent e) {
        String type = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE SynArg type of " + lexiconControllerFormDetail.getLemma().getFormWrittenRepr() + " to " + type);
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        SynFrame sf = (SynFrame) component.getAttributes().get("frame");
        sf.setSaveButtonDisabled(false);
    }

    public void synArgMarkerChanged(AjaxBehaviorEvent e) {
        String type = (String) e.getComponent().getAttributes().get("value");
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        SynFrame sf = (SynFrame) component.getAttributes().get("frame");
        sf.setSaveButtonDisabled(false);
        log(Level.INFO, loginController.getAccount(), "UPDATE marker in hte frame " + sf.getName() + " to " + type);
    }

    public void synArgOptChanged(AjaxBehaviorEvent e) {
        boolean type = (boolean) e.getComponent().getAttributes().get("value");
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        SynFrame sf = (SynFrame) component.getAttributes().get("frame");
        sf.setSaveButtonDisabled(false);
        log(Level.INFO, loginController.getAccount(), "UPDATE optionality argument in the frame " + sf.getName() + " set to " + type);
    }

    public ArrayList<String> getSyntacticFrames() {
        return propertyValue.getSynFrameType();
    }

    public ArrayList<String> getSyntacticArgumentTypes() {
        return propertyValue.getSynArgType();
    }

}
