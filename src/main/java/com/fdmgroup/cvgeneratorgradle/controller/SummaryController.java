package com.fdmgroup.cvgeneratorgradle.controller;


import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Location;
import com.fdmgroup.cvgeneratorgradle.models.Stream;
import com.fdmgroup.cvgeneratorgradle.models.User;
import com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToDocument;
import com.fdmgroup.cvgeneratorgradle.views.SummaryPage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.saveObjectAsJson;
import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.selectFileFromFileChooser;

public class SummaryController extends FDMController{

    public SummaryController(CVTemplate cvTemplate, TreeView<String> treeView, Stage stage, Menu recent) {
        this.cvTemplate = cvTemplate;
        this.treeView = treeView;
        this.stage = stage;
        this.recent = recent;
    }

    public void initialize(BorderPane main, MainController mainController) {
        SummaryPage page = new SummaryPage(cvTemplate, stage);
        main.setCenter(page.createCenterPage(page.getCenterBox()));
        if (cvTemplate.getUser()==null) cvTemplate.setUser(new User("","",""));
        ObservableList<String> personalInformationList = FXCollections.observableArrayList(
                "First name: "+ cvTemplate.getUser().getFirstName(), "Last name: "+ cvTemplate.getUser().getLastName(),
                "Email: " + cvTemplate.getUser().getEmail());

        page.getPersonalInformation().setItems(personalInformationList);

        page.getSaveCV().setOnMousePressed(action -> {
                    File selectedFile = selectFileFromFileChooser("Save", "JSON File", "*.json", stage);
                    if (selectedFile != null) {
                        saveObjectAsJson(cvTemplate, selectedFile.getPath());
                        try {
                            mainController.loadRecentCV(stage);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
        });

        page.getGeneratePDFDocument().setOnAction(action -> {
            File selectedFilePDF = selectFileFromFileChooser("Save", "PDF File", "*.pdf", stage);
            if (selectedFilePDF!=null) {
                try {
                    SaveObjectToDocument.createDocument(cvTemplate, "PDF", selectedFilePDF.getPath(), main);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        page.getGenerateWordDocument().setOnAction(action -> {
            checkForNull();
            File selectedFileWord = selectFileFromFileChooser( "Save", "Word File", "*.docx", stage);
            if (selectedFileWord != null) {
                try {
                    SaveObjectToDocument.createDocument(cvTemplate, "docx", selectedFileWord.getPath(),main);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void checkForNull() {
        cvTemplate.setUser((cvTemplate.getUser()!=null) ? cvTemplate.getUser() : new User());
        cvTemplate.setLocation((cvTemplate.getLocation()!=null) ? cvTemplate.getLocation() : new Location());
        cvTemplate.setStream((cvTemplate.getStream()!=null) ? cvTemplate.getStream() : new Stream());
        cvTemplate.setKeySkills((cvTemplate.getKeySkills()!=null) ? cvTemplate.getKeySkills() : new ArrayList<>());
        cvTemplate.setCertificates((cvTemplate.getCertificates()!=null) ? cvTemplate.getCertificates() : new HashSet<>());
        cvTemplate.setInterests((cvTemplate.getInterests()!=null) ? cvTemplate.getInterests() : new HashSet<>());
        cvTemplate.setExperiences((cvTemplate.getExperiences()!=null) ? cvTemplate.getExperiences() : new ArrayList<>());
        cvTemplate.setEducations((cvTemplate.getEducations()!=null) ? cvTemplate.getEducations() : new ArrayList<>());
        cvTemplate.setLanguages((cvTemplate.getLanguages()!=null) ? cvTemplate.getLanguages() : new HashSet<>());
    }

    //not used by this class
    @Override
    void assignInput(MainController mainController) {
    }
}
