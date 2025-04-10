package com.example.physicssimulatorsemester2;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class HelloController {
    public Button resumeBtn;
    public Text angleLbl, velocityLbl;
    @FXML
    private Canvas canvas;

    @FXML
    private Slider angleSlider;
    public Slider velocitySlider;

    private double x,y;
    private double vx, vy;
    private double dt = 0.05;
    private double g = 9.8;
    private double v0 = 100;

    // vx = v0 * cos(angle)
    // vy = v0 * sin(angle)

    public boolean stopped = false;


    private javafx.animation.AnimationTimer timer;

    public void initialize(){
        resumeBtn.setVisible(false);

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


        // canvas is from javafx that can be used as our drawing board where we will simulate!

        GraphicsContext gc = canvas.getGraphicsContext2D(); // this gives me my "drawing tool" for the canvas
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

    public void addition(int x, int y){
        int z = x + y;
    }
}