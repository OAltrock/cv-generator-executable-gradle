package com.fdmgroup.cvgeneratorgradle.controller;


import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasDateValidation;
import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Experience;
import com.fdmgroup.cvgeneratorgradle.views.ExperiencePage;
import com.fdmgroup.cvgeneratorgradle.views.FDMButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.fdmgroup.cvgeneratorgradle.controller.AppUtils.findAllTextFields;

public class ExperienceController implements InitializableFXML, HasToggleableSaveButtons, HasAddableTextFields, HasDateValidation {

    private final CVTemplate cvTemplate;
    private List<Experience> experiences;
    private ExperiencePage experiencePage;
    private ObservableList<TextInputControl> textFields;
    Predicate<String> predicate = input -> !input.matches("^.*[a-zA-Z]+.*$");
    private final String forFutureReference = "3";
    TreeView<String> treeView;

    public ExperienceController(CVTemplate cvTemplate, TreeView<String> treeView) {
        this.cvTemplate = cvTemplate;
        experiences = cvTemplate.getExperiences();
        this.treeView = treeView;
    }

    @Override
    public void initialize(BorderPane main, String resource) {

        textFields = FXCollections.observableArrayList();
        GridPane gridPane;
        if (experiences!=null  && !experiences.isEmpty()) {
            experiencePage = new ExperiencePage(experiences.getLast(),textFields,forFutureReference);
        }
        else {
            experiencePage = new ExperiencePage(textFields,forFutureReference);
        }

        main.setCenter(experiencePage.createCenterPage(experiencePage.getCenterBox()));
        VBox centerBox = experiencePage.getCenterBox();
        gridPane = experiencePage.getKeySkillsGridPane();
        CheckBox checkBox = experiencePage.getOngoing();
        DatePicker start = experiencePage.getStartDate();
        DatePicker end = experiencePage.getEndDate();
        Button[] buttons = new Button[]{experiencePage.getPrevBtn(),experiencePage.getNextBtn()};

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
        addValidationToSaveButtons(textFields, predicate, start, end,checkDate, checkBox, buttons);
        textFields.addAll(findAllTextFields(centerBox));

        createValidationForTextFields(predicate, textFields, "Must contain at least one Letter");
        addValidationToDates(start,end,checkDate, checkBox);


        buttons[1].setOnAction(actionEvent -> {
            assignExperienceInput(start, end);
            treeView.getSelectionModel().select(4);
            new EducationController(cvTemplate, treeView).initialize(main, "");
        });

        buttons[0].setOnAction(actionEvent -> {
            assignExperienceInput(start,end);
            treeView.getSelectionModel().select(2);
            new PersonalInformationController(cvTemplate, treeView).initialize(main,"");
        });

    }

    private void assignExperienceInput(DatePicker start, DatePicker end){
        if (experiences==null) experiences = new ArrayList<>();
        if (experiences.isEmpty()) experiences.add(new Experience());
        Experience experience = experiences.getLast();
        experience.setCompanyName(experiencePage.getCompanyName().getText());
        experience.setJobTitle(experiencePage.getJobTitle().getText());
        experience.setCompanyPlace(experiencePage.getCompanyPlace().getText());
        experience.setPositionFeatures(experiencePage.getKeySkills().stream().map(TextInputControl::getText).toList());
        experiences.getLast().setStartDate(start.getValue().toString());
        experiences.getLast().setEndDate((end.getValue()!=null) ? end.getValue().toString() : LocalDate.now().plusMonths(1).toString());
        cvTemplate.setExperiences(experiences);
    }
}
