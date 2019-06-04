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
public class LexiconControllerLemmaFilter extends BaseController implements Serializable {

    @Inject
    private LexiconControllerTabViewList lexiconCreationControllerTabViewList;

    private boolean startWith = true;
    private boolean contains = false;

    private boolean ext = false;
    private boolean rec = false;
    private boolean hyp = false;
    private boolean all = true;

    public boolean isExt() {
        return ext;
    }

    public void setExt(boolean ext) {
        this.ext = ext;
    }

    public boolean isRec() {
        return rec;
    }

    public void setRec(boolean rec) {
        this.rec = rec;
    }

    public boolean isHyp() {
        return hyp;
    }

    public void setHyp(boolean hyp) {
        this.hyp = hyp;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

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
        if (lexiconCreationControllerTabViewList.getLemmaField() != null) {
            if (lexiconCreationControllerTabViewList.getLemmaField().length() > 0) {
                lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent(lexiconCreationControllerTabViewList.getLemmaField());
            }
        }
    }

    public void containsChanged() {
        startWith = !contains;
        if (lexiconCreationControllerTabViewList.getLemmaField() != null) {
            if (lexiconCreationControllerTabViewList.getLemmaField().length() > 0) {
                lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent(lexiconCreationControllerTabViewList.getLemmaField());
            }
        }
    }

    public void extChanged() {
        all = false;
        ext = true;
        hyp = false;
        rec = false;
        if (lexiconCreationControllerTabViewList.getLemmaField() != null) {
            if (lexiconCreationControllerTabViewList.getLemmaField().length() > 0) {
                lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent(lexiconCreationControllerTabViewList.getLemmaField());
            } else {
                lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent("");
            }
        } else {
            lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent("");
        }
    }

    public void recChanged() {
        all = false;
        ext = false;
        hyp = false;
        rec = true;
        if (lexiconCreationControllerTabViewList.getLemmaField() != null) {
            if (lexiconCreationControllerTabViewList.getLemmaField().length() > 0) {
                lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent(lexiconCreationControllerTabViewList.getLemmaField());
            } else {
                lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent("");
            }
        } else {
            lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent("");
        }
    }

    public void hypChanged() {
        all = false;
        ext = false;
        hyp = true;
        rec = false;
        if (lexiconCreationControllerTabViewList.getLemmaField() != null) {
            if (lexiconCreationControllerTabViewList.getLemmaField().length() > 0) {
                lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent(lexiconCreationControllerTabViewList.getLemmaField());
            } else {
                lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent("");
            }
        } else {
            lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent("");
        }
    }

    public void allChanged() {
        all = true;
        ext = false;
        hyp = false;
        rec = false;
        if (lexiconCreationControllerTabViewList.getLemmaField() != null) {
            if (lexiconCreationControllerTabViewList.getLemmaField().length() > 0) {
                lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent(lexiconCreationControllerTabViewList.getLemmaField());
            } else {
                lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent("");
            }
        } else {
            lexiconCreationControllerTabViewList.lemmaKeyupFilterEvent("");
        }
    }

}
