package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EducationController {

    @FXML
    private TextField university;

    private Node parent;

    public EducationController(Node parent) {
        this.parent = parent;
    }

    @FXML
    public void initialize() {
        FXMLLoader fxmlLoader = new FXMLLoader(CVGeneratorApp.class.getResource("education.fxml"));
        try {
            BorderPane borderPane = (BorderPane) parent;
            VBox centerBox = (VBox) borderPane.getCenter();
            centerBox.setAlignment(Pos.TOP_CENTER);
            borderPane.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println(e);
        }

    }
}
