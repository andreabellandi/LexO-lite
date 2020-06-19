package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.HibernateUtil;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.domain.AccountType;
import it.cnr.ilc.lexolite.domain.AccountType.Permission;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author oakgen
 */
public class AccountManager implements Serializable {

    public static final String ADMINISTRATOR = "Administrator";
    public static final String USER = "User";
    public static final String VIEWER = "Viewer";

    public enum Access {

        ACCOUNT,
        LEXICON_EDITOR,
        LEXICON_STAT
    }

    private static final String DEFAULT_TYPE_NAME = USER;

    @Inject
    private DomainManager domainManager;

    private static AccountType defaultType;
    private static List<AccountType> accountTypes;
    private static final Map<String, AccountType> accountTypesByColor = new HashMap<>();
    private static final Map<String, AccountType> accountTypesByName = new HashMap<>();

    static {
        accountTypes = HibernateUtil.getSession().createCriteria(AccountType.class).list();
        for (AccountType accountType : accountTypes) {
            accountTypesByColor.put(accountType.getColor(), accountType);
            accountTypesByName.put(accountType.getName(), accountType);
            if (accountType.getName().equals(DEFAULT_TYPE_NAME)) {
                defaultType = accountType;
            }
        }
    }

    public List<AccountType> getAccountTypes() {
        return accountTypes;
    }

    public AccountType getAccountTypeByName(String name) {
        return accountTypesByName.get(name);
    }

    public AccountType getAccountTypeByColor(String color) {
        return accountTypesByColor.get(color);
    }

    private static final String AUTHENTICATE_SQL = "select * from Account where username = ':u' and password = upper(sha1(':p')) and status = 1 and enabled = true";

    public Account authenticate(String username, String password) {
        password = Matcher.quoteReplacement(password).replaceAll("'", "''");
        String sql = AUTHENTICATE_SQL.replaceAll(":u", username).replaceAll(":p", password);
        SQLQuery query = HibernateUtil.getSession().createSQLQuery(sql).addEntity(Account.class);
        Account account = (Account) query.uniqueResult();
        return account;
    }

    public boolean checkPassword(String username, String currentPassword) {
        return authenticate(username, currentPassword) != null;
    }

    public List<Account> loadAccounts() {
        return HibernateUtil.getSession().createCriteria(Account.class).list();
    }

    public Account loadAccount(Long id) {
        return (Account) HibernateUtil.getSession().get(Account.class, id);
    }

    public List<Account> loadAccounts(String name) {
        Criteria criteria = HibernateUtil.getSession().createCriteria(Account.class);
        criteria.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
        criteria.addOrder(Order.asc("name"));
        return criteria.list();
    }

    public Account loadAccount(String username) {
        Criteria criteria = HibernateUtil.getSession().createCriteria(Account.class);
        criteria.add(Restrictions.eq("username", username));
        List<Account> list = criteria.list();
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean hasPermission(Permission permission, Access access, Account account) {
        if (account.getType().getName().equals(ADMINISTRATOR)) {
            return true;
        }
        Permission accountPermission = account.getType().getPermissions().get(access.name());
        return Permission.hasPermission(accountPermission, permission);
    }

    public void updateName(Account account, String name) {
        account.setName(name);
        domainManager.update(account);
    }

    public void updateUsername(Account account, String username) {
        account.setUsername(username);
        domainManager.update(account);
    }

    public void updateEmail(Account account, String email) {
        account.setEmail(email);
        domainManager.update(account);
    }

    public boolean checkUsername(String username) {
        return true;
    }

    private static final String ENCRYPT_PASSWORD = "select upper(sha1(':p'))";

    public void updatePassword(Account account, String password) {
        password = Matcher.quoteReplacement(password).replaceAll("'", "''");
        String sql = ENCRYPT_PASSWORD.replaceFirst(":p", password);
        SQLQuery query = HibernateUtil.getSession().createSQLQuery(sql);
        password = (String) query.uniqueResult();
        account.setPassword(password);
        domainManager.update(account);
    }

    public void updateType(Account account, String color) {
        AccountType accountType = accountTypesByColor.get(color);
        account.setType(accountType);
        domainManager.update(account);
    }

    public void setEnabled(Account account, boolean enabled) {
        account.setEnabled(enabled);
        domainManager.update(account);
    }

    public Account remove(Account account) {
        if (checkDelete(account)) {
            HibernateUtil.getSession().delete(account);
            return null;
        } else {
            domainManager.deleteWithHistory(account);
            return account;
        }
    }

    private static final String CHECK_DELETE = "select count(*) from `Account` where `valid_id` = :a";

    private boolean checkDelete(Account account) {
        String sql = CHECK_DELETE.replaceAll(":a", account.getId().toString());
        SQLQuery query = HibernateUtil.getSession().createSQLQuery(sql);
        return ((Number) query.uniqueResult()).intValue() == 0;
    }

    private static final String CHECK_CREATION = "select count(*) from `Account` where `status` = 1 and (`username` = '' or `password` = '')";

    public boolean checkCreation() {
        String sql = CHECK_CREATION;
        SQLQuery query = HibernateUtil.getSession().createSQLQuery(sql);
        return ((Number) query.uniqueResult()).intValue() != 0;
    }

    public Account newEmptyAccount() {
        Account account = new Account();
        account.setEnabled(Boolean.FALSE);
        account.setType(defaultType);
        account.setName("");
        account.setUsername("");
        account.setPassword("");
        account.setEmail("");
        domainManager.insert(account);
        return account;
    }

    public void reinsertAccount(Account account) {
        domainManager.hiddenUpdate(account);
    }

    public Account loadByUsernameOrEmail(String username, String email) {
        Criteria criteria = HibernateUtil.getSession().createCriteria(Account.class);
        if (username != null && !username.isEmpty()) {
            criteria.add(Restrictions.eq("username", username));
        }
        if (email != null && !email.isEmpty()) {
            criteria.add(Restrictions.eq("email", email));
        }
        return (Account) criteria.uniqueResult();
    }

    private static final CharSequence PASSWORD_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvxyz";
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static String getRandomPassword() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return password.toString();
    }

    public void resetPassword(Account account) throws MessagingException {
        String decripted = getRandomPassword();
        updatePassword(account, decripted);
    }

    public String getSetting(Account account, String key) {
        Map<String, String> settings = account.getSettings();
        return settings == null ? null : settings.get(key);
    }

    public void setSetting(Account account, String key, String value) {
        if (account.getSettings() == null) {
            account.setSettings(new HashMap<>());
        }
        account.getSettings().put(key, value);
        domainManager.update(account);
    }
}
