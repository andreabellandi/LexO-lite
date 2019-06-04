package it.cnr.ilc.lexolite.controller.action;

import it.cnr.ilc.lexolite.controller.AccountControllerTable;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.manager.AccountManager;

/**
 *
 * @author oakgen
 */
public class AccountNewAction implements Action {

    private final AccountControllerTable accountControllerTable;
    private final AccountManager accountManager;

    public AccountNewAction(AccountControllerTable accountControllerTable, AccountManager accountManager) {
        this.accountControllerTable = accountControllerTable;
        this.accountManager = accountManager;
    }

    @Override
    public void doAction() throws ActionAbort {
        if (accountManager.checkCreation()) {
            accountControllerTable.warn("template.account", "account.toolbar.message.creationPresent");
            throw new ActionAbort();
        } else {
            Account account = accountManager.newEmptyAccount();
            accountControllerTable.insertFirst(account);
            accountControllerTable.info("template.account", "account.toolbar.message.created");
        }
    }

    @Override
    public void undoAction() throws ActionAbort {
        accountControllerTable.warn("template.account", "template.message.unsupported");
        throw new ActionAbort();
    }

    @Override
    public void redoAction() throws ActionAbort {
        accountControllerTable.warn("template.account", "template.message.unsupported");
        throw new ActionAbort();
    }

}
