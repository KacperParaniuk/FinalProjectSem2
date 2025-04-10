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
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class HelloController {
    public Button resumeBtn;
    public Text angleLbl, velocityLbl;
    public ToggleButton earthToggleButton, marsToggleButton, jupiterToggleButton;
    public Text gravityLbl;
    @FXML
    private Canvas canvas;


    @FXML
    private Slider angleSlider;
    public Slider velocitySlider;

    private double x,y = 0;
    private double vx, vy;
    private double dt = 0.05;
    private double g = 9.8;
    private double v0 = 100;

    // vx = v0 * cos(angle)
    // vy = v0 * sin(angle)

    public boolean stopped = false;



    private javafx.animation.AnimationTimer timer;

    double scale = .2;

    public void initialize(){
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


    @FXML
    private void handleStart() {
//        System.out.println("Start clicked! Angle = " + angleSlider.getValue());
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//        gc.fillText("Launching at angle: " + angleSlider.getValue(), 50, 50);

        x = 0;
        y= canvas.getHeight()-10; // initial (0,0) position
        v0 = velocitySlider.getValue();

        double angle = Math.toRadians(angleSlider.getValue());
//        System.out.println(angle);
        vx = v0 * cos(angle);
        vy = -v0 * sin(angle); // negative so that it is upward at first

        // ^ this is a unit of speed so we need to convert into pixels.


        GraphicsContext gc = canvas.getGraphicsContext2D(); // this gives me my "drawing tool" for the canvas


        // canvas is from javafx that can be used as our drawing board where we will simulate!

        gc.clearRect(0,0,canvas.getWidth(), canvas.getHeight()); // clearing canvas


        // creating the background

        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(0, canvas.getHeight()-10, canvas.getWidth(),10); // creates a ground
        gc.setFill(Color.SKYBLUE);
        gc.fillRect(0,0, canvas.getWidth(),380);



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

        // draw
        gc.setFill(Color.RED);
        gc.fillOval(x,y,5,5);

        // Euler time-stepping


        x += vx * dt;
        y += vy * dt;
        vy += g * dt;
        // no vx component because no drag and vx stays constant

        System.out.println(x);
        System.out.println(y);
        System.out.println(vy);
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



    public void handleChangeAngle(MouseEvent mouseEvent) {
        double angle = Math.toRadians(angleSlider.getValue());
        System.out.println(angle);

        vx = v0 * cos(angle);
        vy = -v0 * sin(angle);


    }

    public void handleChangeVelocity(MouseEvent mouseEvent) {
        GraphicsContext gc = canvas.getGraphicsContext2D(); // this gives me my "drawing tool" for the canvas

        double angle = Math.toRadians(angleSlider.getValue());

        vx = v0 * cos(angle);
        vy = -v0 * sin(angle);

        double initialX = 0;
        double initialY = canvas.getHeight()-10;
        double endX = x + vx * scale; // sets the magnitude
        double endY = y + vy * scale;

        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);
        gc.strokeLine(initialX, initialY, endX, endY);





    }
}