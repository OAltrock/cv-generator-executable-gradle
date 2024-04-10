package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.fdmgroup.cvgeneratorgradle.CVGeneratorApp.*;

public class EducationController {
    @FXML
    private Label hintDegree;
    @FXML
    private Label hintStudyTitle;
    @FXML
    private TextField degree;
    @FXML
    private TextField studyTitle;
    @FXML
    private Label hintUniversity;
    @FXML
    private Label hintUniPlace;
    @FXML
    private TextField uniPlace;
    @FXML
    private Label hintThesis;
    @FXML
    private TextField thesis;
    @FXML
    private Label hintDate;
    @FXML
    private Label hintLabel;
    @FXML
    private TextField keyModule;
    @FXML
    private TextField uni;

    private Node parent;

    public EducationController(Node parent) {
        this.parent = parent;
    }

    @FXML
    public void initialize() {
        FXMLLoader fxmlLoader = new FXMLLoader(CVGeneratorApp.class.getResource("education.fxml"));
        try {
            BorderPane borderPane = (BorderPane) parent;
            borderPane.setCenter(fxmlLoader.load());
            Button addModuleButton = (Button) borderPane.getCenter().lookup("#addModuleBtn");
            addModuleButton.setOnAction(event -> {
                GridPane keyModules = (GridPane) borderPane.getCenter().lookup("#keyModules");
                TextField moduleToAdd = new TextField();
                moduleToAdd.setId("keyModule"+getCounter1());
                setCounter1(getCounter1()+1);
                moduleToAdd.setStyle("-fx-pref-width: 300;");
                moduleToAdd.setPromptText("Key Module");
                Button removeButton = new Button("Remove Key Module");
                removeButton.setId("removeBtn"+ (getCounter1() - 1));
                addListenerTooRemoveBtn(removeButton, keyModules, addModuleButton);
                keyModules.add(moduleToAdd ,0,getCounter1());
                keyModules.add(removeButton, 1,getCounter1());
                keyModules.getChildren().remove(addModuleButton);
                keyModules.add(addModuleButton, 2,getCounter1());
            });
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    private void addListenerTooRemoveBtn(Button removeButton, GridPane gridPane, Button addModuleBtn) {
        removeButton.setOnAction(event -> {
            Button which = (Button) event.getSource();
            String id = String.valueOf(which.getId().charAt(which.getId().length()-1));
            TextField toRemove = (TextField) gridPane.lookup("#keyModule"+id);
            gridPane.getChildren().remove(toRemove);
            gridPane.getChildren().remove(removeButton);
            setCounter1(getCounter1()-1);
            gridPane.getChildren().remove(addModuleBtn);
            gridPane.add(addModuleBtn,2,getCounter1());

        });
    }


}
