package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.function.Predicate;

import static com.fdmgroup.cvgeneratorgradle.CVGeneratorApp.getCounter1;
import static com.fdmgroup.cvgeneratorgradle.CVGeneratorApp.setCounter1;

public interface Initialization {

    @FXML
    default void initialize(BorderPane main, String resource) {
        FXMLLoader fxmlLoader = new FXMLLoader(CVGeneratorApp.class.getResource(resource+".fxml"));
        try {
            main.setCenter(fxmlLoader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*default Boolean addValidationToSaveButtons(Parent node) {
        Predicate<String> checkNoLetters = input -> input.matches("[a-zA-Z]+");

        List<Node> textFields = node.getChildrenUnmodifiable().stream().filter(child -> child.getClass().toString().contains("TextField")).toList();
        List<Node> filteredTextFields =  textFields.stream().filter(textField -> !checkNoLetters.test(((TextField)textField).getText())).toList();
        return filteredTextFields.isEmpty();
    }*/

    default void addValidationToSaveButtons(ObservableList<TextField> list, Predicate<String> predicate, Button saveBtn) {
        list.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                BooleanBinding allVboxFieldsValidated = list.stream()
                        .map(TextField::textProperty)
                        .map(stringProperty -> Bindings.createBooleanBinding(() -> predicate.test(stringProperty.get()), stringProperty))
                        .reduce(Bindings::or)
                        .get();
                saveBtn.disableProperty().bind(allVboxFieldsValidated);
            }
        });
    }

    default void addAddButtons(BorderPane main, Button btn, ObservableList<TextField> textFields) {
        TextField textField = (TextField) main.getCenter().lookup("#keyModule");
        final StringProperty model = new SimpleStringProperty("Can't be empty");

        textField.promptTextProperty().bindBidirectional(model);

        Button addModuleButton = (Button) main.getCenter().lookup("#addTextField");
        addModuleButton.setOnAction(event -> {
            GridPane keyModules = (GridPane) main.getCenter().lookup("#keyModules");
            TextField moduleToAdd = new TextField();
            moduleToAdd.setId("keyModule"+getCounter1());
            setCounter1(getCounter1()+1);
            moduleToAdd.setStyle("-fx-pref-width: 300;");
            moduleToAdd.setPromptText("Key Module");
            Button removeButton = new Button("Remove Key Module");
            removeButton.setId("removeBtn"+ (getCounter1() - 1));
            addListenerTooRemoveBtn(removeButton, keyModules, addModuleButton, textFields);
            keyModules.add(moduleToAdd ,0,getCounter1());
            keyModules.add(removeButton, 1,getCounter1());
            keyModules.getChildren().remove(addModuleButton);
            keyModules.add(addModuleButton, 2,getCounter1());
        });
    }

    default void addListenerTooRemoveBtn(Button removeButton, GridPane gridPane, Button addModuleBtn, ObservableList textFields) {
        removeButton.setOnAction(event -> {
            Button which = (Button) event.getSource();
            String id = String.valueOf(which.getId().charAt(which.getId().length()-1));
            TextField toRemove = (TextField) gridPane.lookup("#keyModule"+id);
            gridPane.getChildren().remove(toRemove);
            gridPane.getChildren().remove(removeButton);
            setCounter1(getCounter1()-1);
            gridPane.getChildren().remove(addModuleBtn);
            gridPane.add(addModuleBtn,2,getCounter1());
            textFields.remove(toRemove);
        });
    }

}
