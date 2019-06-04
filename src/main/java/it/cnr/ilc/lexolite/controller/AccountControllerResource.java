package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.manager.AccountManager;
import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author oakgen
 */
@ViewScoped
@Named
public class AccountControllerResource extends BaseController implements Serializable {

    @Inject
    private AccountControllerTable accountControllerTable;
    @Inject
    private AccountManager accountManager;

}
