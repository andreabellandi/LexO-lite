/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.domain.AccountType;
import it.cnr.ilc.lexolite.manager.AccountManager;
import it.cnr.ilc.lexolite.manager.OntologyData;
import it.cnr.ilc.lexolite.manager.OntologyManager;
import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerOntologyDetail extends BaseController implements Serializable {

    @Inject
    private OntologyManager ontologyManager;
    @Inject
    private AccountManager accountManager;
    @Inject
    private LoginController loginController;

    private boolean ontologyClassRendered = false;
    private OntologyData ontologyData;
//    private ArrayList<String> individualTabList;

//    public ArrayList<String> getIndividualTabList() {
//        return individualTabList;
//    }

    public boolean isOntologyClassRendered() {
        return ontologyClassRendered;
    }

    public void setOntologyClassRendered(boolean ontologyClassRendered) {
        this.ontologyClassRendered = ontologyClassRendered;
    }

    public OntologyData getOntologyClassDetail() {
        return ontologyData;
    }

    public void addOntologyClassDetails(String clazz) {
//        domainOntologyManager.getIndividualsByClass(clazz);
        ontologyData = new OntologyData();
        ontologyData.setMetadata(ontologyManager.getMetadataByClass(clazz));
//        individualTabList = domainOntologyManager.getIndividualsByClass(clazz);
        ontologyData.setIndividuals(ontologyManager.getIndividualsByClass(clazz));
    }

    public boolean isOntologyTabEnabled() {
        return accountManager.hasPermission(AccountType.Permission.WRITE_ALL, AccountManager.Access.LEXICON_EDITOR, loginController.getAccount());
    }
    
}

