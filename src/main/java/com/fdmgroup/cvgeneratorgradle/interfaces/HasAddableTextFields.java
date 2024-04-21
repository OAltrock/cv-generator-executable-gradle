package com.fdmgroup.cvgeneratorgradle.interfaces;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import com.fdmgroup.cvgeneratorgradle.views.FDMButton;
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

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

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
    default void createAddableArea(GridPane parent, ObservableList<TextInputControl> textFields,
                                   Button addBtn, String removeBtnMessage, String promptMessage, Predicate<String> predicate, String forFutureReference, List<TextInputControl> addableTextFields) {

        addBtn.setOnAction(event -> {
            if (parent.getChildren().size() / 2 + 1 <= Integer.parseInt(forFutureReference)) {
                javafx.scene.control.TextField textFieldToAdd = new javafx.scene.control.TextField();
                int rowCount = parent.getRowCount() + 1;
                setCounter1(CVGeneratorApp.getCounter1() + 1);
                textFieldToAdd.setStyle("-fx-pref-width: 300;");
                textFieldToAdd.setPromptText(promptMessage);
                textFieldToAdd.setId(String.valueOf(parent.getRowCount() + 1));
                javafx.scene.control.Button removeButton = new javafx.scene.control.Button(removeBtnMessage);
                removeButton.setId(Integer.toString(rowCount));

                parent.add(textFieldToAdd, 0, rowCount);
                parent.add(removeButton, 1, rowCount);

                //ToDo: maybe there is a better way (includes the whole system of adding and removing removeBtns)
                if (parent.getChildren().size() > 3 && parent.getChildren().size()<6) {
                    FDMButton lastRemoveBtn = new FDMButton(removeBtnMessage);
                    parent.add(lastRemoveBtn, 1, rowCount - 2);
                    TextField lastTextField = (TextField) parent.getChildren().stream().filter(child -> (GridPane.getColumnIndex(child) == 0 &&
                                    Objects.equals(GridPane.getRowIndex(child), GridPane.getRowIndex(lastRemoveBtn))))
                            .toList().getLast();
                    addListenerTooRemoveBtn(lastTextField, lastRemoveBtn, parent, addBtn, textFields, addableTextFields);
                }
                parent.getChildren().remove(addBtn);
                parent.add(addBtn, 2, rowCount);

                addListenerTooRemoveBtn(textFieldToAdd, removeButton, parent, addBtn, textFields, addableTextFields);
                textFields.add(textFieldToAdd);
                addableTextFields.add(textFieldToAdd);
                createValidationForTextFields(predicate, textFields);
            }
        });
    }

    default void addListenerTooRemoveBtn(TextInputControl textFieldToRemove, javafx.scene.control.Button removeButton,
                                         GridPane gridPane, javafx.scene.control.Button addModuleBtn, ObservableList<TextInputControl> textFields, List<TextInputControl> addableTextFields) {
        removeButton.setOnAction(event -> {
            textFields.remove(textFieldToRemove);
            addableTextFields.remove(textFieldToRemove);
            gridPane.getChildren().removeAll(addModuleBtn, removeButton, textFieldToRemove);
            gridPane.add(addModuleBtn, 2, gridPane.getRowCount() - 1);
            //ToDo: bug with remove middle of 3
            if (gridPane.getChildren().size() <= 3 && gridPane.getChildren().size()>1) {
                Button rmvBtn = (Button) gridPane.getChildren().get(1);
                if (rmvBtn != null) gridPane.getChildren().remove(rmvBtn);
            }

        });
    }


    /**
     * Method to create validation for the given {@link ObservableList} of {@link TextInputControl}s. If validated creates a green Border around each {@link TextInputControl},
     * a red one otherwise.
     *
     * @param predicate  {@link Predicate}&lt;String&gt; to validate the text of a {@link TextInputControl} by.
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
     *
     * @param predicate  {@link Predicate}&lt;String&gt; to validate the text of a {@link TextInputControl} by.
     * @param textFields {@link ObservableList} of {@link TextInputControl}s that should be validated.
     * @param message    {@link String}-message shown by the {@link Tooltip} if a field is not validated.
     */
    default void createValidationForTextFields(Predicate<String> predicate, ObservableList<TextInputControl> textFields, String message) {
        validationHelper(predicate, textFields, message);
    }

    /**
     * Creates the {@link GridPane} with addable and removable {@link TextField}s from model
     * (eg: {@link com.fdmgroup.cvgeneratorgradle.models.Experience}, {@link com.fdmgroup.cvgeneratorgradle.models.Education} etc.)
     *
     * @param keyProperty        List of {@link TextInputControl}s
     * @param gridPane           {@link GridPane}-wrapper
     * @param addBtn             {@link Button} to add more TextInputControls
     * @param textFields         {@link ObservableList}&lt;TextInputControls&gt; of validatable TextInputControls
     * @param forFutureReference emulating template limits for when those will be implemented
     */
    default void createAddableAreaFromModel(List<TextInputControl> keyProperty, GridPane gridPane, Button addBtn,
                                            ObservableList<TextInputControl> textFields,
                                            String forFutureReference, String removeButtonMsg, String textFieldPromptMsg) {
        if (keyProperty.isEmpty()) {
            TextField newTextField = new TextField();
            newTextField.setPromptText("Key skill");
            newTextField.setId("DefaultKeyProperty");
            gridPane.add(newTextField, 0, 0);
            gridPane.add(addBtn, 2, 0);
            textFields.add(newTextField);
            keyProperty.add(newTextField);
            createAddableArea(gridPane, textFields, addBtn, removeButtonMsg, textFieldPromptMsg,
                    (string -> !string.matches("^.*[a-zA-Z]+.*$")), forFutureReference, keyProperty);
        } else {
            keyProperty.forEach(textField -> {
                if (textField == keyProperty.getFirst() && textField != keyProperty.getLast()) {
                    Button removeBtn = new Button(removeButtonMsg);
                    gridPane.add(textField, 0, 0);
                    gridPane.add(removeBtn, 1, 0);
                    addListenerTooRemoveBtn(textField, removeBtn, gridPane, addBtn, textFields, keyProperty);
                } else if (textField == keyProperty.getLast() && textField != keyProperty.getFirst()) {
                    int rowCount = gridPane.getRowCount();
                    Button removeBtn = new Button(removeButtonMsg);
                    gridPane.add(textField, 0, rowCount);
                    gridPane.add(removeBtn, 1, rowCount);
                    gridPane.add(addBtn, 2, rowCount);
                    addListenerTooRemoveBtn(textField, removeBtn, gridPane, addBtn, textFields, keyProperty);
                } else if (textField == keyProperty.getFirst()) {
                    int rowCount = gridPane.getRowCount();
                    gridPane.add(textField, 0, rowCount);
                    gridPane.add(addBtn, 2, rowCount);
                } else {
                    int rowCount = gridPane.getRowCount();
                    Button removeBtn = new Button(removeButtonMsg);
                    gridPane.add(textField, 0, rowCount);
                    gridPane.add(removeBtn, 1, rowCount);
                    addListenerTooRemoveBtn(textField, removeBtn, gridPane, addBtn, textFields, keyProperty);
                }
                createAddableArea(gridPane, textFields, addBtn, removeButtonMsg, textFieldPromptMsg,
                        (string -> !string.matches("^.*[a-zA-Z]+.*$")), forFutureReference, keyProperty);
            });
        }
    }
}
