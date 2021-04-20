package it.cnr.ilc.lexolite.controller.action;

import it.cnr.ilc.lexolite.controller.AccountControllerTable;
import it.cnr.ilc.lexolite.controller.AccountControllerTable.AccountData;
import it.cnr.ilc.lexolite.controller.LoginController;
import it.cnr.ilc.lexolite.controller.converter.AccountTypeConverter.AccountTypeData;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.manager.AccountManager;
import org.slf4j.event.Level;

/**
 *
 * @author oakgen
 */
public class AccountTypeEditAction implements Action {

    private final AccountControllerTable accountControllerTable;
    private final LoginController loginController;
    private final AccountManager accountManager;
    private final AccountData tableData;

    private String redoTypeColor;
    private String undoTypeColor;

    public AccountTypeEditAction(AccountControllerTable accountControllerTable, LoginController loginController, AccountManager accountManager, AccountData tableData) {
        this.accountControllerTable = accountControllerTable;
        this.loginController = loginController;
        this.accountManager = accountManager;
        this.tableData = tableData;
    }

    @Override
    public void doAction() throws ActionAbort {
        Account account = accountManager.loadAccount(tableData.getId());
        if (!tableData.getType().getId().equals(account.getType().getId())) {
            undoTypeColor = account.getType().getColor();
            redoTypeColor = tableData.getType().getColor();
            accountManager.updateType(account, redoTypeColor);
            accountControllerTable.info("template.account", "account.table.message.typeEdited");
        } else {
            accountControllerTable.log(Level.INFO, loginController.getAccount(), "type on account '" + tableData.getUsername() + "' unchanged");
            accountControllerTable.warn("template.account", "account.table.message.typeUnchanged");
            throw new ActionAbort();
        }
    }

    @Override
    public void undoAction() {
        if (undoTypeColor != null) {
            Account account = accountManager.loadAccount(tableData.getId());
            accountManager.updateType(account, undoTypeColor);
            tableData.setType(new AccountTypeData(account.getType()));
            accountControllerTable.info("template.account", "account.table.message.typeRestored");
        }
    }

    @Override
    public void redoAction() {
        if (redoTypeColor != null) {
            Account account = accountManager.loadAccount(tableData.getId());
            accountManager.updateType(account, redoTypeColor);
            tableData.setType(new AccountTypeData(account.getType()));
            accountControllerTable.info("template.account", "account.table.message.typeEdited");
        }
    }

}
