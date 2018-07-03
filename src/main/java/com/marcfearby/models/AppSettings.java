package com.marcfearby.models;

import com.marcfearby.utils.Helper;
import com.marcfearby.utils.Settings;
import org.ini4j.Wini;
import java.io.StringReader;
import java.io.StringWriter;

public class AppSettings {

    private static AppSettings instance;

    private AppSettings() { }

    static {
        try {
            String settings = Settings.getInstance().getAppSettings();
            instance = new AppSettings(settings);
        } catch(Exception e) {
            throw new RuntimeException("Exception occured whilst creating the 'AppSettings' singleton instance.");
        }
    }

    public static AppSettings getInstance() {
        return instance;
    }


    private Double windowWidth = 1024.0;
    private Double windowHeight = 768.0;
    private Double windowX = 0.0;
    private Double windowY = 0.0;
    private Double volume = 0.5;
    private Double volumeBeforeMute = 0.5;

    private static final String KEY_SECTION_MAIN = "Main";
    private static final String KEY_WIN_X = "winX";
    private static final String KEY_WIN_Y = "winY";
    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_VOLUME = "volume";
    private static final String KEY_VOLUME_BEFORE_MUTE = "volumeBeforeMute";


    /**
     * Load application settings from the supplied string
     * @param settings An AppSettings object with the received settings or defaults if they couldn't be read
     */
    private AppSettings(String settings) {
        // Defaults declared above will be used if there is no settings.ini
        if (!settings.isEmpty()) {
            try {
                // http://ini4j.sourceforge.net/index.html
                StringReader sr = new StringReader(settings);
                Wini ini = new Wini(sr);

                Double x = Helper.getDoubleOrZero(ini.get(KEY_SECTION_MAIN, KEY_WIN_X));
                if (x > 0) windowX = x;

                Double y = Helper.getDoubleOrZero(ini.get(KEY_SECTION_MAIN, KEY_WIN_Y));
                if (y > 0) windowY = y;

                Double w = Helper.getDoubleOrZero(ini.get(KEY_SECTION_MAIN, KEY_WIDTH));
                if (w > 0) windowWidth = w;

                Double h = Helper.getDoubleOrZero(ini.get(KEY_SECTION_MAIN, KEY_HEIGHT));
                if (h > 0) windowHeight = h;

                Double v = Helper.getDoubleOrZero(ini.get(KEY_SECTION_MAIN, KEY_VOLUME));
                volume = v; // the user might want a volume of 0 to mute it!

                Double m = Helper.getDoubleOrZero(ini.get(KEY_SECTION_MAIN, KEY_VOLUME_BEFORE_MUTE));
                volumeBeforeMute = m;

            } catch (Exception e) {
                System.out.println("AppSettings(String settings): " + e);
            }
        }
    }


    public void save() {
        String settings = getAppSettingsString();
        Settings.getInstance().saveAppSettings(settings);
    }


    private String getAppSettingsString() {
        StringWriter contents = new StringWriter();

        try {
            // http://ini4j.sourceforge.net/index.html
            Wini ini = new Wini();

            ini.put(KEY_SECTION_MAIN, KEY_WIN_X, windowX);
            ini.put(KEY_SECTION_MAIN, KEY_WIN_Y, windowY);
            ini.put(KEY_SECTION_MAIN, KEY_WIDTH, windowWidth);
            ini.put(KEY_SECTION_MAIN, KEY_HEIGHT, windowHeight);
            ini.put(KEY_SECTION_MAIN, KEY_VOLUME, volume);
            ini.put(KEY_SECTION_MAIN, KEY_VOLUME_BEFORE_MUTE, volumeBeforeMute);
            ini.store(contents);

        } catch (Exception e) {
            System.out.println("AppSettings.getAppSettingsString(): " + e);
        }

        return contents.toString();
    }


    public boolean hasNoPosition() {
        return windowX == 0.0 && windowY == 0;
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


    public Double getVolume() {
        return volume;
    }
    public void setVolume(Double volume) {
        this.volume = volume;
    }


    public Double getVolumeBeforeMute() {
        return volumeBeforeMute;
    }
    public void setVolumeBeforeMute(Double volume) {
        this.volumeBeforeMute = volume;
    }
}
