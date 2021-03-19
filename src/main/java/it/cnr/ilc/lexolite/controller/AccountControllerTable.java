package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.controller.action.AccountEmailEditAction;
import it.cnr.ilc.lexolite.controller.action.AccountNameEditAction;
import it.cnr.ilc.lexolite.controller.action.AccountPasswordEditAction;
import it.cnr.ilc.lexolite.controller.action.AccountTypeEditAction;
import it.cnr.ilc.lexolite.controller.action.AccountUsernameEditAction;
import it.cnr.ilc.lexolite.controller.action.Action;
import it.cnr.ilc.lexolite.controller.converter.AccountTypeConverter.AccountTypeData;
import it.cnr.ilc.lexolite.controller.converter.PasswordConverter;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.domain.AccountType;
import it.cnr.ilc.lexolite.domain.AccountType.Permission;
import it.cnr.ilc.lexolite.manager.AccountManager;
import it.cnr.ilc.lexolite.manager.AccountManager.Access;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.event.data.SortEvent;

/**
 *
 * @author oakgen
 */
@ViewScoped
@Named
public class AccountControllerTable extends BaseController implements Serializable {

    @Inject
    private LoginController loginController;
    @Inject
    private AccountManager accountManager;
    @Inject
    private AccountControllerToolbar accountControllerToolbar;

    private final List<AccountTypeData> accountTypes = new ArrayList<>();
    private final List<AccountData> data = new ArrayList();
    private AccountData selection;

    @PostConstruct
    private void init() {
        for (AccountType current : accountManager.getAccountTypes()) {
            accountTypes.add(new AccountTypeData(current));
        }
        reset();
    }

    public void reset() {
        data.clear();
        List<Account> records = accountManager.loadAccounts();
        for (Account account : records) {
            data.add(new AccountData(account));
        }
        selection = null;
    }

    public List<AccountTypeData> getAccountTypes() {
        return accountTypes;
    }

    public List<AccountData> getData() {
        return data;
    }

    public AccountData getSelection() {
        return selection;
    }

    public void setSelection(AccountData selection) {
        this.selection = selection;
    }

    public boolean isSelectionEmpty() {
        return selection == null;
    }

    public Long getSelectedId() {
        return selection == null ? null : selection.getId();
    }

    public void insertFirst(Account account) {
        selection = new AccountData(account);
        data.add(0, selection);
    }

    public void onRowSelect(SelectEvent event) {
        AccountData accountData = (AccountData) event.getObject();
        if (accountData == null) {
            log(Level.INFO, loginController.getAccount(), "select null table data");
        } else {
            log(Level.INFO, loginController.getAccount(), "select account '" + accountData.getUsername() + "'");
        }
    }

    public void onSort(SortEvent event) {
        log(Level.INFO, loginController.getAccount(), "sort account table by " + event.getSortColumn().getHeaderText());
    }

    public void onFilter(FilterEvent event) {
        log(Level.INFO, loginController.getAccount(), "filter account table by " + event.getFilterBy());
    }

    public void cancelEditAction(AccountData accountData, String what) {
        log(Level.INFO, loginController.getAccount(), "cancel edit " + what + " on account '" + accountData.getUsername() + "'");
    }

    public void typeEditAction(AccountData accountData) {
        log(Level.INFO, loginController.getAccount(), "edit type on account '" + accountData.getUsername() + "' to " + accountData.getType().getName());
        Action action = new AccountTypeEditAction(this, loginController, accountManager, accountData);
        accountControllerToolbar.doAction(action);
    }

    public void enabledEditAction(AccountData accountData) {
        log(Level.INFO, loginController.getAccount(), (accountData.isEnabled() ? "enabled " : "disabled ") + " account '" + accountData.getUsername() + "'");
        Account account = accountManager.loadAccount(accountData.getId());
        if (account.getUsername().isEmpty() || account.getPassword().isEmpty()) {
            accountData.setEnabled(false);
            warn("template.account", "account.table.message.unableEnabled");
        } else {
            accountManager.setEnabled(account, accountData.isEnabled());
            info("template.account", accountData.isEnabled() ? "account.table.message.enable" : "account.table.message.disable");
        }
    }

    public void nameEditAction(AccountData accountData) {
        log(Level.INFO, loginController.getAccount(), "edit name on account '" + accountData.getUsername() + "' to '" + accountData.getName() + "'");
        Action action = new AccountNameEditAction(this, loginController, accountManager, accountData);
        accountControllerToolbar.doAction(action);
    }

    public void usernameEditAction(AccountData accountData) {
        log(Level.INFO, loginController.getAccount(), "edit username on account '" + accountData.getUsername() + "' to '" + accountData.getUsername() + "'");
        Action action = new AccountUsernameEditAction(this, loginController, accountManager, accountData);
        accountControllerToolbar.doAction(action);
    }

    public void passwordEditAction(AccountData accountData) {
        log(Level.INFO, loginController.getAccount(), "edit password on account '" + accountData.getUsername() + "'");
        Action action = new AccountPasswordEditAction(this, accountManager, accountData);
        accountControllerToolbar.doAction(action);
    }

    public void emailEditAction(AccountData accountData) {
        log(Level.INFO, loginController.getAccount(), "edit email on account '" + accountData.getUsername() + "' to '" + accountData.getEmail() + "'");
        Action action = new AccountEmailEditAction(this, loginController, accountManager, accountData);
        accountControllerToolbar.doAction(action);
    }

    public boolean isPermission() {
        return accountManager.hasPermission(Permission.WRITE_ALL, Access.ACCOUNT, loginController.getAccount());
    }

    public String emptyPassword(String password, String emptyMessage) {
        return password == null || password.equals("") ? emptyMessage : PasswordConverter.HIDDEN_PASSWORD;
    }

    public void remove(AccountData selection) {
        data.remove(selection);
        this.selection = null;
    }

    public class AccountData implements Serializable {

        private final Long id;
        private AccountTypeData type;
        private String username;
        private String password;
        private String name;
        private String email;
        private boolean enabled;

        public AccountData(Account account) {
            this.id = account.getId();
            this.type = new AccountTypeData(account.getType());
            this.username = account.getUsername();
            this.password = account.getPassword();
            this.name = account.getName();
            this.email = account.getEmail();
            this.enabled = account.getEnabled();
        }

        public Long getId() {
            return id;
        }

        public AccountTypeData getType() {
            return type;
        }

        public void setType(AccountTypeData type) {
            this.type = type;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

    };
}
