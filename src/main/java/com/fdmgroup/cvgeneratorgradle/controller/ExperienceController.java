package com.fdmgroup.cvgeneratorgradle.controller;


import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasDateValidation;
import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ExperienceController implements InitializableFXML, HasToggleableSaveButtons, HasAddableTextFields, HasDateValidation {

    private ObservableList<TextInputControl> textFields;
    Predicate<String> predicate = input -> !input.matches("[a-zA-Z]+");

    @Override
    public void initialize(BorderPane main, String resource) {
        InitializableFXML.super.initialize(main, resource);
        ScrollPane scrollPane = (ScrollPane) main.getCenter();
        VBox centerBox = (VBox) scrollPane.getContent();
        Button saveBtn = (Button) centerBox.lookup("#saveBtn");

        List<Node> uncheckedTextFields = new ArrayList<>(centerBox.getChildren().stream().filter(child -> child.getClass().toString().contains("TextField")).toList());
        List<TextField> castTextFields = uncheckedTextFields.stream().map(textField -> (TextField) textField).toList();
        textFields = FXCollections.observableArrayList();


        GridPane gridPane = (GridPane) centerBox.getChildren().stream().filter(child -> child.getClass().toString().contains("GridPane")).toList().getFirst();
        createKeySkillsArea(gridPane);
        CheckBox checkBox = (CheckBox) centerBox.lookup("#ongoing");
        DatePicker start = (DatePicker) centerBox.lookup("#start");
        DatePicker end = (DatePicker) centerBox.lookup("#end");
        BiPredicate<LocalDate, LocalDate> checkDate = (startDate, endDate) -> {
            if (startDate==null) {
                return false;
            }
            else if (checkBox.isSelected()) {
                return !startDate.isAfter(LocalDate.now());
            }
            else if (endDate==null) return false;
            else  {
                return startDate.isBefore(endDate) || startDate.isEqual(endDate);
            }
        };
        addValidationToSaveButtons(textFields, predicate, saveBtn, start, end,checkDate, checkBox);

        textFields.addAll(castTextFields);
        createValidationForTextFields(predicate, textFields, "Must contain at least one Letter");
        addValidationToDates(start,end,checkDate, checkBox);
    }

    private void createKeySkillsArea(GridPane gridPane) {
        Button addBtn = (Button) gridPane.lookup("#0");
        TextField textField = (TextField) gridPane.lookup("#keySkill");
        textFields.add(textField);
        addAddButtons(gridPane, textFields, addBtn, "Remove Position", "Position", predicate);
    }
}
