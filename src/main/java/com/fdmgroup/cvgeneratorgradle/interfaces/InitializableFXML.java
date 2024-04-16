package com.fdmgroup.cvgeneratorgradle.interfaces;

import com.fdmgroup.cvgeneratorgradle.CVGeneratorApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public interface InitializableFXML {


    //ToDo: making the app dynamically load pages, would make it necessary to abandon fxml which would make this interface superfluous
    /**
     * Convenience method to inject a FXML-file into the center of the main BorderPane.
     * This is also circumventing the use of the FXML Initializable {@link javafx.fxml.Initializable} initialize method, which would prevent controller classes from having constructors.
     * @param main main BorderPane {@link BorderPane}
     * @param resource FXML file to inject as String {@link FXMLLoader}
     */
    @FXML
    default void initialize(BorderPane main, String resource) {
        FXMLLoader fxmlLoader = new FXMLLoader(CVGeneratorApp.class.getResource(resource+".fxml"));
        try {
            main.setCenter(fxmlLoader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
