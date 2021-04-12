/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cnr.ilc.lexolite.controller;

import it.cnr.ilc.lexolite.constant.Label;
import it.cnr.ilc.lexolite.manager.AttestationRenaming;
import it.cnr.ilc.lexolite.manager.ImageData;
import it.cnr.ilc.lexolite.manager.ImageManager;
import it.cnr.ilc.lexolite.manager.SenseData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ResizeEvent;
import org.primefaces.extensions.event.ImageAreaSelectEvent;
import org.primefaces.extensions.event.RotateEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author andreabellandi
 */


@Named
@ViewScoped
public class ImageController extends BaseController implements Serializable {

    @Inject
    private LexiconControllerFormDetail lexiconControllerFormDetail;
    @Inject
    private LexiconControllerSenseDetail lexiconControllerSenseDetail;
    @Inject
    private LoginController loginController;
    @Inject
    private ImageManager imageManager;

    private String source;
    private String date;
    private String description;

    private SenseData selectedSense;
    private ImageData clickedImage;

    private boolean metadataSaveButtonDisabled = true;

    public boolean isMetadataSaveButtonDisabled() {
        return metadataSaveButtonDisabled;
    }

    public void setMetadataSaveButtonDisabled(boolean metadataSaveButtonDisabled) {
        this.metadataSaveButtonDisabled = metadataSaveButtonDisabled;
    }
    
    
    
    public ImageData getClickedImage() {
        return clickedImage;
    }

    public void setClickedImage(ImageData clickedImage) {
        this.clickedImage = clickedImage;
    }

    public SenseData getSelectedSense() {
        return selectedSense;
    }

    public void setSelectedSense(SenseData selectedSense) {
        this.selectedSense = selectedSense;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void handleFileUpload(FileUploadEvent f) {
        try {
            persistImage(f.getFile());
        } catch (IOException ex) {
            Logger.getLogger(ImageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StreamedContent getImage() {
        StreamedContent img = null;
        if (clickedImage != null) {
            try {
                img = DefaultStreamedContent.builder()
                        .contentType("image/png")
                        .stream(() -> {
                            try {
                                return new FileInputStream(new File(System.getProperty("user.home")
                                        + Label.LEXO_FOLDER
                                        + Label.IMAGES_FOLDER
                                        + clickedImage.getFileName()));
                            } catch (FileNotFoundException e) {
                                return null;
                            }
                        })
                        .build();
            } catch (Exception e) {
            }
            return img;
        }
        return null;
    }

    public StreamedContent getImage(SenseData sd, int n) {
        if (sd != null) {
            if (sd.getImages().size() > (n - 1)) {
                StreamedContent img = null;
                try {
                    img = DefaultStreamedContent.builder()
                            .contentType("image/png")
                            .stream(() -> {
                                try {
                                    return new FileInputStream(new File(System.getProperty("user.home")
                                            + Label.LEXO_FOLDER
                                            + Label.IMAGES_FOLDER
                                            + sd.getImages().get(n - 1).getFileName()));
                                } catch (FileNotFoundException e) {
                                    return null;
                                }
                            })
                            .build();
                } catch (Exception e) {
                }
                return img;
            }
        }
        return null;
    }

    public void selectEndListener(final ImageAreaSelectEvent e) {
        final FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Area selected",
                "X1: " + e.getX1() + ", X2: " + e.getX2() + ", Y1: " + e.getY1() + ", Y2: " + e.getY2()
                + ", Image width: " + e.getImgWidth() + ", Image height: " + e.getImgHeight());

        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void rotateListener(final RotateEvent e) {
    }

    public void resizeListener(final ResizeEvent e) {
    }

    public String getLemma() {
        return lexiconControllerFormDetail.getLemma().getFormWrittenRepr();
    }

    public synchronized void persistImage(UploadedFile f) throws IOException {
        String fileName = selectedSense.getName() + "_" + f.getFileName();
        imageManager.insertImage(loginController.getAccount(), selectedSense.getName(), f.getFileName(), fileName, description, source, date);
        InputStream input = f.getInputStream();
        File targetFile = new File(System.getProperty("user.home") + Label.LEXO_FOLDER + Label.IMAGES_FOLDER + fileName);
        FileUtils.copyInputStreamToFile(input, targetFile);
        // ---TODO: scale image to 400x400
        log(org.apache.log4j.Level.INFO, loginController.getAccount(), "UPLOADED image " + f.getFileName() + " to sense " + selectedSense.getName()
                + " (saved in " + System.getProperty("user.home") + Label.LEXO_FOLDER + Label.IMAGES_FOLDER + f.getFileName() + ")");
        info("template.message.saveSense.summary", "template.message.saveSense.description", selectedSense.getName());

        // sense data update
        ImageData imgd = new ImageData();
        imgd.setDate(date);
        imgd.setDescription(description);
        imgd.setFileName(fileName);
        imgd.setOriginalFileName(f.getFileName());
        imgd.setSource(source);
        int senseIndex = lexiconControllerSenseDetail.getSenses().indexOf(selectedSense);
        lexiconControllerSenseDetail.getSenses().get(senseIndex).getImages().add(imgd);
    }
    
    public void saveMetadata() {
        log(org.apache.log4j.Level.INFO, loginController.getAccount(), "UPDATED metadata of " + clickedImage.getFileName());
        clickedImage.setDate(date);
        clickedImage.setDescription(description);
        clickedImage.setSource(source);
        imageManager.update(clickedImage);
        info("template.message.updateMetadataImage.summary", "template.message.updateMetadataImage.description");
        metadataSaveButtonDisabled = true;
    }
    
    public void metadataSourceKeyUpEvent(AjaxBehaviorEvent e) {
        String _source = (String) e.getComponent().getAttributes().get("value");
        source = _source;
        log(org.apache.log4j.Level.INFO, loginController.getAccount(), "UPDATE source metadata of " + clickedImage.getFileName());
        metadataSaveButtonDisabled = false;
    }
    
    public void metadataDateKeyUpEvent(AjaxBehaviorEvent e) {
        String _date = (String) e.getComponent().getAttributes().get("value");
        date = _date;
        log(org.apache.log4j.Level.INFO, loginController.getAccount(), "UPDATE date metadata of " + clickedImage.getFileName());
        metadataSaveButtonDisabled = false;
    }
    
    public void metadataDescriptionKeyUpEvent(AjaxBehaviorEvent e) {
        String _desc = (String) e.getComponent().getAttributes().get("value");
        description = _desc;
        log(org.apache.log4j.Level.INFO, loginController.getAccount(), "UPDATE description metadata of " + clickedImage.getFileName());
        metadataSaveButtonDisabled = false;
    }

}
