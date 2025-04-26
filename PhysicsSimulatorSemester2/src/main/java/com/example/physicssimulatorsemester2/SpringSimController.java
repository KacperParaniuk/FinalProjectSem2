package com.example.physicssimulatorsemester2;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


import java.awt.*;
import java.io.IOException;

public class SpringSimController extends Drawing{
    public Canvas canvas;
    public Button startBtn, stopBtn, resetBtn, resumeBtn;
    public Text currentDisplacementLbl, currentKValLbl, currentMassLbl, currentSpringLengthLbl;
    public Slider lengthSlider, kValSlider, massSlider, deltaXSlider;
    public CheckBox dampingCheckBox;
    public CheckBox allVectorsCheckBox, springForceCheckBox, dragForceCheckBox, gravityForceCheckBox;

    private boolean isEducationalMode = false;



    private GraphicsContext gc;

    private double g = 9.81;

    private double massX = 400;
    private double massY = 300;
    private double widthOfBox = 100;


    private javafx.animation.AnimationTimer timer;

    public void initialize(){
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GRAY);
        gc.fillRect(0,300,canvas.getWidth(),300);
        gc.fillRect(20, 200, 50, 300);
        resumeBtn.setVisible(false);
        stopBtn.setDisable(true);

        setCurrentValues();
        createBox();
        drawSpring();







    }

    public void setCurrentValues(){
        currentDisplacementLbl.setText("Displacement: " + roundToOneDecimalPlace(x) + " Meters (m)");
        currentMassLbl.setText("Mass: " + roundToOneDecimalPlace(mass) + " kg");
        currentKValLbl.setText("K-Value: " + roundToOneDecimalPlace(k));
        currentSpringLengthLbl.setText("Spring Length: " + roundToOneDecimalPlace(springLength/metersToPixels) + " Meters (m)");
    }

    public void resetSpring(){
        massX = 400;
        massY = 300;
        endX = massX;
        k = 2.0;
        mass = 2.0;
        x = 10; // displacement from rest (meters)
        velocity = 0.0;
        acceleration = 0.0;
        lastTime = 0;
        widthOfBox = 100;
        numZigs = 12;
        createBox();
        drawSpring();
    }

    public void createBox(){
        gc.setFill(Color.BLACK);
        gc.fillRect(massX, massY-widthOfBox, widthOfBox, widthOfBox);
    }
    public void createBox(double massX){
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

    public void drawSpring(double drawToEndX, double drawToEndY){
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);

        double dx = (drawToEndX-startX) / numZigs;
        double dy = (drawToEndY-startY) / numZigs;

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

        gc.lineTo(drawToEndX, drawToEndY);
        gc.stroke();

    }

    public void setEducationalMode(boolean val){
        isEducationalMode = val;
    }






    public void actionStartSim(ActionEvent actionEvent) {
        startBtn.setDisable(true);
        stopBtn.setDisable(false);


        timer = new AnimationTimer(){
            @Override
            public void handle(long now){
                update(now);
                drawForces();
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
        if(dampingCheckBox.isSelected()){
            velocity *= .99;
        }
        x += velocity * dt *metersToPixels;

        System.out.println(x);

        massX = (x + massX);
        endX = massX;

        createBox();
        drawSpring();

    }


    private boolean stopped;

    public void actionStopSim() {
        if(timer != null){
            stopped = true;
            lastTime = 0;
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
        stopBtn.setDisable(true);
        resumeBtn.setVisible(false);
        lengthSlider.setValue(0);
        kValSlider.setValue(0);
        deltaXSlider.setValue(0);
        massSlider.setValue(0);
        setCurrentValues();

    }

    public void handleResume(){
        if(timer != null && stopped){
            stopped = false;
            timer.start();
        }
        stopBtn.setDisable(false);
        resumeBtn.setVisible(false);

    }


    // changing sliders

    public void actionSpringLength(MouseEvent mouseEvent) {
        actionStopSim();
        stopBtn.setDisable(false);
        resumeBtn.setVisible(false);

        double stretchTo = (lengthSlider.getValue() + 50) * 5;
        endX = stretchTo;
        massX = endX;

        clearCanvas();
        drawSpring();
        createBox();


        springLength = stretchTo - startX;

        setCurrentValues();


    }

    public void actionMass(MouseEvent mouseEvent) {
        actionStopSim();
        stopBtn.setDisable(false);
        resumeBtn.setVisible(false);

        mass = massSlider.getValue() / 10;
        widthOfBox = mass * 50;

        clearCanvas();
        drawSpring();
        createBox();


        setCurrentValues();

    }

    public void actionKVal(MouseEvent mouseEvent) {
        actionStopSim();
        stopBtn.setDisable(false);
        resumeBtn.setVisible(false);

        k = kValSlider.getValue()/10;


        numZigs = (int) (Math.random()* (k)) + 12;
        clearCanvas();
        drawSpring();
        createBox();

        setCurrentValues();


    }

    public void actionDeltaX(MouseEvent mouseEvent) {
        actionStopSim();
        stopBtn.setDisable(false);
        resumeBtn.setVisible(false);


        x= deltaXSlider.getValue()/5;

        clearCanvas();
        drawSpring(endX + x , endY);
        createBox(endX + x );


        setCurrentValues();

    }

    public void drawForces(){

        if(dragForceCheckBox.isSelected() && dampingCheckBox.isSelected()){
            // draw force
        }
        if(gravityForceCheckBox.isSelected()){
            double gravityForce = mass * g * 5;
            drawVector(gc, massX+widthOfBox/2, massY-widthOfBox/2, massX+widthOfBox/2,
                    massY-widthOfBox/2+gravityForce,Color.RED, "F_Gravity",5,0);
        }





    }
}
