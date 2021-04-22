/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.HibernateUtil;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.domain.Attestation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author andreabellandi
 */
public class AttestationManager implements Serializable {

    @Inject
    private DomainManager domainManager;
    
    private static final String DELETE_ATTESTATION_BY_SENSE = "delete from Attestation where senseUri = :senseUri";
    private static final String DELETE_ATTESTATION_BY_FORM = "delete from Attestation where formUri = :formUri and form = :form "
            + "and senseUri = :senseUri and attestation = :attestation";

    public boolean isDictionaryPreferreable(Attestation att) {
        Criteria criteria = HibernateUtil.getSession().createCriteria(Attestation.class);
        criteria.add(Restrictions.eq("senseUri", att.getSenseUri()));
        criteria.add(Restrictions.eq("formUri", att.getFormUri()));
        criteria.add(Restrictions.eq("dictionaryPreferred", true));
        return (criteria.list().size() <= 2);
    }
    
    public void setDictionaryPreferred(Attestation att, boolean value) {
        att.setDictionaryPreferred(value);
        domainManager.update(att);
    }
    
    public void remove(String senseUri) {
        SQLQuery query = HibernateUtil.getSession().createSQLQuery(DELETE_ATTESTATION_BY_SENSE);
        query.setString("senseUri", senseUri);
        query.executeUpdate();
    }
    
    public void remove(String senseUri, String formUri, String form, String att) {
        SQLQuery query = HibernateUtil.getSession().createSQLQuery(DELETE_ATTESTATION_BY_FORM);
        query.setString("senseUri", senseUri);
        query.setString("formUri", formUri);
        query.setString("form", form);
        query.setString("attestation", att);
        query.executeUpdate();
    }

    public List<Attestation> loadAttestationsBySense(String senseUri) {
        Criteria criteria = HibernateUtil.getSession().createCriteria(Attestation.class);
        criteria.add(Restrictions.eq("senseUri", senseUri));
        List<Attestation> list = criteria.list();
        return list.isEmpty() ? new ArrayList() : list;
    }

    public List<Attestation> loadAttestationsByForm(String formUri) {
        Criteria criteria = HibernateUtil.getSession().createCriteria(Attestation.class);
        criteria.add(Restrictions.eq("formUri", formUri));
        List<Attestation> list = criteria.list();
         return list.isEmpty() ? new ArrayList() : list;
    }
    
     public List<Attestation> loadAttestationsForDictionaryBySense(String senseUri) {
        Criteria criteria = HibernateUtil.getSession().createCriteria(Attestation.class);
        criteria.add(Restrictions.eq("senseUri", senseUri));
        criteria.add(Restrictions.eq("dictionaryPreferred", true));
        List<Attestation> list = criteria.list();
        return list.isEmpty() ? new ArrayList() : list;
    }

    public List<Attestation> loadAttetationsForDictionary(String senseUri) {
        Criteria criteria = HibernateUtil.getSession().createCriteria(Attestation.class);
        criteria.add(Restrictions.eq("senseUri", senseUri));
        criteria.add(Restrictions.eq("dictionaryPreferred", true));
        List<Attestation> list = criteria.list();
         return list.isEmpty() ? new ArrayList() : list;
    }

    public void updateAttestationURIs(AttestationRenaming renamings) {
        for (AttestationRenaming.AttestationFormUris attForm : renamings.getAttestationFormUris()) {
            for (Attestation att : loadAttestationsByForm(attForm.getOldFormUri())) {
                att.setFormUri(attForm.getNewFormUri());
                if (attForm.getOldForm() != null && attForm.getNewForm() != null) {
                    att.setForm(attForm.getNewForm());
                }
                domainManager.update(att);
            }
        }
        for (AttestationRenaming.AttestationSenseUris attSense : renamings.getAttestationSenseUris()) {
            for (Attestation att : loadAttestationsBySense(attSense.getOldSenseUri())) {
                att.setSenseUri(attSense.getNewSenseUri());
                domainManager.update(att);
            }
        }
    }

    public void updateAttestatonFormUri(String oldUri, String newUri, String newForm) {
        for (Attestation att : loadAttestationsByForm(oldUri)) {
            att.setFormUri(newUri);
            att.setForm(newForm);
            domainManager.update(att);
        }
    }

    public Attestation insertAttestation(String senseUri, String docID, String formType, String formUri, String form, String attestation, String pageNumber, String lineNumber, boolean dictionaryPreferred, Account acc) {
        Attestation a = new Attestation();
        a.setSenseUri(senseUri);
        a.setFormUri(formUri);
        a.setFormType(formType);
        a.setForm(form);
        a.setPageNumber(pageNumber);
        a.setLineNumber(lineNumber);
        a.setAccount(acc);
        a.setDictionaryPreferred(dictionaryPreferred);
        a.setAttestation(attestation);
        a.setDocID(docID);
        domainManager.insert(a);
        return a;
    }
//
//    public void updateExtensionAttribute(ExtensionAttribute ea, String fieldName, String value) {
//        if (fieldName.contains("attributeName")) {
//            ea.setName(value);
//        } else if (fieldName.contains("attributeLabel")) {
//            ea.setLabel(value.replaceAll("\\s+", ""));
//        } else if (fieldName.contains("attributeRelation")) {
//            ea.setRelation(value);
//        } else if (fieldName.contains("attributeDatatype")) {
//            ea.setType(value);
//        } else if (fieldName.contains("attributeDomain")) {
//            ea.setDomain(value);
//        } else if (fieldName.contains("attributeCardinality")) {
//            ea.setCardinality(value);
//        } 
//        domainManager.update(ea);
//    }
}
