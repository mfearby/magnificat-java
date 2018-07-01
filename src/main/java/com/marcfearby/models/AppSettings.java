package com.marcfearby.models;

import org.ini4j.Wini;
import java.io.StringReader;

public class AppSettings {

    private Double windowWidth = 1024.0;
    private Double windowHeight = 768.0;
    private Double windowX = 0.0;
    private Double windowY = 0.0;

    private static final String KEY_SECTION_MAIN = "Main";
    private static final String KEY_WIN_X = "winX";
    private static final String KEY_WIN_Y = "winY";
    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";

    /**
     * Load application settings from the supplied string
     * @param settings An AppSettings object with the received settings or defaults if they couldn't be read
     */
    public AppSettings(String settings) {
        try {
            // http://ini4j.sourceforge.net/index.html
            StringReader sr = new StringReader(settings);
            Wini ini = new Wini(sr);

            Double x = Double.parseDouble(ini.get(KEY_SECTION_MAIN, KEY_WIN_X));
            if (x > 0) windowX = x;

            Double y = Double.parseDouble(ini.get(KEY_SECTION_MAIN, KEY_WIN_Y));
            if (y > 0) windowY = y;

            Double w = Double.parseDouble(ini.get(KEY_SECTION_MAIN, KEY_WIDTH));
            if (w > 0) windowWidth = w;

            Double h = Double.parseDouble(ini.get(KEY_SECTION_MAIN, KEY_HEIGHT));
            if (h > 0) windowHeight = h;

        } catch (Exception e) {
            System.out.println("AppSettings(String settings): " + e);
        }
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
