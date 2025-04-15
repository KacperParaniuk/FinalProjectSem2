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
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.*;

public class ProjectileSimController {
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

        x = 10;
        y = canvas.getHeight()-10; // initial (0,0) position
        v0 = velocitySlider.getValue();

        System.out.println(isEducationalMode);

        double angle = Math.toRadians(angleSlider.getValue());
//        System.out.println(angle);
        vx = v0 * cos(angle);
        vy = -v0 * sin(angle); // negative so that it is upward at first

        // ^ this is a unit of speed so we need to convert into pixels.


        GraphicsContext gc = canvas.getGraphicsContext2D(); // this gives me my "drawing tool" for the canvas
        // canvas is from javafx that can be used as our drawing board where we will simulate!

        createKinematicsSimulationSpace();

        timer = new AnimationTimer(){ // gives us access to the timer variable anywhere in our class.
            @Override
            public void handle(long now){
//                System.out.println("Timer");
                update(gc);
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
        System.out.println("Stopped");



    }

    public void handleReset(){
        if(timer!= null){
            timer.stop();
        }
        earthToggleButton.setVisible(true);
        marsToggleButton.setVisible(true);
        jupiterToggleButton.setVisible(true);
        g = 9.81;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        System.out.println("Reset");


    }

    public void handleResume(){
        if(timer != null && stopped){
            stopped = false;
            timer.start();
        }

        resumeBtn.setVisible(false);

    }



    public void update(GraphicsContext gc){
        if(y >= canvas.getHeight()-9){ // if the y value gets hits the bottom of the page
            timer.stop();
            return;
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

        System.out.println(x + " X Component");
        System.out.println(y + " Y Component");
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

        drawArrowHead(gc, endX,endY,angle);

    }

    public void drawArrowHead(GraphicsContext gc, double x, double y, double angle){
        double size = 10; // size of arrow lines

        double angle1 = angle + Math.toRadians(150); // offset for lines making up the arrow
        double angle2 = angle - Math.toRadians(150);

        System.out.println(Math.toDegrees(angle1) + "Angle 1");
        System.out.println(Math.toDegrees(angle2) + "Angle 2");

        double x1 = x + size * Math.cos(angle1);
        double y1 = y + Math.abs(size * Math.sin(angle1));
        double x2 = x + size * Math.cos(angle2);
        double y2 = y + Math.abs(size * Math.sin(angle2));

        gc.strokeLine(x,y, x1,y1);
        gc.strokeLine(x,y,x2,y2);



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
            case 4:
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

        }

    }

    public void buildHeightAndDistance(double v, double initialAngle, GraphicsContext gc){
        double y = (Math.pow(v,2)  * Math.pow((Math.sin(initialAngle)),2)) / (2 * g); // angle in radians
        double actualMaxHeight = canvas.getHeight() - (y * metersToPixels);
        double x = (Math.pow(v, 2) * Math.sin(2*initialAngle)/g);
        double actualRange = 10 + x * metersToPixels;
        gc.setFill(Color.BLACK);
        gc.fillText("Max Height: "+ roundToOneDecimalPlace(y) + " meters", 10, actualMaxHeight-10);
        gc.fillText("Range " + roundToOneDecimalPlace(x) +" meters", actualRange, canvas.getHeight()-20);

    }

    public double roundToOneDecimalPlace(double val){
        return (Math.round(val * 10)) / 10.0;
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



    private boolean isDrag = false;
    public void handleAddDrag(ActionEvent actionEvent) {
        isDrag = true;
    }

    private boolean isVectorArrows = false;
    public void handleShowVectorArrows(ActionEvent actionEvent) {
        isVectorArrows = true;
    }

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

    public void actionMainMenu(){
        try {

            HelloApplication.loadScene("MainMenu.fxml", "Physics Simulator", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
