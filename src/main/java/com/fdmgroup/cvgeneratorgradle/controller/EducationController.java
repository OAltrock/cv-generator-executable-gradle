package com.fdmgroup.cvgeneratorgradle.controller;


import com.fdmgroup.cvgeneratorgradle.models.Education;
import javafx.beans.binding.Bindings;


import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;


import static com.fdmgroup.cvgeneratorgradle.CVGeneratorApp.*;

public class EducationController implements Initialization {

    private BorderPane main;
    @Setter
    @Getter
    private Education education;
    private ObservableList<TextField> textFields;

    Predicate<String> predicate = input -> !input.matches("[a-zA-Z]+");
    //ToDo: write input to education object
    public EducationController(Education education) {
        this.education = education;
    }

    //ToDo: validation for dates
    @Override
    public void initialize(BorderPane main, String resource) {
        Initialization.super.initialize(main, resource);
        this.main = main;
        Button saveBtn = (Button) main.getCenter().lookup("#saveBtn");
        textFields = FXCollections.observableArrayList();
        createValidation();
        createKeyModulesArea();

        VBox center = (VBox) main.getCenter();
        List<Node> uncheckedTextFields = new ArrayList<>(center.getChildren().stream().filter(child -> child.getClass().toString().contains("TextField")).toList());
        List<TextField> castTextFields = uncheckedTextFields.stream().map(textField -> (TextField) textField).toList();


        addValidationToSaveButtons(textFields, predicate, saveBtn);

        textFields.addAll(castTextFields);


    }
    //ToDo: maybe change current validation to border validation with tooltips?!
    private void createValidation() {
        VBox center = (VBox) main.getCenter();
        List<Node> textFields = center.getChildren().stream().filter(child -> child.getClass().toString().contains("TextField")).toList();
        textFields.forEach(ele -> {
            final StringProperty model = new SimpleStringProperty("");
            ((TextField) ele).textProperty().bindBidirectional(model);
            ObservableBooleanValue validInput = Bindings.createBooleanBinding(() -> predicate.test(model.get()), model);
            String id = ele.getId();
            Label hint = (Label) center.lookup("#hint" + id.substring(0, 1).toUpperCase() + id.substring(1));
            hint.setText("Can't be empty");
            hint.visibleProperty().bind(validInput);
        });
    }


    //ToDo: use methods in interface. add objectBindings to all newly created textFields.
    private void createKeyModulesArea() {
        TextField textField = (TextField) main.getCenter().lookup("#keyModule");
        Tooltip tooltipToAdd = new Tooltip("Can't be empty");
        Tooltip.install(textField, tooltipToAdd);
        ObservableBooleanValue validInput = Bindings.createBooleanBinding(() -> predicate.test(textField.getText()), textField.textProperty());

        textFields.add(textField);

        StringBinding stringBinding = Bindings.createStringBinding(() -> {
            if (validInput.get()) return "Can't be empty";
            else return "";
        }, textField.textProperty());

        ObjectBinding objectBinding = Bindings.createObjectBinding(() -> {
                    if (validInput.get())
                        return new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
                    else
                        return new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
                },
                textField.textProperty());

        textField.borderProperty().bind(objectBinding);
        tooltipToAdd.textProperty().bind(stringBinding);
        //tooltipToAdd.textProperty().bind(allValid);

        Button addModuleButton = (Button) main.getCenter().lookup("#addTextField");
        addModuleButton.setOnAction(event -> {
            GridPane keyModules = (GridPane) main.getCenter().lookup("#keyModules");
            TextField first = (TextField) keyModules.getChildren().getFirst();
            TextField moduleToAdd = new TextField();
            moduleToAdd.setId("keyModule" + getCounter1());
            setCounter1(getCounter1() + 1);
            moduleToAdd.setStyle("-fx-pref-width: 300;");
            moduleToAdd.setPromptText("Key Module");
            Button removeButton = new Button("Remove Key Module");
            removeButton.setId("removeBtn" + (getCounter1() - 1));
            addListenerTooRemoveBtn(removeButton, keyModules, addModuleButton, textFields);
            keyModules.add(moduleToAdd, 0, getCounter1());
            keyModules.add(removeButton, 1, getCounter1());
            keyModules.getChildren().remove(addModuleButton);
            keyModules.add(addModuleButton, 2, getCounter1());
            textFields.addAll(Stream.of(moduleToAdd, first).toList());
        });
    }
}
