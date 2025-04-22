package com.example.physicssimulatorsemester2;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;


import java.awt.*;
import java.io.IOException;

public class SpringSimController extends Drawing{
    public Canvas canvas;
    public Button startBtn, stopBtn, resetBtn, resumeBtn;
    private boolean isEducationalMode = false;

    private GraphicsContext gc;


    private double massX = 400;
    private double massY = 300;
    private double widthOfBox = 100;


    private javafx.animation.AnimationTimer timer;

    public void initialize(){
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GRAY);
        gc.fillRect(0,300,canvas.getWidth(),300);
        gc.fillRect(20, 200, 50, 300);


        createBox();
        drawSpring();





    }

    public void resetSpring(){
        massX = 400;
        massY = 300;
        endX = massX;
    }

    public void createBox(){
        gc.setFill(Color.BLACK);
        gc.fillRect(massX, massY-widthOfBox, widthOfBox, widthOfBox);

    }



    private double startX = 50;
    private double startY = 225;

    private double endX = massX;// the mass that is at the end of the spring
    private double endY = startY;

    private int numZigs = 12;
    private int springWidth = 50;


    public void drawSpring(){
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);

        double dx = (endX-startX) / numZigs;
        double dy = (endY-startY) / numZigs;

        double length = Math.sqrt(dx*dx + dy*dy);
        double perpX = -dy / length; // can be used for vertical springs
        double perpY = dx / length;

        double zigDir = 1;

        gc.beginPath();
        gc.moveTo(startX, startY);

        for (int i = 1; i < numZigs; i++) {
            double x = startX + i * dx;
            double y = startY + i * dy + zigDir * springWidth *perpY;
            gc.lineTo(x, y);
            zigDir *= -1; // alternate left/right
        }

        gc.lineTo(endX, endY);
        gc.stroke();

    }

    public void setEducationalMode(boolean val){
        isEducationalMode = val;
    }






    public void actionStartSim(ActionEvent actionEvent) {
        timer = new AnimationTimer(){
            @Override
            public void handle(long now){

                update(now);




            }



        };
        timer.start();

    }

    public void clearCanvas(){
        gc.clearRect(0,0,canvas.getWidth(), canvas.getHeight()); // clearing canvas
        gc.setFill(Color.GRAY);
        gc.fillRect(0,300,canvas.getWidth(),300);
        gc.fillRect(20, 200, 50, 300);

    }

    // all variables that can be changed by the user
    private double k = 2.0;
    private double mass = 2.0;
    private double x = 10; // displacement from rest (meters)
    private double velocity = 0.0;
    private double acceleration = 0.0;
    private double equilibriumX = endX;
    private double springLength = endX - startX;

    private double lastTime = 0;

    private int metersToPixels = 60;


    public void update(double now){
        clearCanvas();

        if(lastTime==0){
            lastTime = now;
        }

        double dt = (now-lastTime) / 1e9;
        lastTime = now;

        acceleration = (-k / mass) * x;
        velocity += acceleration *dt;
        x += velocity * dt *metersToPixels;

        massX = x + massX;
        endX = massX;

        createBox();
        drawSpring();




    }


    private boolean stopped;

    public void actionStopSim(ActionEvent actionEvent) {
        if(timer != null){
            stopped = true;
            timer.stop();
        }
        stopBtn.setDisable(true);
        resumeBtn.setVisible(true);


    }

    public void actionResetSim(ActionEvent actionEvent) {
        clearCanvas();
        resetSpring();
        if(timer!= null){
            timer.stop();
        }
        startBtn.setDisable(false);
        stopBtn.setDisable(false);
        resumeBtn.setVisible(false);




    }

    public void handleResume(){
        if(timer != null && stopped){
            stopped = false;
            timer.start();
        }
        stopBtn.setDisable(false);
        resumeBtn.setVisible(false);

    }


}
