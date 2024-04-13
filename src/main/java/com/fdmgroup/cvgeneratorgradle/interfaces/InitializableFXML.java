package com.fdmgroup.cvgeneratorgradle.interfaces;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public interface InitializableFXML {

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



    /*default void addAddButtons(BorderPane main, Button btn, ObservableList<TextField> textFields) {
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
    }*/



}
