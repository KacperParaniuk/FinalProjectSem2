package com.example.physicssimulatorsemester2;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Images {



    public Image getProjectileMotionPicture(){
        try {
            return new Image(new FileInputStream("src/main/java/resources/Pictures/projectileMotion.png"));
        }
        catch (FileNotFoundException e) {

            e.printStackTrace();

        }
        return null;

    }

    public Image getPendulumPicture(){
        try {
            return new Image(new FileInputStream("src/main/java/resources/Pictures/pendulum.png"));
        }
        catch (FileNotFoundException e) {

            e.printStackTrace();

        }
        return null;


    }

    public Image getSpringPicture(){
        try {
            return new Image(new FileInputStream("src/main/java/resources/Pictures/spring.png"));
        }
        catch (FileNotFoundException e) {

            e.printStackTrace();

        }
        return null;



    }



}
