/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.manager.PropertyValue;
import it.cnr.ilc.lexolite.manager.ReferenceMenuTheme;
import it.cnr.ilc.lexolite.manager.ReferenceMenuTheme.itemType;
import it.cnr.ilc.lexolite.manager.SenseData;
import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerLexicalAspect extends BaseController implements Serializable {

    @Inject
    private LoginController loginController;
    @Inject
    private LexiconControllerFormDetail lexiconControllerFormDetail;
    @Inject
    private LexiconControllerSenseDetail lexiconControllerSenseDetail;
    @Inject
    private LexiconControllerAttestation lexiconControllerAttestation;
    @Inject
    private LexiconControllerVarTransFormDetail lexiconControllerVarTransFormDetail;
    @Inject
    private LexiconControllerVarTransSenseDetail lexiconControllerVarTransSenseDetail;
    @Inject
    private LexiconControllerSynSemSenseDetail lexiconControllerSynSemSenseDetail;
    @Inject
    private LexiconControllerSynSemFormDetail lexiconControllerSynSemFormDetail;
    @Inject
    private LexiconControllerOntologyDetail lexiconControllerOntologyDetail;
    @Inject
    private PropertyValue propertyValue;

    // Possible values: Core, VarTrans, SynSem, Attestation
    private String lexicalAspectActive = "Core";
    private boolean rendered = true;

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public String getLexicalAspectActive() {
        return lexicalAspectActive;
    }

    public void setLexicalAspectActive(String lexicalAspectActive) {
        this.lexicalAspectActive = lexicalAspectActive;
    }

    // managing of the menu buttons of the lexical aspects
    public void lexicalAspectChangeEvent(AjaxBehaviorEvent e) {
        // just a simple way to verify if an entry is selected
        if (lexiconControllerFormDetail.getLemma().getFormWrittenRepr() != null) {
            String aspect = (String) e.getComponent().getAttributes().get("value");
            switch (aspect) {
                case "Core":
                    lexiconControllerFormDetail.setLemmaRendered(true);
                    lexiconControllerSenseDetail.setSenseRendered(true);
                    lexiconControllerVarTransFormDetail.setVarTransRendered(false);
                    lexiconControllerVarTransSenseDetail.setSenseVarTransRendered(false);
                    lexiconControllerSynSemFormDetail.setSynSemRendered(false);
                    lexiconControllerSynSemSenseDetail.setSenseSynSemRendered(false);
                    lexiconControllerOntologyDetail.setOntologyClassRendered(false);
                    lexiconControllerAttestation.setAttestationViewRendered(false);
                    break;
                case "Variation and Translation":
                    lexiconControllerFormDetail.setLemmaRendered(false);
                    lexiconControllerSenseDetail.setSenseRendered(false);
                    lexiconControllerVarTransFormDetail.setVarTransRendered(true);
                    lexiconControllerVarTransSenseDetail.setSenseVarTransRendered(true);
                    lexiconControllerSynSemFormDetail.setSynSemRendered(false);
                    lexiconControllerSynSemSenseDetail.setSenseSynSemRendered(false);
                    lexiconControllerOntologyDetail.setOntologyClassRendered(false);
                    lexiconControllerAttestation.setAttestationViewRendered(false);
                    setVarTransEntryAndCopy();
                    lexiconControllerVarTransFormDetail.addLexicalRelations();
                    lexiconControllerVarTransSenseDetail.addSenseRelations();
                    break;
                case "Syntax and Semantics":
                    lexiconControllerFormDetail.setLemmaRendered(false);
                    lexiconControllerSenseDetail.setSenseRendered(false);
                    lexiconControllerVarTransFormDetail.setVarTransRendered(false);
                    lexiconControllerVarTransSenseDetail.setSenseVarTransRendered(false);
                    lexiconControllerSynSemFormDetail.setSynSemRendered(true);
                    lexiconControllerSynSemSenseDetail.setSenseSynSemRendered(true);
                    lexiconControllerOntologyDetail.setOntologyClassRendered(false);
                    lexiconControllerAttestation.setAttestationViewRendered(false);
                    setSynSemEntryAndCopy();
                    lexiconControllerSynSemFormDetail.addSyntax();
                    lexiconControllerSynSemSenseDetail.addSemantics();
                    break;
                case "Attestation":
                    lexiconControllerFormDetail.setLemmaRendered(false);
                    lexiconControllerSenseDetail.setSenseRendered(false);
                    lexiconControllerVarTransFormDetail.setVarTransRendered(false);
                    lexiconControllerVarTransSenseDetail.setSenseVarTransRendered(false);
                    lexiconControllerOntologyDetail.setOntologyClassRendered(false);
                    lexiconControllerAttestation.setAttestationViewRendered(true);
                    lexiconControllerSynSemFormDetail.setSynSemRendered(false);
                    break;
                default:
                    break;
            }
        }

    }

    private void setVarTransEntryAndCopy() {
        lexiconControllerVarTransFormDetail.getLemmaVarTrans().setIndividual(lexiconControllerFormDetail.getLemma().getIndividual());
        lexiconControllerVarTransFormDetail.getLemmaVarTransCopy().setIndividual(lexiconControllerFormDetail.getLemma().getIndividual());
        lexiconControllerVarTransSenseDetail.getSensesVarTrans().clear();
        lexiconControllerVarTransSenseDetail.getSensesVarTransCopy().clear();
        for (SenseData sd : lexiconControllerSenseDetail.getSenses()) {
            lexiconControllerVarTransSenseDetail.getSensesVarTrans().add(getSenseCopy(sd));
            lexiconControllerVarTransSenseDetail.getSensesVarTransCopy().add(getSenseCopy(sd));
        }
    }

    private void setSynSemEntryAndCopy() {
        lexiconControllerSynSemFormDetail.getLemmaSynSem().setIndividual(lexiconControllerFormDetail.getLemma().getIndividual());
        lexiconControllerSynSemFormDetail.getLemmaSynSemCopy().setIndividual(lexiconControllerFormDetail.getLemma().getIndividual());
        lexiconControllerSynSemSenseDetail.getSensesSynSem().clear();
        lexiconControllerSynSemSenseDetail.getSensesSynSemCopy().clear();
        for (SenseData sd : lexiconControllerSenseDetail.getSenses()) {
            lexiconControllerSynSemSenseDetail.getSensesSynSem().add(getSenseCopy(sd));
            lexiconControllerSynSemSenseDetail.getSensesSynSemCopy().add(getSenseCopy(sd));
        }
    }

    private SenseData getSenseCopy(SenseData sd) {
        SenseData _sd = new SenseData();
        _sd.setName(sd.getName());

//        SenseData.Openable ref = new SenseData.Openable();
//        ref.setName(sd.getOWLClass().getName());
//        _sd.setOWLClass(ref);
        ReferenceMenuTheme rmt = new ReferenceMenuTheme();
        rmt.setId(sd.getThemeOWLClass().getId());
        rmt.setName(sd.getThemeOWLClass().getName());
        rmt.setType((sd.getThemeOWLClass().getName().isEmpty()) ? null : ReferenceMenuTheme.itemType.valueOf(sd.getThemeOWLClass().getType()));
        _sd.setThemeOWLClass(rmt);

        return _sd;
    }

    public ArrayList<String> getLexicalAspects() {
        return propertyValue.getLexicalAspects();
    }

}
