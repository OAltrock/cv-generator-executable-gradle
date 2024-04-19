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

    public ExperienceController(CVTemplate cvTemplate) {
        this.cvTemplate = cvTemplate;
        experiences = cvTemplate.getExperiences();
    }

    @Override
    public void initialize(BorderPane main, String resource) {


        /*InitializableFXML.super.initialize(main, resource);
        ScrollPane scrollPane = (ScrollPane) main.getCenter();
        VBox centerBox = (VBox) scrollPane.getContent();
        Button saveBtn = (Button) centerBox.lookup("#saveBtn");

        List<Node> uncheckedTextFields = new ArrayList<>(centerBox.getChildren().stream().filter(child -> child.getClass().toString().contains("TextField")).toList());
        List<TextField> castTextFields = uncheckedTextFields.stream().map(textField -> (TextField) textField).toList();*/
        textFields = FXCollections.observableArrayList();
        GridPane gridPane;
        if (experiences!=null  && !experiences.isEmpty()) {
            experiencePage = new ExperiencePage(experiences.getLast(),textFields,forFutureReference);
            //gridPane = experiencePage.getKeySkillsGridPane();
        }
        else {
            experiencePage = new ExperiencePage(textFields,forFutureReference);

            //createKeySkillsArea(gridPane);
        }

        main.setCenter(experiencePage.createCenterPage(experiencePage.getCenterBox()));
        VBox centerBox = experiencePage.getCenterBox();
        gridPane = experiencePage.getKeySkillsGridPane();
        //GridPane gridPane = (GridPane) centerBox.getChildren().stream().filter(child -> child.getClass().toString().contains("GridPane")).toList().getFirst();
        //createKeySkillsArea(gridPane);
        CheckBox checkBox = experiencePage.getOngoing();
        DatePicker start = experiencePage.getStartDate();
        DatePicker end = experiencePage.getEndDate();
        FDMButton saveBtn = experiencePage.getSaveBtn();
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
        textFields.addAll(findAllTextFields(centerBox));

        createValidationForTextFields(predicate, textFields, "Must contain at least one Letter");
        addValidationToDates(start,end,checkDate, checkBox);

        saveBtn.setOnAction(actionEvent -> {
            assignExperienceInput(start, end);
        });

    }

    private void assignExperienceInput(DatePicker start, DatePicker end){
        if (experiences==null) experiences = new ArrayList<>();
        if (experiences.isEmpty()) experiences.add(new Experience());
        Experience experience = experiences.getLast();
        //ToDo: rest of textFields (use page fields)
        experience.setCompanyName(experiencePage.getCompanyName().getText());
        experience.setJobTitle(experiencePage.getJobTitle().getText());
        experience.setCompanyPlace(experiencePage.getCompanyPlace().getText());
        experience.setPositionFeatures(experiencePage.getKeySkills().stream().map(TextInputControl::getText).toList());
        experiences.getLast().setStartDate(start.getValue().toString());
        experiences.getLast().setEndDate((end.getValue()!=null) ? end.getValue().toString() : LocalDate.now().plusMonths(1).toString());
        cvTemplate.setExperiences(experiences);
    }
}
