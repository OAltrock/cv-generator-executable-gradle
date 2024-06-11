package com.fdmgroup.cvgeneratorgradle.interfaces;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import com.fdmgroup.cvgeneratorgradle.models.Language;
import com.fdmgroup.cvgeneratorgradle.models.enums.LanguageLevel;
import com.fdmgroup.cvgeneratorgradle.views.FDMButton;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;


public interface HasAddableTextFields {

    /**
     * Adds functionality to the add-Button which creates a new row within the given GridPane. This row contains a {@link TextField}, a remove-Button and moves the
     * add-Button to the newly created row.
     * It also adds functionality to the created remove-Button to remove the row the Button is in and if applicable, place the add-Button in the new bottom row.
     *
     * @param parent           Parent {@link GridPane} to the given add-Button.
     * @param textFields       {@link ObservableList} of {@link TextInputControl}s to which the TextFields, newly created by the add-Button should be
     *                         added or removed if removed by the remove-Button. These will be bound to the disableProperty of the save-Button(see: {@link HasToggleableSaveButtons})
     * @param addBtn           Add {@link Button} to which the new functionality should be added.
     * @param removeBtnMessage {@link String}-message for the newly created remove-{@link Button}.
     * @param promptMessage    {@link String}-message for the prompt of the newly created {@link TextField}.
     * @param predicate        {@link Predicate}&lt;String&gt; to validate the newly created {@link TextField}.
     * @param limit            limit for adding Modules
     */
    default void createAddableArea(GridPane parent, ObservableList<TextInputControl> textFields,
                                   Button addBtn, boolean isLanguageGridPane, String removeBtnMessage, String promptMessage, Predicate<String> predicate,
                                   int limit, List<TextInputControl> addableTextFields) {

        addBtn.setOnMousePressed(event -> {
            MenuButton languageLevelButton = null;
            if (parent.getChildren().size() / 2 + 1 <= limit) {
                javafx.scene.control.TextField textFieldToAdd = new javafx.scene.control.TextField();
                int rowCount = parent.getRowCount() + 1;
                textFieldToAdd.setStyle("-fx-pref-width: 300;");
                textFieldToAdd.setPromptText(promptMessage);
                textFieldToAdd.setId(String.valueOf(parent.getRowCount() + 1));
                FDMButton removeButton = new FDMButton(removeBtnMessage);
                removeButton.setDesign("primary");
                removeButton.setId(Integer.toString(rowCount));


                parent.add(textFieldToAdd, 0, rowCount);
                if (isLanguageGridPane) {
                    List<MenuItem> languageLevels = Arrays.stream(LanguageLevel.values()).map(languageLevel -> new MenuItem(languageLevel.toString())).toList();
                    languageLevelButton = new MenuButton("Choose language level");
                    languageLevelButton.getItems().addAll(languageLevels);
                    parent.add(languageLevelButton, 1, rowCount);
                    addListenerToLanguageLevelButton(languageLevelButton);
                }
                parent.add(removeButton, (isLanguageGridPane) ? 2 : 1, rowCount);

                //ToDo: maybe there is a better way (includes the whole system of adding and removing removeBtns)
                parent.getChildren().remove(addBtn);
                parent.add(addBtn, (isLanguageGridPane) ? 3 : 2, rowCount);
                if (parent.getChildren().size() >= 3 && parent.getChildren().size() < 6) {
                    FDMButton removeBottomBtn = new FDMButton(removeBtnMessage);
                    removeBottomBtn.setDesign("primary");
                    removeBottomBtn.setId(String.valueOf(rowCount - 2));
                    parent.add(removeBottomBtn, 1, rowCount - 2);
                    addListenerTooRemoveBtn((TextInputControl) parent.getChildren().getFirst(),null,removeBottomBtn,parent,addBtn,textFields,addableTextFields);
                }


                addListenerTooRemoveBtn(textFieldToAdd, languageLevelButton, removeButton, parent, addBtn, textFields, addableTextFields);
                textFields.add(textFieldToAdd);
                addableTextFields.add(textFieldToAdd);
                createValidationForTextFields(predicate, textFields);
            }
        });
    }

    /**
     * Adds listener to given {@link MenuButton} changing its text to the text of the {@link MenuItem} clicked.
     * @param levelButton MenuButton to add the listener to
     */
    default void addListenerToLanguageLevelButton(MenuButton levelButton) {
        levelButton.getItems().forEach(item -> {
            item.setOnAction(actionEvent -> {
                levelButton.setText(item.getText());
            });
        });
    }

    ;

    /**
     * Adds the remove-logic (ie: removes a before added {@link TextInputControl}) to the remove {@link Button}
     * @param textFieldToRemove TextInputControl to the corresponding remove-button {@code removeButton}
     * @param languageLevelButton {@link MenuButton} to choose a language level to the corresponding remove-button {@code removeButton}
     * @param removeButton remove-button to which the logic should be added
     * @param gridPane parent {@link GridPane} which contains all elements
     * @param addModuleBtn add-button that corresponds with given remove-button {@code removeButton}
     * @param textFields {@link ObservableList} of TextInputControls from which the given TextInputControl has to be removed since it no longer needs to be validated
     * @param addableTextFields {@link List} of TextInputControls form which the given TextInputControl needs to be removed since it no longer needs to be validated
     *                                      (this list contains exclusively addable TextInputControls)
     */
    default void addListenerTooRemoveBtn(TextInputControl textFieldToRemove, MenuButton languageLevelButton, javafx.scene.control.Button removeButton,
                                         GridPane gridPane, javafx.scene.control.Button addModuleBtn,
                                         ObservableList<TextInputControl> textFields, List<TextInputControl> addableTextFields) {
        removeButton.setOnMousePressed(event -> {
            textFields.remove(textFieldToRemove);
            addableTextFields.remove(textFieldToRemove);

            Node languageLvlBtn = gridPane.getChildren().get(gridPane.getChildren().indexOf(textFieldToRemove)+1);
            if (languageLvlBtn instanceof MenuButton)
                gridPane.getChildren().removeAll(addModuleBtn, languageLvlBtn, removeButton, textFieldToRemove);
            else gridPane.getChildren().removeAll(addModuleBtn, removeButton, textFieldToRemove);
            gridPane.add(addModuleBtn, (languageLevelButton !=null) ? 3 : 2, gridPane.getRowCount() - 1);

            if (gridPane.getChildren().size() <= 3 && gridPane.getChildren().size() > 1) {
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
            tooltipToAdd.textProperty().bind(Bindings.createStringBinding(()->{
                if (validInput.get()) return o;
                else return "";
            },textField.textProperty()));
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
     * @param keyProperty List of {@link TextInputControl}s
     * @param gridPane    {@link GridPane}-wrapper
     * @param addBtn      {@link Button} to add more TextInputControls
     * @param textFields  {@link ObservableList}&lt;TextInputControls&gt; of validatable TextInputControls
     * @param limit       template limit stored in {@link com.fdmgroup.cvgeneratorgradle.models.Location}.
     */
    default void createAddableAreaFromModel(List<TextInputControl> keyProperty, GridPane gridPane, Button addBtn,
                                            ObservableList<TextInputControl> textFields,
                                            int limit, String removeButtonMsg, String textFieldPromptMsg) {
        addableAreaHelper(keyProperty, gridPane, addBtn, false, textFields, limit, removeButtonMsg, textFieldPromptMsg, null);
    }

    //option to use an overloaded createAddableAreaFromModel()-method
    private void addableAreaHelper(List<TextInputControl> keyProperty, GridPane gridPane, Button addBtn, boolean isLanguageGrid, ObservableList<TextInputControl> textFields, int limit, String removeButtonMsg, String textFieldPromptMsg, CVTemplate cvTemplate) {

        addBtn.minWidth(80);
        addBtn.maxWidth(80);
        if (keyProperty.isEmpty()) {
            TextField newTextField = new TextField();
            newTextField.setPromptText(textFieldPromptMsg);
            newTextField.setId("bottom");
            gridPane.add(newTextField, 0, 0);
            if (isLanguageGrid) {
                MenuButton languageLevelButton = new MenuButton("Choose language level");
                languageLevelButton.getItems().addAll(Arrays.stream(LanguageLevel.values()).map(languageLevel -> new MenuItem(languageLevel.toString())).toList());
                gridPane.add(languageLevelButton, 1, 0);
                addListenerToLanguageLevelButton(languageLevelButton);
            }
            gridPane.add(addBtn, (isLanguageGrid) ? 3 : 2, 0);
            keyProperty.add(newTextField);
            createAddableArea(gridPane, textFields, addBtn, isLanguageGrid, removeButtonMsg, textFieldPromptMsg,
                    (string -> !string.matches("^.*[a-zA-Z]+.*$")), limit, keyProperty);
        } else {
            keyProperty.forEach(textField -> {
                if (textField == keyProperty.getFirst() && textField != keyProperty.getLast()) {
                    Button removeBtn = new Button(removeButtonMsg);
                    MenuButton languageLevelButton = null;
                    gridPane.add(textField, 0, 0);
                    if (isLanguageGrid) {
                        languageLevelButton = new MenuButton("Choose language level");
                        if (cvTemplate.getLanguages()!=null && !cvTemplate.getLanguages().isEmpty()) {
                            for (Language language : cvTemplate.getLanguages()) {
                                languageLevelButton.setText(language.getLanguageLevel().toString());
                            }
                        }
                        languageLevelButton.getItems().addAll(Arrays.stream(LanguageLevel.values()).map(languageLevel -> new MenuItem(languageLevel.toString())).toList());
                        addListenerToLanguageLevelButton(languageLevelButton);
                        gridPane.add(languageLevelButton, 1, 0);
                    }
                    removeBtn.minWidth(40);
                    removeBtn.maxWidth(80);
                    gridPane.add(removeBtn, (isLanguageGrid) ? 2 : 1, 0);
                    addListenerTooRemoveBtn(textField, languageLevelButton, removeBtn, gridPane, addBtn, textFields, keyProperty);
                } else if (textField == keyProperty.getLast() && textField != keyProperty.getFirst()) {
                    int rowCount = gridPane.getRowCount();
                    Button removeBtn = new Button(removeButtonMsg);
                    removeBtn.minWidth(40);
                    removeBtn.maxWidth(80);
                    gridPane.add(textField, 0, rowCount);
                    MenuButton languageLevelButton = null;
                    if (isLanguageGrid) {
                        languageLevelButton = new MenuButton("Choose language level");
                        languageLevelButton.getItems().addAll(Arrays.stream(LanguageLevel.values()).map(languageLevel -> new MenuItem(languageLevel.toString())).toList());
                        gridPane.add(languageLevelButton, 1, rowCount);
                    }
                    gridPane.add(removeBtn, (isLanguageGrid) ? 2 : 1, rowCount);
                    gridPane.add(addBtn, 2, rowCount);
                    addListenerTooRemoveBtn(textField, languageLevelButton, removeBtn, gridPane, addBtn, textFields, keyProperty);
                } else if (textField == keyProperty.getFirst()) {
                    MenuButton languageLevelButton = null;
                    int rowCount = gridPane.getRowCount();
                    gridPane.add(textField, 0, rowCount);
                    if (isLanguageGrid) {
                        languageLevelButton = new MenuButton("Choose language level");
                        languageLevelButton.getItems().addAll(Arrays.stream(LanguageLevel.values()).map(languageLevel -> new MenuItem(languageLevel.toString())).toList());
                        gridPane.add(languageLevelButton, 1, rowCount);
                    }
                    gridPane.add(addBtn, 2, rowCount);
                } else {
                    int rowCount = gridPane.getRowCount();
                    Button removeBtn = new Button(removeButtonMsg);
                    MenuButton languageLevelButton = null;
                    gridPane.add(textField, 0, rowCount);
                    if (isLanguageGrid) {
                        languageLevelButton = new MenuButton("Choose language level");
                        languageLevelButton.getItems().addAll(Arrays.stream(LanguageLevel.values()).map(languageLevel -> new MenuItem(languageLevel.toString())).toList());
                        gridPane.add(languageLevelButton, 1, rowCount);
                    }
                    gridPane.add(removeBtn, (isLanguageGrid) ? 2 : 1, rowCount);
                    addListenerTooRemoveBtn(textField, languageLevelButton, removeBtn, gridPane, addBtn, textFields, keyProperty);
                }
                createAddableArea(gridPane, textFields, addBtn, isLanguageGrid, removeButtonMsg, textFieldPromptMsg,
                        (string -> !string.matches("^.*[a-zA-Z]+.*$")), limit, keyProperty);
            });
        }
    }
}
