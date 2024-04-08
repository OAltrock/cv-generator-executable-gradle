module com.fdmgroup.cvgeneratorgradle {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.fdmgroup.cvgeneratorgradle to javafx.fxml;
    opens com.fdmgroup.cvgeneratorgradle.controller to javafx.fxml;
    exports com.fdmgroup.cvgeneratorgradle;
}