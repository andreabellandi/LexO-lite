/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.domain.Attestation;
import it.cnr.ilc.lexolite.manager.AttestationManager;
import it.cnr.ilc.lexolite.manager.DocumentationManager;
import it.cnr.ilc.lexolite.manager.SenseData;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.CellEditEvent;
import org.slf4j.event.Level;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 *
 * @author andreabellandi
 */
@ViewScoped
@Named
public class LexiconControllerAttestation extends BaseController implements Serializable {

    @Inject
    private LoginController loginController;
    @Inject
    private AttestationManager attestationManager;
    @Inject
    private LexiconControllerFormDetail lexiconControllerFormDetail;
    @Inject
    private LexiconControllerSenseDetail lexiconControllerSenseDetail;
    @Inject
    private DocumentationManager documentationManager;

    private boolean attestationViewRendered = false;

    private String formUri = "";
    private String form = "";
    private String attestedForm = "";
    private String pageNumber;
    private String lineNumber;
    private boolean dictionaryPreferred = false;
    private String docID = "";
    private String attestation = "";

    private SenseData selectedSense;

    public boolean isAttestationViewRendered() {
        return attestationViewRendered;
    }

    public void setAttestationViewRendered(boolean attestationViewRendered) {
        this.attestationViewRendered = attestationViewRendered;
    }

    public String getAttestedForm() {
        return attestedForm;
    }

    public void setAttestedForm(String attestedForm) {
        this.attestedForm = attestedForm;
    }

    public boolean isSaveEnabled() {
        return !form.isEmpty() && !docID.isEmpty() && !attestation.isEmpty();
    }

    public SenseData getSelectedSense() {
        return selectedSense;
    }

    public void setSelectedSense(SenseData selectedSense) {
        this.selectedSense = selectedSense;
    }

    public String getAttestation() {
        return attestation;
    }

    public void setAttestation(String attestation) {
        this.attestation = attestation;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public boolean isDictionaryPreferred() {
        return dictionaryPreferred;
    }

    public void setDictionaryPreferred(boolean dictionaryPreferred) {
        this.dictionaryPreferred = dictionaryPreferred;
    }

    public void formChanged(AjaxBehaviorEvent e) {
        String form = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE form of attestation of " + selectedSense.getName() + " to " + form);
        setForm(form);
    }

    public void docIDKeyupEvent(AjaxBehaviorEvent e) {
        String value = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE docID of attestation of " + selectedSense.getName() + " to " + value);
    }

    public void attestationKeyupEvent(AjaxBehaviorEvent e) {
        String value = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE attestation of " + selectedSense.getName() + " to " + value);
    }

    public void dictionaryPreferredChanged() {
        log(Level.INFO, loginController.getAccount(), "UPDATE attestation of " + selectedSense.getName() + " to " + dictionaryPreferred);
    }

    public List<SelectItem> getDocuments() {
        List<String> intDocs = documentationManager.getAbbreviationDocuments("Internal");
        SelectItemGroup groupInt = new SelectItemGroup("Internal Documents");
        List<SelectItem> internals = new ArrayList<SelectItem>();
        for (String doc : intDocs) {
            internals.add(new SelectItem(doc));
        }
        groupInt.setSelectItems(internals.toArray(new SelectItem[internals.size()]));

        List<String> extDocs = documentationManager.getAbbreviationDocuments("External");
        SelectItemGroup groupExt = new SelectItemGroup("External Documents");
        List<SelectItem> externals = new ArrayList<SelectItem>();
        for (String doc : extDocs) {
            externals.add(new SelectItem(doc));
        }
        groupExt.setSelectItems(externals.toArray(new SelectItem[externals.size()]));
        List<SelectItem> docsList = new ArrayList();
        docsList.add(groupInt);
        docsList.add(groupExt);
        return docsList;
    }

    public ArrayList<AttestedForm> getAttestedForms() {
        ArrayList<AttestedForm> afs = new ArrayList<>();
        afs.add(new AttestedForm(lexiconControllerFormDetail.getLemma().getFormWrittenRepr(), -1));
        lexiconControllerFormDetail.getForms().forEach((fd) -> {
            afs.add(new AttestedForm(fd.getFormWrittenRepr(), lexiconControllerFormDetail.getForms().indexOf(fd)));
        });
        return afs;
    }

    public void save() throws IOException, OWLOntologyStorageException {
        log(Level.INFO, loginController.getAccount(), "Attestation save() form: " + form);
        int formIndex = Integer.parseInt(form);
        if (formIndex == -1) {
            formUri = lexiconControllerFormDetail.getLemma().getIndividual();
            attestedForm = lexiconControllerFormDetail.getLemma().getFormWrittenRepr();
        } else {
            formUri = lexiconControllerFormDetail.getForms().get(formIndex).getIndividual();
            attestedForm = lexiconControllerFormDetail.getForms().get(formIndex).getFormWrittenRepr();
        }
        attestationManager.insertAttestation(selectedSense.getName(),
                docID,
                form.equals("-1") ? "Lemma" : "Form",
                formUri, attestedForm, attestation, pageNumber, lineNumber, dictionaryPreferred, loginController.getAccount());
        log(Level.INFO, loginController.getAccount(), "SAVE new attestation of " + selectedSense.getName());
        FacesMessage message = new FacesMessage("Successful", "Attestation saved");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void checkDictionayPreferred(Attestation att) {
        log(Level.INFO, loginController.getAccount(), ("\"" + att.getAttestation() + "\" " + (att.isDictionaryPreferred() ? "marked" : "unmarked") + " as dictionary preferred."));
        if (att.isDictionaryPreferred()) {
            if (attestationManager.isDictionaryPreferreable(att)) {
                attestationManager.setDictionaryPreferred(att, true);
                info("template.attestation", "attestation.view.message.dictionaryChecked");
            } else {
                att.setDictionaryPreferred(false);
                warn("template.attestation", "attestation.view.message.dictionaryNotChecked");
            }
        } else {
            att.setDictionaryPreferred(false);
            info("template.attestation", "attestation.view.message.removedAttestation");
        }
    }

    public List<SenseData> getSenses() {
        return lexiconControllerSenseDetail.getSenses();
    }

    public List<Attestation> getAttestations(SenseData sd) {
        return attestationManager.loadAttestationsBySense(sd.getName());
    }

    public List<Attestation> getAttestationsForDictionary(String sense) {
        return attestationManager.loadAttestationsForDictionaryBySense(sense);
    }
    
    public void deleteAttestations(String senseUri, String formUri, String form, String att) {
        attestationManager.remove(senseUri, formUri, form, att);
    }

    public String getHighlightAttestations(String form, String text) {
        log(Level.DEBUG, loginController.getAccount().getUsername() + " Searching for: " + form + " on text: " + String.format("%.30s ...",text));

        String openMark = "<mark>";
        String closeMark = "</mark>";
        StringBuilder highlightedText = new StringBuilder();
        Pattern pattern = Pattern.compile("\\b" + form + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        int prevEndMatch = 0;
        int startMatch = 0;
        int endMatch = 0;
        while (matcher.find()) {
            startMatch = matcher.start();
            endMatch = matcher.end();
            highlightedText.append(text.substring(prevEndMatch, startMatch))
                    .append(openMark)
                    .append(text.substring(startMatch, endMatch))
                    .append(closeMark);
            prevEndMatch = endMatch;
        }
        highlightedText.append(text.substring(prevEndMatch));
        log(Level.DEBUG, loginController.getAccount().getUsername() + " highlightedText: " + highlightedText);

        return highlightedText.toString();
    }

    public String getAttestationForm() {

        log(Level.INFO, loginController.getAccount().getUsername() + " Attestation get attersted form: " + form);
        int formIndex = Integer.parseInt(form);
        if (formIndex == -1) {
            attestedForm = lexiconControllerFormDetail.getLemma().getFormWrittenRepr();
        } else {
            attestedForm = lexiconControllerFormDetail.getForms().get(formIndex).getFormWrittenRepr();
        }

        return attestedForm;
    }

    public static class AttestedForm {

        private String form;
        private int index;

        public String getForm() {
            return form;
        }

        public void setForm(String form) {
            this.form = form;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public AttestedForm(String form, int index) {
            this.form = form;
            this.index = index;
        }

    }
    
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        if (newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }
}
