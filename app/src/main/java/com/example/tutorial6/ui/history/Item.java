package com.example.tutorial6.ui.history;

public class Item {
    String startTime;
    String stopTime;
    String numMoving;
    int image;

    public Item(String startTime, String stopTime, String numMoving, int image) {
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.numMoving = numMoving;
        this.image = image;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String name) {
        this.startTime = name;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getNumMoving() {
        return numMoving;
    }

    public void setNumMoving(String numMoving) {
        this.numMoving = numMoving;
    }
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }


}
