/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.HibernateUtil;
import it.cnr.ilc.lexolite.domain.Document;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author andrea
 */
public class DocumentationManager implements Serializable {

    @Inject
    private DomainManager domainManager;

    public List<Document> getDocuments() {
        return HibernateUtil.getSession().createCriteria(Document.class).addOrder(Order.asc("abbreviation")).list();
    }

    public Document getDocumentByID(Long id) {
        return (Document) (HibernateUtil.getSession().get(Document.class, id));
    }

    public Document getDocumentByAbbreviation(String abbreviation) {
        return (Document) HibernateUtil.getSession().createCriteria(Document.class).add(Restrictions.eq("abbreviation", abbreviation)).list().get(0);
    }

    private final String docsAbb = "SELECT abbreviation FROM Document WHERE status = 1 and sourceType = '_SOURCE_TYPE_' ORDER BY abbreviation";

    public List<String> getAbbreviationDocuments(String sourceType) {
        SQLQuery sqlQuery = HibernateUtil.getSession().createSQLQuery(docsAbb.replaceAll("_SOURCE_TYPE_", sourceType));
        return sqlQuery.list();
    }

    public boolean abbreviationDocumentAlreadyExists(String abbreviation) {
        return (HibernateUtil.getSession().createCriteria(Document.class).add(Restrictions.eq("abbreviation", abbreviation)).list()).size() > 0;
    }

    public void deleteDocumentByID(Long id, String type) {
        domainManager.delete(getDocumentByID(id));
    }

    public Long insertDocument(DocumentData dd) {
        Document d = new Document();
        d.setAbbreviation(dd.getAbbreviation());
        d.setSourceType(dd.getSourceType());
        d.setTitle(dd.getTitle());
        d.setType(dd.getType());
        return domainManager.insert(d);
    }

    public void updateDocument(DocumentData dd) {
            Document d = getDocumentByID(dd.getDocId());
            d.setAbbreviation(dd.getAbbreviation());
            d.setSourceType(dd.getSourceType());
            d.setType(dd.getType());
            d.setTitle(dd.getTitle());
            domainManager.update(d);
    }

    private final String sqlDocumentType = "SELECT d.type "
            + "FROM ExternalAttestation ea join Document d on ea.document_id = d.id "
            + "WHERE ea.status = 1 and d.status = 1 and d.id = _ID_";

    public List<Object[]> getDocumentType(Long id) {
        SQLQuery sqlQuery = HibernateUtil.getSession().createSQLQuery(sqlDocumentType.replace("_ID_", id.toString()));
        return sqlQuery.list();
    }

}
