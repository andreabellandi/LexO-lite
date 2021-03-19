/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.controller.AccountControllerTable.AccountData;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.domain.ExtensionAttribute;
import it.cnr.ilc.lexolite.manager.AccountManager;
import it.cnr.ilc.lexolite.manager.ExtensionAttributeManager;
import it.cnr.ilc.lexolite.manager.PropertyValue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.log4j.Level;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class ExtensionAttributeControllerTable extends BaseController implements Serializable {

    @Inject
    private LoginController loginController;
    @Inject
    private AccountManager accountManager;
    @Inject
    private ExtensionAttributeManager extensionAttributeManager;
    @Inject
    private ExtensionControllerToolbar extensionControllerToolbar;
    @Inject
    private PropertyValue propertyValue;

    private final List<ExtensionAttributeData> data = new ArrayList();
    private ExtensionAttributeData selection;

    public ExtensionAttributeData getSelection() {
        return selection;
    }

    public void setSelection(ExtensionAttributeData selection) {
        this.selection = selection;
    }

    public List<ExtensionAttributeData> getData() {
        return data;
    }

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(PropertyValue propertyValue) {
        this.propertyValue = propertyValue;
    }

    @PostConstruct
    private void init() {
        data.clear();
        List<ExtensionAttribute> records = extensionAttributeManager.loadExtensionsAttribute();
        for (ExtensionAttribute ea : records) {
            data.add(new ExtensionAttributeData(ea));
        }
        selection = null;
    }

    public void onRowSelect(SelectEvent event) {
        ExtensionAttributeData ea = (ExtensionAttributeData) event.getObject();
        if (ea == null) {
            log(Level.INFO, loginController.getAccount(), "select null table extension attribute");
        } else {
            log(Level.INFO, loginController.getAccount(), "select extension attribute '" + ea.getName() + "'");
        }
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        if (newValue != null && !newValue.equals(oldValue)) {
            DataTable table = (DataTable) event.getSource();
            List<?> elements = (List<?>) table.getValue();
            ExtensionAttributeData ead = (ExtensionAttributeData) elements.get(event.getRowIndex());
            ExtensionAttribute ea = extensionAttributeManager.loadExtensionAttribute(ead.getId());
            extensionAttributeManager.updateExtensionAttribute(ea, event.getColumn().getColumnKey(), newValue.toString());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            log(Level.INFO, loginController.getAccount(), "Extension attribute changed " + event.getColumn().getHeaderText()
                    + " from " + oldValue.toString() + " to " + newValue.toString());
        }
    }

    public void insertFirst(ExtensionAttribute ea) {
        selection = new ExtensionAttributeData(ea);
        data.add(0, selection);
    }

    public void enableExtensionAttribute(ExtensionAttributeData ead) {
        log(Level.INFO, loginController.getAccount(), (ead.isActive() ? "enabled " : "disabled ") + " attribute extension " + ead.getName());
        ExtensionAttribute ea = extensionAttributeManager.loadExtensionAttribute(ead.getId());
        if (!isConsistentExtension(ea)) {
            ead.setActive(false);
            warn("template.extensionAttribute", "extensionAttribute.table.message.activationUnable");
        } else {
            extensionAttributeManager.setExtensionEnabled(ea, ead.isActive());
            info("template.extensionAttribute", ead.isActive() ? "extensionAttribute.table.active" : "extensionAttribute.table.noactive");
        }
    }
    
    private boolean isConsistentExtension(ExtensionAttribute ea) {
        return !ea.getCardinality().isEmpty() &&
                !ea.getDomain().isEmpty() &&
                !ea.getLabel().isEmpty() &&
                !ea.getName().isEmpty() &&
                !ea.getRelation().isEmpty() &&
                !ea.getType().isEmpty();
    }

    public class ExtensionAttributeData implements Serializable {

        private final Long id;
        private String name;
        private String label;
        private String relation;
        private String type;
        private String domain;
        private boolean active;
        private String cardinality;

        public ExtensionAttributeData(ExtensionAttribute ea) {
            this.id = ea.getId();
            this.name = ea.getName();
            this.label = ea.getLabel();
            this.relation = ea.getRelation();
            this.type = ea.getType();
            this.domain = ea.getDomain();
            this.cardinality = ea.getCardinality();
            this.active = ea.isActive();
        }


        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getCardinality() {
            return cardinality;
        }

        public void setCardinality(String cardinality) {
            this.cardinality = cardinality;
        }

    }

}
