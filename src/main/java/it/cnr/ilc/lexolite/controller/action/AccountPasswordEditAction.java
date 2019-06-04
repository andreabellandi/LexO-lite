package it.cnr.ilc.lexolite.controller.action;

import it.cnr.ilc.lexolite.controller.AccountControllerTable;
import it.cnr.ilc.lexolite.controller.AccountControllerTable.AccountData;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.manager.AccountManager;

/**
 *
 * @author oakgen
 */
public class AccountPasswordEditAction implements Action {

    private final AccountControllerTable accountControllerTable;
    private final AccountManager accountManager;
    private final AccountData tableData;

    private String redoPassword;
    private String undoPassword;

    public AccountPasswordEditAction(AccountControllerTable accountControllerTable, AccountManager accountManager, AccountData tableData) {
        this.accountControllerTable = accountControllerTable;
        this.accountManager = accountManager;
        this.tableData = tableData;
    }

    @Override
    public void doAction() throws ActionAbort {
        Account account = accountManager.loadAccount(tableData.getId());
        if (!tableData.getPassword().equals(account.getPassword())) {
            if (tableData.getPassword().isEmpty()) {
                tableData.setPassword(account.getPassword());
                accountControllerTable.warn("template.account", "account.table.message.missingPassword");
                throw new ActionAbort();
            } else {
                undoPassword = account.getPassword();
                redoPassword = tableData.getPassword();
                accountManager.updatePassword(account, redoPassword);
                accountControllerTable.info("template.account", "account.table.message.passwordEdited");
            }
        }
    }

    @Override
    public void undoAction() {
        if (undoPassword != null) {
            Account account = accountManager.loadAccount(tableData.getId());
            accountManager.updatePassword(account, undoPassword);
            tableData.setPassword(undoPassword);
            accountControllerTable.info("template.account", "account.table.message.passwordRestored");
        }
    }

    @Override
    public void redoAction() {
        if (redoPassword != null) {
            Account account = accountManager.loadAccount(tableData.getId());
            accountManager.updatePassword(account, redoPassword);
            tableData.setPassword(redoPassword);
            accountControllerTable.info("template.account", "account.table.message.passwordEdited");
        }
    }

}
