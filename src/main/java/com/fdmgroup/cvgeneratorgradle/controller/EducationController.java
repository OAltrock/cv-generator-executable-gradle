package com.fdmgroup.cvgeneratorgradle.controller;


import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasDateValidation;
import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Education;


import com.fdmgroup.cvgeneratorgradle.views.EducationPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    CVTemplate cvTemplate;

    private ObservableList<TextInputControl> textFields;
    /**
     * emulating variable loaded from template
     */
    private final String forFutureReference = "3";
    Predicate<String> predicate = input -> !input.matches("^.*[a-zA-Z]+.*$");

    //ToDo: write input to education object

    /**
     *
     * @param cvTemplate {@link CVTemplate}
     */
    public EducationController(CVTemplate cvTemplate) {
        this.cvTemplate=cvTemplate;
        educations = cvTemplate.getEducations();
    }



    @Override
    public void initialize(BorderPane main, String resource) {
        InitializableFXML.super.initialize(main, resource);
        ScrollPane center = (ScrollPane) main.getCenter();
        CheckBox checkBox;
        VBox centerBox;
        DatePicker start;
        DatePicker end;
        Button saveBtn;

        textFields = FXCollections.observableArrayList();
        textFields.forEach(System.out::println);
        if (educations!=null) {
            EducationPage educationPage = new EducationPage(educations.getLast(),forFutureReference, textFields);
            main.setCenter(educationPage.createCenterPage());
            centerBox = educationPage.getCenterBox();
            checkBox = educationPage.getOngoing();
            start=educationPage.getStartDate();
            end=educationPage.getEndDate();
            saveBtn = educationPage.getSaveBtn();
        }
        else {
            centerBox = (VBox) center.getContent();
            createKeyModulesArea(centerBox);
            Label hintLabel = (Label) centerBox.lookup("#hintLabel");
            hintLabel.setText("Add " + forFutureReference + " key modules");
            start = (DatePicker) centerBox.lookup("#start");
            end = (DatePicker) centerBox.lookup("#end");
            checkBox = (CheckBox) centerBox.lookup("#ongoing");
            saveBtn = (Button) center.getContent().lookup("#saveBtn");
        }


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
        saveBtn.setOnAction(actionEvent -> {
            assignEducationInput(start, end);
        });

    }

    private void createKeyModulesArea(VBox centerBox) {
        TextField textField = (TextField) centerBox.lookup("#keyModule");
        textFields.add(textField);
        GridPane gridPane = (GridPane) centerBox.lookup("#keyModules");
        javafx.scene.control.Button addModuleButton = (javafx.scene.control.Button) centerBox.lookup("#0");
        addAddButtons(gridPane,textFields, addModuleButton, "Remove Key Module", "Key Module", predicate, forFutureReference);
    }

    private void assignEducationInput(DatePicker start, DatePicker end){

        if (educations==null) educations = new ArrayList<>();
        if (educations.isEmpty()) educations.add(new Education());

        educations.getLast().setKeyModules(new ArrayList<>());

        textFields.forEach(textInputControl -> {
            switch (textInputControl.getId()) {
                case "degree" -> {
                    educations.getLast().setDegree(textInputControl.getText());
                }
                case "studyTitle" -> {
                    educations.getLast().setStudyTitle(textInputControl.getText());
                }
                case "universityName" -> {
                    educations.getLast().setUniversityName(textInputControl.getText());
                }
                case "universityPlace" -> {
                    educations.getLast().setUniversityPlace(textInputControl.getText());
                }
                case "thesisTitle" -> {
                    educations.getLast().setThesisTitle(textInputControl.getText());
                }
                default -> {
                    educations.getLast().getKeyModules().add(textInputControl.getText());
                }
            }
        });
        educations.getLast().setStartDate(start.getValue().toString());
        educations.getLast().setEndDate((end.getValue()!=null) ? end.getValue().toString() : LocalDate.now().plusMonths(1).toString());

        cvTemplate.setEducations(educations);
        System.out.println(educations);

    }

}
