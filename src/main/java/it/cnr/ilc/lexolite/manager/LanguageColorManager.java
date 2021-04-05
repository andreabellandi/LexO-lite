/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.HibernateUtil;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.domain.LanguageColor;
import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author andreabellandi
 */
public class LanguageColorManager implements Serializable {

    @Inject
    private DomainManager domainManager;

    private static HashMap<String, String> langColors = new HashMap();

    public HashMap<String, String> getLangColors() {
        return langColors;
    }

    public void setLangColors(HashMap<String, String> langColors) {
        this.langColors = langColors;
    }

    private static final String COLOR_BY_LANG = "SELECT languageTag, color FROM LanguageColor";

    static {
        langColors.clear();
        for (Iterator it = HibernateUtil.getSession().createSQLQuery(COLOR_BY_LANG).list().iterator(); it.hasNext();) {
            Object[] obj = (Object[]) it.next();
            langColors.put(obj[0].toString(), obj[1].toString());
        }
    }

    public void insertLanguageColor(Account account, String lang, String creator, String description, String color) {
        LanguageColor lc = new LanguageColor();
        lc.setLanguageTag(lang);
        lc.setColor(color);
        lc.setCreator(creator);
        lc.setDescription(description);
        lc.setAccount(account);
        domainManager.insert(lc);
    }

    public LanguageColor loadLanguageColorByTag(String lang) {
        Criteria criteria = HibernateUtil.getSession().createCriteria(LanguageColor.class);
        criteria.add(Restrictions.eq("languageTag", lang));
        List<LanguageColor> list = criteria.list();
        return list.isEmpty() ? null : list.get(0);
    }

    public Color getColorByLanguage(String lang) {
        Criteria criteria = HibernateUtil.getSession().createCriteria(LanguageColor.class);
        criteria.add(Restrictions.eq("languageTag", lang));
        List<LanguageColor> list = criteria.list();
        if (list.isEmpty()) {
            return null;
        } else {
            return new Color(
                    Integer.valueOf(list.get(0).getColor().substring(0, 2), 16),
                    Integer.valueOf(list.get(0).getColor().substring(2, 4), 16),
                    Integer.valueOf(list.get(0).getColor().substring(4, 6), 16));
        }
    }

}
