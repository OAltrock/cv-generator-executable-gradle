package com.fdmgroup.cvgeneratorgradle.controller;


import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasDateValidation;
import com.fdmgroup.cvgeneratorgradle.interfaces.InitializableFXML;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Experience;
import com.fdmgroup.cvgeneratorgradle.views.*;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
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
    TreeView<String> treeView;
    private final ObservableList<ExperiencePage> experiencePages;

    public ExperienceController(CVTemplate cvTemplate, TreeView<String> treeView) {
        this.cvTemplate = cvTemplate;
        experiences = cvTemplate.getExperiences();
        this.treeView = treeView;
        experiencePages = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(BorderPane main, String resource) {

        textFields = FXCollections.observableArrayList();
        FDMCenterVBoxWrapper wrapper = new FDMCenterVBoxWrapper();
        //experiencePage = new ExperiencePage(cvTemplate, textFields);


        if (experiences != null && !experiences.isEmpty()) {
            experiences.forEach(experience -> {
                experiencePages.add(new ExperiencePage(cvTemplate, textFields, experience, false));
            });
        } else {
            experiencePages.add(new ExperiencePage(cvTemplate, textFields, new Experience("",
                    "", "", new ArrayList<>(), "",
                    "", ""), true));
            cvTemplate.setExperiences(new ArrayList<>());
        }

        Button[] buttons = new Button[]{new FDMButton("Previous"), new FDMButton("Next"), new FDMButton("Add Experience")};
        final FDMHBox[] buttonWrapper = {new FDMHBox(buttons[0], buttons[2], buttons[1])};
        buttonWrapper[0].setDesign();

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
            experiencePage1.getRemovePage().setOnAction(action -> {
                FDMButton btn = (FDMButton) action.getSource();
                FDMCenterVBoxWrapper parent = (FDMCenterVBoxWrapper) btn.getParent();
                textFields.removeAll(findAllTextFields(experiencePage1.getCenterBox()));
                textFields.removeAll(findAllTextFields(experiencePage1.getKeySkillsGridPane()));
                wrapper.getChildren().remove(parent);
                ExperiencePage lastPage = (experiencePages.indexOf(experiencePage1)==0) ? experiencePages.get(experiencePages.indexOf(experiencePage1)+1) : experiencePages.get(experiencePages.indexOf(experiencePage1)-1);
                experiencePages.remove(experiencePage1);
                lastPage.getOngoing().setSelected(!lastPage.getOngoing().isSelected());
                lastPage.getOngoing().setSelected(!lastPage.getOngoing().isSelected());
                wrapper.getChildren().remove(parent);
            });
            addValidationToSaveButtons(textFields, predicate, start, end, checkDate, checkBox, buttons);
            textFields.addAll(findAllTextFields(centerBox));
            textFields.addAll(findAllTextFields(experiencePage1.getKeySkillsGridPane()));
            createValidationForTextFields(predicate, textFields, "Must contain at least one Letter");
            addValidationToDates(start, end, checkDate, checkBox);
            experiencePage1.getRemovePage().setDisable(true);
            experiencePages.addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    BooleanBinding moreThanOne = Bindings.createBooleanBinding(() -> experiencePages.size() <= 1);
                    experiencePage1.getRemovePage().disableProperty().bind(moreThanOne);
                }
            });
        });
        wrapper.getChildren().addLast(buttonWrapper[0]);
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

        buttons[2].setOnAction(actionEvent -> {
            Experience newExperience = new Experience("", "", "",
                    new ArrayList<>(), "", "", "");
            ExperiencePage page = new ExperiencePage(cvTemplate, textFields,
                    newExperience, false);
            experiencePages.add(page);
            wrapper.getChildren().add(page.getCenterBox());
            wrapper.getChildren().remove(buttonWrapper[0]);
            wrapper.getChildren().add(buttonWrapper[0]);

            BiPredicate<LocalDate, LocalDate> checkDate = (startDate, endDate) -> {
                if (startDate == null) {
                    return false;
                } else if (page.getOngoing().isSelected()) {
                    return !startDate.isAfter(LocalDate.now());
                } else if (endDate == null) return false;
                else {
                    return startDate.isBefore(endDate) || startDate.isEqual(endDate);
                }
            };
            page.getRemovePage().setOnAction(action -> {
                FDMButton btn = (FDMButton) action.getSource();
                FDMCenterVBoxWrapper parent = (FDMCenterVBoxWrapper) btn.getParent();
                textFields.removeAll(findAllTextFields(page.getCenterBox()));
                textFields.removeAll(findAllTextFields(page.getKeySkillsGridPane()));
                wrapper.getChildren().remove(parent);
                System.out.println(experiencePages);
                ExperiencePage lastPage = (experiencePages.indexOf(page)==0) ? experiencePages.get(experiencePages.indexOf(page)+1) : experiencePages.get(experiencePages.indexOf(page)-1);
                experiencePages.remove(page);
                System.out.println(lastPage);
                lastPage.getOngoing().setSelected(!lastPage.getOngoing().isSelected());
                lastPage.getOngoing().setSelected(!lastPage.getOngoing().isSelected());
            });
            addValidationToSaveButtons(textFields, predicate, page.getStartDate(), page.getEndDate(),
                    checkDate, page.getOngoing(), buttons);
            textFields.addAll(findAllTextFields(page.getCenterBox()));
            textFields.addAll(findAllTextFields(page.getKeySkillsGridPane()));
            createValidationForTextFields(predicate, textFields, "Must contain at least one Letter");
            addValidationToDates(page.getStartDate(), page.getEndDate(), checkDate, page.getOngoing());
            experiencePages.addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    BooleanBinding moreThanOne = Bindings.createBooleanBinding(() -> experiencePages.size() <= 1);
                    page.getRemovePage().disableProperty().bind(moreThanOne);
                }
            });
        });

    }

    private void assignExperienceInput(List<ExperiencePage> experiencePages) {
        if (experiences == null) experiences = new ArrayList<>();
        //experiences.add(new Experience());
        List<Experience> experienceList = new ArrayList<>();
        for (ExperiencePage page : experiencePages) {
            List<String> keySkills = new ArrayList<>();
            for (TextInputControl keySkill : page.getKeySkills()) {
                keySkills.add(keySkill.getText());
            }
            //(end.getValue() != null) ? end.getValue().toString() : LocalDate.now().plusMonths(1).toString()
            experienceList.add(new Experience(page.getJobTitle().getText(), page.getStartDate().getValue().toString(),
                    (page.getEndDate().getValue() != null) ?
                            page.getEndDate().getValue().toString() :
                            LocalDate.now().plusMonths(1).toString(),
                    keySkills, page.getCompanyName().getText(),
                    page.getCompanyPlace().getText(), page.getDescription().getText()));
        }
        cvTemplate.setExperiences(experienceList);
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
