/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.ilc.lexolite.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;

/**
 *
 * @author oakgen
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@FilterDef(name = "status")
@Filter(name = "status", condition = "status = 1")
public abstract class SuperEntity implements Serializable {

    public static enum Status {

        HISTORY, VALID, REMOVED
    }

    private Long id;
    private Status status;
    private Date time;
    private SuperEntity valid;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Enumerated
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public SuperEntity getValid() {
        return valid;
    }

    public void setValid(SuperEntity valid) {
        this.valid = valid;
    }

}
