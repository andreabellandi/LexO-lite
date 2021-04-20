package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.domain.AccountType.Permission;
import it.cnr.ilc.lexolite.manager.AccountManager;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import org.slf4j.event.Level;
import javax.inject.Named;
import javax.inject.Inject;

/**
 *
 * @author oakgen
 */
@SessionScoped
@Named
public class TemplateController extends BaseController implements Serializable {

    @Inject
    private LoginController loginController;
    @Inject
    private AccountManager accountManager;

    public String homeAction() {
        log(Level.INFO, loginController.getAccount(), "navigate to home");
        return "homeView?faces-redirect=true";
    }

    public boolean isEnableAccount() {
        return accountManager.hasPermission(Permission.READ_ALL, AccountManager.Access.ACCOUNT, loginController.getAccount());
    }

    public String accountAction() {
        log(Level.INFO, loginController.getAccount(), "navigate to accounts");
        return "accountView?faces-redirect=true";
    }

    public boolean isEnableLexiconEditor() {
        return accountManager.hasPermission(Permission.READ_ALL, AccountManager.Access.LEXICON_EDITOR, loginController.getAccount());
    }

    public String lexiconEditorAction() {
        log(Level.INFO, loginController.getAccount(), "navigate to lexicon editor");
        return "lexiconView?faces-redirect=true";
    }

    public boolean isEnableLexiconStat() {
        return accountManager.hasPermission(Permission.READ_ALL, AccountManager.Access.LEXICON_STAT, loginController.getAccount());
    }

    public String lexiconStatAction() {
        log(Level.INFO, loginController.getAccount(), "navigate to lexicon statistics");
        return "statisticsView?faces-redirect=true";
    }

    public String lexiconExtensionAction() {
        log(Level.INFO, loginController.getAccount(), "navigate to lexicon extension");
        return "extensionView?faces-redirect=true";
    }
}
