/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.MelchuckModelExtension;
import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.domain.Attestation;
import it.cnr.ilc.lexolite.manager.FormData;
import it.cnr.ilc.lexolite.manager.LemmaData;
import it.cnr.ilc.lexolite.manager.ReferenceMenuTheme;
import it.cnr.ilc.lexolite.manager.SenseData;
import static j2html.TagCreator.a;
import static j2html.TagCreator.attrs;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.span;
import static j2html.TagCreator.i;
import static j2html.TagCreator.img;
import static j2html.TagCreator.join;
import static j2html.TagCreator.sup;
import j2html.tags.ContainerTag;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.event.Level;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerDictionary extends BaseController implements Serializable {

    @Inject
    private LexiconControllerFormDetail lexiconCreationControllerFormDetail;
    @Inject
    private LexiconControllerSenseDetail lexiconCreationControllerSenseDetail;
    @Inject
    private LexiconControllerVarTransSenseDetail lexiconControllerVarTransSenseDetail;
    @Inject
    private LexiconControllerTabViewList lexiconControllerTabViewList;
    @Inject
    private LexiconControllerAttestation lexiconControllerAttestation;
    //results cache
    private HashMap<String, ArrayList> senseLexicalFunctionC;

    public ArrayList getSenseLexicalFunctionC(String s) {
        return senseLexicalFunctionC.get(s);
    }

    public void setSenseLexicalFunctionC(String s, ArrayList senseLexicalFunctionC) {
        this.senseLexicalFunctionC.put(s, senseLexicalFunctionC);
    }

    // for handling numbers at the end of the forms
    Pattern patternLemma = Pattern.compile("(.+?)(\\d+\\*?)");
    Pattern patternSense = Pattern.compile("(.+)_([a-zA-Z]+)_(sense\\d+)");

    public LexiconControllerDictionary() {
        this.senseLexicalFunctionC = new HashMap<>();
    }

    private String getMorphoTraits(String pos, String gender, String number, String person, String mood, String voice) {
        StringBuilder morpho = new StringBuilder();
        morpho.append((pos.isEmpty() || pos.equals(Label.NO_ENTRY_FOUND)) ? "" : pos.charAt(0) + ". ");
        morpho.append((gender.isEmpty() || gender.equals(Label.NO_ENTRY_FOUND)) ? "" : gender.charAt(0) + ". ");
        morpho.append((number.isEmpty() || number.equals(Label.NO_ENTRY_FOUND)) ? "" : number.charAt(0) + ".");
        // traits only for verbs
        morpho.append((person.isEmpty() || person.equals(Label.NO_ENTRY_FOUND)) ? "" : person.charAt(0) + ".");
        morpho.append((mood.isEmpty() || mood.equals(Label.NO_ENTRY_FOUND)) ? "" : mood.charAt(0) + ".");
        morpho.append((voice.isEmpty() || voice.equals(Label.NO_ENTRY_FOUND)) ? "" : voice.charAt(0) + ".");

        return morpho.toString();
    }

    private String getMorphoTraits(ArrayList<LemmaData.MorphoTrait> almt) {
        StringBuilder morpho = new StringBuilder();
        for (LemmaData.MorphoTrait mt : almt) {
            morpho.append(mt.getValue()).append(" ");
        }
        return morpho.toString().trim();
    }

    private String getLemma() {
        String ret;
        if (null != lexiconCreationControllerFormDetail) {
            if (null != lexiconCreationControllerFormDetail.getLemma()) {
                ret = lexiconCreationControllerFormDetail.getLemma().getFormWrittenRepr();
                return ret;
            }
        }
        log(Level.ERROR, "Lemma not found!");
        return "unknown";
    }

    private String getPos() {
        String ret;
        if (null != lexiconCreationControllerFormDetail) {
            if (null != lexiconCreationControllerFormDetail.getLemma()) {
                ret = lexiconCreationControllerFormDetail.getLemma().getPoS();
                return ret;
            }
        }
        log(Level.ERROR, "Lemma not found!");
        return "unknown";
    }

    private boolean isVerified() {
        if (null != lexiconCreationControllerFormDetail) {
            if (null != lexiconCreationControllerFormDetail.getLemma()) {
                return !lexiconCreationControllerFormDetail.getLemma().getValid().equals("false");
            }
        }
        return false;
    }

    private ArrayList<LemmaData.MorphoTrait> getMorphoTraits() {
        return lexiconCreationControllerFormDetail.getLemma().getMorphoTraits();
    }

    private String getTraits() {
        return getMorphoTraits(getMorphoTraits());
        //return getMorphoTraits(getPos(), getGender(), getNumber(), getPerson(), getMood(), getVoice());
    }

    public String getLemmaGramGrpInfo(String lemmaId, String lemmaClassName, String expClassName, String gramGrpClassName, String verifiedClass) {
        String lemma = getLemma();
        String esponente = null;
        if (null != lemma) {
            Matcher matcher = patternLemma.matcher(lemma);
            if (matcher.find()) {
                lemma = matcher.group(1);
                esponente = matcher.group(2);
                log(Level.INFO, "lemma " + lemma + ", esponente " + esponente);
            }
            log(Level.INFO, "lemma " + lemma + ", NO esponente");
        }
        ContainerTag div = div(attrs("#" + lemmaId)).withStyle("position: relative;");
        ContainerTag spanLemma = span(lemma).withClass(lemmaClassName);
        if (esponente != null) {
            spanLemma.with(sup(esponente).withClass(expClassName));
        }
        div.with(spanLemma);
        div.with(span(getPos()).withClass(gramGrpClassName));
        div.with(span(getTraits()).withClass(gramGrpClassName));
//        div.with(img().withSrc(getClass().getResource("resources/image/ilccnr.png").getPath()).withClass(verifiedClass));
        div.with(img().withSrc(isVerified() ? "resources/image/locked.png" : "resources/image/unlocked.png").withClass(verifiedClass));
        log(Level.DEBUG, "div.renderFormatted() " + div.renderFormatted());
        return div.renderFormatted();
    }

    public boolean isRendable() {
        return lexiconCreationControllerFormDetail.getLemma().getFormWrittenRepr() != null;
    }

    public boolean isRendableLemmaComment() {
        boolean ret = !lexiconCreationControllerFormDetail.getLemma().getNote().isEmpty();
        return ret;
    }

    public String getLemmaComment(String id, String className) {
        String ret = "";
        if (isRendableLemmaComment()) {
            ret = lexiconCreationControllerFormDetail.getLemma().getNote();
        }
        log(Level.DEBUG, "getLemmaComment() (" + ret + ")");
        return ret;
    }

    public List<String> getVariants() {

        ArrayList variants = new ArrayList();
        for (FormData fd : lexiconCreationControllerFormDetail.getForms()) {
            variants.add(fd.getFormWrittenRepr());
        }
        return variants;
    }

    public String getVariants(String variantFormFrameClass, String variantClass, String variantFormClass, String variantFormMorphoClass, String variantAttestationClass, String variantNoteClass) {
        ContainerTag divVariants = div().withClass(variantFormFrameClass);
        for (FormData fd : lexiconCreationControllerFormDetail.getForms()) {
            ContainerTag div = div();
            div.with(span(fd.getFormWrittenRepr()).withClass(variantFormClass)).withClass(variantClass);
            String morpho = "";
            for (LemmaData.MorphoTrait mt : fd.getMorphoTraits()) {
                morpho = morpho + mt.getValue() + " ";
            }
            if (!morpho.isEmpty()) {
                div.with(span(morpho.trim()).withClass(variantFormMorphoClass)).withClass(variantClass);
            }
            if (!fd.getNote().isEmpty()) {
                div.with(div(span(fd.getNote())).withClass(variantNoteClass));
            }
            divVariants.with(div);
        }
        log(Level.DEBUG, divVariants.renderFormatted());
        return divVariants.renderFormatted();

    }

    public List<List<String>> __getVariantsList() {
        ArrayList results = new ArrayList();
        for (FormData fd : lexiconCreationControllerFormDetail.getForms()) {
            ArrayList row = new ArrayList();
            row.add(fd.getFormWrittenRepr());//0
            row.add((!fd.getNote().isEmpty() ? fd.getNote() : null));//2
            results.add(row);
        }
        return results;
    }

    public String __getVariantForm(List<String> variant) {
        StringBuilder sb = new StringBuilder();
        sb.append(variant.get(0));
        if (null != variant.get(1)) {
            sb.append(" ").append("[").append(variant.get(1)).append("]");
        }
        return sb.toString();
    }

    public String __getVariantNote(List<String> variant) {
        return variant.get(1); //note
    }

    public String getVariantAttributes(List<String> variant) {
        log(Level.DEBUG, "variant: " + variant);
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i < variant.size(); i++) {
            sb.append(variant.get(i));
            sb.append("; ");
        }
        String attestations;
        if (sb.length() > 2) {
            attestations = sb.substring(0, sb.length() - 1);
        } else {
            attestations = sb.toString();
        }

        return attestations;
    }

    public List<List<String>> getSensesList() {
        ArrayList results = new ArrayList();
        for (SenseData sd : lexiconCreationControllerSenseDetail.getSenses()) {
            ArrayList row = new ArrayList();
            StringBuilder sb = new StringBuilder();
            row.add(sd.getName()); //sense iri 0
            if (sd.getName().contains("_sense")) {
                if (lexiconCreationControllerSenseDetail.getSenses().size() > 1) {
                    sb.append("sense ").append(sd.getName().split("_sense")[1]);
                    row.add(sb.toString());
                    sb = new StringBuilder();
                } else {
                    row.add(null);
                }
            } else {
                row.add(null);
            }
            if (!sd.getDefinition().isEmpty() && !sd.getDefinition().equals(Label.NO_ENTRY_FOUND)) { //sense definition 1
                sb.append(sd.getDefinition());
                row.add(sb.toString());
            } else {
                row.add(null);
            }
            if (sd.getThemeOWLClass() != null) {
                log(Level.DEBUG, "getThemeOWLClass(): " + sd.getThemeOWLClass().getType());
                log(Level.DEBUG, "getThemeOWLClass(): " + ReferenceMenuTheme.itemType.none.equals(sd.getThemeOWLClass().getType()));
                if (!ReferenceMenuTheme.itemType.none.toString().equals(sd.getThemeOWLClass().getType())) {
                    row.add("[" + sd.getThemeOWLClass().getNamespace().split("#")[0] + "] " + sd.getThemeOWLClass().getName() + " (" + sd.getThemeOWLClass().getType().replace("zz", "ss") + ")"); //ontology class 2
                } else {
                    row.add(null);
                }
                results.add(row);
            } else {
                log(Level.ERROR, "sd.getThemeOWLClass() is null!!");
            }
        }

        return results;
    }

    public List<String> getSenseExamples(String senseIRI) {
        ArrayList ret = new ArrayList();
        log(Level.DEBUG, senseIRI);
        for (Attestation att : lexiconControllerAttestation.getAttestationsForDictionary(senseIRI)) {
            log(Level.DEBUG, "attestation: " + att.getAttestation());
            ret.add(att.getAttestation());
        }
        return ret;
    }

    public List<String> getSenseLexicalFunction(String senseIRI) {
        ArrayList ret = getSenseLexicalFunctionC(senseIRI);
        if (ret == null) {
            ret = new ArrayList();
            log(Level.DEBUG, senseIRI);
            SenseData sd = lexiconControllerVarTransSenseDetail.getSenseVarTrans(senseIRI);
            if (null != sd) {
                for (SenseData.LexicalFunction lf : sd.getLexicalFunctions()) {
                    if (MelchuckModelExtension.getParadigmaticRenderingTable().get(lf.getLexFunName()) != null) {
                        ret.add(MelchuckModelExtension.getParadigmaticRenderingTable().get(lf.getLexFunName()) + "("
                                + getName(lf.getSource()) + ") = " + getName(lf.getTarget()));
                    } else {
                        ret.add(MelchuckModelExtension.getSyntagmaticRenderingTable().get(lf.getLexFunName()) + "("
                                + getName(lf.getSource()) + ") = " + getName(lf.getTarget()));
                    }
                }
                setSenseLexicalFunctionC(senseIRI, ret);
            }
        }
        return ret;
    }

    /**
     * Retrieve all the relations about the specified <i>senseIri</i>
     * @param senseIRI
     * @return for each senseRel there is an array consisting of three values : the name of relation, the written representation, the language.
     */
    public List<List<String>> getSenseRels(String senseIRI) {
        ArrayList ret = new ArrayList();
        log(Level.DEBUG, senseIRI);

        SenseData sd = lexiconControllerVarTransSenseDetail.getSenseVarTrans(senseIRI);
        if (sd != null) {

            if (sd.getSenseRels().size() > 0) {
                for (SenseData.SenseRelation sr : sd.getSenseRels()) {
                    ArrayList row = new ArrayList();
                    row.add(sr.getRelation());
                    row.add(sr.getWrittenRep());
                    row.add(sr.getLanguage());
                    log(Level.DEBUG, "sense information: " + row);
                    ret.add(row);
                }
            }
        }
        return ret;
    }

    /**
     * @deprecated old method used to create and render all the information about senses.
     * @param sense
     * @param id
     * @param className
     * @param smallCapsClass
     * @return 
     */
    public String getSense(List<String> sense, String id, String className, String smallCapsClass) {
        Map<String, ArrayList<LinkedDictionaryEntry>> senseRelations = new HashMap<>();
        String senseIRI = sense.get(0);
        String name = sense.get(1);
        String def = sense.get(2);
        String onto = sense.get(3);
        ContainerTag mainDiv = div().withClass(className);
        if (null != name) {
            mainDiv.with(div(span(name)));
        }
        if (null != def) {
            mainDiv.with(div(span(join(i("definition: "), def))));
        }

        for (Attestation att : lexiconControllerAttestation.getAttestationsForDictionary(senseIRI)) {
            mainDiv.with(div(span(join(i("usage example: "), att.getAttestation()))));
        }

        SenseData sd = lexiconControllerVarTransSenseDetail.getSenseVarTrans(senseIRI);
        if (sd != null) {
            if (sd.getReifiedTranslationRels().size() > 0) {
                for (SenseData.ReifiedTranslationRelation rtr : sd.getReifiedTranslationRels()) {
                    mainDiv.with(div(span(join(rtr.getCategory()
                            + (rtr.getConfidence() < 1.0 ? " (" + rtr.getConfidence() + ")" : "")
                            + ": ",
                            a(getName(rtr.getTargetWrittenRep()) + " (" + rtr.getTargetLanguage() + ")").attr("style", "text-decoration: underline;")
                                    .attr("onclick", "rc([{name:'entry',value:'" + rtr.getTarget() + "'},{name:'type',value:'Sense'}]);")))));
                }
            }
            if (sd.getSenseRels().size() > 0) {
                for (SenseData.SenseRelation sr : sd.getSenseRels()) {
                    ArrayList<LinkedDictionaryEntry> target = senseRelations.get(sr.getRelation());
                    if (target != null) {
                        target.add(new LinkedDictionaryEntry(", " + getName(sr.getWrittenRep()) + " (" + sr.getLanguage() + ")", sr.getWrittenRep()));
                        senseRelations.put(sr.getRelation(), target);
                    } else {
                        ArrayList<LinkedDictionaryEntry> alde = new ArrayList();
                        alde.add(new LinkedDictionaryEntry(getName(sr.getWrittenRep()) + " (" + sr.getLanguage() + ")", sr.getWrittenRep()));
                        senseRelations.put(sr.getRelation(), alde);
                    }
                }
                for (Map.Entry<String, ArrayList<LinkedDictionaryEntry>> entry : senseRelations.entrySet()) {
                    mainDiv.with(div(span(join(entry.getKey() + ": ",
                            each(entry.getValue(), e
                                    -> a(e.getName()).attr("style", "text-decoration: underline;")
                                    .attr("onclick", "rc([{name:'entry',value:'" + e.getUri() + "'},{name:'type',value:'Sense'}]);"))))));
                }
            }
        }

        // Melchuck's lexical function's
        if (sd != null) {
            if (sd.getLexicalFunctions().size() > 0) {
                for (SenseData.LexicalFunction lf : sd.getLexicalFunctions()) {
                    if (MelchuckModelExtension.getParadigmaticRenderingTable().get(lf.getLexFunName()) != null) {
                        mainDiv.with(div(span(join(MelchuckModelExtension.getParadigmaticRenderingTable().get(lf.getLexFunName()) + "("
                                + getName(lf.getSource()) + ") = ", getName(lf.getTarget())))).withClass("lexicalFunctionClass"));
                    } else {
                        mainDiv.with(div(span(join(MelchuckModelExtension.getSyntagmaticRenderingTable().get(lf.getLexFunName()) + "("
                                + getName(lf.getSource()) + ") = ", getName(lf.getTarget())))).withClass("lexicalFunctionClass"));
                    }
                }
            }
        }

        if (null != onto) {
            if (onto.length() > 0) {
                mainDiv.with(div(span("semantic reference: " + onto)).withClass(smallCapsClass));
            }
        }
        return mainDiv.renderFormatted();
    }

    private String getName(String iri) {
        String[] _target = iri.split("_");
        String target = "";
        for (int i = 0; i < (_target.length - 3); i++) {
            target = target + _target[i] + " ";
        }
        return target.trim();
    }

    public String getSeeAlso(String id, String seeAlsoClassName) {
        ContainerTag div = div().withClass(seeAlsoClassName);
        if (lexiconCreationControllerFormDetail.getLemma().getSeeAlso().size() > 0) {
            div.with(span("See also: "));
        }
        for (Iterator<LemmaData.Word> it = lexiconCreationControllerFormDetail.getLemma().getSeeAlso().iterator(); it.hasNext();) {
            LemmaData.Word word = it.next();
            div.with(span(join("",
                    a(word.getWrittenRep()).attr("style", "text-decoration: underline;")
                            .attr("onclick", "rc([{name:'entry',value:'" + word.getOWLName() + "'},{name:'type',value:'Lemma'}]);"),
                    ((it.hasNext() ? "; " : "")))));
        }
        return div.renderFormatted();
    }

//    public List<Map.Entry<String, String>> getSeeAlsoList(String id, String seeAlsoClassName) {
//        ArrayList<Map.Entry<String, String>> ret = new ArrayList<>();
//        for (Iterator<LemmaData.Word> it = lexiconCreationControllerFormDetail.getLemma().getSeeAlso().iterator(); it.hasNext();) {
//            LemmaData.Word word = it.next();
//            ret.add(new AbstractMap.SimpleEntry<>(word.getOWLName(), word.getWrittenRep()));
//        }
//        return ret;
//    }
    public void execute() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        lexiconControllerTabViewList.onDictionaryViewSelect(params.get("entry"), params.get("type"));
    }

    private static class LinkedDictionaryEntry {

        private String name;
        private String uri;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public LinkedDictionaryEntry(String name, String uri) {
            this.name = name;
            this.uri = uri;
        }

    }

    public void clearCaches() {
        this.senseLexicalFunctionC = new HashMap<>();
    }
}
