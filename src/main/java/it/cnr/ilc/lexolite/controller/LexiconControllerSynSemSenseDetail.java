/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import it.cnr.ilc.lexolite.manager.SenseData;
import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
    private LexiconControllerLinkedLexicalEntryDetail lexiconControllerLinkedLexicalEntryDetail;
    @Inject
    private LexiconControllerFormDetail lexiconControllerFormDetail;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LoginController loginController;
    @Inject
    private PropertyValue propertyValue;

    private boolean senseSynSemRendered = false;

    private ArrayList<SenseData> sensesSynSem = new ArrayList<>();
    private ArrayList<SenseData> sensesSynSemCopy = new ArrayList<>();

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

}
