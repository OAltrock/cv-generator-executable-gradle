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

public class EducationPage implements HasAddableTextFields, HasToggleableSaveButtons, HasDateValidation {
    @Getter
    private ScrollPane center;
    @Getter
    private VBox centerBox;
    private TextField degree;
    private TextField studyTitle;
    private TextField universityName;
    private TextField universityPlace;
    private TextField thesisTitle;
    @Getter
    private DatePicker startDate;
    @Getter
    private DatePicker endDate;
    private List<TextField> keyModules;
    private Label pageTitle;
    private Label keyModuleLabel;
    private GridPane keyModuleGridPane;
    @Getter
    private CheckBox ongoing;
    private HBox dateWrapper;
    private Button saveBtn;

    public EducationPage() {
    }

    public EducationPage(Education education, String forFutureReference, ObservableList<TextInputControl> textFields) {
        this.degree = new TextField(education.getDegree());
        this.studyTitle = new TextField(education.getStudyTitle());
        this.universityName = new TextField(education.getUniversityName());
        this.universityPlace = new TextField(education.getUniversityPlace());
        this.thesisTitle = new TextField(education.getThesisTitle());
        this.startDate = new DatePicker(LocalDate.parse(education.getStartDate()));
        this.endDate = new DatePicker(LocalDate.parse(education.getEndDate()));
        if (endDate.getValue().isAfter(LocalDate.now())) endDate.setDisable(true);
        this.keyModules = education.getKeyModules().stream()
                .map(TextField::new)
                .toList();
        this.pageTitle = new Label("Education");
        this.keyModuleLabel = new Label("Add " + forFutureReference + " key modules");
        this.keyModuleGridPane = new GridPane(3, keyModules.size());
        this.ongoing = new CheckBox();
        ongoing.setSelected(endDate.getValue().isAfter(LocalDate.now()));
        this.dateWrapper = new HBox(startDate, endDate, ongoing);
        dateWrapper.setSpacing(20);
        dateWrapper.setAlignment(Pos.CENTER);
        dateWrapper.setPadding(new Insets(20));


        //textFields.addAll(degree, studyTitle, universityName, universityPlace, thesisTitle);
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
        keyModules.forEach(textField -> {
            if (textField == keyModules.getFirst()) {
                keyModuleGridPane.add(textField, 0, 0);
                keyModuleGridPane.add(addBtn, 2, 0);
            } else if (textField == keyModules.getLast()) {
                int rowCount = keyModuleGridPane.getRowCount();
                Button removeBtn = new Button("Remove key module");

                keyModuleGridPane.add(textField, 0, rowCount);
                keyModuleGridPane.add(removeBtn, 1, rowCount);
                keyModuleGridPane.add(addBtn, 2, rowCount);
                addListenerTooRemoveBtn(textField, removeBtn, keyModuleGridPane, addBtn, textFields);
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
