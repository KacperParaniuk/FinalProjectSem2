package com.example.physicssimulatorsemester2;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class GraphWindowControllerPendulum {
    @FXML
    public LineChart<Number, Number> energyChart;
    public LineChart<Number, Number> angleChart;

    public XYChart.Series<Number, Number> kineticSeries = new XYChart.Series<>();
    public XYChart.Series<Number, Number> potentialSeries = new XYChart.Series<>();
    public XYChart.Series<Number, Number> totalSeries = new XYChart.Series<>();
    public XYChart.Series<Number, Number> angleSeries = new XYChart.Series<>();

    public void initialize() {
        kineticSeries.setName("Kinetic Energy");
        potentialSeries.setName("Potential Energy");
        totalSeries.setName("Total Energy");
        angleSeries.setName("Angle (°)");

        energyChart.getData().addAll(kineticSeries, potentialSeries, totalSeries);
        angleChart.getData().add(angleSeries);


    }


    public XYChart.Series<Number, Number> getKineticSeries(){
        return kineticSeries;
    }

    public XYChart.Series<Number, Number> getPotentialSeries(){
        return potentialSeries;
    }

    public XYChart.Series<Number, Number> getTotalSeries(){
        return totalSeries;
    }
    public XYChart.Series<Number, Number> getAngleSeries(){
        return angleSeries;
    }

}


