package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.fdmgroup.cvgeneratorgradle.CVGeneratorApp.*;

public class EducationController {

    private BorderPane main;

    Predicate<String> predicate = input -> !input.matches("[a-zA-Z]+");
    ObservableBooleanValue validInput;

    @FXML
    public void initialize(BorderPane main, String resource) {
        FXMLLoader fxmlLoader = new FXMLLoader(CVGeneratorApp.class.getResource(resource+".fxml"));
        try {
            this.main = main;
            main.setCenter(fxmlLoader.load());
            Button saveBtn = (Button) main.getCenter().lookup("#saveBtn");
            createKeyModulesArea();
            createValidation();
            saveBtn.disableProperty().bind(validInput);
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    private void createValidation() {
        VBox center = (VBox) main.getCenter();
        List<Node> textFields = center.getChildren().stream().filter(child->child.getClass().toString().contains("TextField")).collect(Collectors.toList());
        textFields.forEach(ele -> {
            final StringProperty model = new SimpleStringProperty("");
            ((TextField) ele).textProperty().bindBidirectional(model);
            validInput = Bindings.createBooleanBinding(() -> predicate.test(model.get()), model);
            String id = ele.getId();
            Label hint = (Label) center.lookup("#hint"+id.substring(0,1).toUpperCase()+id.substring(1));
            hint.setText("Can't be empty");
            hint.visibleProperty().bind(validInput);
        });
    }

    private void createKeyModulesArea() {
        TextField textField = (TextField) main.getCenter().lookup("#keyModule");
        final StringProperty model = new SimpleStringProperty("Can't be empty");
        validInput = Bindings.createBooleanBinding(() -> predicate.test(model.get()), model);
        textField.promptTextProperty().bindBidirectional(model);

        Button addModuleButton = (Button) main.getCenter().lookup("#addModuleBtn");
        addModuleButton.setOnAction(event -> {
            GridPane keyModules = (GridPane) main.getCenter().lookup("#keyModules");
            TextField moduleToAdd = new TextField();
            moduleToAdd.setId("keyModule"+getCounter1());
            setCounter1(getCounter1()+1);
            moduleToAdd.setStyle("-fx-pref-width: 300;");
            moduleToAdd.setPromptText("Key Module");
            Button removeButton = new Button("Remove Key Module");
            removeButton.setId("removeBtn"+ (getCounter1() - 1));
            addListenerTooRemoveBtn(removeButton, keyModules, addModuleButton);
            keyModules.add(moduleToAdd ,0,getCounter1());
            keyModules.add(removeButton, 1,getCounter1());
            keyModules.getChildren().remove(addModuleButton);
            keyModules.add(addModuleButton, 2,getCounter1());
        });
    }

    private void addListenerTooRemoveBtn(Button removeButton, GridPane gridPane, Button addModuleBtn) {
        removeButton.setOnAction(event -> {
            Button which = (Button) event.getSource();
            String id = String.valueOf(which.getId().charAt(which.getId().length()-1));
            TextField toRemove = (TextField) gridPane.lookup("#keyModule"+id);
            gridPane.getChildren().remove(toRemove);
            gridPane.getChildren().remove(removeButton);
            setCounter1(getCounter1()-1);
            gridPane.getChildren().remove(addModuleBtn);
            gridPane.add(addModuleBtn,2,getCounter1());
        });
    }


}
