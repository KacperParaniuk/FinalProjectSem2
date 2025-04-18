package com.example.physicssimulatorsemester2;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.*;

public class ProjectileSimController extends Drawing {
    public Button resumeBtn, startBtn, stopBtn, resetBtn;
    public Text angleLbl, velocityLbl;
    public ToggleButton earthToggleButton, marsToggleButton, jupiterToggleButton;
    public Text gravityLbl;
    public ToggleButton showVectorArrowsBtn, showBtn, handleDragBtn;
    public Text currentVelocityLabel;
    public ToggleButton nextStepBtn;
    public Text angleSliderLabel, velocitySliderLabel, chooseGravityLabel;
    public Button nextStepButton;
    public Label tooltipLabel;
    public ImageView tooltipImage;
    public Text currentVelocityYLabel, currentVelocityXLabel, currentXLabel, currentYLabel;
    public ToggleButton currentVal;
    @FXML
    private Canvas canvas;


    @FXML
    private Slider angleSlider;
    public Slider velocitySlider;

    private double x,y = 0;
    private double vx, vy;
    private double dt = 0.05;
    private double dtTrajectory = 0.10;
    private double g = 9.8;
    private double v0 = 100;

    private double metersToPixels = 10;

    // vx = v0 * cos(angle)
    // vy = v0 * sin(angle)

    public boolean stopped = false;


    private boolean isEducationalMode = false;



    private javafx.animation.AnimationTimer timer;

    double scale = .5;

    public void initialize(){
        createKinematicsSimulationSpace();

        currentXLabel.setVisible(false);
        currentVelocityXLabel.setVisible(false);
        currentVelocityYLabel.setVisible(false);
        currentYLabel.setVisible(false);
        resumeBtn.setVisible(false);

        gravityLbl.setText("Gravitational Acceleration: 9.81 m/s2");

        angleLbl.setText("Angle " + (int) angleSlider.getValue() +"°");
        velocityLbl.setText("Velocity " + (int) velocitySlider.getValue() + "m/s");

        // listeners that listen to events such as movement of the slider

        angleSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            angleLbl.setText("Angle " + newVal.intValue() + "°");
        });

        velocitySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            velocityLbl.setText("Velocity " +newVal.intValue() + "m/s");
        });



    }


    public void createKinematicsSimulationSpace(){
        // creating the background

        GraphicsContext gc = canvas.getGraphicsContext2D(); // this gives me my "drawing tool" for the canvas

        gc.clearRect(0,0,canvas.getWidth(), canvas.getHeight()); // clearing canvas

        // creating the background

        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(0, canvas.getHeight()-10, canvas.getWidth(),10); // creates a ground
        gc.setFill(Color.SKYBLUE);
        gc.fillRect(0,0, canvas.getWidth(),380);


    }


    @FXML
    private void handleStart() {
//        System.out.println("Start clicked! Angle = " + angleSlider.getValue());
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//        gc.fillText("Launching at angle: " + angleSlider.getValue(), 50, 50);

        startBtn.setDisable(true);
        x = 10;
        y = canvas.getHeight()-10; // initial (0,0) position
        if(!isEducationalMode){
            v0 = velocitySlider.getValue();

            double angle = Math.toRadians(angleSlider.getValue());
//        System.out.println(angle);
            vx = v0 * cos(angle);
            vy = -v0 * sin(angle); // negative so that it is upward at first

            // ^ this is a unit of speed so we need to convert into pixels.
        }
        else{
            vx = 25 * cos(Math.toRadians(60));
            vy = -25 * sin(Math.toRadians(60));
        }

        GraphicsContext gc = canvas.getGraphicsContext2D(); // this gives me my "drawing tool" for the canvas
        // canvas is from javafx that can be used as our drawing board where we will simulate!

        createKinematicsSimulationSpace();

        timer = new AnimationTimer(){ // gives us access to the timer variable anywhere in our class.
            @Override
            public void handle(long now){
//                System.out.println("Timer");
                update(gc);
                if(isEducationalMode){
                    if(stepIndex==5){
                        if((now-simulationStart)>firstTimeLock){
                            createGravityVector(gc);
                            simulationStop = now;
                            firstTimeLock = now - simulationStart;
                            timer.stop();
                        }
                    }
                    if(stepIndex==6){
                        if((now - (simulationStart - simulationStop) - simulationSecondStart + firstTimeLock + 1300000000   >timeToTop)){
                            createGravityVector(gc);
                            System.out.println("STOPPED AT TOP");
                            timer.stop();
                        }
                    }
                }

            }
            
        };
        timer.start();

    }

    public void handleStop(){
        if(timer != null){
            resumeBtn.setVisible(true);
            stopped = true;
            timer.stop(); // this is the reason we wanted to make it "global"
        }



    }

    public void handleReset(){
        if(timer!= null){
            timer.stop();
        }
        startBtn.setDisable(false);
        earthToggleButton.setVisible(true);
        marsToggleButton.setVisible(true);
        jupiterToggleButton.setVisible(true);
        g = 9.81;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());



    }

    public void handleResume(){
        if(timer != null && stopped){
            stopped = false;
            timer.start();
        }

        resumeBtn.setVisible(false);

    }


    private double vySim = 25 * sin(Math.toRadians(60));
    private long firstTimeLock = (long) (((vySim)/9.81)*100000000);
    private long timeToTop = (long) (((vySim)/9.81)*1000000000);
    private double timeSim = (2 * 25 * sin(Math.toRadians(60))) / (g);

    public void update(GraphicsContext gc){
        if(y >= canvas.getHeight()-9){ // if the y value gets hits the bottom of the page
            System.out.println("STOPPED DUE TO UPDATE");
            startBtn.setDisable(false);
            timer.stop();
            return;
        }



        if(showCurrentValue){
            currentXLabel.setText("X: " + roundToOneDecimalPlace(x/metersToPixels) + " meters");
            currentVelocityXLabel.setText("Vx: " + roundToOneDecimalPlace(vx) + " m/s");
            currentVelocityYLabel.setText("Vy: " + roundToOneDecimalPlace(vy) + " m/s");
            currentYLabel.setText("Y: " + roundToOneDecimalPlace(y/metersToPixels) + " meters");
        }


//        if(isVectorArrows){
//            showVectors(x,y,gc);
//        }
        
        // draw
        gc.setFill(Color.RED);
        gc.fillOval(x,y,5,5);

        // Euler time-stepping

        x += (vx * dt) * metersToPixels;
        y += (vy * dt) * metersToPixels;
        vy += g * dt;

//        System.out.println(x + " X Component");
//        System.out.println(y + " Y Component");
        // no vx component because no drag and vx stays constant

    }

    public void toggleEarth(ActionEvent actionEvent) {
        earthToggleButton.setVisible(true);
        marsToggleButton.setVisible(false);
        jupiterToggleButton.setVisible(false);
        g = 9.81;
        gravityLbl.setText("Gravitational Acceleration: 9.81 m/s2");
    }


    public void toggleMars(ActionEvent actionEvent) {
        earthToggleButton.setVisible(false);
        marsToggleButton.setVisible(true);
        jupiterToggleButton.setVisible(false);
        g = 3.73;
        gravityLbl.setText("Gravitational Acceleration: 3.73 m/s2");

    }

    public void toggleJupiter(ActionEvent actionEvent) {
        earthToggleButton.setVisible(false);
        marsToggleButton.setVisible(false);
        jupiterToggleButton.setVisible(true);
        g = 24.79;
        gravityLbl.setText("Gravitational Acceleration: 24.79 m/s2");

    }



//    public void handleChangeAngle(MouseEvent mouseEvent) {
//        double angle = Math.toRadians(angleSlider.getValue());
//        System.out.println(angle);
//
//        vx = v0 * cos(angle);
//        vy = -v0 * sin(angle);
//
//
//    }

    public void handleChangeVelocity() {
        GraphicsContext gc = canvas.getGraphicsContext2D(); // this gives me my "drawing tool" for the canvas
        createKinematicsSimulationSpace(); // reset velocity vector by clearing canvas
        double angle;
                
        if(isEducationalMode){
            angle = Math.toRadians(60);
            v0 = 25;
            angleLbl.setText("Angle: " + 60+"°");
            velocityLbl.setText("Velocity: " + 25 +" m/s");
            
        }
        else{
            angle = Math.toRadians(angleSlider.getValue());
            v0 = velocitySlider.getValue(); // get a new velocity each time user drags slider
        }
        
        x= 10;
        y = canvas.getHeight()-10;
        double vx = v0 * cos(angle);
        double vy = -v0 * sin(angle);

        // user sees path of trajectory
        buildTrajectoryPath(gc, vx,vy);

        // user sees the max height and distance
        if(showHeightAndDistance){
            buildHeightAndDistance(v0, angle, gc);
        }

        double initialX = 10;
        double initialY = canvas.getHeight()-10;
        double endX = x + vx * scale; // sets the magnitude
        double endY = y + vy * scale;

        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(initialX, initialY, endX, endY);

        super.drawArrowHead(gc, endX,endY,angle);

    }


    public void buildTrajectoryPath(GraphicsContext gc , double vx, double vy){
        double xT = 10;
        double yT = canvas.getHeight()-10;
        ArrayList<Point> trajectoryPoints = new ArrayList<>();

        while(!(yT >= canvas.getHeight()-9)){
            xT += vx * dtTrajectory * metersToPixels;
            yT += vy * dtTrajectory * metersToPixels;
            vy += g * dtTrajectory;
            trajectoryPoints.add(new Point(xT, yT));
        }

        for (int i = 0; i < trajectoryPoints.size(); i++) {
            gc.setFill(Color.WHITE);
            gc.fillOval(trajectoryPoints.get(i).getX(),trajectoryPoints.get(i).getY(),5,5);
        }

    }

//    public void showVectors(double x, double y, GraphicsContext gc){
//        double size = 10;
//        double endX = x;
//        double endY = y+size;
//
//        gc.setStroke(Color.RED);
//        gc.setLineWidth(5);
//        gc.strokeLine(x, y, endX, endY);
//
//        drawArrowHead(gc, endX,endY,-90);
//
//    }

    private int stepIndex = 0;

    @FXML
    private void handleNextStep(){
        if(stepIndex<10){
            stepIndex++;
            updateLectureStep();

        }
    }

    private void handlePreviousStep(){

    }

    private void updateLectureStep(){

        double v0 = velocitySlider.getValue();
        double angle = Math.toRadians(angleSlider.getValue());
        double vx = v0 * Math.cos(angle);
        double vy = v0 * Math.sin(angle);

        switch (stepIndex) {
            case 1:
                tooltipLabel.setText("These are the basic kinematics equations \n" +
                        "used for calculating various variables during an objects flight \n" +
                        "and creating creating projectile motion !   \n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n");
                tooltipImage.setLayoutY(250);
                tooltipImage.setImage(new Image(getClass().getResourceAsStream("/Pictures/kinematicsEQS.PNG")));
                break;
            case 2:
                tooltipLabel.setText("When you throw an object in real life there will be some angle and some velocity \n" +
                        "and that component of velocity splits into Vx and Vy for velocity's in both the x and y directions\n " +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n");
                tooltipImage.setImage(new Image(getClass().getResourceAsStream("/Pictures/rightTriangle.PNG")));
                break;
            case 3:
                tooltipLabel.setText("The variables we will be using \n" +
                        "V = 25m/s\n" +
                        "Theta = 60°\n" +
                        "g = 9.81 (Acceleration due to gravity)" );
                tooltipImage.setImage(null);
                tooltipLabel.setLayoutX(50);
                tooltipLabel.setLayoutY(400);
                nextStepButton.setLayoutX(50);
                nextStepButton.setLayoutY(540);
                handleChangeVelocity();
                break;
            case 4:
                tooltipLabel.setText("We will need to portion off Vx and Vy using trig! \n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n");
                tooltipImage.setImage(new Image(getClass().getResourceAsStream("/Pictures/angleRightTriangle.PNG")));
                tooltipImage.setLayoutY(460);
                tooltipImage.setLayoutX(150);

                break;
            case 5:
                tooltipLabel.setText("The ball always experiences a gravitational force \n" +
                        "downward creating a parabolic path");
                simulationStart = System.nanoTime();
                handleStart();
                tooltipImage.setImage(null);
                break;
            case 6:
                tooltipLabel.setText("The ball always experiences a gravitational force \n" +
                        "downward creating a parabolic path \n" +
                        "In order to find the maximum height we manipulate the kinematics equations \n" +
                        "and it's important to notice that there are multiple ways to find the same variable\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n");
                nextStepButton.setLayoutY(700);
                buildHeightAndDistance(25, Math.toRadians(60), canvas.getGraphicsContext2D());
                simulationSecondStart = System.nanoTime();
                tooltipImage.setImage(new Image(getClass().getResourceAsStream("/Pictures/maximumHeightFormula.PNG")));
                tooltipImage.setLayoutY(600);
                timer.start();
                break;
            case 7:
                tooltipLabel.setText("The ball always experiences a gravitational force \n" +
                        "downward creating a parabolic path \n" +
                        "We can also find the range of the parabolic path the ball goes using the kinematics equations and basic algebra \n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n");
                buildHeightAndDistance(25, Math.toRadians(60), canvas.getGraphicsContext2D());
                timer.start();
                tooltipImage.setImage(new Image(getClass().getResourceAsStream("/Pictures/rangeFormula.PNG")));
                break;
            case 8:
                tooltipLabel.setText("Lastly, we can find the time of travel using as well the kinematics eqs \n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n");
                showTime(canvas.getGraphicsContext2D(), x, y);
                tooltipImage.setImage(new Image(getClass().getResourceAsStream("/Pictures/timeFormula.PNG")));
                tooltipImage.setLayoutY(500);
                break;

            case 9:
                tooltipLabel.setText("That's it! Congratulations on finishing the 2D Kinematics lesson! \n" +
                        "Go into sandbox mode to play around with projectile motion! ");
                tooltipImage.setImage(null);
                break;
            case 10:
                actionMainMenu();
                break;

        }
        

    }



    public void buildHeightAndDistance(double v, double initialAngle, GraphicsContext gc){
        double y = (Math.pow(v,2)  * Math.pow((Math.sin(initialAngle)),2)) / (2 * g); // angle in radians
        double actualMaxHeight = canvas.getHeight() - (y * metersToPixels);
        double x = (Math.pow(v, 2) * Math.sin(2*initialAngle)/g);
        double actualRange = 10 + x * metersToPixels;
        gc.setFill(Color.BLACK);
        if(isEducationalMode){
            if(stepIndex == 6){
                gc.fillText("Max Height: "+ roundToOneDecimalPlace(y) + " meters", 10, actualMaxHeight-10);
            } else if (stepIndex ==7) {
                gc.fillText("Range " + roundToOneDecimalPlace(x) +" meters", actualRange, canvas.getHeight()-20);
            }
        }else{
            gc.fillText("Max Height: "+ roundToOneDecimalPlace(y) + " meters", 10, actualMaxHeight-10);
            gc.fillText("Range " + roundToOneDecimalPlace(x) +" meters", actualRange, canvas.getHeight()-20);
        }


    }

    public void showTime(GraphicsContext gc, double x, double y){
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("Time: " + roundToOneDecimalPlace(timeSim) + " seconds", x + 10, y);
    }



    private boolean showHeightAndDistance = false;
    public void handleShowHeightDistance(ActionEvent actionEvent) {
        if(showHeightAndDistance){
            showHeightAndDistance = false;
            handleChangeVelocity();

        }
        else{
            showHeightAndDistance = true;
            handleChangeVelocity();
        }


    }

    public void createGravityVector(GraphicsContext gc){
        double size = 50;
        double endX = x;
        double endY = y+size;

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeLine(x, y+8, endX, endY);


        double arrowX1 = endX + (Math.sin(Math.toRadians(60)) * 5);
        double arrowX2 = endX - (Math.sin(Math.toRadians(60)) * 5);
        double arrowY1 = endY - (Math.cos(Math.toRadians(60))* 5);

        gc.strokeLine(endX,endY, arrowX1,arrowY1);
        gc.strokeLine(endX,endY,arrowX2,arrowY1);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 14));
        gc.fillText("Fg", endX + 10, endY);

    }




    private boolean isDrag = false;
    public void handleAddDrag(ActionEvent actionEvent) {
        isDrag = true;
    }

    private boolean isVectorArrows = false;
    public void handleShowVectorArrows(ActionEvent actionEvent) {
        isVectorArrows = true;
    }

    long simulationStart;
    long simulationStop;
    long simulationSecondStart;

    public void setEducationalMode(boolean val){
        isEducationalMode = val;
        if(isEducationalMode){
            resumeBtn.setVisible(false);
            earthToggleButton.setVisible(false);
            marsToggleButton.setVisible(false);
            jupiterToggleButton.setVisible(false);
            showVectorArrowsBtn.setVisible(false);
            showBtn.setVisible(false);
            handleDragBtn.setVisible(false);
            angleSlider.setVisible(false);
            velocitySlider.setVisible(false);
            angleSliderLabel.setVisible(false);
            velocitySliderLabel.setVisible(false);
            stopBtn.setVisible(false);
            startBtn.setVisible(false);
            resetBtn.setVisible(false);
            chooseGravityLabel.setVisible(false);
            currentXLabel.setVisible(false);
            currentVelocityXLabel.setVisible(false);
            currentVelocityYLabel.setVisible(false);
            currentYLabel.setVisible(false);
            currentVal.setVisible(false);

            tooltipLabel.setVisible(true);
            tooltipLabel.setText("Step 1: Welcome top the Projectile Motion Simulation! \n"
            + "You will click next step each time you complete the task / read what I say! \n"
            + "Today we are learning the basics of projectile motion!" + " Click \"next step\" once your done! \n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n"
            );
            tooltipImage.setImage(new Image(getClass().getResourceAsStream("/Pictures/projectileMotion.PNG")));
        }
        else{
            tooltipLabel.setVisible(false);
            nextStepButton.setVisible(false);
            tooltipImage.setVisible(false);

        }
    }

    private boolean showCurrentValue = false;
    public void handleEnableCurrentVal(ActionEvent actionEvent) {
        showCurrentValue = currentVal.isSelected();
        if(showCurrentValue){
            currentXLabel.setVisible(true);
            currentVelocityXLabel.setVisible(true);
            currentVelocityYLabel.setVisible(true);
            currentYLabel.setVisible(true);
        }
        else{
            currentXLabel.setVisible(false);
            currentVelocityXLabel.setVisible(false);
            currentVelocityYLabel.setVisible(false);
            currentYLabel.setVisible(false);
        }
    }




}
