package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.HibernateUtil;
import it.cnr.ilc.lexolite.controller.action.AccountNewAction;
import it.cnr.ilc.lexolite.controller.action.AccountRemoveAction;
import it.cnr.ilc.lexolite.controller.action.Action;
import it.cnr.ilc.lexolite.controller.action.ActionAbort;
import it.cnr.ilc.lexolite.controller.action.ActionException;
import it.cnr.ilc.lexolite.domain.AccountType;
import it.cnr.ilc.lexolite.manager.AccountManager;
import java.io.Serializable;
import java.util.Stack;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 *
 * @author oakgen
 */
@ViewScoped
@Named
public class AccountControllerToolbar extends BaseController implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(AccountControllerToolbar.class);

    @Inject
    private LoginController loginController;
    @Inject
    private AccountControllerTable accountControllerTable;
    @Inject
    private AccountManager accountManager;

    private transient final Stack<Action> undo = new Stack<>();
    private transient final Stack<Action> redo = new Stack<>();

    public boolean isUndoDisabled() {
        return undo.isEmpty();
    }

    public boolean isRedoDisabled() {
        return redo.isEmpty();
    }

    public boolean isNewDisabled() {
        return !accountManager.hasPermission(AccountType.Permission.WRITE_ALL, AccountManager.Access.ACCOUNT, loginController.getAccount());
    }

    public boolean isRemoveDisabled() {
        return !accountManager.hasPermission(AccountType.Permission.WRITE_ALL, AccountManager.Access.ACCOUNT, loginController.getAccount())
                || accountControllerTable.isSelectionEmpty();
    }

    public void newAction() {
        log(Level.INFO, loginController.getAccount(), "create new account");
        Action action = new AccountNewAction(accountControllerTable, accountManager);
        doAction(action);
    }

    public void removeAction() {
        log(Level.INFO, loginController.getAccount(), "remove account '" + accountControllerTable.getSelection().getUsername() + "'");
        Action action = new AccountRemoveAction(accountControllerTable, accountManager, accountControllerTable.getSelection());
        doAction(action);
    }

    public void resetAction() {
        undo.clear();
        redo.clear();
    }

    public void doAction(Action action) {
        try {
            action.doAction();
            HibernateUtil.getSession().flush();
            undo.push(action);
            redo.clear();
        } catch (ActionAbort ex) {
            //  log(Level.INFO, loginController.getAccount(), "on doAction() '" + accountControllerTable.getSelection().getUsername() + "'");
            LOG.error("On doAction() '" + accountControllerTable.getSelection().getUsername() + "'", ex);
        } catch (HibernateException t) {
            LOG.error("On doAction() '" + accountControllerTable.getSelection().getUsername() + "'", t);
            HibernateUtil.getSession().getTransaction().rollback();
            HibernateUtil.getSession().beginTransaction();
            throw new ActionException(t);
        }

    }

    public void undoAction() {
        try {
            log(Level.INFO, loginController.getAccount(), "undo");
            Action action = undo.peek();
            action.undoAction();
            HibernateUtil.getSession().flush();
            undo.pop();
            redo.push(action);
        } catch (ActionAbort ex) {
            LOG.error("On undoAction() '" + accountControllerTable.getSelection().getUsername() + "'", ex);

        } catch (HibernateException t) {
            LOG.error("On undoAction() '" + accountControllerTable.getSelection().getUsername() + "'", t);
            HibernateUtil.getSession().getTransaction().rollback();
            HibernateUtil.getSession().beginTransaction();
            throw new ActionException(t);
        }
    }

    public void redoAction() {
        try {
            log(Level.INFO, loginController.getAccount(), "redo");
            Action action = redo.peek();
            action.redoAction();
            HibernateUtil.getSession().flush();
            redo.pop();
            undo.push(action);
        } catch (ActionAbort ex) {
            LOG.error("On redoAction() '" + accountControllerTable.getSelection().getUsername() + "'", ex);
        } catch (HibernateException t) {
            LOG.error("On redoAction() '" + accountControllerTable.getSelection().getUsername() + "'", t);
            HibernateUtil.getSession().getTransaction().rollback();
            HibernateUtil.getSession().beginTransaction();
            throw new ActionException(t);
        }
    }

}
