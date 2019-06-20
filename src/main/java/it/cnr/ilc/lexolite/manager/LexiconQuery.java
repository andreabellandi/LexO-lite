/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import de.derivo.sparqldlapi.Query;
import de.derivo.sparqldlapi.QueryArgument;
import de.derivo.sparqldlapi.QueryBinding;
import de.derivo.sparqldlapi.QueryEngine;
import de.derivo.sparqldlapi.QueryResult;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;
import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.constant.LexicalQuery;
import it.cnr.ilc.lexolite.constant.Namespace;
import it.cnr.ilc.lexolite.constant.OntoLexEntity;
import it.cnr.ilc.lexolite.controller.BaseController;
import it.cnr.ilc.lexolite.controller.LexiconComparator;
import it.cnr.ilc.lexolite.manager.LemmaData.Word;
import it.cnr.ilc.lexolite.manager.SenseData.Openable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author andrea
 */
public class LexiconQuery extends BaseController {

    private static final Logger LOG = LogManager.getLogger(LexiconQuery.class);

    private OWLOntologyManager ontologyManager;
    private OWLOntology ontology;
    private OWLDataFactory owlDataFactory;

    private static int FIELD_MAX_LENGHT = 50;

    StructuralReasonerFactory reasonerFactory = null;
    OWLReasoner reasoner = null;

    // for query optimization
    public static Pattern pattern = Pattern.compile("([a-z]+_lemma)");

    public LexiconQuery() {
    }

    public LexiconQuery(LexiconModel lm) {
        ontologyManager = lm.getManager();
        ontology = lm.getOntology();
        owlDataFactory = lm.getFactory();
        reasonerFactory = new StructuralReasonerFactory();
//        reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
        reasoner = reasonerFactory.createReasoner(ontology);

    }

    public OWLOntologyManager getManager() {
        return ontologyManager;
    }

    public void setManager(OWLOntologyManager manager) {
        this.ontologyManager = manager;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public void setOntology(OWLOntology ontology) {
        this.ontology = ontology;
    }

    public OWLDataFactory getFactory() {
        return owlDataFactory;
    }

    public void setFactory(OWLDataFactory factory) {
        this.owlDataFactory = factory;
    }

    private ArrayList<String> getList(List<Map<String, String>> l) {
        ArrayList<String> al = new ArrayList();
        for (Map<String, String> m : l) {
            for (Map.Entry<String, String> entry : m.entrySet()) {
                al.add(entry.getValue());
            }
        }
        if (al.isEmpty()) {
            al.add(Label.NO_ENTRY_FOUND);
        }
        return al;
    }

    public List<SelectItem> getPoS() {
        List<SelectItem> groupedPosList = new ArrayList<>();
        ArrayList<String> posClasses = getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.PoS_CLASS));
        posClasses.sort(String::compareToIgnoreCase);
        for (String posClass : posClasses) {
            groupedPosList.add(getGroupedPoS(posClass));
        }
        return groupedPosList;
    }

    private SelectItemGroup getGroupedPoS(String posClass) {
        SelectItemGroup g = new SelectItemGroup(posClass);
        ArrayList<String> PoS = getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.PoS.replace("POS_CLASS", posClass)));
        PoS.sort(String::compareToIgnoreCase);
        SelectItem[] selPoS = new SelectItem[PoS.size()];
        for (int i = 0; i < PoS.size(); i++) {
            selPoS[i] = new SelectItem(PoS.get(i), PoS.get(i));
        }
        g.setSelectItems(selPoS);
        return g;
    }

    public ArrayList<String> getGenders() {
        return getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.GENDER));
    }

    public ArrayList<String> getNumbers() {
        return getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.NUMBER));
    }

    public List<Map<String, String>> getLemmas(String lang) {
        if (lang.equals("All languages")) {
            return (processQuery(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_BASIC));
        } else {
            return processQuery(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_BASIC_BY_LANGUAGE.replace("_LANG_", lang));
        }
    }

    public List<Map<String, String>> getForms(String lang) {
        if (lang.equals("All languages")) {
            return (processQuery(LexicalQuery.PREFIXES + LexicalQuery.FORM_BASIC));
        } else {
            return processQuery(LexicalQuery.PREFIXES + LexicalQuery.FORM_BASIC_BY_LANGUAGE.replace("_LANG_", lang));
        }
    }

    public List<Map<String, String>> getSenses(String lang) {
        if (lang.equals("All languages")) {
            return (processQuery(LexicalQuery.PREFIXES + LexicalQuery.SENSE_BASIC));
        } else {
            return processQuery(LexicalQuery.PREFIXES + LexicalQuery.SENSE_BASIC_BY_LANGUAGE.replace("_LANG_", lang));
        }
    }

    public String getLexicon(String lang) {
        List<Map<String, String>> lexicon = processQuery(LexicalQuery.PREFIXES + LexicalQuery.LEXICON.replace("_LANG_", lang));
        if (!lexicon.isEmpty()) {
            return lexicon.get(0).get("lexicon");
        } else {
            return Label.NO_ENTRY_FOUND;
        }
    }

    public List<Map<String, String>> advancedFilter_lemmas() {
        return processQuery(LexicalQuery.PREFIXES + LexicalQuery.ADVANCED_FILTER_LEMMA);
    }

    public List<SelectItem> getSensesByLanguage(String lang) {
        List<SelectItem> groupedSenseList = new ArrayList<>();
        ArrayList<String> senses = getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.SENSES_BY_LANGUAGE.replace("_LANG_", lang)));
        senses.sort(String::compareToIgnoreCase);
        SelectItemGroup g = new SelectItemGroup(lang);
        SelectItem[] selSenses = new SelectItem[senses.size()];
        for (int i = 0; i < senses.size(); i++) {
            selSenses[i] = new SelectItem(senses.get(i), senses.get(i));
        }
        g.setSelectItems(selSenses);
        groupedSenseList.add(g);
        return groupedSenseList;
    }

    public ArrayList<String> getLexicaLanguages() {
        return getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.LANGUAGES));
    }

    public ArrayList<String> getLexicalizations(String entry) {
        return getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.LEXICALIZATIONS.replaceAll("_ENTRY_", entry)));
    }

    public ArrayList<String> getLanguageDescription(String lang) {
        return getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.LANGUAGE_DESCRIPTION.replaceAll("_LANG_", lang)));
    }

    public ArrayList<String> getLanguageCreator(String lang) {
        return getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.LANGUAGE_CREATOR.replaceAll("_LANG_", lang)));
    }

    // invoked in order to get lemma attributes of a specific lemma
    public LemmaData getLemmaAttributes(String lemma) {
        LemmaData ld = new LemmaData();
        setLemmaData(lemma, ld);
        return ld;
    }

    // invoked in order to get a lemma attributes of a specific sense
    public LemmaData getLemmaOfSense(String sense) {
        LemmaData ld = new LemmaData();
        ArrayList<String> results = getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_INSTANCE_OF_SENSE.replace("_SENSE_", sense)));
        String lemma = results.get(0);
        setLemmaData(lemma, ld);
        return ld;
    }

    // invoked in order to get a lemma attributes of a specific form
    public LemmaData getLemmaEntry(String form) {
        LemmaData ld = new LemmaData();
        ArrayList<String> results = getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_INSTANCE_OF_FORM.replace("_FORM_", form)));
        String lemma = results.get(0);
        setLemmaData(lemma, ld);
        return ld;
    }

    private void setLemmaData(String lemma, LemmaData ld) {
        ld.setIndividual(lemma);
        // It is the type of the entry of the lemma
        ld.setType(getLemmaType(lemma));
        ld.setFormWrittenRepr(getLemmaWrittenRep(lemma));
        Matcher matcher = pattern.matcher(lemma);
        if (matcher.find()) {
            ld.setLanguage(matcher.group(1).split("_lemma")[0]);
        } else {
            ld.setLanguage("");
        }
        String valid = getEntryVerified(lemma);
        if (getEntryVerified(lemma).equals("false")) {
            ld.setVerified(false);
        } else {
            ld.setVerified(true);
        }
        ld.setValid(valid);
        ld.setPoS(ld.getType().equals(OntoLexEntity.Class.WORD.getLabel()) ? getLemmaPoS(lemma) : getLemmaPoS(lemma) + "Phrase");
        ld.setGender(getLemmaGender(lemma));
        ld.setPerson(getLemmaPerson(lemma));
        ld.setMood(getLemmaMood(lemma));
        ld.setVoice(getLemmaVoice(lemma));
        ld.setNumber(getLemmaNumber(lemma));
        ld.setNote(getLemmaNote(lemma));
        ld.setSeeAlso(getLemmaReference(lemma));
        if (!ld.getType().equals(OntoLexEntity.Class.WORD.getLabel())) {
            ld.setMultiword(getLemmaMultiword(lemma, ld.getFormWrittenRepr()));
        }
    }

    private String getLemmaNote(String lemma) {
        String note = getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_NOTE, "_LEMMA_", lemma);
        return note.equals(Label.NO_ENTRY_FOUND) ? "" : note;
    }

    private ArrayList<Word> getLemmaReference(String lemma) {
        return getEntryAttributeWordList(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_SEEALSO, "_LEMMA_", lemma);
    }

    private String getLemmaType(String lemma) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_TYPE, "_LEMMA_", lemma);
    }

    public String getComponentAtPosition(String entry, String position) {
        String constituentAtPositionQuery = LexicalQuery.PREFIXES + LexicalQuery.CONSTITUENT_AT_POSITION.replace("_ENTRY_", entry).replace("_POSITION_", position);
        List<Map<String, String>> comp = processQuery(constituentAtPositionQuery);
        return comp.get(0).get("constituent");
    }

    private ArrayList<Word> getLemmaMultiword(String lemma, String wr) {
        ArrayList<Word> alw = new ArrayList();
        String entry = lemma.replace("_lemma", "_entry");
        String constituentsQuery = LexicalQuery.PREFIXES + LexicalQuery.CONSTITUENTS.replace("_ENTRY_", entry);
        List<Map<String, String>> constituents = processQuery(constituentsQuery);
        Collections.sort(constituents, new LexiconComparator("position"));
        for (Map<String, String> m : constituents) {
            String wordQuery = LexicalQuery.PREFIXES + LexicalQuery.WORDS_OF_MULTIWORD.replace("_CONST_", m.get("constituent"));
            List<Map<String, String>> word = processQuery(wordQuery);
            alw.add(getWord(word, m.get("constituent"), m.get("position"), wr));
        }
        return alw;
    }

    private Word getWord(List<Map<String, String>> word, String OWLComponentIndividual, String position, String wr) {
        Word _w = new Word();
        if (word.isEmpty()) {
            // the word does not exist as lexical entry
            _w.setViewButtonDisabled(true);
            _w.setWrittenRep(wr.split(" ")[Integer.parseInt(position)] + " not found");
            _w.setOWLComp(OWLComponentIndividual);
            return _w;
        } else {
            // the word exists as lexical entry
            for (Map<String, String> w : word) {
                _w.setWrittenRep(w.get("writtenRep"));
                _w.setOWLName(w.get("individual"));
                _w.setLanguage(w.get("lang"));
                _w.setOWLComp(OWLComponentIndividual);
                _w.setLabel(_w.getWrittenRep() + "@" + _w.getLanguage());
            }
        }
        return _w;
    }

    private ArrayList<Word> getEntryAttributeWordList(String q, String t, String e) {
        ArrayList<Word> alw = new ArrayList();
        List<Map<String, String>> wl = processQuery(q.replace(t, e));
        for (Map<String, String> m : wl) {
            alw.add(getWord(m));
        }
        return alw;
    }

    private Word getWord(Map<String, String> m) {
        Word w = new Word();
        w.setWrittenRep(m.get("writtenRep"));
        w.setOWLName(m.get("individual"));
        if (m.get("lang") != null) {
            w.setLanguage(m.get("lang"));
        } else {
            Matcher matcher = pattern.matcher((m.get("individual") != null) ? m.get("individual") : "");
            if (matcher.find()) {
                w.setLanguage(matcher.group(1).split("_lemma")[0]);
            } else {
                w.setLanguage(m.get("lang"));
            }
        }
        return w;
    }

    public ArrayList<FormData> getFormsOfLemma(String lemma, String lang) {
        ArrayList<FormData> fdList = new ArrayList<>();
        ArrayList<String> results = getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.FORM_INSTANCES_OF_LEMMA.replace("_LEMMA_", lemma)));
        Collections.sort(results);
        for (String form : results) {
            if (!results.get(0).equals(Label.NO_ENTRY_FOUND)) {
                FormData fd = new FormData();
                setFormData(form, lang, fd);
                fdList.add(fd);
            }
        }
        return fdList;
    }

    private void setFormData(String form, String lang, FormData fd) {
        fd.setIndividual(form);
        fd.setLanguage(lang);
        fd.setFormWrittenRepr(getFormWrittenRep(form));
        fd.setGender(getFormGender(form));
        fd.setPerson(getFormPerson(form));
        fd.setMood(getFormMood(form));
        fd.setVoice(getFormVoice(form));
        fd.setNumber(getFormNumber(form));
        fd.setNote(getFormNote(form));
    }

    private String getFormNote(String form) {
        String note = getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.FORM_NOTE, "_FORM_", form);
        return note.equals(Label.NO_ENTRY_FOUND) ? "" : note;
    }

    public ArrayList<SenseData> getSensesOfLemma(String lemma) {
        ArrayList<SenseData> sdList = new ArrayList<>();
        ArrayList<String> results = getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.SENSES_OF_LEMMA.replace("_LEMMA_", lemma)));
        Collections.sort(results);
        for (String sense : results) {
            if (!results.get(0).equals(Label.NO_ENTRY_FOUND)) {
                SenseData sd = new SenseData();
                senseData(sense, sd);
                sdList.add(sd);
            }
        }
        return sdList;
    }

    public ArrayList<SenseData> getSensesOfForm(String form) {
        ArrayList<SenseData> sdList = new ArrayList<>();
        ArrayList<String> results = getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.SENSES_OF_FORM.replace("_FORM_", form)));
        Collections.sort(results);
        for (String sense : results) {
            if (!results.get(0).equals(Label.NO_ENTRY_FOUND)) {
                SenseData sd = new SenseData();
                senseData(sense, sd);
                sdList.add(sd);
            }
        }
        return sdList;
    }

    private String getSenseNote(String sense) {
        String note = getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.SENSE_NOTE, "_SENSE_", sense);
        return note.equals(Label.NO_ENTRY_FOUND) ? "" : note;
    }

    private void senseData(String sense, SenseData sd) {
        sd.setName(sense);
        sd.setDefinition(getDefinition(sense));
        sd.setNote(getSenseNote(sense));
        sd.setTranslation(getSenseRelation(sense, LexicalQuery.SENSE_RELATION.replace("_RELATION_", "translation")));
        sd.setSynonym(getSenseRelation(sense, LexicalQuery.SENSE_RELATION.replace("_RELATION_", "synonym")));
        sd.setAntonym(getSenseRelation(sense, LexicalQuery.SENSE_RELATION.replace("_RELATION_", "antonym")));
        sd.setHypernym(getSenseRelation(sense, LexicalQuery.SENSE_RELATION.replace("_RELATION_", "hypernym")));
        sd.setHyponym(getSenseRelation(sense, LexicalQuery.SENSE_RELATION.replace("_RELATION_", "hyponym")));
        sd.setApproximateSynonym(getSenseRelation(sense, LexicalQuery.SENSE_RELATION.replace("_RELATION_", "approximateSynonym")));
        sd.setOWLClass(getOntoClass(sense));
        setFieldMaxLenght(sd.getName(), sd);
        setFieldMaxLenght(sd.getSynonym(), sd);
        setFieldMaxLenght(sd.getTranslation(), sd);
        setFieldMaxLenght(sd.getOWLClass(), sd);
        setFieldMaxLenght(sd.getAntonym(), sd);
        setFieldMaxLenght(sd.getApproximateSynonym(), sd);
        setFieldMaxLenght(sd.getHypernym(), sd);
        setFieldMaxLenght(sd.getHyponym(), sd);
        sd.setFiledMaxLenght((sd.getFiledMaxLenght() > FIELD_MAX_LENGHT) ? FIELD_MAX_LENGHT : sd.getFiledMaxLenght());
    }

    private void setFieldMaxLenght(List<Openable> lo, SenseData sd) {
        for (Openable op : lo) {
            if (op.getName().length() > sd.getFiledMaxLenght()) {
                sd.setFiledMaxLenght(op.getName().length());
            }
        }
    }

    private void setFieldMaxLenght(Openable op, SenseData sd) {
        if (op.getName().length() > sd.getFiledMaxLenght()) {
            sd.setFiledMaxLenght(op.getName().length());
        }
    }

    private void setFieldMaxLenght(String s, SenseData sd) {
        if (s.length() > sd.getFiledMaxLenght()) {
            sd.setFiledMaxLenght(s.length());
        }
    }

    public ArrayList<SenseData> getOtherSenses(String sense) {
        ArrayList<SenseData> sdList = new ArrayList<>();
        ArrayList<String> results = getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.OTHER_INSTANCES_OF_SENSES.replace("_SENSE_", sense)));
        Collections.sort(results);
        for (String s : results) {
            if (!results.get(0).equals(Label.NO_ENTRY_FOUND)) {
                SenseData sd = new SenseData();
                senseData(s, sd);
                sdList.add(sd);
            }
        }
        return sdList;
    }

    // invoked in order to retrieve the data of the lemma involved in sublemma or collocation relation
    public Word getLemma(String lemma, String lang) {
        ArrayList<String> word = getList(processQuery(LexicalQuery.PREFIXES + LexicalQuery.LEXICAL_RELATION_WORD.replace("_LANG_", lang).replace("_LEMMA_", lemma)));
        Word w = new Word();
        w.setOWLName(word.get(0));
        w.setLanguage(lang);
        w.setWrittenRep(lemma);
        return w;
    }

    private String getLemmaWrittenRep(String lemma) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_REPRESENTATION, "_LEMMA_", lemma);
    }

    private String getLemmaPoS(String lemma) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_POS, "_LEMMA_", lemma);
    }

    private String getEntryVerified(String lemma) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.ENTRY_VALID, "_ENTRY_", lemma.replace("_lemma", "_entry"));
    }

    private String getFormWrittenRep(String form) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.FORM_REPRESENTATION, "_FORM_", form);
    }

    private String getFormGender(String form) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.FORM_GENDER, "_FORM_", form);
    }

    private String getFormPerson(String form) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.FORM_PERSON, "_FORM_", form);
    }

    private String getFormMood(String form) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.FORM_MOOD, "_FORM_", form);
    }

    private String getFormVoice(String form) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.FORM_VOICE, "_FORM_", form);
    }

    private String getFormNumber(String form) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.FORM_NUMBER, "_FORM_", form);
    }

    public String getDefinition(String sense) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.SENSE_DEFINITION, "_SENSE_", sense);
    }

//    public ArrayList<Openable> getSynonym(String sense) {
//        ArrayList<Openable> sdoList = new ArrayList();
//        ArrayList<String> syns = getEntryAttributeList(LexicalQuery.PREFIXES + LexicalQuery.SENSE_SYNONYM, "_SENSE_", sense);
//        for (String syn : syns) {
//            SenseData.Openable sdo = new SenseData.Openable();
//            if (!syn.equals(Label.NO_ENTRY_FOUND)) {
//                sdo.setName(syn);
//                sdo.setViewButtonDisabled(false);
//                sdo.setDeleteButtonDisabled(false);
//                sdoList.add(sdo);
//            }
//        }
//        return sdoList;
//    }
    public ArrayList<Openable> getSenseRelation(String sense, String query) {
        ArrayList<Openable> sdoList = new ArrayList();
        ArrayList<String> s = getEntryAttributeList(LexicalQuery.PREFIXES + query, "_SENSE_", sense);
        for (String _s : s) {
            SenseData.Openable sdo = new SenseData.Openable();
            if (!_s.equals(Label.NO_ENTRY_FOUND)) {
                sdo.setName(_s);
                sdo.setViewButtonDisabled(false);
                sdo.setDeleteButtonDisabled(false);
                sdoList.add(sdo);
            }
        }
        return sdoList;
    }

//    public ArrayList<Openable> getTranslation(String sense) {
//        ArrayList<Openable> sdoList = new ArrayList();
//        ArrayList<String> trans = getEntryAttributeList(LexicalQuery.PREFIXES + LexicalQuery.SENSE_TRANSLATION, "_SENSE_", sense);
//        for (String tr : trans) {
//            SenseData.Openable sdo = new SenseData.Openable();
//            if (!tr.equals(Label.NO_ENTRY_FOUND)) {
//                sdo.setName(tr);
//                sdo.setViewButtonDisabled(false);
//                sdo.setDeleteButtonDisabled(false);
//                sdoList.add(sdo);
//            }
//        }
//        return sdoList;
//    }
    private Openable getOntoClass(String sense) {
        Openable ref = new Openable();
        String ontoClass = getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.SENSE_REFERENCE, "_SENSE_", sense);
        ref.setName(ontoClass.equals(Label.NO_ENTRY_FOUND) ? "" : ontoClass);
        ref.setViewButtonDisabled(!ontoClass.equals(Label.NO_ENTRY_FOUND));
        return ref;
    }

    private String getLemmaGender(String lemma) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_GENDER, "_LEMMA_", lemma);
    }

    private String getLemmaPerson(String lemma) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_PERSON, "_LEMMA_", lemma);
    }

    private String getLemmaMood(String lemma) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_MOOD, "_LEMMA_", lemma);
    }

    private String getLemmaVoice(String lemma) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_VOICE, "_LEMMA_", lemma);
    }

    private String getLemmaNumber(String lemma) {
        return getEntryAttribute(LexicalQuery.PREFIXES + LexicalQuery.LEMMA_NUMBER, "_LEMMA_", lemma);
    }

    private String getEntryAttribute(String q, String t, String e) {
        ArrayList<String> r = getList(processQuery(q.replace(t, e)));
        return r.get(0);
    }

    private ArrayList<String> getEntryAttributeList(String q, String t, String e) {
        return getList(processQuery(q.replace(t, e)));
    }

    public List<Map<String, String>> processQuery(String q) {
        QueryResult result = null;
        QueryEngine engine = QueryEngine.create(ontologyManager, reasoner);
        Query query;
        try {
            query = Query.create(q);
            result = engine.execute(query);
        } catch (QueryEngineException ex) {
            LOG.fatal(ex);
        } catch (QueryParserException ex2) {
            LOG.error("Error parsing {0}", ex2);
        }
        return getQueryResults(result);
    }

    private List<Map<String, String>> getQueryResults(QueryResult qr) {
        List<Map<String, String>> resultsList = new ArrayList<>();
        if (qr != null) {
            Iterator<QueryBinding> itr = qr.iterator();
            if (itr.hasNext()) {
                while (itr.hasNext()) {
                    QueryBinding qb = itr.next();
                    Set<QueryArgument> keys = qb.getBoundArgs();
                    Map<String, String> map = new HashMap<>();
                    for (QueryArgument key : keys) {
                        switch (qb.get(key).getType().name()) {
                            case "LITERAL":
                                map.put(key.getValueAsVar().getName(), qb.get(key).getValueAsLiteral().getLiteral());
                                break;
                            case "URI":
                                map.put(key.getValueAsVar().getName(), qb.get(key).getValueAsIRI().getShortForm());
                                break;
                            default:
                        }
                    }
                    resultsList.add(map);
                }
            } else {
            }
        } else {
        }
        return resultsList;
    }

}
