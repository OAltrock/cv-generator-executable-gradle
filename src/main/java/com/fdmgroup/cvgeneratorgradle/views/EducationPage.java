package com.fdmgroup.cvgeneratorgradle.views;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasDateValidation;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.models.Education;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EducationPage extends FDMPage implements HasAddableTextFields/*, HasToggleableSaveButtons, HasDateValidation*/ {
    private final Education education;

    private FDMCenterVBoxWrapper centerBox;

    private Label pageTitle;
    private TextField degree;
    private TextField studyTitle;
    private TextField universityName;
    private TextField universityPlace;
    private TextField thesisTitle;

    private FDMDateWrapper dateWrapper;
    private DatePicker startDate;
    private DatePicker endDate;
    private CheckBox ongoing;


    private Label keyModuleLabel;
    private GridPane keyModuleGridPane;
    private final List<TextInputControl> keyModules;

    private FDMButton nextBtn;
    private FDMButton prevBtn;
    private FDMHBox buttonWrapper;

    private final ObservableList<TextInputControl> textFields;
    private final String forFutureReference;

    //alternative way to create views (to fxml). scene builder can be weird.
    public EducationPage(ObservableList<TextInputControl> textFields, String forFutureReference) {
        education = new Education();
        this.textFields = textFields;
        this.forFutureReference = forFutureReference;
        keyModules = new ArrayList<>();
        education.setKeyModules(new ArrayList<>());
        initialize();
    }

    public EducationPage(Education education, String forFutureReference, ObservableList<TextInputControl> textFields) {
        this.education = education;
        this.textFields = textFields;
        this.forFutureReference = forFutureReference;
        keyModules = new ArrayList<>();
        initialize();


    }

    private void initialize() {
        degree = (education.getDegree() != null) ? new TextField(education.getDegree()) : new TextField("");
        degree.setId("degree");
        degree.setPromptText("Degree");
        studyTitle = (education.getDegree() != null) ? new TextField(education.getStudyTitle()) : new TextField("");
        studyTitle.setId("studyTitle");
        studyTitle.setPromptText("Study title");
        universityName = (education.getDegree() != null) ? new TextField(education.getUniversityName()) : new TextField("");
        universityName.setId("universityName");
        universityName.setPromptText("University name");
        universityPlace = (education.getDegree() != null) ? new TextField(education.getUniversityPlace()) : new TextField("");
        universityPlace.setId("universityPlace");
        universityPlace.setPromptText("Place of university");
        thesisTitle = (education.getDegree() != null) ? new TextField(education.getThesisTitle()) : new TextField("");
        thesisTitle.setId("thesisTitle");
        thesisTitle.setPromptText("Title of thesis");
        startDate = (education.getStartDate() != null) ? new DatePicker(LocalDate.parse(education.getStartDate())) : new DatePicker();
        endDate = (education.getStartDate() != null) ? new DatePicker(LocalDate.parse(education.getEndDate())) : new DatePicker();
        ongoing = new CheckBox("ongoing");
        if (endDate.getValue() != null) {
            ongoing.setSelected(endDate.getValue().isAfter(LocalDate.now()));
            endDate.setDisable(endDate.getValue().isAfter(LocalDate.now()));
        }

        education.getKeyModules().forEach(keyModule -> {
            TextField textField = new TextField(keyModule);
            textField.setId(keyModule);
            keyModules.add(textField);
        });
        pageTitle = new Label("Education");
        keyModuleLabel = new Label("Add " + forFutureReference + " key modules");
        keyModuleGridPane = new GridPane(3, keyModules.size());

        dateWrapper = new FDMDateWrapper(startDate, endDate, ongoing);
        dateWrapper.setDesign();


        textFields.addAll(keyModules);
        FDMButton addBtn = new FDMButton("Add key module");
        addBtn.setDesign();
        createAddableAreaFromModel(keyModules, keyModuleGridPane, addBtn, textFields, forFutureReference, "Remove key module", "Key module");

        prevBtn = new FDMButton("Previous");
        nextBtn = new FDMButton("Next");
        buttonWrapper = new FDMHBox(prevBtn,nextBtn);
        buttonWrapper.setDesign();

        centerBox = new FDMCenterVBoxWrapper(pageTitle, degree,
                studyTitle,
                universityName,
                universityPlace,
                thesisTitle, dateWrapper, keyModuleLabel, keyModuleGridPane, buttonWrapper);
        centerBox.setDesign();
    }
}
