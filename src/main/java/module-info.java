module com.xcoders {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.xcoders            to javafx.fxml;
    opens com.xcoders.controller to javafx.fxml;
    opens com.xcoders.model      to javafx.base;   // needed for PropertyValueFactory reflection

    exports com.xcoders;
}
