package com.marcfearby.models;

public class AppSettings {

    private Double windowWidth = 1024.0;
    private Double windowHeight = 768.0;
    private Double windowX = 300.0;
    private Double windowY = 300.0;

    public AppSettings() {

    }


    public boolean hasNoPosition() {
        return windowX == 0.0 && windowY == 0;
    }


    public void save() {
        // todo - implement settings.ini saving
        System.out.println("height: " + windowHeight + ", width: " + windowWidth + ", x: " + windowX + ", y: " + windowY);
    }


    public Double getWindowWidth() {
        return windowWidth;
    }
    public void setWindowWidth(Double windowWidth) {
        this.windowWidth = windowWidth;
    }


    public Double getWindowHeight() {
        return windowHeight;
    }
    public void setWindowHeight(Double windowHeight) {
        this.windowHeight = windowHeight;
    }


    public Double getWindowX() {
        return windowX;
    }
    public void setWindowX(Double windowX) {
        this.windowX = windowX;
    }


    public Double getWindowY() {
        return windowY;
    }
    public void setWindowY(Double windowY) {
        this.windowY = windowY;
    }
}
