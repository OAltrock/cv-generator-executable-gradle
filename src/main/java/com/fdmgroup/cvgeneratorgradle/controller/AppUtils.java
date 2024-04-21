package com.fdmgroup.cvgeneratorgradle.controller;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextInputControl;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AppUtils {

    public static List<TextInputControl> findAllTextFields (Parent parent) {
        List<Node> uncheckedTextFields = filterNodeClass(parent,(string -> string.contains("TextField") || string.contains("TextArea")));
        return uncheckedTextFields.stream().map(textField -> (TextInputControl) textField).toList();
    }

    private static List<Node> filterNodeClass(Parent parent, Predicate<String> checkText) {
        return new ArrayList<>(parent.getChildrenUnmodifiable().stream().filter(child -> checkText.test(child.getClass().toString())).toList());
    }

}
