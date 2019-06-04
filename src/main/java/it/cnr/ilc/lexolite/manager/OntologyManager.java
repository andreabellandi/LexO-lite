/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.controller.BaseController;
import it.cnr.ilc.lexolite.controller.LoginController;
import it.cnr.ilc.lexolite.manager.OntologyData.IndividualDetails;
import it.cnr.ilc.lexolite.manager.OntologyData.Metadata;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.TreeNode;

/**
 *
 * @author andrea
 */
@ApplicationScoped
public class OntologyManager extends BaseController implements Serializable {

    @Inject
    private LoginController loginController;

    private OntologyModel ontologyModel;

    // never called ...
    public void loadDomainOntology(FileUploadEvent f) {
        ontologyModel = new OntologyModel(f);
    }

    public void deafult_loadOntology() {
        if (ontologyModel == null) {
            ontologyModel = new OntologyModel();
        }
    }

    public synchronized int getOntologyHierarchy(TreeNode ontoRoot) {
        return ontologyModel.getOntologyHierarchy(ontoRoot);
    }

    public synchronized int getOntologyHierarchy(TreeNode ontoRoot, String nameToSelect) {
        return ontologyModel.getOntologyHierarchy(ontoRoot, nameToSelect);
    }

    public ArrayList<String> getOntologyClasses() {
        return ontologyModel.getOntologyClasses();
    }

    public String getOntologyID() {
        return ontologyModel.getDomainOntology().getOntologyID().getOntologyIRI().get().toURI().toString();
    }

    public int getOntologyClassesNumber() {
        if (ontologyModel != null) {
            return ontologyModel.getOntologyClassesNumber();
        }
        return 0;
    }

    public int getIndividualsNumber() {
        return ontologyModel.getIndividualsNumber();
    }

    public int getOntologyDatatypePropertiesNumber() {
        return ontologyModel.getOntologyDatatypePropertiesNumber();
    }

    public int getOntologyObjectPropertiesNumber() {
        return ontologyModel.getOntologyObjectPropertiesNumber();
    }

    public ArrayList<IndividualDetails> getIndividualsByClass(String clazz) {
        return ontologyModel.getIndividualsByClass(clazz);
    }

    public ArrayList<Metadata> getMetadataByClass(String clazz) {
        return ontologyModel.getMetadataByClass(clazz);
    }

    public List<String> classesList() {
        return ontologyModel.getClasses();
    }

}
