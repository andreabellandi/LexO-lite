/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerSenseFilter extends BaseController implements Serializable {

    @Inject
    private LexiconControllerTabViewList lexiconCreationControllerTabViewList;

    private boolean startWith = true;
    private boolean contains = false;

    public boolean isStartWith() {
        return startWith;
    }

    public void setStartWith(boolean startWith) {
        this.startWith = startWith;
    }

    public boolean isContains() {
        return contains;
    }

    public void setContains(boolean contains) {
        this.contains = contains;
    }

    public void startsWithChanged() {
        contains = !startWith;
        if (lexiconCreationControllerTabViewList.getSenseField() != null) {
            if (lexiconCreationControllerTabViewList.getSenseField().length() > 0) {
                lexiconCreationControllerTabViewList.senseKeyupFilterEvent(lexiconCreationControllerTabViewList.getSenseField());
            }
        }
    }

    public void containsChanged() {
        startWith = !contains;
        if (lexiconCreationControllerTabViewList.getSenseField() != null) {
            if (lexiconCreationControllerTabViewList.getSenseField().length() > 0) {
                lexiconCreationControllerTabViewList.senseKeyupFilterEvent(lexiconCreationControllerTabViewList.getSenseField());
            }
        }
    }

}
