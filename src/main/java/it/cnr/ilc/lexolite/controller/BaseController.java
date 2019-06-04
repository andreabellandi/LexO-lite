package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.domain.Account;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author andrea
 */
public abstract class BaseController {

    public void error(String summary, String... details) {
        String detail = buildDetail(details);
        message(FacesMessage.SEVERITY_ERROR, summary, detail);
    }

    public void warn(String summary, String... details) {
        summary = getLabel(summary);
        String detail = buildDetail(details);
        message(FacesMessage.SEVERITY_WARN, summary, detail);
    }

    public void info(String summary, String... details) {
        summary = getLabel(summary);
        String detail = buildDetail(details);
        message(FacesMessage.SEVERITY_INFO, summary, detail);
    }

    protected String buildDetail(String[] details) {
        String detail = getLabel(details[0]);
        for (int i = 1; i < details.length; i++) {
            detail = detail.replaceAll("\\{" + (i - 1) + "\\}", details[i]);
        }
        return detail;
    }

    public void message(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public String getLabel(String key) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle resourceBundle = context.getApplication().getResourceBundle(context, "label");
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException ex) {
            Logger.getLogger("LexO-lite").log(Level.ERROR, ex.getStackTrace());
            return "???" + key + "???";
        }
    }

    public Locale getRequestLocale() {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getExternalContext().getRequestLocale();
    }

    public void log(Level level, Account account, String message) {
        message = "(" + (account == null ? "null" : account.getUsername()) + ") " + message;
        Logger.getLogger("LexO-lite").log(level, message);
    }

    public void log(Level level, Account account, String message, Throwable t) {
        message = "(" + (account == null ? "null" : account.getUsername()) + ") " + message;
        Logger.getLogger("LexO-lite").log(level, message, t);
    }

    public String emptyMessage(String text, String emptyMessage) {
        return emptyMessage(text, text, emptyMessage);
    }

    public String emptyMessage(String test, String text, String emptyMessage) {
        return test == null || test.equals("") ? emptyMessage : text;
    }
}
