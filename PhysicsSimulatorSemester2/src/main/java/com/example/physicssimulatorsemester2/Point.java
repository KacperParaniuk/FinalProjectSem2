package com.example.physicssimulatorsemester2;

public class Point {

    private double x1, y1, x2, y2;
    private double opacity;

    public Point(double x, double y){
        this.x1 = x;
        this.y1 = y;

    }

    public Point(double x1, double y1, double x2, double y2, double opacity){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.opacity = opacity;
    }

    public double getX() {
        return x1;
    }

    public double getY() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public double getOpacity() {
        return opacity;
    }
}
