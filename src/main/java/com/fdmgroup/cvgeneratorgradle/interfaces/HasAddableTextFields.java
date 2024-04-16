package com.fdmgroup.cvgeneratorgradle.interfaces;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;


import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.fdmgroup.cvgeneratorgradle.CVGeneratorApp.setCounter1;

public interface HasAddableTextFields {


    /**
     * Adds functionality to the add-Button which creates a new row within the given GridPane. This row contains a {@link TextField}, a remove-Button and moves the
     * add-Button to the newly created row.
     * It also adds functionality to the created remove-Button to remove the row the Button is in and if applicable, place the add-Button in the new bottom row.
     *
     * @param parent             Parent {@link GridPane} to the given add-Button.
     * @param textFields         {@link ObservableList} of {@link TextInputControl}s to which the TextFields, newly created by the add-Button should be
     *                           added or removed if removed by the remove-Button. These will be bound to the disableProperty of the save-Button(see: {@link HasToggleableSaveButtons})
     * @param addBtn             Add {@link Button} to which the new functionality should be added.
     * @param removeBtnMessage   {@link String}-message for the newly created remove-{@link Button}.
     * @param promptMessage      {@link String}-message for the prompt of the newly created {@link TextField}.
     * @param predicate          {@link Predicate}&lt;String&gt; to validate the newly created {@link TextField}.
     * @param forFutureReference limit for adding Modules
     */
    default void addAddButtons(GridPane parent, ObservableList<TextInputControl> textFields,
                               Button addBtn, String removeBtnMessage, String promptMessage, Predicate<String> predicate, String forFutureReference) {
        System.out.println(parent.getRowCount());
            addBtn.setOnAction(event -> {
                if (parent.getChildren().size()/2+1<=Integer.parseInt(forFutureReference)) {
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
                }
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


    /**
     * Method to create validation for the given {@link ObservableList} of {@link TextInputControl}s. If validated creates a green Border around each {@link TextInputControl},
     * a red one otherwise.
     * @param predicate {@link Predicate}&lt;String&gt; to validate the text of a {@link TextInputControl} by.
     * @param textFields {@link ObservableList} of {@link TextInputControl}s that should be validated.
     */
    default void createValidationForTextFields(Predicate<String> predicate, ObservableList<TextInputControl> textFields) {
        validationHelper(predicate, textFields, null);
    }

    //creates validation for both the default and the overloaded method
    private void validationHelper(Predicate<String> predicate, ObservableList<TextInputControl> textFields, String o) {
        String message = (o == null) ? "cant' be empty" : o;
        textFields.forEach(textField -> {

            ObservableBooleanValue validInput = Bindings.createBooleanBinding(() -> predicate.test(textField.getText()), textField.textProperty());

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
        });
    }

    /**
     * Overloaded method to validate a list of {@link TextInputControl}.
     * @param predicate {@link Predicate}&lt;String&gt; to validate the text of a {@link TextInputControl} by.
     * @param textFields {@link ObservableList} of {@link TextInputControl}s that should be validated.
     * @param message {@link String}-message shown by the {@link Tooltip} if a field is not validated.
     */
    default void createValidationForTextFields(Predicate<String> predicate, ObservableList<TextInputControl> textFields, String message) {
        validationHelper(predicate, textFields, message);
    }
}
