/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.HibernateUtil;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.domain.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
public class ImageManager implements Serializable {

    @Inject
    private DomainManager domainManager;

    private static final String DELETE_IMAGES = "delete from Image where fileName = :fileName";
    private static final String UPDATE_FILENAME = "UPDATE Image SET fileName = :newFileName WHERE fileName = :oldFileName";
    
    public void insertImage(Account account, String senseReference, String originalFileName, String fileName,
            String description, String source, String date) {
        Image img = new Image();
        img.setAccount(account);
        img.setFileName(fileName);
        img.setOriginalFileName(originalFileName);
        img.setSenseReference(senseReference);
        img.setDate(date);
        img.setDescription(description);
        img.setSource(source);
        domainManager.insert(img);
    }

    public List<Image> loadImages(String senseRef) {
        Criteria criteria = HibernateUtil.getSession().createCriteria(Image.class);
        criteria.add(Restrictions.like("senseReference", senseRef));
        criteria.addOrder(Order.asc("senseReference"));
        return criteria.list();
    }

    public void remove(String fileName) {
        SQLQuery query = HibernateUtil.getSession().createSQLQuery(DELETE_IMAGES);
        query.setString("fileName", fileName);
        query.executeUpdate();
    }
    
    public HashMap<String, String> update(ArrayList<AttestationRenaming.AttestationSenseUris> renamings) {
        HashMap<String, String> map = new HashMap();
         for (AttestationRenaming.AttestationSenseUris sense : renamings) {
            for (Image image : loadImages(sense.getOldSenseUri())) {
                map.put(image.getFileName(), sense.getNewSenseUri() + "_" + image.getOriginalFileName());
                image.setSenseReference(sense.getNewSenseUri());
                image.setFileName(sense.getNewSenseUri() + "_" + image.getOriginalFileName());
                domainManager.update(image);
            }
         }
         return map;
    }
}
