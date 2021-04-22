package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.domain.Account;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 *
 * @author andrea
 */
public abstract class BaseController {

    // private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
            logger.error(" ", ex);
            return "???" + key + "???";
        }
    }

    public Locale getRequestLocale() {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getExternalContext().getRequestLocale();
    }

    public void log(Level level, String message) {
        String newMessage = String.format("(Unknown user) %s", message);
        printLog(level, newMessage);
    }

    public void log(Level level, Account account, String message) {
        String newMessage = String.format("(%s) %s", (account == null)?"Unknown user":account.getUsername(), message);
        printLog(level, newMessage);
    }

    public void log(Level level, String message, Throwable t) {
        String newMessage = String.format("(Unknown user) %s", message);
        printLog(level, newMessage, t);
    }

    public void log(Level level, Account account, String message, Throwable t) {
        String newMessage = String.format("(%s) %s", (account == null)?"Unknown user":account.getUsername(), message);
        printLog(level, newMessage, t);
    }

    private void printLog(Level level, String msg) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        //String callerClass = stackTraceElements[3].getClassName();
        String callerLine = String.format("%d", stackTraceElements[3].getLineNumber());
        String message = callerLine + " - " + msg;
        switch (level) {
            case ERROR:
                logger.error(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case INFO:
                logger.info(message);
                break;
            case DEBUG:
                logger.debug(message);
                break;
            case TRACE:
                logger.trace(message);
                break;
            default:
                logger.error("No logger for level {}", level);
                break;
        }
    }

    private void printLog(Level level, String message, Throwable t) {
        switch (level) {
            case ERROR:
                logger.error(message, t);
                break;
            case WARN:
                logger.warn(message, t);
                break;
            case INFO:
                logger.info(message, t);
                break;
            case DEBUG:
                logger.debug(message, t);
                break;
            case TRACE:
                logger.trace(message, t);
                break;
            default:
                logger.error("No logger for level {}", level);
                break;
        }
    }

    public String emptyMessage(String text, String emptyMessage) {
        return emptyMessage(text, text, emptyMessage);
    }

    public String emptyMessage(String test, String text, String emptyMessage) {
        return test == null || test.equals("") ? emptyMessage : text;
    }
}
