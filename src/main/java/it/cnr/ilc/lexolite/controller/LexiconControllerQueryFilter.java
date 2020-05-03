/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.OntologyManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerQueryFilter extends BaseController implements Serializable {

    @Inject
    private LexiconControllerTabViewList lexiconCreationControllerTabViewList;
    @Inject
    private LexiconControllerLinkedLexicalEntryDetail lexiconCreationControllerRelationDetail;
     @Inject
    private LexiconManager lexiconManager;
    @Inject
    private OntologyManager ontologyManager;
    @Inject
    private LoginController loginController;

    // for query optimization
    private static Pattern entryLangPattern = Pattern.compile("([a-z]+_entry)");

    private String pos;
    private String gender;
    private String number;
    private String alphabet;

    private String ontoClass;
    private String lemma;
    private String lexicalRelationType;

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getLexicalRelationType() {
        return lexicalRelationType;
    }

    public void setLexicalRelationType(String lexicalRelationType) {
        this.lexicalRelationType = lexicalRelationType;
    }

    public String getOntoClass() {
        return ontoClass;
    }

    public void setOntoClass(String ontoClass) {
        this.ontoClass = ontoClass;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }

    public ArrayList<String> getOntologyClasses() {
        return ontologyManager.getOntologyClasses();
    }

    public ArrayList<String> getLemmas() {
        ArrayList<String> al = new ArrayList();
        for (Map<String, String> m : lexiconManager.lemmasList("fr")) {
            al.add(m.get("writtenRep"));
        }
        Collections.sort(al);
        return al;
    }

    private String getLang(String individual) {
        Matcher matcher = entryLangPattern.matcher(individual);
        if (matcher.find()) {
            return matcher.group(1).split("_entry")[0];
        } else {
            return null;
        }
    }

    private boolean isAskedConcept(Map<String, String> m) {
        String _concept = m.get("ontoClass");
        if (_concept.equals(ontoClass)) {
            return true;
        } else {
            return false;
        }
    }

    public void ontoQueryGroup_1() {
        long startTime = System.currentTimeMillis();
        List<Map<String, String>> l = new ArrayList<Map<String, String>>();
        List<Map<String, String>> f = new ArrayList<Map<String, String>>();
        List<Map<String, String>> s = new ArrayList<Map<String, String>>();
        for (Map<String, String> m : lexiconManager.ontoQueryGroup_1_lemmas(ontoClass)) {
            if (isAskedConcept(m)) {
                Map<String, String> _mL = new HashMap<String, String>();
                Map<String, String> _mS = new HashMap<String, String>();
                String lang = getLang(m.get("le"));
                _mL.put("writtenRep", m.get("writtenRep"));
                _mL.put("individual", m.get("individual"));
                _mL.put("verified", m.get("verified"));
                _mL.put("lang", lang);
                _mL.put("type", m.get("type"));
                l.add(_mL);
                _mS.put("writtenRep", m.get("sense"));
                _mS.put("individual", m.get("sense"));
                _mS.put("lang", lang);
                s.add(_mS);
            }
        }
        for (Map<String, String> m : lexiconManager.ontoQueryGroup_1_forms(ontoClass)) {
            if (isAskedConcept(m)) {
                Map<String, String> _mF = new HashMap<String, String>();
                _mF.put("writtenRep", m.get("writtenRep"));
                _mF.put("individual", m.get("individual"));
                _mF.put("verified", m.get("verified"));
                _mF.put("lang", getLang(m.get("le")));
                _mF.put("type", m.get("type"));
                f.add(_mF);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("durata: " + (endTime - startTime));
        lexiconCreationControllerTabViewList.cnlqFilterLemmaTabView(l);
        lexiconCreationControllerTabViewList.cnlqFilterSenseTabView(s);
        lexiconCreationControllerTabViewList.cnlqFilterFormTabView(f);
    }

    public void resetQueryFilter() {
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        List<Map<String, String>> l = lexiconManager.lemmasList(currentLanguage);
        List<Map<String, String>> f = lexiconManager.formsList(currentLanguage);
        List<Map<String, String>> s = lexiconManager.sensesList(currentLanguage);
        lexiconCreationControllerTabViewList.cnlqFilterLemmaTabView(l);
        lexiconCreationControllerTabViewList.cnlqFilterSenseTabView(s);
        lexiconCreationControllerTabViewList.cnlqFilterFormTabView(f);
    }
}
