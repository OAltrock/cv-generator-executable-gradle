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
public class EducationPage extends FDMPage implements HasAddableTextFields, HasToggleableSaveButtons, HasDateValidation {
    private final Education education;

    private FDMScrollPaneWrapper center;
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

    private FDMButton saveBtn;

    private final ObservableList<TextInputControl> textFields;
    private final String forFutureReference;

    //alternative way to create views (to fxml). scene builder can be weird. not used yet.
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
        studyTitle = (education.getDegree() != null) ? new TextField(education.getStudyTitle()) : new TextField("");
        studyTitle.setId("studyTitle");
        universityName = (education.getDegree() != null) ? new TextField(education.getUniversityName()) : new TextField("");
        universityName.setId("universityName");
        universityPlace = (education.getDegree() != null) ? new TextField(education.getUniversityPlace()) : new TextField("");
        universityPlace.setId("universityPlace");
        thesisTitle = (education.getDegree() != null) ? new TextField(education.getThesisTitle()) : new TextField("");
        thesisTitle.setId("thesisTitle");
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
        createAddableAreaFromModel(keyModules, keyModuleGridPane, addBtn, textFields, forFutureReference);
        saveBtn = new FDMButton("Save");
        centerBox = new FDMCenterVBoxWrapper(pageTitle, degree,
                studyTitle,
                universityName,
                universityPlace,
                thesisTitle, dateWrapper, keyModuleLabel, keyModuleGridPane, saveBtn);
        centerBox.setDesign();
    }

    /*public ScrollPane createCenterPage() {
        center = new FDMScrollPaneWrapper(centerBox);
        center.setDesign();
        return center;
    }*/
}
