module com.example.javafx_tests {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens Application to javafx.fxml;
    exports Application;
    exports Application.Controller;
    opens Application.Controller to javafx.fxml;
}