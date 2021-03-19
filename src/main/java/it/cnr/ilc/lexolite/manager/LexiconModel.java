/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.LexOliteProperty;
import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.constant.Namespace;
import it.cnr.ilc.lexolite.constant.OntoLexEntity;
import it.cnr.ilc.lexolite.controller.BaseController;
import it.cnr.ilc.lexolite.controller.LoginController;
import it.cnr.ilc.lexolite.manager.LemmaData.ExtensionAttributeIstance;
import it.cnr.ilc.lexolite.manager.LemmaData.LexicalRelation;
import it.cnr.ilc.lexolite.manager.LemmaData.ReifiedLexicalRelation;
import it.cnr.ilc.lexolite.manager.LemmaData.Word;
import it.cnr.ilc.lexolite.manager.SenseData.Openable;
import it.cnr.ilc.lexolite.manager.SenseData.ReifiedSenseRelation;
import it.cnr.ilc.lexolite.manager.SenseData.ReifiedTranslationRelation;
import it.cnr.ilc.lexolite.manager.SenseData.SenseRelation;
import it.cnr.ilc.lexolite.util.LexiconUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.singleton;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.rdf.rdfxml.renderer.OWLOntologyXMLNamespaceManager;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.search.Searcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

/**
 *
 * @author andreabellandi
 */
public class LexiconModel extends BaseController {

    @Inject
    private OntologyManager ontologyManager;
    @Inject
    private LoginController loginController;

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory factory;
    private PrefixManager pm;

    public LexiconModel(FileUploadEvent f) {
        manager = OWLManager.createOWLOntologyManager();
        try (InputStream inputStream = f.getFile().getInputStream()) {
            ontology = manager.loadOntologyFromOntologyDocument(inputStream);
            factory = manager.getOWLDataFactory();
            setPrefixes();
        } catch (OWLOntologyCreationException | IOException ex) {
            log(org.apache.log4j.Level.ERROR, loginController.getAccount(), "LOADING lexicon ", ex);
        }
    }

    public LexiconModel() {
        LexOliteProperty.load();
        manager = OWLManager.createOWLOntologyManager();
        try (InputStream inputStream = new FileInputStream(System.getProperty("user.home") + Label.LEXO_FOLDER
                + LexOliteProperty.getProperty(Label.LEXICON_FILE_NAME_KEY))) {
            ontology = manager.loadOntologyFromOntologyDocument(inputStream);
            factory = manager.getOWLDataFactory();
            setPrefixes();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LexiconModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | OWLOntologyCreationException ex) {
            Logger.getLogger(LexiconModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setPrefixes() {
        pm = new DefaultPrefixManager();
        pm.setPrefix("lexicon", getLexiconNamespace());
        pm.setPrefix("lexinfo", Namespace.LEXINFO);
        pm.setPrefix("rdfs", Namespace.RDFS);
        pm.setPrefix("skos", Namespace.SKOS);
        pm.setPrefix("ontolex", Namespace.ONTOLEX);
        pm.setPrefix("lime", Namespace.LIME);
        pm.setPrefix("dct", Namespace.DCT);
        pm.setPrefix("decomp", Namespace.DECOMP);
        pm.setPrefix("rdf", Namespace.RDF);
        pm.setPrefix("vartrans", Namespace.VARTRANS);
        pm.setPrefix("trcat", Namespace.TRCAT);
        pm.setPrefix("synsem", Namespace.SYNSEM);
        pm.setPrefix("extension", Namespace.EXTENSION);
        pm.setPrefix("melchuck", Namespace.LEXFUN);
    }

    private String getLexiconNamespace() {
        String ret = "";
        //  if (!ontology.getOntologyID().getOntologyIRI().isEmpty()) {
        //    ret = ontology.getOntologyID().getOntologyIRI().get().toURI().toString();
        // } else {
        OWLDocumentFormat format = manager.getOntologyFormat(ontology);
        OWLOntologyXMLNamespaceManager nsManager = new OWLOntologyXMLNamespaceManager(ontology, format);
        ret = nsManager.getDefaultNamespace();
        //}
        return ret.contains("#") ? ret : ret + "#";
    }

    // params: langName, uriLang, lingCat, descritpion, creator
    public void addNewLangLexicon(String... params) {
        OWLClass lexiconClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("lime:"), "Lexicon");
        OWLNamedIndividual lexiconEntry = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), params[0] + "_lexicon");
        addIndividualAxiom(lexiconClass, lexiconEntry);
        addDataPropertyAxiom("language", lexiconEntry, params[0], pm.getPrefixName2PrefixMap().get("lime:"));
        addDataPropertyAxiom("language", lexiconEntry, params[1], pm.getPrefixName2PrefixMap().get("dct:"));
        addDataPropertyAxiom("linguisticCatalog", lexiconEntry, params[2], pm.getPrefixName2PrefixMap().get("lime:"));
        addDataPropertyAxiom("description", lexiconEntry, params[3], pm.getPrefixName2PrefixMap().get("dct:"));
        addDataPropertyAxiom("creator", lexiconEntry, params[4], pm.getPrefixName2PrefixMap().get("dct:"));
    }

    // NEW LEMMA ACTION: write all the triples about the new lemma entry
    public void addLemma(LemmaData ld, String lex) {
        String lemmaInstance = LexiconUtil.getIRI(ld.getFormWrittenRepr(), ld.getPoS(), ld.getLanguage(), "lemma");
        String entryInstance = LexiconUtil.getIRI(ld.getFormWrittenRepr(), ld.getPoS(), ld.getLanguage(), "entry");
        ld.setIndividual(lemmaInstance);
        OWLNamedIndividual lexicon = getIndividual(lex);
        OWLNamedIndividual le = getEntry(entryInstance, ld.getType());
        OWLNamedIndividual cf = getForm(lemmaInstance);

        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.ENTRY.getLabel(), lexicon, le, pm.getPrefixName2PrefixMap().get("lime:"));
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.CANONICALFORM.getLabel(), le, cf, pm.getPrefixName2PrefixMap().get("ontolex:"));
        setMoprhology(le, cf, ld);
    }

    private void setMoprhology(OWLNamedIndividual le, OWLNamedIndividual cf, LemmaData ld) {
        addDataPropertyAxiom(OntoLexEntity.DataProperty.WRITTENREP.getLabel(), cf, ld.getFormWrittenRepr(), pm.getPrefixName2PrefixMap().get("ontolex:"));
        if (!ld.getPoS().equals(Label.UNSPECIFIED_POS)) {
            if (ld.getType().equals(OntoLexEntity.Class.WORD.getLabel()) || ld.getType().equals(OntoLexEntity.Class.AFFIX.getLabel())) {
                addObjectPropertyAxiom("lexinfo", le, "partOfSpeech", ld.getPoS());
            } else {
                addObjectPropertyAxiom("lexinfo", le, "partOfSpeech", getMultiwordPoS(ld.getPoS()));
            }
        }
        for (LemmaData.MorphoTrait mt : ld.getMorphoTraits()) {
            addObjectPropertyAxiom("lexinfo", cf, mt.getName(), mt.getValue());
        }

        addDataPropertyAxiom("valid", le, "false", pm.getPrefixName2PrefixMap().get("dct:"));
    }

    private String getMultiwordPoS(String pos) {
        switch (pos) {
            case "nounPhrase":
                return "noun";
            case "verbPhrase":
                return "verb";
            case "adjectivePhrase":
                return "adjective";
        }
        return Label.UNSPECIFIED_POS;
    }

    // write the entry as individual of the related class and returns it 
    private OWLNamedIndividual getEntry(String uri, String clazz) {
        OWLClass lexicalEntryClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("ontolex:"), clazz);
        OWLNamedIndividual lexicalEntry = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), uri);
        addIndividualAxiom(lexicalEntryClass, lexicalEntry);
        return lexicalEntry;
    }

    // write the form as individual of the related class and returns it 
    private OWLNamedIndividual getForm(String uri) {
        OWLClass lexicalFormClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("ontolex:"), OntoLexEntity.Class.FORM.getLabel());
        OWLNamedIndividual form = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), uri);
        addIndividualAxiom(lexicalFormClass, form);
        return form;
    }

    // write the sense as individual of the related class and returns it 
    private OWLNamedIndividual getSense(String senseName, int n) {
        OWLClass lexicalSenseClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("ontolex:"), "LexicalSense");
        OWLNamedIndividual sense = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), senseName + n);
        addIndividualAxiom(lexicalSenseClass, sense);
        return sense;
    }

    // returns the ontological individual of a given uri string
    private OWLNamedIndividual getIndividual(String uri) {
        return factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), uri);
    }

    private OWLObjectProperty getObjectProperty(String uri, String uriObj) {
        return factory.getOWLObjectProperty(pm.getPrefixName2PrefixMap().get(uriObj + ":"), uri);
    }

    // Save a new lemma note
    public void saveLemmaNote(LemmaData ld, String oldNote) {
        OWLNamedIndividual lemma = getIndividual(ld.getIndividual());
        if (oldNote.isEmpty()) {
            addDataPropertyAxiom("comment", lemma, ld.getNote(), pm.getPrefixName2PrefixMap().get("rdfs:"));
        } else {
            updateDataPropertyAxiom(lemma, "comment", oldNote, ld.getNote(), pm.getPrefixName2PrefixMap().get("rdfs:"));
        }
    }

    // write all triples about lemma entry with RENAMING
    public void updateLemmaWithRenaming(LemmaData oldLemma, LemmaData newLemma) {
        String oldLemmaInstance = oldLemma.getIndividual();
        String newLemmaInstance = LexiconUtil.getIRI(newLemma.getFormWrittenRepr(), newLemma.getPoS(), newLemma.getLanguage(), "lemma");
        String oldEntryInstance = oldLemmaInstance.replace("_lemma", "_entry");
        String newEntryInstance = LexiconUtil.getIRI(newLemma.getFormWrittenRepr(), newLemma.getPoS(), newLemma.getLanguage(), "entry");
        newLemma.setIndividual(newLemmaInstance);
        updateLemma(oldLemma, newLemma);
        IRIrenaming(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + oldLemmaInstance), IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + newLemmaInstance));
        OWLNamedIndividual le = getIndividual(oldEntryInstance);
        // form individuals renaming
        formRenaming(oldLemma, newLemma, le);
        // sense individuals renaming
        senseRenaming(oldLemma, newLemma, le);
        IRIrenaming(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + oldEntryInstance), IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + newEntryInstance));
    }

    private void formRenaming(LemmaData oldLemma, LemmaData newLemma, OWLNamedIndividual le) {
        OWLObjectProperty otherForm = factory.getOWLObjectProperty(pm.getPrefixName2PrefixMap().get("ontolex:"), OntoLexEntity.ObjectProperty.OTHERFORM.getLabel());
        for (OWLIndividual i : EntitySearcher.getObjectPropertyValues(le, otherForm, ontology).collect(Collectors.toList())) {
            String formInstance = i.toStringID().replace(pm.getPrefixName2PrefixMap().get("lexicon:"), "");
            formInstance = formInstance.replace(LexiconUtil.sanitize(oldLemma.getFormWrittenRepr()), LexiconUtil.sanitize(newLemma.getFormWrittenRepr()));
            if (!oldLemma.getPoS().equals(newLemma.getPoS())) {
                // old pos is unspecified
                if (oldLemma.getPoS().isEmpty()) {
                    formInstance = formInstance.replace(Label.UNSPECIFIED_POS, newLemma.getPoS());
                } else {
                    // new pos is unspecified
                    if (newLemma.getPoS().isEmpty()) {
                        formInstance = formInstance.replace(oldLemma.getPoS(), Label.UNSPECIFIED_POS);
                    } else {
                        formInstance = formInstance.replace(oldLemma.getPoS(), newLemma.getPoS());
                    }
                }
            }
            IRIrenaming(IRI.create(i.toStringID()), IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + formInstance));
        }
    }

    private void senseRenaming(LemmaData oldLemma, LemmaData newLemma, OWLNamedIndividual le) {
        OWLObjectProperty sense = factory.getOWLObjectProperty(pm.getPrefixName2PrefixMap().get("ontolex:"), OntoLexEntity.ObjectProperty.SENSE.getLabel());
        int senseNumb = 1;
        for (OWLIndividual i : EntitySearcher.getObjectPropertyValues(le, sense, ontology).collect(Collectors.toList())) {
            String senseInstance = LexiconUtil.getIRI(newLemma.getFormWrittenRepr(),
                    newLemma.getPoS().isEmpty() ? Label.UNSPECIFIED_POS : newLemma.getPoS(), oldLemma.getLanguage(), "sense");
            IRIrenaming(IRI.create(i.toStringID()), IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + senseInstance + senseNumb));
            senseNumb++;
        }
    }

    private void synsemRenaming(LemmaData oldLemma, LemmaData newLemma, OWLNamedIndividual le) {
        OWLObjectProperty synBehavior = factory.getOWLObjectProperty(pm.getPrefixName2PrefixMap().get("synsem:"), OntoLexEntity.ObjectProperty.SYNBEHAVIOR.getLabel());
        int frame = 1;
        for (OWLIndividual i : EntitySearcher.getObjectPropertyValues(le, synBehavior, ontology).collect(Collectors.toList())) {
            String frameInstance = LexiconUtil.getIRI(newLemma.getFormWrittenRepr(),
                    newLemma.getPoS().isEmpty() ? Label.UNSPECIFIED_POS : newLemma.getPoS(), oldLemma.getLanguage(), "frame");
            IRIrenaming(IRI.create(i.toStringID()), IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + frameInstance + frame));
            frame++;
        }
    }

    private void IRIrenaming(IRI oldIRI, IRI newIRI) {
        OWLEntityRenamer ren = new OWLEntityRenamer(manager, singleton(ontology));
        List<OWLOntologyChange> changes = ren.changeIRI(oldIRI, newIRI);
        ontology.getOWLOntologyManager().applyChanges(changes);
    }

    private void updateEntryValidity(OWLNamedIndividual entrySubject, LemmaData oldLemma, LemmaData newLemma) {
        if (!oldLemma.getValid().equals(newLemma.getValid())) {
            if (newLemma.getValid().equals("false")) {
                removeDataPropertyAxiom("dct", entrySubject, "valid", oldLemma.getValid());
                addDataPropertyAxiom("valid", entrySubject, "false", pm.getPrefixName2PrefixMap().get("dct:"));
            } else {
                removeDataPropertyAxiom("dct", entrySubject, "valid", "false");
                addDataPropertyAxiom("valid", entrySubject, newLemma.getValid(), pm.getPrefixName2PrefixMap().get("dct:"));
            }
        }
    }

    public void updateLemma(LemmaData oldLemma, LemmaData newLemma) {
        String _subject = oldLemma.getIndividual();
        OWLNamedIndividual subject = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + _subject));
        OWLNamedIndividual entrySubject = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + _subject.replace("_lemma", "_entry")));
        updatePhonetic(subject, oldLemma.getFormPhoneticRep(), newLemma.getFormPhoneticRep());
        updateMorphology(entrySubject, subject, oldLemma, newLemma);
        updateEntryValidity(entrySubject, oldLemma, newLemma);
//        updateObjectPropertyAxiom(entrySubject, OntoLexEntity.ObjectProperty.DENOTES.getLabel(),
//                oldLemma.getOWLClass().getName(), newLemma.getOWLClass().getName(), pm.getPrefixName2PrefixMap().get("ontolex:"));
        //updateDataPropertyAxiom(entrySubject, "verified", oldLemma.getValid(), newLemma.getValid(), pm.getPrefixName2PrefixMap().get("dct:"));
        updateLinkingRelation(oldLemma.getIndividual(), oldLemma.getSeeAlso(), newLemma.getSeeAlso(), "seeAlso");
        updateExtensionAttribute(subject, oldLemma.getExtensionAttributeInstances(), newLemma.getExtensionAttributeInstances());
        if (oldLemma.getFormWrittenRepr().equals(newLemma.getFormWrittenRepr())) {
            if (!oldLemma.getPoS().equals(newLemma.getPoS())) {
                // it needs to modify IRI anyway
                String oldLemmaInstance = oldLemma.getIndividual();
                String newLemmaInstance = LexiconUtil.getIRI(newLemma.getFormWrittenRepr(), newLemma.getPoS(), newLemma.getLanguage(), "lemma");
                String oldEntryInstance = oldLemmaInstance.replace("_lemma", "_entry");
                String newEntryInstance = LexiconUtil.getIRI(newLemma.getFormWrittenRepr(), newLemma.getPoS(), newLemma.getLanguage(), "entry");
                newLemma.setIndividual(newLemmaInstance);
                IRIrenaming(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + oldLemmaInstance), IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + newLemmaInstance));
                OWLNamedIndividual le = getIndividual(oldEntryInstance);
                // form individuals renaming
                formRenaming(oldLemma, newLemma, le);
                // sense individuals renaming
                senseRenaming(oldLemma, newLemma, le);
                IRIrenaming(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + oldEntryInstance), IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + newEntryInstance));
                // synsem individuals renaming

            }
        }
    }

    private void updatePhonetic(OWLNamedIndividual subject, String oldPhonetic, String newPhonetic) {
        removeDataPropertyAxiom("ontolex", subject, OntoLexEntity.DataProperty.PHONETICREP.getLabel(), oldPhonetic);
        if (!newPhonetic.isEmpty() && !newPhonetic.equals(Label.NO_ENTRY_FOUND)) {
            addDataPropertyAxiom(OntoLexEntity.DataProperty.PHONETICREP.getLabel(), subject, newPhonetic, pm.getPrefixName2PrefixMap().get("ontolex:"));
        }
    }

    private void updateExtensionAttribute(OWLNamedIndividual subject, ArrayList<ExtensionAttributeIstance> oldExtAtt,
            ArrayList<ExtensionAttributeIstance> newExtAtt) {
        // *******************
        // let suppose that extension attributes are i) at lemma-level, ii) single value
        // *******************
        for (ExtensionAttributeIstance oldEai : oldExtAtt) {
            // delete oldier attributes
            removeDataPropertyAxiom("extension", subject, oldEai.getName(), oldEai.getValue());
        }
        for (ExtensionAttributeIstance newEai : newExtAtt) {
            // add newer attributes
            addDataPropertyAxiom(newEai.getName(), subject, newEai.getValue(), pm.getPrefixName2PrefixMap().get("extension:"));
        }
    }

    private void updateMorphology(OWLNamedIndividual entrySubject, OWLNamedIndividual subject, LemmaData oldLemma, LemmaData newLemma) {
        updateDataPropertyAxiom(subject, OntoLexEntity.DataProperty.WRITTENREP.getLabel(), oldLemma.getFormWrittenRepr(), newLemma.getFormWrittenRepr(), pm.getPrefixName2PrefixMap().get("ontolex:"));

        if (oldLemma.getType().equals(OntoLexEntity.Class.WORD.getLabel()) || oldLemma.getType().equals(OntoLexEntity.Class.WORD.getLabel())) {
            updateObjectPropertyAxiom(entrySubject, "partOfSpeech", oldLemma.getPoS(), newLemma.getPoS(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
        } else {
            updateObjectPropertyAxiom(entrySubject, "partOfSpeech", getMultiwordPoS(oldLemma.getPoS()), getMultiwordPoS(newLemma.getPoS()), pm.getPrefixName2PrefixMap().get("lexinfo:"));
        }

        for (LemmaData.MorphoTrait mt : oldLemma.getMorphoTraits()) {
            removeObjectPropertyAxiom("lexinfo", subject, mt.getName(),
                    factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexinfo:") + mt.getValue())));
        }
        for (LemmaData.MorphoTrait mt : newLemma.getMorphoTraits()) {
            addObjectPropertyAxiom("lexinfo", subject, mt.getName(), mt.getValue());
        }
    }

    private void updateLinkingRelation(String sbj, ArrayList<Word> oldWords, ArrayList<Word> newWords, String rel) {
        // for each old linking relation, if it is not in new linking relation then remove old linking relation
        for (Word w : oldWords) {
            if ((!contains(newWords, w)) && (!w.getWrittenRep().isEmpty())) {
                if (rel.equals("seeAlso")) {
                    removeObjectPropertyAxiom("rdf", getIndividual(sbj.replace("_lemma", "_entry")), rel, getIndividual(w.getOWLName().replace("_lemma", "_entry")));
                }
            }
        }
        // for each new linking relation, if it is not in old linking relation then add new linking relation
        for (Word w : newWords) {
            if ((!contains(oldWords, w)) && (!w.getWrittenRep().isEmpty())) {
                if (rel.equals("seeAlso")) {
                    addObjectPropertyAxiom(rel, getIndividual(sbj.replace("_lemma", "_entry")), getIndividual(w.getOWLName().replace("_lemma", "_entry")), pm.getPrefixName2PrefixMap().get("rdf:"));
                }
            }
        }
    }

    private boolean contains(ArrayList<Word> alw, Word w) {
        for (Word _w : alw) {
            if (w.getOWLName().equals(_w.getOWLName())) {
                return true;
            }
        }
        return false;
    }

    private boolean contains(ArrayList<LexicalRelation> alr, LexicalRelation lr) {
        for (LexicalRelation _lr : alr) {
            if (_lr.getOWLName().equals(lr.getOWLName()) && (_lr.getRelation().equals(lr.getRelation()))) {
                return true;
            }
        }
        return false;
    }

    private boolean contains(ArrayList<ReifiedLexicalRelation> alr, ReifiedLexicalRelation lr) {
        for (ReifiedLexicalRelation _lr : alr) {
            if (_lr.getSourceOWLName().equals(lr.getSourceOWLName()) && (_lr.getCategory().equals(lr.getCategory()))
                    && (_lr.getTargetOWLName().equals(lr.getTargetOWLName()))) {
                return true;
            }
        }
        return false;
    }

    private void updateDirectLexicalRelation(String sbj, ArrayList<LexicalRelation> oldLexRels, ArrayList<LexicalRelation> newLexRels) {
        // for each old lexical relation relation, if it is not in new lexical relation then remove old lexical relation
        for (LexicalRelation lr : oldLexRels) {
            if ((!contains(newLexRels, lr)) && (!lr.getWrittenRep().isEmpty()) && (!lr.getRelation().isEmpty())) {
                if (lr.getRelation().equals(OntoLexEntity.ObjectProperty.LEXICALREL.getLabel())
                        || lr.getRelation().equals(OntoLexEntity.ObjectProperty.TRANSLATABLEAS.getLabel())) {
                    removeObjectPropertyAxiom("vartrans", getIndividual(sbj.replace("_lemma", "_entry")), lr.getRelation(), getIndividual(lr.getOWLName().replace("_lemma", "_entry")));
                } else {
                    removeObjectPropertyAxiom("lexinfo", getIndividual(sbj.replace("_lemma", "_entry")), lr.getRelation(), getIndividual(lr.getOWLName().replace("_lemma", "_entry")));
                }
            }
        }
        // for each new lexical relation, if it is not in old lexical relation then add new lexical relation
        for (LexicalRelation lr : newLexRels) {
            if ((!contains(oldLexRels, lr)) && (!lr.getWrittenRep().isEmpty()) && (!lr.getRelation().isEmpty())) {
                if (lr.getRelation().equals(OntoLexEntity.ObjectProperty.LEXICALREL.getLabel())
                        || lr.getRelation().equals(OntoLexEntity.ObjectProperty.TRANSLATABLEAS.getLabel())) {
                    addObjectPropertyAxiom(lr.getRelation(), getIndividual(sbj.replace("_lemma", "_entry")), getIndividual(lr.getOWLName().replace("_lemma", "_entry")), pm.getPrefixName2PrefixMap().get("vartrans:"));
                } else {
                    addObjectPropertyAxiom(lr.getRelation(), getIndividual(sbj.replace("_lemma", "_entry")), getIndividual(lr.getOWLName().replace("_lemma", "_entry")), pm.getPrefixName2PrefixMap().get("lexinfo:"));
                    addObjectPropertyAxiom(getObjectProperty(lr.getRelation(), "lexinfo"), getObjectProperty(OntoLexEntity.ObjectProperty.LEXICALREL.getLabel(), "vartrans"));
                }
            }
        }
    }

    private void updateIndirectLexicalRelation(ArrayList<LemmaData.ReifiedLexicalRelation> oldLexRels, ArrayList<ReifiedLexicalRelation> newLexRels) {
        // for each old lexical relation relation, if it is not in new lexical relation then remove old lexical relation
        for (ReifiedLexicalRelation lr : oldLexRels) {
            if ((!contains(newLexRels, lr)) && (!lr.getTargetWrittenRep().isEmpty()) && (!lr.getCategory().isEmpty())) {
                removeReifiedLexicalRelation(lr);
            }
        }
        // for each new lexical relation, if it is not in old lexical relation then add new lexical relation
        for (ReifiedLexicalRelation lr : newLexRels) {
            if ((!contains(oldLexRels, lr)) && (!lr.getTargetWrittenRep().isEmpty()) && (!lr.getCategory().isEmpty())) {
                addReifiedLexicalRelation(lr);
            }
        }
    }

    private void addReifiedLexicalRelation(ReifiedLexicalRelation rlr) {
        OWLClass LexicalRelation = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("vartrans:"), OntoLexEntity.Class.LEXICALRELATION.getLabel());
        OWLNamedIndividual lr = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), rlr.getSourceOWLName() + "_" + rlr.getCategory() + "_" + rlr.getTargetOWLName());
        OWLNamedIndividual src = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), rlr.getSourceOWLName());
        OWLNamedIndividual trg = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), rlr.getTargetOWLName());
        OWLNamedIndividual cat = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexinfo:"), rlr.getCategory());
        addIndividualAxiom(LexicalRelation, lr);
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.SOURCE.getLabel(), lr, src, pm.getPrefixName2PrefixMap().get("vartrans:"));
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.TARGET.getLabel(), lr, trg, pm.getPrefixName2PrefixMap().get("vartrans:"));
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.CATEGORY.getLabel(), lr, cat, pm.getPrefixName2PrefixMap().get("vartrans:"));
    }

    private void removeReifiedLexicalRelation(ReifiedLexicalRelation rlr) {
        OWLClass LexicalRelation = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("vartrans:"), OntoLexEntity.Class.LEXICALRELATION.getLabel());
        OWLNamedIndividual lr = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), rlr.getSourceOWLName() + "_" + rlr.getCategory() + "_" + rlr.getTargetOWLName());
        removeIndividualAxiom(LexicalRelation, lr);
    }

    private void addReifiedTranslationSenseRelation(ReifiedTranslationRelation rtr) {
        OWLClass TranslationRelation = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("vartrans:"), OntoLexEntity.Class.TRANSLATION.getLabel());
        OWLNamedIndividual tr = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), rtr.getSource() + "_" + rtr.getCategory() + "_" + rtr.getTarget());
        OWLNamedIndividual src = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), rtr.getSource());
        OWLNamedIndividual trg = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), rtr.getTarget());
        OWLNamedIndividual cat = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("trcat:"), rtr.getCategory());
        addIndividualAxiom(TranslationRelation, tr);
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.SOURCE.getLabel(), tr, src, pm.getPrefixName2PrefixMap().get("vartrans:"));
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.TARGET.getLabel(), tr, trg, pm.getPrefixName2PrefixMap().get("vartrans:"));
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.CATEGORY.getLabel(), tr, cat, pm.getPrefixName2PrefixMap().get("vartrans:"));
        addDataPropertyAxiom("translationConfidence", tr, rtr.getConfidence(), pm.getPrefixName2PrefixMap().get("lexinfo:"));

    }

    private void removeReifiedTranslationSenseRelation(ReifiedTranslationRelation rtr) {
        OWLClass TranslationRelation = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("vartrans:"), OntoLexEntity.Class.TRANSLATION.getLabel());
        OWLNamedIndividual tr = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), rtr.getSource() + "_" + rtr.getCategory() + "_" + rtr.getTarget());
        removeIndividualAxiom(TranslationRelation, tr);
    }

    private void addLexicalFunction(SenseData.LexicalFunction lf) {
        OWLNamedIndividual src = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), lf.getSource());
        OWLNamedIndividual trg = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), lf.getTarget());
        addObjectPropertyAxiom(lf.getLexFunName(), src, trg, pm.getPrefixName2PrefixMap().get("melchuck:"));
    }

    private void removeLexicalFunction(SenseData.LexicalFunction lf) {
        OWLNamedIndividual src = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), lf.getSource());
        OWLNamedIndividual trg = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), lf.getTarget());
        removeObjectPropertyAxiom("melchuck", src, lf.getLexFunName(), trg);
    }

    private void addReifiedSenseRelation(ReifiedSenseRelation rsr) {
        OWLClass SenseRelation = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("vartrans:"), OntoLexEntity.Class.TERMINOLOGICALRELATION.getLabel());
        OWLNamedIndividual termRel = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), rsr.getSource() + "_" + rsr.getCategory() + "_" + rsr.getTarget());
        OWLNamedIndividual src = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), rsr.getSource());
        OWLNamedIndividual trg = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), rsr.getTarget());
        OWLNamedIndividual cat = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexinfo:"), rsr.getCategory());
        addIndividualAxiom(SenseRelation, termRel);
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.SOURCE.getLabel(), termRel, src, pm.getPrefixName2PrefixMap().get("vartrans:"));
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.TARGET.getLabel(), termRel, trg, pm.getPrefixName2PrefixMap().get("vartrans:"));
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.CATEGORY.getLabel(), termRel, cat, pm.getPrefixName2PrefixMap().get("vartrans:"));
    }

    private void removeReifiedSenseRelation(ReifiedSenseRelation rsr) {
        OWLClass SenseRelation = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("vartrans:"), OntoLexEntity.Class.TERMINOLOGICALRELATION.getLabel());
        OWLNamedIndividual termRel = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), rsr.getSource() + "_" + rsr.getCategory() + "_" + rsr.getTarget());
        removeIndividualAxiom(SenseRelation, termRel);
    }

    public void updateLemmaVarTrans(LemmaData oldLemma, LemmaData newLemma) {
        updateDirectLexicalRelation(oldLemma.getIndividual(), oldLemma.getLexRels(), newLemma.getLexRels());
        updateIndirectLexicalRelation(oldLemma.getReifiedLexRels(), newLemma.getReifiedLexRels());
    }

    public void updateSenseVarTrans(ArrayList<SenseData> oldSense, ArrayList<SenseData> newSense) {
        for (int i = 0; i < oldSense.size(); i++) {
            OWLNamedIndividual sbj = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + oldSense.get(i).getName()));
            updateDirectSenseRelation(sbj, oldSense.get(i).getSenseRels(), newSense.get(i).getSenseRels());
            updateTranslationlRelation(oldSense.get(i).getReifiedTranslationRels(), newSense.get(i).getReifiedTranslationRels());
            updateTerminologicalRelation(oldSense.get(i).getReifiedSenseRels(), newSense.get(i).getReifiedSenseRels());
            updateLexicalFunction(oldSense.get(i).getLexicalFunctions(), newSense.get(i).getLexicalFunctions());
        }
    }

    private void updateDirectSenseRelation(OWLNamedIndividual sbj, ArrayList<SenseRelation> oldSenseRels, ArrayList<SenseRelation> newSenseRels) {
        // for each old sense relation relation, if it is not in new sense relation then remove old sense relation
        for (SenseRelation lr : oldSenseRels) {
            if ((!contains(newSenseRels, lr)) && (!lr.getWrittenRep().isEmpty()) && (!lr.getRelation().isEmpty())) {
                if (lr.getRelation().equals(OntoLexEntity.ObjectProperty.SENSEREL.getLabel())
                        || lr.getRelation().equals(OntoLexEntity.ObjectProperty.TRANSLATION.getLabel())) {
                    removeObjectPropertyAxiom("vartrans", sbj, lr.getRelation(), getIndividual(lr.getWrittenRep()));
                } else {
                    removeObjectPropertyAxiom("lexinfo", sbj, lr.getRelation(), getIndividual(lr.getWrittenRep()));
                }
            }
        }
        // for each new sense relation, if it is not in old sense relation then add new sense relation
        for (SenseRelation lr : newSenseRels) {
            if ((!contains(oldSenseRels, lr)) && (!lr.getWrittenRep().isEmpty()) && (!lr.getRelation().isEmpty())) {
                if (lr.getRelation().equals(OntoLexEntity.ObjectProperty.SENSEREL.getLabel())
                        || lr.getRelation().equals(OntoLexEntity.ObjectProperty.TRANSLATION.getLabel())) {
                    addObjectPropertyAxiom(lr.getRelation(), sbj, getIndividual(lr.getWrittenRep()), pm.getPrefixName2PrefixMap().get("vartrans:"));
                } else {
                    addObjectPropertyAxiom(lr.getRelation(), sbj, getIndividual(lr.getWrittenRep()), pm.getPrefixName2PrefixMap().get("lexinfo:"));
                    addObjectPropertyAxiom(getObjectProperty(lr.getRelation(), "lexinfo"), getObjectProperty(OntoLexEntity.ObjectProperty.SENSEREL.getLabel(), "vartrans"));
                }
            }
        }
    }

    private void updateTranslationlRelation(ArrayList<ReifiedTranslationRelation> oldReifTrans, ArrayList<ReifiedTranslationRelation> newReifTrans) {
        // for each old sense relation relation, if it is not in new sense relation then remove old sense relation
        for (ReifiedTranslationRelation rt : oldReifTrans) {
            if ((!contains(newReifTrans, rt)) && (!rt.getTargetWrittenRep().isEmpty()) && (!rt.getCategory().isEmpty())) {
                removeReifiedTranslationSenseRelation(rt);
            }
        }
        // for each new sense relation, if it is not in old sense relation then add new sense relation
        for (ReifiedTranslationRelation rt : newReifTrans) {
            if ((!contains(oldReifTrans, rt)) && (!rt.getTargetWrittenRep().isEmpty()) && (!rt.getCategory().isEmpty())) {
                addReifiedTranslationSenseRelation(rt);
            }
        }
    }

    private void updateLexicalFunction(ArrayList<SenseData.LexicalFunction> oldLf, ArrayList<SenseData.LexicalFunction> newLf) {
        // for each old lf, if it is not in new lf then remove old lf
        for (SenseData.LexicalFunction lf : oldLf) {
            if ((!contains(newLf, lf)) && (!lf.getTargetWrittenRep().isEmpty()) && (!lf.getLexFunName().isEmpty())) {
                removeLexicalFunction(lf);
            }
        }
        // for each new lf, if it is not in old lf then add new lf
        for (SenseData.LexicalFunction lf : newLf) {
            if ((!contains(oldLf, lf)) && (!lf.getTargetWrittenRep().isEmpty()) && (!lf.getLexFunName().isEmpty())) {
                addLexicalFunction(lf);
            }
        }
    }

    private void updateTerminologicalRelation(ArrayList<ReifiedSenseRelation> oldReifRels, ArrayList<ReifiedSenseRelation> newReifRels) {
        // for each old sense relation relation, if it is not in new sense relation then remove old sense relation
        for (ReifiedSenseRelation rr : oldReifRels) {
            if ((!contains(newReifRels, rr)) && (!rr.getTargetWrittenRep().isEmpty()) && (!rr.getCategory().isEmpty())) {
                removeReifiedSenseRelation(rr);
            }
        }
        // for each new sense relation, if it is not in old sense relation then add new sense relation
        for (ReifiedSenseRelation rr : newReifRels) {
            if ((!contains(oldReifRels, rr)) && (!rr.getTargetWrittenRep().isEmpty()) && (!rr.getCategory().isEmpty())) {
                addReifiedSenseRelation(rr);
            }
        }
    }

    private boolean contains(ArrayList<SenseRelation> alr, SenseRelation lr) {
        for (SenseRelation _lr : alr) {
            if (_lr.getWrittenRep().equals(lr.getWrittenRep()) && (_lr.getRelation().equals(lr.getRelation()))) {
                return true;
            }
        }
        return false;
    }

    private boolean contains(ArrayList<ReifiedSenseRelation> alr, ReifiedSenseRelation lr) {
        for (ReifiedSenseRelation _lr : alr) {
            if (_lr.getSource().equals(lr.getSource()) && (_lr.getCategory().equals(lr.getCategory()))
                    && (_lr.getTarget().equals(lr.getTarget()))) {
                return true;
            }
        }
        return false;
    }

    private boolean contains(ArrayList<ReifiedTranslationRelation> alr, ReifiedTranslationRelation lr) {
        for (ReifiedTranslationRelation _lr : alr) {
            if (_lr.getSource().equals(lr.getSource()) && (_lr.getCategory().equals(lr.getCategory()))
                    && (_lr.getTarget().equals(lr.getTarget()))) {
                return true;
            }
        }
        return false;
    }

    private boolean contains(ArrayList<SenseData.LexicalFunction> allf, SenseData.LexicalFunction lf) {
        for (SenseData.LexicalFunction _lf : allf) {
            if (_lf.getSource().equals(lf.getSource()) && (_lf.getLexFunName().equals(lf.getLexFunName()))
                    && (_lf.getTarget().equals(lf.getTarget()))) {
                return true;
            }
        }
        return false;
    }

    public String createSyntacticFrame(String lemma, LemmaData.SynFrame synFrame) {
        OWLClass SyntacticFrame = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("synsem:"), OntoLexEntity.Class.SYNTACTICFRAME.getLabel());
        OWLNamedIndividual le = getIndividual(lemma.replace("_lemma", "_entry"));
        OWLObjectProperty synBehavior = factory.getOWLObjectProperty(pm.getPrefixName2PrefixMap().get("synsem:"), OntoLexEntity.ObjectProperty.SYNBEHAVIOR.getLabel());
        int frameNumber = EntitySearcher.getObjectPropertyValues(le, synBehavior, ontology).collect(Collectors.toList()).size();
        OWLNamedIndividual sf = getIndividual(lemma.replace("_lemma", "_frame") + ++frameNumber);
        OWLClass sfType = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("lexinfo:"), synFrame.getType());
        addIndividualAxiom(SyntacticFrame, sf);
        addIndividualAxiom(sfType, sf);
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.SYNBEHAVIOR.getLabel(), le, sf, pm.getPrefixName2PrefixMap().get("synsem:"));
        if (!synFrame.getExample().isEmpty()) {
            addDataPropertyAxiom("example", sf, synFrame.getExample(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
        }
        return sf.getIRI().getShortForm();
    }

    public void updateSyntacticFrame(LemmaData.SynFrame oldSynFrame, LemmaData.SynFrame newSynFrame) {
        updateSynFrameType(oldSynFrame, newSynFrame);
        updateSynFrameArgs(oldSynFrame, newSynFrame);
    }

    private void updateSynFrameType(LemmaData.SynFrame oldSynFrame, LemmaData.SynFrame newSynFrame) {
        if (!newSynFrame.getType().isEmpty() && !newSynFrame.getType().equals(oldSynFrame.getType())) {
            OWLClass sfOldType = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("lexinfo:"), oldSynFrame.getType());
            OWLClass sfNewType = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("lexinfo:"), newSynFrame.getType());
            OWLNamedIndividual sf = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), newSynFrame.getName());
            removeIndividualAxiom(sfOldType, sf);
            addIndividualAxiom(sfNewType, sf);
        }
        if (!oldSynFrame.getExample().equals(newSynFrame.getExample())) {
            OWLNamedIndividual sf = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), newSynFrame.getName());
            addDataPropertyAxiom("example", sf, newSynFrame.getExample(), pm.getPrefixName2PrefixMap().get("lexinfo:"));
            removeDataPropertyAxiom("lexinfo", sf, "example", oldSynFrame.getExample());
        }
    }

    private void updateSynFrameArgs(LemmaData.SynFrame oldSynFrame, LemmaData.SynFrame newSynFrame) {
        // assumption: since, it is not possible to remove args from the interface box, newSynFrame contains the number of args in oldSynFrame at least.
        // -- args are sorted by number
        for (LemmaData.SynArg sa : newSynFrame.getSynArgs()) {
            int nArg = sa.getNumber();
            if (oldSynFrame.getSynArgs().size() >= nArg) {
                updateSynArg(newSynFrame.getName(), sa, oldSynFrame.getSynArgs().get(nArg - 1));
            } else {
                createSynArg(newSynFrame.getName(), sa);
            }
        }
    }

    private void updateSynArg(String frameName, LemmaData.SynArg newSA, LemmaData.SynArg oldSA) {
        OWLNamedIndividual synFrame = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), frameName);
        OWLNamedIndividual synArg = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), frameName + "_arg_" + oldSA.getNumber());
        if (!oldSA.getType().equals(newSA.getType())) {
            String oldNS = oldSA.getType().equals(OntoLexEntity.ObjectProperty.SYNARG.getLabel()) ? "synsem" : "lexinfo";
            String newNS = newSA.getType().equals(OntoLexEntity.ObjectProperty.SYNARG.getLabel()) ? pm.getPrefixName2PrefixMap().get("synsem:") : pm.getPrefixName2PrefixMap().get("lexinfo:");
            removeObjectPropertyAxiom(oldNS, synFrame, oldSA.getType(), synArg);
            addObjectPropertyAxiom(newSA.getType(), synFrame, synArg, newNS);
        }
        if (!oldSA.getMarker().equals(newSA.getMarker())) {
            removeDataPropertyAxiom("synsem", synArg, OntoLexEntity.ObjectProperty.MARKER.getLabel(), oldSA.getMarker());
            addDataPropertyAxiom(OntoLexEntity.ObjectProperty.MARKER.getLabel(), synArg, newSA.getMarker(), pm.getPrefixName2PrefixMap().get("synsem:"));
        }
        if (newSA.isOptional() ^ oldSA.isOptional()) {
            removeDataPropertyAxiom("synsem", synArg, OntoLexEntity.DataProperty.OPTIONAL.getLabel(), oldSA.isOptional() ? "true" : "false");
            addDataPropertyAxiom(OntoLexEntity.DataProperty.OPTIONAL.getLabel(), synArg, newSA.isOptional() ? "true" : "false", pm.getPrefixName2PrefixMap().get("synsem:"));
        }
    }

    private void createSynArg(String frameName, LemmaData.SynArg sa) {
        OWLNamedIndividual synFrame = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), frameName);
        OWLNamedIndividual synArg = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), frameName + "_arg_" + sa.getNumber());
        OWLClass synArgClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("synsem:"), OntoLexEntity.Class.SYNTACTICARGUMENT.getLabel());
        String ns = sa.getType().equals(OntoLexEntity.ObjectProperty.SYNARG.getLabel()) ? pm.getPrefixName2PrefixMap().get("synsem:") : pm.getPrefixName2PrefixMap().get("lexinfo:");
        addIndividualAxiom(synArgClass, synArg);
        addObjectPropertyAxiom(sa.getType(), synFrame, synArg, ns);
        if (!sa.getType().equals(OntoLexEntity.ObjectProperty.SYNARG.getLabel())) {
            addObjectPropertyAxiom(factory.getOWLObjectProperty(sa.getType(), ns), factory.getOWLObjectProperty(OntoLexEntity.ObjectProperty.SYNARG.getLabel(), pm.getPrefixName2PrefixMap().get("rdf:")));
        }
        if (!sa.getMarker().isEmpty()) {
            addDataPropertyAxiom(OntoLexEntity.ObjectProperty.MARKER.getLabel(), synArg, sa.getMarker(), pm.getPrefixName2PrefixMap().get("synsem:"));
        }
        if (sa.isOptional()) {
            addDataPropertyAxiom(OntoLexEntity.DataProperty.OPTIONAL.getLabel(), synArg, "true", pm.getPrefixName2PrefixMap().get("synsem:"));
        } else {
            addDataPropertyAxiom(OntoLexEntity.DataProperty.OPTIONAL.getLabel(), synArg, "false", pm.getPrefixName2PrefixMap().get("synsem:"));
        }
    }

    public void updateSenseSynSem(SenseData oldSense, SenseData newSense) {
        if (oldSense.getOntoMap() != null) {
            if (!oldSense.getOntoMap().getFrame().isEmpty()) {
                removeOntoMap(oldSense);
            }
        }
        if (newSense.getOntoMap() != null) {
            if (!newSense.getOntoMap().getFrame().isEmpty()) {
                addOntoMap(newSense);
            }
        }
    }

    private void removeOntoMap(SenseData oldSense) {
        OWLNamedIndividual sense = getIndividual(oldSense.getName());
        OWLClass ontoMapClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("synsem:"), OntoLexEntity.Class.ONTOMAP.getLabel());
        removeIndividualAxiom(ontoMapClass, sense);
        removeObjectPropertyAxiom(pm.getPrefixName2PrefixMap().get("synsem:"), sense, OntoLexEntity.ObjectProperty.ONTOMAPPING.getLabel(), sense);
        if (!oldSense.getOntoMap().getIsA().isEmpty()) {
            removeObjectPropertyAxiom(pm.getPrefixName2PrefixMap().get("synsem:"), sense, OntoLexEntity.ObjectProperty.ISA.getLabel(), getIndividual(oldSense.getOntoMap().getIsA()));
        } else {
            removeObjectPropertyAxiom(pm.getPrefixName2PrefixMap().get("synsem:"), sense, OntoLexEntity.ObjectProperty.SUBJOFPROP.getLabel(), getIndividual(oldSense.getOntoMap().getSubjOfProp()));
            removeObjectPropertyAxiom(pm.getPrefixName2PrefixMap().get("synsem:"), sense, OntoLexEntity.ObjectProperty.OBJOFPROP.getLabel(), getIndividual(oldSense.getOntoMap().getObjOfProp()));
        }
    }

    private void addOntoMap(SenseData newSense) {
        OWLNamedIndividual sense = getIndividual(newSense.getName());
        OWLClass ontoMapClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("synsem:"), OntoLexEntity.Class.ONTOMAP.getLabel());
        addIndividualAxiom(ontoMapClass, sense);
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.ONTOMAPPING.getLabel(), sense, sense, pm.getPrefixName2PrefixMap().get("synsem:"));
        if (!newSense.getOntoMap().getIsA().isEmpty()) {
            addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.ISA.getLabel(), sense,
                    getIndividual(newSense.getOntoMap().getIsA()),
                    pm.getPrefixName2PrefixMap().get("synsem:"));
        } else {
            addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.SUBJOFPROP.getLabel(), sense,
                    getIndividual(newSense.getOntoMap().getSubjOfProp()),
                    pm.getPrefixName2PrefixMap().get("synsem:"));
            addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.OBJOFPROP.getLabel(), sense,
                    getIndividual(newSense.getOntoMap().getObjOfProp()),
                    pm.getPrefixName2PrefixMap().get("synsem:"));
        }
    }

    // NEW LEMMA MULTIWORD ACTION: write all the triples about the new lemma entry
    public void addMultiwordLemma(LemmaData ld, String lex) {
        addLemma(ld, lex);
        createMultiwordDecomposition(ld);
    }

    private void createMultiwordDecomposition(LemmaData ld) {
        OWLNamedIndividual entry = getIndividual(ld.getIndividual().replace("_lemma", "_entry"));
        for (int i = 0; i < ld.getMultiword().size(); i++) {
            String position = Integer.toString(i);
            // write the component individual
            OWLNamedIndividual componentIndividual = getComponent(LexiconUtil.getIRI(ld.getIndividual().replace("_lemma", "_entry"), "comp", position));
            addObjectPropertyAxiom("constituent", entry, componentIndividual, pm.getPrefixName2PrefixMap().get("decomp:"));
            // write its position
            addDataPropertyAxiom("comment", componentIndividual, position, pm.getPrefixName2PrefixMap().get("rdfs:"));
            // write its correspondence if the word exists as lexical entry
            String lexicalEntryOfWord = ld.getMultiword().get(i).getOWLName();
            if (!lexicalEntryOfWord.isEmpty()) {
                OWLNamedIndividual le = getIndividual(lexicalEntryOfWord.replace("_lemma", "_entry"));
                addObjectPropertyAxiom("correspondsTo", componentIndividual, le, pm.getPrefixName2PrefixMap().get("decomp:"));
            }
        }
    }

    // write the comp of the component class and returns it
    private OWLNamedIndividual getComponent(String uri) {
        OWLClass ComponentClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("decomp:"), "Component");
        OWLNamedIndividual c = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), uri);
        addIndividualAxiom(ComponentClass, c);
        return c;
    }

    // UPDATE multiwordLemma with no renaming
    public void updateMultiwordLemma(LemmaData oldLemma, LemmaData newLemma) {
        // it verifies if some component has been changed
        //updateComponentsOfLemma(oldLemma.getMultiword(), newLemma.getMultiword(), oldLemma.getPoS(), newLemma.getPoS());
        deleteMultiwordDecomposition(oldLemma);
        // update the oppurtune lemma fields
        updateLemma(oldLemma, newLemma);
        createMultiwordDecomposition(newLemma);
    }

    // A component can be changed but NOT the written representation of the multiword 
    private void updateComponentsOfLemma(ArrayList<Word> oldComponents, ArrayList<Word> newComponents, String oldPoS, String newPoS) {
        for (int i = 0; i < oldComponents.size(); i++) {
            if (!oldComponents.get(i).getLabel().equals(newComponents.get(i).getLabel())) {
                Word oldComp = oldComponents.get(i);
                Word newComp = newComponents.get(i);
                // write the new component (only the modification of the correspondsTo property is required)
                if (!oldComp.getWrittenRep().contains("not found")) {
                    // the related word is associated with a lexical entry, so deletion it is required
                    removeObjectPropertyAxiom(pm.getPrefixName2PrefixMap().get("decomp:"), getIndividual(oldComp.getOWLComp()), "correspondsTo", getIndividual(oldComp.getOWLName()));
                }
                addObjectPropertyAxiom("correspondsTo", getIndividual(oldComp.getOWLComp()), getIndividual(newComp.getOWLName().replace("_lemma", "_entry")), pm.getPrefixName2PrefixMap().get("decomp:"));
            }
        }
        if (!oldPoS.equals(newPoS)) {
            for (int j = 0; j < newComponents.size(); j++) {
                String oldIndividual = oldComponents.get(j).getOWLComp();
                String newIndividual = oldIndividual.replaceAll((oldPoS.isEmpty() ? Label.UNSPECIFIED_POS : oldPoS), (newPoS.isEmpty() ? Label.UNSPECIFIED_POS : newPoS));
                IRIrenaming(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + oldIndividual),
                        IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + newIndividual));
            }
        }
    }

    // UPDATE multiwordLemma with renaming
    public void updateMultiwordLemmaWithRenaming(LemmaData oldLemma, LemmaData newLemma) {
        // delete the old constituents
        deleteMultiwordDecomposition(oldLemma);
        // update the oppurtune lemma fields
        updateLemmaWithRenaming(oldLemma, newLemma);
        // create the new constituents
        createMultiwordDecomposition(newLemma);
    }

    private void deleteMultiwordDecomposition(LemmaData oldLemma) {
        OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
        for (Word comp : oldLemma.getMultiword()) {
            remover.visit(getIndividual(comp.getOWLComp()));
            manager.applyChanges(remover.getChanges());
        }
    }

    // Save a new form note
    public void saveFormNote(FormData fd, String oldNote) {
        OWLNamedIndividual form = getIndividual(fd.getIndividual());
        if (oldNote.isEmpty()) {
            // it needs to create the instance of the property note
            addDataPropertyAxiom("comment", form, fd.getNote(), pm.getPrefixName2PrefixMap().get("rdfs:"));
        } else {
            // an instance of the property already exists and we have to modify its value
            updateDataPropertyAxiom(form, "comment", oldNote, fd.getNote(), pm.getPrefixName2PrefixMap().get("rdfs:"));
        }
    }

    // NEW FORM ACTION: write all triples about the form
    public void addForm(FormData fd, LemmaData ld) {
        String formInstance = LexiconUtil.getIRI(ld.getFormWrittenRepr(), ld.getPoS(), ld.getLanguage(), fd.getFormWrittenRepr(), "form");
        String entryInstance = LexiconUtil.getIRI(ld.getFormWrittenRepr(), ld.getPoS(), ld.getLanguage(), "entry");
        OWLNamedIndividual le = getIndividual(entryInstance);
        OWLNamedIndividual of = getForm(formInstance);
        fd.setIndividual(formInstance);
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.OTHERFORM.getLabel(), le, of, pm.getPrefixName2PrefixMap().get("ontolex:"));
        setMorphology(of, fd);
    }

    private void setMorphology(OWLNamedIndividual of, FormData fd) {
        addDataPropertyAxiom(OntoLexEntity.DataProperty.WRITTENREP.getLabel(), of, fd.getFormWrittenRepr(), pm.getPrefixName2PrefixMap().get("ontolex:"));

        for (LemmaData.MorphoTrait mt : fd.getMorphoTraits()) {
            addObjectPropertyAxiom("lexinfo", of, mt.getName(), mt.getValue());
        }
    }

    // write all triples about a form with RENAMING
    public void addFormWithRenaming(FormData oldForm, FormData newForm, LemmaData ld) {
        String oldFormInstance = oldForm.getIndividual();
        String newFormInstance = LexiconUtil.getIRI(ld.getFormWrittenRepr(), ld.getPoS(), ld.getLanguage(), newForm.getFormWrittenRepr(), "form");
        updateForm(oldForm, newForm, ld);
        IRIrenaming(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + oldFormInstance), IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + newFormInstance));
    }

    public void updateForm(FormData oldForm, FormData newForm, LemmaData ld) {
        String _subject = oldForm.getIndividual();
        OWLNamedIndividual subject = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + _subject));
        updateFormPhonetic(subject, oldForm.getFormPhoneticRep(), newForm.getFormPhoneticRep());
        updateMorphology(subject, oldForm, newForm);
        updateExtensionAttribute(subject, oldForm.getExtensionAttributeInstances(), newForm.getExtensionAttributeInstances());
    }

    private void updateFormPhonetic(OWLNamedIndividual subject, String oldPhonetic, String newPhonetic) {
        removeDataPropertyAxiom("ontolex", subject, OntoLexEntity.DataProperty.PHONETICREP.getLabel(), oldPhonetic);
        if (!newPhonetic.isEmpty() && !newPhonetic.equals(Label.NO_ENTRY_FOUND)) {
            addDataPropertyAxiom(OntoLexEntity.DataProperty.PHONETICREP.getLabel(), subject, newPhonetic, pm.getPrefixName2PrefixMap().get("ontolex:"));
        }
    }

    private void updateMorphology(OWLNamedIndividual subject, FormData oldForm, FormData newForm) {
        updateDataPropertyAxiom(subject, OntoLexEntity.DataProperty.WRITTENREP.getLabel(), oldForm.getFormWrittenRepr(), newForm.getFormWrittenRepr(), pm.getPrefixName2PrefixMap().get("ontolex:"));

        for (LemmaData.MorphoTrait mt : oldForm.getMorphoTraits()) {
            removeObjectPropertyAxiom("lexinfo", subject, mt.getName(),
                    factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexinfo:") + mt.getValue())));
        }
        for (LemmaData.MorphoTrait mt : newForm.getMorphoTraits()) {
            addObjectPropertyAxiom("lexinfo", subject, mt.getName(), mt.getValue());
        }

    }

    private void updateDataPropertyAxiom(OWLNamedIndividual subject, String dataProperty, String oldValue, String newValue, String ns) {
        OWLDataProperty p = factory.getOWLDataProperty(IRI.create(ns + dataProperty));
        OWLDataPropertyAssertionAxiom oldDataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(p, subject, oldValue);
        ontology.removeAxiom(oldDataPropertyAssertion);
        OWLDataPropertyAssertionAxiom newDataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(p, subject, newValue);
        manager.addAxiom(ontology, newDataPropertyAssertion);
    }

    private void updateDataPropertyAxiom(OWLNamedIndividual subject, String dataProperty, boolean oldValue, boolean newValue, String ns) {
        OWLDataProperty p = factory.getOWLDataProperty(IRI.create(ns + dataProperty));
        OWLDataPropertyAssertionAxiom oldDataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(p, subject, String.valueOf(oldValue));
        ontology.removeAxiom(oldDataPropertyAssertion);
        OWLDataPropertyAssertionAxiom newDataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(p, subject, String.valueOf(newValue));
        manager.addAxiom(ontology, newDataPropertyAssertion);
    }

    private void updateObjectPropertyAxiom(OWLNamedIndividual subject, String objProperty, String oldObj, String newObj, String ns) {
        oldObj = (oldObj.equals(Label.NO_ENTRY_FOUND) || oldObj.isEmpty()) ? Label.NO_ENTRY_FOUND : oldObj;
        newObj = (newObj.equals(Label.NO_ENTRY_FOUND) || newObj.isEmpty()) ? Label.NO_ENTRY_FOUND : newObj;
        if (!oldObj.equals(newObj)) {
            OWLObjectProperty p = factory.getOWLObjectProperty(IRI.create(ns + objProperty));
            if (oldObj.equals(Label.NO_ENTRY_FOUND)) {
                if (!newObj.equals(Label.UNSPECIFIED_POS)) {
                    addAxiom(ns, newObj, p, subject);
                }
            } else {
                if (newObj.equals(Label.NO_ENTRY_FOUND)) {
                    if (!oldObj.equals(Label.UNSPECIFIED_POS)) {
                        removeAxiom(ns, oldObj, p, subject);
                    }
                } else {
                    if (!newObj.equals(Label.UNSPECIFIED_POS)) {
                        addAxiom(ns, newObj, p, subject);
                    }
                    if (!oldObj.equals(Label.UNSPECIFIED_POS)) {
                        removeAxiom(ns, oldObj, p, subject);
                    }
                }
            }
        }
    }

    private void addAxiom(String ns, String obj, OWLObjectProperty p, OWLNamedIndividual subject) {
        OWLNamedIndividual newObject = factory.getOWLNamedIndividual(ns, obj);
        OWLObjectPropertyAssertionAxiom newObjPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(p, subject, newObject);
        manager.addAxiom(ontology, newObjPropertyAssertion);
    }

    private void removeAxiom(String ns, String obj, OWLObjectProperty p, OWLNamedIndividual subject) {
        OWLNamedIndividual oldObject = factory.getOWLNamedIndividual(ns, obj);
        OWLObjectPropertyAssertionAxiom oldObjPropertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(p, subject, oldObject);
        ontology.removeAxiom(oldObjPropertyAssertion);
    }

    private void addObjectPropertyAxiom(String ns, OWLNamedIndividual subject, String predicate, String object) {
        if (!object.equals(Label.NO_ENTRY_FOUND) && !object.isEmpty()) {
            OWLNamedIndividual i = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get(ns + ":"), object);
            addObjectPropertyAxiom(predicate, subject, i, pm.getPrefixName2PrefixMap().get(ns + ":"));
        }
    }

    // remove a specific triple
    private void removeObjectPropertyAxiom(String ns, OWLNamedIndividual subject, String predicate, OWLNamedIndividual object) {
        OWLObjectProperty prop = factory.getOWLObjectProperty(pm.getPrefixName2PrefixMap().get(ns + ":"), predicate);
        OWLObjectPropertyAssertionAxiom propertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(prop, subject, object);
        ontology.removeAxiom(propertyAssertion);
    }

    private void removeDataPropertyAxiom(String ns, OWLNamedIndividual subject, String predicate, String value) {
        OWLDataProperty prop = factory.getOWLDataProperty(pm.getPrefixName2PrefixMap().get(ns + ":"), predicate);
        OWLDataPropertyAssertionAxiom propertyAssertion = factory.getOWLDataPropertyAssertionAxiom(prop, subject, value);
        ontology.removeAxiom(propertyAssertion);
    }

    // Save a new sense note
    public void saveSenseNote(SenseData sd, String oldNote) {
        OWLNamedIndividual sense = getIndividual(sd.getName());
        if (oldNote.isEmpty()) {
            // it needs to create the instance of the property note
            addDataPropertyAxiom("comment", sense, sd.getNote(), pm.getPrefixName2PrefixMap().get("rdfs:"));
        } else {
            // an instance of the property already exists and we have to modify its value
            updateDataPropertyAxiom(sense, "comment", oldNote, sd.getNote(), pm.getPrefixName2PrefixMap().get("rdfs:"));
        }
    }

    // NEW ACTION: write all triples about sense entry
    public void addSense(SenseData sd, LemmaData ld) {
        String entryInstance = ld.getIndividual().replace("_lemma", "_entry");
        String senseInstance = ld.getIndividual().replace("_lemma", "_sense");
        OWLNamedIndividual le = getIndividual(entryInstance);
        OWLObjectProperty sense = factory.getOWLObjectProperty(pm.getPrefixName2PrefixMap().get("ontolex:"), OntoLexEntity.ObjectProperty.SENSE.getLabel());
        int senseNumber = EntitySearcher.getObjectPropertyValues(le, sense, ontology).collect(Collectors.toList()).size();
        OWLNamedIndividual s = getSense(senseInstance, senseNumber + 1);
        addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.SENSE.getLabel(), le, s, pm.getPrefixName2PrefixMap().get("ontolex:"));
        sd.setName(s.getIRI().getShortForm());
    }

    public void deleteLemma(LemmaData ld) {
        String entryInstance = ld.getIndividual().replace("_lemma", "_entry");
        OWLNamedIndividual lemma = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + ld.getIndividual()));
        OWLNamedIndividual lexicalEntry = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + entryInstance));
        if (!ld.getType().equals(OntoLexEntity.Class.WORD.getLabel()) && !ld.getType().equals(OntoLexEntity.Class.AFFIX.getLabel())) {
            deleteMultiwordDecomposition(ld);
        }
        OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
        remover.visit(lemma);
        manager.applyChanges(remover.getChanges());
        remover.visit(lexicalEntry);
        manager.applyChanges(remover.getChanges());

    }

    public void deleteSense(SenseData sd) {
        OWLNamedIndividual i = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + sd.getName()));
        OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
        remover.visit(i);
        manager.applyChanges(remover.getChanges());
    }

    public void deleteOntologyreferences() {
        OWLObjectProperty reference = factory.getOWLObjectProperty(IRI.create(pm.getPrefixName2PrefixMap().get("ontolex:")) + OntoLexEntity.ObjectProperty.REFERENCE.getLabel());
        OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
        for (Object o : Searcher.values(ontology.axioms(AxiomType.OBJECT_PROPERTY_ASSERTION), reference).toArray()) {
            remover.visit((OWLNamedIndividual) o);
            manager.applyChanges(remover.getChanges());
        }
    }

    public void deleteForm(FormData fd) {
        OWLNamedIndividual i = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + fd.getIndividual()));
        OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
        remover.visit(i);
        manager.applyChanges(remover.getChanges());
    }

    // NEW ACTION: write and delete all triples about new sense relations
    public void addSenseRelation(SenseData oldSense, SenseData newSense) {
        OWLNamedIndividual sbj = factory.getOWLNamedIndividual(IRI.create(pm.getPrefixName2PrefixMap().get("lexicon:") + oldSense.getName()));
        if (!newSense.getDefinition().equals(oldSense.getDefinition())) {
            removeDataPropertyAxiom("skos", sbj, "definition", oldSense.getDefinition());
            if (!newSense.getDefinition().isEmpty() && !newSense.getDefinition().equals(Label.NO_ENTRY_FOUND)) {
                addDataPropertyAxiom("definition", sbj, newSense.getDefinition(), pm.getPrefixName2PrefixMap().get("skos:"));
            }
        }
        //saveOntologyReference(sbj, oldSense.getOWLClass(), newSense.getOWLClass());
        saveOntologyReference(sbj, oldSense.getThemeOWLClass(), newSense.getThemeOWLClass());
    }

    private void saveOntologyReference(OWLNamedIndividual sbj, Openable oldR, Openable newR) {
        if (newR.isViewButtonDisabled()) {
            if (oldR.isViewButtonDisabled()) {
                if (!oldR.getName().equals(newR.getName())) {
                    removeObjectPropertyAxiom("ontolex", sbj, OntoLexEntity.ObjectProperty.REFERENCE.getLabel(),
                            factory.getOWLNamedIndividual(LexOliteProperty.getProperty(Label.ONTOLOGY_NAMESPACE_KEY), oldR.getName()));
                    addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.REFERENCE.getLabel(), sbj,
                            factory.getOWLNamedIndividual(LexOliteProperty.getProperty(Label.ONTOLOGY_NAMESPACE_KEY), newR.getName()), pm.getPrefixName2PrefixMap().get("ontolex:"));
                }
            } else {
                addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.REFERENCE.getLabel(), sbj,
                        factory.getOWLNamedIndividual(LexOliteProperty.getProperty(Label.ONTOLOGY_NAMESPACE_KEY), newR.getName()), pm.getPrefixName2PrefixMap().get("ontolex:"));
            }
        } else {
            if (oldR.isViewButtonDisabled()) {
                removeObjectPropertyAxiom("ontolex", sbj, OntoLexEntity.ObjectProperty.REFERENCE.getLabel(),
                        factory.getOWLNamedIndividual(LexOliteProperty.getProperty(Label.ONTOLOGY_NAMESPACE_KEY), oldR.getName()));
            }
        }
    }

    private void saveOntologyReference(OWLNamedIndividual sbj, ReferenceMenuTheme oldR, ReferenceMenuTheme newR) {
        if (!newR.getName().equals(oldR.getName())) {
            // they are different
            if (!newR.getName().isEmpty() && !oldR.getName().isEmpty()) {
                // they are not empty
                removeObjectPropertyAxiom("ontolex", sbj, OntoLexEntity.ObjectProperty.REFERENCE.getLabel(),
                        factory.getOWLNamedIndividual(LexOliteProperty.getProperty(Label.ONTOLOGY_NAMESPACE_KEY), oldR.getName()));
                addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.REFERENCE.getLabel(), sbj,
                        factory.getOWLNamedIndividual(LexOliteProperty.getProperty(Label.ONTOLOGY_NAMESPACE_KEY), newR.getName()), pm.getPrefixName2PrefixMap().get("ontolex:"));
            } else {
                if (newR.getName().isEmpty() && !oldR.getName().isEmpty()) {
                    removeObjectPropertyAxiom("ontolex", sbj, OntoLexEntity.ObjectProperty.REFERENCE.getLabel(),
                            factory.getOWLNamedIndividual(LexOliteProperty.getProperty(Label.ONTOLOGY_NAMESPACE_KEY), oldR.getName()));
                }
                if (!newR.getName().isEmpty() && oldR.getName().isEmpty()) {
                    addObjectPropertyAxiom(OntoLexEntity.ObjectProperty.REFERENCE.getLabel(), sbj,
                            factory.getOWLNamedIndividual(LexOliteProperty.getProperty(Label.ONTOLOGY_NAMESPACE_KEY), newR.getName()), pm.getPrefixName2PrefixMap().get("ontolex:"));
                }
            }
        }
    }

    private void saveRelations(OWLNamedIndividual sbj, ArrayList<Openable> oldR, ArrayList<Openable> newR, String rel, String ns) {
        // for each old sense relation, if it is not in new sense relation then remove old sense relation
        for (Openable o : oldR) {
            if ((!contains(newR, o)) && (!o.getName().isEmpty())) {
                String name = getNormalizedName(o.getName());
                o.setName(name);
                removeSenseRelation(ns, sbj, rel, getIndividual(name));
                // symmetric or inverse relation is removed
                if (rel.equals("synonym") || rel.equals("antonym") || rel.equals("approximateSynonym") || rel.equals("translation")) {
                    removeSenseRelation(ns, getIndividual(name), rel, sbj);
                } else {
                    if (rel.equals("hypernym")) {
                        removeSenseRelation(ns, getIndividual(name), "hyponym", sbj);
                    } else {
                        if (rel.equals("hyponym")) {
                            removeSenseRelation(ns, getIndividual(name), "hypernym", sbj);
                        }
                    }
                }
            }
        }
        // for each new sense relation, if it is not in old sense relation then add new sense relation
        for (Openable o : newR) {
            if ((!contains(oldR, o)) && (!o.getName().isEmpty())) {
                String name = getNormalizedName(o.getName());
                addSenseRelation(ns, sbj, rel, getIndividual(name));
                o.setName(name);
                o.setDeleteButtonDisabled(false);
                o.setViewButtonDisabled(false);
                // symmetric or inverse relation is added
                if (rel.equals("synonym") || rel.equals("antonym") || rel.equals("approximateSynonym") || rel.equals("translation")) {
                    addSenseRelation(ns, getIndividual(name), rel, sbj);
                } else {
                    if (rel.equals("hypernym")) {
                        addSenseRelation(ns, getIndividual(name), "hyponym", sbj);
                    } else {
                        if (rel.equals("hyponym")) {
                            addSenseRelation(ns, getIndividual(name), "hypernym", sbj);
                        }
                    }
                }
            }
        }
    }

    private boolean contains(ArrayList<Openable> alo, Openable o) {
        for (Openable _o : alo) {
            if (o.getName().equals(_o.getName())) {
                return true;
            }
        }
        return false;
    }

    // write a specific triple
    private void addSenseRelation(String ns, OWLNamedIndividual subject, String relType, OWLNamedIndividual object) {
        addObjectPropertyAxiom(relType, subject, object, pm.getPrefixName2PrefixMap().get(ns + ":"));
    }

    private void removeSenseRelation(String ns, OWLNamedIndividual subject, String relType, OWLNamedIndividual object) {
        removeObjectPropertyAxiom(ns, subject, relType, object);
    }

    private String getNormalizedName(String t) {
        if (t.contains("@")) {
            return t.split("@")[0];
        } else {
            return t;
        }
    }

    public StreamedContent export(String format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String fileName = LexOliteProperty.getProperty(Label.LEXICON_FILE_NAME_KEY) == null ? "lexicon" : LexOliteProperty.getProperty(Label.LEXICON_FILE_NAME_KEY);
        try {
            if (format.equals("rdf")) {
                manager.saveOntology(ontology, baos);
                fileName = fileName + ".owl";
            } else {
                if (format.equals("turtle")) {
                    manager.saveOntology(ontology, new TurtleOntologyFormat(), baos);
                    fileName = fileName + ".ttl";
                } else {
                    manager.saveOntology(ontology, new OWLXMLOntologyFormat(), baos);
                    fileName = fileName + ".owl";
                }
            }
        } catch (OWLOntologyStorageException ex) {
            Logger.getLogger(LexiconModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        ByteArrayInputStream in = new ByteArrayInputStream(baos.toByteArray());
        return new DefaultStreamedContent(in, "application/txt", fileName);
    }

    public synchronized void persist() throws IOException, OWLOntologyStorageException {
        System.out.println("[" + getTimestamp() + "] LexO-lite : persist start");
        File f = new File(System.getProperty("user.home") + Label.LEXO_FOLDER
                + LexOliteProperty.getProperty(Label.LEXICON_FILE_NAME_KEY));
        File bkp = new File(System.getProperty("user.home") + Label.LEXO_FOLDER
                + LexOliteProperty.getProperty(Label.LEXICON_FILE_NAME_KEY) + "." + getTimestamp());
        if (!f.renameTo(bkp)) {
            throw new IOException("unable to rename " + bkp.getName());
        }
        try (FileOutputStream fos = new FileOutputStream(System.getProperty("user.home") + Label.LEXO_FOLDER
                + LexOliteProperty.getProperty(Label.LEXICON_FILE_NAME_KEY))) {
            manager.saveOntology(ontology, fos);
            Runtime.getRuntime().exec("gzip " + bkp.getAbsolutePath());
        }
        System.out.println("[" + getTimestamp() + "] LexO-lite : persist end");
    }

    private String getTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void addIndividualAxiom(OWLClass c, OWLNamedIndividual i) {
        OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(c, i);
        manager.addAxiom(ontology, classAssertion);
    }

    private void removeIndividualAxiom(OWLClass c, OWLNamedIndividual i) {
        OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(c, i);
        ontology.remove(classAssertion);
    }

    private void addObjectPropertyAxiom(String objProp, OWLNamedIndividual src, OWLNamedIndividual trg, String ns) {
        OWLObjectProperty p = factory.getOWLObjectProperty(ns, objProp);
        OWLObjectPropertyAssertionAxiom propertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(p, src, trg);
        manager.addAxiom(ontology, propertyAssertion);
    }

    private void addObjectPropertyAxiom(OWLObjectProperty srcObjProp, OWLObjectProperty trgObjProp) {
        OWLSubObjectPropertyOfAxiom propertyAssertion = factory.getOWLSubObjectPropertyOfAxiom(srcObjProp, trgObjProp);
        manager.addAxiom(ontology, propertyAssertion);
    }

    private void addDataPropertyAxiom(String dataProp, OWLNamedIndividual src, String trg, String ns) {
        if (!trg.isEmpty()) {
            OWLDataProperty p = factory.getOWLDataProperty(ns, dataProp);
            OWLDataPropertyAssertionAxiom dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(p, src, trg);
            manager.addAxiom(ontology, dataPropertyAssertion);
        }
    }

    private void addDataPropertyAxiom(String dataProp, OWLNamedIndividual src, double trg, String ns) {
        OWLDataProperty p = factory.getOWLDataProperty(ns, dataProp);
        OWLDataPropertyAssertionAxiom dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(p, src, trg);
        manager.addAxiom(ontology, dataPropertyAssertion);
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public OWLDataFactory getFactory() {
        return factory;
    }

}
