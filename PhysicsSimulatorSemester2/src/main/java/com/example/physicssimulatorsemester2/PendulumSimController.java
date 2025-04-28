package com.example.physicssimulatorsemester2;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class PendulumSimController extends Drawing {
    public Slider lengthSlider, angleSlider, massSlider;
    public Canvas canvas;
    public Button startBtn, stopBtn, resetBtn, resumeBtn;
    public Text angleLbl, lengthLbl, massLbl;
    public ToggleButton idealPendBtn, realisticBtn;
    public CheckBox gravityCheckBox, gComponentCheckBox, restoringCheckBox, pathCheckBox, showAllCheckBox;
    public ToggleButton openGraphBtn;
    private boolean isEducationalMode = false;

    private int metersToPixels = 60;

    private double g = 9.81;
    private double dt = 0.016;// 60 fps
    private double graphInterval = 200_000_000.0;
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


    // Graph Data

    private double timeElapsed = 0;
    private double KE;
    private double PE;
    private double height;
    private double TE;

    private javafx.animation.AnimationTimer timer;

    private GraphicsContext gc;
    private GraphWindowControllerPendulum graphController;


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

    private double updateGraphTimeInterval = 0;
    @FXML
    public void handleStart() {
        updateGraphTimeInterval = System.nanoTime();
        clearCanvas();
        drawRope(pivotX, pivotY, bobX, bobY);
        drawBall(bobX, bobY);
        angularAcceleration =  -1 * (g/ (length/metersToPixels)) * Math.sin(angle);

        startBtn.setDisable(true);



        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // animation
                update();

                // graph update
                if(graphController != null){
                    if(now - updateGraphTimeInterval > graphInterval){
                        updateGraphTimeInterval = now;
                        updateGraphs();
                    }
                }
            }
        };
        timer.start();

    }

    public void update(){
        // Euler integration loop
        timeElapsed +=dt;
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

    private int maxPoints = 150;

    public void updateGraphs(){

        if (graphController.kineticSeries.getData().size() > maxPoints) {
            graphController.kineticSeries.getData().removeFirst();
            graphController.potentialSeries.getData().removeFirst();
            graphController.totalSeries.getData().removeFirst();
            graphController.angleSeries.getData().removeFirst();
        }

        height = length * (1 - Math.cos(angle)); // l - lcosθ or (l(1-cosθ))
        PE = mass * g * height;
        KE = .5 * mass * Math.pow(length*angularVelocity, 2);
        TE = PE + KE;

        double angleDeg = Math.toDegrees(angle);

        graphController.kineticSeries.getData().add(new XYChart.Data<>(timeElapsed, KE));
        graphController.potentialSeries.getData().add(new XYChart.Data<>(timeElapsed, PE));
        graphController.totalSeries.getData().add(new XYChart.Data<>(timeElapsed, TE));
        graphController.angleSeries.getData().add(new XYChart.Data<>(timeElapsed, angleDeg));

    }



    private double forceScale = 30;

    private ArrayList<Point> pendulumPoints = new ArrayList<>();

    public void drawRestoringForce(boolean path, boolean restoring){
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



    @FXML
    private void openGraphPopup() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PendulumGraphWindow.fxml"));
        Parent root = loader.load();

        GraphWindowControllerPendulum controller = loader.getController();

        Stage graphStage = new Stage();
        graphStage.setTitle("Pendulum Graphs");
        graphStage.setScene(new Scene(root, 800, 600));
        graphStage.show();

        // OPTIONAL: store reference to controller to update the graphs
        this.graphController = controller;
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

    public void setEducationalMode(boolean val) {
        isEducationalMode = val;
        if (isEducationalMode) {
            resumeBtn.setVisible(false);
            gComponentCheckBox.setVisible(false);
            gravityCheckBox.setVisible(false);
            pathCheckBox.setVisible(false);
            restoringCheckBox.setVisible(false);
            showAllCheckBox.setVisible(false);
            angleSlider.setVisible(false);
            lengthSlider.setVisible(false);
            massSlider.setVisible(false);
            stopBtn.setVisible(false);
            startBtn.setVisible(false);
            resetBtn.setVisible(false);
            massSlider.setVisible(false);
            angleLbl.setVisible(false);
            lengthLbl.setVisible(false);
            massLbl.setVisible(false);
            idealPendBtn.setVisible(false);
            realisticBtn.setVisible(false);
            openGraphBtn.setVisible(false);

            tooltipLabel.setVisible(true);
            tooltipLabel.setText("Step 1: Welcome to the Pendulum Simulation! \n"
                    + "You will click next step each time you complete the task / read what I say! \n"
                    + "Today we are learning the basics of pendulums!" + " Click \"next step\" once your done! \n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "\n"
            );
            tooltipImage.setImage(new Image(getClass().getResourceAsStream("/Pictures/pendulumSimulation.png")));
        } else {
            tooltipLabel.setVisible(false);
            nextStepBtn.setVisible(false);
            tooltipImage.setVisible(false);

        }
    }












}
