/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.constant.OntoLexEntity;
import it.cnr.ilc.lexolite.manager.LexiconManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerAdvancedFilter extends BaseController implements Serializable {

    @Inject
    private LexiconControllerTabViewList lexiconCreationControllerTabViewList;
    @Inject
    private LexiconManager lexiconManager;
    @Inject
    private LoginController loginController;

    // for query optimization
    private static Pattern entryLangPattern = Pattern.compile("([a-z]+_entry)");

    private boolean allEntries = true;
    private boolean word = false;
    private boolean multiword = false;
    private boolean verified = false;
    private boolean unverified = false;
    private boolean polysemic = false;
    private boolean monosemic = false;

    private boolean typesBlockDisabled = true;
    private boolean verifiyBlockDisabled = false;
    private boolean semicBlockDisabled = false;

    private String language = "All languages";
    private String pos = "any";

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isTypesBlockDisabled() {
        return typesBlockDisabled;
    }

    public void setTypesBlockDisabled(boolean typesBlockDisabled) {
        this.typesBlockDisabled = typesBlockDisabled;
    }

    public boolean isVerifiyBlockDisabled() {
        return verifiyBlockDisabled;
    }

    public void setVerifiyBlockDisabled(boolean verifiyBlockDisabled) {
        this.verifiyBlockDisabled = verifiyBlockDisabled;
    }

    public boolean isSemicBlockDisabled() {
        return semicBlockDisabled;
    }

    public void setSemicBlockDisabled(boolean semicBlockDisabled) {
        this.semicBlockDisabled = semicBlockDisabled;
    }

    public boolean isUnverified() {
        return unverified;
    }

    public void setUnverified(boolean unverified) {
        this.unverified = unverified;
    }

    public boolean isMonosemic() {
        return monosemic;
    }

    public void setMonosemic(boolean monosemic) {
        this.monosemic = monosemic;
    }

    public boolean isAllEntries() {
        return allEntries;
    }

    public void setAllEntries(boolean allEntries) {
        this.allEntries = allEntries;
    }

    public boolean isWord() {
        return word;
    }

    public void setWord(boolean word) {
        this.word = word;
    }

    public boolean isMultiword() {
        return multiword;
    }

    public void setMultiword(boolean multiword) {
        this.multiword = multiword;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isPolysemic() {
        return polysemic;
    }

    public void setPolysemic(boolean polysemic) {
        this.polysemic = polysemic;
    }

    public void allEntriesChanged() {
        if (allEntries) {
            word = false;
            multiword = false;
            typesBlockDisabled = true;
        } else {
            allEntries = true;
            typesBlockDisabled = true;
            multiword = false;
        }
    }

    public void wordChanged() {
        if (word) {
            typesBlockDisabled = true;
            multiword = false;
            allEntries = false;
        } else {
            typesBlockDisabled = true;
            multiword = false;
            allEntries = true;
        }
    }

    public void multiwordChanged() {
        if (multiword) {
            allEntries = false;
            word = false;
            typesBlockDisabled = false;
        } else {
            allEntries = true;
            word = false;
            typesBlockDisabled = true;
        }
    }

    public void verifiedChanged() {
        if (verified) {
            unverified = false;
        }
    }

    public void unverifiedChanged() {
        if (unverified) {
            verified = false;
        }
    }

    public void polysemicChanged() {
        if (polysemic) {
            monosemic = false;
        }
    }

    public void monosemicChanged() {
        if (monosemic) {
            polysemic = false;
        }
    }

    private boolean isFilterable(Map<String, String> m, String keyFilter, String keyName) {
        boolean filterable = false;
        if (keyFilter.equals("any")) {
            filterable = true;
        } else {
            if (m.get(keyName) != null) {
                if (keyFilter.equals(m.get(keyName))) {
                    filterable = true;
                }
            } else {
                filterable = false;
            }
        }
        return filterable;
    }

    private void filterEntry(Map<String, String> m, Map<String, String> _mL, Map<String, String> _mS, List<Map<String, String>> l, List<Map<String, String>> s, String check) {
        if (isFilterable(m, pos, "pos")) {
            String lang = getLang(m.get("le"));
            if (language.equals(lang) || language.equals("All languages")) {
                if (check.equals("all")) {
                    setEntry(m, _mL, _mS, l, s, lang);
                } else {
                    if (check.equals("true")) {
                        if (!m.get("verified").equals("false")) {
                            setEntry(m, _mL, _mS, l, s, lang);
                        }
                    } else {
                        if (m.get("verified").equals("false")) {
                            setEntry(m, _mL, _mS, l, s, lang);
                        }
                    }
                }
            }
        }
    }

    private void setEntry(Map<String, String> m, Map<String, String> _mL, Map<String, String> _mS, List<Map<String, String>> l, List<Map<String, String>> s, String lang) {
        _mL.put("writtenRep", m.get("writtenRep"));
        _mL.put("individual", m.get("individual"));
        _mL.put("lang", lang);
        _mL.put("type", m.get("type"));
        _mL.put("verified", m.get("verified"));
        l.add(_mL);
        _mS.put("writtenRep", m.get("sense"));
        _mS.put("individual", m.get("sense"));
        _mS.put("lang", lang);
        _mS.put("verified", m.get("verified"));
        s.add(_mS);
    }

    private void setEntry(Map<String, String> m, Map<String, String> _mF, List<Map<String, String>> f, String lang) {
        _mF.put("lang", lang);
        _mF.put("writtenRep", m.get("writtenRep"));
        _mF.put("individual", m.get("individual"));
        _mF.put("lang", getLang(m.get("le")));
        _mF.put("type", m.get("type"));
        _mF.put("verified", m.get("verified"));
        f.add(_mF);
    }

    private void filterEntry(Map<String, String> m, Map<String, String> _mF, List<Map<String, String>> f, String check) {
        if (isFilterable(m, pos, "pos")) {
            String lang = getLang(m.get("le"));
            if (language.equals(lang) || language.equals("All languages")) {
                if (check.equals("all")) {
                    setEntry(m, _mF, f, lang);
                } else {
                    if (m.get("verified").equals(check)) {
                        setEntry(m, _mF, f, lang);
                    }
                }
            }
        }
    }

    private boolean contains(String individual, List<Map<String, String>> l) {
        for (Map<String, String> _m : l) {
            if (_m.get("individual").equals(individual)) {
                return true;
            }
        }
        return false;
    }

    private List<Map<String, String>> getDistinctList(List<Map<String, String>> l) {
        List<Map<String, String>> _l = new ArrayList<Map<String, String>>();
        for (Map<String, String> m : l) {
            if (!contains(m.get("individual"), _l)) {
                _l.add(m);
            }
        }
        return _l;
    }

    public void filter() {
        String type = allEntries ? "all" : (word ? OntoLexEntity.Class.WORD.getLabel() : (multiword ? OntoLexEntity.Class.MULTIWORD.getLabel() : ""));
        String check = "all";
        if (verified) {
            check = "true";
        }
        if (unverified) {
            check = "false";
        }
        List<Map<String, String>> l = new ArrayList<Map<String, String>>();
        List<Map<String, String>> f = new ArrayList<Map<String, String>>();
        List<Map<String, String>> s = new ArrayList<Map<String, String>>();
        for (Map<String, String> m : lexiconManager.advancedFilter_lemmas()) {
            Map<String, String> _mL = new HashMap<String, String>();
            Map<String, String> _mS = new HashMap<String, String>();
            if (type.equals("all")) {
                filterEntry(m, _mL, _mS, l, s, check);
            } else {
                if (type.equals(m.get("type"))) {
                    filterEntry(m, _mL, _mS, l, s, check);
                }
            }
        }
        lexiconCreationControllerTabViewList.cnlqFilterLemmaTabView(getDistinctList(l));

    }

    public void resetFilter() {
        allEntries = true;
        word = false;
        multiword = false;
        verified = false;
        unverified = false;
        polysemic = false;
        monosemic = false;
        typesBlockDisabled = true;
        verifiyBlockDisabled = false;
        semicBlockDisabled = false;
        language = "All languages";
        pos = "any";
        String recoveryLang = lexiconCreationControllerTabViewList.getLexiconLanguage();
        lexiconCreationControllerTabViewList.initFormTabView(recoveryLang);
        lexiconCreationControllerTabViewList.initLemmaTabView(recoveryLang);

    }

    private String getLang(String individual) {
        Matcher matcher = entryLangPattern.matcher(individual);
        if (matcher.find()) {
            return matcher.group(1).split("_entry")[0];
        } else {
            return null;
        }
    }
}
