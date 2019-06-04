/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.constant;

/**
 *
 * @author andrea
 */
public class OntoLexEntity {

    public enum Class {

        WORD("Word"),
        MULTIWORD("MultiwordExpression"),
        AFFIX("Affix"),
        FORM("Form"),
        LEXICALENTRY("LexicalEntry"),
        LEXICALSENSE("LexicalSense"),
        LEXICALCONCEPT("LexicalConcept");

        private String label;

        public String getLabel() {
            return label;
        }

        Class(String label) {
            this.label = label;
        }
    }

    public enum DataProperty {

        WRITTENREP("writtenRep");

        private String label;

        public String getLabel() {
            return label;
        }

        DataProperty(String label) {
            this.label = label;
        }
    }

    public enum ObjectProperty {

        CANONICALFORM("canonicalForm"),
        OTHERFORM("otherForm"),
        SENSE("sense"),
        ENTRY("entry"),
        REFERENCE("reference");

        private String label;

        public String getLabel() {
            return label;
        }

        ObjectProperty(String label) {
            this.label = label;
        }
    }

}
