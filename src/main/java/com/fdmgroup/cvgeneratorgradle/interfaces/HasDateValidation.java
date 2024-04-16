package com.fdmgroup.cvgeneratorgradle.interfaces;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.function.BiPredicate;


public interface HasDateValidation {

    /**
     * Binds the BorderProperty of a Region {@link Region} which is wrapping the given two DatePickers and a CheckBox to the validation of said Nodes {@link javafx.scene.Node},
     * making it green if all are validated and red otherwise.
     * @param start DatePicker {@link DatePicker} representing a start date of an occupation
     * @param end DatePicker representing an end date of an occupation
     * @param checkDate BiPredicate &lt;LocalDate, LocalDate&gt; {@link LocalDate} {@link BiPredicate} to validate the given start and end date by.
     * @param ongoing CheckBox {@link CheckBox} representing an ongoing occupation therefore ignoring the end-date-picker if checked
     */
    default void addValidationToDates(DatePicker start, DatePicker end, BiPredicate<LocalDate, LocalDate> checkDate, CheckBox ongoing) {

        ObservableBooleanValue validInput = Bindings.createBooleanBinding(() -> checkDate.test(start.getValue(), end.getValue()), start.valueProperty(), ongoing.selectedProperty(), end.valueProperty());
        ObjectBinding<Border> startBorderBinding = Bindings.createObjectBinding(() -> {
                    if (start.valueProperty().isNull().get())
                        return new Border(new BorderStroke(javafx.scene.paint.Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
                    else
                        return new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
                }, start.valueProperty()
        );

        ObjectBinding<Border> endBorderBinding = Bindings.createObjectBinding(() -> {
                    if (!validInput.get())
                        return new Border(new BorderStroke(javafx.scene.paint.Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
                    else
                        return new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
                }, end.valueProperty(), ongoing.selectedProperty()
        );
        Tooltip startTooltipToAdd = new Tooltip();
        Tooltip endTooltipToAdd = new Tooltip();

        ObjectBinding<Tooltip> tooltipStartBinding = Bindings.createObjectBinding(() -> {
            if (!validInput.get()) {
                if (start.valueProperty().isNull().get()) {
                    startTooltipToAdd.setText("Enter a date");
                } else if (start.getValue().isAfter(LocalDate.now())) {
                    startTooltipToAdd.setText("Enter a start date before now");
                } else if (!end.valueProperty().isNull().get() && end.getValue().isBefore(start.getValue())) {
                        startTooltipToAdd.setText("Enter an end date after the start date");
                }
                return startTooltipToAdd;
            }
            return null;
        }, start.valueProperty(), end.valueProperty(), ongoing.selectedProperty());

        ObjectBinding<Tooltip> tooltipEndBinding = Bindings.createObjectBinding(() -> {
            if (!validInput.get()) {
                if (end.valueProperty().isNull().get()) {
                    endTooltipToAdd.setText("Enter a date");
                }
                else if (end.getValue().isAfter(LocalDate.now())) {
                    endTooltipToAdd.setText("Use the ongoing check box");
                }
                else if (!start.valueProperty().isNull().get() && end.getValue().isBefore(start.getValue())) {
                    endTooltipToAdd.setText("Enter an end date after the start date");
                }
                return endTooltipToAdd;
            }
            return null;
        }, start.valueProperty(), end.valueProperty());

        ObjectBinding<Border> dateBorderBinding = Bindings.createObjectBinding(() -> {
                    if (!validInput.get())
                        return new Border(new BorderStroke(javafx.scene.paint.Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
                    else
                        return new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
                }, start.valueProperty(), ongoing.selectedProperty(),  end.valueProperty()
        );

        Region hBox = (Region) start.getParent();
        hBox.borderProperty().bind(dateBorderBinding);

        start.tooltipProperty().

                bind(tooltipStartBinding);
        end.tooltipProperty().

                bind(tooltipEndBinding);
        Tooltip.install(start, startTooltipToAdd);
        Tooltip.install(end, endTooltipToAdd);
        start.borderProperty().

                bind(startBorderBinding);
        end.borderProperty().

                bind(endBorderBinding);
    }
}
