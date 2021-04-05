/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.HibernateUtil;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.domain.ExtensionAttribute;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author andrea
 */
public class ExtensionAttributeManager implements Serializable {

    @Inject
    private DomainManager domainManager;

    public List<ExtensionAttribute> loadExtensionsAttribute() {
        return HibernateUtil.getSession().createCriteria(ExtensionAttribute.class).list();
    }

    public List<ExtensionAttribute> loadActiveExtensionsAttribute() {
        Criteria criteria = HibernateUtil.getSession().createCriteria(ExtensionAttribute.class);
        criteria.add(Restrictions.eq("active", true));
        return criteria.list();
    }

    public ExtensionAttribute loadExtensionAttribute(Long id) {
        return (ExtensionAttribute) HibernateUtil.getSession().get(ExtensionAttribute.class, id);
    }

    public void setExtensionEnabled(ExtensionAttribute ea, boolean enabled) {
        ea.setActive(enabled);
        domainManager.update(ea);
    }

    public ExtensionAttribute newEmptyExtension() {
        ExtensionAttribute ea = new ExtensionAttribute();
        ea.setAccount(null);
        ea.setCardinality("single");
        ea.setName("");
        ea.setDomain("Lexical Entry");
        ea.setLabel("");
        ea.setRelation("owl:topDataProperty");
        ea.setType("string");
        ea.setActive(false);
        domainManager.insert(ea);
        return ea;
    }

    public void updateExtensionAttribute(ExtensionAttribute ea, String fieldName, String value) {
        if (fieldName.contains("attributeName")) {
            ea.setName(value.replaceAll("\\s+", ""));
        } else if (fieldName.contains("attributeLabel")) {
            ea.setLabel(value.replaceAll("\\s+", ""));
        } else if (fieldName.contains("attributeRelation")) {
            ea.setRelation(value);
        } else if (fieldName.contains("attributeDatatype")) {
            ea.setType(value);
        } else if (fieldName.contains("attributeDomain")) {
            ea.setDomain(value);
        } else if (fieldName.contains("attributeCardinality")) {
            ea.setCardinality(value);
        } 
        domainManager.update(ea);
    }
}
