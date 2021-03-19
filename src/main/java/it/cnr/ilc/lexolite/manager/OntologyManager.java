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
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author andrea
 */
@ApplicationScoped
public class OntologyManager extends BaseController implements Serializable {

    @Inject
    private LoginController loginController;

    private OntologyModel ontologyModel;
    private String ontologyIRI;

    public String getOntologyIRI() {
        return ontologyIRI;
    }

    public void setOntologyIRI(String ontologyIRI) {
        this.ontologyIRI = ontologyIRI;
    }

    public void loadDomainOntology(FileUploadEvent f) {
        ontologyModel = new OntologyModel(f);
    }

    public void deafult_loadOntology() {
        if (ontologyModel == null) {
            ontologyModel = new OntologyModel();
        }
    }

    public OntologyModel getOntologyModel() {
        return ontologyModel;
    }

    public void persist(UploadedFile f) throws IOException {
        ontologyModel.persist(f);
    }

    public synchronized int getOntologyHierarchy(TreeNode ontoRoot) {
        if (ontologyModel != null) {
            return ontologyModel.getOntologyHierarchy(ontoRoot);
        }
        return 0;
    }

    public synchronized int getPropertyHierarchy(TreeNode ontoRoot) {
        if (ontologyModel != null) {
            return ontologyModel.getPropertyHierarchy(ontoRoot);
        }
        return 0;
    }

    public synchronized int getOntologyHierarchy(TreeNode ontoRoot, String nameToSelect) {
        if (ontologyModel != null) {
            return ontologyModel.getOntologyHierarchy(ontoRoot, nameToSelect);
        }
        return 0;
    }

    public ArrayList<String> getOntologyClasses() {
        if (ontologyModel != null) {
            return ontologyModel.getOntologyClasses();
        }
        return new ArrayList<>();
    }

    public String getOntologyID() {
        if (ontologyModel != null) {
            return ontologyModel.getDomainOntology().getOntologyID().getOntologyIRI().get().toURI().toString();
        }
        return "";
    }

    public int getOntologyClassesNumber() {
        if (ontologyModel != null) {
            return ontologyModel.getOntologyClassesNumber();
        }
        return 0;
    }

    public int getIndividualsNumber() {
        if (ontologyModel != null) {
            return ontologyModel.getIndividualsNumber();
        }
        return 0;
    }

    public int getOntologyDatatypePropertiesNumber() {
        if (ontologyModel != null) {
            ontologyModel.getOntoDetails();
            ontologyModel.getPropertyDetails();
            return ontologyModel.getOntologyDatatypePropertiesNumber();
        }
        return 0;
    }

    public int getOntologyObjectPropertiesNumber() {
        if (ontologyModel != null) {
            return ontologyModel.getOntologyObjectPropertiesNumber();
        }
        return 0;
    }
    
    public ArrayList<IndividualDetails> getIndividualsByClass(String clazz) {
        if (ontologyModel != null) {
            return ontologyModel.getIndividualsByClass(clazz);
        }
        return null;
    }

    public ArrayList<Metadata> getMetadataByClass(String clazz) {
        if (ontologyModel != null) {
            return ontologyModel.getMetadataByClass(clazz);
        }
        return null;
    }

    public List<String> classesList() {
        if (ontologyModel != null) {
            return ontologyModel.getClasses();
        }
        return null;
    }
    
    public List<String> subClassesListOf(String clazz) {
        if (ontologyModel != null) {
            return ontologyModel.getSubClassesOf(clazz);
        }
        return null;
    }

    public List<ReferenceMenuTheme> ontologyEntities() {
        if (ontologyModel != null) {
            if (ontologyModel.getOntoRefItems().size() > 0) {
                return ontologyModel.getOntoRefItems();
            } else {
                return ontologyModel.getOntologyEntities();
            }
        }
        return new ArrayList<>();
    }

}
