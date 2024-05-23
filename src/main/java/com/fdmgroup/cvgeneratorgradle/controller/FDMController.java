package com.fdmgroup.cvgeneratorgradle.controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

public abstract class FDMController {
    void validateMaybeEmptyTextFields(List<MenuButton> languageLevelBtns, GridPane parent) {
        //String message = "Please choose a language level";
        languageLevelBtns.forEach(btn -> {
            TextInputControl correspondingLanguage = (TextInputControl) parent.getChildren().get(parent.getChildren().indexOf(btn) - 1);
            //System.out.println(btn.textProperty());

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

    void validatePreviousBtn(List<MenuButton> languageLevelBtns, List<TextInputControl> languageInput, GridPane parent, Button... prevBtn) {
        languageLevelBtns.forEach(btn -> {
            TextInputControl correspondingLanguage = (TextInputControl) parent.getChildren().get(parent.getChildren().indexOf(btn) - 1);

            correspondingLanguage.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    BooleanBinding allLevelsSelected = languageInput.stream()
                            .map(TextInputControl::textProperty)
                            .map(stringProperty -> Bindings.createBooleanBinding(() -> ((stringProperty.get() != null &&
                                    !stringProperty.get().isEmpty()) && btn.getText().contains("Choose")),
                                    stringProperty, btn.textProperty()))
                            .reduce(Bindings::and)
                            .get();
                    BooleanBinding allLanguageLevelBtnsSelected = languageLevelBtns.stream()
                            .map(MenuButton::textProperty)
                            .map(stringProperty -> Bindings.createBooleanBinding(() -> (stringProperty.getValue().contains("Choose") &&
                                    (correspondingLanguage.getText() != null && !correspondingLanguage.getText().isEmpty())),
                                    stringProperty, correspondingLanguage.textProperty()))
                            .reduce(Bindings::and)
                            .get();
                    //ObservableBooleanValue languageLevelBinding = getObservableLanguageAndLanguageLvl(correspondingLanguage,btn).not();
                    Arrays.stream(prevBtn).forEach(btn -> btn.disableProperty()
                            .bind(allLevelsSelected.or(allLanguageLevelBtnsSelected)));
                }
            });
            /*if (correspondingLanguage.getText()!=null && !correspondingLanguage.getText().isEmpty()) {*/

                btn.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        BooleanBinding allLevelsSelected = languageLevelBtns.stream()
                                .map(MenuButton::textProperty)
                                .map(stringProperty -> Bindings.createBooleanBinding(() -> (stringProperty.getValue().contains("Choose") &&
                                                (correspondingLanguage.getText() != null && !correspondingLanguage.getText().isEmpty())),
                                        stringProperty, correspondingLanguage.textProperty()))
                                .reduce(Bindings::and)
                                .get();
                        BooleanBinding allLanguageLevelBtnsSelected = languageLevelBtns.stream()
                                .map(MenuButton::textProperty)
                                .map(stringProperty -> Bindings.createBooleanBinding(() -> (stringProperty.getValue().contains("Choose") &&
                                        (correspondingLanguage.getText() != null && !correspondingLanguage.getText().isEmpty())),
                                        stringProperty, correspondingLanguage.textProperty()))
                                .reduce(Bindings::and)
                                .get();
                        Arrays.stream(prevBtn).forEach(btn -> btn.disableProperty()
                                .bind(allLevelsSelected.or(allLanguageLevelBtnsSelected)));
                    }
                });
            /*}*/
        });

        /*languageInput.forEach(languageInputField -> {
            MenuButton correspondingLanguageLevelBtn = (MenuButton) parent.getChildren().get(parent.getChildren().indexOf(languageInputField)+1);

            correspondingLanguageLevelBtn.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    BooleanBinding languageLevelBinding = getBooleanBinding(languageInput,correspondingLanguageLevelBtn);
                    prevBtn.disableProperty().bind(languageLevelBinding);
                }
            });

            languageInputField.textProperty().addListener(new ChangeListener<String>() {
                //MenuButton correspondingMenuBtn = (MenuButton) parent.getChildren().get(parent.getChildren().indexOf(languageInputField)+1);
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    BooleanBinding languageLevelBinding = getBooleanBinding(languageInput, correspondingLanguageLevelBtn);
                    prevBtn.disableProperty().bind(languageLevelBinding);
                }
            });
        });*/

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
