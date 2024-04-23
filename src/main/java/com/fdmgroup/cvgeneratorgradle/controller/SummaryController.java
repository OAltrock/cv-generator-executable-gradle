package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class SummaryController implements InitializableFXML {
    CVTemplate cvTemplate;
    public SummaryController(CVTemplate cvTemplate) {
        this.cvTemplate = cvTemplate;
    }

    @Override
    public void initialize(BorderPane main, String resource) {
        InitializableFXML.super.initialize(main, resource);
        ScrollPane scrollBar = (ScrollPane) main.getCenter();

        VBox center = (VBox) scrollBar.getContent();
        GridPane gridPaneSummary = (GridPane) center.getChildren().get(8);

        ListView<String> competencesListView = (ListView) gridPaneSummary.lookup("#competencesListView");
        competencesListView.prefHeightProperty().bind(Bindings.size(competencesListView.getItems()).multiply(18));
        competencesListView.getItems().addAll("- Java", "- HTML/ CSS/ JavaScript", "- Junit", "- Eclipse", "- Maven","- Git", "- MySQL", "- UML");

    }
}
