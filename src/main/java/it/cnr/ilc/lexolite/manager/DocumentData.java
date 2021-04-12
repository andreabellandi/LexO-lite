/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.domain.Document;
import java.io.Serializable;

/**
 *
 * @author andrea
 */
public class DocumentData implements Serializable {

    private boolean saveButtonDisabled;
    private boolean deleteButtonDisabled;

    private Long docId;
    private String abbreviation;
    private String type;
    private String title;
    private String sourceType;

    public DocumentData(Document d) {
        this.saveButtonDisabled = true;
        this.deleteButtonDisabled = false;
        this.docId = d.getId();
        this.abbreviation = d.getAbbreviation();
        this.type = d.getType();
        this.title = d.getTitle();
        this.sourceType = d.getSourceType();
    }

    public DocumentData() {
        this.saveButtonDisabled = true;
        this.deleteButtonDisabled = false;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
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

    public boolean isSaveButtonDisabled() {
        return saveButtonDisabled;
    }

    public void setSaveButtonDisabled(boolean saveButtonDisabled) {
        this.saveButtonDisabled = saveButtonDisabled;
    }

    public boolean isDeleteButtonDisabled() {
        return deleteButtonDisabled;
    }

    public void setDeleteButtonDisabled(boolean deleteButtonDisabled) {
        this.deleteButtonDisabled = deleteButtonDisabled;
    }

    public void clear() {
        this.saveButtonDisabled = true;
        this.deleteButtonDisabled = false;
    }

}
