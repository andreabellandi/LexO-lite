/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author andrea
 */

@FacesConverter(value = "simpleTextConverter")
public class SimpleTextConverter implements Converter {
    
     @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        string = replaceQuote(string);
        return string;
    }

    private String replaceQuote(String string) {
        string = string.trim();
        return string;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        return (String) object;
    }
    
}
