/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import it.cnr.ilc.lexolite.controller.BaseController;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author andrea
 */
public class LexiconLocker extends BaseController implements Serializable {

    // Table for locking lexical entries. 
    // @key: uri
    // @value: user
    private Map<String, String> lexiconLockTable;
    private static final Logger LOG = LogManager.getLogger(LexiconLocker.class);

    public LexiconLocker() {
        lexiconLockTable = new HashMap<>();
    }

    public Map<String, String> getLexiconLockTable() {
        return lexiconLockTable;
    }

    public void lock(String user, String uri) {

        lexiconLockTable.put(uri, user);
        LOG.info("Locked " + uri + " for user " + user);
    }

    // returns true if a resource has been unlock, false elsewhether
    public boolean unlock(String user) {
        Iterator it = lexiconLockTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().equals(user)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    // returns true if a resource has been unlock by a specific user, false elsewhether
    public boolean unlock(String user, String le) {
        Iterator it = lexiconLockTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().equals(user) && (entry.getKey().equals(le))) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public void unlockAll(String user) {
        Iterator it = lexiconLockTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().equals(user)) {
                it.remove();
            }
        }
    }

    public void print() {
        Iterator it = lexiconLockTable.entrySet().iterator();
        LOG.info("Lock Table:");

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            LOG.info("Entry: " + entry.getKey() + " - User: " + entry.getValue());
        }
    }

    public String getUser(String uri) {
        return lexiconLockTable.get(uri);
    }

    public boolean isLocked(String uri) {
        LOG.info("is " + uri + " Locked ? " + lexiconLockTable.containsKey(uri));

        return lexiconLockTable.containsKey(uri);
    }

}
