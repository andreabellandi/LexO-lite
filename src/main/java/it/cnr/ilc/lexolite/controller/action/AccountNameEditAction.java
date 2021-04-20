package it.cnr.ilc.lexolite.controller.action;

import it.cnr.ilc.lexolite.controller.AccountControllerTable;
import it.cnr.ilc.lexolite.controller.AccountControllerTable.AccountData;
import it.cnr.ilc.lexolite.controller.LoginController;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.manager.AccountManager;
import org.slf4j.event.Level;

/**
 *
 * @author oakgen
 */
public class AccountNameEditAction implements Action {

    private final AccountControllerTable accountControllerTable;
    private final LoginController loginController;
    private final AccountManager accountManager;
    private final AccountData tableData;

    private String redoName;
    private String undoName;

    public AccountNameEditAction(AccountControllerTable accountControllerTable, LoginController loginController, AccountManager accountManager, AccountData tableData) {
        this.accountControllerTable = accountControllerTable;
        this.loginController = loginController;
        this.accountManager = accountManager;
        this.tableData = tableData;
    }

    @Override
    public void doAction() throws ActionAbort {
        Account account = accountManager.loadAccount(tableData.getId());
        if (!tableData.getName().equals(account.getName())) {
            undoName = account.getName();
            redoName = tableData.getName();
            accountManager.updateName(account, redoName);
            accountControllerTable.info("template.account", "account.table.message.nameEdited");
        } else {
            accountControllerTable.log(Level.INFO, loginController.getAccount(), "name on account '" + tableData.getUsername() + "' unchanged");
            accountControllerTable.warn("template.account", "account.table.message.nameUnchanged");
            throw new ActionAbort();
        }
    }

    @Override
    public void undoAction() {
        if (undoName != null) {
            Account account = accountManager.loadAccount(tableData.getId());
            accountManager.updateName(account, undoName);
            tableData.setName(undoName);
            accountControllerTable.info("template.account", "account.table.message.nameRestored");
        }
    }

    @Override
    public void redoAction() {
        if (redoName != null) {
            Account account = accountManager.loadAccount(tableData.getId());
            accountManager.updateName(account, redoName);
            tableData.setName(redoName);
            accountControllerTable.info("template.account", "account.table.message.nameEdited");
        }
    }

}
