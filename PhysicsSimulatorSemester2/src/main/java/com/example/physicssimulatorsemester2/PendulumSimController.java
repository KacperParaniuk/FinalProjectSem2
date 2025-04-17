package com.example.physicssimulatorsemester2;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;

public class PendulumSimController {
    public Slider lengthSlider, angleSlider;
    public Canvas canvas;
    public Button startBtn, stopBtn, resetBtn, resumeBtn;
    public Text angleLbl, lengthLbl;
    private boolean isEducationalMode = false;

    private int metersToPixels = 60;

    private double g = 9.81;
    private double dt = 0.016; // 60 fps

    // pendulum state

    private double angle = Math.toRadians(0);
    private double length = 2 * metersToPixels; // in pixels
    private double pivotX = 387;
    private double pivotY = 80;
    private double bobRadius = .175 * metersToPixels;
    private double boxX = pivotX + length * Math.sin(angle);
    private double bobY = pivotY + length * Math.cos(angle);
    private double angularAcceleration =  -1 * (g/2) * Math.sin(angle);
    private double angularVelocity = angularAcceleration * dt;



    private javafx.animation.AnimationTimer timer;

    private GraphicsContext gc;



    public void initialize(){
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GREY);
        gc.fillRect(0, 0, canvas.getWidth(),80);

        drawRope(pivotX, pivotY, pivotX, pivotY+(length)); // 2 meters long

        drawBall(pivotX, 80 + (length));





    }

    public void clearCanvas(){
        gc.clearRect(0,0,canvas.getWidth(), canvas.getHeight()); // clearing canvas
        gc.setFill(Color.GREY);
        gc.fillRect(0, 0, canvas.getWidth(),80);





    }


    public void drawRope(double x, double y, double endX, double endY){
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeLine(x, y,endX,endY);

    }

    public void drawBall(double x, double y){
        gc.setFill(Color.DARKBLUE);
        gc.fillOval(x-bobRadius,y-bobRadius,bobRadius*2,bobRadius*2);
    }
    public void setEducationalMode(boolean val){
        isEducationalMode = val;
    }

    @FXML
    public void handleStart(ActionEvent actionEvent) {
        clearCanvas();
        drawRope(pivotX, pivotY, boxX, bobY);
        drawBall(boxX, bobY);
        angularAcceleration =  -1 * (g/ (length/metersToPixels)) * Math.sin(angle);





        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();

    }

    public void update(){
        // Euler integration loop
        clearCanvas();
        drawRope(pivotX, pivotY, boxX, bobY);
        drawBall(boxX, bobY);

        if(angle<0){
            angularVelocity -= angularAcceleration * dt;
        }
        else{
            angularVelocity += angularAcceleration * dt;
        }

        angle += angularVelocity * dt;


        boxX = pivotX + length * Math.sin(angle);
        bobY = pivotY + length * Math.cos(angle);

    }

    public void handleStop(ActionEvent actionEvent) {
        timer.stop();
    }

    public void handleReset(ActionEvent actionEvent) {
        clearCanvas();

        // initial position
        drawRope(pivotX, pivotY, pivotX, pivotY+(length));
        drawBall(pivotX, 80 + (length));

    }

    public void handleResume(ActionEvent actionEvent) {
        drawRope(pivotX, pivotY, pivotX, pivotY+(length));
        drawBall(pivotX, 80 + (length));
    }


    public void actionLengthOfString() {
        clearCanvas();
        length = (lengthSlider.getValue() / 10);
        lengthLbl.setText("Length: " + roundToOneDecimalPlace(length) + " meters");
        length = length * metersToPixels;

        boxX = pivotX + length * Math.sin(angle);
        bobY = pivotY + length * Math.cos(angle);

        drawRope(pivotX, pivotY, boxX, bobY);
        drawBall(boxX, bobY);
    }


    public void handleChangeAngle() {
        clearCanvas();
        angle = Math.toRadians(angleSlider.getValue());
        angleLbl.setText("Angle: " + roundToOneDecimalPlace(angleSlider.getValue())+ "°");

        boxX = pivotX + length * Math.sin(angle);
        bobY = pivotY + length * Math.cos(angle);

        drawRope(pivotX, pivotY, boxX, bobY);
        drawBall(boxX, bobY);


    }
    public double roundToOneDecimalPlace(double val){
        return (Math.round(val * 10)) / 10.0;
    }





    public void actionMainMenu(){
        try {
            HelloApplication.loadScene("MainMenu.fxml", "Physics Simulator", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
