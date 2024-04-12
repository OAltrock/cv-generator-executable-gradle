package com.fdmgroup.cvgeneratorgradle.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ExperienceController implements Initialization{

    private ObservableList<TextField> textFields;

    @Override
    public void initialize(BorderPane main, String resource) {
        Initialization.super.initialize(main, resource);

        Button saveBtn = (Button) main.getCenter().lookup("#saveBtn");
        addAddButtons(main, saveBtn, textFields);
        VBox center = (VBox) main.getCenter();
        List<Node> uncheckedTextFields = new ArrayList<>(center.getChildren().stream().filter(child -> child.getClass().toString().contains("TextField")).toList());
        List<TextField> castTextFields = uncheckedTextFields.stream().map(textField -> (TextField) textField).toList();
        textFields = FXCollections.observableArrayList();
        GridPane gridPane = (GridPane) center.getChildren().stream().filter(child -> child.getClass().toString().contains("GridPane")).toList().getFirst();
        Predicate<String> predicate = input -> !input.matches("[a-zA-Z]+");
        addValidationToSaveButtons(textFields, predicate, saveBtn);
        textFields.addAll(castTextFields);
    }
}
