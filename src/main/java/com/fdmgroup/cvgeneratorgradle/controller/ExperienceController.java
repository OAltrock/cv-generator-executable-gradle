package com.fdmgroup.cvgeneratorgradle.controller;


import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ExperienceController implements Initialization{

    @Override
    public void initialize(BorderPane main, String resource) {
        Initialization.super.initialize(main, resource);

        VBox center = (VBox) main.getCenter();
        List<Node> textFields = center.getChildren().stream().filter(child -> child.getClass().toString().contains("TextField")).toList();

        center.getChildren().forEach(child -> {
            if (child.getClass().toString().contains("GridPane")) {
                child.setId("keyPositions");
            }
        });
        GridPane gridPane = (GridPane) main.getCenter().lookup("#keyPositions");
        Button saveBtn = (Button) main.getCenter().lookup("#saveBtn");
        saveBtn.setOnAction(event -> {

            Boolean mainValidated = addValidation(center);
            Boolean gridPaneValidated = addValidation(gridPane);
            if (mainValidated && gridPaneValidated) {
                Label label = new Label("Saved");
                VBox vBox = new VBox(label);
                main.setCenter(vBox);
            };
        });
    }
}
