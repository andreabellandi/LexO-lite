package it.cnr.ilc.lexolite.controller.action;

/**
 *
 * @author oakgen
 */
public class ActionException extends RuntimeException {

    public ActionException(Throwable cause) {
        super(cause);
    }

    public ActionException(String message) {
        super(message);
    }

    public ActionException(String message, Throwable cause) {
        super(message, cause);
    }

}
