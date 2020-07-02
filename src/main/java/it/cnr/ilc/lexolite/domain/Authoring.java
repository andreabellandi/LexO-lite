/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.domain;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 *
 * @author andrea
 */
@Entity
public class Authoring extends SuperEntity {

    public enum IRIType {
        LEXICAL_ENTRY,
        FORM,
        LEXICAL_SENSE,
        LEXICAL_ENTRY_NOTE,
        FORM_NOTE,
        SENSE_NOTE
    }

    private String IRI;
    private IRIType type;
    private Account account;

    @Enumerated
    public IRIType getType() {
        return type;
    }

    public void setType(IRIType type) {
        this.type = type;
    }

    public String getIRI() {
        return IRI;
    }

    public void setIRI(String IRI) {
        this.IRI = IRI;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
