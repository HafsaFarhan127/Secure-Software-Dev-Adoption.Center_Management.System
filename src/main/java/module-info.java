module com.example.testing101 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.testing101 to javafx.fxml;
    exports com.example.testing101;
}