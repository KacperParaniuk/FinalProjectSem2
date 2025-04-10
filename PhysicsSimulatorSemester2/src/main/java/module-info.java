module com.example.physicssimulatorsemester2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.physicssimulatorsemester2 to javafx.fxml;
    exports com.example.physicssimulatorsemester2;
}