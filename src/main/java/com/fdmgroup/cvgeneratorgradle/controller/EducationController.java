package com.fdmgroup.cvgeneratorgradle.controller;


import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasDateValidation;
import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.models.Education;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class EducationController implements InitializableFXML, HasToggleableSaveButtons, HasAddableTextFields, HasDateValidation {

    private BorderPane main;
    @Setter
    @Getter
    private Education education;
    private ObservableList<TextField> textFields;
    Predicate<String> predicate = input -> !input.matches("[a-zA-Z]+");
    @Setter
    @Getter
    private int counter=0;

    //ToDo: write input to education object
    public EducationController(Education education) {
        this.education = education;
    }

    @Override
    public void initialize(BorderPane main, String resource) {
        InitializableFXML.super.initialize(main, resource);
        this.main = main;
        Button saveBtn = (Button) main.getCenter().lookup("#saveBtn");
        textFields = FXCollections.observableArrayList();

        createKeyModulesArea();

        VBox center = (VBox) main.getCenter();
        List<Node> uncheckedTextFields = new ArrayList<>(center.getChildren().stream().filter(child -> child.getClass().toString().contains("TextField")).toList());
        List<TextField> castTextFields = uncheckedTextFields.stream().map(textField -> (TextField) textField).toList();

        DatePicker start = (DatePicker) center.lookup("#start");
        DatePicker end = (DatePicker) center.lookup("#end");
        CheckBox checkBox = (CheckBox) center.lookup("#ongoing");
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

        addValidationToSaveButtons(textFields, predicate, saveBtn, start,end,checkDate,checkBox);

        textFields.addAll(castTextFields);
        createValidationForTextFields(predicate, textFields, "Must contain at least one letter");
        addValidationToDates(start, end,checkDate,checkBox);

    }

    private void createKeyModulesArea() {
        TextField textField = (TextField) main.getCenter().lookup("#keyModule");
        textFields.add(textField);
        GridPane gridPane = (GridPane) main.getCenter().lookup("#keyModules");
        javafx.scene.control.Button addModuleButton = (javafx.scene.control.Button) main.getCenter().lookup("#0");
        addAddButtons(gridPane,textFields, addModuleButton, "Remove Key Module", "Key Module", predicate);
    }

}
