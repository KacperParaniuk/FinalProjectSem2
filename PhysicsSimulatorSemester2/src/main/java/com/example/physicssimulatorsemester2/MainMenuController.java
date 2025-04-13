package com.example.physicssimulatorsemester2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class MainMenuController {





    public void launchProjectile(ActionEvent actionEvent) {
        try {
            HelloApplication.loadScene("ProjectileSim.fxml", "Projectile Simulator");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void launchPendulum(ActionEvent actionEvent) {
        try {
            HelloApplication.loadScene("PendulumSim.fxml", "Projectile Simulator");
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void launchSpring(ActionEvent actionEvent) {
        try {
            HelloApplication.loadScene("SpringSim.fxml", "Projectile Simulator");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
