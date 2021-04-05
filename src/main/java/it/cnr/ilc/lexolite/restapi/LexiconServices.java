/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.restapi;

import com.google.gson.JsonObject;
import it.cnr.ilc.lexolite.LexOliteProperty;
import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.constant.Namespace;
import it.cnr.ilc.lexolite.constant.OntoLexEntity;
import it.cnr.ilc.lexolite.controller.LexiconComparator;
import it.cnr.ilc.lexolite.manager.FormData;
import it.cnr.ilc.lexolite.manager.LemmaData;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import it.cnr.ilc.lexolite.manager.SenseData;
import it.cnr.ilc.lexolite.manager.SenseData.SenseRelation;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFJsonLDDocumentFormat;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 *
 * @author andrea
 */
@Path("/")
public class LexiconServices {

    private LexiconManager lexiconManager = LexiconManager.getInstance();

    public static Pattern pattern = Pattern.compile("([a-z]+_lemma)");

    @GET
    @Path("/languages")
    @Produces(MediaType.APPLICATION_JSON)
    // params: none
    // invocation: lexicon/languages
    // return: the list of the lexicon languages
    public Response getLanguages() {
        JsonObject languages = new JsonObject();
        for (String lang : lexiconManager.lexicaLanguagesList()) {
            JsonObject language = new JsonObject();
            language.addProperty("lang", lang);
            language.addProperty("entries", lexiconManager.lemmasList(lang).size());
            languages.add(lang, language);
        }
        return Response.ok()
                .entity(languages.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @GET
    @Path("/lemmas")
    @Produces(MediaType.APPLICATION_JSON)
    // params:
    //      lang (mandatory): language of lemmas
    //      startswith (optional): the chars the lemma starts with
    //      class (optional): domain ontology class the lemma belgons to
    //      limit (optional): results set size
    // invocation: lexicon/lemmas?lang=string&class=string&limit=n
    public Response list(@QueryParam("lang") String lang, @QueryParam("startswith") String sw, @QueryParam("class") String clazz, @QueryParam("limit") int limit) {
        JsonObject lemmas = new JsonObject();
        List<Map<String, String>> lemmaList = lexiconManager.lemmasList(lang);
        Collections.sort(lemmaList, new LexiconComparator("writtenRep"));
        int remaining = limit == 0 ? lemmaList.size() : limit;
        for (Map<String, String> m : lemmaList) {
            if (sw != null) {
                if (m.get("writtenRep").startsWith(sw)) {
                    if (remaining > 0) {
                        lemmas.add(m.get("writtenRep"), setLemma(m));
                        remaining--;
                    } else {
                        break;
                    }
                }
            } else {
                if (remaining > 0) {
                    lemmas.add(m.get("writtenRep"), setLemma(m));
                    remaining--;
                } else {
                    break;
                }
            }
        }
        return Response.ok()
                .entity(lemmas.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    private JsonObject setLemma(Map<String, String> m) {
        JsonObject lemma = new JsonObject();
        Iterator it = m.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            lemma.addProperty(entry.getKey().toString().replace("individual", "id"), entry.getValue().toString());
        }
        return lemma;
    }

    @GET
    @Path("/lemma")
    @Produces(MediaType.APPLICATION_JSON)
    // params: none
    // invocation: lexicon/id=lemma-id
    public Response getLemma(@QueryParam("id") String id) {
        LemmaData ld = lexiconManager.getLemmaAttributes(id);
        ArrayList<FormData> fds;
        ArrayList<SenseData> sds;
        Matcher matcher = pattern.matcher(id);
        if (matcher.find()) {
            fds = lexiconManager.getFormsOfLemma(id, matcher.group(1).split("_lemma")[0], null);
            sds = lexiconManager.getSensesOfLemma(id, null, null, null);
        } else {
            fds = null;
            sds = null;
        }
        return Response.ok()
                .entity(createEntry(ld, fds, sds))
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    private String createEntry(LemmaData ld, ArrayList<FormData> fds, ArrayList<SenseData> sds) {
        OWLOntologyManager manager;
        OWLOntology ontology;
        OWLDataFactory factory;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            manager = OWLManager.createOWLOntologyManager();
            ontology = manager.createOntology();
            factory = manager.getOWLDataFactory();
            PrefixManager pm = getPrefixs();
            OWLClass lexicalEntryClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("ontolex:"), OntoLexEntity.Class.LEXICALENTRY.getLabel());
            OWLClass lexicalFormClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("ontolex:"), OntoLexEntity.Class.FORM.getLabel());
            OWLClass lexicalSenseClass = factory.getOWLClass(pm.getPrefixName2PrefixMap().get("ontolex:"), OntoLexEntity.Class.LEXICALSENSE.getLabel());
            OWLNamedIndividual lexicalEntry = factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get("lexicon:"), ld.getIndividual().replace("_lemma", "_entry"));
            addIndividualAxiom(manager, ontology, factory, lexicalEntryClass, lexicalEntry);
            createLemma(manager, ontology, factory, pm, ld, lexicalFormClass);
            createForms(manager, ontology, factory, pm, fds, ld, lexicalFormClass);
            createSenses(manager, ontology, factory, pm, sds, ld, lexicalSenseClass);
            OWLDocumentFormat ontologyFormat = new RDFJsonLDDocumentFormat();
            manager.saveOntology(ontology, ontologyFormat, baos);
        } catch (OWLOntologyCreationException | OWLOntologyStorageException ex) {
            Logger.getLogger(LexiconServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return baos.toString();
    }

    private PrefixManager getPrefixs() {
        PrefixManager pm = new DefaultPrefixManager();
        pm.setPrefix("lexicon", LexOliteProperty.getProperty(Label.LEXICON_NAMESPACE_KEY));
        pm.setPrefix("lexinfo", Namespace.LEXINFO);
        pm.setPrefix("rdfs", Namespace.RDFS);
        pm.setPrefix("skos", Namespace.SKOS);
        pm.setPrefix("ontolex", Namespace.ONTOLEX);
        pm.setPrefix("lime", Namespace.LIME);
        pm.setPrefix("dct", Namespace.DCT);
        pm.setPrefix("decomp", Namespace.DECOMP);
        pm.setPrefix("rdf", Namespace.RDF);
        return pm;
    }

    private void createLemma(OWLOntologyManager manager, OWLOntology ontology, OWLDataFactory factory, PrefixManager pm, LemmaData ld, OWLClass lexicalFormClass) {
        addIndividualAxiom(manager, ontology, factory, lexicalFormClass, getIndividual(factory, pm, "lexicon", ld.getIndividual()));
        addObjectPropertyAxiom(manager, ontology, factory, pm, OntoLexEntity.ObjectProperty.CANONICALFORM.getLabel(),
                getIndividual(factory, pm, "lexicon", ld.getIndividual().replace("_lemma", "_entry")),
                getIndividual(factory, pm, "lexicon", ld.getIndividual()),
                "ontolex");
    }

    private void createForms(OWLOntologyManager manager, OWLOntology ontology, OWLDataFactory factory, PrefixManager pm, ArrayList<FormData> fds, LemmaData ld, OWLClass lexicalFormClass) {
        for (FormData fd : fds) {
            addIndividualAxiom(manager, ontology, factory, lexicalFormClass, getIndividual(factory, pm, "lexicon", fd.getIndividual()));
            addObjectPropertyAxiom(manager, ontology, factory, pm, OntoLexEntity.ObjectProperty.OTHERFORM.getLabel(),
                    getIndividual(factory, pm, "lexicon", ld.getIndividual().replace("_lemma", "_entry")),
                    getIndividual(factory, pm, "lexicon", fd.getIndividual()),
                    "ontolex");
        }
    }

    private void createSenses(OWLOntologyManager manager, OWLOntology ontology, OWLDataFactory factory, PrefixManager pm, ArrayList<SenseData> sds, LemmaData ld, OWLClass lexicalSenseClass) {
        for (SenseData sd : sds) {
            addIndividualAxiom(manager, ontology, factory, lexicalSenseClass, getIndividual(factory, pm, "lexicon", sd.getName()));
            addObjectPropertyAxiom(manager, ontology, factory, pm, OntoLexEntity.ObjectProperty.SENSE.getLabel(),
                    getIndividual(factory, pm, "lexicon", ld.getIndividual().replace("_lemma", "_entry")),
                    getIndividual(factory, pm, "lexicon", sd.getName()),
                    "ontolex");
        }
    }

    private OWLNamedIndividual getIndividual(OWLDataFactory factory, PrefixManager pm, String ns, String ind) {
        return factory.getOWLNamedIndividual(pm.getPrefixName2PrefixMap().get(ns + ":"), ind);
    }

    private void addIndividualAxiom(OWLOntologyManager manager, OWLOntology ontology, OWLDataFactory factory, OWLClass c, OWLNamedIndividual i) {
        OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(c, i);
        manager.addAxiom(ontology, classAssertion);
    }

    private void addObjectPropertyAxiom(OWLOntologyManager manager, OWLOntology ontology, OWLDataFactory factory, PrefixManager pm, String objProp, OWLNamedIndividual src, OWLNamedIndividual trg, String ns) {
        OWLObjectProperty p = factory.getOWLObjectProperty(pm.getPrefixName2PrefixMap().get(ns + ":"), objProp);
        OWLObjectPropertyAssertionAxiom propertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(p, src, trg);
        manager.addAxiom(ontology, propertyAssertion);
    }

    private void addDataPropertyAxiom(OWLOntologyManager manager, OWLOntology ontology, OWLDataFactory factory, String dataProp, OWLNamedIndividual src, String trg, String ns) {
        OWLDataProperty p = factory.getOWLDataProperty(ns, dataProp);
        OWLDataPropertyAssertionAxiom dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(p, src, trg);
        manager.addAxiom(ontology, dataPropertyAssertion);

    }

    @GET
    @Path("/lemmaByRel")
    @Produces(MediaType.APPLICATION_JSON)
    // params:
    //      entry (mandatory): lemma
    //      lang (mandatory): language of the lemma
    //      rel (mandatory): relation
    // invocation: lexicon/lemmaByRel?entry=acorus&rel=translation
    public Response getLemmaByRel(@QueryParam("entry") String entry, @QueryParam("rel") String rel, @QueryParam("lang") String lang) {
        JsonObject senseNumber = new JsonObject();
        List<SenseData> senses = lexiconManager.getSensesOfLemma(entry + "_" + lang + "_lemma", null, null, null);
        for (SenseData sense : senses) {
            JsonObject lemmas = new JsonObject();
            for (SenseRelation sr : sense.getSenseRels()) {
                if (sr.getRelation().equals(rel)) {
                    LemmaData ld = lexiconManager.getLemmaOfSense(sr.getWrittenRep());
                    lemmas.add(ld.getFormWrittenRepr(), setLemma(ld));
                }
            }
            if (lemmas.size() > 0) {
                senseNumber.add(sense.getName(), lemmas);
            }
        }
        return Response.ok()
                .entity(senseNumber.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS").build();
    }

    private JsonObject setLemma(LemmaData ld) {
        JsonObject lemma = new JsonObject();
        lemma.addProperty("id", ld.getIndividual());
        lemma.addProperty("writtenRep", ld.getFormWrittenRepr());
        lemma.addProperty("type", ld.getType());
        return lemma;
    }

    @GET
    @Path("/statistics")
    @Produces(MediaType.APPLICATION_JSON)
    // params:
    //      lang 
    // invocation: lexicon/statistics?lang=aoc
    public Response getStatistics(@QueryParam("lang") String lang) {
        List<Map<String, String>> lemmaList = lexiconManager.lemmasList(lang);
        int entryNumber = lemmaList.size();
        JsonObject languages = new JsonObject();
        languages.addProperty("lang", lang);
        JsonObject entries = new JsonObject();
        int noun = 0, adjective = 0, verb = 0, nounPhrase = 0, adjectivePhrase = 0, verbPhrase = 0;
        int lexicalization = 0;
        for (Map<String, String> m : lemmaList) {
            String _pos = getPosFromIndividual(m.get("individual"), m.get("lang"));
            m.put("pos", _pos.isEmpty() ? Label.UNSPECIFIED_POS : _pos);
            if (m.get("type").equals(OntoLexEntity.Class.WORD.getLabel()) || m.get("type").equals(OntoLexEntity.Class.AFFIX.getLabel())) {
                if (m.get("pos").equals("noun")) {
                    noun++;
                } else {
                    if (m.get("pos").equals("verb")) {
                        verb++;
                    } else {
                        if (m.get("pos").equals("adjective")) {
                            adjective++;
                        }
                    }
                }
            } else {
                if (m.get("type").equals(OntoLexEntity.Class.MULTIWORD.getLabel())) {
                    if (m.get("pos").equals("noun")) {
                        nounPhrase++;
                    } else {
                        if (m.get("pos").equals("verb")) {
                            verbPhrase++;
                        } else {
                            if (m.get("pos").equals("adjective")) {
                                adjectivePhrase++;
                            }
                        }
                    }
                }
            }
            ArrayList<String> al = (lexiconManager.lexicalizazions(m.get("individual").replaceAll("_lemma", "_entry")));
            if (!al.get(0).equals(Label.NO_ENTRY_FOUND)) {
                lexicalization = lexicalization + al.size();
            }
        }
        entries.addProperty("total", entryNumber);
        JsonObject words = new JsonObject();
        words.addProperty("total", noun + verb + adjective);
        words.addProperty("noun", noun);
        words.addProperty("adjective", adjective);
        words.addProperty("verb", verb);
        JsonObject multiwords = new JsonObject();
        multiwords.addProperty("total", nounPhrase + verbPhrase + adjectivePhrase);
        multiwords.addProperty("nounPhrase", nounPhrase);
        multiwords.addProperty("adjectivePhrase", adjectivePhrase);
        multiwords.addProperty("verbPhrase", verbPhrase);
        entries.add("word", words);
        entries.add("multiword", multiwords);
        languages.add("entries", entries);
        languages.addProperty("lexicalizations", lexicalization);
        return Response.ok()
                .entity(languages.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS").build();
    }

    public String getPosFromIndividual(String individual, String lang) {
        String[] ret = individual.split("_" + lang + "_")[0].split("_");
        if (!ret[ret.length - 1].equals(Label.UNSPECIFIED_POS)) {
            return ret[ret.length - 1];
        } else {
            return "";
        }
    }

}
