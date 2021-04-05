/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author andreabellandi
 */
public class AttestationRenaming implements Serializable {

    private ArrayList<AttestationFormUris> attestationFormUris;
    private ArrayList<AttestationSenseUris> attestationSenseUris;

    public ArrayList<AttestationFormUris> getAttestationFormUris() {
        return attestationFormUris;
    }

    public void setAttestationFormUris(ArrayList<AttestationFormUris> attestationFormUris) {
        this.attestationFormUris = attestationFormUris;
    }

    public ArrayList<AttestationSenseUris> getAttestationSenseUris() {
        return attestationSenseUris;
    }

    public void setAttestationSenseUris(ArrayList<AttestationSenseUris> attestationSenseUris) {
        this.attestationSenseUris = attestationSenseUris;
    }
    
    public AttestationRenaming(ArrayList<AttestationFormUris> attFormUris, ArrayList<AttestationSenseUris> attSenseUris) {
        this.attestationFormUris = attFormUris;
        this.attestationSenseUris = attSenseUris;
    }

    public static class AttestationFormUris {

        private String oldFormUri;
        private String newFormUri;
        private String oldForm;
        private String newForm;

        public String getOldFormUri() {
            return oldFormUri;
        }

        public void setOldFormUri(String oldFormUri) {
            this.oldFormUri = oldFormUri;
        }

        public String getNewFormUri() {
            return newFormUri;
        }

        public void setNewFormUri(String newFormUri) {
            this.newFormUri = newFormUri;
        }

        public String getOldForm() {
            return oldForm;
        }

        public void setOldForm(String oldForm) {
            this.oldForm = oldForm;
        }

        public String getNewForm() {
            return newForm;
        }

        public void setNewForm(String newForm) {
            this.newForm = newForm;
        }
        
        public AttestationFormUris(String oldFormUri, String newFormUri, String oldForm, String newForm) {
            this.newForm = newForm;
            this.newFormUri = newFormUri;
            this.oldForm = oldForm;
            this.oldFormUri = oldFormUri;
        }

    }

    public static class AttestationSenseUris {

        private String oldSenseUri;
        private String newSenseUri;

        public String getOldSenseUri() {
            return oldSenseUri;
        }

        public void setOldSenseUri(String oldSenseUri) {
            this.oldSenseUri = oldSenseUri;
        }

        public String getNewSenseUri() {
            return newSenseUri;
        }

        public void setNewSenseUri(String newSenseUri) {
            this.newSenseUri = newSenseUri;
        }

        public AttestationSenseUris(String oldSenseUri, String newSenseUri) {
            this.newSenseUri = newSenseUri;
            this.oldSenseUri = oldSenseUri;
        }
    }

}
