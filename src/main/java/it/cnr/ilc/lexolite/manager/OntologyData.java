/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.manager;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andrea
 */
public class OntologyData {

    private List<Metadata> metadata;
    private List<IndividualDetails> individuals;

    public List<Metadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<Metadata> metadata) {
        this.metadata = metadata;
    }

    public List<IndividualDetails> getIndividuals() {
        return individuals;
    }

    public void setIndividuals(List<IndividualDetails> individuals) {
        this.individuals = individuals;
    }

    public OntologyData() {
    }

    public static class Metadata {

        private String label;
        private String content;

        public Metadata(String label, String content) {
            this.label = label;
            this.content = content;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }

    public static class IndividualDetails {

        private String iri;
        private String shortForm;
        private ArrayList<Metadata> metadata;
        private ArrayList<IndividualAxiom> axioms;

        public IndividualDetails() {
        }

        public String getShortForm() {
            return shortForm;
        }

        public void setShortForm(String shortForm) {
            this.shortForm = shortForm;
        }

        public String getIri() {
            return iri;
        }

        public void setIri(String iri) {
            this.iri = iri;
        }

        public ArrayList<Metadata> getMetadata() {
            return metadata;
        }

        public void setMetadata(ArrayList<Metadata> metadata) {
            this.metadata = metadata;
        }

        public ArrayList<IndividualAxiom> getAxioms() {
            return axioms;
        }

        public void setAxioms(ArrayList<IndividualAxiom> axioms) {
            this.axioms = axioms;
        }

    }

    public static class IndividualAxiom {

        public enum AxiomType {
            OBJECT_TYPE,
            DATA_TYPE;
        }

        private String property;
        private String value;
        private AxiomType type;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public AxiomType getType() {
            return type;
        }

        public void setType(AxiomType type) {
            this.type = type;
        }

        public IndividualAxiom(String property, String value, AxiomType type) {
            this.property = property;
            this.value = value;
            this.type = type;
        }

    }

}
