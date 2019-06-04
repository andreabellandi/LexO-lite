/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.domain;

import java.util.Map;
import javax.faces.context.FacesContext;

/**
 *
 * @author oakgen
 */
public interface Labeled {

    public String getName();

    public Map<String, String> getLabels();

    public static String getLabel(Labeled labeled) {
        String label = null;
        if (labeled.getLabels() != null) {
            String language = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale().getLanguage();
            label = labeled.getLabels().get(language);
        }
        return label == null ? labeled.getName() : label;
    }
}
