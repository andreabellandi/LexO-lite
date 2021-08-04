/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.domain.AccountType;
import it.cnr.ilc.lexolite.domain.Document;
import it.cnr.ilc.lexolite.manager.AccountManager;
import it.cnr.ilc.lexolite.manager.DocumentData;
import it.cnr.ilc.lexolite.manager.DocumentationManager;
import java.io.Serializable;
import java.util.Objects;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.event.Level;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerDocument extends BaseController implements Serializable {

    @Inject
    private LexiconControllerTabViewList lexiconControllerTabViewList;
    @Inject
    private DocumentationManager documentationManager;
    @Inject
    private LoginController loginController;
    @Inject
    private AccountManager accountManager;

    private boolean docRendered = false;
    private boolean newAction = false;
    private boolean docAlreadyExists = false;
    private boolean addDocButtonDisabled = true;

    private DocumentData doc = new DocumentData();

    public boolean isDocumentTabEnabled() {
        return accountManager.hasPermission(AccountType.Permission.WRITE_ALL, AccountManager.Access.LEXICON_EDITOR, loginController.getAccount());
    }

    public boolean isDocRendered() {
        return docRendered;
    }

    public void setDocRendered(boolean docRendered) {
        this.docRendered = docRendered;
    }

    public boolean isNewAction() {
        return newAction;
    }

    public void setNewAction(boolean newAction) {
        this.newAction = newAction;
    }

    public boolean isDocAlreadyExists() {
        return docAlreadyExists;
    }

    public void setDocAlreadyExists(boolean docAlreadyExists) {
        this.docAlreadyExists = docAlreadyExists;
    }

    public boolean isAddDocButtonDisabled() {
        return addDocButtonDisabled;
    }

    public void setAddDocButtonDisabled(boolean addDocButtonDisabled) {
        this.addDocButtonDisabled = addDocButtonDisabled;
    }

    public DocumentData getDoc() {
        return doc;
    }

    public void cleanForm() {
        doc.setDocId(Long.valueOf(-1));
        doc.setAbbreviation("");
        doc.setSourceType("");
        doc.setTitle("");
        doc.setType("");
    }

    public void docTypeChanged(AjaxBehaviorEvent e) {
        String type = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Document type " + doc.getAbbreviation() + " to " + type);
        doc.setType(type);
        doc.setSaveButtonDisabled(!isSavableDocument());
    }

    public void docAbbChanged(AjaxBehaviorEvent e) {
        String abb = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Document abbreviation of " + doc.getDocId() + " to " + doc.getAbbreviation());
        doc.setAbbreviation(abb);
        if (abbreviationAlreadyExists(abb)) {
            docAlreadyExists = true;
            doc.setSaveButtonDisabled(true);
        } else {
            docAlreadyExists = false;
            doc.setSaveButtonDisabled(!isSavableDocument());
        }
    }

    private boolean abbreviationAlreadyExists(String abb) {
        if (documentationManager.abbreviationDocumentAlreadyExists(abb)) {
            return true;
        } else {
            return false;
        }
    }

    public void docTitleChanged(AjaxBehaviorEvent e) {
        String tit = (String) e.getComponent().getAttributes().get("value");
        log(Level.INFO, loginController.getAccount(), "UPDATE Document title of " + doc.getAbbreviation() + " to " + doc.getTitle());
        doc.setTitle(tit);
        doc.setSaveButtonDisabled(!isSavableDocument());
    }

    // invoked by the controller after an user selected a document in the tabview
    public void addDocument(LexiconControllerTabViewList.DocTreeNode dtn) {
        doc.clear();
        setDocument(dtn.getDocId());
    }

    private void setDocument(Long ID) {
        Document d = documentationManager.getDocumentByID(ID);
        doc.setDocId(d.getId());
        doc.setAbbreviation(d.getAbbreviation());
        doc.setSourceType(d.getSourceType());
        doc.setTitle(d.getTitle());
        doc.setType(d.getType());
    }

    public void removeDocument() {
        if (!Objects.equals(doc.getDocId(), Long.valueOf(-1))) {
            documentationManager.deleteDocumentByID(doc.getDocId(), doc.getType());
            log(Level.INFO, loginController.getAccount(), "DELETE Document " + doc.getAbbreviation());
            info("template.message.deleteDocument.summary", "template.message.deleteDocument.description", doc.getAbbreviation());
            doc.clear();
            docRendered = false;
            addDocButtonDisabled = true;
            lexiconControllerTabViewList.initDocumentTabView();
        } else {
            docRendered = false;
        }
    }

    public void saveDocument() {
        doc.setSaveButtonDisabled(true);
        doc.setDeleteButtonDisabled(false);
        addDocButtonDisabled = false;
        info("template.message.saveDocument.summary", "template.message.saveDocument.description", doc.getAbbreviation());
        if (newAction) {
            Long id = documentationManager.insertDocument(doc);
            setDocument(id);
            log(Level.INFO, loginController.getAccount(), "SAVE new document " + doc.getAbbreviation());

        } else {
            documentationManager.updateDocument(doc);

            setDocument(doc.getDocId());
            log(Level.INFO, loginController.getAccount(), "SAVE updated document " + doc.getAbbreviation());

        }
        newAction = false;
        lexiconControllerTabViewList.initDocumentTabView();
    }

    private boolean isSavableDocument() {
        return (!doc.getAbbreviation().isEmpty() && (!docAlreadyExists)
                && (doc.getType().equals("Article") || doc.getType().equals("Book") || doc.getType().equals("Map") || doc.getType().equals("Manuscript") || doc.getType().equals("Dictionary")
                || doc.getType().equals("Text")));
    }

}
