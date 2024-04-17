package com.fdmgroup.cvgeneratorgradle.views;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasDateValidation;
import com.fdmgroup.cvgeneratorgradle.interfaces.HasToggleableSaveButtons;
import com.fdmgroup.cvgeneratorgradle.models.Education;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EducationPage implements HasAddableTextFields, HasToggleableSaveButtons, HasDateValidation {
    private final Education education;
    private ScrollPane center;
    private VBox centerBox;
    private TextField degree;
    private TextField studyTitle;
    private TextField universityName;
    private TextField universityPlace;
    private TextField thesisTitle;
    private DatePicker startDate;
    private DatePicker endDate;
    private List<TextInputControl> keyModules;
    private Label pageTitle;
    private Label keyModuleLabel;
    private GridPane keyModuleGridPane;
    private CheckBox ongoing;
    private HBox dateWrapper;
    private Button saveBtn;
    private final ObservableList<TextInputControl> textFields;
    private final String forFutureReference;

    //alternative way to create views (to fxml). scene builder can be weird. not used yet.
    public EducationPage(ObservableList<TextInputControl> textFields, String forFutureReference) {
        education = new Education();
        this.textFields = textFields;
        this.forFutureReference = forFutureReference;
        keyModules = new ArrayList<>();
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
        degree = new TextField(education.getDegree());
        degree.setId("degree");
        studyTitle = new TextField(education.getStudyTitle());
        studyTitle.setId("studyTitle");
        universityName = new TextField(education.getUniversityName());
        universityName.setId("universityName");
        universityPlace = new TextField(education.getUniversityPlace());
        universityPlace.setId("universityPlace");
        thesisTitle = new TextField(education.getThesisTitle());
        thesisTitle.setId("thesisTitle");
        startDate = new DatePicker(LocalDate.parse(education.getStartDate()));
        endDate = new DatePicker(LocalDate.parse(education.getEndDate()));
        if (endDate.getValue().isAfter(LocalDate.now())) endDate.setDisable(true);
        /*keyModules = education.getKeyModules().stream()
                .map(TextField::new)
                .toList();*/

        education.getKeyModules().forEach(keyModule -> {
            TextField textField = new TextField(keyModule);
            textField.setId(keyModule);
            keyModules.add(textField);
        });
        pageTitle = new Label("Education");
        keyModuleLabel = new Label("Add " + forFutureReference + " key modules");
        keyModuleGridPane = new GridPane(3, keyModules.size());
        ongoing = new CheckBox();
        ongoing.setSelected(endDate.getValue().isAfter(LocalDate.now()));
        dateWrapper = new HBox(startDate, endDate, ongoing);
        dateWrapper.setSpacing(20);
        dateWrapper.setAlignment(Pos.CENTER);
        dateWrapper.setPadding(new Insets(20));

        textFields.addAll(keyModules);
        createGridPane(textFields, forFutureReference);
        saveBtn = new Button("Save");
        centerBox = new VBox(pageTitle, degree,
                studyTitle,
                universityName,
                universityPlace,
                thesisTitle, dateWrapper, keyModuleLabel, keyModuleGridPane, saveBtn);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(20));
        centerBox.setSpacing(20);
        centerBox.minWidth(450);
        centerBox.setFillWidth(true);
    }

    private void createGridPane(ObservableList<TextInputControl> textFields, String forFutureReference) {
        Button addBtn = new Button("Add key module");
        keyModules.forEach(System.out::println);
        keyModules.forEach(textField -> {
            if (textField == keyModules.getFirst() && textField!=keyModules.getLast()) {
                Button removeBtn = new Button("Remove key module");
                keyModuleGridPane.add(textField, 0, 0);
                keyModuleGridPane.add(removeBtn, 1, 0);
                addListenerTooRemoveBtn(textField, removeBtn,keyModuleGridPane,addBtn,textFields);
            } else if (textField == keyModules.getLast() && textField!=keyModules.getFirst()) {
                int rowCount = keyModuleGridPane.getRowCount();
                Button removeBtn = new Button("Remove key module");
                keyModuleGridPane.add(textField, 0, rowCount);
                keyModuleGridPane.add(removeBtn, 1, rowCount);
                keyModuleGridPane.add(addBtn, 2, rowCount);
                addListenerTooRemoveBtn(textField, removeBtn, keyModuleGridPane, addBtn, textFields);
            } else if (textField == keyModules.getFirst()) {
                int rowCount = keyModuleGridPane.getRowCount();
                keyModuleGridPane.add(textField, 0, rowCount);
                keyModuleGridPane.add(addBtn, 2, rowCount);
            } else {
                int rowCount = keyModuleGridPane.getRowCount();
                Button removeBtn = new Button("Remove key module");
                keyModuleGridPane.add(textField, 0, rowCount);
                keyModuleGridPane.add(removeBtn, 1, rowCount);
                addListenerTooRemoveBtn(textField, removeBtn, keyModuleGridPane, addBtn, textFields);
            }
            addAddButtons(keyModuleGridPane,textFields,addBtn, "Remove Key Module", "Key Module",(string -> !string.matches("[a-zA-Z]+")),forFutureReference);
        });
    }

    public ScrollPane createCenterPage() {
        center = new ScrollPane(centerBox);
        center.setFitToHeight(true);
        center.setFitToWidth(true);
        return center;
    }
}
