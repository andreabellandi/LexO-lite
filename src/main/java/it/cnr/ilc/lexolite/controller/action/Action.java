package it.cnr.ilc.lexolite.controller.action;

/**
 *
 * @author oakgen
 */
public interface Action {

    public void doAction() throws ActionAbort;

    public void undoAction() throws ActionAbort;

    public void redoAction() throws ActionAbort;

}
