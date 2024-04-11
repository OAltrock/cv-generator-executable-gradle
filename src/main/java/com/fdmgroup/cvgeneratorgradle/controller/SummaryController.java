package com.fdmgroup.cvgeneratorgradle.controller;

import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class SummaryController implements Initialization{
    @Override
    public void initialize(BorderPane main, String resource) {
        Initialization.super.initialize(main, resource);
        VBox center = (VBox) main.getCenter();

        GridPane gridPaneSummary = (GridPane) center.getChildren().get(8);
        ListView<String> competencesListView = (ListView) gridPaneSummary.getChildren();
        competencesListView.getItems().addAll("- Java", "- HTML/ CSS/ JavaScript", "- Junit", "- Eclipse", "- Maven","- Git", "- MySQL", "- UML");

    }
}
