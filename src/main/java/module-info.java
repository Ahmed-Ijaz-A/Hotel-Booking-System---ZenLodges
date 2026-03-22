module com.xcoders {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires transitive java.sql;

    opens com.xcoders            to javafx.fxml;
    opens com.xcoders.controller to javafx.fxml;
    opens com.xcoders.model      to javafx.base;   // needed for PropertyValueFactory reflection

    exports com.xcoders;
    exports com.xcoders.model;
}
