/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.OntologyManager;
import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerTabViewToolbar extends BaseController implements Serializable {

    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LoginController loginController;
    @Inject
    private LexiconControllerTabViewList lexiconCreationControllerTabViewList;

    private MenuModel model = null;

    public void updateMenu(ArrayList<String> languages) {
        model = new DefaultMenuModel();
        for (String lang : languages) {
            model.addElement(getLanguageItemMenu(lang));
        }
    }

    private DefaultMenuItem getLanguageItemMenu(String lang) {
        DefaultMenuItem item = new DefaultMenuItem(lang);
        item.setIcon(lexiconCreationControllerTabViewList.getLexiconLanguage().equals(lang) ? "fa fa-check" : "fa fa-at");
        item.setStyleClass("lexiconTabViewLanguage");
        item.setValue(lang);
        item.setCommand("#{lexiconControllerTabViewList.loadLexicon('" + lang + "')}");
        item.setOnstart("PF('loadingDialog').show()");
        item.setOncomplete("PF('loadingDialog').hide()");
        item.setUpdate(":editViewTab :lexiconTabViewForm:tabView:lemmaGrid :lexiconTabViewForm:tabView:lemmaTree "
                + ":lexiconTabViewForm:tabView:formGrid :lexiconTabViewForm:tabView:formTree lexiconTabViewToolbarForm");
        item.setAjax(true);
        return item;
    }

    public MenuModel getModel() {
        if (model == null) {
            updateMenu(lexiconCreationControllerTabViewList.getDynamicLexicaMenuItems());
        }
        return model;
    }
    

}
