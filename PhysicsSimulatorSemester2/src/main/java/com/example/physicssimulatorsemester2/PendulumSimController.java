package com.example.physicssimulatorsemester2;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;

public class PendulumSimController extends Drawing {
    public Slider lengthSlider, angleSlider, massSlider;
    public Canvas canvas;
    public Button startBtn, stopBtn, resetBtn, resumeBtn;
    public Text angleLbl, lengthLbl, massLbl;
    public ToggleButton idealPendBtn, realisticBtn;
    public CheckBox gravityCheckBox, gComponentCheckBox, restoringCheckBox, pathCheckBox, showAllCheckBox;
    private boolean isEducationalMode = false;

    private int metersToPixels = 60;

    private double g = 9.81;
    private double dt = 0.016;// 60 fps
    private double mass = .5;
    // pendulum state

    private double angle = Math.toRadians(0);
    private double length = 2 * metersToPixels; // in pixels
    private double pivotX = 387;
    private double pivotY = 80;
    private double bobRadius = .175 * metersToPixels;
    private double bobX = pivotX + length * Math.sin(angle);
    private double bobY = pivotY + length * Math.cos(angle);
    private double angularAcceleration =  -1 * (g/2) * Math.sin(angle);
    private double angularVelocity = angularAcceleration * dt;



    private javafx.animation.AnimationTimer timer;

    private GraphicsContext gc;



    public void initialize(){
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GREY);
        gc.fillRect(0, 0, canvas.getWidth(),80);
        resumeBtn.setVisible(false);

        drawRope(pivotX, pivotY, pivotX, pivotY+(length)); // 2 meters long

        drawBall(pivotX, 80 + (length));

    }

    public void resetPendulum(){
        length = 2 * metersToPixels;
        bobX = pivotX;
        angle = Math.toRadians(0);
        bobY = 80 + length;
        angularVelocity = 0;
        angularAcceleration=0;

        drawRope(pivotX, pivotY, pivotX, pivotY+(length)); // 2 meters long
        drawBall(pivotX, 80 + length);
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
    public void handleStart() {
        clearCanvas();
        drawRope(pivotX, pivotY, bobX, bobY);
        drawBall(bobX, bobY);
        angularAcceleration =  -1 * (g/ (length/metersToPixels)) * Math.sin(angle);

        startBtn.setDisable(true);



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

        if(showAllCheckBox.isSelected()){
            drawRestoringForce(false, true);
            drawGravityVectors(true,true);
        }
        else{
            if(gravityCheckBox.isSelected() && gComponentCheckBox.isSelected()){
                drawGravityVectors(true,true);
            }
            if(gravityCheckBox.isSelected()){
                drawGravityVectors(false,true);
            }
            if(gComponentCheckBox.isSelected()){
                drawGravityVectors(true,false);
            }
            if(restoringCheckBox.isSelected()){
                drawRestoringForce(false, true);
            }
        }
        if(pathCheckBox.isSelected()){
            drawRestoringForce(true, false);
        }

        drawRope(pivotX, pivotY, bobX, bobY);
        drawBall(bobX, bobY);

        if(angle<0){
            angularVelocity -= angularAcceleration * dt;
        }
        else{
            angularVelocity += angularAcceleration * dt;
        }

        if(realisticBtn.isSelected()){
            angularVelocity *= 0.995;
        }

        angle += angularVelocity * dt;


        bobX = pivotX + length * Math.sin(angle);
        bobY = pivotY + length * Math.cos(angle);

    }



    private double forceScale = 30;

    private ArrayList<Point> pendulumPoints = new ArrayList<>();

    private void drawRestoringForce(boolean path, boolean restoring){
        // Compute angle and force
        double restoringForce = Math.abs(mass * g * Math.sin(angle)); // in Newtons need to convert
//        System.out.println(restoringForce);
        double fx = forceScale * restoringForce * Math.cos(angle + Math.PI / 2);
        double fy = forceScale * restoringForce * Math.sin(angle + Math.PI / 2);

        double arrowX = bobX;
        double arrowY = bobY;

        double endX = arrowX + fx;
        double endY = arrowY + fy;

        if(path){
            pendulumPoints.add(new Point(arrowX, arrowY));

            gc.setStroke(Color.LIGHTBLUE);
            for(Point pt: pendulumPoints){
                gc.fillOval(pt.getX()-2, pt.getY()-2, 4,4);
            }

        }


//        gc.setStroke(Color.ORANGE);
//        gc.strokeLine(bobX, bobY, bobX + 50 * Math.cos(angle), bobY + 50 * Math.sin(angle));
//
//        gc.setStroke(Color.RED);
//        gc.strokeLine(bobX, bobY, bobX + 50 * Math.cos(angle + Math.PI / 2), bobY + 50 * Math.sin(angle + Math.PI / 2));


        if(restoring){
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(2);
            gc.strokeLine(arrowX, arrowY, endX, endY);

            drawArrowhead(gc, arrowX, arrowY, endX, endY);

            gc.setFill(Color.BLUE);
            gc.setFont(new Font("Arial", 12));
            gc.fillText("F_restoring", endX + 5, endY);
        }


    }

    public void drawGravityVectors(boolean vectorComponents, boolean drawGravity){
        double forceGravity = mass * g;
        double vectorY = bobY + 20 *forceGravity;

        if(drawGravity){
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
            gc.strokeLine(bobX, bobY, bobX, vectorY);
            drawArrowhead(gc, bobX, bobY, bobX, vectorY);

            gc.setFill(Color.RED);
            gc.setFont(new Font("Arial", 12));
            gc.fillText("F_gravity", bobX + 5, vectorY);
        }

        if(vectorComponents){
            double vectorComponentX = bobX + -1*(10 * forceGravity * Math.sin(angle));

            gc.setStroke(Color.GREEN);
            gc.setLineWidth(2);
            gc.strokeLine(bobX, bobY, vectorComponentX, bobY);
            drawArrowhead(gc, bobX, bobY, vectorComponentX, bobY);

            gc.setFill(Color.GREEN);
            gc.setFont(new Font("Arial", 12));
            gc.fillText("Fx_gravity", vectorComponentX + 5, bobY);


            double vectorComponentY = bobY + Math.abs(10 * forceGravity * Math.cos(angle));

            gc.setStroke(Color.GREEN);
            gc.setLineWidth(2);
            gc.strokeLine(bobX, bobY, bobX, vectorComponentY);
            drawArrowhead(gc, bobX, bobY, bobX, vectorComponentY);

            gc.setFill(Color.GREEN);
            gc.setFont(new Font("Arial", 12));
            gc.fillText("Fy_gravity", bobX + 5, vectorComponentY);



        }

    }


    private boolean stopped = false;

    public void handleStop(ActionEvent actionEvent) {
        if(timer != null){
            resumeBtn.setVisible(true);
            stopped = true;
            timer.stop();
        }
        stopBtn.setDisable(true);
        resumeBtn.setVisible(true);
    }

    public void handleReset(ActionEvent actionEvent) {
        clearCanvas();
        resetPendulum();
        if(timer!= null){
            timer.stop();
        }
        angleSlider.setValue(0);
        lengthSlider.setValue(0);
        lengthLbl.setText("Length: ");
        angleLbl.setText("Angle: ");
        startBtn.setDisable(false);
        resumeBtn.setVisible(false);
        stopBtn.setDisable(false);
        pendulumPoints.clear();


    }


    public void handleResume(){
        if(timer != null && stopped){
            stopped = false;
            timer.start();
        }
        stopBtn.setDisable(false);
        resumeBtn.setVisible(false);

    }


    public void actionLengthOfString() {
        clearCanvas();
        length = (lengthSlider.getValue() / 10);
        lengthLbl.setText("Length: " + roundToOneDecimalPlace(length) + " meters");
        length = length * metersToPixels;

        bobX = pivotX + length * Math.sin(angle);
        bobY = pivotY + length * Math.cos(angle);

        drawRope(pivotX, pivotY, bobX, bobY);
        drawBall(bobX, bobY);
    }


    public void handleChangeAngle() {
        clearCanvas();
        angle = Math.toRadians(angleSlider.getValue());
        angleLbl.setText("Angle: " + roundToOneDecimalPlace(angleSlider.getValue())+ "°");

        bobX = pivotX + length * Math.sin(angle);
        bobY = pivotY + length * Math.cos(angle);

        drawRope(pivotX, pivotY, bobX, bobY);
        drawBall(bobX, bobY);


    }

    public void actionChangeMass() {
        mass = massSlider.getValue()/10.0;
        massLbl.setText("Mass: "+ roundToOneDecimalPlace(mass) + "kg");
        bobRadius = mass*10.0;
        clearCanvas();
        drawRope(pivotX, pivotY, bobX, bobY);
        drawBall(bobX, bobY);
    }




    public void resetPath(MouseEvent mouseEvent) {
        pendulumPoints.clear();
    }

}
