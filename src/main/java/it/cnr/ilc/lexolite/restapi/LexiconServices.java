/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.restapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.cnr.ilc.lexolite.controller.LexiconComparator;
import it.cnr.ilc.lexolite.manager.LemmaData;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.SenseData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author andrea  */
@Path("/")
public class LexiconServices {

    private LexiconManager lexiconManager = LexiconManager.getInstance();

    @GET
    @Path("/lemmas")
    @Produces(MediaType.APPLICATION_JSON)
    // params:
    //      lang (mandatory): language of lemmas
    //      class (optional): domain ontology class thelemma belgons to
    //      limit (optional): results set size
    // invocation: lexicon/lemmas?lang=string&class=string&limit=n
    public Response list(@QueryParam("lang") String lang, @QueryParam("class") String clazz, @QueryParam("limit") int limit) {
        JsonObject entries = new JsonObject();
        JsonObject lemmas = new JsonObject();
        List<Map<String, String>> lemmaList = lexiconManager.lemmasList(lang);
        Collections.sort(lemmaList, new LexiconComparator("writtenRep"));
        for (int i = 0; i <= (limit >= lemmaList.size() ? lemmaList.size() - 1 : limit - 1); i++) {
            Iterator it = lemmaList.get(i).entrySet().iterator();
            JsonObject lemma = new JsonObject();
            String writtenRep = null;
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if (entry.getKey().toString().equals("writtenRep")) {
                    writtenRep = entry.getValue().toString();
                }
                lemma.addProperty(entry.getKey().toString(), entry.getValue().toString());
            }
            lemmas.add(writtenRep, lemma);
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
    @Path("/lemmaByRel")
    @Produces(MediaType.APPLICATION_JSON)
    // params:
    //      entry (mandatory): lemma
    //      lang (mandatory): language of the lemma
    //      rel (mandatory): relation
    // invocation: lexicon/lemmaByRel?entry=acorus&rel=translation
    public Response getLemmaByRel(@QueryParam("entry") String entry, @QueryParam("rel") String rel, @QueryParam("lang") String lang) {
        JsonObject relations = new JsonObject();
        JsonObject jsenses = new JsonObject();
        List<String> list = new ArrayList<String>();
        List<SenseData> senses = lexiconManager.getSensesOfLemma(entry + "_" + lang + "_lemma");
        for (SenseData sense : senses) {
            JsonObject jsense = new JsonObject();
            jsenses.addProperty("sense", sense.getName());
            switch (rel) {
                case "synonym":
                    for (SenseData.Openable sRel : sense.getSynonym()) {

                        System.out.println("uri: " + sRel.getName());
                        list.add(sRel.getName());
                    }
                    jsense.addProperty(rel, new Gson().toJson(list));
                    break;
                case "antonym":
                    for (SenseData.Openable sRel : sense.getAntonym()) {

                        System.out.println("uri: " + sRel.getName());
                        list.add(sRel.getName());
                    }
                    jsense.addProperty(rel, new Gson().toJson(list));
                    break;
                case "hypernym":
                    for (SenseData.Openable sRel : sense.getHypernym()) {

                        System.out.println("uri: " + sRel.getName());
                        list.add(sRel.getName());
                    }
                    jsense.addProperty(rel, new Gson().toJson(list));
                    break;
                case "hyponym":
                    for (SenseData.Openable sRel : sense.getHyponym()) {

                        System.out.println("uri: " + sRel.getName());
                        list.add(sRel.getName());
                    }
                    jsense.addProperty(rel, new Gson().toJson(list));
                    break;
                case "translation":
                    for (SenseData.Openable sRel : sense.getTranslation()) {

                        System.out.println("uri: " + sRel.getName());
                        list.add(sRel.getName());
                    }
                    jsense.addProperty(rel, new Gson().toJson(list));
                    break;
            }
            jsenses.add(rel, jsense);
        }
        relations.add("relations", jsenses);
        return Response.ok()
                .entity(relations.toString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS").build();
    }

    @GET
    @Path("/statistics")
    @Produces(MediaType.APPLICATION_JSON)
    // params:
    //      lang (optional): lemma
    // invocation: lexicon/statistics?lang=aoc
    public Response getStatistics(@QueryParam("lang") String lang) {
        LemmaData lemmas = new LemmaData();
        return Response.ok()
                .entity(lemmas)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS").build();
    }
}
