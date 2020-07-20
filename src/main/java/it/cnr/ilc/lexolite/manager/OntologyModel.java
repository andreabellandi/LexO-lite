/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import com.google.common.collect.Multimap;
import it.cnr.ilc.lexolite.LexOliteProperty;
import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.controller.BaseController;
import it.cnr.ilc.lexolite.controller.LexiconControllerTabViewList;
import it.cnr.ilc.lexolite.controller.LexiconControllerTabViewList.DataTreeNode;
import it.cnr.ilc.lexolite.controller.LoginController;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.manager.OntologyData.IndividualAxiom;
import it.cnr.ilc.lexolite.manager.OntologyData.IndividualDetails;
import it.cnr.ilc.lexolite.manager.OntologyData.Metadata;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.InferenceDepth;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 *
 * @author andrea
 */
public class OntologyModel extends BaseController {

    @Inject
    private LoginController loginController;

    private OWLOntologyManager manager;
    private OWLOntology domainOntology;
    private OWLDataFactory factory;
    StructuralReasonerFactory reasonerFactory = null;
    OWLReasoner reasoner = null;

    private int individualsNumber = 0;

    private ArrayList<ReferenceMenuTheme> ontoRefItems = new ArrayList();

    public ArrayList<ReferenceMenuTheme> getOntoRefItems() {
        return ontoRefItems;
    }

    public void setOntoRefItems(ArrayList<ReferenceMenuTheme> ontoRefItems) {
        this.ontoRefItems = ontoRefItems;
    }

    private PrefixManager pm;

    public OntologyModel(FileUploadEvent f) {
        manager = OWLManager.createOWLOntologyManager();
        try (InputStream inputStream = f.getFile().getInputstream()) {
            domainOntology = manager.loadOntologyFromOntologyDocument(inputStream);
            factory = manager.getOWLDataFactory();
            reasonerFactory = new StructuralReasonerFactory();
            reasoner = reasonerFactory.createReasoner(domainOntology);
            setPrefixes();
            persist(f.getFile());
        } catch (OWLOntologyCreationException | IOException ex) {
            log(Level.ERROR, ((loginController == null) ? new Account() : loginController.getAccount()), "LOADING domain ontology ", ex);
        }
    }

    public OntologyModel() {
        manager = OWLManager.createOWLOntologyManager();
        try (InputStream inputStream = new FileInputStream(System.getProperty("user.home") + Label.LEXO_FOLDER
                + LexOliteProperty.getProperty(Label.ONTOLOGY_FILE_NAME_KEY))) {
            domainOntology = manager.loadOntologyFromOntologyDocument(inputStream);
            factory = manager.getOWLDataFactory();
            reasonerFactory = new StructuralReasonerFactory();
            reasoner = reasonerFactory.createReasoner(domainOntology);
            setPrefixes();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setPrefixes() {
        String namespace = domainOntology.getOntologyID().getOntologyIRI().get().toURI().toString();
        pm = new DefaultPrefixManager();
        pm.setPrefix("domainOntology", namespace);
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public OWLOntology getDomainOntology() {
        return domainOntology;
    }

    public OWLDataFactory getFactory() {
        return factory;
    }

    public int getIndividualsNumber() {
        return individualsNumber;
    }

    private int getIndividualsNumberByClass(OWLClass cls) {
        int n = 0;
        NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(cls, true);
        for (OWLNamedIndividual i : instances.getFlattened()) {
            n++;
        }
        return n;
    }

    public ArrayList<IndividualDetails> getIndividualsByClass(String clazz) {
        OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
        ArrayList<IndividualDetails> indDetails = new ArrayList<IndividualDetails>();
        OWLClass cls = factory.getOWLClass(IRI.create(pm.getPrefixName2PrefixMap().get("domainOntology:"), "#" + clazz));
        NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(cls, true);
        for (OWLNamedIndividual i : instances.getFlattened()) {
            IndividualDetails id = new IndividualDetails();
            id.setIri(i.getIRI().toString());
            id.setShortForm(i.getIRI().getShortForm());
            ArrayList<Metadata> metadata = getMetadataByIndividual(id, i);
            if (metadata != null) {
                id.setMetadata(metadata);
            }
            Multimap<OWLObjectPropertyExpression, OWLIndividual> assertedValues = EntitySearcher.getObjectPropertyValues(i, domainOntology);
            ArrayList<IndividualAxiom> axList = new ArrayList<IndividualAxiom>();
            for (OWLObjectProperty objProp : domainOntology.getObjectPropertiesInSignature(Imports.INCLUDED)) {
                for (OWLNamedIndividual ind : reasoner.getObjectPropertyValues(i, objProp).getFlattened()) {
                    boolean asserted = assertedValues.get(objProp).contains(ind);
                    axList.add(new IndividualAxiom(renderer.render(objProp), renderer.render(ind), IndividualAxiom.AxiomType.OBJECT_TYPE));
                }
            }
            for (OWLDataPropertyAssertionAxiom ax : domainOntology.getDataPropertyAssertionAxioms(i)) {
                axList.add(new IndividualAxiom(ax.getProperty().asOWLDataProperty().getIRI().getShortForm(), ax.getObject().getLiteral(), IndividualAxiom.AxiomType.DATA_TYPE));
            }
            if (axList != null) {
                id.setAxioms(axList);
            }
            indDetails.add(id);
        }
        return indDetails;
    }

    public ArrayList<Metadata> getMetadataByIndividual(IndividualDetails ind, OWLNamedIndividual i) {
        ArrayList<Metadata> mdList = new ArrayList<Metadata>();
        setMetadataByIndividual(mdList, i, OWLRDFVocabulary.RDFS_COMMENT.getIRI());
        setMetadataByIndividual(mdList, i, OWLRDFVocabulary.RDFS_LABEL.getIRI());
        return mdList;
    }

    private void setMetadataByIndividual(ArrayList<Metadata> mdList, OWLNamedIndividual i, IRI metadata) {
        Metadata md = getMetadataByIndividual(i, factory.getOWLAnnotationProperty(metadata));
        if (md != null) {
            mdList.add(md);
        }
    }

    public ArrayList<Metadata> getMetadataByClass(String clazz) {
        ArrayList<Metadata> metadataList = new ArrayList();
        OWLClass cls = factory.getOWLClass(IRI.create(pm.getPrefixName2PrefixMap().get("domainOntology:"), "#" + clazz));
        metadataList.add(new Metadata("Full class name", cls.getIRI().getIRIString()));
        metadataList.add(new Metadata("Short name", clazz));
        metadataList.add(getMetadataByClass(cls, factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI())));
        metadataList.add(getMetadataByClass(cls, factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI())));
        metadataList.add(new Metadata("Number of instances", Integer.toString(getIndividualsNumberByClass(cls))));
        return metadataList;
    }

    // **********************
    // it supposes that only ONE property of type annotation exists !!! (there could be more ... @it, @en, ...)
    // **********************
    private Metadata getMetadataByClass(OWLClass cls, OWLAnnotationProperty annotation) {
        for (Object obj : EntitySearcher.getAnnotations(cls, domainOntology, annotation).toArray()) {
            OWLAnnotationValue value = ((OWLAnnotation) obj).getValue();
            if (value instanceof OWLLiteral) {
                //System.out.println(cls + " labelled " + ((OWLLiteral) value).getLiteral());
                log(Level.INFO, (loginController == null) ? new Account() : loginController.getAccount(), cls + " labelled " + ((OWLLiteral) value).getLiteral());
                return new Metadata(annotation.getIRI().getShortForm(), ((OWLLiteral) value).getLiteral());
            }
        }
        return null;
    }

    private Metadata getMetadataByIndividual(OWLNamedIndividual i, OWLAnnotationProperty annotation) {
        for (Object obj : EntitySearcher.getAnnotations(i, domainOntology, annotation).toArray()) {
            OWLAnnotationValue value = ((OWLAnnotation) obj).getValue();
            if (value instanceof OWLLiteral) {
                // System.out.println(i + " labelled " + ((OWLLiteral) value).getLiteral());
                log(Level.INFO, (loginController == null) ? new Account() : loginController.getAccount(), i + " labelled " + ((OWLLiteral) value).getLiteral());

                return new Metadata(annotation.getIRI().getShortForm(), ((OWLLiteral) value).getLiteral());
            }
        }
        return null;
    }

    public int getOntologyHierarchy(TreeNode ontoRoot) {
        int classCounter = 0;
        individualsNumber = 0;
        OWLClass clazz = factory.getOWLThing();
        System.out.println("Class       : " + clazz);
        Set<OWLClass> set = domainOntology.classesInSignature().collect(Collectors.toSet());
        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(domainOntology);
        printHierarchy(reasoner, clazz, 0, ontoRoot);
        for (OWLClass cl : set) {
            classCounter++;
            individualsNumber = individualsNumber + getIndividualsNumberByClass(cl);
            if (!reasoner.isSatisfiable(cl)) {
                log(Level.INFO, (loginController == null) ? new Account() : loginController.getAccount(), "XXX: " + cl.getIRI().getShortForm());
                // System.out.println("XXX: " + cl.getIRI().getShortForm());
            }
        }
        reasoner.dispose();
        System.out.println("total individuals: " + individualsNumber);
        return classCounter;
    }

    public int getOntologyClassesNumber() {
        if (domainOntology != null) {
            return domainOntology.classesInSignature().collect(Collectors.toSet()).size();
        }
        return 0;
    }

    private void printHierarchy(OWLReasoner reasoner, OWLClass clazz, int level, TreeNode tn) {
        if (reasoner.isSatisfiable(clazz)) {
            for (int i = 0; i < level * 3; i++) {
                System.out.print(" ");
            }
            System.out.println(clazz.getIRI().getShortForm());
            TreeNode n = null;
            if (!clazz.getIRI().getShortForm().equals("Thing")) {
                LexiconControllerTabViewList.DataTreeNode dtn = new LexiconControllerTabViewList.DataTreeNode(clazz.getIRI().getShortForm(), clazz.getIRI().toString(), "", "", "false", "false", "", 0);
                n = new DefaultTreeNode(dtn, tn);
                n.setExpanded(true);
            }
            NodeSet<OWLClass> c = reasoner.getSubClasses(clazz, true);
            for (OWLClass child : c.entities().collect(Collectors.toList())) {
                if (!child.equals(clazz)) {
                    printHierarchy(reasoner, child, level + 1, n == null ? tn : n);
                }
            }
        }
    }

    public int getOntologyHierarchy(TreeNode ontoRoot, String nameToSelect) {
        int classCounter = 0;
        OWLClass clazz = manager.getOWLDataFactory().getOWLThing();
        System.out.println("Class       : " + clazz);
        Set<OWLClass> set = domainOntology.classesInSignature().collect(Collectors.toSet());
        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(domainOntology);
        printHierarchy(reasoner, clazz, 0, ontoRoot, nameToSelect);
        for (OWLClass cl : set) {
            classCounter++;
            if (!reasoner.isSatisfiable(cl)) {
                System.out.println("XXX: " + cl.getIRI().getShortForm());
            }
        }
        reasoner.dispose();
        return classCounter;
    }

    private void printHierarchy(OWLReasoner reasoner, OWLClass clazz, int level, TreeNode tn, String nameToSelect) {
        if (reasoner.isSatisfiable(clazz)) {
            for (int i = 0; i < level * 3; i++) {
                System.out.print(" ");
            }
            System.out.println(clazz.getIRI().getShortForm());
            TreeNode n = null;
            if (!clazz.getIRI().getShortForm().equals("Thing")) {
                DataTreeNode dtn = new LexiconControllerTabViewList.DataTreeNode(clazz.getIRI().getShortForm(), clazz.getIRI().toString(), "", "", "false", "false", "", 0);
                n = new DefaultTreeNode(dtn, tn);
                if (nameToSelect.equals("collapse")) {
                    n.setExpanded(false);
                } else {
                    n.setExpanded(true);
                }
            }
            NodeSet<OWLClass> c = reasoner.getSubClasses(clazz, true);
            for (OWLClass child : c.entities().collect(Collectors.toList())) {
                if (!child.equals(clazz)) {
                    printHierarchy(reasoner, child, level + 1, n == null ? tn : n, nameToSelect);
                }
            }

        }
    }

    public List<String> getClasses() {
        ArrayList<String> al = new ArrayList();
        OWLClass clazz = manager.getOWLDataFactory().getOWLThing();
        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(domainOntology);
        printClasses(reasoner, clazz, 0, al);
        reasoner.dispose();
        return al;
    }

    public List<String> getSubClassesOf(String clazz) {
        ArrayList<String> al = new ArrayList();
        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(domainOntology);
        OWLClass cl = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("domainOntology:"), "#" + clazz);
        printClasses(reasoner, cl, al);
        reasoner.dispose();
        al.remove(clazz);
        return al;
    }

    public List<ReferenceMenuTheme> getOntologyEntities() {
        ArrayList<ReferenceMenuTheme> alClasses = new ArrayList();
        ArrayList<ReferenceMenuTheme> alObjProps = new ArrayList();
        ArrayList<ReferenceMenuTheme> alDataProps = new ArrayList();
        ArrayList<ReferenceMenuTheme> alIndividuals = new ArrayList();
        OWLClass clazz = manager.getOWLDataFactory().getOWLThing();
        OWLObjectProperty objProp = factory.getOWLTopObjectProperty();
        OWLDataProperty dataProp = factory.getOWLTopDataProperty();
        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(domainOntology);
        _printClasses(reasoner, clazz, 0, alClasses, 1);
        _printObjectProperties(reasoner, objProp, 0, alObjProps, 1);
        _printDataProperties(reasoner, dataProp, 0, alDataProps, 1);
        //_printIndividuals(reasoner, clazz, alIndividuals, 1);
        reasoner.dispose();
        idNumbering(alClasses, alObjProps, alDataProps);
        ontoRefItems.addAll(alClasses);
        ontoRefItems.addAll(alObjProps);
        ontoRefItems.addAll(alDataProps);
        //ontoRefItems.addAll(alIndividuals);
        return ontoRefItems;
    }

    private void idNumbering(ArrayList<ReferenceMenuTheme> alc, ArrayList<ReferenceMenuTheme> alo, ArrayList<ReferenceMenuTheme> ald) {
        int id = 0;
        for (ReferenceMenuTheme _alc : alc) {
            _alc.setId(id);
            id = id + 1;
        }
        for (ReferenceMenuTheme _alo : alo) {
            _alo.setId(id);
            id = id + 1;
        }
        for (ReferenceMenuTheme _ald : ald) {
            _ald.setId(id);
            id = id + 1;
        }
    }

    private List<ReferenceMenuTheme> _printClasses(OWLReasoner reasoner, OWLClass clazz, int level, ArrayList<ReferenceMenuTheme> al, int id) {
        if (!clazz.getIRI().getShortForm().equals("Thing") && !clazz.getIRI().getShortForm().equals("Nothing")) {
            al.add(new ReferenceMenuTheme(id, ReferenceMenuTheme.itemType.clazz, clazz.getIRI().getShortForm()));
        }
        NodeSet<OWLClass> c = reasoner.getSubClasses(clazz, true);
        id = id + 1;
        for (OWLClass child : c.entities().collect(Collectors.toList())) {
            if (!child.equals(clazz)) {
                _printClasses(reasoner, child, level + 1, al, id);
            }
        }
        return al;
    }

    private List<ReferenceMenuTheme> _printObjectProperties(OWLReasoner reasoner, OWLObjectProperty objProp, int level, ArrayList<ReferenceMenuTheme> al, int id) {
        if (!objProp.getIRI().getShortForm().equals("topObjectProperty") && !objProp.getIRI().getShortForm().equals("bottomObjectProperty")
                && !contains(al, objProp.getIRI().getShortForm())) {
            al.add(new ReferenceMenuTheme(id, ReferenceMenuTheme.itemType.objectProperty, objProp.getIRI().getShortForm()));
        }
        NodeSet<OWLObjectPropertyExpression> op = reasoner.getSubObjectProperties(objProp, true);
        id = id + 1;
        for (OWLObjectPropertyExpression child : op.entities().collect(Collectors.toList())) {
            if (!child.getNamedProperty().getIRI().equals(objProp)) {
                _printObjectProperties(reasoner, child.getNamedProperty(), level + 1, al, id);
            }
        }
        return al;
    }

    private List<ReferenceMenuTheme> _printDataProperties(OWLReasoner reasoner, OWLDataProperty dataProp, int level, ArrayList<ReferenceMenuTheme> al, int id) {
        if (!dataProp.getIRI().getShortForm().equals("topDataProperty") && !dataProp.getIRI().getShortForm().equals("bottomDataProperty")
                && !contains(al, dataProp.getIRI().getShortForm())) {
            al.add(new ReferenceMenuTheme(id, ReferenceMenuTheme.itemType.dataProperty, dataProp.getIRI().getShortForm()));
        }
        NodeSet<OWLDataProperty> op = reasoner.getSubDataProperties(dataProp, true);
        id = id + 1;
        for (OWLDataProperty child : op.entities().collect(Collectors.toList())) {
            if (!child.getIRI().equals(dataProp)) {
                _printDataProperties(reasoner, child, level + 1, al, id);
            }
        }
        return al;
    }

    private List<ReferenceMenuTheme> _printIndividuals(OWLReasoner reasoner, OWLClass clazz, ArrayList<ReferenceMenuTheme> al, int id) {
        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(clazz, InferenceDepth.ALL);
        for (OWLNamedIndividual i : individuals.entities().collect(Collectors.toList())) {
            al.add(new ReferenceMenuTheme(id, ReferenceMenuTheme.itemType.instance, i.getIRI().getShortForm()));
            id = id + 1;
        }
        return al;
    }

    private boolean contains(ArrayList<ReferenceMenuTheme> al, String objProp) {
        for (ReferenceMenuTheme rmt : al) {
            if (rmt.getName().equals(objProp)) {
                return true;
            }
        }
        return false;
    }

    private List<String> printClasses(OWLReasoner reasoner, OWLClass clazz, int level, ArrayList<String> al) {
        if (!clazz.getIRI().getShortForm().equals("Thing") && !clazz.getIRI().getShortForm().equals("Nothing")) {
            al.add(clazz.getIRI().getShortForm());
        }
        NodeSet<OWLClass> c = reasoner.getSubClasses(clazz, true);
        for (OWLClass child : c.entities().collect(Collectors.toList())) {
            if (!child.equals(clazz)) {
                printClasses(reasoner, child, level + 1, al);
            }
        }
        return al;
    }

    private List<String> printClasses(OWLReasoner reasoner, OWLClass clazz, ArrayList<String> al) {
        if (!clazz.getIRI().getShortForm().equals("Thing") && !clazz.getIRI().getShortForm().equals("Nothing")) {
            al.add(clazz.getIRI().getShortForm());
        }
        NodeSet<OWLClass> c = reasoner.getSubClasses(clazz, true);
        for (OWLClass child : c.entities().collect(Collectors.toList())) {
            if (!child.equals(clazz)) {
                printClasses(reasoner, child, al);
            }
        }
        return al;
    }

    public int getOntologyDatatypePropertiesNumber() {
//        return domainOntology.getAxiomCount(AxiomType.DATA_PROPERTY_ASSERTION);
        return (int) domainOntology.dataPropertiesInSignature().count();
    }

    public int getOntologyObjectPropertiesNumber() {
        return (int) domainOntology.objectPropertiesInSignature().count();
//        return domainOntology.getAxiomCount(AxiomType.OBJECT_PROPERTY_ASSERTION);
    }

    public ArrayList<String> getOntologyClasses() {
        ArrayList<String> al = new ArrayList();
        Set<OWLClass> set = domainOntology.classesInSignature().collect(Collectors.toSet());
        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(domainOntology);
        for (OWLClass cl : set) {
            if (reasoner.isSatisfiable(cl)) {
                al.add(cl.getIRI().getShortForm());
            }
        }
        reasoner.dispose();
        return al;
    }

    public int getPropertyHierarchy(TreeNode ontoRoot) {
        int classCounter = 0;
        individualsNumber = 0;
        OWLObjectProperty objProp = factory.getOWLTopObjectProperty();
        System.out.println("Class       : " + objProp);
        Set<OWLObjectProperty> set = domainOntology.objectPropertiesInSignature().collect(Collectors.toSet());
        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(domainOntology);
        printHierarchy(reasoner, objProp, 0, ontoRoot);
//        for (OWLClass cl : set) {
//            classCounter++;
//            individualsNumber = individualsNumber + getIndividualsNumberByClass(cl);
//            if (!reasoner.isSatisfiable(cl)) {
//                log(Level.INFO, (loginController == null) ? new Account() : loginController.getAccount(), "XXX: " + cl.getIRI().getShortForm());
//                // System.out.println("XXX: " + cl.getIRI().getShortForm());
//            }
//        }
        reasoner.dispose();
        return classCounter;
    }

    private void printHierarchy(OWLReasoner reasoner, OWLObjectPropertyExpression objProp, int level, TreeNode tn) {

        for (int i = 0; i < level * 3; i++) {
            System.out.print(" ");
        }
        System.out.println(objProp.getNamedProperty().getIRI().getShortForm());
        TreeNode n = null;
//        if (!objProp.getIRI().getShortForm().equals("topObjectProperty")) {
//            LexiconControllerTabViewList.DataTreeNode dtn = new LexiconControllerTabViewList.DataTreeNode(clazz.getIRI().getShortForm(), clazz.getIRI().toString(), "", "", "false", "false", "", 0);
//            n = new DefaultTreeNode(dtn, tn);
//            n.setExpanded(true);
//        }
        //NodeSet<OWLClass> c = reasoner.getSubClasses(clazz, true);
        NodeSet<OWLObjectPropertyExpression> op = reasoner.getSubObjectProperties(objProp, true);
        for (OWLObjectPropertyExpression child : op.entities().collect(Collectors.toList())) {
            if (!child.equals(objProp)) {
                printHierarchy(reasoner, child, (level + 1), null);
            }
        }
//        for (OWLClass child : c.entities().collect(Collectors.toList())) {
//            if (!child.equals(clazz)) {
//                printHierarchy(reasoner, child, level + 1, n == null ? tn : n);
//            }
//        }

    }

    public void getOntoDetails() {
        for (OWLClass oc : domainOntology.classesInSignature().collect(Collectors.toSet())) {
            System.out.println("Class: " + oc.toString());
            // get all axioms for each class
            for (OWLAxiom axiom : domainOntology.axioms(oc).collect(Collectors.toSet())) {
                System.out.println("\tAxiom: " + axiom.toString());
                // create an object visitor to get to the subClass restrictions
                axiom.accept(new OWLObjectVisitor() {
                    // found the subClassOf axiom  
                    public void visit(OWLSubClassOfAxiom subClassAxiom) {
                        // create an object visitor to read the underlying (subClassOf) restrictions
                        subClassAxiom.getSuperClass().accept(new OWLObjectVisitor() {

                            public void visit(OWLObjectSomeValuesFrom someValuesFromAxiom) {
                                System.out.println("\t\tClass: " + oc.toString());
                                System.out.println("\t\tClassExpressionType: " + someValuesFromAxiom.getClassExpressionType().toString());
                                System.out.println("\t\tProperty: " + someValuesFromAxiom.getProperty().toString());
                                System.out.println("\t\tObject: " + someValuesFromAxiom.getFiller().toString());
                                System.out.println();
                            }

                            public void visit(OWLObjectExactCardinality exactCardinalityAxiom) {
                                System.out.println("\t\tClass: " + oc.toString());
                                System.out.println("\t\tClassExpressionType: " + exactCardinalityAxiom.getClassExpressionType().toString());
                                System.out.println("\t\tCardinality: " + exactCardinalityAxiom.getCardinality());
                                System.out.println("\t\tProperty: " + exactCardinalityAxiom.getProperty().toString());
                                System.out.println("\t\tObject: " + exactCardinalityAxiom.getFiller().toString());
                                System.out.println();
                            }

                            public void visit(OWLObjectMinCardinality minCardinalityAxiom) {
                                System.out.println("\t\tClass: " + oc.toString());
                                System.out.println("\t\tClassExpressionType: " + minCardinalityAxiom.getClassExpressionType().toString());
                                System.out.println("\t\tCardinality: " + minCardinalityAxiom.getCardinality());
                                System.out.println("\t\tProperty: " + minCardinalityAxiom.getProperty().toString());
                                System.out.println("\t\tObject: " + minCardinalityAxiom.getFiller().toString());
                                System.out.println();
                            }

                            public void visit(OWLObjectMaxCardinality maxCardinalityAxiom) {
                                System.out.println("\t\tClass: " + oc.toString());
                                System.out.println("\t\tClassExpressionType: " + maxCardinalityAxiom.getClassExpressionType().toString());
                                System.out.println("\t\tCardinality: " + maxCardinalityAxiom.getCardinality());
                                System.out.println("\t\tProperty: " + maxCardinalityAxiom.getProperty().toString());
                                System.out.println("\t\tObject: " + maxCardinalityAxiom.getFiller().toString());
                                System.out.println();
                            }

                            public void visit(OWLObjectAllValuesFrom allValuesFromAxiom) {
                                System.out.println("\t\tClass: " + oc.toString());
                                System.out.println("\t\tClassExpressionType: " + allValuesFromAxiom.getClassExpressionType().toString());
                                System.out.println("\t\tProperty: " + allValuesFromAxiom.getProperty().toString());
                                System.out.println("\t\tObject: " + allValuesFromAxiom.getFiller().toString());
                                System.out.println();
                            }

                        });
                    }
                });

            }
        }
    }

    public void getPropertyDetails() {
        for (OWLObjectProperty oc : domainOntology.objectPropertiesInSignature().collect(Collectors.toSet())) {
            System.out.println("Property: " + oc.toString());
            // get all axioms for each class
            for (OWLAxiom axiom : domainOntology.axioms(oc).collect(Collectors.toSet())) {
                System.out.println("\tAxiom: " + axiom.toString());
            }
        }
        for (OWLDataProperty oc : domainOntology.dataPropertiesInSignature().collect(Collectors.toSet())) {
            System.out.println("Property: " + oc.toString());
            // get all axioms for each class
            for (OWLAxiom axiom : domainOntology.axioms(oc).collect(Collectors.toSet())) {
                System.out.println("\tAxiom: " + axiom.toString());
            }
        }

    }

    public synchronized void persist(UploadedFile f) throws IOException {
        System.out.println("[" + getTimestamp() + "] LexO-lite : persist imported ontology file " + f.getFileName());
        InputStream input = f.getInputstream();
        File targetFile = new File(System.getProperty("user.home") + Label.LEXO_FOLDER + f.getFileName());
        FileUtils.copyInputStreamToFile(input, targetFile);
    }

    private void copyFile(File src, File trg) throws FileNotFoundException, IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(src).getChannel();
            destChannel = new FileOutputStream(trg).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } finally {
            sourceChannel.close();
            destChannel.close();
        }
    }

    private String getTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
