package com.example.physicssimulatorsemester2;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


import java.awt.*;
import java.io.IOException;

public class SpringSimController extends Drawing{
    public Canvas canvas;
    private boolean isEducationalMode = false;

    private GraphicsContext gc;


    private double massX = 200;
    private double massY = 300;
    private double widthOfBox = 100;

    public void initialize(){
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GRAY);
        gc.fillRect(0,300,canvas.getWidth(),300);
        gc.fillRect(20, 200, 50, 300);


        createBox();
        drawSpring();





    }

    public void createBox(){
        gc.setFill(Color.BLACK);
        gc.fillRect(massX, massY-widthOfBox, widthOfBox, widthOfBox);

    }



    private double startX = 50;
    private double startY = 275;

    private double endX = massX;// the mass that is at the end of the spring
    private double endY = massY;




    private int numZigs = 5;
    public void drawSpring(){
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);

        double dx = (endX-startX) / numZigs;
        double dy = (endY-startY) / numZigs;

        double zigDir = 1;

        gc.beginPath();
        gc.moveTo(startX, startY);

        for (int i = 1; i < numZigs; i++) {
            double x = startX + i * dx + zigDir * 10;
            double y = startY + i * dy;
            gc.lineTo(x, y);
            zigDir *= -1; // alternate left/right
        }

        gc.lineTo(endX, endY);
        gc.stroke();



    }


    public void setEducationalMode(boolean val){
        isEducationalMode = val;
    }












}
