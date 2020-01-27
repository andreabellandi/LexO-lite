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

        // Core
        WORD("Word"),
        MULTIWORD("MultiwordExpression"),
        AFFIX("Affix"),
        FORM("Form"),
        LEXICALENTRY("LexicalEntry"),
        LEXICALSENSE("LexicalSense"),
        LEXICALCONCEPT("LexicalConcept"),
        
        // vartrans module
        LEXICALRELATION("LexicalRelation"),
        TRANSLATION("Translation"),
        TERMINOLOGICALRELATION("TerminologicalRelation"),
        SENSERELATION("SenseRelation"),
        LEXICOSEMANTICRELATION("LexicoSemanticRelation"),

        // synsem module
        SYNTACTICFRAME("SyntacticFrame"),
        SYNTACTICARGUMENT("SyntacticArgument"),
        ONTOMAP("OntoMap");
        
        private String label;

        public String getLabel() {
            return label;
        }

        Class(String label) {
            this.label = label;
        }
    }

    public enum DataProperty {

        // Core
        WRITTENREP("writtenRep"),

        // synsem module
        OPTIONAL("optional");
        
        private String label;

        public String getLabel() {
            return label;
        }

        DataProperty(String label) {
            this.label = label;
        }
    }

    public enum ObjectProperty {

        // Core
        CANONICALFORM("canonicalForm"),
        OTHERFORM("otherForm"),
        SENSE("sense"),
        ENTRY("entry"),
        REFERENCE("reference"),
        
        // vartrans module
        LEXICALREL("lexicalRel"),
        SENSEREL("senseRel"),
        TRANSLATABLEAS("translatableAs"),
        TRANSLATION("translation"),
        RELATES("relates"),
        SOURCE("source"),
        TARGET("target"),
        CATEGORY("category"),
        
        // synsem module
        SYNBEHAVIOR("synBehavior"),
        SYNARG("synArg"),
        ONTOMAPPING("ontoMapping"),
        ONTOCORRESPONDENCE("ontoCorrespondence"),
        ISA("isA"),
        SUBJOFPROP("subjOfProp"),
        OBJOFPROP("objOfProp"),
        MARKER("marker"),
        SUBMAP("submap");
        
        private String label;

        public String getLabel() {
            return label;
        }

        ObjectProperty(String label) {
            this.label = label;
        }
    }

}
