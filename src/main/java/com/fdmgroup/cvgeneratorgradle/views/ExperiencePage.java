package com.fdmgroup.cvgeneratorgradle.views;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.models.Experience;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
public class ExperiencePage extends FDMPage implements HasAddableTextFields {

    private final Experience experience;
    private javafx.scene.control.ScrollPane center;
    private FDMCenterVBoxWrapper centerBox;
    private javafx.scene.control.Label pageTitle;

    private javafx.scene.control.TextField jobTitle;
    private javafx.scene.control.TextField companyName;
    private javafx.scene.control.TextField companyPlace;
    private TextInputControl description;

    private FDMDateWrapper dateWrapper;
    private DatePicker startDate;
    private DatePicker endDate;
    private CheckBox ongoing;

    private javafx.scene.control.Label keySkillsLabel;
    private GridPane keySkillsGridPane;
    private final List<TextInputControl> keySkills;

    private FDMButton addBtn;
    private FDMButton saveBtn;

    private final ObservableList<TextInputControl> textFields;
    private final String forFutureReference;

    public ExperiencePage(ObservableList<TextInputControl> textFields, String forFutureReference) {
        this.forFutureReference = forFutureReference;
        this.textFields = textFields;
        keySkills = new ArrayList<>();
        experience = new Experience();
        experience.setPositionFeatures(new ArrayList<>());
        initialize();
    }

    public ExperiencePage(Experience experience, ObservableList<TextInputControl> textFields, String forFutureReference) {
        this.experience = experience;
        this.textFields = textFields;
        this.forFutureReference = forFutureReference;
        keySkills = new ArrayList<>();

        initialize();

    }

    private void initialize() {
        pageTitle = new Label("Experience");
        jobTitle = (experience.getJobTitle()!=null) ? new javafx.scene.control.TextField(experience.getJobTitle()) : new TextField("");
        companyName = (experience.getJobTitle()!=null) ? new javafx.scene.control.TextField(experience.getCompanyName()): new TextField("");
        companyPlace = (experience.getJobTitle()!=null) ? new javafx.scene.control.TextField(experience.getCompanyName()): new TextField("");
        description = (experience.getJobTitle()!=null) ? new TextArea(experience.getDescription()): new TextField("");

        startDate = (experience.getStartDate() != null) ? new DatePicker(LocalDate.parse(experience.getStartDate()))
                : new DatePicker();
        endDate = (experience.getStartDate() != null) ? new DatePicker(LocalDate.parse(experience.getEndDate()))
                : new DatePicker();
        ongoing = new CheckBox("ongoing");
        if (endDate.getValue() != null) {
            ongoing.setSelected(endDate.getValue().isAfter(LocalDate.now()));
            endDate.setDisable(endDate.getValue().isAfter(LocalDate.now()));
        }
        dateWrapper = new FDMDateWrapper(startDate, endDate, ongoing);
        dateWrapper.setDesign();

        experience.getPositionFeatures().forEach(positionFeature -> {
            javafx.scene.control.TextField textField = new javafx.scene.control.TextField(positionFeature);
            textField.setId(positionFeature);
            keySkills.add(textField);
        });
        keySkillsGridPane = new GridPane(3, keySkills.size());
        keySkillsLabel = new javafx.scene.control.Label("Key Skills");
        addBtn = new FDMButton("Add key skills");

        textFields.addAll(keySkills);

        createAddableAreaFromModel(keySkills, keySkillsGridPane, addBtn, textFields, forFutureReference);

        saveBtn = new FDMButton("Save");


        centerBox = new FDMCenterVBoxWrapper(pageTitle, jobTitle, companyName, companyPlace,
                dateWrapper, keySkillsGridPane, saveBtn);
        centerBox.setDesign();
        center = new ScrollPane(centerBox);
    }
}
