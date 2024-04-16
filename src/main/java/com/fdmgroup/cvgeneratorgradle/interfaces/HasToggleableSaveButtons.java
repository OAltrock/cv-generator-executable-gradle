package com.fdmgroup.cvgeneratorgradle.interfaces;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.function.BiPredicate;
import java.util.function.Predicate;


public interface HasToggleableSaveButtons {

    /**
     * Binds the disableProperty of the given {@link  Button} to the validation of a given {@link ObservableList} of {@link TextInputControl}s, disabling it
     * when not all {@link  TextInputControl}s are validated.
     * @param list {@link ObservableList} of {@link TextInputControl}s
     * @param checkText {@link Predicate} to validate given {@link  TextInputControl}s by
     * @param saveBtn {@link Button} that should be disabled when not all given {@link TextInputControl}s are validated
     */
    default void addValidationToSaveButtons(ObservableList<TextInputControl> list, Predicate<String> checkText, Button saveBtn) {
        list.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                BooleanBinding validInput = validateTextFields(list, checkText);
                saveBtn.disableProperty().bind(validInput.not());
            }
        });
    }

    /**
     * Overloaded method to bind the given Button to the validation of a list of TextInputControls {@link TextInputControl}, two datePickers and a checkbox
     * @param list observable list {@link ObservableList} of TextInputControls {@link TextInputControl}
     * @param checkText predicate {@link Predicate} to validate the text of TextInputControls by
     * @param saveBtn Button {@link Button} that should be disabled with invalid input
     * @param start DatePicker {@link DatePicker} representing the start date of an occupation
     * @param end DatePicker representing the end date of an occupation
     * @param checkDate BiPredicate &lt;LocalDate, LocalDate&gt; {@link LocalDate} {@link BiPredicate} to validate the given start and end date by.
     * @param ongoing CheckBox {@link CheckBox} representing an ongoing occupation therefore ignoring the end-date-picker if checked
     */
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
                BooleanBinding allVboxFieldsValidated = validateTextFields(list,checkText);
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

    //creates BooleanBindings since both default and overloaded method using them
    private BooleanBinding validateTextFields(ObservableList<TextInputControl> list, Predicate<String> checkText) {
        return list.stream()
                .map(TextInputControl::textProperty)
                .map(stringProperty -> Bindings.createBooleanBinding(() -> checkText.test(stringProperty.get()), stringProperty))
                .reduce(Bindings::or)
                .get();

    }
}
