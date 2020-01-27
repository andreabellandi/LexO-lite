/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.manager.PropertyValue;
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

    // Possible values: Core, VarTrans, SynSem
    private String lexicalAspectActive = "Core";

    public String getLexicalAspectActive() {
        return lexicalAspectActive;
    }

    public void setLexicalAspectActive(String lexicalAspectActive) {
        this.lexicalAspectActive = lexicalAspectActive;
    }

    // managing of the menu buttons of the lexical aspects
    public void lexicalAspectChangeEvent(AjaxBehaviorEvent e) {
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
                lexiconControllerSynSemFormDetail.getLemmaSynSem().clear();
                lexiconControllerSynSemFormDetail.getLemmaSynSemCopy().clear();
                break;
            case "Variation and Translation":
                lexiconControllerFormDetail.setLemmaRendered(false);
                lexiconControllerSenseDetail.setSenseRendered(false);
                lexiconControllerVarTransFormDetail.setVarTransRendered(true);
                lexiconControllerVarTransSenseDetail.setSenseVarTransRendered(true);
                lexiconControllerSynSemFormDetail.setSynSemRendered(false);
                lexiconControllerSynSemSenseDetail.setSenseSynSemRendered(false);
                lexiconControllerOntologyDetail.setOntologyClassRendered(false);
                setVarTransEntryAndCopy();
                lexiconControllerVarTransFormDetail.addLexicalRelations();
                lexiconControllerVarTransSenseDetail.addSenseRelations();
                lexiconControllerSynSemFormDetail.getLemmaSynSem().clear();
                lexiconControllerSynSemFormDetail.getLemmaSynSemCopy().clear();
                break;
            case "Syntax and Semantics":
                lexiconControllerFormDetail.setLemmaRendered(false);
                lexiconControllerSenseDetail.setSenseRendered(false);
                lexiconControllerVarTransFormDetail.setVarTransRendered(false);
                lexiconControllerVarTransSenseDetail.setSenseVarTransRendered(false);
                lexiconControllerSynSemFormDetail.setSynSemRendered(true);
                lexiconControllerSynSemSenseDetail.setSenseSynSemRendered(true);
                lexiconControllerOntologyDetail.setOntologyClassRendered(false);
                setSynSemEntryAndCopy();
                lexiconControllerSynSemFormDetail.addSyntax();
                break;
            default:
                break;
        }

    }

    private void setVarTransEntryAndCopy() {
        lexiconControllerVarTransFormDetail.getLemmaVarTrans().setIndividual(lexiconControllerFormDetail.getLemma().getIndividual());
        lexiconControllerVarTransFormDetail.getLemmaVarTransCopy().setIndividual(lexiconControllerFormDetail.getLemma().getIndividual());
        lexiconControllerVarTransSenseDetail.getSensesVarTrans().clear();
        lexiconControllerVarTransSenseDetail.getSensesVarTransCopy().clear();
        for (SenseData sd : lexiconControllerSenseDetail.getSenses()) {
            lexiconControllerVarTransSenseDetail.getSensesVarTrans().add(getSenseCopy(sd.getName()));
            lexiconControllerVarTransSenseDetail.getSensesVarTransCopy().add(getSenseCopy(sd.getName()));
        }
    }

    private void setSynSemEntryAndCopy() {
        lexiconControllerSynSemFormDetail.getLemmaSynSem().setIndividual(lexiconControllerFormDetail.getLemma().getIndividual());
        lexiconControllerSynSemFormDetail.getLemmaSynSemCopy().setIndividual(lexiconControllerFormDetail.getLemma().getIndividual());
        lexiconControllerSynSemSenseDetail.getSensesSynSem().clear();
        for (SenseData sd : lexiconControllerSenseDetail.getSenses()) {
            lexiconControllerSynSemSenseDetail.getSensesSynSem().add(getSenseCopy(sd.getName()));
        }
    }

    private SenseData getSenseCopy(String name) {
        SenseData sd = new SenseData();
        sd.setName(name);
        return sd;
    }

    public ArrayList<String> getLexicalAspects() {
        return propertyValue.getLexicalAspects();
    }

}
