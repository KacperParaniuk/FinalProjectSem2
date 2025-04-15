package com.example.physicssimulatorsemester2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class HelloApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        loadScene("MainMenu.fxml", "Physics Simulator", false);
    }

    public static void loadScene(String fxmlFile, String title, boolean educationalMode) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
        Parent root = loader.load();

        if(fxmlFile.equals("ProjectileSim.fxml")) {
            ProjectileSimController controller = loader.getController();
            controller.setEducationalMode(educationalMode);
        }
        else if (fxmlFile.equals("PendulumSim.fxml")) {
            PendulumSimController controller = loader.getController();
            controller.setEducationalMode(educationalMode);
        }
        else if (fxmlFile.equals("SpringSim.fxml")) {
            SpringSimController controller = loader.getController();
            controller.setEducationalMode(educationalMode);
        }



        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
