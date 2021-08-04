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
import org.slf4j.event.Level;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import it.cnr.ilc.lexolite.LexOliteProperty;
import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.domain.Document;
import it.cnr.ilc.lexolite.manager.DocumentData;
import it.cnr.ilc.lexolite.manager.DocumentationManager;
import it.cnr.ilc.lexolite.manager.LanguageColorManager;
import it.cnr.ilc.lexolite.manager.LemmaData;
import it.cnr.ilc.lexolite.manager.SenseData;

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
    private LexiconControllerAttestation lexiconControllerAttestation;
    @Inject
    private LexiconControllerDictionary lexiconControllerDictionary;

    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LoginController loginController;
    @Inject
    private OntologyManager ontologyManager;
    @Inject
    private AccountManager accountManager;
    @Inject
    private LanguageColorManager languageColorManager;
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
    @Inject
    private LexiconControllerDocumentFilter lexiconControllerDocumentFilter;
    @Inject
    private LexiconControllerDocument lexiconControllerDocument;
    @Inject
    private DocumentationManager documentationManager;

    private String lemmaField;
    private String formField;
    private String senseField;
    private String docField;

    private Integer activeTab = 0;
    private String ontologyField;
    private Integer ontoCounter = 0;

    private final TreeNode lemmaRoot = new DefaultTreeNode("Root", null);
    private final TreeNode formRoot = new DefaultTreeNode("Root", null);
    private final TreeNode senseRoot = new DefaultTreeNode("Root", null);
    private final TreeNode ontoRoot = new DefaultTreeNode("Root", null);
    private final TreeNode docRoot = new DefaultTreeNode("Root", null);
    private TreeNode selection;

    private ArrayList<String> dynamicLexicaMenuItems = new ArrayList<>();
    private String lexiconLanguage;
    private String lemmaStartsWith;
    private boolean enabledFilter = false;

    private List<Map<String, String>> cachedLemmaList = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> cachedFormList = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> cachedSenseList = new ArrayList<Map<String, String>>();

    private List<DocumentData> cachedDocumentList = new ArrayList<DocumentData>();

    public String getDocField() {
        return docField;
    }

    public void setDocField(String docField) {
        this.docField = docField;
    }

    public Integer getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(Integer activeTab) {
        this.activeTab = activeTab;
    }

    public TreeNode getDocRoot() {
        return docRoot;
    }

    public String getDocCounter() {
        return Integer.toString(docRoot.getChildCount());
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
        initDocumentTabView();

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
            m.put("pos", getPosFromIndividual(m.get("individual"), lang));
            DataTreeNode dtn = new DataTreeNode(m, 0);
            lemmaRoot.getChildren().add(new DefaultTreeNode(dtn));
            System.out.println(m.get("writtenRep"));
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
        log(Level.INFO, loginController.getAccount(), "navigationEntry() Entry " + entry);

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
    }

    public void onSelect(NodeSelectEvent event) {
        boolean verified = ((DataTreeNode) event.getTreeNode().getData()).getVerified().equals("true");
        lexiconCreationControllerFormDetail.setVerified(verified);
        lexiconCreationControllerSenseDetail.setVerified(verified);
        long startTime = System.currentTimeMillis();
        resetPanels();
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        String entry = ((DataTreeNode) event.getTreeNode().getData()).getOWLname();
        log(Level.INFO, loginController.getAccount(), "nonSelect Entry:  " + entry);
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
                log(Level.INFO, loginController.getAccount(), "SELECT Lemma " + entry);
                // set breadcrumb
                lexiconCreationControllerFormDetail.setBreadCrumb(entryType, entry, lexiconCreationControllerFormDetail.getLemma().getFormWrittenRepr(),
                        Label.ClickProvenance.LEMMA_LIST_VIEW, event.getTreeNode().getRowKey());
                break;
            case "Form":
                log(Level.INFO, loginController.getAccount(), "SELECT Form " + entry);
                lexiconCreationControllerFormDetail.addForm(entry);
                // set breadcrumb
                lexiconCreationControllerFormDetail.setBreadCrumb(entryType, entry, ((DataTreeNode) event.getTreeNode().getData()).getName(),
                        Label.ClickProvenance.FORM_LIST_VIEW, event.getTreeNode().getRowKey());
                break;
            case "Sense":
                log(Level.INFO, loginController.getAccount(), "SELECT Sense " + entry);
                lexiconCreationControllerFormDetail.addForms(entry);
                break;
            default:
        }
        lexiconCreationControllerSenseDetail.setSenseRendered(true);
        lexiconCreationControllerSenseDetail.setAddSenseButtonDisabled(false);
        lexiconCreationControllerSenseDetail.setSenseToolbarRendered(true);
        lexiconCreationControllerSenseDetail.addSense(entry, entryType);
        lexiconControllerVarTransFormDetail.setVarTransRendered(false);
        lexiconControllerVarTransSenseDetail.setSenseVarTransRendered(false);
        lexiconControllerDocument.setDocRendered(false);
        // for dicitonary view purposes
        lexiconControllerVarTransSenseDetail.addSenseRelations();
        // -----
        lexiconControllerSynSemFormDetail.setSynSemRendered(false);
        lexiconControllerSynSemSenseDetail.setSenseSynSemRendered(false);
        checkForLock(entry);
        lexiconManager.getLexiconLocker().print();
        lexiconCreationControllerFormDetail.setActiveTab(0);
        lexiconControllerLexicalAspect.setRendered(true);

        lexiconControllerAttestation.setAttestationViewRendered(false);

        lexiconControllerDictionary.clearCaches();
        long endTime = System.currentTimeMillis();
        log(Level.INFO, loginController.getAccount(), "DURATA CONTROLLER CHE CONTIENE LE QUERIES: " + (endTime - startTime));

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
        lexiconControllerDocument.setDocRendered(false);
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
            case "Document":
                log(Level.INFO, loginController.getAccount(), "RESET Document Search Filter ");
                updateDocumentationCache();
                resetDocumentFilter();
                getFilteredDocumentationList(docRoot, cachedDocumentList, "", true);
                break;
            default:
        }
    }

    private void resetDocumentFilter() {
        lexiconControllerDocumentFilter.setDocType("All");
        lexiconControllerDocumentFilter.setAll(true);
        lexiconControllerDocumentFilter.setExternal(false);
        lexiconControllerDocumentFilter.setInternal(false);
        setDocField("");
    }

    private void getFilteredList(TreeNode tn, List<Map<String, String>> list, String keyFilter, String type) {
        tn.getChildren().clear();
        Collections.sort(list, new LexiconComparator("writtenRep"));
        if (!keyFilter.equals("")) {
            if ((lexiconCreationLemmaFilterController.isStartWith() && type.equals("Lemma"))
                    || ((lexiconCreationFormFilterController.isStartWith() && type.equals("Form")))
                    || ((lexiconCreationSenseFilterController.isStartWith() && type.equals("Sense")))) {
                for (Map<String, String> m : list) {
                    if ((m.get("writtenRep").toLowerCase().startsWith(keyFilter.toLowerCase())) && (!m.get("writtenRep").isEmpty())) {
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
                    if ((m.get("writtenRep").toLowerCase().contains(keyFilter.toLowerCase())) && (!m.get("writtenRep").isEmpty())) {
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

    public String getLanguageColor(String lang) {
        return "color: #" + languageColorManager.getLangColors().get(lang);
    }

    public String getPosFromIndividual(String individual, String lang) {
        String[] ret = individual.split("_" + lang + "_")[0].split("_");
        if (!ret[ret.length - 1].equals(Label.UNSPECIFIED_POS)) {
            return ret[ret.length - 1];
        } else {
            return "";
        }
    }

    public String getPosFromFormIndividual(String individual, String lang) {
        String[] ret = individual.split("_" + lang + "_")[0].split("_");
        if (!ret[ret.length - 1].equals(Label.UNSPECIFIED_POS)) {
            return ret[ret.length - 1];
        } else {
            return "";
        }
    }

    public void onDictionaryViewSelect(String entry, String entryType) {
//        boolean verified = ((DataTreeNode) event.getTreeNode().getData()).getVerified().equals("true");
//        lexiconCreationControllerFormDetail.setVerified(verified);
//        lexiconCreationControllerSenseDetail.setVerified(verified);
        resetPanels();
        log(Level.INFO, loginController.getAccount(), "onDictionaryViewSelect Entry:  " + entry);

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
                log(Level.INFO, loginController.getAccount(), "SELECT lemma " + entry + " on dictionary view");
                break;
            case "Form":
                log(Level.INFO, loginController.getAccount(), "SELECT form " + entry + " on dictionary view");
                lexiconCreationControllerFormDetail.addForm(entry);
                break;
            case "Sense":
                log(Level.INFO, loginController.getAccount(), "SELECT sense " + entry + " on dictionary view");
                lexiconCreationControllerFormDetail.addForms(entry);
                break;
            default:
        }
        lexiconCreationControllerSenseDetail.setSenseRendered(true);
        lexiconCreationControllerSenseDetail.setAddSenseButtonDisabled(false);
        lexiconCreationControllerSenseDetail.setSenseToolbarRendered(true);
        lexiconCreationControllerSenseDetail.addSense(entry, entryType);
        lexiconControllerVarTransFormDetail.setVarTransRendered(false);
        lexiconControllerVarTransSenseDetail.setSenseVarTransRendered(false);
        lexiconControllerDocument.setDocRendered(false);
        // for dicitonary view purposes
        lexiconControllerVarTransSenseDetail.addSenseRelations();
        // -----
        lexiconControllerSynSemFormDetail.setSynSemRendered(false);
        lexiconControllerSynSemSenseDetail.setSenseSynSemRendered(false);
        checkForLock(entry);
        lexiconManager.getLexiconLocker().print();
        // set breadcrumb
        lexiconCreationControllerFormDetail.setBreadCrumb(entryType, entry, lexiconCreationControllerFormDetail.getLemma().getFormWrittenRepr(),
                Label.ClickProvenance.DICTIONARY_VIEW, "");
        // dictionary tab needs to be selected
        if (loginController.getAccount().getType().getName().equals("Viewer")) {
            lexiconCreationControllerFormDetail.setActiveTab(0);
        } else {
            lexiconCreationControllerFormDetail.setActiveTab(1);
        }
        lexiconControllerLexicalAspect.setRendered(true);

    }

    public void onBreadCrumbSelect(String entry, String entryType, String prov) {
//        boolean verified = ((DataTreeNode) event.getTreeNode().getData()).getVerified().equals("true");
//        lexiconCreationControllerFormDetail.setVerified(verified);
//        lexiconCreationControllerSenseDetail.setVerified(verified);
        resetPanels();
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
                log(Level.INFO, loginController.getAccount(), "SELECT lemma " + entry + " on dictionary view");
                break;
            case "Form":
                log(Level.INFO, loginController.getAccount(), "SELECT form " + entry + " on dictionary view");
                lexiconCreationControllerFormDetail.addForm(entry);
                break;
            case "Sense":
                log(Level.INFO, loginController.getAccount(), "SELECT sense " + entry + " on dictionary view");
                lexiconCreationControllerFormDetail.addForms(entry);
                break;
            default:
        }
        lexiconCreationControllerSenseDetail.setSenseRendered(true);
        lexiconCreationControllerSenseDetail.setAddSenseButtonDisabled(false);
        lexiconCreationControllerSenseDetail.setSenseToolbarRendered(true);
        lexiconCreationControllerSenseDetail.addSense(entry, entryType);
        lexiconControllerVarTransFormDetail.setVarTransRendered(false);
        lexiconControllerVarTransSenseDetail.setSenseVarTransRendered(false);
        lexiconControllerDocument.setDocRendered(false);
        // for dicitonary view purposes
        lexiconControllerVarTransSenseDetail.addSenseRelations();
        // -----
        lexiconControllerSynSemFormDetail.setSynSemRendered(false);
        lexiconControllerSynSemSenseDetail.setSenseSynSemRendered(false);
        checkForLock(entry);
        lexiconManager.getLexiconLocker().print();
        lexiconControllerLexicalAspect.setRendered(true);
        
        if (loginController.getAccount().getType().getName().equals("Viewer")) {
            lexiconCreationControllerFormDetail.setActiveTab(0);
        } else {
            if (prov.equals(Label.ClickProvenance.DICTIONARY_VIEW.name())) {
                // dictionary tab needs to be selected
                lexiconCreationControllerFormDetail.setActiveTab(1);
            } else {
                // edit tab needs to be selected
                lexiconCreationControllerFormDetail.setActiveTab(0);
            }
        }
        lexiconControllerAttestation.setAttestationViewRendered(false);

    }

    public void onLinkedEntryByRelationSelect(Object entry, String relType) {
//        boolean verified = ((DataTreeNode) event.getTreeNode().getData()).getVerified().equals("true");
//        lexiconCreationControllerFormDetail.setVerified(verified);
//        lexiconCreationControllerSenseDetail.setVerified(verified);
        resetPanels();
        String _entry = "";
        lexiconControllerLexicalAspect.setLexicalAspectActive("Core");
        lexiconCreationControllerFormDetail.setNewAction(false);
        lexiconCreationControllerFormDetail.setLemmaRendered(true);
        lexiconCreationControllerFormDetail.setAddFormButtonDisabled(false);
        lexiconCreationControllerFormDetail.setFormAlreadyExists(false);
        lexiconCreationControllerFormDetail.setLemmAlreadyExists(false);
        lexiconCreationControllerFormDetail.setIsAdmissibleLemma(true);
        switch (relType) {
            case "Multiword component":
                _entry = ((LemmaData.Word) (entry)).getOWLName().replace("_entry", "_lemma");
                log(Level.INFO, loginController.getAccount(), "SELECT multiword component " + _entry);
                break;
            case "See Also":
                _entry = ((LemmaData.Word) (entry)).getOWLName().replace("_entry", "_lemma");
                log(Level.INFO, loginController.getAccount(), "SELECT see also " + _entry);
                break;
            case "Sense relation":
                _entry = ((SenseData.SenseRelation) (entry)).getWrittenRep().split("_sense")[0].concat("_lemma");
                log(Level.INFO, loginController.getAccount(), "SELECT sense relation " + _entry);
                break;
            case "Reified sense relation":
                _entry = ((SenseData.ReifiedSenseRelation) (entry)).getTarget().split("_sense")[0].concat("_lemma");
                log(Level.INFO, loginController.getAccount(), "SELECT reified sense relation " + _entry);
                break;
            case "Translation reified relation":
                _entry = ((SenseData.ReifiedTranslationRelation) (entry)).getTarget().split("_sense")[0].concat("_lemma");
                log(Level.INFO, loginController.getAccount(), "SELECT reified translation " + _entry);
                break;
            case "Lexical relation":
                _entry = ((LemmaData.LexicalRelation) (entry)).getOWLName().replace("_entry", "_lemma");
                log(Level.INFO, loginController.getAccount(), "SELECT lexical relation " + _entry);
                break;
            case "Reified lexical relation":
                _entry = ((LemmaData.ReifiedLexicalRelation) (entry)).getTargetOWLName().replace("_entry", "_lemma");
                log(Level.INFO, loginController.getAccount(), "SELECT reified lexical relation " + _entry);
                break;
//            case "Lexical function":
//                log(Level.INFO, loginController.getAccount(), "SELECT lexical function " + entry);
//                lexiconCreationControllerFormDetail.addForms(entry);
//                break;
            default:
        }
        lexiconCreationControllerFormDetail.addLemma(_entry);
        lexiconCreationControllerSenseDetail.addSense(_entry, "Lemma");
        lexiconCreationControllerSenseDetail.setSenseRendered(true);
        lexiconCreationControllerSenseDetail.setAddSenseButtonDisabled(false);
        lexiconCreationControllerSenseDetail.setSenseToolbarRendered(true);
        lexiconControllerVarTransFormDetail.setVarTransRendered(false);
        lexiconControllerVarTransSenseDetail.setSenseVarTransRendered(false);
        lexiconControllerDocument.setDocRendered(false);
        // for dicitonary view purposes
        lexiconControllerVarTransSenseDetail.addSenseRelations();
        // -----
        lexiconControllerSynSemFormDetail.setSynSemRendered(false);
        lexiconControllerSynSemSenseDetail.setSenseSynSemRendered(false);
        checkForLock(_entry);
        lexiconManager.getLexiconLocker().print();
        lexiconCreationControllerFormDetail.setActiveTab(0);
        lexiconControllerLexicalAspect.setRendered(true);

        // set breadcrumb
        lexiconCreationControllerFormDetail.setBreadCrumb("Lemma", _entry, lexiconCreationControllerFormDetail.getLemma().getFormWrittenRepr(),
                Label.ClickProvenance.LEMMA_LIST_VIEW, "");

    }

    // invoked when the user types a char
    public void docKeyupFilterEvent(AjaxBehaviorEvent e) {
        String keyFilter = (String) e.getComponent().getAttributes().get("value");
        getFilteredDocumentationList(docRoot, cachedDocumentList, keyFilter.toLowerCase(), false);
    }

    private void getFilteredDocumentationList(TreeNode tn, List<DocumentData> ddList, String keyFilter, boolean reset) {
        tn.getChildren().clear();
        keyFilter = (keyFilter == null) ? "" : keyFilter;
        if (lexiconControllerDocumentFilter.isStartWith()) {
            for (DocumentData dd : ddList) {
                if (dd.getAbbreviation().toLowerCase().startsWith(keyFilter) || keyFilter.equals("")) {
                    if (isDocumentFilterable(dd) || reset) {
                        DocTreeNode dtn = new DocTreeNode(dd.getDocId(), dd.getAbbreviation(), dd.getType(), dd.getTitle(), dd.getSourceType(), 0);
                        tn.getChildren().add(new DefaultTreeNode(dtn));
                    }
                }
            }
        }
        if (lexiconControllerDocumentFilter.isContains()) {
            for (DocumentData dd : ddList) {
                if (dd.getAbbreviation().toLowerCase().contains(keyFilter) || keyFilter.equals("")) {
                    if (isDocumentFilterable(dd) || reset) {
                        DocTreeNode dtn = new DocTreeNode(dd.getDocId(), dd.getAbbreviation(), dd.getType(), dd.getTitle(), dd.getSourceType(), 0);
                        tn.getChildren().add(new DefaultTreeNode(dtn));
                    }
                }
            }
        }
        log(Level.INFO, loginController.getAccount(), "SEARCH performed about document " + lexiconControllerDocumentFilter.getDocType() + " type - "
                + "external: " + Boolean.toString(lexiconControllerDocumentFilter.isExternal()) + " - "
                + "internal: " + Boolean.toString(lexiconControllerDocumentFilter.isInternal()) + " - "
                + "all: " + Boolean.toString(lexiconControllerDocumentFilter.isAll()) + " - "
                + (lexiconControllerDocumentFilter.isStartWith() ? "startsWith: " : "") + keyFilter
                + (lexiconControllerDocumentFilter.isContains() ? "contains: " : "") + keyFilter);
    }

    private boolean isDocumentFilterable(DocumentData dd) {
        if (lexiconControllerDocumentFilter.isAll()) {
            // both internal and external documents are required
            if (lexiconControllerDocumentFilter.getDocType().equals("All")) {
                // all document types are required
                return true;
            } else {
                return dd.getType().equals(lexiconControllerDocumentFilter.getDocType());
            }
        } else {
            if (lexiconControllerDocumentFilter.getDocType().equals("All")) {
                // all document types are required
                if (lexiconControllerDocumentFilter.isExternal()) {
                    return dd.getSourceType().equals("External");
                } else {
                    return dd.getSourceType().equals("Internal");
                }
            } else {
                if (lexiconControllerDocumentFilter.isExternal()) {
                    return dd.getSourceType().equals("External") && dd.getType().equals(lexiconControllerDocumentFilter.getDocType());
                } else {
                    return dd.getSourceType().equals("Internal") && dd.getType().equals(lexiconControllerDocumentFilter.getDocType());
                }
            }
        }
    }

    // invoked when the user select the filter mode (starts with or contains)
    public void docKeyupFilterEvent(String keyFilter) {
        getFilteredDocumentationList(docRoot, cachedDocumentList, keyFilter, false);
    }

    private void updateDocumentationCache() {
        cachedDocumentList.clear();
        for (Document d : documentationManager.getDocuments()) {
            cachedDocumentList.add(new DocumentData(d));
        }
    }

    public void initDocumentTabView() {
        docRoot.getChildren().clear();
        updateDocumentationCache();
        for (DocumentData d : cachedDocumentList) {
            DocTreeNode dtn = new DocTreeNode(d.getDocId(), d.getAbbreviation(), d.getType(), d.getTitle(), d.getSourceType(), 0);
            docRoot.getChildren().add(new DefaultTreeNode(dtn));
        }
    }

    public void onDocumentationSelect(NodeSelectEvent event) {
        resetPanels();
        DocTreeNode dtn = ((DocTreeNode) event.getTreeNode().getData());
        lexiconControllerDocument.setNewAction(false);
        lexiconControllerDocument.setDocRendered(true);
        lexiconControllerDocument.setDocAlreadyExists(false);
        lexiconControllerDocument.addDocument(dtn);
        log(Level.INFO, loginController.getAccount(), "SELECT Document " + dtn.getAbbreviation());
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

    public static class DocTreeNode {

        private int hierarchyLevel;
        private Long docId;
        private String abbreviation;
        private String type;
        private String title;
        private String sourceType;

        public DocTreeNode(Long docId, String abbreviation, String type, String title, String sourceType, int hierarchyLevel) {
            this.docId = docId;
            this.abbreviation = abbreviation;
            this.hierarchyLevel = hierarchyLevel;
            this.sourceType = sourceType;
            this.type = type;
            this.title = title;
        }

        // only for manuscripts
        public DocTreeNode(Long docId, String abbreviation, String title, int hierarchyLevel) {
            this.docId = docId;
            this.abbreviation = abbreviation;
            this.hierarchyLevel = hierarchyLevel;
            this.sourceType = "Internal";
            this.type = "Manuscript";
            this.title = title;
        }

        public Long getDocId() {
            return docId;
        }

        public void setDocId(Long docId) {
            this.docId = docId;
        }

        public int getHierarchyLevel() {
            return hierarchyLevel;
        }

        public void setHierarchyLevel(int hierarchyLevel) {
            this.hierarchyLevel = hierarchyLevel;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public void setAbbreviation(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

    }

}
