/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.restapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.constant.Namespace;
import it.cnr.ilc.lexolite.constant.OntoLexEntity;
import it.cnr.ilc.lexolite.controller.LexiconComparator;
import it.cnr.ilc.lexolite.manager.FormData;
import it.cnr.ilc.lexolite.manager.LemmaData;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import static it.cnr.ilc.lexolite.manager.LexiconQuery.pattern;
import it.cnr.ilc.lexolite.manager.SenseData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

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
    //      class (optional): domain ontology class thelemma belgons to
    //      limit (optional): results set size
    // invocation: lexicon/lemmas?lang=string&class=string&limit=n
    public Response list(@QueryParam("lang") String lang, @QueryParam("startswith") String sw, @QueryParam("class") String clazz, @QueryParam("limit") int limit) {
        JsonObject entries = new JsonObject();
        JsonObject lemmas = new JsonObject();
        List<Map<String, String>> lemmaList = lexiconManager.lemmasList(lang);
        Collections.sort(lemmaList, new LexiconComparator("writtenRep"));
        if (limit == 0) {
            limit = lemmaList.size();
        }
        for (int i = 0; i <= (limit >= lemmaList.size() ? lemmaList.size() - 1 : limit - 1); i++) {
            Iterator it = lemmaList.get(i).entrySet().iterator();
            JsonObject lemma = new JsonObject();
            String writtenRep = "";
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if (entry.getKey().toString().equals("writtenRep")) {
                    writtenRep = entry.getValue().toString();
                }
                lemma.addProperty(entry.getKey().toString().replace("individual", "id"), entry.getValue().toString());
            }
            if (sw != null) {
                if (writtenRep.startsWith(sw)) {
                    lemmas.add(writtenRep, lemma);
                }
            } else {
                lemmas.add(writtenRep, lemma);
            }
        }
        entries.add("entries", lemmas);
        return Response.ok()
                .entity(entries.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @GET
    @Path("/lemma")
    @Produces(MediaType.APPLICATION_JSON)
    // params: none
    // invocation: lexicon/languages
    public Response getEntryById(@QueryParam("id") String id) {
        JsonObject entry = new JsonObject();
        LemmaData ld = lexiconManager.getLemmaAttributes(id);
        ArrayList<FormData> fds;
        ArrayList<SenseData> sds;
        Matcher matcher = pattern.matcher(id);
        if (matcher.find()) {
            fds = lexiconManager.getFormsOfLemma(id, matcher.group(1).split("_lemma")[0]);
            sds = lexiconManager.getSensesOfLemma(id);
        } else {
            fds = null;
            sds = null;
        }
        createEntry(ld, fds, sds);
        return Response.ok()
                .entity(entry.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    private void createEntry(LemmaData ld, ArrayList<FormData> fds, ArrayList<SenseData> sds) {
        OWLOntologyManager manager;
        OWLOntology ontology;
        OWLDataFactory factory;
        try {
            manager = OWLManager.createOWLOntologyManager();
            ontology = manager.createOntology();
            factory = manager.getOWLDataFactory();
            OWLObjectProperty p = factory.getOWLObjectProperty("ns", "puppa");
            OWLObjectPropertyAssertionAxiom propertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(p, factory.getOWLNamedIndividual("sss"), factory.getOWLNamedIndividual("ttt"));
            manager.addAxiom(ontology, propertyAssertion);
            manager.saveOntology(ontology, new TurtleOntologyFormat());
            ontology.axioms().forEach(System.out::println);
        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(LexiconServices.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OWLOntologyStorageException ex) {
            Logger.getLogger(LexiconServices.class.getName()).log(Level.SEVERE, null, ex);
        }

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
        List<SenseData> senses = lexiconManager.getSensesOfLemma(entry + "_" + lang + "_lemma");
        for (SenseData sense : senses) {
            JsonObject lemmas = new JsonObject();
            switch (rel) {
                case "synonym":
                    for (SenseData.Openable sRel : sense.getSynonym()) {
                        LemmaData ld = lexiconManager.getLemmaOfSense(sRel.getName());
                        lemmas.add(ld.getFormWrittenRepr(), getJSon(ld));
                    }
                    break;
                case "antonym":
                    for (SenseData.Openable sRel : sense.getAntonym()) {
                        LemmaData ld = lexiconManager.getLemmaOfSense(sRel.getName());
                        lemmas.add(ld.getFormWrittenRepr(), getJSon(ld));
                    }
                    break;
                case "hypernym":
                    for (SenseData.Openable sRel : sense.getHypernym()) {
                        LemmaData ld = lexiconManager.getLemmaOfSense(sRel.getName());
                        lemmas.add(ld.getFormWrittenRepr(), getJSon(ld));
                    }
                    break;
                case "hyponym":
                    for (SenseData.Openable sRel : sense.getHyponym()) {
                        LemmaData ld = lexiconManager.getLemmaOfSense(sRel.getName());
                        lemmas.add(ld.getFormWrittenRepr(), getJSon(ld));
                    }
                    break;
                case "translation":
                    for (SenseData.Openable sRel : sense.getTranslation()) {
                        LemmaData ld = lexiconManager.getLemmaOfSense(sRel.getName());
                        lemmas.add(ld.getFormWrittenRepr(), getJSon(ld));
                    }
                    break;
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
            if (m.get("type").equals(OntoLexEntity.Class.WORD.getLabel())) {
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
        languages.addProperty("lexicalization", lexicalization);
        return Response.ok()
                .entity(languages.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS").build();
    }

    private JsonObject getJSon(LemmaData l) {
        JsonObject lemma = new JsonObject();
        lemma.addProperty("writtenRep", l.getFormWrittenRepr());
        lemma.addProperty("lang", l.getLanguage());
        lemma.addProperty("type", l.getType());
        lemma.addProperty("verified", l.getValid());
        lemma.addProperty("uri", Namespace.LEXICON + l.getIndividual());
        lemma.addProperty("partOfSpeech", l.getPoS());
        return lemma;
    }

}
