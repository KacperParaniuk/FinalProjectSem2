package com.example.physicssimulatorsemester2;

import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;

public class Drawing {

    public void actionMainMenu(){
        try {
            HelloApplication.loadScene("MainMenu.fxml", "Physics Simulator", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void drawArrowHead(GraphicsContext gc, double x, double y, double angle){
        double size = 10; // size of arrow lines

        double angle1 = angle + Math.toRadians(150); // offset for lines making up the arrow
        double angle2 = angle - Math.toRadians(150);

//        System.out.println(Math.toDegrees(angle1) + "Angle 1");
//        System.out.println(Math.toDegrees(angle2) + "Angle 2");

        double x1 = x + size * Math.cos(angle1);
        double y1 = y + Math.abs(size * Math.sin(angle1));
        double x2 = x + size * Math.cos(angle2);
        double y2 = y + Math.abs(size * Math.sin(angle2));

        gc.strokeLine(x,y, x1,y1);
        gc.strokeLine(x,y,x2,y2);



    }

    public double roundToOneDecimalPlace(double val){
        return (Math.round(val * 10)) / 10.0;
    }




}
