package com.fdmgroup.cvgeneratorgradle.views;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Education;
import com.fdmgroup.cvgeneratorgradle.models.Location;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EducationPage extends FDMPage implements HasAddableTextFields/*, HasToggleableSaveButtons, HasDateValidation*/ {

    private final CVTemplate cvTemplate;
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



    private final ObservableList<TextInputControl> textFields;

    public EducationPage(CVTemplate cvTemplate, ObservableList<TextInputControl> textFields) {
        this.cvTemplate = cvTemplate;
        if (cvTemplate.getEducations() == null) {
            List<Education> educations = new ArrayList<>(List.of(new Education("", "",
                    "", "", "", "", "", new ArrayList<>())));
            //educations.getLast().setKeyModules(new ArrayList<>());
            cvTemplate.setEducations(educations);
        }
        education = cvTemplate.getEducations().getLast();
        this.textFields = textFields;
        keyModules = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        assert education != null;
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
        startDate = (education.getStartDate() != null && !education.getStartDate().isEmpty())
                ? new DatePicker(LocalDate.parse(education.getStartDate())) : new DatePicker();
        endDate = (education.getStartDate() != null && !education.getStartDate().isEmpty()) ?
                (LocalDate.parse(education.getEndDate()).isAfter(LocalDate.parse(education.getStartDate()))) ?
                        new DatePicker(LocalDate.parse(education.getEndDate()))
                        : new DatePicker(LocalDate.now().plusMonths(1))
                : new DatePicker();
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
        if (cvTemplate.getLocation() == null)
            cvTemplate.setLocation(new Location("", 1, 1, 1, 3, 1, 3, 1, 3, 0, 1, 0, 1, 1, 3, 0, 3, false));
        keyModuleLabel = new Label("Add " + cvTemplate.getLocation().getMinKeyModule() + " to " + cvTemplate.getLocation().getMaxKeyModule() + " key modules");
        keyModuleGridPane = new GridPane(3, keyModules.size());

        dateWrapper = new FDMDateWrapper(startDate, endDate, ongoing);
        dateWrapper.setDesign();


        textFields.addAll(keyModules);
        FDMButton addBtn = new FDMButton("Add key module");
        createAddableAreaFromModel(keyModules, keyModuleGridPane, addBtn, textFields, cvTemplate.getLocation().getMaxKeyModule(), "Remove key module", "Key module");

        prev = new FDMButton("Previous");
        next = new FDMButton("Next");
        buttonWrapper = new FDMHBox(prev, next);
        buttonWrapper.setDesign();

        centerBox = new FDMCenterVBoxWrapper(pageTitle, degree,
                studyTitle,
                universityName,
                universityPlace,
                thesisTitle, dateWrapper, keyModuleLabel, keyModuleGridPane, buttonWrapper);
        centerBox.setDesign();
    }
}
