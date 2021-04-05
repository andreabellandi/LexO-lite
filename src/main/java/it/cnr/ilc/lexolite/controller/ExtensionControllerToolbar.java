/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.domain.ExtensionAttribute;
import it.cnr.ilc.lexolite.manager.ExtensionAttributeManager;
import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class ExtensionControllerToolbar extends BaseController implements Serializable {

    @Inject
    private ExtensionAttributeManager extensionAttributeManager;
    @Inject
    private ExtensionAttributeControllerTable extensionAttributeControllerTable;
    
    public void addExtension() {
        ExtensionAttribute ea = extensionAttributeManager.newEmptyExtension();
        extensionAttributeControllerTable.insertFirst(ea);
        extensionAttributeControllerTable.info("template.extensionAttribute", "extensionAttribute.toolbar.message.created");
    }

    public void removeExtension() {
        
    }

}
