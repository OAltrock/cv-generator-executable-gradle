package com.fdmgroup.cvgeneratorgradle.controller;

import com.fdmgroup.cvgeneratorgradle.interfaces.HasAddableTextFields;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FDMControllerTest {
    private FDMController fdmController;
    private HasAddableTextFields hasAddableTextFields;
    //creates an app; necessary to run the tests although seemingly not used.
    private JFXPanel panel = new JFXPanel();

    MenuButton menuButton1;
    List<MenuButton> menuButtonList;
    TextField textField;
    GridPane parent;
    final Border borderValid = new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));
    final Border borderInvalid = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));


    @BeforeEach
    void setUp() {
        fdmController = new FDMController() {
            //abstract method implementations need to be tested with framework (TestFX)
            @Override
            void assignInput(MainController mainController) {}

            @Override
            void initialize(BorderPane main, MainController mainController) {}
        };
        menuButton1 = new MenuButton();
        menuButtonList = new ArrayList<>();
        textField = new TextField();
        parent = new GridPane();
        parent.add(textField,0,0);
        parent.add(menuButton1,1,0);
    }

    @Test
    void testValidateMaybeEmptyTextFields() {
        menuButtonList.add(menuButton1);
        fdmController.validateMaybeEmptyTextFields(menuButtonList,parent);



        menuButton1.setText("Choose");
        textField.setText("test");
        assertEquals(borderInvalid.getStrokes().getFirst().getBottomStroke(), menuButton1.getBorder().getStrokes().getFirst().getBottomStroke());

        menuButton1.setText("A1");
        textField.setText("test");
        assertEquals(borderValid.getStrokes().getFirst().getBottomStroke(), menuButton1.getBorder().getStrokes().getFirst().getBottomStroke());

        menuButton1.setText("A1");
        textField.setText(null);
        assertEquals(borderInvalid.getStrokes().getFirst().getBottomStroke(), menuButton1.getBorder().getStrokes().getFirst().getBottomStroke());

    }

    @Test
    void testValidatePreviousBtn() {
        MenuButton menuButton2 = new MenuButton();
        TextField textField2 = new TextField();
        Button prevBtn = new Button();

        menuButtonList.add(menuButton1);
        menuButtonList.add(menuButton2);
        List<TextInputControl> textInputControlList = List.of(textField, textField2);

        parent.add(textField2,0,1);
        parent.add(menuButton2,1,1);

        fdmController.validatePreviousBtn(menuButtonList,textInputControlList,parent,prevBtn);

        //a field has a language but no corresponding level. this should disable the button.
        textField.setText("test");
        menuButton1.setText("Choose");
        assertTrue(prevBtn.disableProperty().get());

        //a field has a language and a corresponding level. this shouldn't disable the button.
        textField.setText("test");
        menuButton1.setText("A1");
        assertFalse(prevBtn.disableProperty().get());

        //a field has no language and no corresponding level. this shouldn't disable the button.
        textField.setText("");
        menuButton1.setText("");
        assertFalse(prevBtn.disableProperty().get());

        //one field has language and corresponding level, while the other has no input.
        //this shouldn't disable the button.
        textField.setText("test");
        menuButton1.setText("A1");
        textField2.setText("");
        menuButton2.setText("Choose");
        assertFalse(prevBtn.disableProperty().get());

        //one field has both a language and a corresponding level but another field has
        //a language with no level. this should disable the button.
        textField.setText("test");
        menuButton1.setText("A1");
        textField2.setText("test");
        menuButton2.setText("Choose");
        assertTrue(prevBtn.disableProperty().get());

        //one field has both a language and a corresponding level, while the other has a level
        //but no corresponding language input. this shouldn't disable the button.
        textField.setText("test");
        menuButton1.setText("A1");
        textField2.setText("");
        menuButton2.setText("A1");
        assertFalse(prevBtn.disableProperty().get());
    }

    @Test
    void testCreateValidationForTextFields() {
        hasAddableTextFields = new HasAddableTextFields(){};
        TextField textField2 = new TextField();
        ObservableList<TextInputControl> textFields = FXCollections.observableArrayList(textField, textField2);


        hasAddableTextFields.createValidationForTextFields(input-> !input.matches("^.*[a-zA-Z]+.*$") ,textFields);

        //textField with no input should be invalid (have a red border).
        textField.setText("");
        assertEquals(borderInvalid.getStrokes().getFirst().getBottomStroke(), textField.getBorder().getStrokes().getFirst().getBottomStroke());

        //textField with input that contains at least a letter should be valid (have a green border).
        textField.setText("test");
        assertEquals(borderValid.getStrokes().getFirst().getBottomStroke(), textField.getBorder().getStrokes().getFirst().getBottomStroke());

        //textField with input that does not contain at least one letter should be invalid (have a red border).
        textField.setText("123");
        assertEquals(borderInvalid.getStrokes().getFirst().getBottomStroke(), textField.getBorder().getStrokes().getFirst().getBottomStroke());
    }
}