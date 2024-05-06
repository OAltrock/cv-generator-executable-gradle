package com.fdmgroup.cvgeneratorgradle.controller;


import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasDateValidation;
import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Experience;
import com.fdmgroup.cvgeneratorgradle.views.*;
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

    private List<ExperiencePage> experiencePages;

    public ExperienceController(CVTemplate cvTemplate, TreeView<String> treeView) {
        this.cvTemplate = cvTemplate;
        experiences = cvTemplate.getExperiences();
        this.treeView = treeView;
        experiencePages = new ArrayList<>();
    }

    @Override
    public void initialize(BorderPane main, String resource) {

        textFields = FXCollections.observableArrayList();
        FDMCenterVBoxWrapper wrapper = new FDMCenterVBoxWrapper();
        //experiencePage = new ExperiencePage(cvTemplate, textFields);
        if (experiences != null && !experiences.isEmpty()) {
            experiences.forEach(experience -> {
                experiencePages.add(new ExperiencePage(cvTemplate, textFields, experience));
            });
        } else
            experiencePages.add(new ExperiencePage(cvTemplate, textFields, new Experience("", "", "", new ArrayList<>(), "", "", "")));

        Button[] buttons = new Button[]{new FDMButton("Previous"), new FDMButton("Next")};
        FDMHBox buttonWrapper = new FDMHBox(buttons[0],buttons[1]);
        buttonWrapper.setDesign();

        experiencePages.forEach(experiencePage1 -> {
            wrapper.getChildren().add(experiencePage1.getCenterBox());
            CheckBox checkBox = experiencePage1.getOngoing();
            DatePicker start = experiencePage1.getStartDate();
            DatePicker end = experiencePage1.getEndDate();

            VBox centerBox = experiencePage1.getCenterBox();
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
            textFields.addAll(findAllTextFields(experiencePage1.getKeySkillsGridPane()));
            createValidationForTextFields(predicate, textFields, "Must contain at least one Letter");
            addValidationToDates(start, end, checkDate, checkBox);

        });
        wrapper.getChildren().addLast(buttonWrapper);
        main.setCenter(experiencePages.getLast().createCenterPage(wrapper));


        //GridPane gridPane = experiencePage.getKeySkillsGridPane();


        buttons[1].setOnAction(actionEvent -> {
            assignExperienceInput(experiencePages);
            treeView.getSelectionModel().select(4);
            new EducationController(cvTemplate, treeView).initialize(main, "");
        });

        buttons[0].setOnAction(actionEvent -> {
            assignExperienceInput(experiencePages);
            treeView.getSelectionModel().select(2);
            new PersonalInformationController(cvTemplate, treeView).initialize(main, "");
        });

    }

    private void assignExperienceInput(List<ExperiencePage> experiencePages) {
        if (experiences == null) experiences = new ArrayList<>();
        //experiences.add(new Experience());
        //List<Experience> experienceList = new ArrayList<>();
        for (ExperiencePage page : experiencePages) {
            List<String> keySkills = new ArrayList<>();
            for (TextInputControl keySkill : page.getKeySkills()) {
                keySkills.add(keySkill.getText());
            }
            //(end.getValue() != null) ? end.getValue().toString() : LocalDate.now().plusMonths(1).toString()
            cvTemplate.getExperiences().add(new Experience(page.getJobTitle().getText(), page.getStartDate().getValue().toString(),
                    (page.getEndDate().getValue() != null) ?
                            page.getEndDate().getValue().toString() :
                            LocalDate.now().plusMonths(1).toString(),
                    keySkills, page.getCompanyName().getText(),
                    page.getCompanyPlace().getText(), page.getDescription().getText()));
            //System.out.println(experienceList.getLast());
            System.out.println(cvTemplate.getExperiences());
        }
        /*Experience experience = experiences.getLast();
        experience.setCompanyName(experiencePage.getCompanyName().getText());
        experience.setJobTitle(experiencePage.getJobTitle().getText());
        experience.setCompanyPlace(experiencePage.getCompanyPlace().getText());
        experience.setPositionFeatures(experiencePage.getKeySkills().stream().map(TextInputControl::getText).toList());
        experiences.getLast().setStartDate(start.getValue().toString());
        experiences.getLast().setEndDate();
        System.out.println(experiences);*/
        //cvTemplate.setExperiences(experienceList);
    }
}
