package com.fdmgroup.cvgeneratorgradle.interfaces;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;


import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.fdmgroup.cvgeneratorgradle.CVGeneratorApp.setCounter1;

public interface HasAddableTextFields {

    default void addAddButtons(GridPane parent, ObservableList<TextInputControl> textFields, javafx.scene.control.Button addBtn, String removeBtnMessage, String promptMessage, Predicate<String> predicate) {
        addBtn.setOnAction(event -> {
            javafx.scene.control.TextField first = (javafx.scene.control.TextField) parent.getChildrenUnmodifiable().getFirst();
            javafx.scene.control.TextField textFieldToAdd = new javafx.scene.control.TextField();
            int rowCount = parent.getRowCount() + 1;
            setCounter1(CVGeneratorApp.getCounter1() + 1);
            textFieldToAdd.setStyle("-fx-pref-width: 300;");
            textFieldToAdd.setPromptText(promptMessage);
            javafx.scene.control.Button removeButton = new javafx.scene.control.Button(removeBtnMessage);
            removeButton.setId(Integer.toString(rowCount));


            parent.add(textFieldToAdd, 0, rowCount);
            parent.add(removeButton, 1, rowCount);
            parent.getChildren().remove(addBtn);
            parent.add(addBtn, 2, rowCount);

            addListenerTooRemoveBtn(textFieldToAdd, removeButton, parent, addBtn, textFields);
            textFields.addAll(Stream.of(textFieldToAdd, first).toList());
            createValidationForTextFields(predicate, textFields);
        });
    }

    default void addListenerTooRemoveBtn(TextField textFieldToRemove, javafx.scene.control.Button removeButton,
                                         GridPane gridPane, javafx.scene.control.Button addModuleBtn, ObservableList<TextInputControl> textFields) {
        removeButton.setOnAction(event -> {
            textFields.remove(textFieldToRemove);
            gridPane.getChildren().removeAll(addModuleBtn, removeButton,textFieldToRemove);
            gridPane.add(addModuleBtn, 2, gridPane.getRowCount()-1);

        });
    }

    default void createValidationForTextFields(Predicate<String> predicate, ObservableList<TextInputControl> textFields) {
        validationHelper(predicate, textFields, null);
    }

    default void validationHelper(Predicate<String> predicate, ObservableList<TextInputControl> textFields, String o) {
        String message = (o == null) ? "cant' be empty" : o;
        textFields.forEach(System.out::println);
        textFields.forEach(textField -> {

            ObservableBooleanValue validInput = Bindings.createBooleanBinding(() -> predicate.test(textField.getText()), textField.textProperty());


            /*StringBinding stringBinding = Bindings.createStringBinding(() -> {
                if (validInput.get()) return message;
                else return "";
            }, textField.textProperty());*/


            ObjectBinding<Border> objectBinding = Bindings.createObjectBinding(() -> {
                        if (validInput.get())
                            return new Border(new BorderStroke(javafx.scene.paint.Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
                        else
                            return new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
                    },
                    textField.textProperty());
            Tooltip tooltipToAdd = new Tooltip(message);
            ObjectBinding<Tooltip> tooltipObjectBinding = Bindings.createObjectBinding(() -> {
                if (validInput.get()) return tooltipToAdd;
                else return null;
            }, textField.textProperty());
            textField.tooltipProperty().bind(tooltipObjectBinding);
            Tooltip.install(textField, tooltipToAdd);
            textField.borderProperty().bind(objectBinding);
            /*tooltipToAdd.textProperty().bind(stringBinding);*/
        });
    }

    ;


    default void createValidationForTextFields(Predicate<String> predicate, ObservableList<TextInputControl> textFields, String message) {
        validationHelper(predicate, textFields, message);
    }
}
