package com.fdmgroup.cvgeneratorgradle.controller;


import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasDateValidation;
import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.models.Education;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.fdmgroup.cvgeneratorgradle.controller.SceneSearchUtil.findAllTextFields;

public class EducationController implements InitializableFXML, HasToggleableSaveButtons, HasAddableTextFields, HasDateValidation {


    @Setter
    @Getter
    private List<Education> educations;
    private ObservableList<TextInputControl> textFields;
    Predicate<String> predicate = input -> !input.matches("[a-zA-Z]+");
    @Setter
    @Getter
    private int counter=0;

    //ToDo: write input to education object
    public EducationController(List<Education> educations) {
        this.educations = educations;
    }

    @Override
    public void initialize(BorderPane main, String resource) {
        InitializableFXML.super.initialize(main, resource);
        ScrollPane center = (ScrollPane) main.getCenter();

        Button saveBtn = (Button) center.getContent().lookup("#saveBtn");

        textFields = FXCollections.observableArrayList();



        VBox centerBox = (VBox) center.getContent();
        createKeyModulesArea(centerBox);



        DatePicker start = (DatePicker) centerBox.lookup("#start");
        DatePicker end = (DatePicker) centerBox.lookup("#end");
        CheckBox checkBox = (CheckBox) centerBox.lookup("#ongoing");
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

        textFields.addAll(findAllTextFields(centerBox));
        createValidationForTextFields(predicate, textFields, "Must contain at least one letter");
        addValidationToDates(start, end,checkDate,checkBox);
        saveBtn.setOnAction(actionEvent -> new ExperienceController().initialize(main, "experience2"));

    }

    private void createKeyModulesArea(VBox centerBox) {
        TextField textField = (TextField) centerBox.lookup("#keyModule");
        textFields.add(textField);
        GridPane gridPane = (GridPane) centerBox.lookup("#keyModules");
        javafx.scene.control.Button addModuleButton = (javafx.scene.control.Button) centerBox.lookup("#0");
        addAddButtons(gridPane,textFields, addModuleButton, "Remove Key Module", "Key Module", predicate);
    }

}
