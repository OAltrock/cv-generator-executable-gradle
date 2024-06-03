package com.fdmgroup.cvgeneratorgradle.controller;


import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasDateValidation;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Experience;
import com.fdmgroup.cvgeneratorgradle.views.*;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;

import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.fdmgroup.cvgeneratorgradle.controller.AppUtils.findAllTextFields;
import static com.fdmgroup.cvgeneratorgradle.utils.SaveObjectToJson.saveObjectAsJson;

public class ExperienceController extends FDMController implements HasToggleableSaveButtons, HasAddableTextFields, HasDateValidation {

    private List<Experience> experiences;
    private ObservableList<TextInputControl> textFields;
    Predicate<String> predicate = input -> !input.matches("^.*[a-zA-Z]+.*$");
    TreeView<String> treeView;
    private final ObservableList<ExperiencePage> experiencePages;

    public ExperienceController(CVTemplate cvTemplate, TreeView<String> treeView, Stage stage, Menu recent) {
        this.stage = stage;
        this.cvTemplate = cvTemplate;
        experiences = cvTemplate.getExperiences();
        this.treeView = treeView;
        experiencePages = FXCollections.observableArrayList();
        this.recent = recent;
    }

    public void initialize(BorderPane main, MainController mainController) {

        textFields = FXCollections.observableArrayList();
        FDMCenterVBoxWrapper wrapper = new FDMCenterVBoxWrapper();

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


        Button[] buttons = new Button[]{new FDMButton("Next")};
        FDMButton prevBtn = new FDMButton("Previous");
        FDMButton addExpBtn = new FDMButton("Add Experience");
        ObservableBooleanValue canAdd = Bindings.createBooleanBinding(() -> experiencePages.size() >= cvTemplate.getLocation().getMaxExperience(), experiencePages);
        addExpBtn.disableProperty().bind(canAdd);

        ((FDMButton) buttons[0]).setDesign("primary");
        prevBtn.setDesign("primary");
        addExpBtn.setDesign("primary");
        final FDMHBox[] buttonWrapper = {new FDMHBox(prevBtn, addExpBtn, buttons[0])};
        buttonWrapper[0].setDesign();


        experiencePages.forEach(experiencePage1 -> {
            experiencePage1.getCenterBox().setOnMouseExited(event->{
                assignToModel();
            });
            experiencePage1.getPageTitle().setText("Experience " + (experiencePages.indexOf(experiencePage1) + 1));
            experiencePages.addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    BooleanBinding moreThanOne = Bindings.createBooleanBinding(() -> experiencePages.size() <= 1);
                    experiencePage1.getRemovePage().disableProperty().bind(moreThanOne);
                }
            });
            textFields.addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    textFields.forEach(textInputControl -> textInputControl.setOnMouseClicked(actionEvent ->
                            assignInput(mainController)));
                }
            });
            wrapper.getChildren().add(experiencePage1.getCenterBox());

            CheckBox checkBox = experiencePage1.getOngoing();
            DatePicker start = experiencePage1.getStartDate();
            DatePicker end = experiencePage1.getEndDate();
            List<DatePicker> datePickers = List.of(start,end);
            datePickers.forEach(datePicker -> datePicker.setOnMouseExited(event -> assignToModel()));

            VBox centerBox = experiencePage1.getCenterBox();
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
            experiencePage1.getRemovePage().setOnAction(action -> {
                FDMButton btn = (FDMButton) action.getSource();
                FDMCenterVBoxWrapper parent = (FDMCenterVBoxWrapper) btn.getParent();
                textFields.removeAll(findAllTextFields(experiencePage1.getCenterBox()));
                textFields.removeAll(findAllTextFields(experiencePage1.getKeySkillsGridPane()));
                wrapper.getChildren().remove(parent);
                ExperiencePage lastPage = (experiencePages.indexOf(experiencePage1) == 0) ? experiencePages.get(experiencePages.indexOf(experiencePage1) + 1) : experiencePages.get(experiencePages.indexOf(experiencePage1) - 1);
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

        });

        ExperiencePage temp = new ExperiencePage(cvTemplate, textFields,
                new Experience("", "", "",
                        new ArrayList<>(), "", "", ""),
                true);
        experiencePages.add(temp);
        experiencePages.remove(temp);
        wrapper.getChildren().addLast(buttonWrapper[0]);
        main.setCenter(experiencePages.getLast().createCenterPage(wrapper));

        buttons[0].setOnAction(actionEvent -> {
            assignInput(mainController);
            treeView.getSelectionModel().select(4);
            new EducationController(cvTemplate, treeView, stage, recent).initialize(main, mainController);
        });

        prevBtn.setOnAction(actionEvent -> {
            assignInput(mainController);
            treeView.getSelectionModel().select(2);
            new PersonalInformationController(cvTemplate, treeView, stage, recent).initialize(main, mainController);
        });

        addExpBtn.setOnAction(actionEvent -> {
            Experience newExperience = new Experience("", "", "",
                    new ArrayList<>(), "", "", "");
            ExperiencePage page = new ExperiencePage(cvTemplate, textFields,
                    newExperience, false);

            experiencePages.addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    BooleanBinding moreThanOne = Bindings.createBooleanBinding(() -> experiencePages.size() <= 1);
                    page.getRemovePage().disableProperty().bind(moreThanOne);
                }
            });
            experiencePages.add(page);
            page.getPageTitle().setText("Experience " + (experiencePages.indexOf(page) + 1));
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
                ExperiencePage lastPage = (experiencePages.indexOf(page) == 0) ? experiencePages.get(experiencePages.indexOf(page) + 1) : experiencePages.get(experiencePages.indexOf(page) - 1);
                experiencePages.remove(page);
                lastPage.getOngoing().setSelected(!lastPage.getOngoing().isSelected());
                lastPage.getOngoing().setSelected(!lastPage.getOngoing().isSelected());
            });

            addValidationToSaveButtons(textFields, predicate, page.getStartDate(), page.getEndDate(),
                    checkDate, page.getOngoing(), buttons);
            textFields.addAll(findAllTextFields(page.getCenterBox()));
            textFields.addAll(findAllTextFields(page.getKeySkillsGridPane()));
            createValidationForTextFields(predicate, textFields, "Must contain at least one Letter");
            addValidationToDates(page.getStartDate(), page.getEndDate(), checkDate, page.getOngoing());
        });
    }

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
        if (experiences == null) experiences = new ArrayList<>();
        List<Experience> experienceList = new ArrayList<>();
        for (ExperiencePage page : experiencePages) {
            List<String> keySkills = new ArrayList<>();
            for (TextInputControl keySkill : page.getKeySkills()) {
                keySkills.add(keySkill.getText());
            }
            experienceList.add(new Experience(page.getJobTitle().getText(), (page.getStartDate().getValue() != null) ? page.getStartDate().getValue().toString() : "",
                    (page.getEndDate().getValue() != null && (!page.getOngoing().isSelected() && page.getStartDate().getValue() != null)) ?
                            (page.getEndDate().getValue().isAfter(page.getStartDate().getValue())) ?
                                    page.getEndDate().getValue().isBefore(LocalDate.now()) ?
                                            page.getEndDate().getValue().toString() : "9999-01-01"
                                    : "9999-01-01"
                    : "9999-01-01",
                    keySkills, page.getCompanyName().getText(),
                    page.getCompanyPlace().getText(), page.getDescription().getText()));
        }
        cvTemplate.setExperiences(experienceList);
    }
}
