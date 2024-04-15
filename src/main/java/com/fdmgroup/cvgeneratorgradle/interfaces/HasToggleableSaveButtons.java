package com.fdmgroup.cvgeneratorgradle.interfaces;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface HasToggleableSaveButtons {

    default void addValidationToSaveButtons(ObservableList<TextInputControl> list, Predicate<String> checkText, Button saveBtn, DatePicker start,
                                            DatePicker end, BiPredicate<LocalDate, LocalDate> checkDate, CheckBox ongoing) {
        end.valueProperty().addListener(new ChangeListener<LocalDate>() {
                                            @Override
                                            public void changed(ObservableValue<? extends LocalDate> observableValue, LocalDate localDate, LocalDate t1) {
                                                BooleanBinding allVboxFieldsValidated = list.stream()
                                                        .map(TextInputControl::textProperty)
                                                        .map(stringProperty -> Bindings.createBooleanBinding(() -> checkText.test(stringProperty.get()), stringProperty))
                                                        .reduce(Bindings::or)
                                                        .get();
                                                BooleanBinding datesCorrect = Bindings.createBooleanBinding(() -> checkDate.test(start.getValue(), end.getValue()), end.valueProperty()).not();
                                                saveBtn.disableProperty().bind(allVboxFieldsValidated.or(datesCorrect));
                                            }
                                        }
        );
        list.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                BooleanBinding allVboxFieldsValidated = list.stream()
                        .map(TextInputControl::textProperty)
                        .map(stringProperty -> Bindings.createBooleanBinding(() -> checkText.test(stringProperty.get()), stringProperty))
                        .reduce(Bindings::or)
                        .get();
                BooleanBinding datesCorrect = Bindings.createBooleanBinding(() -> checkDate.test(start.getValue(), end.getValue()), start.valueProperty(), end.valueProperty()).not();
                saveBtn.disableProperty().bind(allVboxFieldsValidated.or(datesCorrect));
            }
        });

        start.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observableValue, LocalDate localDate, LocalDate t1) {
                BooleanBinding allVboxFieldsValidated = list.stream()
                        .map(TextInputControl::textProperty)
                        .map(stringProperty -> Bindings.createBooleanBinding(() -> checkText.test(stringProperty.get()), stringProperty))
                        .reduce(Bindings::or)
                        .get();
                BooleanBinding datesCorrect = Bindings.createBooleanBinding(() -> checkDate.test(start.getValue(), end.getValue()), end.valueProperty()).not();
                saveBtn.disableProperty().bind(allVboxFieldsValidated.or(datesCorrect));
            }
        });

        ongoing.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                BooleanBinding allVboxFieldsValidated = list.stream()
                        .map(TextInputControl::textProperty)
                        .map(stringProperty -> Bindings.createBooleanBinding(() -> checkText.test(stringProperty.get()), stringProperty))
                        .reduce(Bindings::or)
                        .get();
                BooleanBinding datesCorrect = Bindings.createBooleanBinding(() -> checkDate.test(start.getValue(), end.getValue()), end.valueProperty()).not();
                end.disableProperty().bind(ongoing.selectedProperty());
                saveBtn.disableProperty().bind(allVboxFieldsValidated.or(datesCorrect));
            }
        });

    }

    default void addValidationToSaveButtons(ObservableList<TextInputControl> list, Predicate<String> checkText, Button saveBtn) {
        list.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                BooleanBinding validInput = validateTextFields(list, checkText);
                saveBtn.disableProperty().bind(validInput.not());
            }
        });


    };

    private BooleanBinding validateTextFields(ObservableList<TextInputControl> list, Predicate<String> checkText) {
        return list.stream()
                .map(TextInputControl::textProperty)
                .map(stringProperty -> Bindings.createBooleanBinding(() -> checkText.test(stringProperty.get()), stringProperty))
                .reduce(Bindings::or)
                .get();

    }
}
