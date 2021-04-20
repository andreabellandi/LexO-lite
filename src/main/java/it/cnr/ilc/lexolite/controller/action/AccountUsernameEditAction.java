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
public class AccountUsernameEditAction implements Action {

    private final AccountControllerTable accountControllerTable;
    private final LoginController loginController;
    private final AccountManager accountManager;
    private final AccountData tableData;

    private String redoUsername;
    private String undoUsername;

    public AccountUsernameEditAction(AccountControllerTable accountControllerTable, LoginController loginController, AccountManager accountManager, AccountData tableData) {
        this.accountControllerTable = accountControllerTable;
        this.loginController = loginController;
        this.accountManager = accountManager;
        this.tableData = tableData;
    }

    @Override
    public void doAction() throws ActionAbort {
        Account account = accountManager.loadAccount(tableData.getId());
        if (tableData.getUsername().isEmpty()) {
            tableData.setUsername(account.getUsername());
            accountControllerTable.log(Level.INFO, loginController.getAccount(), "username is empty");
            accountControllerTable.warn("template.account", "account.table.message.missingUsername");
            throw new ActionAbort();
        } else if (tableData.getUsername().equals(account.getUsername())) {
            accountControllerTable.log(Level.INFO, loginController.getAccount(), "username on account '" + tableData.getUsername() + "' unchanged");
            accountControllerTable.warn("template.account", "account.table.message.usernameUnchanged");
            throw new ActionAbort();
        } else if (!accountManager.checkUsername(tableData.getUsername())) {
            tableData.setUsername(account.getUsername());
            accountControllerTable.log(Level.INFO, loginController.getAccount(), "username '" + tableData.getUsername()+ "' already exsists");
            accountControllerTable.warn("template.account", "account.table.message.existingUsername");
            throw new ActionAbort();
        } else {
            undoUsername = account.getUsername();
            redoUsername = tableData.getUsername();
            accountManager.updateUsername(account, redoUsername);
            accountControllerTable.info("template.account", "account.table.message.usernameEdited");
        }
    }

    @Override
    public void undoAction() {
        if (undoUsername != null) {
            Account account = accountManager.loadAccount(tableData.getId());
            accountManager.updateUsername(account, undoUsername);
            tableData.setUsername(undoUsername);
            accountControllerTable.info("template.account", "account.table.message.usernameRestored");
        }
    }

    @Override
    public void redoAction() {
        if (redoUsername != null) {
            Account account = accountManager.loadAccount(tableData.getId());
            accountManager.updateUsername(account, redoUsername);
            tableData.setUsername(redoUsername);
            accountControllerTable.info("template.account", "account.table.message.usernameEdited");
        }
    }

}
