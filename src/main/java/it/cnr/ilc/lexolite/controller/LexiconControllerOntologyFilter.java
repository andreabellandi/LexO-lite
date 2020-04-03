/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

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
public class LexiconControllerOntologyFilter extends BaseController implements Serializable {

    @Inject
    private OntologyManager ontologyManager;

    public int getOntologyClassesNumber() {
        if (ontologyManager != null) {
            return ontologyManager.getOntologyClassesNumber();
        }
        return 0;
    }

    public String getOntologyID() {
        if (ontologyManager != null) {
            return ontologyManager.getOntologyID();
        }
        return "";
    }

    public int getOntologyIndividualsNumber() {
        if (ontologyManager != null) {
            return ontologyManager.getIndividualsNumber();
        }
        return 0;
    }

    public int getOntologyDatatypePropertiesNumber() {
        if (ontologyManager != null) {
            return ontologyManager.getOntologyDatatypePropertiesNumber();
        }
        return 0;
    }

    public int getOntologyObjectPropertiesNumber() {
        if (ontologyManager != null) {
            return ontologyManager.getOntologyObjectPropertiesNumber();
        }
        return 0;
    }

}
