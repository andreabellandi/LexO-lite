/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.manager.FormData;
import it.cnr.ilc.lexolite.manager.LemmaData;
import it.cnr.ilc.lexolite.manager.SenseData;
import it.cnr.ilc.lexolite.util.LexiconUtil;
import static j2html.TagCreator.attrs;
import static j2html.TagCreator.div;
import static j2html.TagCreator.span;
import static j2html.TagCreator.li;
import static j2html.TagCreator.ul;
import static j2html.TagCreator.a;
import static j2html.TagCreator.i;
import static j2html.TagCreator.join;
import static j2html.TagCreator.sup;
import j2html.tags.ContainerTag;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author andrea
 */
@ViewScoped
@Named
public class LexiconControllerDictionary extends BaseController implements Serializable {

    private static final Logger LOG = LogManager.getLogger(LexiconControllerDictionary.class);

    @Inject
    private LexiconControllerFormDetail lexiconCreationControllerFormDetail;
    @Inject
    private LexiconControllerSenseDetail lexiconCreationControllerSenseDetail;

    // for handling numbers at the end of the forms
    Pattern patternLemma = Pattern.compile("(.+?)(\\d+\\*?)");
    Pattern patternSense = Pattern.compile("(.+)_([a-zA-Z]+)_(sense\\d+)");

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

    private String getLemma() {
        String ret;
        if (null != lexiconCreationControllerFormDetail) {
            if (null != lexiconCreationControllerFormDetail.getLemma()) {
                ret = lexiconCreationControllerFormDetail.getLemma().getFormWrittenRepr();
                return ret;
            }
        }
        LOG.error("Lemma not found!");
        return "unknown";
    }

    private String getPos() {
        return lexiconCreationControllerFormDetail.getLemma().getPoS().equals("No entry specified") ? "" : lexiconCreationControllerFormDetail.getLemma().getPoS();
    }

    private String getGender() {
        return lexiconCreationControllerFormDetail.getLemma().getGender().equals("No entry specified") ? "" : lexiconCreationControllerFormDetail.getLemma().getGender();
    }

    private String getNumber() {
        return lexiconCreationControllerFormDetail.getLemma().getNumber().equals("No entry specified") ? "" : lexiconCreationControllerFormDetail.getLemma().getNumber();
    }

    private String getPerson() {
        return lexiconCreationControllerFormDetail.getLemma().getPerson().equals("No entry specified") ? "" : lexiconCreationControllerFormDetail.getLemma().getPerson();
    }

    private String getMood() {
        return lexiconCreationControllerFormDetail.getLemma().getMood().equals("No entry specified") ? "" : lexiconCreationControllerFormDetail.getLemma().getMood();
    }

    private String getVoice() {
        return lexiconCreationControllerFormDetail.getLemma().getVoice().equals("No entry specified") ? "" : lexiconCreationControllerFormDetail.getLemma().getVoice();
    }

    private String getTraits() {
        return getMorphoTraits(getPos(), getGender(), getNumber(), getPerson(), getMood(), getVoice());
    }

    public String getLemmaGramGrpInfo(String lemmaId, String lemmaClassName, String expClassName, String gramGrpClassName) {
        String lemma = getLemma();
        String esponente = null;
        if (null != lemma) {
            Matcher matcher = patternLemma.matcher(lemma);
            if (matcher.find()) {
                lemma = matcher.group(1);
                esponente = matcher.group(2);
                LOG.info("lemma " + lemma + ", esponente " + esponente);
            }
            LOG.info("lemma " + lemma + ", NO esponente");
        }
        ContainerTag div = div(attrs("#" + lemmaId));
        ContainerTag spanLemma = span(lemma).withClass(lemmaClassName);
        if (esponente != null) {
            spanLemma.with(sup(esponente).withClass(expClassName));
        }
        div.with(spanLemma);
        div.with(span(getTraits()).withClass(gramGrpClassName));
        LOG.debug("div.renderFormatted() " + div.renderFormatted());
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
        LOG.debug("getLemmaComment() (" + ret + ")");
        return ret;
    }

    public String getVariants(String variantFormFrameClass, String variantClass, String variantFormClass, String variantAttestationClass, String variantNoteClass) {
        ContainerTag divVariants = div().withClass(variantFormFrameClass);
        for (FormData fd : lexiconCreationControllerFormDetail.getForms()) {
            ContainerTag div = div();
            div.with(span(fd.getFormWrittenRepr()).withClass(variantFormClass)).withClass(variantClass);
            if (!fd.getNote().isEmpty()) {
                div.with(div(span(fd.getNote())).withClass(variantNoteClass));
            }
            divVariants.with(div);
        }
        LOG.debug(divVariants.renderFormatted());
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
        LOG.info("variant: " + variant);
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
            if (sd.getName().contains("_sense")) {
                sb.append("sense ").append(sd.getName().split("_sense")[1]).append(": ");
                row.add(sb.toString());
                sb = new StringBuilder();
            } else {
                row.add(null);
            }
            if (!sd.getDefinition().isEmpty() && !sd.getDefinition().equals(Label.NO_ENTRY_FOUND)) { //sense definition 1
                sb.append(sd.getDefinition());
                row.add(sb.toString());
            } else {
                row.add(null);
            }
            row.add(sd.getOWLClass().getName()); //ontology class 2
            results.add(row);
        }

        return results;
    }

    public String getSense(List<String> sense, String id, String className, String smallCapsClass) {
        String name = sense.get(0);
        String def = sense.get(1);
        String onto = sense.get(2);
        ContainerTag mainDiv = div().withClass(className);
        if (null != name) {
            mainDiv.with(div(span(name)));
        }
        if (null != def) {
            mainDiv.with(div(span(join(i("Definition: "), def))));
        }
        if (onto.length() > 0) {
            mainDiv.with(div(span("Onto: " + onto)).withClass(smallCapsClass));
        }
        return mainDiv.renderFormatted();
    }

    public String getSeeAlso(String id, String seeAlsoClassName) {
        ContainerTag div = div().withClass(seeAlsoClassName);
        if (lexiconCreationControllerFormDetail.getLemma().getSeeAlso().size() > 0) {
            div.with(span("See also: "));
        }
        for (Iterator<LemmaData.Word> it = lexiconCreationControllerFormDetail.getLemma().getSeeAlso().iterator(); it.hasNext();) {
            LemmaData.Word word = it.next();
            div.with(span(word.getWrittenRep() + ((it.hasNext() ? "; " : ""))));
        }
        return div.renderFormatted();
    }

    public List<Map.Entry<String, String>> getSeeAlsoList(String id, String seeAlsoClassName) {
        ArrayList<Map.Entry<String, String>> ret = new ArrayList<>();
        for (Iterator<LemmaData.Word> it = lexiconCreationControllerFormDetail.getLemma().getSeeAlso().iterator(); it.hasNext();) {
            LemmaData.Word word = it.next();
            ret.add(new AbstractMap.SimpleEntry<>(word.getOWLName(), word.getWrittenRep()));
        }
        return ret;
    }

}
