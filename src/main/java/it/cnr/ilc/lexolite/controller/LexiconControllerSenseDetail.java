/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.LexOliteProperty;
import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.domain.Authoring;
import it.cnr.ilc.lexolite.domain.ExtensionAttribute;
import it.cnr.ilc.lexolite.manager.AttestationManager;
import it.cnr.ilc.lexolite.manager.AuthoringManager;
import it.cnr.ilc.lexolite.manager.ImageData;
import it.cnr.ilc.lexolite.manager.ImageManager;
import it.cnr.ilc.lexolite.manager.LemmaData;
import it.cnr.ilc.lexolite.manager.LemmaData.ExtensionAttributeIstance;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.OntologyManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import it.cnr.ilc.lexolite.manager.PropertyValue.Ontology;
import it.cnr.ilc.lexolite.manager.ReferenceMenuTheme;
import it.cnr.ilc.lexolite.manager.ReferenceMenuTheme.itemType;
import it.cnr.ilc.lexolite.manager.SenseData;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.event.Level;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSeparator;
import org.primefaces.model.menu.MenuModel;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerSenseDetail extends BaseController implements Serializable {

    @Inject
    private LexiconControllerFormDetail lexiconCreationControllerFormDetail;
    @Inject
    private LexiconControllerAttestation lexiconControllerAttestation;
    @Inject
    private OntologyManager ontologyManager;
    @Inject
    private PropertyValue propertyValue;
    @Inject
    private ImageManager imageManager;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LoginController loginController;
    @Inject
    private AuthoringManager authoringManager;
    @Inject
    private ImageController imageController;
    @Inject
    private AttestationManager attestationManager;

    private final List<SenseData> senses = new ArrayList<>();
    private final List<SenseData> sensesCopy = new ArrayList<>();
    private boolean addSenseButtonDisabled = true;
    private boolean senseToolbarRendered = false;
    private boolean undeletableSense = true;

    private boolean verified = false;

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    private boolean senseRendered = false;
    private boolean locked = false;

    public boolean isSenseRendered() {
        return senseRendered;
    }

    public void setSenseRendered(boolean senseRendered) {
        this.senseRendered = senseRendered;
    }

    public List<SenseData> getSenses() {
        return senses;
    }

    public boolean isAddSenseButtonDisabled() {
        return addSenseButtonDisabled;
    }

    public void setAddSenseButtonDisabled(boolean addSenseButtonDisabled) {
        this.addSenseButtonDisabled = addSenseButtonDisabled;
    }

    public boolean isSenseToolbarRendered() {
        return senseToolbarRendered;
    }

    public void setSenseToolbarRendered(boolean senseToolbarRendered) {
        this.senseToolbarRendered = senseToolbarRendered;
    }

    public boolean isUndeletableSense() {
        return undeletableSense;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isNewAction() {
        return lexiconCreationControllerFormDetail.isNewAction();
    }

    public String emptyMessage(String text, String emptyMessage) {
        return emptyMessage(text, text, emptyMessage);
    }

    public String emptyMessage(String test, String text, String emptyMessage) {
        return test == null || test.equals("") ? emptyMessage : text;
    }

    public void openNote(String senseName) {
        log(Level.INFO, loginController.getAccount(), "OPEN note of Sense " + senseName);
    }

    public void saveNote(SenseData sd) throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "EDIT note of Sense " + sd.getName() + " in " + sd.getNote());
        int order = senses.indexOf(sd);
        lexiconManager.saveSenseNote(sd, sensesCopy.get(order).getNote());
        sensesCopy.get(order).setNote(sd.getNote());
        info("template.message.saveSenseNote.summary", "template.message.saveSenseNote.description");
//        if (sd.getNote().isEmpty()) {
//            authoringManager.removeAuthoring(Authoring.IRIType.SENSE_NOTE, sd.getName());
//        } else {
//            authoringManager.updateIRIAuthoring(loginController.getAccount(), Authoring.IRIType.SENSE_NOTE, sd.getName());
//        }
    }

    public void closeNote(String senseName) {
        log(Level.INFO, loginController.getAccount(), "CLOSE note of Sense " + senseName);
    }

    public String getCommentIcon(SenseData sd) {
        if (sd.getNote().length() > 0) {
            return "fa fa-comment";
        } else {
            return "fa fa-comment-o";
        }
    }

    public List<SenseData> getSensesCopy() {
        return sensesCopy;
    }

    private void addSenseCopy(SenseData sd) {
        sensesCopy.add(copySenseData(sd));
    }

    private void updateSenseCopy(SenseData sd, int order) {
        SenseData updatedSense = copySenseData(sd);
        sensesCopy.remove(order);
        sensesCopy.add(order, updatedSense);
    }

    private void addSenseCopy(ArrayList<SenseData> sdList) {
        sensesCopy.clear();
        for (SenseData sd : sdList) {
            sensesCopy.add(copySenseData(sd));
        }
    }

    private SenseData copySenseData(SenseData sd) {
        SenseData _sd = new SenseData();
//        Openable _OWLClass = new Openable();
//        _OWLClass.setDeleteButtonDisabled(sd.getOWLClass().isDeleteButtonDisabled());
//        _OWLClass.setViewButtonDisabled(sd.getOWLClass().isViewButtonDisabled());
//        _OWLClass.setName(sd.getOWLClass().getName());
//        _sd.setOWLClass(_OWLClass);
        ReferenceMenuTheme rmt = new ReferenceMenuTheme();
        rmt.setId(sd.getThemeOWLClass().getId());
        rmt.setName(sd.getThemeOWLClass().getName());
        rmt.setNamespace(sd.getThemeOWLClass().getNamespace());
        rmt.setType(itemType.valueOf(sd.getThemeOWLClass().getType()));
        _sd.setThemeOWLClass(rmt);
        _sd.setDefinition(sd.getDefinition());
        _sd.setName(sd.getName());
        _sd.setNote(sd.getNote());
        _sd.setExtensionAttributeInstances(copyExtensionAttributeInstances(sd.getExtensionAttributeInstances()));
        return _sd;
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

    public List<ReferenceMenuTheme> completeTheme(String query) {
        String queryLowerCase = query.toLowerCase();
        List<ReferenceMenuTheme> allThemes = ontologyManager.ontologyEntities();
        List<ReferenceMenuTheme> n = allThemes.stream().filter(t -> t.getName().toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());
        return allThemes.stream().filter(t -> t.getName().toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());
    }

    public void senseDefinitionKeyupEvent(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        SenseData sd = (SenseData) component.getAttributes().get("sense");
        String sensePart = ((String) e.getComponent().getAttributes().get("value"));
        log(Level.INFO, loginController.getAccount(), "UPDATE Sense definition of " + sd.getName() + " in " + sensePart);
        if (!sensePart.isEmpty()) {
            sd.setSaveButtonDisabled(false);
        }
    }

    public void removeImage(SenseData sd, ImageData image) {
        log(Level.INFO, loginController.getAccount(), "REMOVE image " + image.getFileName() + " of " + sd.getName());
        imageManager.remove(image.getFileName());
        sd.getImages().remove(image);
        if (deleteImageFromFilesystem(image.getFileName())) {
            info("template.message.deletedImage.summary", "template.message.deletedImage.description");
        } else {
            warn("template.message.deletedImage.summary", "template.message.NOdeletedImage.description");
        }
    }

    private boolean deleteImageFromFilesystem(String imageName) {
        File targetFile = new File(System.getProperty("user.home") + Label.LEXO_FOLDER + Label.IMAGES_FOLDER + imageName);
        if (targetFile.delete()) {
            log(Level.INFO, loginController.getAccount(), "DELETE image " + imageName);
            return true;
        } else {
            log(Level.INFO, loginController.getAccount(), "DELETE image " + imageName + " failed");
            return false;
        }
    }

    public void removeDefinition(SenseData sd) {
        log(Level.INFO, loginController.getAccount(), "REMOVE " + sd.getDefinition() + " (scientific name of " + sd.getName() + ")");
        sd.setDefinition("");
        sd.setSaveButtonDisabled(false);
    }

    public void addSense() {
        SenseData sd = new SenseData();
        sd.setName("automatically set");
        sd.setSaveButtonDisabled(false);
        senses.add(sd);
    }

    public void addDefinition() {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        SenseData sd = (SenseData) component.getAttributes().get("sense");
        log(Level.INFO, loginController.getAccount(), "ADD empty definition to sense " + sd.getName());
        sd.setDefinition("");
    }

    public void imageUpload() {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        SenseData sd = (SenseData) component.getAttributes().get("sense");
        log(Level.INFO, loginController.getAccount(), "OPEN image uploader for the sense " + sd.getName());
        imageController.setSelectedSense(sd);
    }

    public void addSenseRelation(String relType) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        SenseData sd = (SenseData) component.getAttributes().get("sense");
        log(Level.INFO, loginController.getAccount(), "ADD empty " + relType + " relation to " + sd.getName());
        ReferenceMenuTheme rmt = new ReferenceMenuTheme();
        rmt.setId(1);
        //SenseData.Openable sdo = new SenseData.Openable();
        //sdo.setViewButtonDisabled(true);
        switch (relType) {
            case "reference":
                //sd.setOWLClass(sdo);
                sd.setThemeOWLClass(rmt);
                break;
            default:
        }
    }

    public void addAttestation() {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        SenseData sd = (SenseData) component.getAttributes().get("sense");
        log(Level.INFO, loginController.getAccount(), "ADD empty attestation to " + sd.getName());
        lexiconControllerAttestation.setSelectedSense(sd);
        lexiconControllerAttestation.setAttestation("");
        lexiconControllerAttestation.setAttestedForm("");
        lexiconControllerAttestation.setDictionaryPreferred(false);
        lexiconControllerAttestation.setDocID("");
        lexiconControllerAttestation.setForm("-1");
        lexiconControllerAttestation.setPageNumber("");
        lexiconControllerAttestation.setLineNumber("");
    }

    public void addSense(String entry, String entryType) {
        senses.clear();
        sensesCopy.clear();
        ArrayList<SenseData> al = null;
        switch (entryType) {
            case "Lemma":
                al = lexiconManager.getSensesOfLemma(entry, imageManager, ontologyManager.getOntologyModel() == null ? null : ontologyManager.ontologyEntities(),
                        lexiconCreationControllerFormDetail.getExtensionAttributeList());
                break;
            case "Form":
                al = lexiconManager.getSensesOfForm(entry, imageManager, ontologyManager.getOntologyModel() == null ? null : ontologyManager.ontologyEntities(),
                        lexiconCreationControllerFormDetail.getExtensionAttributeList());
                break;
            // it never happens because currently the "Sense" tab panel does not exist
            case "Sense":
                al = lexiconManager.getSenses(entry, imageManager, ontologyManager.getOntologyModel() == null ? null : ontologyManager.ontologyEntities(),
                        lexiconCreationControllerFormDetail.getExtensionAttributeList());
                break;
        }
        senses.addAll(al);
        addSenseCopy(al);
    }

    public void removeSense(SenseData sd) throws IOException, OWLOntologyStorageException {
        if ((senses.indexOf(sd) == 0) && (senses.indexOf(sd) != -1)) {
            // default sense can't be deleted !
            log(Level.INFO, loginController.getAccount(), "Negated Deletion of Default Sense " + sd.getName());
            warn("template.message.deleteSenseDenied.summary", "template.message.deleteSenseDenied.description", sd.getName());
        } else {
            if ((senses.indexOf(sd) > 0) && (senses.indexOf(sd) != -1)) {
                // it sets all sense relations as invalid (to be deleted)
                log(Level.INFO, loginController.getAccount(), "DELETE Sense " + sd.getName());
                lexiconManager.deleteSense(sd);
                attestationManager.remove(sd.getName());
//                authoringManager.removeAuthoring(Authoring.IRIType.LEXICAL_SENSE, sd.getName());
                info("template.message.deleteSense.summary", "template.message.deleteSense.description", sd.getName());
                // remove the sense box
                senses.remove(sd);
            }
        }
    }

    public List<String> completeText(String sense) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        String relType = (String) component.getAttributes().get("Relation");
        String currentSense = (String) component.getAttributes().get("currentSense");
        List<Map<String, String>> al = null;

        if (relType.equals("ontoRef")) {
            return getFilteredClasses(ontologyManager.classesList(), sense);
        } else {
            return null;
        }
    }

    private List<String> getFilteredClasses(List<String> list, String keyFilter) {
        List<String> filteredList = new ArrayList();
        Collections.sort(list);
        for (String clazz : list) {
            if (clazz.toLowerCase().startsWith(keyFilter.toLowerCase())) {
                filteredList.add(clazz);
            }
        }
        return filteredList;
    }

    public void saveSenseRelation(SenseData sd) throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "SAVE Sense " + sd.getName());
        int order = senses.indexOf(sd);
        lexiconManager.saveSenseRelation(sensesCopy.get(order), sd);
        updateSenseCopy(sd, order);
        sd.setSaveButtonDisabled(true);
        setSenseToolbarRendered(true);
        info("template.message.saveSenseRelation.summary", "template.message.saveSenseRelation.description", sd.getName());
    }

    public void removeSenseRelation(SenseData sd, ReferenceMenuTheme rmt, String relType) {
        switch (relType) {
            case "reference":
                log(Level.INFO, loginController.getAccount(), "REMOVE " + (rmt.getName().isEmpty() ? " empty reference" : rmt.getName()) + " (reference of " + sd.getName() + ")");
                sd.getThemeOWLClass().setName("");
                sd.getThemeOWLClass().setId(0);
                break;
            default:
        }
        if (!rmt.getName().isEmpty() || (relType.equals("reference"))) {
            sd.setSaveButtonDisabled(false);
        }
    }

    public void onOntologyReferenceSelect(SenseData sd) {
        //log(Level.INFO, loginController.getAccount(), "ADD ontological reference " + sd.getOWLClass().getName() + " to the sense " + sd.getName());
        log(Level.INFO, loginController.getAccount(), "ADD ontological reference " + sd.getThemeOWLClass().getName() + " to the sense " + sd.getName());
        sd.setSaveButtonDisabled(false);
    }

    // invoked by new sense button of lemma box
    public void saveSense(LemmaData ld) throws IOException, OWLOntologyStorageException {
        SenseData sd = new SenseData();
        sd.setSaveButtonDisabled(true);
        lexiconManager.saveSense(sd, ld);
//        authoringManager.updateIRIAuthoring(loginController.getAccount(), Authoring.IRIType.LEXICAL_SENSE, sd.getName());
        senses.add(sd);
        addSenseCopy(sd);
        info("template.message.saveSense.summary", "template.message.saveSense.description", sd.getName());
    }

    // invoked by new lemma
    public void saveDefaultSense(LemmaData ld, String wordType) throws IOException, OWLOntologyStorageException {
        setDefaultSense();
        lexiconManager.saveLemmaAndDefaultSense(senses.get(0), ld, wordType);
        addSenseCopy(senses.get(0));
    }

    private void setDefaultSense() {
        senses.clear();
        sensesCopy.clear();
        SenseData defaultSense = new SenseData();
        defaultSense.setSaveButtonDisabled(true);
        defaultSense.setDeleteButtonDisabled(false);
        senses.add(defaultSense);
    }

    public void ontoClassChanged(SenseData sense) {
        log(Level.INFO, loginController.getAccount(), "UPDATE OntoClass to " + sense.getName() + " of " + sense.getName());
        sense.setSaveButtonDisabled(false);
    }

    public void resetSenseDetails() {
        senses.clear();
        senseRendered = false;
    }

    public void addOntoReference(SenseData sd) {
        log(Level.INFO, loginController.getAccount(), "ADD empty ontological reference to " + sd.getName());
        sd.getOWLClass().setViewButtonDisabled(true);
    }

    public List<SelectItem> getSensesByLanguage(String sense, boolean filterByLanguage) {
        String filter = "";
        if (!filter.isEmpty()) {
            return lexiconManager.getSensesByLanguage(sense, filter);
        } else {
            return lexiconManager.getSensesByLanguage(sense, null);
        }
    }

    public ArrayList<Ontology> getTaxonomy() {
        return propertyValue.getTaxonomy();
    }

    public boolean isOntologyEnabled() {
        return LexOliteProperty.getProperty(Label.ONTOLOGY_FILE_NAME_KEY) != null;
    }

    public void extensionSenseAttributeKeyUpEvent(AjaxBehaviorEvent e) {
        UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());
        ExtensionAttributeIstance eai = (ExtensionAttributeIstance) component.getAttributes().get("extAtt");
        String att = (String) e.getComponent().getAttributes().get("value");
        SenseData sd = (SenseData) component.getAttributes().get("sense");
        log(Level.INFO, loginController.getAccount(), "UPDATE Sense attribute extesion " + eai.getName() + " of " + sd.getName() + " to " + att);
        for (LemmaData.ExtensionAttributeIstance _eai : sd.getExtensionAttributeInstances()) {
            if (eai.getName().equals(_eai.getName())) {
                _eai.setValue(att);
            }
        }
        sd.setSaveButtonDisabled(false);
    }

    public void removeExtensionAttribute(ExtensionAttributeIstance eai, SenseData sd) {
        log(Level.INFO, loginController.getAccount(), "REMOVE extension attribute " + eai.getName() + " with value " + (eai.getValue().isEmpty() ? " empty" : eai.getValue()) + " from " + sd.getName());
        sd.getExtensionAttributeInstances().remove(eai);
        sd.setSaveButtonDisabled(false);
    }

    // dynamic form menu creation
    public MenuModel getAddSenseMenuModel(SenseData sd, int index) {

        MenuModel addSenseMenuModel = new DefaultMenuModel();

        DefaultMenuItem definition = new DefaultMenuItem();
        definition.setValue("Add definition");
        definition.setStyleClass("lexiconTabView");
        definition.setIcon("fa fa-plus");
        definition.setDisabled(!sd.getDefinition().equals(Label.NO_ENTRY_FOUND));
        definition.setUpdate(":editViewTab:lexiconSenseDetailForm:SenseDataList");
        definition.setCommand("#{lexiconControllerSenseDetail.addDefinition()}");
        definition.setOnstart("PF('loadingDialog').show();");
        definition.setOncomplete("setHeight();PF('loadingDialog').hide()");

        DefaultMenuItem image = new DefaultMenuItem();
        image.setValue("Add image");
        image.setStyleClass("lexiconTabView");
        image.setIcon("fa fa-plus");
        image.setDisabled(sd.getImages().size() > 1);
        image.setUpdate("lexiconImageUploadPanel");
        image.setCommand("#{lexiconControllerSenseDetail.imageUpload()}");
        image.setOnstart("PF('loadingDialog').show();");
        image.setOncomplete("setHeight();PF('dlgLexiconUploadImage').show();PF('loadingDialog').hide();");

        DefaultMenuItem reference = new DefaultMenuItem();
        reference.setValue("Add reference");
        reference.setStyleClass("lexiconTabView");
        reference.setIcon("fa fa-plus");
        reference.setDisabled(!isOntologyEnabled() || !sd.getThemeOWLClass().getName().isEmpty());
        reference.setUpdate(":editViewTab:lexiconSenseDetailForm:SenseDataList");
        reference.setCommand("#{lexiconControllerSenseDetail.addSenseRelation('reference')}");
        reference.setOnstart("PF('loadingDialog').show();");
        reference.setOncomplete("setHeight();PF('loadingDialog').hide()");

        DefaultMenuItem attestation = new DefaultMenuItem();
        attestation.setValue("Add attestation");
        attestation.setStyleClass("lexiconTabView");
        attestation.setIcon("fa fa-plus");
        attestation.setDisabled(false);
        attestation.setUpdate("newAttestationPanel");
        attestation.setCommand("#{lexiconControllerSenseDetail.addAttestation()}");
        attestation.setOnstart("PF('loadingDialog').show();");
        attestation.setOncomplete("setHeight();PF('newAttestationDialog').show();PF('loadingDialog').hide();");

        addSenseMenuModel.getElements().add(definition);
        addSenseMenuModel.getElements().add(image);
        addSenseMenuModel.getElements().add(reference);
        addSenseMenuModel.getElements().add(new DefaultSeparator());
        addSenseMenuModel.getElements().add(attestation);
        addSenseExtensionAttributeElement(addSenseMenuModel, sd, index);

        addSenseMenuModel.generateUniqueIds();

        return addSenseMenuModel;
    }

    private void addSenseExtensionAttributeElement(MenuModel addMenuModel, SenseData sd, int index) {
        if (lexiconCreationControllerFormDetail.getExtensionAttributeList().size() > 0) {
            for (ExtensionAttribute ea : lexiconCreationControllerFormDetail.getExtensionAttributeList()) {
                if (ea.getDomain().equals("Lexical Entry") || ea.getDomain().equals("Sense")) {
                    DefaultMenuItem dmi = new DefaultMenuItem();
                    dmi.setValue("Add " + ea.getLabel());
                    dmi.setStyleClass("lexiconTabView");
                    dmi.setIcon("fa fa-user-plus");
                    dmi.setDisabled(setDisabled(ea, sd));
                    dmi.setUpdate(":editViewTab:lexiconSenseDetailForm:SenseDataList :systemMessage");
                    dmi.setCommand("#{lexiconControllerSenseDetail.addExtensionAttribute("
                            + index + ", '" + ea.getName() + "', '" + ea.getLabel() + "')}");
                    dmi.setOnstart("PF('loadingDialog').show();");
                    dmi.setOncomplete("setHeight();PF('loadingDialog').hide()");
                    addMenuModel.getElements().add(dmi);
                }
            }
        }
    }

    private boolean setDisabled(ExtensionAttribute ea, SenseData sd) {
        for (ExtensionAttributeIstance eai : sd.getExtensionAttributeInstances()) {
            if (eai.getName().equals(ea.getName())) {
                return eai.isDisabled();
            }
        }
        return false;
    }

    public void addExtensionAttribute(int index, String attName, String label) {
        log(Level.INFO, loginController.getAccount(), "ADD empty extension attribute " + attName + " to sense " + senses.get(index).getName());
        LemmaData.ExtensionAttributeIstance eai = new ExtensionAttributeIstance();
        eai.setName(attName);
        eai.setValue("");
        eai.setLabel(label);
        eai.setDisabled(true);
        senses.get(index).getExtensionAttributeInstances().add(eai);
        senses.get(index).setSaveButtonDisabled(false);
    }

    public int hasAttestation(String senseUri) {
        
        return attestationManager.loadAttestationsBySense(senseUri).size();
        
    }
}
