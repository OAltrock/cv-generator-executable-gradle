package com.fdmgroup.cvgeneratorgradle.views;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Experience;
import com.fdmgroup.cvgeneratorgradle.models.Location;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
public class ExperiencePage extends FDMPage implements HasAddableTextFields {
    private final CVTemplate cvTemplate;
    //private List<Experience> experiences;
    private Experience experience;
    private boolean singleExperience;
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
    @Setter
    private FDMButton removePage;

    private FDMButton nextBtn;
    private FDMButton prevBtn;
    private FDMHBox buttonWrapper;

    private final ObservableList<TextInputControl> textFields;

    /*public ExperiencePage(ObservableList<TextInputControl> textFields, String forFutureReference) {
        this.forFutureReference = forFutureReference;
        this.textFields = textFields;
        keySkills = new ArrayList<>();
        experience = new Experience();
        experience.setPositionFeatures(new ArrayList<>());
        initialize();
    }*/

    public ExperiencePage(CVTemplate cvTemplate, ObservableList<TextInputControl> textFields, Experience experience, boolean singleExperience) {
        this.cvTemplate = cvTemplate;
        this.singleExperience = singleExperience;
        /*if (cvTemplate.getExperiences() != null && !cvTemplate.getExperiences().isEmpty())
            experiences = cvTemplate.getExperiences();
        else {
            cvTemplate.setExperiences(new ArrayList<>());
            cvTemplate.getExperiences().add(new Experience("", "", "", new ArrayList<>(), "", "", ""));
        }*/
        this.textFields = textFields;
        this.experience = experience;

        keySkills = new ArrayList<>();

        initialize();

    }

    private void initialize() {
        /*String number = (cvTemplate.getExperiences() != null) ? String.valueOf(cvTemplate.getExperiences().size()) : "1";*/
        pageTitle = new Label(); /*"Experience" + number);*/
        jobTitle = (experience.getJobTitle() != null) ? new javafx.scene.control.TextField(experience.getJobTitle()) : new TextField("");
        jobTitle.setPromptText("Title of job");
        companyName = (experience.getJobTitle() != null) ? new javafx.scene.control.TextField(experience.getCompanyName()) : new TextField("");
        companyName.setPromptText("Name of company");
        companyPlace = (experience.getJobTitle() != null) ? new javafx.scene.control.TextField(experience.getCompanyName()) : new TextField("");
        companyPlace.setPromptText("Place of company");
        description = (experience.getJobTitle() != null) ? new TextArea(experience.getDescription()) : new TextField("");
        description.setPromptText("Description");

        startDate = (experience.getStartDate() != null && !experience.getStartDate().isEmpty()) ? new DatePicker(LocalDate.parse(experience.getStartDate()))
                : new DatePicker();
        endDate = (experience.getStartDate() != null && !experience.getStartDate().isEmpty()) ? new DatePicker(LocalDate.parse(experience.getEndDate()))
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
        if (cvTemplate.getLocation() == null)
            cvTemplate.setLocation(new Location("", 1, 1, 1, 3, 1, 3, 1, 3, 0, 1, 0, 1, 1, 3, 0, 3, false));
        createAddableAreaFromModel(keySkills, keySkillsGridPane, addBtn, textFields, cvTemplate.getLocation().getMaxPositionFeature(), "Remove key skill", "Key skill");

                    /*nextBtn = new FDMButton("Next");
                    prevBtn = new FDMButton("Previous");
                    buttonWrapper = new FDMHBox(prevBtn, nextBtn);
                    buttonWrapper.setDesign();*/

        centerBox = new FDMCenterVBoxWrapper();
        removePage = new FDMButton("Remove experience");
        centerBox.getChildren().addAll(pageTitle, jobTitle, companyName, companyPlace,
                dateWrapper, keySkillsGridPane, removePage);


        centerBox.setDesign();
        center = new ScrollPane(centerBox);
    }
}
