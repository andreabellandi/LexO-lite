/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.domain.AccountType;
import it.cnr.ilc.lexolite.manager.AccountManager;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.OntologyManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Level;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import it.cnr.ilc.lexolite.LexOliteProperty;
import it.cnr.ilc.lexolite.constant.Label;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerTabViewList extends BaseController implements Serializable {

    @Inject
    private LexiconControllerLexicalAspect lexiconControllerLexicalAspect;
    @Inject
    private LexiconControllerFormDetail lexiconCreationControllerFormDetail;
    @Inject
    private LexiconControllerSenseDetail lexiconCreationControllerSenseDetail;
    @Inject
    private LexiconControllerVarTransFormDetail lexiconCreationControllerVarTransFormDetail;
    @Inject
    private LexiconControllerVarTransSenseDetail lexiconCreationControllerVarTransSenseDetail;
    @Inject
    private LexiconControllerSynSemFormDetail lexiconCreationControllerSynSemFormDetail;
    @Inject
    private LexiconControllerSynSemSenseDetail lexiconCreationControllerSynSemSenseDetail;
    @Inject
    private LexiconControllerLinkedLexicalEntryDetail lexiconCreationControllerRelationDetail;

    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LoginController loginController;
    @Inject
    private OntologyManager ontologyManager;
    @Inject
    private AccountManager accountManager;
    @Inject
    private LexiconControllerLemmaFilter lexiconCreationLemmaFilterController;
    @Inject
    private LexiconControllerFormFilter lexiconCreationFormFilterController;
    @Inject
    private LexiconControllerSenseFilter lexiconCreationSenseFilterController;
    @Inject
    private LexiconControllerOntologyDetail lexiconCreationOntologyDetailController;
    @Inject
    private LexiconControllerTabViewToolbar lexiconCreationControllerTabViewToolbar;
    @Inject
    private LexiconControllerVarTransFormDetail lexiconControllerVarTransFormDetail;
    @Inject
    private LexiconControllerVarTransSenseDetail lexiconControllerVarTransSenseDetail;
    @Inject
    private LexiconControllerSynSemFormDetail lexiconControllerSynSemFormDetail;
    @Inject
    private LexiconControllerSynSemSenseDetail lexiconControllerSynSemSenseDetail;

    private String lemmaField;
    private String formField;
    private String senseField;

    private Integer activeTab = 0;
    private String ontologyField;
    private Integer ontoCounter = 0;

    private final TreeNode lemmaRoot = new DefaultTreeNode("Root", null);
    private final TreeNode formRoot = new DefaultTreeNode("Root", null);
    private final TreeNode senseRoot = new DefaultTreeNode("Root", null);
    private final TreeNode ontoRoot = new DefaultTreeNode("Root", null);
    private TreeNode selection;

    private ArrayList<String> dynamicLexicaMenuItems = new ArrayList<>();
    private String lexiconLanguage;
    private String lemmaStartsWith;
    private boolean enabledFilter = false;

    private List<Map<String, String>> cachedLemmaList = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> cachedFormList = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> cachedSenseList = new ArrayList<Map<String, String>>();

    public Integer getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(Integer activeTab) {
        this.activeTab = activeTab;
    }

    public String getLemmaField() {
        return lemmaField;
    }

    public void setLemmaField(String lemmaField) {
        this.lemmaField = lemmaField;
    }

    public String getFormField() {
        return formField;
    }

    public String getOntologyField() {
        return ontologyField;
    }

    public void setOntologyField(String ontologyField) {
        this.ontologyField = ontologyField;
    }

    public void setFormField(String formField) {
        this.formField = formField;
    }

    public String getSenseField() {
        return senseField;
    }

    public void setSenseField(String senseField) {
        this.senseField = senseField;
    }

    public TreeNode getLemmaRoot() {
        return lemmaRoot;
    }

    public TreeNode getFormRoot() {
        return formRoot;
    }

    public TreeNode getOntoRoot() {
        return ontoRoot;
    }

    public TreeNode getSenseRoot() {
        return senseRoot;
    }

    public TreeNode getSelection() {
        return selection;
    }

    public void setSelection(TreeNode selection) {
        this.selection = selection;
    }

    public String getLemmaStartsWith() {
        return lemmaStartsWith;
    }

    public void setLemmaStartsWith(String lemmaStartsWith) {
        this.lemmaStartsWith = lemmaStartsWith;
    }

    public boolean isEnabledFilter() {
        return enabledFilter;
    }

    public void setEnabledFilter(boolean enabledFilter) {
        this.enabledFilter = enabledFilter;
    }

    public ArrayList<String> getDynamicLexicaMenuItems() {
        return dynamicLexicaMenuItems;
    }

    public String getLexiconLanguage() {
        return lexiconLanguage;
    }

    public void setLexiconLanguage(String lexiconLanguage) {
        this.lexiconLanguage = lexiconLanguage;
    }

    public String getLemmaCounter() {
        return Integer.toString(lemmaRoot.getChildCount());
    }

    public String getFormCounter() {
        return Integer.toString(formRoot.getChildCount());
    }

    public String getSenseCounter() {
        return Integer.toString(senseRoot.getChildCount());
    }

    public boolean isEditTabRendered() {
        return accountManager.hasPermission(AccountType.Permission.WRITE_ALL, AccountManager.Access.LEXICON_EDITOR, loginController.getAccount());
    }

    @PostConstruct
    public void INIT() {
        LexOliteProperty.load();
        if (LexOliteProperty.getProperty(Label.LEXICON_FILE_NAME_KEY) != null) {
            lexiconManager.deafult_loadLexicon();
            initLexicaMenu();
            setLexiconLanguage("All languages");
            initLemmaTabView("All languages");
            initFormTabView("All languages");
            setEnabledFilter(true);
        }
        if (LexOliteProperty.getProperty(Label.ONTOLOGY_FILE_NAME_KEY) != null) {
            ontologyManager.deafult_loadOntology();
            initDomainOntologyTabView();
        }
    }

    public void loadLexicon(String lang) {
        log(Level.INFO, loginController.getAccount(), "LOAD lexicon " + lang);
        resetPanels();
        setLexiconLanguage(lang);
        updateLexiconLanguagesList();
        initLemmaTabView(lang);
        initFormTabView(lang);
        lemmaField = "";
        formField = "";
        senseField = "";
    }

    public void updateLexiconLanguagesList() {
        dynamicLexicaMenuItems.clear();
        dynamicLexicaMenuItems = lexiconManager.lexicaLanguagesList();
        dynamicLexicaMenuItems.add("All languages");
        Collections.sort(dynamicLexicaMenuItems);
        lexiconCreationControllerTabViewToolbar.updateMenu(dynamicLexicaMenuItems);
    }

    public void initDomainOntologyTabView() {
        if (ontologyManager.getOntologyModel() != null) {
            ontoRoot.getChildren().clear();
            ontoCounter = ontologyManager.getOntologyHierarchy(ontoRoot);
            ontologyManager.getPropertyHierarchy(ontoRoot);
        }
    }

    public void initDomainOntologyTabView(String nameToSelect) {
        ontoRoot.getChildren().clear();
        ontoCounter = ontologyManager.getOntologyHierarchy(ontoRoot, nameToSelect);
    }

    public void initOntoTabView() {
        ontoRoot.getChildren().clear();
    }

    private void updateCache(List<Map<String, String>> l, String typeList) {
        Collections.sort(l, new LexiconComparator("writtenRep"));
        switch (typeList) {
            case "Lemma":
                cachedLemmaList.clear();
                cachedLemmaList.addAll(l);
                break;
            case "Form":
                cachedFormList.clear();
                cachedFormList.addAll(l);
                break;
            // it never happens because currently the "Sense" tab panel does not exist
            case "Sense":
                cachedSenseList.clear();
                cachedSenseList.addAll(l);
                break;
            default:
        }
    }

    // invoked by CNL query filter and advanced one
    public void cnlqFilterLemmaTabView(List<Map<String, String>> ll) {
        lemmaRoot.getChildren().clear();
        updateCache(ll, "Lemma");
        for (Map<String, String> m : cachedLemmaList) {
            DataTreeNode dtn = new DataTreeNode(m, 0);
            lemmaRoot.getChildren().add(new DefaultTreeNode(dtn));
        }
    }

    // invoked by CNL query filter and advanced one
    public void cnlqFilterFormTabView(List<Map<String, String>> fl) {
        formRoot.getChildren().clear();
        updateCache(fl, "Form");
        for (Map<String, String> m : cachedFormList) {
            DataTreeNode dtn = new DataTreeNode(m, 0);
            formRoot.getChildren().add(new DefaultTreeNode(dtn));
        }
    }

    // invoked by CNL query filter and advanced one
    public void cnlqFilterSenseTabView(List<Map<String, String>> sl) {
        senseRoot.getChildren().clear();
        updateCache(sl, "Sense");
        for (Map<String, String> m : cachedSenseList) {
            DataTreeNode dtn = new DataTreeNode(m, 0);
            senseRoot.getChildren().add(new DefaultTreeNode(dtn));
        }
    }

    public void initLemmaTabView(String lang) {
        lemmaRoot.getChildren().clear();
        updateCache(lexiconManager.lemmasList(lang), "Lemma");
        for (Map<String, String> m : cachedLemmaList) {
            DataTreeNode dtn = new DataTreeNode(m, 0);
            lemmaRoot.getChildren().add(new DefaultTreeNode(dtn));
        }
    }

    public void initFormTabView(String lang) {
        formRoot.getChildren().clear();
        List<Map<String, String>> fl = lexiconManager.formsList(lang);
        Collections.sort(fl, new LexiconComparator("writtenRep"));
        updateCache(lexiconManager.formsList(lang), "Form");
        for (Map<String, String> m : cachedFormList) {
            DataTreeNode dtn = new DataTreeNode(m, 0);
            formRoot.getChildren().add(new DefaultTreeNode(dtn));
        }
    }

    public void initLexicaMenu() {
        if (null == lexiconManager) {
            throw new RuntimeException("lexiconManager is null!");
        }
        dynamicLexicaMenuItems.clear();
        dynamicLexicaMenuItems.add("All languages");
        for (String lang : lexiconManager.lexicaLanguagesList()) {
            dynamicLexicaMenuItems.add(lang);
        }
        Collections.sort(dynamicLexicaMenuItems);
    }

    public void navigationEntry(String entry) {
        System.err.println("Entry " + entry);
        String entryType = "Lemma";
        resetPanels();
        lexiconCreationControllerFormDetail.setNewAction(false);
        lexiconCreationControllerFormDetail.setLemmaRendered(true);
        lexiconCreationControllerFormDetail.setAddFormButtonDisabled(false);
        lexiconCreationControllerFormDetail.setFormAlreadyExists(false);
        lexiconCreationControllerFormDetail.setLemmAlreadyExists(false);
        lexiconCreationControllerFormDetail.setIsAdmissibleLemma(true);
        switch (entryType) {
            case "Lemma":
                lexiconCreationControllerFormDetail.addLemma(entry);
                break;
            case "Form":
                log(Level.INFO, loginController.getAccount(), "SELECT Form " + entry);
                lexiconCreationControllerFormDetail.addForm(entry);
                break;
            default:
        }
        lexiconCreationControllerSenseDetail.setSenseRendered(true);
        lexiconCreationControllerSenseDetail.setAddSenseButtonDisabled(false);
        lexiconCreationControllerSenseDetail.setSenseToolbarRendered(true);
        lexiconCreationControllerSenseDetail.addSense(entry, entryType);
        checkForLock(entry);
        lexiconManager.getLexiconLocker().print();
        lexiconCreationControllerRelationDetail.setActiveTab(0);
    }

    public void onSelect(NodeSelectEvent event) {
        boolean verified = ((DataTreeNode) event.getTreeNode().getData()).getVerified().equals("true") ? true : false;
        lexiconCreationControllerFormDetail.setVerified(verified);
        lexiconCreationControllerSenseDetail.setVerified(verified);
        long startTime = System.currentTimeMillis();
        resetPanels();
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        String entry = ((DataTreeNode) event.getTreeNode().getData()).getOWLname();
        System.err.println("onSelect Entry: " + entry);
        String entryType = (String) component.getAttributes().get("LexicalEntryType");
        lexiconControllerLexicalAspect.setLexicalAspectActive("Core");
        lexiconCreationControllerFormDetail.setNewAction(false);
        lexiconCreationControllerFormDetail.setLemmaRendered(true);
        lexiconCreationControllerFormDetail.setAddFormButtonDisabled(false);
        lexiconCreationControllerFormDetail.setFormAlreadyExists(false);
        lexiconCreationControllerFormDetail.setLemmAlreadyExists(false);
        lexiconCreationControllerFormDetail.setIsAdmissibleLemma(true);
        switch (entryType) {
            case "Lemma":
                lexiconCreationControllerFormDetail.addLemma(entry);
                break;
            case "Form":
                log(Level.INFO, loginController.getAccount(), "SELECT Form " + entry);
                lexiconCreationControllerFormDetail.addForm(entry);
                break;
            default:
        }
        lexiconCreationControllerSenseDetail.setSenseRendered(true);
        lexiconCreationControllerSenseDetail.setAddSenseButtonDisabled(false);
        lexiconCreationControllerSenseDetail.setSenseToolbarRendered(true);
        lexiconCreationControllerSenseDetail.addSense(entry, entryType);
        lexiconControllerVarTransFormDetail.setVarTransRendered(false);
        lexiconControllerVarTransSenseDetail.setSenseVarTransRendered(false);
        lexiconControllerSynSemFormDetail.setSynSemRendered(false);
        lexiconControllerSynSemSenseDetail.setSenseSynSemRendered(false);
        checkForLock(entry);
        lexiconManager.getLexiconLocker().print();
        lexiconCreationControllerRelationDetail.setActiveTab(0);
        lexiconControllerLexicalAspect.setRendered(true);

        long endTime = System.currentTimeMillis();
        System.out.println("DURATA CONTROLLER CHE CONTIENE LE QUERIES: " + (endTime - startTime));
        log(org.apache.log4j.Level.INFO, null, "DURATA QUERY LEMMA: " + (endTime - startTime));

    }

    public void onOntoSelect(NodeSelectEvent event) {
        lexiconControllerLexicalAspect.setRendered(false);
        resetPanels();
        String entry = ((DataTreeNode) event.getTreeNode().getData()).getName();
        log(Level.INFO, loginController.getAccount(), "SELECT Ontology class: " + entry);
        lexiconCreationOntologyDetailController.addOntologyClassDetails(entry);
        lexiconCreationOntologyDetailController.setOntologyClassRendered(true);
    }

    private void checkForLock(String entry) {
        // unlock the previous lexical entry
        boolean unlocked = lexiconManager.unlock();
        if (unlocked) {
            log(Level.INFO, loginController.getAccount(), "UNLOCK the lexical entry related to " + entry);
            lexiconCreationControllerFormDetail.setLocker("");
            lexiconCreationControllerFormDetail.setLocked(false);
            lexiconCreationControllerSenseDetail.setLocked(false);
        }
        // unlock the previous relational lexical entry
        unlocked = lexiconManager.unlock();
        if (unlocked) {
            log(Level.INFO, loginController.getAccount(), "UNLOCK the lexical entry related to " + entry);
            lexiconCreationControllerRelationDetail.setLocker("");
            lexiconCreationControllerRelationDetail.setLocked(false);
        }
        // check if the lexical entry is available and lock it
        boolean locked = lexiconManager.checkForLock(entry);
        if (locked) {
            lexiconCreationControllerFormDetail.setLocked(true);
            lexiconCreationControllerSenseDetail.setLocked(true);
            lexiconCreationControllerFormDetail.setLocker(lexiconManager.getLockingUser(entry) + " is working ... ");
            log(Level.INFO, loginController.getAccount(), "ACCESS TO THE LOCKED lexical entry related to " + entry);
        } else {
            lexiconCreationControllerFormDetail.setLocked(false);
            lexiconCreationControllerSenseDetail.setLocked(false);
            lexiconCreationControllerFormDetail.setLocker("");
            log(Level.INFO, loginController.getAccount(), "LOCK the lexical entry related to " + entry);
        }
    }

    // invoked when the user types a char
    public void lemmaKeyupFilterEvent(AjaxBehaviorEvent e) {
        String keyFilter = (String) e.getComponent().getAttributes().get("value");
        lemmaField = keyFilter;
        getFilteredList(lemmaRoot, cachedLemmaList, keyFilter, "Lemma");
    }

    // invoked when the user select the filter mode (starts with or contains)
    public void lemmaKeyupFilterEvent(String keyFilter) {
        getFilteredList(lemmaRoot, cachedLemmaList, keyFilter, "Lemma");
    }

    // invoked when the user types a char
    public void formKeyupFilterEvent(AjaxBehaviorEvent e) {
        String keyFilter = (String) e.getComponent().getAttributes().get("value");
        getFilteredList(formRoot, cachedFormList, keyFilter, "Form");
    }

    // invoked when the user select the filter mode (starts with or contains)
    public void formKeyupFilterEvent(String keyFilter) {
        getFilteredList(formRoot, cachedFormList, keyFilter, "Form");
    }

    // invoked when the user types a char
    public void senseKeyupFilterEvent(AjaxBehaviorEvent e) {
        String keyFilter = (String) e.getComponent().getAttributes().get("value");
        getFilteredList(senseRoot, cachedSenseList, keyFilter, "Sense");
    }

    // invoked when the user select the filter mode (starts with or contains)
    public void senseKeyupFilterEvent(String keyFilter) {
        getFilteredList(senseRoot, cachedFormList, keyFilter, "Sense");
    }

    private void resetPanels() {
        lexiconCreationControllerFormDetail.setLemmaRendered(false);
        lexiconCreationControllerFormDetail.resetFormDetails();
        lexiconCreationControllerSenseDetail.resetSenseDetails();
        lexiconCreationControllerRelationDetail.resetRelationDetails();
        lexiconCreationControllerSenseDetail.setSenseRendered(false);
        lexiconCreationOntologyDetailController.setOntologyClassRendered(false);
        lexiconCreationControllerVarTransFormDetail.setVarTransRendered(false);
        lexiconCreationControllerVarTransFormDetail.resetFormDetails();
        lexiconCreationControllerVarTransSenseDetail.setSenseVarTransRendered(false);
        lexiconCreationControllerVarTransSenseDetail.resetSenseDetails();
        lexiconCreationControllerSynSemFormDetail.setSynSemRendered(false);
        lexiconCreationControllerSynSemFormDetail.resetFormDetails();
        lexiconCreationControllerSynSemSenseDetail.setSenseSynSemRendered(false);
        lexiconCreationControllerSynSemSenseDetail.resetSenseDetails();
    }

    public void searchReset(String entryType) {
        switch (entryType) {
            case "Lemma":
                log(Level.INFO, loginController.getAccount(), "RESET Lemma Search Filter ");
                updateCache(lexiconManager.lemmasList(getLexiconLanguage()), "Lemma");
                getFilteredList(lemmaRoot, cachedLemmaList, "", "Lemma");
                setLemmaField("");
                break;
            case "Form":
                log(Level.INFO, loginController.getAccount(), "RESET Form Search Filter ");
                updateCache(lexiconManager.formsList(getLexiconLanguage()), "Form");
                getFilteredList(formRoot, cachedFormList, "", "Form");
                setFormField("");
                break;
            default:
        }
    }

    private void getFilteredList(TreeNode tn, List<Map<String, String>> list, String keyFilter, String type) {
        tn.getChildren().clear();
        Collections.sort(list, new LexiconComparator("writtenRep"));
        if (!keyFilter.equals("")) {
            if ((lexiconCreationLemmaFilterController.isStartWith() && type.equals("Lemma"))
                    || ((lexiconCreationFormFilterController.isStartWith() && type.equals("Form")))
                    || ((lexiconCreationSenseFilterController.isStartWith() && type.equals("Sense")))) {
                for (Map<String, String> m : list) {
                    if ((m.get("writtenRep").startsWith(keyFilter)) && (!m.get("writtenRep").isEmpty())) {
                        if (type.equals("Sense")) {
                            m.put("individual", m.get("writtenRep"));
                        }
                        if (type.equals("Form")) {
                            DataTreeNode dtn = new DataTreeNode(m, 0);
                            formRoot.getChildren().add(new DefaultTreeNode(dtn));
                        }
                        if (type.equals("Lemma") && isLemmaFilterable(m)) {
                            DataTreeNode dtn = new DataTreeNode(m, 0);
                            tn.getChildren().add(new DefaultTreeNode(dtn));
                        }
                    }
                }
            }
            if ((lexiconCreationLemmaFilterController.isContains() && type.equals("Lemma"))
                    || ((lexiconCreationFormFilterController.isContains() && type.equals("Form")))
                    || ((lexiconCreationSenseFilterController.isContains() && type.equals("Sense")))) {
                for (Map<String, String> m : list) {
                    if ((m.get("writtenRep").contains(keyFilter)) && (!m.get("writtenRep").isEmpty())) {
                        if (type.equals("Sense")) {
                            m.put("individual", m.get("writtenRep"));
                        }
                        if (type.equals("Form")) {
                            DataTreeNode dtn = new DataTreeNode(m, 0);
                            formRoot.getChildren().add(new DefaultTreeNode(dtn));
                        }
                        if (type.equals("Lemma") && isLemmaFilterable(m)) {
                            DataTreeNode dtn = new DataTreeNode(m, 0);
                            tn.getChildren().add(new DefaultTreeNode(dtn));
                        }
                    }
                }
            }
        } else {
            if (type.equals("Lemma")) {
                for (Map<String, String> m : list) {
                    if (isLemmaFilterable(m)) {
                        DataTreeNode dtn = new DataTreeNode(m, 0);
                        tn.getChildren().add(new DefaultTreeNode(dtn));
                    }
                }
            } else {
                if (type.equals("Form")) {
                    for (Map<String, String> m : list) {
                        DataTreeNode dtn = new DataTreeNode(m, 0);
                        formRoot.getChildren().add(new DefaultTreeNode(dtn));
                    }
                }
            }
        }
    }

    private boolean isLemmaFilterable(Map<String, String> m) {
        if (lexiconCreationLemmaFilterController.isAll()) {
            return true;
        } else {
            if (lexiconCreationLemmaFilterController.isExt()) {
                if (m.get("lemmaInfo") != null) {
                    return m.get("lemmaInfo").equals("corpusExternalLemma");
                } else {
                    return false;
                }
            } else {
                if (lexiconCreationLemmaFilterController.isRec()) {
                    if (m.get("lemmaInfo") != null) {
                        return m.get("lemmaInfo").equals("reconstructedForm");
                    } else {
                        return false;
                    }
                } else {
                    if (lexiconCreationLemmaFilterController.isHyp()) {
                        if (lexiconCreationLemmaFilterController.isRec()) {
                            if (m.get("lemmaInfo") != null) {
                                return m.get("lemmaInfo").equals("hypoteticalForm");
                            } else {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    public void collapseOnto() {
        initDomainOntologyTabView("collapse");
        setSelection(null);
    }

    public void expandOnto() {
        initDomainOntologyTabView();
        setSelection(null);
    }

    // temporary naive solution. When a user creates a new language 
    // he/she should have the possibility to choice a color
    public String getLanguageColor(String lang) {
        switch (lang) {
            case "fr":
                return "color: #5858FA;";
            case "it":
                return "color: #AC58FA;";
            case "ru":
                return "color: #2E2EFE;";
            case "pl":
                return "color: #FE2E2E;";
            case "es":
                return "color: #5F04B4;";
            case "la":
                return "color: #F6CECE;";
            case "he":
                return "color: #5858FA;";
            case "ar":
                return "color: #9AFE2E;";
            case "de":
                return "color: #FACC2E;";
            case "gr":
                return "color: #AC58FA;";
            case "en":
                return "color: #AC58FA;";
        }
        return "color: #9E9E9E";
    }

    public String getPosFromIndividual(String individual, String lang) {
        String[] ret = individual.split("_" + lang + "_")[0].split("_");
        return ret[ret.length - 1];
    }
    
    public static class DataTreeNode {

        private int hierarchyLevel;
        private String name;
        private String OWLname;
        private String language;
        private String type;
        private String verified;

        public DataTreeNode(String name, String OWLname, String language, String type, String verified, String corr, String info, int hierarchyLevel) {
            this.name = name;
            this.OWLname = OWLname;
            this.hierarchyLevel = hierarchyLevel;
            this.language = language;
            this.type = type;
            this.verified = "false";
        }

        public DataTreeNode(Map<String, String> m, int hierarchyLevel) {
            this.hierarchyLevel = hierarchyLevel;
            for (Map.Entry<String, String> entry : m.entrySet()) {
                switch (entry.getKey()) {
                    case "writtenRep":
                        this.name = entry.getValue();
                        break;
                    case "individual":
                        this.OWLname = entry.getValue();
                        break;
                    case "lang":
                        this.language = entry.getValue();
                        break;
                    case "type":
                        this.type = entry.getValue();
                        break;
                    case "verified":
                        this.verified = entry.getValue();
                        break;
                }
            }
        }

        public String getOWLname() {
            return OWLname;
        }

        public void setOWLname(String OWLname) {
            this.OWLname = OWLname;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public int getHierarchyLevel() {
            return hierarchyLevel;
        }

        public void setHierarchyLevel(int hierarchyLevel) {
            this.hierarchyLevel = hierarchyLevel;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVerified() {
            return verified;
        }

        public void setVerified(String verified) {
            this.verified = verified;
        }

    }

}
