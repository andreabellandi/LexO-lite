/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.manager.LexiconManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class StatisticsController extends BaseController implements Serializable {

    @Inject
    private PropertyValue propertyValue;
    @Inject
    private LexiconControllerTabViewList lexiconCreationControllerTabViewList;
    @Inject
    private LexiconControllerSenseDetail lexiconCreationViewSenseDetail;
    @Inject
    private LexiconControllerLinkedLexicalEntryDetail lexiconCreationControllerRelationDetail;
    @Inject
    private LexiconControllerDictionary lexiconExplorationControllerDictionary;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LoginController loginController;

    private PieChartModel pieModel1;

    public PieChartModel getPieModel1() {
        createPieModel1();
        return pieModel1;
    }

    private void createPieModel1() {
        pieModel1 = new PieChartModel();
        pieModel1.set("Ancient Occitan", 1833);
        pieModel1.set("Ancient Catalan", 18);

        pieModel1.set("French", 1012);
        pieModel1.set("English", 741);

        pieModel1.set("Hebrew", 361);
        pieModel1.set("Aramaic", 37);
        pieModel1.set("Latin", 160);
        pieModel1.set("Arabic", 805);

//        pieModel1.setTitle("Multilingual lexicon languages");
        pieModel1.setLegendPosition("w");
        pieModel1.setShowDataLabels(true);
    }

    public void itemSelect(ItemSelectEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",
                "Item Index: " + event.getItemIndex() + ", Series Index:" + event.getSeriesIndex());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        System.out.println("AAAAAAAHHHHHHHH");
    }

    
    
    public static class User {
        
        private String username;
        private String password;
        private String role;
        private String editedEntries;
        private String validatedEntries;
        private String percent;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getEditedEntries() {
            return editedEntries;
        }

        public void setEditedEntries(String editedEntries) {
            this.editedEntries = editedEntries;
        }

        public String getValidatedEntries() {
            return validatedEntries;
        }

        public void setValidatedEntries(String validatedEntries) {
            this.validatedEntries = validatedEntries;
        }

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }
        
        
        
        public User(String us, String pwd, String role, String ee, String ve, String perc) {
            this.editedEntries = ee;
            this.password = pwd;
            this.percent = perc;
            this.role = role;
            this.username = us;
            this.validatedEntries = ve;
        }
        
    }

}
