/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.domain.Account;
import it.cnr.ilc.lexolite.manager.AccountManager;
import it.cnr.ilc.lexolite.manager.LexiconLocker;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.application.exceptionhandler.ExceptionInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author andrea
 */
@Named
@SessionScoped
public class LoginController extends BaseController implements Serializable {

    private static final Logger LOG = LogManager.getLogger(LoginController.class);

    public void keepAliveAction() {

    }

    private static final long FAIL_AUTHENTICATION_SLEEP = 2000;

    @Inject
    private AccountManager accountManager;
    @Inject
//    private LexiconLocker lexiconLocker;
    private LexiconManager lexiconManager;

    private final Account dummyAccount = new Account();
    private Account account;
    private String sessionId;
    private String address;
    private String userAgent;
    private String username;
    private String password;

    public Account getAccount() {
        account = account == null ? null : accountManager.loadAccount(account.getId());
        return account;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getAddress() {
        return address;
    }

    public String getUserAgent() {
        return userAgent;
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

    public String enterAction() {
        if (account != null) {
            warn("login.header", "login.message.alreadyLoggedIn", account.getUsername());
            username = null;
            password = null;
            return "homeView?faces-redirect=true";
        } else if (username.isEmpty()) {
            warn("login.header", "login.message.usernameMissing");
            return null;
        } else {
            account = accountManager.authenticate(username, password);
            sessionId = FacesContext.getCurrentInstance().getExternalContext().getSessionId(false);
            address = getRequestAddress();
            userAgent = getRequestUserAgent();
            if (account == null) {
                try {
                    Thread.sleep(FAIL_AUTHENTICATION_SLEEP);
                } catch (InterruptedException ex) {
                    LOG.error("On sleep " + ex.getLocalizedMessage());

                }
                dummyAccount.setUsername(username);
                LOG.info("access denied for " + dummyAccount + " from " + userAgent + " on " + address);
                // log(Level.INFO, dummyAccount, "access denied from " + userAgent + " on " + address);
                warn("login.header", "login.message.accessDenied");
                return null;
            } else {
                LOG.info(account.getUsername() + " is logged in from " + userAgent + " on " + address);
                // log(Level.INFO, account, "logged in from " + userAgent + " on " + address);
                username = null;
                password = null;
                return "homeView?faces-redirect=true";
            }
        }
    }

    public static String getRequestAddress() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public static String getRequestUserAgent() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String userAgent = request.getHeader("user-agent");
        return userAgent;
    }

    @PreDestroy
    public void _exitAction() {
        lexiconManager.getLexiconLocker().unlockAll(account.getUsername());
        LOG.info("All resources released for account " + account);
        LOG.info(account.getUsername() + " logged out from " + userAgent + " on " + address);
        //log(Level.INFO, account, "All resources released ");
        //log(Level.INFO, account, "logged out from " + userAgent + " on " + address);
        account = null;
        sessionId = null;
        address = null;
        username = null;
        password = null;
    }

    public String exitAction() {
        _exitAction();
        return "loginView?faces-redirect=true";
    }

    public void checkLogin(boolean internal) throws IOException {
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath();
        if (internal && account == null) {
            LOG.info("checkLogin: redirect loginView.xhtml");
            FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath + "/faces/loginView.xhtml");
        } else if (!internal && account != null) {
            LOG.info("checkLogin: redirect home.xhtml");
            FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath + "/faces/homeView.xhtml");
        }
    }

    public void logError(ExceptionInfo error) {
        if (error != null) {
            LOG.error("Error for account " + account.getUsername() + " Exception: " + error.getMessage());
            //log(Level.ERROR, account, "", error.getException());
        }
    }

}
