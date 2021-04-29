/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.constant.OntoLexEntity;
import it.cnr.ilc.lexolite.domain.Authoring;
import it.cnr.ilc.lexolite.domain.ExtensionAttribute;
import it.cnr.ilc.lexolite.manager.AccountManager;
import it.cnr.ilc.lexolite.manager.AttestationManager;
import it.cnr.ilc.lexolite.manager.AttestationRenaming;
import it.cnr.ilc.lexolite.manager.AuthoringManager;
import it.cnr.ilc.lexolite.manager.DomainManager;
import it.cnr.ilc.lexolite.manager.ExtensionAttributeManager;
import it.cnr.ilc.lexolite.manager.FormData;
import it.cnr.ilc.lexolite.manager.ImageManager;
import it.cnr.ilc.lexolite.manager.LemmaData;
import it.cnr.ilc.lexolite.manager.LemmaData.ExtensionAttributeIstance;
import it.cnr.ilc.lexolite.manager.LemmaData.LexicalRelation;
import it.cnr.ilc.lexolite.manager.LemmaData.Openable;
import it.cnr.ilc.lexolite.manager.LemmaData.Word;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import it.cnr.ilc.lexolite.manager.SenseData;
import it.cnr.ilc.lexolite.util.LexiconUtil;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.FileUtils;
import org.slf4j.event.Level;
import org.primefaces.event.MenuActionEvent;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSeparator;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuModel;
import org.primefaces.model.menu.Separator;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerFormDetail extends BaseController implements Serializable {

    @Inject
    private PropertyValue propertyValue;
    @Inject
    private LexiconControllerTabViewList lexiconCreationControllerTabViewList;
    @Inject
    private LexiconControllerSenseDetail lexiconCreationViewSenseDetail;
    @Inject
    private LexiconControllerVarTransFormDetail lexiconControllerVarTransFormDetail;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LoginController loginController;
    @Inject
    private AuthoringManager authoringManager;
    @Inject
    private ExtensionAttributeManager extensionAttributeManager;
    @Inject
    private ImageManager imageManager;
    @Inject
    private AttestationManager attestationManager;

    private LemmaData lemma = new LemmaData();
    private LemmaData lemmaCopy = new LemmaData();

    private List<Map<String, String>> lemmaListCached = new ArrayList();

    private final ArrayList<FormData> forms = new ArrayList();
    private final ArrayList<FormData> formsCopy = new ArrayList();

    private final String LemmaOfMultiwordNotFound = "";

    private boolean lemmaRendered = false;

    private int activeTab = 0;

    private boolean newAction = false;
    private boolean newFormAction = false;
    private boolean lemmAlreadyExists = false;
    private boolean isAdmissibleLemma = true;
    private boolean formAlreadyExists = false;
    private boolean addFormButtonDisabled = true;

    private ArrayList<ExtensionAttribute> extensionAttributeList = new ArrayList();
    private boolean verified = false;

    private boolean locked = false;
    private String locker = "";

    private final String MULTIWORD_COMPONENT_REGEXP = "([0-9]*[#|\\*]*)*";
//    private final String ADMISSIBLE_WORD_REGEXP = "^[aA-zZ]+[0-9]*"; 
    //    private final String ADMISSIBLE_MULTIWORD_REGEXP = "^[aA-zZ]+";
    //private final String ADMISSIBLE_WORD_REGEXP = "^[^\\*\\.\\#\\(\\)\\[\\]\\{\\};:,\\/=\\+\\-_\\\\\\?\\!\"%&0-9\\s]+[0-9]*$";
    private final String ADMISSIBLE_WORD_REGEXP = "^[^\\^\\°\\|\\*\\.\\#\\(\\)\\[\\]\\{\\};:,\\/=\\+_\\\\\\?\\!\"%&\\d\\s]+\\d*$";
    private final String ADMISSIBLE_MULTIWORD_REGEXP = "^[^\\^\\°\\|\\*\\.\\#\\(\\)\\[\\]\\{\\};:,\\/=\\+_\\\\\\?\\!\"%&\\d]+$";
    //writtenrep (pos)@lang
    private final String MULTIWORD_COMPONENT_INDIVIDUAL_REGEXP = "([aA-zZ]+)\\s\\(([aA-zZ]+)\\)@([aA-zZ]+)";

    private final MenuModel breadCrumbModel = new DefaultMenuModel();
    private final int breadCrumbWindowSize = 15;

    public int getBreadCrumbWindowSize() {
        return breadCrumbWindowSize;
    }

    public MenuModel getBreadCrumbModel() {
        return breadCrumbModel;
    }

    private void addItemToBreadCrumbModel(MenuModel model, String type, String value, String uri, boolean disabled, int id, Enum prov, String rowKey) {
        if (!isInBreadCrumb(model, uri)) {
            DefaultMenuItem element = new DefaultMenuItem();
            if (prov.equals(Label.ClickProvenance.DICTIONARY_VIEW)) {
                element.setIcon("fa fa-file-text-o");
            } else {
                element.setIcon("fa fa-th-list");
                if (prov.equals(Label.ClickProvenance.LEMMA_LIST_VIEW)) {
                    element.setStyle("color: #b74c4c;");
                } else {
                    element.setStyle("color: #7286ad;");
                }
            }
            element.setId(String.valueOf(id));
            element.setValue(value);
            element.setDisabled(disabled);
            element.setUpdate(":editViewTab :breadCrumb");
            element.setCommand("#{lexiconControllerTabViewList.onBreadCrumbSelect('" + uri + "', '" + type + "', '" + prov + "')}");
            //element.setOnstart("PF('loadingDialog').show();");
            element.setOnclick("PF('lemmaTreeVar').unselectAllNodes(); PF('lemmaTreeVar').selectNode(PF('lemmaTreeVar').container.children(\"li:eq(" +rowKey+ ")\"))");
            //element.setOncomplete("setHeight();PF('loadingDialog').hide()");
            element.setOncomplete("setHeight();");
            model.getElements().add(element);
            model.generateUniqueIds();
        }
    }

    private boolean isInBreadCrumb(MenuModel model, String uri) {
        boolean found = false;
        for (MenuElement me : model.getElements()) {
            if (((DefaultMenuItem) me).getCommand().contains(uri)) {
                found = true;
                break;
            }
        }
        return found;
    }

    private void slideItemToBreadCrumbModel(MenuModel model, String type, String value, String uri, boolean disabled, int id, Enum prov, String rowKey) {
        DefaultMenuItem element = new DefaultMenuItem();
        if (prov.equals(Label.ClickProvenance.DICTIONARY_VIEW)) {
            element.setIcon("fa fa-file-text-o");
        } else {
            element.setIcon("fa fa-th-list");
            if (prov.equals(Label.ClickProvenance.LEMMA_LIST_VIEW)) {
                element.setStyle("color: #b74c4c;");
            } else {
                element.setStyle("color: #7286ad;");
            }
        }
        element.setId(String.valueOf(id));
        element.setValue(value);
        element.setDisabled(disabled);
        element.setUpdate(":editViewTab :breadCrumb");
        element.setCommand("#{lexiconControllerTabViewList.onBreadCrumbSelect('" + uri + "', '" + type + "', '" + prov + "')}");
        element.setOnstart("PF('loadingDialog').show();");
        element.setOncomplete("setHeight();PF('loadingDialog').hide()");
        model.getElements().add(element);
        model.generateUniqueIds();
        model.getElements().remove(0);
        for (MenuElement me : model.getElements()) {
            me.setId(String.valueOf(Integer.parseInt(me.getId()) - 1));
        }
    }

    /**
     * 
     * @param entryType
     * @param entryUri
     * @param entry
     * @param prov
     * @param rowKey the number of the treenode selected
     */
    public void setBreadCrumb(String entryType, String entryUri, String entry, Enum prov, String rowKey) {
        if (breadCrumbModel.getElements().size() < breadCrumbWindowSize) {
            addItemToBreadCrumbModel(breadCrumbModel, entryType, entry, entryUri, false, breadCrumbModel.getElements().size(), prov, rowKey);
        } else {
            slideItemToBreadCrumbModel(breadCrumbModel, entryType, entry, entryUri, false, breadCrumbModel.getElements().size(), prov, rowKey);
        }
    }

    private void updateBreadCrumb(String type, String value, String oldUri, String newUri, boolean disabled, Enum prov) {
        for (MenuElement me : breadCrumbModel.getElements()) {
            if (((DefaultMenuItem) me).getCommand().contains(oldUri)) {
                ((DefaultMenuItem) me).setValue(value);
                ((DefaultMenuItem) me).setDisabled(disabled);
                ((DefaultMenuItem) me).setUpdate(":editViewTab :breadCrumb");
                ((DefaultMenuItem) me).setCommand("#{lexiconControllerTabViewList.onBreadCrumbSelect('" + newUri + "', '" + type + "', '" + prov + "')}");
                ((DefaultMenuItem) me).setOnstart("PF('loadingDialog').show();");
                ((DefaultMenuItem) me).setOncomplete("setHeight();PF('loadingDialog').hide()");
                if (prov.equals(Label.ClickProvenance.DICTIONARY_VIEW)) {
                    ((DefaultMenuItem) me).setIcon("fa fa-file-text-o");
                } else {
                    ((DefaultMenuItem) me).setIcon("fa fa-th-list");
                    if (prov.equals(Label.ClickProvenance.LEMMA_LIST_VIEW)) {
                        ((DefaultMenuItem) me).setStyle("color: #b74c4c;");
                    } else {
                        ((DefaultMenuItem) me).setStyle("color: #7286ad;");
                    }
                }
                break;
            }
        }
    }

    private void removeFromBreadCrumb(String uri) {
        MenuModel _breadCrumbModel = new DefaultMenuModel();
        boolean found = false;
        for (MenuElement me : breadCrumbModel.getElements()) {
            if (!((DefaultMenuItem) me).getCommand().contains(uri)) {
                _breadCrumbModel.getElements().add(me);
            } else {
                found = true;
            }
        }
        if (found) {
            breadCrumbModel.getElements().clear();
            for (MenuElement me : _breadCrumbModel.getElements()) {
                breadCrumbModel.getElements().add(me);
            }
        }
    }

    public boolean isBreadCrumbRenderable() {
        return (lemma.getFormWrittenRepr() != null);
    }

    public boolean isUserEnable() {
        return loginController.getAccount().getType().getName().equals(AccountManager.ADMINISTRATOR);
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getLocker() {
        return locker;
    }

    public void setLocker(String locker) {
        this.locker = locker;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public List<FormData> getForms() {
        return forms;
    }

    public ArrayList<FormData> getFormsCopy() {
        return formsCopy;
    }

    public int getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(int activeTab) {
        this.activeTab = activeTab;
    }

    public ArrayList<ExtensionAttribute> getExtensionAttributeList() {
        extensionAttributeList = (ArrayList<ExtensionAttribute>) extensionAttributeManager.loadActiveExtensionsAttribute();
        return extensionAttributeList;
    }

    public void setExtensionAttributeList(ArrayList<ExtensionAttribute> extensionAttributeList) {
        this.extensionAttributeList = extensionAttributeList;
    }

    public LemmaData getLemma() {
        return lemma;
    }

    public LemmaData getLemmaCopy() {
        return lemmaCopy;
    }

    public boolean isNewFormAction() {
        return newFormAction;
    }

    public void setNewFormAction(boolean newFormAction) {
        this.newFormAction = newFormAction;
    }

    public boolean isNewAction() {
        return newAction;
    }

    public void setNewAction(boolean newAction) {
        this.newAction = newAction;
    }

    public boolean isLemmAlreadyExists() {
        return lemmAlreadyExists;
    }

    public void setLemmAlreadyExists(boolean lemmAlreadyExists) {
        this.lemmAlreadyExists = lemmAlreadyExists;
    }

    public boolean isIsAdmissibleLemma() {
        return isAdmissibleLemma;
    }

    public void setIsAdmissibleLemma(boolean isAdmissibleLemma) {
        this.isAdmissibleLemma = isAdmissibleLemma;
    }

    public boolean isFormAlreadyExists() {
        return formAlreadyExists;
    }

    public void setFormAlreadyExists(boolean formAlreadyExists) {
        this.formAlreadyExists = formAlreadyExists;
    }

    public boolean isAddFormButtonDisabled() {
        return addFormButtonDisabled;
    }

    public void setAddFormButtonDisabled(boolean addFormButtonDisabled) {
        this.addFormButtonDisabled = addFormButtonDisabled;
    }

    public boolean isLemmaRendered() {
        return lemmaRendered;
    }

    public void setLemmaRendered(boolean lemmaRendered) {
        this.lemmaRendered = lemmaRendered;
    }

    public String getLemmaOfMultiwordNotFound() {
        return LemmaOfMultiwordNotFound;
    }

    public String getEntryErrorLabel() {
        if ((lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel()) || lemma.getType().equals(OntoLexEntity.Class.AFFIX.getLabel()))
                && (lemma.getFormWrittenRepr().contains(" "))) {
            return "Word can not contain spaces";
        } else {
            if (!isAdmissibleLemma) {
                return "Only letters are admissible. <br/> For homonyms you can concatenate a number <br/> to the written representation";
            } else {
                return "Lemma already exists";
            }
        }
    }

    public String getCommentIcon() {
        if (lemma.getNote().length() > 0) {
            return "fa fa-comment";
        } else {
            return "fa fa-comment-o";
        }
    }

    public String getCommentIcon(FormData fd) {
        if (fd.getNote().length() > 0) {
            return "fa fa-comment";
        } else {
            return "fa fa-comment-o";
        }
    }

    public boolean isRenderedEntryErrorLabel() {
        if ((lemmAlreadyExists)
                || (lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel()) || lemma.getType().equals(OntoLexEntity.Class.AFFIX.getLabel()))
                && (lemma.getFormWrittenRepr().contains(" "))
                || (!isAdmissibleLemma)) {
            return true;
        }
        return false;
    }

    public String emptyMessage(String text, String emptyMessage) {
        return emptyMessage(text, text, emptyMessage);
    }

    public String emptyMessage(String test, String text, String emptyMessage) {
        return test == null || test.equals("") ? emptyMessage : text;
    }

    public void openNote() {
        log(Level.INFO, loginController.getAccount(), "OPEN note of Lemma " + lemma.getFormWrittenRepr());
    }

    public void openNote(String form) {
        log(Level.INFO, loginController.getAccount(), "OPEN note of Form " + form);
    }

    public void saveNote() throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "EDIT note of Lemma " + lemma.getFormWrittenRepr() + " in " + lemma.getNote());
        lexiconManager.saveLemmaNote(lemma, lemmaCopy.getNote());
        lemmaCopy.setNote(lemma.getNote());
        if (lemma.getNote().isEmpty()) {
            authoringManager.removeAuthoring(Authoring.IRIType.LEXICAL_ENTRY_NOTE, lemma.getIndividual());
        } else {
            authoringManager.updateIRIAuthoring(loginController.getAccount(), Authoring.IRIType.LEXICAL_ENTRY_NOTE, lemma.getIndividual());
        }
        info("template.message.saveLemmaNote.summary", "template.message.saveLemmaNote.description");
    }

    public void saveNote(FormData fd) throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "EDIT note of Form " + fd.getFormWrittenRepr() + " in " + fd.getNote());
        int order = forms.indexOf(fd);
        lexiconManager.saveFormNote(fd, formsCopy.get(order).getNote());
        formsCopy.get(order).setNote(fd.getNote());
        if (fd.getNote().isEmpty()) {
            authoringManager.removeAuthoring(Authoring.IRIType.FORM_NOTE, fd.getIndividual());
        } else {
            authoringManager.updateIRIAuthoring(loginController.getAccount(), Authoring.IRIType.FORM_NOTE, fd.getIndividual());
        }
        info("template.message.saveFormNote.summary", "template.message.saveFormNote.description");
    }

    public void closeNote() {
        log(Level.INFO, loginController.getAccount(), "CLOSE note of Lemma " + lemma.getFormWrittenRepr());
    }

    public void closeNote(String form) {
        log(Level.INFO, loginController.getAccount(), "CLOSE note of Form " + form);
    }

    // invoked by the controller after an user selected a lemma in the tabview
    public void addLemma(String lemma) {
        this.lemma.clear();
        this.lemma = lexiconManager.getLemmaAttributes(lemma, propertyValue.getMorphoTrait(), getExtensionAttributeList());
        ArrayList<FormData> al = lexiconManager.getFormsOfLemma(lemma, this.lemma.getLanguage(), propertyValue.getMorphoTrait(), getExtensionAttributeList());
        forms.clear();
        forms.addAll(al);
        createLemmaCopy();
        addFormCopy(al);
    }

    // for keeping track of modifies
    private void createLemmaCopy() {
        this.lemmaCopy.setFormWrittenRepr(lemma.getFormWrittenRepr());
        this.lemmaCopy.setFormPhoneticRep(lemma.getFormPhoneticRep());
        this.lemmaCopy.setMorphoTraits(copyMorphData(lemma.getMorphoTraits()));
        this.lemmaCopy.setLanguage(lemma.getLanguage());
        this.lemmaCopy.setPoS(lemma.getPoS());
        this.lemmaCopy.setIndividual(lemma.getIndividual());
        this.lemmaCopy.setType(lemma.getType());
        this.lemmaCopy.setNote(lemma.getNote());
        this.lemmaCopy.setVerified(lemma.isVerified());
        this.lemmaCopy.setSeeAlso(copyWordData(lemma.getSeeAlso()));
        this.lemmaCopy.setMultiword(copyWordData(lemma.getMultiword()));
        this.lemmaCopy.setExtensionAttributeInstances(copyExtensionAttributeInstances(lemma.getExtensionAttributeInstances()));
        this.lemmaCopy.setLexRels(copyLexicalRelationData(lemma.getLexRels()));
        this.lemmaCopy.setValid(lemma.getValid());

    }

    private ArrayList<LemmaData.MorphoTrait> copyMorphData(ArrayList<LemmaData.MorphoTrait> almt) {
        ArrayList<LemmaData.MorphoTrait> _almt = new ArrayList<>();
        for (LemmaData.MorphoTrait mt : almt) {
            LemmaData.MorphoTrait _mt = new LemmaData.MorphoTrait();
            _mt.setName(mt.getName());
            _mt.setSchema(mt.getSchema());
            _mt.setValue(mt.getValue());
            _almt.add(_mt);
        }
        return _almt;
    }

    private ArrayList<LemmaData.ExtensionAttributeIstance> copyExtensionAttributeInstances(ArrayList<LemmaData.ExtensionAttributeIstance> aleai) {
        ArrayList<LemmaData.ExtensionAttributeIstance> _aleai = new ArrayList();
        for (LemmaData.ExtensionAttributeIstance eai : aleai) {
            LemmaData.ExtensionAttributeIstance _eai = new LemmaData.ExtensionAttributeIstance();
            _eai.setName(eai.getName());
            _eai.setType(eai.getType());
            _eai.setLabel(eai.getLabel());
            _eai.setValue(eai.getValue());
            _eai.setDisabled(eai.isDisabled());
            _aleai.add(_eai);
        }
        return _aleai;
    }

    private ArrayList<Word> copyWordData(ArrayList<Word> alw) {
        ArrayList<Word> _alw = new ArrayList();
        for (Word w : alw) {
            Word _w = new Word();
            _w.setOWLName(w.getOWLName());
            _w.setLanguage(w.getLanguage());
            _w.setWrittenRep(w.getWrittenRep());
            _w.setOWLComp(w.getOWLComp());
            _w.setLabel(w.getLabel());
            _alw.add(_w);
        }
        return _alw;
    }

    private ArrayList<LexicalRelation> copyLexicalRelationData(ArrayList<LexicalRelation> alw) {
        ArrayList<LexicalRelation> _alw = new ArrayList();
        for (LexicalRelation w : alw) {
            LexicalRelation _w = new LexicalRelation();
            _w.setOWLName(w.getOWLName());
            _w.setLanguage(w.getLanguage());
            _w.setWrittenRep(w.getWrittenRep());
            _w.setRelation(w.getRelation());
            _alw.add(_w);
        }
        return _alw;
    }

    private void addFormCopy(FormData fd) {
        formsCopy.add(0, copyFormData(fd));
    }

    private void updateFormCopy(FormData fd, int order) {
        formsCopy.remove(order);
        formsCopy.add(order, copyFormData(fd));
    }

    private void addFormCopy(ArrayList<FormData> fdList) {
        formsCopy.clear();
        for (FormData fd : fdList) {
            formsCopy.add(copyFormData(fd));
        }
    }

    private void removeFormCopy(int order) {
        formsCopy.remove(order);
    }

    private FormData copyFormData(FormData fd) {
        FormData _fd = new FormData();
        _fd.setFormWrittenRepr(fd.getFormWrittenRepr());
        _fd.setFormPhoneticRep(fd.getFormPhoneticRep());
        _fd.setMorphoTraits(copyFormMorphoTraits(fd.getMorphoTraits()));
        _fd.setExtensionAttributeInstances(copyExtensionAttributeInstances(fd.getExtensionAttributeInstances()));
        _fd.setNote(fd.getNote());
        _fd.setLanguage(fd.getLanguage());
        _fd.setIndividual(fd.getIndividual());
        return _fd;
    }

    private ArrayList<LemmaData.MorphoTrait> copyFormMorphoTraits(ArrayList<LemmaData.MorphoTrait> almt) {
        ArrayList<LemmaData.MorphoTrait> _almt = new ArrayList<>();
        for (LemmaData.MorphoTrait mt : almt) {
            LemmaData.MorphoTrait _mt = new LemmaData.MorphoTrait();
            _mt.setName(mt.getName());
            _mt.setSchema(mt.getValue());
            _mt.setValue(mt.getValue());
            _almt.add(_mt);
        }
        return _almt;
    }

    // invoked by an user that adds a new form of a lemma
    public void addForm() {
        log(Level.INFO, loginController.getAccount(), "ADD empty Form box");
        FormData fd = new FormData();
        setFormDefaultValues(fd);
        forms.add(0, fd);
        addFormCopy(fd);
        setNewFormAction(true);
    }

    private void setFormDefaultValues(FormData fd) {
        fd.setLanguage(lemma.getLanguage());
        fd.setFormWrittenRepr("");
    }

    // invoked by the controller after an user selected a form in the tabview
    public void addForm(String form) {
        this.lemma.clear();
        this.lemma = lexiconManager.getLemmaEntry(form, propertyValue.getMorphoTrait(), getExtensionAttributeList());
        ArrayList<FormData> al = lexiconManager.getFormsOfLemma(getLemma().getIndividual(), this.lemma.getLanguage(), propertyValue.getMorphoTrait(), getExtensionAttributeList());
        forms.clear();
        forms.addAll(al);
        createLemmaCopy();
        addFormCopy(al);

    }

    // invoked by the controller after an user selected a sense in the tabview
    public void addForms(String sense) {
        this.lemma.clear();
        this.lemma = lexiconManager.getLemmaOfSense(sense, propertyValue.getMorphoTrait());
        ArrayList<FormData> al = lexiconManager.getFormsOfLemma(getLemma().getIndividual(), this.lemma.getLanguage(), propertyValue.getMorphoTrait(), getExtensionAttributeList());
        forms.clear();
        forms.addAll(al);
        createLemmaCopy();
        addFormCopy(al);
    }

    // invoked by the controller after an user selected add sense to lemma
    public void addSense() throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "ADD empty Sense box");
        lexiconCreationViewSenseDetail.saveSense(lemma);
        lexiconCreationViewSenseDetail.setSenseToolbarRendered(true);
    }

    public void removeForm(FormData fd) throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "DELETE Form " + fd.getFormWrittenRepr());
        lexiconManager.deleteForm(fd);
        authoringManager.removeAuthoring(Authoring.IRIType.FORM, fd.getIndividual());
        info("template.message.deleteForm.summary", "template.message.deleteForm.description", fd.getFormWrittenRepr());
        // remove the form from forms and copyForms
        int order = forms.indexOf(fd);
        forms.remove(fd);
        removeFormCopy(order);
        removeFromBreadCrumb(fd.getIndividual());
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        lexiconCreationControllerTabViewList.initFormTabView(currentLanguage);
    }

    public void saveForm(FormData fd) throws IOException, OWLOntologyStorageException {
        fd.setSaveButtonDisabled(true);
        fd.setDeleteButtonDisabled(false);
        int order = forms.indexOf(fd);
        if (formsCopy.get(order).getFormWrittenRepr() == null || formsCopy.get(order).getFormWrittenRepr().isEmpty()) {
            // saving due to new form action
            log(Level.INFO, loginController.getAccount(), "SAVE new Form " + fd.getFormWrittenRepr());
            lexiconManager.saveForm(fd, lemma);
        } else {
            // saving due to a form modification action
            if (isSameWrittenRep(fd.getFormWrittenRepr(), order)) {
                // modification does not concern form written representation
                log(Level.INFO, loginController.getAccount(), "SAVE updated Form " + fd.getFormWrittenRepr());
                lexiconManager.updateForm(formsCopy.get(order), fd, lemma);
            } else {
                // modification concerns also form written representation
                log(Level.INFO, loginController.getAccount(), "SAVE updated Form with renaming from " + formsCopy.get(order).getFormWrittenRepr() + " to " + fd.getFormWrittenRepr());
                String newInstanceName = lexiconManager.saveFormWithIRIRenaming(formsCopy.get(order), fd, lemma);
                updateBreadCrumb("Form", fd.getFormWrittenRepr(), fd.getIndividual(), newInstanceName, false, Label.ClickProvenance.FORM_LIST_VIEW);
            }
        }
        info("template.message.saveForm.summary", "template.message.saveForm.description", fd.getFormWrittenRepr());
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        lexiconCreationControllerTabViewList.initFormTabView(currentLanguage);
        updateFormCopy(fd, order);
        setNewFormAction(false);
        authoringManager.updateIRIAuthoring(loginController.getAccount(), Authoring.IRIType.FORM, fd.getIndividual());
    }

    private boolean isSameWrittenRep(String wr, int order) {
        if (formsCopy.get(order).getFormWrittenRepr().equals(wr)) {
            return true;
        } else {
            return false;
        }
    }

    public void formNameKeyupEvent(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        FormData fd = (FormData) component.getAttributes().get("form");
        String formPart = ((String) e.getComponent().getAttributes().get("value"));
        String currentLanguage = lemma.getLanguage();
        List<Map<String, String>> formList = lexiconManager.formsList(currentLanguage);
        if (contains(formList, formPart, lemma.getLanguage(), lemma.getPoS())
                && (isSameLemma(formList, formPart))
                && (!isSameForm(fd, formPart))) {
            formAlreadyExists = true;
            fd.setSaveButtonDisabled(true);
        } else {
            formAlreadyExists = false;
            fd.setSaveButtonDisabled(false);
        }
        log(Level.INFO, loginController.getAccount(), "UPDATE Form name from " + fd.getFormWrittenRepr() + " to " + formPart);
        fd.setFormWrittenRepr(formPart);
        fd.setSaveButtonDisabled(isSavableForm(fd));
    }

    private boolean isSameForm(FormData fd, String part) {
        int order = forms.indexOf(fd);
        if (formsCopy.get(order).getFormWrittenRepr() == null) {
            return false;
        } else {
            if (!formsCopy.get(order).getFormWrittenRepr().equals(part)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public void lemmaNameKeyupEvent(String lemmaPart, List<Map<String, String>> lemmaList) {
        if (contains(lemmaList, lemmaPart, lemma.getLanguage(), lemma.getPoS()) && (!lemmaPart.equals(lemmaCopy.getFormWrittenRepr()))) {
            lemmAlreadyExists = true;
            /**/ isAdmissibleLemma = true;
            lemma.setSaveButtonDisabled(true);
        } else {
            /**/ if (isAdmissibleLemma(lemmaPart, lemma.getType())) {
                lemmAlreadyExists = false;
                /**/ isAdmissibleLemma = true;
                lemma.setSaveButtonDisabled(false);
                /**/            } else {
                /**/ isAdmissibleLemma = false;
                /**/ lemma.setSaveButtonDisabled(true);
                /**/            }
        }
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma name from " + lemma.getFormWrittenRepr() + " to " + lemmaPart);
        lemma.setSaveButtonDisabled(isSavableLemma() || (lemma.getFormWrittenRepr().contains(" ")));
        lemma.setFormWrittenRepr(lemmaPart);
    }

    private boolean isAdmissibleLemma(String w, String type) {
        if (type.equals(OntoLexEntity.Class.WORD.getLabel()) || type.equals(OntoLexEntity.Class.AFFIX.getLabel())) {
            return isAdmissibleWord(w);
        } else {
            return isAdmissibleMultiwordWord(w);
        }
    }

    private boolean isAdmissibleWord(String w) {
//        if (w.matches(ADMISSIBLE_WORD_REGEXP) || w.isEmpty()) {
        return !w.contains(" ") || w.isEmpty();
    }

    private boolean isAdmissibleMultiwordWord(String mw) {
        for (String comp : mw.split(" ")) {
            if (!(comp.matches(ADMISSIBLE_MULTIWORD_REGEXP) || mw.isEmpty())) {
                return false;
            }
        }
        return true;
    }

    public void lemmaMultiwordKeyupEvent(String lemmaPart, List<Map<String, String>> lemmaList) {
        if (!lemmaPart.isEmpty()) {
            int spacesNumber = getSpaceCount(lemmaPart, " ");
            lemma.getMultiword().clear();
            String[] splitted = lemmaPart.split(" ");
            for (int i = 0; i < spacesNumber; i++) {
                setMultiword(splitted, i, lemmaList);
            }
            if (spacesNumber > 0) {
                if ((splitted.length == spacesNumber + 1) && (!splitted[spacesNumber].isEmpty())) {
                    setMultiword(splitted, spacesNumber, lemmaList);
                }
            }
            lemmaNameKeyupEvent(lemmaPart, lemmaList);
        } else {
            lemma.getMultiword().clear();
        }
        lemma.setSaveButtonDisabled(isSavableLemmaMultiword());
    }

    private boolean isSavableLemmaMultiword() {
        if (isSavableLemma() || lemma.getFormWrittenRepr().split(" ").length <= 1) {
            return true;
        } else {
            return false;
        }
    }

    private void setMultiword(String[] s, int i, List<Map<String, String>> lemmaList) {
        Word w = getComponent(s, i, lemmaList);
        lemma.getMultiword().add(w);
    }

    private Word getComponent(String[] s, int i, List<Map<String, String>> lemmaList) {
        Word w = new Word();
        w.setWrittenRep(s[i] + " not found");
        w.setViewButtonDisabled(true);
        Collections.sort(lemmaList, new LexiconComparator("writtenRep"));
        for (Map<String, String> m : lemmaList) {
            if ((s[i].equals(m.get("writtenRep")) || (m.get("writtenRep").matches(Pattern.quote(s[i] + MULTIWORD_COMPONENT_REGEXP)))) && (!m.get("writtenRep").isEmpty())) {
                setComponent(m, w, i);
                break;
            }
        }
        return w;
    }

    private void setComponent(Map<String, String> m, Word w, int position) {
        String _pos[] = m.get("individual").split("_" + m.get("lang"))[0].split("_");
        String pos = _pos[_pos.length - 1];
        w.setWrittenRep(m.get("writtenRep"));
        w.setOWLName(m.get("individual"));
        w.setLanguage(m.get("lang"));
        w.setLabel(w.getWrittenRep() + " (" + pos + ")@" + w.getLanguage());
        w.setViewButtonDisabled(false);
        if (!isNewAction()) {
            String OWLComp = lexiconManager.getComponentAtPosition(lemma.getIndividual(), Integer.toString(position));
            w.setOWLComp(OWLComp);
        }
    }

    public void lemmaNameKeyupEvent(AjaxBehaviorEvent e) {
        String lemmaPart = (String) e.getComponent().getAttributes().get("value");
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        List<Map<String, String>> lemmaList = lexiconManager.lemmasList(currentLanguage);
        lemmaListCached = lemmaList;
        if (lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel()) || lemma.getType().equals(OntoLexEntity.Class.AFFIX.getLabel())) {
            lemmaNameKeyupEvent(lemmaPart, lemmaList);
        } else {
            lemmaMultiwordKeyupEvent(lemmaPart, lemmaList);
        }
    }

    private int getSpaceCount(String str, String toFind) {
        String s = str.replaceAll("\\s+", " ");
        int lastIndex = 0;
        int count = 0;
        while (lastIndex != -1) {
            lastIndex = s.indexOf(toFind, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += toFind.length();
            }
        }
        return count;
    }

    // returns true if the lemma (or form) already exists in a specific lexicon
    private boolean contains(List<Map<String, String>> l, String form, String lang, String pos) {
        boolean contains = false;
        for (Map<String, String> m : l) {
            String _pos = lexiconCreationControllerTabViewList.getPosFromIndividual(m.get("individual"), m.get("lang"));
            m.put("pos", _pos.isEmpty() ? Label.UNSPECIFIED_POS : _pos);
            if (lang.isEmpty() && pos.isEmpty()) {
                if (m.get("writtenRep").equals(form.trim())) {
                    contains = true;
                    break;
                }
            } else {
                if (lang.isEmpty() && !pos.isEmpty()) {
                    if (m.get("writtenRep").equals(form.trim())
                            && (m.get("pos").equals(pos))) {
                        contains = true;
                        break;
                    }
                } else {
                    if (!lang.isEmpty() && pos.isEmpty()) {
                        if (m.get("writtenRep").equals(form.trim())
                                && (m.get("lang").equals(lang))) {
                            contains = true;
                            break;
                        }
                    } else {
                        if (m.get("writtenRep").equals(form.trim())
                                && (m.get("lang").equals(lang))
                                && (m.get("pos").equals(pos))) {
                            contains = true;
                            break;
                        }
                    }
                }
            }
        }
        return contains;
    }

    private boolean isSameLemma(List<Map<String, String>> l, String formPart) {
        boolean same = false;
        for (Map<String, String> m : l) {
            if (m.get("writtenRep").equals(formPart) && (m.get("lemma").equals(lemma.getIndividual()))) {
                same = true;
                break;
            }
        }
        return same;
    }

    private void ckeckLemmaSavability() {
        if (!lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel()) && !lemma.getType().equals(OntoLexEntity.Class.AFFIX.getLabel())) {
            lemma.setSaveButtonDisabled(isSavableLemma() || (!lemma.getFormWrittenRepr().contains(" ")));
        } else {
            if (isSavableLemma()) {
                lemma.setSaveButtonDisabled(true);
            } else {
                if (lemma.getFormWrittenRepr().contains(" ")) {
                    lemma.setSaveButtonDisabled(true);
                } else {
                    lemma.setSaveButtonDisabled(false);
                }
            }
        }
    }

    public void languageChanged(AjaxBehaviorEvent e) {
        String lang = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma language of " + lemma.getFormWrittenRepr() + " to " + lang);
        lemma.setLanguage(lang);
        // check again if the lemma already exists in the new language
        //lemmaNameKeyupEvent();
        lemmaNameKeyupEvent(lemma.getFormWrittenRepr(), lemmaListCached);
        ckeckLemmaSavability();
    }

    public void verifiedChanged() {
        log(Level.INFO, loginController.getAccount(), "UPDATE Verified field of " + lemma.getFormWrittenRepr() + " to " + lemma.isVerified());
        addFormButtonDisabled = true;
        updateEntryValidity();
        ckeckLemmaSavability();
    }

    private void updateEntryValidity() {
        if (lemma.isVerified()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            lemma.setValid("Validated by " + loginController.getAccount().getName() + " at " + dateFormat.format(date));
        } else {
            lemma.setValid("false");
        }
    }

    public void lemmaMorphoTraitChanged(AjaxBehaviorEvent e) {
        String trait = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma morphological trait of " + lemma.getFormWrittenRepr() + " to " + trait);
        addFormButtonDisabled = true;
        lemma.setSaveButtonDisabled(false);
    }

    public void formMorphoTraitChanged(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        FormData fd = (FormData) component.getAttributes().get("form");
        String trait = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Form morphological trait of " + fd.getFormWrittenRepr() + " to " + trait);
        fd.setSaveButtonDisabled(isSavableForm(fd));
    }

    public void lemmaPoSChanged(AjaxBehaviorEvent e) {
        String pos = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma Part-of-Speech of " + lemma.getFormWrittenRepr() + " to " + pos);
        lemma.setPoS(pos);
        addFormButtonDisabled = true;
        lemmaNameKeyupEvent(lemma.getFormWrittenRepr(), lemmaListCached);
        ckeckLemmaSavability();
    }

    public void phoneticKeyUpEvent(AjaxBehaviorEvent e) {
        String phonetic = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma phonetic of " + lemma.getFormWrittenRepr() + " to " + phonetic);
        lemma.setFormPhoneticRep(phonetic);
        addFormButtonDisabled = true;
        ckeckLemmaSavability();
    }

    public void extensionAttributeKeyUpEvent(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        ExtensionAttributeIstance eai = (ExtensionAttributeIstance) component.getAttributes().get("extAtt");
        String att = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Lemma attribute extesion " + eai.getName() + " of " + lemma.getFormWrittenRepr() + " to " + att);
        for (LemmaData.ExtensionAttributeIstance _eai : lemma.getExtensionAttributeInstances()) {
            if (eai.getName().equals(_eai.getName())) {
                _eai.setValue(att);
            }
        }
        addFormButtonDisabled = true;
        ckeckLemmaSavability();
    }

    public void extensionFormAttributeKeyUpEvent(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        ExtensionAttributeIstance eai = (ExtensionAttributeIstance) component.getAttributes().get("extAtt");
        String att = (String) e.getComponent().getAttributes().get("value");
        FormData fd = (FormData) component.getAttributes().get("form");
        log(Level.DEBUG, loginController.getAccount(), "UPDATE Form attribute extesion " + eai.getName() + " of " + fd.getFormWrittenRepr() + " to " + att);
        for (LemmaData.ExtensionAttributeIstance _eai : fd.getExtensionAttributeInstances()) {
            if (eai.getName().equals(_eai.getName())) {
                _eai.setValue(att);
            }
        }
        fd.setSaveButtonDisabled(isSavableForm(fd));
    }

    public void phoneticFormKeyUpEvent(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        String phonetic = (String) e.getComponent().getAttributes().get("value");
        FormData fd = (FormData) component.getAttributes().get("form");
        log(Level.DEBUG, loginController.getAccount(), "UPDATE Form phonetic of " + fd.getFormWrittenRepr() + " to " + phonetic);
        fd.setSaveButtonDisabled(isSavableForm(fd));
    }

    private boolean isSavableLemma() {
        if (lemma.getLanguage().isEmpty()) {
            lemma.setLanguage(getLexicaLanguages().get(0));
        }
        if ((lemma.getFormWrittenRepr().isEmpty()) || (lemma.getLanguage().isEmpty()) || (lemma.getPoS().isEmpty())) {
            return true;
        } else {
            if ((lemma.getFormWrittenRepr().replaceAll("\\s", "").length() > 0) && (!lemmAlreadyExists)
                    /**/ && (isAdmissibleLemma)
                    && (lemma.getLanguage().length() > 0)
                    && (lemma.getPoS().length() > 0)) {
                return false;
            } else {
                return true;
            }
        }
    }

    private boolean isSavableForm(FormData fd) {
        if ((fd.getFormWrittenRepr() == null)) {
            return true;
        } else {
            if ((fd.getFormWrittenRepr().replaceAll("\\s", "").length() > 0)
                    && (!formAlreadyExists)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public void removeLemma() throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "DELETE Lemma " + lemma.getFormWrittenRepr());
        lexiconManager.deleteLemma(lemmaCopy, forms, lexiconCreationViewSenseDetail.getSenses());
        for (SenseData sd : lexiconCreationViewSenseDetail.getSenses()) {
            attestationManager.remove(sd.getName());
        }
        info("template.message.deleteLemma.summary", "template.message.deleteLemma.description", lemma.getFormWrittenRepr());
        authoringManager.removeAuthoring(Authoring.IRIType.LEXICAL_ENTRY, lemma.getIndividual());
        removeFromBreadCrumb(lemma.getIndividual());
        for (FormData fd : forms) {
            removeFromBreadCrumb(fd.getIndividual());
        }
        forms.clear();
        formsCopy.clear();
        lemma.clear();
        lemmaCopy.clear();
        lemmaRendered = false;
        addFormButtonDisabled = true;
        lexiconCreationViewSenseDetail.getSenses().clear();
        lexiconCreationViewSenseDetail.setAddSenseButtonDisabled(true);
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        lexiconCreationControllerTabViewList.initLemmaTabView(currentLanguage);
        lexiconCreationControllerTabViewList.initFormTabView(currentLanguage);
    }

    public void resetFormDetails() {
        forms.clear();
        formsCopy.clear();
        lemma.clear();
        lemmaCopy.clear();
    }

    public void saveLemma() throws IOException, OWLOntologyStorageException {
        lemma.setSaveButtonDisabled(true);
        lemma.setDeleteButtonDisabled(false);
        addFormButtonDisabled = false;
        if (lemmaCopy.getFormWrittenRepr().isEmpty()) {
            // saving due to a new lemma action
            saveLemma(false, true);
            String entry = LexiconUtil.getIRI(lemma.getFormWrittenRepr(), lemma.getLanguage(), "lemma");
            // check if the lexical entry is available and lock it
            lexiconManager.lock(entry);
            setLocked(false);
            lexiconCreationViewSenseDetail.setLocked(false);
            log(Level.INFO, loginController.getAccount(), "LOCK the lexical entry related to " + entry);
        } else {
            // saving due to a lemma modification action
            if (lemma.getFormWrittenRepr().equals(lemmaCopy.getFormWrittenRepr())) {
                // modification does not concern lemma written representation
                saveLemma(false, false);
            } else {
                // modification concerns also lemma written representation
                saveLemma(true, false);
            }
        }
        createLemmaCopy();
        newAction = false;
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        lexiconCreationControllerTabViewList.initLemmaTabView(currentLanguage);
        lexiconCreationViewSenseDetail.setSenseRendered(true);
        authoringManager.updateIRIAuthoring(loginController.getAccount(), Authoring.IRIType.LEXICAL_ENTRY, lemma.getIndividual());
    }

    private void saveLemma(boolean renaming, boolean newAction) throws IOException, OWLOntologyStorageException {
        info("template.message.saveLemma.summary", "template.message.saveLemma.description", lemma.getFormWrittenRepr());
        if (renaming) {
            saveForUpdateWithRenameAction();
        } else {
            if (newAction) {
                saveForNewAction();
            } else {
                saveForUpdateAction();
            }
        }
        updateTabViewLists();
    }

    private void saveForNewAction() throws IOException, OWLOntologyStorageException {
        if (lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel())) {
            // word case
            log(Level.INFO, loginController.getAccount(), "SAVE new Lemma and its default sense " + lemma.getFormWrittenRepr());
            lexiconCreationViewSenseDetail.saveDefaultSense(lemma, OntoLexEntity.Class.WORD.getLabel());
        } else {
            if (lemma.getType().equals(OntoLexEntity.Class.AFFIX.getLabel())) {
                log(Level.INFO, loginController.getAccount(), "SAVE new Lemma and its default sense " + lemma.getFormWrittenRepr());
                lexiconCreationViewSenseDetail.saveDefaultSense(lemma, OntoLexEntity.Class.AFFIX.getLabel());
            } else {
                // multiword case
                log(Level.INFO, loginController.getAccount(), "SAVE new Multiword and its default sense " + lemma.getFormWrittenRepr());
                lexiconCreationViewSenseDetail.saveDefaultSense(lemma, OntoLexEntity.Class.MULTIWORD.getLabel());
            }
        }
        lexiconCreationViewSenseDetail.setSenseToolbarRendered(true);
        setNewAction(false);
    }

    private void saveForUpdateAction() throws IOException, OWLOntologyStorageException {
        if (lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel()) || lemma.getType().equals(OntoLexEntity.Class.AFFIX.getLabel())) {
            // word case
            log(Level.INFO, loginController.getAccount(), "UPDATE Lemma " + lemma.getFormWrittenRepr());
            lexiconManager.updateLemma(lemmaCopy, lemma);
        } else {
            // multiword case
            log(Level.INFO, loginController.getAccount(), "UPDATE Multiword Lemma " + lemma.getFormWrittenRepr());
            // it is possible that some word has an empty writtenRep, 
            // so the deletion of the related fields (OWLName and lang) it is recommended
            lexiconManager.updateLemmaForMultiword(lemmaCopy, lemma);
        }
    }

    private void saveForUpdateWithRenameAction() throws IOException, OWLOntologyStorageException {
        if (lemma.getType().equals(OntoLexEntity.Class.WORD.getLabel()) || lemma.getType().equals(OntoLexEntity.Class.AFFIX.getLabel())) {
            // word case
            log(Level.INFO, loginController.getAccount(), "SAVE updated Lemma with renaming from " + lemmaCopy.getFormWrittenRepr() + " to " + lemma.getFormWrittenRepr());
            AttestationRenaming renamings = lexiconManager.saveLemmaWithIRIRenaming(lemmaCopy, lemma);
            attestationManager.updateAttestationURIs(renamings);
            HashMap<String, String> filesToRename = imageManager.update(renamings.getAttestationSenseUris());
            renameImageFiles(filesToRename);
            lexiconCreationViewSenseDetail.addSense(lemma.getIndividual(), "Lemma");
        } else {
            // multiword case
            log(Level.INFO, loginController.getAccount(), "SAVE updated Multiword Lemma with renaming from " + lemmaCopy.getFormWrittenRepr() + " to " + lemma.getFormWrittenRepr());
            AttestationRenaming renamings = lexiconManager.saveMultiwordLemmaWithIRIRenaming(lemmaCopy, lemma);
            attestationManager.updateAttestationURIs(renamings);
            HashMap<String, String> filesToRename = imageManager.update(renamings.getAttestationSenseUris());
            renameImageFiles(filesToRename);
            lexiconCreationViewSenseDetail.addSense(lemma.getIndividual(), "Lemma");
        }
        updateBreadCrumb("Lemma", lemma.getFormWrittenRepr(), lemmaCopy.getIndividual(), lemma.getIndividual(), false, Label.ClickProvenance.LEMMA_LIST_VIEW);

    }

    private void renameImageFiles(HashMap<String, String> filesToRename) throws IOException {
        for (Map.Entry<String, String> entry : filesToRename.entrySet()) {
            File source = new File(System.getProperty("user.home") + Label.LEXO_FOLDER + Label.IMAGES_FOLDER + entry.getKey());
            File target = new File(System.getProperty("user.home") + Label.LEXO_FOLDER + Label.IMAGES_FOLDER + entry.getValue());
            FileUtils.moveFile(source, target);
        }
    }

    private void updateTabViewLists() {
        String currentLanguage = lexiconCreationControllerTabViewList.getLexiconLanguage();
        lexiconCreationControllerTabViewList.initLemmaTabView(currentLanguage);
        lexiconCreationControllerTabViewList.initFormTabView(currentLanguage);
    }

    public ArrayList<String> getLexicaLanguages() {
        ArrayList<String> al = new ArrayList();
        for (String lang : lexiconManager.lexicaLanguagesList()) {
            al.add(lang);
        }
        Collections.sort(al);
        return al;
    }

    // invoked when lemma is a multiword in order to get the languages according to the multiword type
    public ArrayList<String> getLexicaLanguages(String mwType) {
        ArrayList<String> al = new ArrayList();
        for (String lang : lexiconManager.lexicaLanguagesList()) {
            if (mwType.equals("bilingual")) {
                if (lang.length() > 4) {
                    al.add(lang);
                }
            } else {
                if (lang.length() <= 4) {
                    al.add(lang);
                }
            }
        }
        return al;
    }

    public List<String> completeTextOnlyForMW(String _lemma) {
        List<Map<String, String>> al = lexiconManager.lemmasList("All languages");
        return getFilteredList(al, _lemma, lemma.getFormWrittenRepr(), true, false);
    }

    public List<String> completeText(String _lemma) {
        List<Map<String, String>> al = lexiconManager.lemmasList("All languages");
        return getFilteredList(al, _lemma, lemma.getFormWrittenRepr(), false, false);
    }

    private List<String> getFilteredList(List<Map<String, String>> list, String keyFilter, String currentLemma, boolean onlyMW, boolean seeAOc) {
        List<String> filteredList = new ArrayList();
        Collections.sort(list, new LexiconComparator("writtenRep"));
        for (Map<String, String> m : list) {
            String _pos = lexiconCreationControllerTabViewList.getPosFromIndividual(m.get("individual"), m.get("lang"));
            m.put("pos", _pos.isEmpty() ? Label.UNSPECIFIED_POS : _pos);
            String wr = m.get("writtenRep");
            if ((wr.startsWith(keyFilter)) && (!wr.isEmpty())) {
                if (!wr.equals(currentLemma)) {
                    if (onlyMW) {
                        if (wr.contains(" ")) {
                            filteredList.add(wr + " (" + m.get("pos") + ") @" + m.get("lang"));
                        }
                    } else {
                        if (seeAOc) {
                            if (m.get("lang").equals("aoc")) {
                                filteredList.add(wr + "@aoc");
                            }
                        } else {
                            filteredList.add(wr + " (" + m.get("pos") + ") @" + m.get("lang"));
                        }
                    }
                }
            }
        }
        return filteredList;
    }

    private List<String> getFilteredListForCorr(List<Map<String, String>> list, String keyFilter, String currentLemma) {
        List<String> filteredList = new ArrayList();
        Collections.sort(list, new LexiconComparator("writtenRep"));
        for (Map<String, String> m : list) {
            String wr = m.get("writtenRep");
            if (m.get("corr").equals("true")) {
                if ((wr.startsWith(keyFilter)) && (!wr.isEmpty())) {
                    if (!wr.equals(currentLemma)) {
                        filteredList.add(wr + "@" + m.get("lang"));
                    }
                }
            }
        }
        return filteredList;
    }

    public List<String> completeComponents(String currentComponent) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        Word w = (Word) component.getAttributes().get("currentComponent");
        List<Map<String, String>> al = lexiconManager.lemmasList("All languages");
        return getFilteredComponentList(al, w.getWrittenRep());

    }

    private List<String> getFilteredComponentList(List<Map<String, String>> list, String writtenRep) {
        List<String> filteredList = new ArrayList();
        String[] splitted = getNormalizedHomonym(writtenRep);
        for (Map<String, String> m : list) {
            String _pos = lexiconCreationControllerTabViewList.getPosFromIndividual(m.get("individual"), m.get("lang"));
            m.put("pos", _pos.isEmpty() ? Label.UNSPECIFIED_POS : _pos);
            String wr = m.get("writtenRep");
            if ((wr.matches(splitted[0] + MULTIWORD_COMPONENT_REGEXP)) && (!wr.isEmpty())) {
                filteredList.add(wr + " (" + m.get("pos") + ")@" + m.get("lang"));
            }
        }
        return filteredList;
    }

    private String[] getNormalizedHomonym(String wr) {
        String[] splitted = new String[10];
        if (wr.matches(".*\\d.*")) {
            // contains a number
            splitted = wr.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        } else {
            // does not contain a number
            splitted[0] = wr;
        }
        return splitted;
    }

    public void onReferenceSelect(Word reference) {
        log(Level.INFO, loginController.getAccount(), "ADD Reference (seeAlso) " + reference.getWrittenRep() + " to the Lemma " + lemma.getFormWrittenRepr());
        lemma.setSaveButtonDisabled(false);
        setLexicalRelationEntry(reference);
        reference.setDeleteButtonDisabled(false);
        reference.setViewButtonDisabled(false);
    }

    // w.getWrittenRep() has the following form: 'writtenrep (pos) @lang' 
    private void setLexicalRelationEntry(Word w) {
        String splitted[] = w.getWrittenRep().split("@");
        String lemma = splitted[0].split("\\([aA-zZ]+\\)")[0].trim();
        String lang = splitted[1];
        Word wd = lexiconManager.getLemma(lemma, lang);
        w.setWrittenRep(wd.getWrittenRep());
        w.setOWLName(wd.getOWLName());
        w.setLanguage(wd.getLanguage());
    }

    public void onWordSelect(Word word) {
        log(Level.INFO, loginController.getAccount(), "UPDATE the word " + word.getWrittenRep() + " of the Multiword " + lemma.getFormWrittenRepr());
        setMultiwordComponentButtons(word);
        lemma.setSaveButtonDisabled(isSavableLemmaMultiword());
        setWordOfMultiword(word);
        word.setDeleteButtonDisabled(false);
        word.setViewButtonDisabled(false);
    }

    // split w.getLabel() that contains 'writtenrep (pos)@lang'
    private void setWordOfMultiword(Word w) {
        Pattern pattern = Pattern.compile(MULTIWORD_COMPONENT_INDIVIDUAL_REGEXP);
        Matcher matcher = pattern.matcher(w.getLabel());
        String writtenRep = null;
        String pos = null;
        String lang = null;
        while (matcher.find()) {
            writtenRep = matcher.group(1);
            pos = matcher.group(2);
            lang = matcher.group(3);
        }
        w.setWrittenRep(writtenRep);
        w.setOWLName(writtenRep + "_" + pos + "_" + lang + "_entry");
        w.setLanguage(lang);

//        String splitted[] = w.getLabel().split("@");
//        String lemma = splitted[0].split("\\([aA-zZ]+\\)")[0].trim();
//        String lang2 = splitted[1];
//        Word wd = lexiconManager.getLemma(lemma, lang2);
//        w.setWrittenRep(wd.getWrittenRep());
//        w.setOWLName(wd.getOWLName().replace("_lemma", "_entry"));
//        w.setLanguage(wd.getLanguage());
//        w.setOWLComp(wd.getOWLComp());
//        w.setLabel(wd.getWrittenRep() + "@" + wd.getLanguage());
    }
    // invoked by the lemma box in order to get the details of a component of a multiword

    public void addEntryOfMultiwordComponent(Word lemma, String relType) {
        info("todo.title", "todo.description");
//        log(Level.INFO, loginController.getAccount(), "VIEW Deatils of multiword component " + lemma.getOWLName() + " of " + lemma.getWrittenRep());
//        setMultiwordComponentButtons(lemma);
//        lexiconCreationControllerRelationDetail.resetRelationDetails();
//        lexiconCreationControllerRelationDetail.setAddButtonsDisabled(false);
//        lexiconCreationControllerRelationDetail.setEntryOfLexicalRelation(lemma.getOWLName().replace("_entry", "_lemma"));
//        checkForLock(lemma.getOWLName().replace("_entry", "_lemma"));
//        lexiconManager.getLexiconLocker().print();
//        lexiconCreationControllerRelationDetail.setRelationLemmaRendered(true);
//        lexiconCreationControllerRelationDetail.setCurrentLexicalEntry(lemma.getOWLName().replace("_entry", "_lemma"));
//        lexiconCreationControllerRelationDetail.setActiveTab(2);
    }

    private void setMultiwordComponentButtons(Word comp) {
        for (Word w : lemma.getMultiword()) {
            if (comp.getWrittenRep().equals(w.getWrittenRep()) && comp.getLanguage().equals(w.getLanguage()) || w.getWrittenRep().isEmpty()) {
                w.setViewButtonDisabled(true);
            } else {
                w.setViewButtonDisabled(false);
            }
        }
    }

    // invoked by the lemma box in order to get the details of the lexical relation
    public void addEntryOfLexicalRelation(Word w, String relType) {
        info("todo.title", "todo.description");
//        log(Level.INFO, loginController.getAccount(), "VIEW Deatils of " + w.getWrittenRep() + " by " + relType + " relation of Lemma " + lemma.getFormWrittenRepr());
//        lexiconCreationControllerRelationDetail.resetRelationDetails();
//        lexiconCreationControllerRelationDetail.setAddButtonsDisabled(false);
//        lexiconCreationControllerRelationDetail.setEntryOfLexicalRelation(w.getOWLName());
//        checkForLock(w.getOWLName());
//        lexiconManager.getLexiconLocker().print();
//        lexiconCreationControllerRelationDetail.setRelationLemmaRendered(true);
//        lexiconCreationControllerRelationDetail.setCurrentLexicalEntry(w.getOWLName());
//        lexiconCreationControllerRelationDetail.setActiveTab(2);
    }

    public void setLexicalRelationButtons(boolean b) {
        for (Word w : lemma.getSeeAlso()) {
            w.setViewButtonDisabled(b);
            w.setDeleteButtonDisabled(b);
        }
    }

    // invoked by controller after an user selected add a reference (seeAlso) to lemma
    public void addReference() {
        log(Level.INFO, loginController.getAccount(), "ADD empty reference (seeAlso) to lemma " + lemma.getFormWrittenRepr());
        Word reference = new Word();
        reference.setViewButtonDisabled(true);
        lemma.getSeeAlso().add(reference);
    }

    // invoked by controller after an user selected add phonetic to lemma
    public void addPhonetic() {
        log(Level.INFO, loginController.getAccount(), "ADD empty phonetic to lemma " + lemma.getFormWrittenRepr());
        lemma.setFormPhoneticRep("");
    }

    public void removePhonetic() {
        log(Level.INFO, loginController.getAccount(), "REMOVE phonetic " + lemma.getFormPhoneticRep() + " of the lemma " + lemma.getFormWrittenRepr());
        lemma.setFormPhoneticRep(Label.NO_ENTRY_FOUND);
        lemma.setSaveButtonDisabled(false);
    }

    public void addExtensionAttribute(String attName, String label) {
        log(Level.INFO, loginController.getAccount(), "ADD empty extension attribute " + attName + " to lemma " + lemma.getFormWrittenRepr());
        LemmaData.ExtensionAttributeIstance eai = new ExtensionAttributeIstance();
        eai.setName(attName);
        eai.setValue("");
        eai.setLabel(label);
        eai.setDisabled(true);
        lemma.getExtensionAttributeInstances().add(eai);
    }

    public void addExtensionAttribute(int index, String attName, String label) {
        log(Level.INFO, loginController.getAccount(), "ADD empty extension attribute " + attName + " to form " + forms.get(index).getFormWrittenRepr());
        LemmaData.ExtensionAttributeIstance eai = new ExtensionAttributeIstance();
        eai.setName(attName);
        eai.setValue("");
        eai.setLabel(label);
        eai.setDisabled(true);
        forms.get(index).getExtensionAttributeInstances().add(eai);
        forms.get(index).setSaveButtonDisabled(false);
    }

    public void addFormPhonetic(MenuActionEvent event) {
        DefaultMenuItem item = (DefaultMenuItem) event.getMenuItem();
        Map<String, List<String>> params = item.getParams();
        FormData fd = forms.get(Integer.parseInt(params.get("id").get(0)));
        log(Level.INFO, loginController.getAccount(), "ADD empty phonetic to form " + fd.getFormWrittenRepr());
        fd.setFormPhoneticRep("");
    }

    public void removeReference(Word reference) {
        log(Level.INFO, loginController.getAccount(), "REMOVE reference (seeAlso) " + (reference.getWrittenRep().isEmpty() ? " empty see also" : reference.getWrittenRep()) + " from " + lemma.getFormWrittenRepr());
        lemma.getSeeAlso().remove(reference);
        lemma.setSaveButtonDisabled(false);
    }

    public void removeExtensionAttribute(ExtensionAttributeIstance eai) {
        log(Level.INFO, loginController.getAccount(), "REMOVE extension attribute " + eai.getName() + " with value " + (eai.getValue().isEmpty() ? " empty" : eai.getValue()) + " from " + lemma.getFormWrittenRepr());
        lemma.getExtensionAttributeInstances().remove(eai);
        lemma.setSaveButtonDisabled(false);
    }

    public void removeExtensionAttribute(ExtensionAttributeIstance eai, FormData fd) {
        log(Level.INFO, loginController.getAccount(), "REMOVE extension attribute " + eai.getName() + " with value " + (eai.getValue().isEmpty() ? " empty" : eai.getValue()) + " from " + fd.getFormWrittenRepr());
        fd.getExtensionAttributeInstances().remove(eai);
        fd.setSaveButtonDisabled(false);
    }

    public void removePhonetic(FormData fd) {
        log(Level.INFO, loginController.getAccount(), "REMOVE phonetic " + fd.getFormPhoneticRep() + " from " + fd.getFormWrittenRepr());
        fd.setFormPhoneticRep(Label.NO_ENTRY_FOUND);
        fd.setSaveButtonDisabled(false);
    }

    public void removeMorphoTrait(LemmaData.MorphoTrait trait) {
        log(Level.INFO, loginController.getAccount(), "REMOVE morphological trait " + (trait.getName().isEmpty() ? "(empty)" : trait.getName()) + " from " + lemma.getFormWrittenRepr());
        lemma.getMorphoTraits().remove(trait);
        lemma.setSaveButtonDisabled(false);
    }

    public void removeMorphoTrait(FormData fd, LemmaData.MorphoTrait trait) {
        log(Level.INFO, loginController.getAccount(), "REMOVE morphological trait " + (trait.getName().isEmpty() ? "(empty)" : trait.getName()) + " from " + fd.getFormWrittenRepr());
        fd.getMorphoTraits().remove(trait);
        fd.setSaveButtonDisabled(false);
    }

    public void addDenote() {
        log(Level.INFO, loginController.getAccount(), "ADD empty denote to lemma " + lemma.getFormWrittenRepr());
        Openable denote = new Openable();
        denote.setViewButtonDisabled(true);
        lemma.setOWLClass(denote);
    }

    public ArrayList<String> getPoS() {
        return propertyValue.getPoS(lemma.getType());
    }

    public ArrayList<String> getMorphoTraitValues(String traitName) {
        return propertyValue.getMorphoTrait(traitName);
    }

    public void addMorpho(String trait) {
        log(Level.INFO, loginController.getAccount(), "ADD empty morpho trait (" + trait + ") to lemma " + lemma.getFormWrittenRepr());
        LemmaData.MorphoTrait mt = new LemmaData.MorphoTrait();
        mt.setName(trait);
        lemma.getMorphoTraits().add(mt);
        lemma.setSaveButtonDisabled(false);
    }

    public void addFormMorpho(String trait) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        FormData fd = (FormData) component.getAttributes().get("form");
        log(Level.INFO, loginController.getAccount(), "ADD empty morpho trait (" + trait + ") to form " + fd.getFormWrittenRepr());
        LemmaData.MorphoTrait mt = new LemmaData.MorphoTrait();
        mt.setName(trait);
        fd.getMorphoTraits().add(mt);
        fd.setSaveButtonDisabled(false);
    }

    // dynamic lemma menu creation
    public MenuModel getAddMenuModel() {

        MenuModel addMenuModel = new DefaultMenuModel();
        DefaultSubMenu morphoMenu = new DefaultSubMenu();
        morphoMenu.setLabel("Add morphological trait");
        morphoMenu.setStyleClass("lexiconTabView");
        morphoMenu.setIcon("fa fa-plus");
        createMorphoSubMenu(morphoMenu);

        DefaultMenuItem newForm = new DefaultMenuItem();
        newForm.setValue("Add new form");
        newForm.setStyleClass("lexiconTabView");
        newForm.setIcon("fa fa-plus");
        newForm.setDisabled(newAction);
        newForm.setUpdate("FormDataList :systemMessage");
        newForm.setCommand("#{lexiconControllerFormDetail.addForm()}");
        newForm.setOnstart("PF('loadingDialog').show();");
        newForm.setOncomplete("setHeight();PF('loadingDialog').hide()");

        DefaultMenuItem newSense = new DefaultMenuItem();
        newSense.setValue("Add new sense");
        newSense.setStyleClass("lexiconTabView");
        newSense.setIcon("fa fa-plus");
        newSense.setDisabled(newAction);
        newSense.setUpdate(":editViewTab:lexiconSenseDetailForm:SenseDataList :systemMessage");
        newSense.setCommand("#{lexiconControllerFormDetail.addSense()}");
        newSense.setOnstart("PF('loadingDialog').show();");
        newSense.setOncomplete("setHeight();PF('loadingDialog').hide()");

        DefaultMenuItem newSeeAlso = new DefaultMenuItem();
        newSeeAlso.setValue("Add see also");
        newSeeAlso.setStyleClass("lexiconTabView");
        newSeeAlso.setIcon("fa fa-plus");
        newSeeAlso.setDisabled(newAction);
        newSeeAlso.setUpdate("LemmaPanelGrid :systemMessage");
        newSeeAlso.setCommand("#{lexiconControllerFormDetail.addReference()}");
        newSeeAlso.setOnstart("PF('loadingDialog').show();");
        newSeeAlso.setOncomplete("setHeight();PF('loadingDialog').hide()");

        DefaultMenuItem phonetic = new DefaultMenuItem();
        phonetic.setValue("Add phonetic");
        phonetic.setStyleClass("lexiconTabView");
        phonetic.setIcon("fa fa-plus");
        phonetic.setDisabled(newAction || !lemma.getFormPhoneticRep().equals(Label.NO_ENTRY_FOUND));
        phonetic.setUpdate("LemmaPanelGrid :systemMessage");
        phonetic.setCommand("#{lexiconControllerFormDetail.addPhonetic()}");
        phonetic.setOnstart("PF('loadingDialog').show();");
        phonetic.setOncomplete("setHeight();PF('loadingDialog').hide()");

        addMenuModel.getElements().add(morphoMenu);
        addMenuModel.getElements().add(phonetic);
        addMenuModel.getElements().add(newSeeAlso);
        addMenuModel.getElements().add(new DefaultSeparator());
        addMenuModel.getElements().add(newForm);
        addMenuModel.getElements().add(newSense);

        addExtensionAttributeElement(addMenuModel);

        addMenuModel.generateUniqueIds();

        return addMenuModel;
    }

    private void addExtensionAttributeElement(MenuModel addMenuModel) {
        if (getExtensionAttributeList().size() > 0) {
//            addMenuModel.addElement(new DefaultSeparator());
            for (ExtensionAttribute ea : getExtensionAttributeList()) {
                if (ea.getDomain().equals("Lexical Entry")) {
                    DefaultMenuItem dmi = new DefaultMenuItem();
                    dmi.setValue("Add " + ea.getLabel());
                    dmi.setStyleClass("lexiconTabView");
                    dmi.setIcon("fa fa-user-plus");
                    dmi.setDisabled(setDisabled(ea));
                    dmi.setUpdate("LemmaPanelGrid :systemMessage");
                    dmi.setCommand("#{lexiconControllerFormDetail.addExtensionAttribute('"
                            + ea.getName() + "', '" + ea.getLabel() + "')}");
                    dmi.setOnstart("PF('loadingDialog').show();");
                    dmi.setOncomplete("setHeight();PF('loadingDialog').hide()");
                    addMenuModel.getElements().add(dmi);
                }
            }
        }
    }

    private void addFormExtensionAttributeElement(MenuModel addMenuModel, FormData fd, int index) {
        if (getExtensionAttributeList().size() > 0) {
//            addMenuModel.addElement(new DefaultSeparator());
            for (ExtensionAttribute ea : getExtensionAttributeList()) {
                if (ea.getDomain().equals("Lexical Entry") || ea.getDomain().equals("Form")) {
                    DefaultMenuItem dmi = new DefaultMenuItem();
                    dmi.setValue("Add " + ea.getLabel());
                    dmi.setStyleClass("lexiconTabView");
                    dmi.setIcon("fa fa-user-plus");
                    dmi.setDisabled(setDisabled(ea, fd));
                    dmi.setUpdate("FormDataList :systemMessage");
                    dmi.setCommand("#{lexiconControllerFormDetail.addExtensionAttribute("
                            + index + ", '" + ea.getName() + "', '" + ea.getLabel() + "')}");
                    dmi.setOnstart("PF('loadingDialog').show();");
                    dmi.setOncomplete("setHeight();PF('loadingDialog').hide()");
                    addMenuModel.getElements().add(dmi);
                }
            }
        }
    }

    private boolean setDisabled(ExtensionAttribute ea) {
        for (ExtensionAttributeIstance eai : lemma.getExtensionAttributeInstances()) {
            if (eai.getName().equals(ea.getName())) {
                return eai.isDisabled() || newAction;
            }
        }
        return false;
    }

    private boolean setDisabled(ExtensionAttribute ea, FormData fd) {
        for (ExtensionAttributeIstance eai : fd.getExtensionAttributeInstances()) {
            if (eai.getName().equals(ea.getName())) {
                return eai.isDisabled() || newAction;
            }
        }
        return false;
    }

    private boolean isItemDisabled(String value) {
        for (LemmaData.MorphoTrait item : lemma.getMorphoTraits()) {
            if (item.getName().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private void createMorphoSubMenu(DefaultSubMenu morphoMenu) {
        morphoMenu.getElements().add(getItem(Label.MORPHO_ANIMACY_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_ASPECT_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_CASE_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_CLITICNESS_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_DATING_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_DEFINITENESS_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_DEGREE_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_FINITENESS_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_FREQUENCY_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_GENDER_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_MOOD_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_NEGATIVE_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_NUMBER_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_PERSON_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_TENSE_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_TERM_ELEMENT_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_TERM_TYPE_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_VERB_FORM_MOOD_LABEL));
        morphoMenu.getElements().add(getItem(Label.MORPHO_VOICE_LABEL));
    }

    private DefaultMenuItem getItem(String value) {
        DefaultMenuItem item = new DefaultMenuItem();
        item.setValue(value);
        item.setDisabled(isItemDisabled(value));
        item.setUpdate("LemmaPanelGrid :editViewTab:lexiconViewDictionaryForm");
        item.setCommand("#{lexiconControllerFormDetail.addMorpho('" + value + "')}");
        item.setOnstart("PF('loadingDialog').show();");
        item.setOncomplete("setHeight();PF('loadingDialog').hide()");
        return item;
    }

    // dynamic form menu creation
    public MenuModel getAddFormMenuModel(FormData fd, int index) {

        MenuModel addFormMenuModel = new DefaultMenuModel();
        DefaultSubMenu morphoMenu = new DefaultSubMenu();
        morphoMenu.setLabel("Add morphological trait");
        morphoMenu.setStyleClass("lexiconTabView");
        morphoMenu.setIcon("fa fa-plus");
        createMorphoSubMenu(morphoMenu, fd);

        DefaultMenuItem phonetic = new DefaultMenuItem();
        phonetic.setValue("Add phonetic");
        phonetic.setStyleClass("lexiconTabView");
        phonetic.setIcon("fa fa-plus");
        phonetic.setDisabled(newFormAction || !fd.getFormPhoneticRep().isEmpty() && !fd.getFormPhoneticRep().equals(Label.NO_ENTRY_FOUND));
        phonetic.setUpdate("FormDataList :editViewTab:lexiconViewDictionaryForm");
        phonetic.setParam("id", forms.indexOf(fd));
        phonetic.setCommand("#{lexiconControllerFormDetail.addFormPhonetic}");
        phonetic.setOnstart("PF('loadingDialog').show();");
        phonetic.setOncomplete("setHeight();PF('loadingDialog').hide()");

        addFormMenuModel.getElements().add(phonetic);
        addFormMenuModel.getElements().add(morphoMenu);
        addFormMenuModel.getElements().add(new DefaultSeparator());
        addFormExtensionAttributeElement(addFormMenuModel, fd, index);

        addFormMenuModel.generateUniqueIds();

        return addFormMenuModel;
    }

    private void createMorphoSubMenu(DefaultSubMenu morphoMenu, FormData fd) {
        morphoMenu.getElements().add(getItem(Label.MORPHO_ANIMACY_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_ASPECT_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_CASE_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_CLITICNESS_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_DATING_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_DEFINITENESS_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_DEGREE_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_FINITENESS_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_FREQUENCY_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_GENDER_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_MOOD_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_NEGATIVE_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_NUMBER_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_PERSON_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_TENSE_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_TERM_ELEMENT_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_TERM_TYPE_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_VERB_FORM_MOOD_LABEL, fd));
        morphoMenu.getElements().add(getItem(Label.MORPHO_VOICE_LABEL, fd));
    }

    private boolean isItemDisabled(String value, FormData fd) {
        for (LemmaData.MorphoTrait item : fd.getMorphoTraits()) {
            if (item.getName().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private DefaultMenuItem getItem(String value, FormData fd) {
        DefaultMenuItem item = new DefaultMenuItem();
        item.setValue(value);
        item.setDisabled(isItemDisabled(value, fd));
        item.setUpdate("FormDataList :editViewTab:lexiconViewDictionaryForm");
        item.setCommand("#{lexiconControllerFormDetail.addFormMorpho('" + value + "')}");
        item.setOnstart("PF('loadingDialog').show();");
        item.setOncomplete("setHeight();PF('loadingDialog').hide()");
        return item;
    }

}
