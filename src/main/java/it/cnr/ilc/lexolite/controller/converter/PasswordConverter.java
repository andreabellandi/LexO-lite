package it.cnr.ilc.lexolite.controller.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author oakgen
 */
@FacesConverter(value = "passwordConverter")
public class PasswordConverter implements Converter {
    
    public static final String HIDDEN_PASSWORD = "••••••••";

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return string;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        return object == null || object.toString().isEmpty() ? "" : HIDDEN_PASSWORD;
    }
    
}
