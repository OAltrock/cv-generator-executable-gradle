package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class ExperienceController implements Initialization{

    private BorderPane main;

    @Override
    public void initialize(BorderPane main, String resource) {
        Initialization.super.initialize(main, resource);
        this.main = main;
    }

    /*@FXML
    public void initialize() {
        FXMLLoader fxmlLoader = new FXMLLoader(CVGeneratorApp.class.getResource(resource));
        try {
            parent.setCenter(fxmlLoader.load());
        }
        catch (Exception e) {

        }
    }*/





    /*@FXML
    public void initialize() {
        FXMLLoader fxmlLoader = new FXMLLoader(CVGeneratorApp.class.getResource("experience.fxml"));
        try {
            BorderPane borderPane = (BorderPane) parent;
            borderPane.setCenter(fxmlLoader.load());
            Button saveBtn = (Button) borderPane.getCenter().lookup("#saveBtn");
            createKeyModulesArea(borderPane);
            createValidation(borderPane);
            saveBtn.disableProperty().bind(validInput);
        } catch (IOException e) {
            System.out.println(e);
        }

    }*/
}
