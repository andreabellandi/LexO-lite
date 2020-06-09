/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.HibernateUtil;
import it.cnr.ilc.lexolite.controller.StatisticsController.UserStatistics;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.domain.Authoring;
import it.cnr.ilc.lexolite.domain.Authoring.IRIType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author andrea
 */
public class AuthoringManager implements Serializable {

    @Inject
    private DomainManager domainManager;

    private void insertAuthoring(Account account, IRIType type, String iri) {
        Authoring authoring = new Authoring();
        authoring.setAccount(account);
        authoring.setType(type);
        authoring.setIRI(iri);
        domainManager.insert(authoring);
    }

    public void updateIRIAuthoring(Account account, IRIType type, String iri) {
        if (!IRIisAuthored(type, iri)) {
            insertAuthoring(account, type, iri);
        }
    }

    private Authoring loadAuthoringByResource(String type, String iri) {
        Criteria criteria = HibernateUtil.getSession().createCriteria(Authoring.class);
        criteria.add(Restrictions.eq("type", type)).add(Restrictions.eq("iri", iri));
        List<Authoring> list = criteria.list();
        return list.isEmpty() ? null : list.get(0);
    }

    public void removeAuthoring(IRIType type, String iri) {
        Authoring authoring = loadAuthoringByResource(type.name(), iri);
        if (authoring != null) {
            domainManager.delete(authoring);
        }
    }

    private static final String CHECK_IRI_PRESENCE = "select count(*) from Authoring where status = 1 and IRI = ':iri' and type = ':type'";

    private boolean IRIisAuthored(IRIType type, String iri) {
        String sql = CHECK_IRI_PRESENCE.replaceAll(":iri", iri).replaceAll(":type", type.name());
        SQLQuery query = HibernateUtil.getSession().createSQLQuery(sql);
        return ((Number) query.uniqueResult()).intValue() > 0;
    }

    private static final String USER_STAT = "SELECT aut.type, acc.username, at.name "
            + "FROM Authoring aut join Account acc on aut.account_id = acc.id join AccountType at on acc.type_id = at.id "
            + "WHERE acc.status = 1 and aut.status = 1 ORDER BY acc.username";

    public List<Object[]> getUserStatistics() {
        SQLQuery query = HibernateUtil.getSession().createSQLQuery(USER_STAT);
        return query.list();
    }
    
    private static final String USER_STAT_DETAILS = "SELECT aut.IRI, aut.time "
            + "FROM Authoring aut join Account acc on aut.account_id = acc.id "
            + "WHERE acc.status = 1 and aut.status = 1 and acc.username = ':username' "
            + "ORDER BY aut.IRI";
    
    public List<Object[]> getStatDetails(String username) {
        String sql = USER_STAT_DETAILS.replaceAll(":username", username);
        SQLQuery query = HibernateUtil.getSession().createSQLQuery(sql);
        return query.list();
    }

}
