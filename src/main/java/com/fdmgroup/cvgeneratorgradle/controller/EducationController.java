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
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.fdmgroup.cvgeneratorgradle.controller.AppUtils.findAllTextFields;

public class EducationController implements InitializableFXML, HasToggleableSaveButtons, HasAddableTextFields, HasDateValidation {


    @Setter
    @Getter
    private List<Education> educations;

    CVTemplate cvTemplate;

    EducationPage educationPage;

    private ObservableList<TextInputControl> textFields;
    /**
     * emulating variable loaded from template
     */
    private final Stage stage;
    Predicate<String> predicate = input -> !input.matches("^.*[a-zA-Z]+.*$");
    TreeView<String> treeView;

    /**
     * @param cvTemplate {@link CVTemplate}
     */
    public EducationController(CVTemplate cvTemplate, TreeView<String> treeView, Stage stage) {
        this.cvTemplate = cvTemplate;
        educations = cvTemplate.getEducations();
        this.treeView = treeView;
        this.stage = stage;
    }


    @Override
    public void initialize(BorderPane main, String resource) {
        //InitializableFXML.super.initialize(main, resource);
        CheckBox checkBox;
        VBox centerBox;
        DatePicker start;
        DatePicker end;

        textFields = FXCollections.observableArrayList();
        educationPage = new EducationPage(cvTemplate, textFields);
        /*if (educations != null) {

        } else {
            educationPage = new EducationPage(textFields, forFutureReference);
        }*/
        Button[] buttons = new Button[]{educationPage.getPrevBtn(), educationPage.getNextBtn()};
        main.setCenter(educationPage.createCenterPage(educationPage.getCenterBox()));
        centerBox = educationPage.getCenterBox();
        start = educationPage.getStartDate();
        end = educationPage.getEndDate();
        checkBox = educationPage.getOngoing();
        BiPredicate<LocalDate, LocalDate> checkDate = (startDate, endDate) -> {
            if (startDate == null) {
                return false;
            } else if (checkBox.isSelected()) {
                return !startDate.isAfter(LocalDate.now());
            } else if (endDate == null) return false;
            else {
                return startDate.isBefore(endDate) || startDate.isEqual(endDate);
            }
        };

        addValidationToSaveButtons(textFields, predicate, start, end, checkDate, checkBox, buttons);

        textFields.addAll(findAllTextFields(centerBox));
        textFields.addAll(findAllTextFields(educationPage.getKeyModuleGridPane()));

        createValidationForTextFields(predicate, textFields, "Must contain at least one letter");
        addValidationToDates(start, end, checkDate, checkBox);
        buttons[0].setOnAction(actionEvent -> {
            assignEducationInput(start, end);
            treeView.getSelectionModel().select(3);
            new ExperienceController(cvTemplate, treeView, stage).initialize(main, "");
        });
        buttons[1].setOnAction(actionEvent -> {
            assignEducationInput(start, end);
            treeView.getSelectionModel().select(5);
            new SkillsController(cvTemplate, treeView, stage).initialize(main, "summary");
        });

    }

    private void assignEducationInput(DatePicker start, DatePicker end) {

        if (educations == null) educations = new ArrayList<>();
        if (educations.isEmpty()) educations.add(new Education());

        Education education = educations.getLast();
        education.setDegree(educationPage.getDegree().getText());
        education.setStudyTitle(educationPage.getStudyTitle().getText());
        education.setUniversityName(educationPage.getUniversityName().getText());
        education.setUniversityPlace(educationPage.getUniversityPlace().getText());
        education.setThesisTitle(educationPage.getThesisTitle().getText());
        education.setKeyModules(educationPage.getKeyModules().stream().map(TextInputControl::getText).toList());

        educations.getLast().setStartDate(start.getValue().toString());
        educations.getLast().setEndDate((end.getValue() != null) ? end.getValue().toString() : LocalDate.now().plusMonths(1).toString());

        cvTemplate.setEducations(educations);
        System.out.println(educations);

    }

}
