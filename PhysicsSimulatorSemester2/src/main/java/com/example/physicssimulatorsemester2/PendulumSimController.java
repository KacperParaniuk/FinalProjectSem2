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
    public Text lbl1, lbl2, lbl3;
    public Button answerChoice1, answerChoice2, answerChoice3, answerChoice4;
    public TextField answerTextBox;
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

    public void resetPendulumAttributes(){
        length = 2 * metersToPixels;
        bobX = pivotX;
        angle = Math.toRadians(0);
        bobY = 80 + length;
        angularVelocity = 0;
        angularAcceleration=0;
    }

    public void highestPointPendulum(){
        length = 2 * metersToPixels;
        bobX = pivotX;
        angle = Math.toRadians(60);
        bobY = 100 + length;
        angularVelocity = 0;
        angularAcceleration=0;

        bobX = pivotX + length * Math.sin(angle);
        bobY = pivotY + length * Math.cos(angle);



        drawRope(pivotX, pivotY, bobX, bobY); // 2 meters long
        drawBall(bobX, bobY);
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

                // education mode

                if(isEducationalMode){
                    if(stepIndex==1){
                        if(now-simulationStart>500_000_000.0){
                            stop();
                            drawGravityVectors(false,true);

                        }
                    }
                    else if(stepIndex==4){
                        if(now-simulationStart>600_000_000.0){
                            stop();
                        }
                    }

                    if(periodCalculate){
                        double period = 2 * Math.PI * Math.sqrt((length/48)/g);
                        double currentTimeSinceExecution = (now-updateGraphTimeInterval) / 1_000_000_000.0;
                        System.out.println(currentTimeSinceExecution +" Current Time");
                        if(period > currentTimeSinceExecution){
                            writeText(gc, 200,200,Color.BLACK, "Current Time: " + roundToOneDecimalPlace(currentTimeSinceExecution) + " Seconds");
                        }
                        else{
                            clearCanvas();
                            resetPendulum();
                            writeText(gc, 200,200,Color.BLACK, "Period Was: " + roundToOneDecimalPlace(period) + " Seconds");
                            startBtn.setDisable(false);
                            stop();

                        }

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
        if(isEducationalMode && slidersEnabled){
            resetPendulumAttributes();
            angle = Math.toRadians(90);
            timer.stop();
            startBtn.setDisable(false);
        }
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
    private int userAnswer;

    private int stepIndex = 0;

    public void handleNextStep(){
        if(stepIndex<9){
            stepIndex++;
        }
        else{
            if(answerTextBox.isVisible()){
                userAnswer = Integer.parseInt(answerTextBox.getText());
            }
            System.out.println(lessonStep + "LESSON");
            if(lessonStep+1 > pendulumLessons[educationalAnswerChoice-1].length-1){
                System.out.println("cleared");
                timer.stop();
                slidersEnabled = false;
                startBtn.setVisible(false);
                lengthLbl.setVisible(false);
                lengthSlider.setVisible(false);
                lessonStep=0;
                stepIndex = 8;
            }
            else{
                lessonStep++;
            }

        }
        updateLectureStep();
    }

    private double simulationStart;
    private boolean slidersEnabled = false;
    private boolean periodCalculate = false;

    public void updateLectureStep(){

        System.out.println(stepIndex + ": Lecture Step");

        switch(stepIndex){
            case 1:

                tooltipLabel.setLayoutY(300);
                tooltipLabel.setLayoutX(50);
                tooltipImage.setImage(null);
                nextStepBtn.setLayoutX(50);
                nextStepBtn.setLayoutY(650);
                tooltipLabel.setText("First we need to understand the forces acting on the mass attached to the pendulum!! \n"
                        + "We first have gravity pointing down, this vector of gravity doesn't really help so click next.! \n"
                         +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n"
                );
                angle = Math.toRadians(90);
                simulationStart = System.nanoTime();
                handleStart();
                break;
            case 2:
                drawGravityVectors(true,true);
                tooltipLabel.setText("That's better now we see the components of gravity split up into the x and y directions! \n"
                        + "The y-component of gravity will pull the pendulum down beating the y component of tension, and the x-component of gravity will pull the pendulum toward equilibrium \n"
                        + "Click next to see the next force! \n"
                        +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n"
                );
                break;
            case 3:
                drawRestoringForce(false, true);
                tooltipLabel.setText("The restoring force! This force is the vector sum of gravity's x and y components \n"
                        + "This force provides a torque on the axis of rotation causing rotation but most importantly it always points toward the pendulum equilibrium which is the place of least energy because nature is lazy. \n"
                        + "Click next to see the restoring force on the other side! \n"
                        +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n"
                );
                break;
            case 4:
                simulationStart = System.nanoTime();
                timer.start();
                showAllCheckBox.setSelected(true);

                tooltipLabel.setText("The restoring force will always point toward equilibrium trying to get to the pendulum to the least energy state  \n"
                        + "As a result of having a linear restoring force and inertia the pendulum will forever swing in an ideal situation \n"
                        + "Click next to see the restoring force in action \n"
                        +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n"
                );
                break;
            case 5:
                timer.start();
                tooltipLabel.setText("Once your done watching the beautiful simple harmonic motion click next! \n"+
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n"
                );
                break;
            case 6:
                timer.stop();

                nextStepBtn.setVisible(false);

                tooltipLabel.setLayoutX(25);
                tooltipLabel.setLayoutY(300);

                tooltipImage.setLayoutX(400);
                tooltipImage.setLayoutY(600);

                answerChoice1.setVisible(true);
                answerChoice1.setLayoutX(25);
                answerChoice2.setVisible(true);
                answerChoice2.setLayoutX(300);
                answerChoice3.setVisible(true);
                answerChoice3.setLayoutX(650);
                answerChoice4.setVisible(true);
                answerChoice4.setLayoutX(850);

                answerChoice1.setText("The period is affected by a change in mass");
                answerChoice2.setText("The period only is affected by the variables shown (l and g) ");
                answerChoice3.setText("This is not the period equation");
                answerChoice4.setText("The period is affected by anything");

                correctAnswerChoice = 2;



                tooltipLabel.setText("Lets look at what affects the period of the pendulum! \n"+
                        "Look at the formula... the period is equal to 2 pi * square root of l over g \n" +
                        "What does do you think this means? Click an answer choice!\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n"
                );
                tooltipImage.setImage(new Image(getClass().getResourceAsStream("/Pictures/periodOfPendReg.png")));

                break;
            case 7:
                nextStepBtn.setVisible(true);
                nextStepBtn.setLayoutY(700);

                String chosen = getAnswerChoice(educationalAnswerChoice);
                String correct = getAnswerChoice(correctAnswerChoice);

                if(educationalAnswerChoice==correctAnswerChoice){
                    tooltipLabel.setText("Correct!! You are a pendulum wizard! \n"+
                            "The correct answer is indeed " + correct +
                            "The only variables that will affect the period in time in an ideal pendulum!\n" +
                            "“The period of a simple pendulum depends only on the length of the string and the acceleration due to gravity, " +
                            "because the restoring force is proportional to mass, but so is inertia — so mass cancels out.”\n" +
                            "As you can see in the picture. \n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n"
                    );


                }
                else{
                    tooltipLabel.setText("Wrong! You chose " + chosen + ": It's ok you are learning! \n"+
                            "The correct answer is " + correct +
                            "The only variables that will affect the period in time in an ideal pendulum!\n" +
                            "“The period of a simple pendulum depends only on the length of the string and the acceleration due to gravity, " +
                            "because the restoring force is proportional to mass, but so is inertia — so mass cancels out.”\n" +
                            "As you can see in the picture. \n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n"
                    );

                }

                answerChoice1.setVisible(false);
                answerChoice2.setVisible(false);
                answerChoice3.setVisible(false);
                answerChoice4.setVisible(false);


                tooltipImage.setImage(new Image(getClass().getResourceAsStream("/Pictures/periodOfPendSpec.png")));
                break;
            case 8:
                nextStepBtn.setVisible(false);
                tooltipImage.setVisible(false);
                tooltipLabel.setText("You've learned the basic now if you want to learn more click one of the options or return to main menu! \n"+
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n"
                );

                answerChoice1.setVisible(true);
                answerChoice1.setText("1. Period & Frequency");
                answerChoice2.setVisible(true);
                answerChoice2.setText("2. Effect of Length");
                answerChoice3.setVisible(true);
                answerChoice3.setText("3. Energy in Motion");
                answerChoice4.setVisible(true);
                answerChoice4.setText("4. Angular Acceleration / Forces");
                lessonStep=0;
                break;
            case 9:
                // don't change case unless user wants to go to main menu

                if (educationalAnswerChoice-1==1 && lessonStep == 2 || educationalAnswerChoice-1==0 && lessonStep ==3) {
                    clearCanvas();
                    resetPendulum();
                    startBtn.setVisible(true);
                    lengthLbl.setVisible(true);
                    lengthSlider.setVisible(true);
                    startBtn.setDisable(false);
                    angle = Math.toRadians(90);
                    slidersEnabled =true;
                    if(educationalAnswerChoice-1==0){
                        periodCalculate = true;
                    }
                }


                tooltipImage.setVisible(true);
                tooltipImage.setImage(null);
                tooltipImage.setLayoutX(100);
                tooltipImage.setLayoutY(500);

                answerChoice1.setVisible(false);
                answerChoice2.setVisible(false);
                answerChoice3.setVisible(false);
                answerChoice4.setVisible(false);
                nextStepBtn.setVisible(true);
                String explanation = pendulumLessons[educationalAnswerChoice-1][lessonStep];
                if(slidersEnabled){
                    tooltipLabel.setText("You've clicked on " + getAnswerChoice(educationalAnswerChoice) +" Let's learn! "+
                            explanation + "\n"
                    );
                }
                else{
                    tooltipLabel.setText("You've clicked on " + getAnswerChoice(educationalAnswerChoice) +" Let's learn! "+
                            explanation + "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n"
                    );
                }


                if(lessonPictures[educationalAnswerChoice-1].length > lessonStep ){

                    if(!lessonPictures[educationalAnswerChoice-1][lessonStep].isEmpty()){
                        tooltipImage.setImage(new Image(getClass().getResourceAsStream(lessonPictures[educationalAnswerChoice-1][lessonStep])));
                    }
                }


                if(educationalAnswerChoice-1==2 && lessonStep==0){
                    clearCanvas();
                    highestPointPendulum();
                    gc.setFill(Color.RED);
                    gc.setFont(new Font("Arial", 16));
                    gc.fillText("To calculate potential energy \n" +
                            "we use the formula Ug = mgh ", bobX+10, bobY);
                }
                else if(educationalAnswerChoice-1==2 && lessonStep==1){
                    clearCanvas();
                    resetPendulum();
                    gc.setFill(Color.RED);
                    gc.setFont(new Font("Arial", 16));
                    gc.fillText("To calculate kinetic energy \n" +
                            "we use the formula KE = 1/2mv^2 \n", bobX+10, bobY);
                }
                else if(educationalAnswerChoice-1==2 && lessonStep==2){
                    clearCanvas();
                    resetPendulum();
                    gc.setFill(Color.RED);
                    gc.setFont(new Font("Arial", 16));
                    gc.fillText("Because ME is conserved \n" +
                            "we use conservation of energy \n" +
                            "Such as mgh = 1/2mv^2 to calculate \n" +
                            "certain values", bobX+10, bobY);
                }
                else if(educationalAnswerChoice-1==2 && lessonStep==3){
                    clearCanvas();
                    resetPendulum();
                    gc.setFill(Color.RED);
                    gc.setFont(new Font("Arial", 16));
                    gc.fillText("Because ME is conserved \n" +
                            "we use conservation of energy \n" +
                            "Such as mgh = 1/2mv^2 to calculate \n" +
                            "certain values", bobX+10, bobY);
                    answerTextBox.setVisible(true);
                }
                else if(educationalAnswerChoice-1==2 && lessonStep==4){
                    clearCanvas();
                    resetPendulum();
                    answerTextBox.setVisible(false);
                    if(userAnswer==10){


                    }

                }




                break;
            case 10:
                actionMainMenu();
                break;
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
            lbl1.setVisible(false);
            lbl2.setVisible(false);
            lbl3.setVisible(false);
            answerChoice1.setVisible(false);
            answerChoice2.setVisible(false);
            answerChoice3.setVisible(false);
            answerChoice4.setVisible(false);
            answerTextBox.setVisible(false);

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
            answerChoice1.setVisible(false);
            answerChoice2.setVisible(false);
            answerChoice3.setVisible(false);
            answerChoice4.setVisible(false);
            answerTextBox.setVisible(false);
        }
    }

    private int topicIndex = 0;
    private int lessonStep = 0;



    private String[][] pendulumLessons = {
            {
                    "What is the period? It’s the time it takes to complete one full swing.", // make pendulum go in a full period
                    "The period of a simple pendulum depends only on length and gravity.", // show differential equation (derivation)
                    "Mass doesn't matter because force and inertia both scale with it.", // showcase big force vectors with big mass toggle the slider on for user changing mass
                    "Use the formula: T = 2π√(L/g) When working with an ideal pendulum"
                    // frequency... ask chat what I can teach about frequency
            },
            {
                    "Changing the length affects how far the pendulum travels per swing.",
                    "A longer pendulum has a longer period — it swings more slowly.",
                    "Try adjusting the length slider and observing the change in period." // unlock slider for the user to expierment with
            },
            {
                    "At the highest point, all energy is potential (PE).",
                    "At the lowest point, all energy is kinetic (KE).",
                    "The total mechanical energy remains constant (in ideal mode).",
                    "Now that you know the basics lets try a question! Assuming g = 10 m/s^2 A person releases an ideal pendulum from a height of 5 meters how fast will it be travelling at the bottom? Type" +
                            "your answer WITHOUT units and click next! ",
                    "The answer is.... 10 m/s !!! This mini lesson is over!"
            },
            {
                    "The restoring force pulls the pendulum toward equilibrium.",
                    "It’s proportional to sin(θ), and θ must be small for the period formula to work. This is called a small angle approximation which is a differential equation!",
                    "Angular acceleration = -g/L * sin(θ)",
                    "To derive this restoring force let's start with Newtons 2nd Law equation ",
                    "Then we substitute in the second derivative of position for acceleration and do simple algebra to...",
                    "Find the angular frequency! We know that the second derivative of position equals angular frequency squared times position ",
                    "Once we find angular frequency we can derive the period equation for an ideal pendulum (the time it takes for the pendulum to return back to its starting position "
            }
    };

    private String[][] lessonPictures = {
            {
                "/Pictures/periodOfPendReg.png", "/Pictures/periodDerivationPendulum.png"
            },
            {

            },
            {
                "/Pictures/potentialEnergyPendulum.png", "/Pictures/potentialEnergyPendulum.png", "/Pictures/potentialEnergyPendulum.png"
            },
            {
                "","/Pictures/smallAngleApproximation.png","","/Pictures/part1Spring.png","/Pictures/part2Spring.png","/Pictures/part5Spring.png","/Pictures/part6Spring.png"
            }
    };


    private int educationalAnswerChoice;
    private int correctAnswerChoice;

    public void clickedAnswerChoice(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();

        if(source instanceof Button) {
            Button clickedButton = (Button) source;

            String buttonID = clickedButton.getId();

            if(buttonID.equals("answerChoice1")){
                educationalAnswerChoice = 1;
            }
            else if (buttonID.equals("answerChoice2")) {
                educationalAnswerChoice = 2;
            }
            else if (buttonID.equals("answerChoice3")) {
                educationalAnswerChoice = 3;
            }
            else if (buttonID.equals("answerChoice4")) {
                educationalAnswerChoice = 4;
            }
        }

        stepIndex++;
        updateLectureStep();
    }


    public String getAnswerChoice(int answerChoice){
        if(answerChoice==1){
            return answerChoice1.getText();
        }
        else if(answerChoice==2){
            return answerChoice2.getText();
        }
        else if(answerChoice==3){
            return answerChoice3.getText();
        }
        else if(answerChoice==4){
            return answerChoice4.getText();
        }

        return null;
    }
}

