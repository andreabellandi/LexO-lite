/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.LexOliteProperty;
import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.constant.OntoLexEntity;
import it.cnr.ilc.lexolite.domain.Authoring;
import it.cnr.ilc.lexolite.manager.AuthoringManager;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.OntologyManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import it.cnr.ilc.lexolite.util.LexiconUtil;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.optionconfig.title.Title;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class StatisticsController extends BaseController implements Serializable {

    @Inject
    private PropertyValue propertyValue;
    @Inject
    private LexiconControllerTabViewList lexiconCreationControllerTabViewList;
    @Inject
    private LexiconControllerSenseDetail lexiconCreationViewSenseDetail;
    @Inject
    private LexiconControllerLinkedLexicalEntryDetail lexiconCreationControllerRelationDetail;
    @Inject
    private LexiconControllerDictionary lexiconExplorationControllerDictionary;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private OntologyManager ontologyManager;
    @Inject
    private LoginController loginController;
    @Inject
    private AuthoringManager authoringManager;

    private DonutChartModel donutModel;
    private BarChartModel barModel2;

    private String languageName = "All languages";
    private int numberOfEntries;
    private String languageDescription = "ND";
    private String languageCreator = "ND";
    private String referenceDataset;
    private String lexiconDataset;
    private int lexicalizations;
    private int conceptualizations;

    private ArrayList<UserStatisticsDetail> usd = new ArrayList<>();

    public ArrayList<UserStatisticsDetail> getUsd() {
        return usd;
    }

    public void setUsd(ArrayList<UserStatisticsDetail> usd) {
        this.usd = usd;
    }

    public String getLanguageDescription() {
        return languageDescription;
    }

    public void setLanguageDescription(String languageDescription) {
        this.languageDescription = languageDescription;
    }

    public String getLanguageCreator() {
        return languageCreator;
    }

    public void setLanguageCreator(String languageCreator) {
        this.languageCreator = languageCreator;
    }

    public String getReferenceDataset() {
        return referenceDataset;
    }

    public void setReferenceDataset(String referenceDataset) {
        this.referenceDataset = referenceDataset;
    }

    public String getLexiconDataset() {
        return lexiconDataset;
    }

    public void setLexiconDataset(String lexiconDataset) {
        this.lexiconDataset = lexiconDataset;
    }

    public int getLexicalizations() {
        return lexicalizations;
    }

    public void setLexicalizations(int lexicalizations) {
        this.lexicalizations = lexicalizations;
    }

    public int getConceptualizations() {
        return conceptualizations;
    }

    public void setConceptualizations(int conceptualizations) {
        this.conceptualizations = conceptualizations;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    public void setNumberOfEntries(int numberOfEntries) {
        this.numberOfEntries = numberOfEntries;
    }

    public DonutChartModel getPieModel1() {
        createPieModel();
        return donutModel;
    }

    public BarChartModel getBarModel() {
        return barModel2;
    }

    private void createWordsDistribution(int noun, int adjective, int verb, int nounPhrase, int adjectivePhrase, int verbPhrase) {
        barModel2 = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Noun");
        barDataSet.setBackgroundColor("rgba(32, 32, 32, 0.2)");
        barDataSet.setBorderColor("rgb(32, 32, 32)");
        barDataSet.setBorderWidth(1);
        List<Number> values = new ArrayList<>();
        values.add(noun);
        values.add(nounPhrase);
        barDataSet.setData(values);

        BarChartDataSet barDataSet2 = new BarChartDataSet();
        barDataSet2.setLabel("Verb");
        barDataSet2.setBackgroundColor("rgba(136, 136, 136, 0.2)");
        barDataSet2.setBorderColor("rgb(136, 136, 136)");
        barDataSet2.setBorderWidth(1);
        List<Number> values2 = new ArrayList<>();
        values2.add(verb);
        values2.add(verbPhrase);
        barDataSet2.setData(values2);

        BarChartDataSet barDataSet3 = new BarChartDataSet();
        barDataSet3.setLabel("Adjective");
        barDataSet3.setBackgroundColor("rgba(176, 176, 176, 0.2)");
        barDataSet3.setBorderColor("rgb(176, 176, 176)");
        barDataSet3.setBorderWidth(1);
        List<Number> values3 = new ArrayList<>();
        values3.add(adjective);
        values3.add(adjectivePhrase);
        barDataSet3.setData(values3);

        data.addChartDataSet(barDataSet);
        data.addChartDataSet(barDataSet2);
        data.addChartDataSet(barDataSet3);

        List<String> labels = new ArrayList<>();
        labels.add(OntoLexEntity.Class.WORD.getLabel());
        labels.add(OntoLexEntity.Class.MULTIWORD.getLabel());
        data.setLabels(labels);
        barModel2.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        ticks.setStepSize(200);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        barModel2.setOptions(options);
    }

    private void createPieModel() {
        donutModel = new DonutChartModel();
        ChartData data = new ChartData();
        DonutChartDataSet dataSet = new DonutChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> bgColors = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int lexicalizations = 0;
        int totalNumberOfEntries = 0, partialNumberOfEntries;
        int noun = 0, adjective = 0, verb = 0, nounPhrase = 0, adjectivePhrase = 0, verbPhrase = 0;
        for (String lang : lexiconCreationControllerTabViewList.getDynamicLexicaMenuItems()) {
            if (!lang.equals("All languages")) {
                Color c = LexiconUtil.getRandomColor();
                partialNumberOfEntries = lexiconManager.lemmasList(lang).size();
                values.add(partialNumberOfEntries);
                totalNumberOfEntries = totalNumberOfEntries + partialNumberOfEntries;
                bgColors.add("rgb(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")");
                labels.add(lang);
                int[] posDist = getEntriesDetails(lang);
                lexicalizations = lexicalizations + posDist[6];
                noun = noun + posDist[0];
                adjective = adjective + posDist[2];
                verb = verb + posDist[1];
                nounPhrase = nounPhrase + posDist[3];
                adjectivePhrase = adjectivePhrase + posDist[5];
                verbPhrase = verbPhrase + posDist[4];
            }
        }
        createWordsDistribution(noun, adjective, verb, nounPhrase, adjectivePhrase, verbPhrase);
        setLexicalizations(lexicalizations);
        setNumberOfEntries(totalNumberOfEntries);
        setConceptualizations(ontologyManager.getOntologyClasses().size());
        setReferenceDataset(LexOliteProperty.getProperty(Label.ONTOLOGY_NAMESPACE_KEY));
        setLexiconDataset(LexOliteProperty.getProperty(Label.LEXICON_NAMESPACE_KEY));
        dataSet.setData(values);
        dataSet.setBackgroundColor(bgColors);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        donutModel.setData(data);

    }

    public void itemSelect(ItemSelectEvent event) {
        String selectedLang = lexiconCreationControllerTabViewList.getDynamicLexicaMenuItems().get(event.getItemIndex() + 1);
        setLanguageName(selectedLang);
        setNumberOfEntries(lexiconManager.lemmasList(selectedLang).size());
        setReferenceDataset(LexOliteProperty.getProperty("domainOntologyNamespace"));
        setLexiconDataset(LexOliteProperty.getProperty("lexiconNamespace"));
        String creator = lexiconManager.languageCreator(selectedLang).get(0);
        String description = lexiconManager.languageDescription(selectedLang).get(0);
        setLanguageCreator(creator.equals(Label.NO_ENTRY_FOUND) ? "ND" : creator);
        setLanguageDescription(description.equals(Label.NO_ENTRY_FOUND) ? "ND" : description);
        int[] posDist = getEntriesDetails(selectedLang);
        setLexicalizations(posDist[6]);
        setConceptualizations(ontologyManager.getOntologyClasses().size());
        createWordsDistribution(posDist[0], posDist[2], posDist[1], posDist[3], posDist[5], posDist[4]);
    }

    /* 0 = noun 
       1 = verb 
       2 = adjective 
       3 = nounPhrase 
       4 = verbPhrase
       5 = adjectivePhrase 
       6 = lexicalisations  */
    private int[] getEntriesDetails(String selectedLang) {
        int[] posDist = new int[7];
        for (Map<String, String> m : lexiconManager.lemmasList(selectedLang)) {
            String _pos = lexiconCreationControllerTabViewList.getPosFromIndividual(m.get("individual"), m.get("lang"));
            m.put("pos", _pos.isEmpty() ? Label.UNSPECIFIED_POS : _pos);
            if (m.get("type").equals(OntoLexEntity.Class.WORD.getLabel())) {
                if (m.get("pos").equals("noun")) {
                    posDist[0] = posDist[0] + 1;
                } else {
                    if (m.get("pos").equals("verb")) {
                        posDist[1] = posDist[1] + 1;
                    } else {
                        if (m.get("pos").equals("adjective")) {
                            posDist[2] = posDist[2] + 1;
                        }
                    }
                }
            } else {
                if (m.get("type").equals(OntoLexEntity.Class.MULTIWORD.getLabel())) {
                    if (m.get("pos").equals("noun")) {
                        posDist[3] = posDist[3] + 1;
                    } else {
                        if (m.get("pos").equals("verb")) {
                            posDist[4] = posDist[4] + 1;
                        } else {
                            if (m.get("pos").equals("adjective")) {
                                posDist[5] = posDist[5] + 1;
                            }
                        }
                    }
                }
            }
            ArrayList<String> al = (lexiconManager.lexicalizazions(m.get("individual").replaceAll("_lemma", "_entry")));
            if (!al.get(0).equals(Label.NO_ENTRY_FOUND)) {
                posDist[6] = posDist[6] + al.size();
            }
        }
        return posDist;
    }

    public ArrayList<UserStatistics> getUserStatistics() {
        // 0=type of edit; 1=user; 2=user role
        ArrayList<UserStatistics> al = new ArrayList<>();
        List<Object[]> stats = authoringManager.getUserStatistics();
        String user = (String) stats.get(0)[1];
        String role = (String) stats.get(0)[2];
        int leNumber = 0, formNumber = 0, senseNumber = 0;
        for (Object[] o : stats) {
            if (user.equals(o[1])) {
                if (0 == Integer.parseInt(o[0].toString())) {
                    leNumber++;
                } else {
                    if (1 == Integer.parseInt(o[0].toString())) {
                        formNumber++;
                    } else {
                        if (2 == Integer.parseInt(o[0].toString())) {
                            senseNumber++;
                        }
                    }
                }
            } else {
                addUser(al, user, leNumber, formNumber, senseNumber, role);
                leNumber = 0;
                formNumber = 0;
                senseNumber = 0;
                user = o[1].toString();
                role = o[2].toString();
                if (0 == Integer.parseInt(o[0].toString())) {
                    leNumber++;
                } else {
                    if (1 == Integer.parseInt(o[0].toString())) {
                        formNumber++;
                    } else {
                        if (2 == Integer.parseInt(o[0].toString())) {
                            senseNumber++;
                        }
                    }
                }
            }
        }
        addUser(al, user, leNumber, formNumber, senseNumber, role);
        return al;
    }

    private void addUser(ArrayList<UserStatistics> al, String user, int leNumber, int formNumber, int senseNumber, String role) {
        UserStatistics us = new UserStatistics();
        us.setUsername(user);
        us.setRole(role);
        us.setLeNumber(String.valueOf(leNumber));
        us.setFormNumber(String.valueOf(formNumber));
        us.setSenseNumber(String.valueOf(senseNumber));
        us.setTotal(String.valueOf(leNumber + formNumber + senseNumber));
        al.add(us);
    }

    public void onRowToggle(ToggleEvent event) {
        // 0=iri; 1=creation
        usd.clear();
        for (Object[] o : authoringManager.getStatDetails(((UserStatistics) event.getData()).username)) {
            UserStatisticsDetail _usd = new UserStatisticsDetail();
            _usd.setCreation(o[1].toString());
            _usd.setIRI(o[0].toString());
            usd.add(_usd);
        }
    }

    public static class UserStatistics {

        private String username;
        private String role;
        private String leNumber;
        private String formNumber;
        private String senseNumber;
        private String total;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getLeNumber() {
            return leNumber;
        }

        public void setLeNumber(String leNumber) {
            this.leNumber = leNumber;
        }

        public String getFormNumber() {
            return formNumber;
        }

        public void setFormNumber(String formNumber) {
            this.formNumber = formNumber;
        }

        public String getSenseNumber() {
            return senseNumber;
        }

        public void setSenseNumber(String senseNumber) {
            this.senseNumber = senseNumber;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

    }

    public static class UserStatisticsDetail {

        private String IRI;
        private String creation;

        public String getIRI() {
            return IRI;
        }

        public void setIRI(String IRI) {
            this.IRI = IRI;
        }

        public String getCreation() {
            return creation;
        }

        public void setCreation(String creation) {
            this.creation = creation;
        }

    }

}
