package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.models.CVTemplate;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;

import javafx.scene.control.*;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FDMController {
    CVTemplate cvTemplate;
    Stage stage;
    Menu recent;
    TreeView<String> treeView;
    Map<MenuButton, TextInputControl> correspondingMap = new HashMap<>();

    abstract void assignInput(MainController mainController);

    abstract void initialize(BorderPane main, MainController mainController);


    /**
     * Validates language level buttons {@link MenuButton} by giving them a red border if the corresponding language input isn't empty{@link TextInputControl} and no language level is chosen, or
     * a level is chosen but the corresponding language input is empty. Otherwise, it is given a green border.
     * (corresponding language input will not be validated)
     * @param languageLevelBtns {@link MenuButton}s containing {@link com.fdmgroup.cvgeneratorgradle.models.enums.LanguageLevel} as {@link javafx.scene.control.MenuItem}s
     * @param parent parent {@link GridPane} containing language input TextInputControls and language level MenuButtons
     */
    void validateMaybeEmptyTextFields(List<MenuButton> languageLevelBtns, GridPane parent) {
        languageLevelBtns.forEach(btn -> {
            TextInputControl correspondingLanguage = (TextInputControl) parent.getChildren().get(parent.getChildren().indexOf(btn) - 1);

            correspondingLanguage.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    ObjectBinding<Border> languageLevelBorderBinding = getLanguageAndLanguageLevelBinding(correspondingLanguage, btn);
                    btn.borderProperty().bind(languageLevelBorderBinding);
                }
            });

            btn.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    ObjectBinding<Border> languageLevelBorderBinding = getLanguageAndLanguageLevelBinding(correspondingLanguage, btn);
                    btn.borderProperty().bind(languageLevelBorderBinding);
                }
            });
        });


    }

    private static ObjectBinding<Border> getLanguageAndLanguageLevelBinding(TextInputControl correspondingLanguage, MenuButton btn) {
        return Bindings.createObjectBinding(() -> {
                    ObservableBooleanValue notNull = getObservableLanguageAndLanguageLvl(correspondingLanguage, btn);
                    if (!notNull.get())
                        return new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
                    else
                        return new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
                }, btn.textProperty()
        );
    }

    private static BooleanBinding getObservableLanguageAndLanguageLvl(TextInputControl correspondingLanguage, MenuButton btn) {
        return Bindings.createBooleanBinding(() ->
                        (correspondingLanguage.getText() != null && !correspondingLanguage.getText().isEmpty())
                                && !btn.getText().contains("Choose"), correspondingLanguage.textProperty(),
                btn.textProperty());
    }

    /**
     * Ties the disable-property (ie:whether the button(s) can be clicked) of the given {@link Button}s to the condition, that if the language input {@link TextInputControl}
     * isn't empty the corresponding language level {@link MenuButton} must be selected.
     * @param languageLevelBtns {@link List} of MenuButtons that combined with the corresponding language input {@code languageInput} are considered for validating the given buttons {@code prevBtn}
     * @param languageInput List of TextInputControls that combined with the corresponding language level buttons {@code languageLevelBtns} are considered for validating the given buttons {@code prevBtn}
     * @param parent parent {@link GridPane} that contains {@code languageLevelBtns} and {@code languageInput}
     * @param prevBtn Button(s) that should be enabled/disabled
     */
    void validatePreviousBtn(List<MenuButton> languageLevelBtns, List<TextInputControl> languageInput, GridPane parent, Button... prevBtn) {
        languageLevelBtns.forEach(btn -> {
            TextInputControl correspondingLanguage = (TextInputControl) parent.getChildren().get(parent.getChildren().indexOf(btn) - 1);

            correspondingLanguage.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    languageInput.forEach(textInputControl -> {
                        correspondingMap.put((MenuButton) parent.getChildren().get(parent.getChildren().indexOf(textInputControl) + 1), textInputControl);
                    });
                    BooleanBinding validateLanguages = correspondingMap.entrySet().stream().map(entry ->
                                    Bindings.createBooleanBinding(() ->
                                            (entry.getKey().getText().contains("Choose") && (entry.getValue().getText()!=null && !entry.getValue().getText().isEmpty()))
                                    ))
                            .reduce(Bindings::or)
                            .get();
                    Arrays.stream(prevBtn).forEach(btn -> btn.disableProperty()
                            .bind(validateLanguages));
                }
            });
            btn.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    languageInput.forEach(textInputControl -> {
                        correspondingMap.put((MenuButton) parent.getChildren().get(parent.getChildren().indexOf(textInputControl) + 1), textInputControl);
                    });
                    BooleanBinding validateLanguages = correspondingMap.entrySet().stream().map(entry ->
                                    Bindings.createBooleanBinding(() ->
                                            (entry.getKey().getText().contains("Choose") && (entry.getValue().getText()!=null && !entry.getValue().getText().isEmpty()))
                                    ))
                            .reduce(Bindings::or)
                            .get();
                    Arrays.stream(prevBtn).forEach(btn -> btn.disableProperty()
                            .bind(validateLanguages));
                }
            });
        });

    }

    private static BooleanBinding getBooleanBinding(List<TextInputControl> languageInput, MenuButton btn) {
        return languageInput.stream()
                .map(TextInputControl::textProperty)
                .map(stringProperty ->
                        Bindings.createBooleanBinding(() ->
                                ((stringProperty.get() != null && !stringProperty.get().isEmpty())
                                        && btn.getText().contains("Choose")), stringProperty, btn.textProperty()))
                .reduce(Bindings::or)
                .get();
    }


}
