package com.fdmgroup.cvgeneratorgradle.controller;


import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasDateValidation;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Education;


import com.fdmgroup.cvgeneratorgradle.views.EducationPage;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.fdmgroup.cvgeneratorgradle.controller.AppUtils.findAllTextFields;
import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.saveObjectAsJson;

public class EducationController extends FDMController implements HasToggleableSaveButtons, HasAddableTextFields, HasDateValidation {


    @Setter
    @Getter
    private List<Education> educations;

    EducationPage educationPage;

    private DatePicker start;
    private DatePicker end;

    Predicate<String> predicate = input -> !input.matches("^.*[a-zA-Z]+.*$");


    /**
     * @param cvTemplate {@link CVTemplate} main class to store and create the cv
     * @param treeView {@link TreeView} contains app navigation. needed to set selected to current page
     * @param stage {@link Stage} of the app
     */
    public EducationController(CVTemplate cvTemplate, TreeView<String> treeView, Stage stage, Menu recent) {
        this.cvTemplate = cvTemplate;
        educations = cvTemplate.getEducations();
        this.treeView = treeView;
        this.stage = stage;
        this.recent = recent;
    }

    /**
     * Creates the education page and reads out user input (calls "assignEducationInput()")
     * Also handles validation for the page
     * @param main main {@link BorderPane} that contains all views of the app
     */
    public void initialize(BorderPane main, MainController mainController) {
        CheckBox checkBox;
        VBox centerBox;

        //validation is tied to changes in the list of textFields (eg: refresh validation
        //when a textField is added via button)
        ObservableList<TextInputControl> textFields = FXCollections.observableArrayList();
        educationPage = new EducationPage(cvTemplate, textFields);
        educationPage.getCenterBox().setOnMouseExited(event->{
            assignToModel();
        });

        Button[] buttons = new Button[]{educationPage.getNext()};
        main.setCenter(educationPage.createCenterPage(educationPage.getCenterBox()));
        centerBox = educationPage.getCenterBox();
        start = educationPage.getStartDate();
        end = educationPage.getEndDate();
        List<DatePicker> datePickers = List.of(start,end);
        datePickers.forEach(datePicker -> datePicker.setOnMouseExited(event -> assignToModel()));

        checkBox = educationPage.getOngoing();

        //predicate to check if start date is before now and start date is before
        //end date
        BiPredicate<LocalDate, LocalDate> checkDate = (startDate, endDate) -> {
            if (startDate == null) {
                return false;
            } else if (checkBox.isSelected()) {
                return !startDate.isAfter(LocalDate.now());
            } else if (endDate == null) return false;
            else {
                return (startDate.isBefore(endDate) || startDate.isEqual(endDate)) && startDate.isBefore(LocalDate.now());
            }
        };
        textFields.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                textFields.forEach(textInputControl -> textInputControl.setOnMouseClicked(actionEvent ->
                        assignInput(mainController)));
            }
        });

        addValidationToSaveButtons(textFields, predicate, start, end, checkDate, checkBox, buttons);

        textFields.addAll(findAllTextFields(centerBox));
        textFields.addAll(findAllTextFields(educationPage.getKeyModuleGridPane()));

        createValidationForTextFields(predicate, textFields, "Must contain at least one letter");
        addValidationToDates(start, end, checkDate, checkBox);
        educationPage.getPrev().setOnAction(actionEvent -> {
            assignInput(mainController);
            treeView.getSelectionModel().select(3);
            new ExperienceController(cvTemplate, treeView, stage, recent).initialize(main, mainController);
        });
        buttons[0].setOnAction(actionEvent -> {
            assignInput(mainController);
            treeView.getSelectionModel().select(5);
            new SkillsController(cvTemplate, treeView, stage, recent).initialize(main, mainController);
        });
    }

    /**
     * Reads out the elements of the page
     * @param mainController {@link MainController} passed down to be able to call its methods
     */
    void assignInput(MainController mainController) {

        assignToModel();
        saveObjectAsJson(cvTemplate, recent);
        try {
            mainController.loadRecentCV(stage);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void assignToModel() {
        if (educations == null) educations = new ArrayList<>();
        if (educations.isEmpty()) educations.add(new Education());

        //only the last education is considered. this is a simplification due to only
        //one is needed in the foreseeable future. if this changes see ExperienceController
        Education education = educations.getLast();

        education.setDegree(educationPage.getDegree().getText());
        education.setStudyTitle(educationPage.getStudyTitle().getText());
        education.setUniversityName(educationPage.getUniversityName().getText());
        education.setUniversityPlace(educationPage.getUniversityPlace().getText());
        education.setThesisTitle(educationPage.getThesisTitle().getText());
        education.setKeyModules(educationPage.getKeyModules().stream().map(TextInputControl::getText).toList());

        educations.getLast().setStartDate((start.getValue()!=null) ? start.getValue().toString(): "");

        //if end is null (ie is not being picked due to ongoing is selected, a date in the future
        //is chosen. when reading out cvTemplate, the end date picker will be automatically disabled if the date
        //is in the future and ongoing will be selected (in that case the summary page lists the end date
        //as "ongoing"
        //System.out.println(end.getValue().toString());
        //if (educationPage.getEndDate().getValue()==null) educationPage.getEndDate().setValue(LocalDate.parse("9999-01-01"));
        educations.getLast().setEndDate((educationPage.getEndDate().getValue()!=null && !educationPage.getOngoing().isSelected()) ? (LocalDate.parse(end.getValue().toString()).isAfter(LocalDate.now())) ?
                        "9999-01-01" : end.getValue().toString()
                 : "9999-01-01");

        cvTemplate.setEducations(educations);
    }

}
