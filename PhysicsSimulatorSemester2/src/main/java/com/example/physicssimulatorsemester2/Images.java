package com.example.physicssimulatorsemester2;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Images {



    public Image getProjectileMotionPicture(){

        try {
            return new Image(new FileInputStream("src/main/resources/Pictures/projectileMotion.PNG"));
        }
        catch (FileNotFoundException e) {

            e.printStackTrace();

        }
        return null;

    }

    public Image getPendulumPicture(){
        try {
            return new Image(new FileInputStream("src/main/resources/Pictures/pendulum.png"));
        }
        catch (FileNotFoundException e) {

            e.printStackTrace();

        }
        return null;


    }

    public Image getSpringPicture(){
        try {
            Image p = new Image(new FileInputStream("src/main/resources/Pictures/spring.PNG"));
            return p;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();

        }
        return null;



    }



}
