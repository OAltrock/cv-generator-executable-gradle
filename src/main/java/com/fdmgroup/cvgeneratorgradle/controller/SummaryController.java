package com.fdmgroup.cvgeneratorgradle.controller;


import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.User;
import com.fdmgroup.cvgeneratorgradle.views.SummaryPage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.saveObjectAsJson;

public class SummaryController {
    CVTemplate cvTemplate;
    TreeView<String> treeView;
    private final Stage stage;

    public SummaryController(CVTemplate cvTemplate, TreeView<String> treeView, Stage stage) {
        this.cvTemplate = cvTemplate;
        this.treeView = treeView;
        this.stage = stage;
    }

    public void initialize(BorderPane main) {
        SummaryPage page = new SummaryPage(cvTemplate, stage);
        main.setCenter(page.createCenterPage(page.getCenterBox()));
        if (cvTemplate.getUser()==null) cvTemplate.setUser(new User("","",""));
        ObservableList<String> personalInformationList = FXCollections.observableArrayList(
                "First name: "+ cvTemplate.getUser().getFirstName(), "Last name: "+ cvTemplate.getUser().getLastName(),
                "Email: " + cvTemplate.getUser().getEmail());

        page.getPersonalInformation().setItems(personalInformationList);
        page.getSaveCV().setOnAction(action -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON File","*.json"));
            File selectedFile = fileChooser.showSaveDialog(stage);
            if (selectedFile!=null) saveObjectAsJson(cvTemplate, selectedFile.getPath());
        });

    }
}
