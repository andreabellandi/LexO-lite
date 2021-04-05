/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 *
 * @author andrea
 */
@Entity
public class Attestation extends SuperEntity {

    private String senseUri;
    private String formUri;
    private String form;
    private String formType;
    private String docID;
    private String attestation;
    private String pageNumber;
    private String lineNumber;
    private boolean dictionaryPreferred;
    private Account account;

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getAttestation() {
        return attestation;
    }

    public void setAttestation(String attestation) {
        this.attestation = attestation;
    }

    public String getSenseUri() {
        return senseUri;
    }

    public void setSenseUri(String senseUri) {
        this.senseUri = senseUri;
    }

    public String getFormUri() {
        return formUri;
    }

    public void setFormUri(String formUri) {
        this.formUri = formUri;
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

    @ManyToOne(fetch = FetchType.LAZY)
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
