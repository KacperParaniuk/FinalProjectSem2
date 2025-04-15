package com.example.physicssimulatorsemester2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainMenuController {


    public ToggleButton sandboxBtn, educationalBtn;

    public void initialize() {


    }

    public void launchProjectile(ActionEvent actionEvent) {
        try {
            HelloApplication.loadScene("ProjectileSim.fxml", "Projectile Simulator", isEducationalMode);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void launchPendulum(ActionEvent actionEvent) {
        try {
            HelloApplication.loadScene("PendulumSim.fxml", "Projectile Simulator", isEducationalMode);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void launchSpring(ActionEvent actionEvent) {
        try {
            HelloApplication.loadScene("SpringSim.fxml", "Projectile Simulator", isEducationalMode);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean isSandBoxMode = false;

    public void toggleSandBox(ActionEvent actionEvent) {
        isSandBoxMode = true;
        isEducationalMode = false;
        educationalBtn.setSelected(false);

    }

    private boolean isEducationalMode = false;

    public void toggleEducationalMode(ActionEvent actionEvent) {
        isSandBoxMode = false;
        isEducationalMode = true;
        sandboxBtn.setSelected(false);


    }
}
