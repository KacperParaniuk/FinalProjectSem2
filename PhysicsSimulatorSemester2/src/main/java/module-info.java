module com.example.physicssimulatorsemester2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.physicssimulatorsemester2 to javafx.fxml;
    exports com.example.physicssimulatorsemester2;
}