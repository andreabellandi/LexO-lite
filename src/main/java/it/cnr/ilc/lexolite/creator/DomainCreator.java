package it.cnr.ilc.lexolite.creator;

import it.cnr.ilc.lexolite.HibernateUtil;
import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.domain.AccountType;
import it.cnr.ilc.lexolite.manager.DomainManager;
import it.cnr.ilc.lexolite.manager.AccountManager;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.SQLQuery;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author oakgen
 */
@WebServlet(urlPatterns = "/servlet/domainCreator")
public class DomainCreator extends HttpServlet {

    private static final Logger LOG = LogManager.getLogger(DomainCreator.class);

    private final DomainManager manager = new DomainManager();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String command = request.getParameter("command");
        if ("create".equals(command)) {
            // create the database schema
            create();
        } else if ("update".equals(command)) {
            // update the database schema according to the new entities
            update();
        } else if ("preset".equals(command)) {
            // initialize the database with an administrator user
            preset();
        } else {
            throw new ServletException("Unknow command");
        }
        response.getOutputStream().print("OK");
    }

    public String update() {
        Configuration configuration = new Configuration().configure();
        SchemaUpdate schemaUpdate = new SchemaUpdate(configuration);
        schemaUpdate.execute(true, true);
        return null;
    }

    public String create() {
        Configuration configuration = new Configuration().configure();
        SchemaExport schemaExport = new SchemaExport(configuration);
        schemaExport.create(true, true);
        return null;
    }

    private void preset() {
        createAccountType();
        createAdmin("LexOAdmin","admin","admin");
    }

    private void createAccountType() {

        AccountType accountType = new AccountType();
        accountType.setName(AccountManager.ADMINISTRATOR);
        accountType.setColor("lightsteelblue");
        manager.insert(accountType);

        accountType = new AccountType();
        accountType.setName(AccountManager.USER);
        accountType.setColor("orangered");
        accountType.setPermissions(new HashMap<>());
        accountType.getPermissions().put(AccountManager.Access.LEXICON_EDITOR.toString(), AccountType.Permission.WRITE_ALL);
        accountType.getPermissions().put(AccountManager.Access.LEXICON_STAT.toString(), AccountType.Permission.READ_ALL);
        manager.insert(accountType);

        accountType = new AccountType();
        accountType.setName(AccountManager.VIEWER);
        accountType.setPermissions(new HashMap<>());
        accountType.getPermissions().put(AccountManager.Access.LEXICON_EDITOR.toString(), AccountType.Permission.READ_ALL);
        accountType.getPermissions().put(AccountManager.Access.LEXICON_STAT.toString(), AccountType.Permission.READ_ALL);
        accountType.setColor("white");
        manager.insert(accountType);
    }

    private void createAdmin(String name, String username, String password) {
        Account account = new Account();
        account.setName(name);
        account.setUsername(username);
        account.setPassword(password);
        account.setEnabled(true);
        account.setType((AccountType) HibernateUtil.getSession().createCriteria(AccountType.class).add(Restrictions.eq("name", AccountManager.ADMINISTRATOR)).uniqueResult());
        manager.insert(account);
        HibernateUtil.getSession().flush();
        String sql = "update Account set password = upper(sha1('" + password + "')) where username = '" + username + "'";
        SQLQuery query = HibernateUtil.getSession().createSQLQuery(sql);
        query.executeUpdate();
    }

}
