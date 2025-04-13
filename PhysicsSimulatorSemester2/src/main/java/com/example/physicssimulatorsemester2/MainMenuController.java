package com.example.physicssimulatorsemester2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class MainMenuController {
    public ImageView imgViewPendulum, imgViewProjectileMotion, imgViewSpring;


    public Images getImages = new Images();

    public void initialize(){
        imgViewPendulum.setImage((getImages.getPendulumPicture()));
        imgViewProjectileMotion.setImage((getImages.getProjectileMotionPicture()));
        imgViewSpring.setImage((getImages.getSpringPicture()));


    }

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
