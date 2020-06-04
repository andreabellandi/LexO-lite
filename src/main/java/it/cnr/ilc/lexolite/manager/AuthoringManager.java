/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.HibernateUtil;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.domain.Authoring;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author andrea
 */
public class AuthoringManager implements Serializable {

    @Inject
    private DomainManager domainManager;

    public void insertAuthoring(Account account, String type, String iri) {
        Authoring authoring = new Authoring();
        authoring.setAccount(account);
        authoring.setType(type);
        authoring.setIRI(iri);
        domainManager.insert(authoring);
    }

    public void updateNoteAuthoring(Account account, String type, String iri) {
        if (!noteIsAuthored(type, iri)) {
            insertAuthoring(account, type, iri);
        }
    }

    public Authoring loadAuthoringByResource(String type, String iri) {
        Criteria criteria = HibernateUtil.getSession().createCriteria(Authoring.class);
        criteria.add(Restrictions.eq("type", type)).add(Restrictions.eq("iri", iri));
        List<Authoring> list = criteria.list();
        return list.isEmpty() ? null : list.get(0);
    }

    public void removeAuthoring(String type, String iri) {
        Authoring authoring = loadAuthoringByResource(type, iri);
        if (authoring != null) {
            domainManager.delete(authoring);
        }
    }

    private static final String CHECK_NOTE_PRESENCE = "select count(*) from Authoring where status = 1 and IRI = ':iri' and type = ':type'";

    private boolean noteIsAuthored(String type, String iri) {
        String sql = CHECK_NOTE_PRESENCE.replaceAll(":iri", iri).replaceAll(":type", type);
        SQLQuery query = HibernateUtil.getSession().createSQLQuery(sql);
        return ((Number) query.uniqueResult()).intValue() > 0;
    }
}
