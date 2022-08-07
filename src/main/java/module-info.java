module com.example.phase2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens Main to javafx.fxml;
    exports Main;
}