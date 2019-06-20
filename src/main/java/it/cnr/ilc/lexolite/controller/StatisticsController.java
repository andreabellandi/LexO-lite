/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.LexOliteProperties;
import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.constant.OntoLexEntity;
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
        setReferenceDataset(LexOliteProperties.getProperty("domainOntologyNamespace"));
        setLexiconDataset(LexOliteProperties.getProperty("lexiconNamespace"));
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
        setReferenceDataset(LexOliteProperties.getProperty("domainOntologyNamespace"));
        setLexiconDataset(LexOliteProperties.getProperty("lexiconNamespace"));
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

    public static class User {

        private String username;
        private String password;
        private String role;
        private String editedEntries;
        private String validatedEntries;
        private String percent;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getEditedEntries() {
            return editedEntries;
        }

        public void setEditedEntries(String editedEntries) {
            this.editedEntries = editedEntries;
        }

        public String getValidatedEntries() {
            return validatedEntries;
        }

        public void setValidatedEntries(String validatedEntries) {
            this.validatedEntries = validatedEntries;
        }

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }

        public User(String us, String pwd, String role, String ee, String ve, String perc) {
            this.editedEntries = ee;
            this.password = pwd;
            this.percent = perc;
            this.role = role;
            this.username = us;
            this.validatedEntries = ve;
        }

    }

}
