package com.example.physicssimulatorsemester2;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


import java.awt.*;
import java.io.IOException;

public class SpringSimController extends Drawing{
    public Canvas canvas;
    public Button startBtn, stopBtn, resetBtn, resumeBtn;
    public Text currentDisplacementLbl, currentKValLbl, currentMassLbl;
    public Slider kValSlider, massSlider, deltaXSlider;
    public CheckBox dampingCheckBox;
    public CheckBox allVectorsCheckBox, springForceCheckBox, dragForceCheckBox, gravityForceCheckBox;
    public CheckBox equilCheckBox;
    public Text lbl4;
    public Label lbl1, lbl2, lbl3;
    public CheckBox UCMCheckBox;

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
        drawEquilibrium();







    }

    public void setCurrentValues(){
        currentDisplacementLbl.setText("Displacement: " + roundToOneDecimalPlace(x/metersToPixels) + " Meters (m)");
        currentMassLbl.setText("Mass: " + roundToOneDecimalPlace(mass) + " kg");
        currentKValLbl.setText("K-Value: " + roundToOneDecimalPlace(k));
    }

    public void resetSpring(){
        massX = 400;
        massY = 300;
        endX = massX;
        k = 2.0;
        mass = 2.0;
        x = .25 * metersToPixels; // displacement from rest (pixels)
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
        if(equilCheckBox.isSelected()){
            drawEquilibrium();
        }
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




    public void actionStartSim(ActionEvent actionEvent) {
        startBtn.setDisable(true);
        stopBtn.setDisable(false);


        timer = new AnimationTimer(){
            @Override
            public void handle(long now){
                update(now);
                drawForces();
                drawEquilibrium();
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
    private double velocity = 0.0;
    private double acceleration = 0.0;
    private double equilibriumX = endX;
    private double springLength = endX - startX;

    private double lastTime = 0;

    private int metersToPixels = 60;
    private double x = .25 * metersToPixels; // displacement from rest (pixels)

    private double timeStartOfSim;

    public void update(double now){
        clearCanvas();

        if(lastTime==0){
            timeStartOfSim = now;
            lastTime = now;
        }

        double dt = (now-lastTime) / 1e9;
        lastTime = now;

        acceleration = (-k / mass) * x;
        velocity += acceleration *dt;
        if(dampingCheckBox.isSelected()){
            velocity *= .99;
        }
        x += velocity * dt * metersToPixels;

        massX = equilibriumX + x;
        endX = massX;

        createBox();
        drawSpring();

        if(UCMCheckBox.isSelected()){
            System.out.println("Drawing");
            drawUCM(dt);
        }

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

//    public void actionSpringLength(MouseEvent mouseEvent) {
//        actionStopSim();
//        stopBtn.setDisable(false);
//        resumeBtn.setVisible(false);
//
//        double stretchTo = (lengthSlider.getValue() + 50) * 5;
//        endX = stretchTo;
//        massX = endX;
//
//        clearCanvas();
//        drawSpring();
//        createBox();
//
//
//        springLength = stretchTo - startX;
//
//        setCurrentValues();
//
//
//    }

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


        x= deltaXSlider.getValue()/30;
        x = x*metersToPixels;
        stretchFromEquilibrium =x;
        endX = equilibriumX+x;
        massX= endX;


        clearCanvas();
        drawSpring();
        createBox();

        setCurrentValues();




    }

    public void drawForces(){

        if(dragForceCheckBox.isSelected() && dampingCheckBox.isSelected() || (allVectorsCheckBox.isSelected() && dampingCheckBox.isSelected())){
            double forceFriction = .05 * mass * g * metersToPixels;
            if(velocity<0){
                drawVector(gc, endX+widthOfBox/2, massY-widthOfBox/2, endX+widthOfBox/2+forceFriction, massY-widthOfBox/2, Color.BROWN, "F_Friction", 5,0);

            }
            else{
                drawVector(gc, endX+widthOfBox/2, massY-widthOfBox/2, endX+widthOfBox/2-forceFriction, massY-widthOfBox/2, Color.BROWN, "F_Friction", -5,0);

            }


        }
        if(gravityForceCheckBox.isSelected() || allVectorsCheckBox.isSelected()){
            double gravityForce = mass * g * 5;
            drawVector(gc, massX+widthOfBox/2, massY-widthOfBox/2, massX+widthOfBox/2,
                    massY-widthOfBox/2+gravityForce,Color.RED, "F_Gravity",5,0);
        }

        if(springForceCheckBox.isSelected() || allVectorsCheckBox.isSelected()){
            double forceSpring = -k * x;
            drawVector(gc, endX+widthOfBox/2, massY-widthOfBox/2, endX+widthOfBox/2+forceSpring, massY-widthOfBox/2, Color.GREEN, "F_Spring", 5,0);



        }


    }

    public void drawEquilibrium(){
        if(equilCheckBox.isSelected()){
            double dashedLineX = equilibriumX + widthOfBox/2;
            double dashedLineStartY = startY + 100;
            double dashedLineEndY = startY + 80;
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(2);
            gc.setFill(Color.YELLOW);
            gc.setFont(new Font("Arial", 12));
            gc.fillText("Equilibrium: X=0 ", dashedLineX+10,dashedLineStartY );

            for (int i = 0; i < 6; i++) {
                gc.strokeLine(dashedLineX,dashedLineStartY,dashedLineX,dashedLineEndY);
                dashedLineStartY-=30;
                dashedLineEndY-=30;
            }

        }
    }

    public Label tooltipLabel;
    public ImageView tooltipImage;
    public Button nextStepBtn;

    private int stepIndex = 0;

    public void handleNextStep(){
        if(stepIndex<10){
            stepIndex++;
            updateLectureStep();
        }
    }

    public void updateLectureStep(){

        System.out.println(stepIndex + ": Lecture Step");


        switch(stepIndex){
            case 1:
                break;
            case 2:


        }






    }

    public void setEducationalMode(boolean val){
        isEducationalMode = val;
        if(isEducationalMode){
            resumeBtn.setVisible(false);
            allVectorsCheckBox.setVisible(false);
            dampingCheckBox.setVisible(false);
            dragForceCheckBox.setVisible(false);
            gravityForceCheckBox.setVisible(false);
            equilCheckBox.setVisible(false);
            springForceCheckBox.setVisible(false);
            kValSlider.setVisible(false);
            stopBtn.setVisible(false);
            startBtn.setVisible(false);
            resetBtn.setVisible(false);
            massSlider.setVisible(false);
            deltaXSlider.setVisible(false);
            lbl1.setVisible(false);
            lbl2.setVisible(false);
            lbl3.setVisible(false);
            lbl4.setVisible(false);
            currentMassLbl.setVisible(false);
            currentDisplacementLbl.setVisible(false);
            currentKValLbl.setVisible(false);



            tooltipLabel.setVisible(true);
            tooltipLabel.setText("Step 1: Welcome to the Spring Simulation! \n"
                    + "You will click next step each time you complete the task / read what I say! \n"
                    + "Today we are learning the basics of simple harmonic motion and springs!" + " Click \"next step\" once your done! \n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n"
            );
            tooltipImage.setImage(new Image(getClass().getResourceAsStream("/Pictures/springSimulation.png")));
        }
        else{
            tooltipLabel.setVisible(false);
            nextStepBtn.setVisible(false);
            tooltipImage.setVisible(false);

        }
    }

    private double stretchFromEquilibrium = x;
    double elapsedTime = 0;

    public void drawUCM(double dt){
        double radius = stretchFromEquilibrium;
        double circleCenterX = equilibriumX;
        double circleCenterY = massY-widthOfBox/2;

        double omega = Math.sqrt(k/mass); // angular frequency;
        elapsedTime += dt;
        double angle = omega * elapsedTime;

        double ucmX = circleCenterX + radius * Math.cos(angle);
        double ucmY = circleCenterY + radius * Math.sin(angle);


        System.out.println("Computed ");

        gc.setStroke(Color.RED);
        gc.strokeOval(circleCenterX-radius, circleCenterY - radius, radius * 2, radius * 2);

        gc.setFill(Color.ORANGE);
        gc.fillOval(ucmX - 5, ucmY - 5, 10, 10);

        gc.setFill(Color.BLACK);
        gc.fillText("Uniform Circular Motion (UCM)", circleCenterX - 50, circleCenterY - radius - 10);


    }

    public void actionDrawUCM(ActionEvent actionEvent) {

    }
}


