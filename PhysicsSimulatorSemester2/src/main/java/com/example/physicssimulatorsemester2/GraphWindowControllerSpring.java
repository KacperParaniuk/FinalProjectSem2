package com.example.physicssimulatorsemester2;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class GraphWindowControllerSpring {

    public LineChart<Number, Number> positionChart;
    public XYChart.Series<Number, Number> positionSeries = new XYChart.Series<>();

    public void initialize(){
        positionSeries.setName("Displacement");
        positionChart.getData().add(positionSeries);

    }





}
