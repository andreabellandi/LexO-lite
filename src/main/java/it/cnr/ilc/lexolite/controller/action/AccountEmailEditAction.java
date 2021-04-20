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
public class AccountEmailEditAction implements Action {

    private final AccountControllerTable accountControllerTable;
    private final LoginController loginController;
    private final AccountManager accountManager;
    private final AccountData tableData;

    private String redoEmail;
    private String undoEmail;

    public AccountEmailEditAction(AccountControllerTable accountControllerTable, LoginController loginController, AccountManager accountManager, AccountData tableData) {
        this.accountControllerTable = accountControllerTable;
        this.loginController = loginController;
        this.accountManager = accountManager;
        this.tableData = tableData;
    }

    @Override
    public void doAction() throws ActionAbort {
        Account account = accountManager.loadAccount(tableData.getId());
        if (!tableData.getEmail().isEmpty() && !tableData.getEmail().matches("\\S+@\\S+\\.\\w+")) {
            tableData.setEmail(account.getEmail());
            accountControllerTable.log(Level.INFO, loginController.getAccount(), "wrong email");
            accountControllerTable.warn("template.account", "account.table.message.wrongemail");
            throw new ActionAbort();
        } else if (tableData.getEmail().equals(account.getEmail())) {
            accountControllerTable.log(Level.INFO, loginController.getAccount(), "email on account '" + tableData.getUsername() + "' unchanged");
            accountControllerTable.warn("template.account", "account.table.message.emailUnchanged");
            throw new ActionAbort();
        } else {
            undoEmail = account.getEmail();
            redoEmail = tableData.getEmail();
            accountManager.updateEmail(account, redoEmail);
            accountControllerTable.info("template.account", "account.table.message.emailEdited");
        }
    }

    @Override
    public void undoAction() {
        if (undoEmail != null) {
            Account account = accountManager.loadAccount(tableData.getId());
            accountManager.updateEmail(account, undoEmail);
            tableData.setEmail(undoEmail);
            accountControllerTable.info("template.account", "account.table.message.emailRestored");
        }
    }

    @Override
    public void redoAction() {
        if (redoEmail != null) {
            Account account = accountManager.loadAccount(tableData.getId());
            accountManager.updateEmail(account, redoEmail);
            tableData.setEmail(redoEmail);
            accountControllerTable.info("template.account", "account.table.message.emailEdited");
        }
    }

}
