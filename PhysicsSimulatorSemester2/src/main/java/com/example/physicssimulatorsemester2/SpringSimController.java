package com.example.physicssimulatorsemester2;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


import java.awt.*;
import java.io.IOException;

public class SpringSimController extends Drawing{
    public Canvas canvas;
    private boolean isEducationalMode = false;

    private GraphicsContext gc;

    public void initialize(){
        gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.GRAY);
        gc.fillRect(0,300,canvas.getWidth(),300);

    }
    public void setEducationalMode(boolean val){
        isEducationalMode = val;
    }












}
