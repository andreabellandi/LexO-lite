/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.HibernateUtil;
import it.cnr.ilc.lexolite.domain.SuperEntity;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author oakgen
 */
public class DomainManager implements Serializable {

    public Long insert(SuperEntity entity) {
        entity.setTime(new Date());
        entity.setStatus(SuperEntity.Status.VALID);
        HibernateUtil.getSession().save(entity);
        return entity.getId();
    }

    public void hiddenUpdate(SuperEntity entity) {
        entity.setStatus(SuperEntity.Status.VALID);
        HibernateUtil.getSession().merge(entity);
    }

    public void update(SuperEntity entity) {
        entity.setTime(new Date());
        entity.setStatus(SuperEntity.Status.VALID);
        HibernateUtil.getSession().merge(entity);
    }

    public Long updateWithHistory(SuperEntity entity) {
        HibernateUtil.getSession().evict(entity);
        SuperEntity previous = (SuperEntity) HibernateUtil.getSession().get(entity.getClass(), entity.getId());
        HibernateUtil.getSession().evict(previous);
        previous.setId(null);
        previous.setStatus(SuperEntity.Status.HISTORY);
        previous.setValid(entity);
        HibernateUtil.getSession().save(previous);
        entity.setTime(new Date());
        entity.setStatus(SuperEntity.Status.VALID);
        HibernateUtil.getSession().merge(entity);
        return previous.getId();
    }

    public void delete(SuperEntity entity) {
        entity.setTime(new Date());
        entity.setStatus(SuperEntity.Status.REMOVED);
        HibernateUtil.getSession().merge(entity);
    }

    public Long deleteWithHistory(SuperEntity entity) {
        HibernateUtil.getSession().evict(entity);
        SuperEntity previous = (SuperEntity) HibernateUtil.getSession().get(entity.getClass(), entity.getId());
        HibernateUtil.getSession().evict(previous);
        previous.setId(null);
        previous.setStatus(SuperEntity.Status.HISTORY);
        previous.setValid(entity);
        HibernateUtil.getSession().save(previous);
        entity.setTime(new Date());
        entity.setStatus(SuperEntity.Status.REMOVED);
        HibernateUtil.getSession().merge(entity);
        return previous.getId();
    }
}
