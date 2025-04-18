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

    public void drawArrowhead(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double arrowLength = 10;
        double arrowAngle = Math.toRadians(20);

        double x3 = x2 - arrowLength * Math.cos(angle - arrowAngle);
        double y3 = y2 - arrowLength * Math.sin(angle - arrowAngle);
        double x4 = x2 - arrowLength * Math.cos(angle + arrowAngle);
        double y4 = y2 - arrowLength * Math.sin(angle + arrowAngle);

        gc.strokeLine(x2, y2, x3, y3);
        gc.strokeLine(x2, y2, x4, y4);
    }


    public double roundToOneDecimalPlace(double val){
        return (Math.round(val * 10)) / 10.0;
    }




}
