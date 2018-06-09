package com.marcfearby.Utils;

import com.marcfearby.components.TabController;
import com.marcfearby.models.TabInfo;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import org.ini4j.Wini;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for reading and writing application configuration information
 *
 * @author Marc Fearby
 */
public class Settings {

    private static final String TABS_INI = "tabs.ini";
    private static final String KEY_PATH = "path";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ACTIVE = "active";

    public static boolean appLoaded = false;


    public static ArrayList<TabInfo> getTabs() {
        ArrayList<TabInfo> tabs = new ArrayList<>();
        File settingsFile = getOrCreateSettingsFile(TABS_INI);

        try {
            // http://ini4j.sourceforge.net/index.html
            Wini ini = new Wini(settingsFile);

            for (String section : ini.keySet()) {
                File file = new File(ini.get(section, KEY_PATH));
                if (file.exists()) {
                    TabInfo.TabType type = TabInfo.TabType.valueOf(ini.get(section, KEY_TYPE));
                    boolean active = Boolean.parseBoolean(ini.get(section, KEY_ACTIVE));
                    TabInfo info = new TabInfo(type, file, active);
                    tabs.add(info);
                }
            }
        } catch (Exception e) {
            System.out.println("Settings.getTabs() - Exception: " + e.getMessage());
        }

        return tabs;
    }


    public static void saveTabs(List<TabController> tabControllers) {
        // Don't save settings whilst the app is initialising (and triggering tab onSelectionChanged events)
        if (!appLoaded) return;

        File settingsFile = getOrCreateSettingsFile(TABS_INI);

        try {
            // http://ini4j.sourceforge.net/index.html
            Wini ini = new Wini(settingsFile);

            for (int i = 0; i < tabControllers.size(); i++) {
                TabInfo info = tabControllers.get(i).getTabInfo();
                String section = "tab" + i;
                ini.put(section, KEY_PATH, info.getRoot().getPath());
                ini.put(section, KEY_TYPE, info.getType().name());
                ini.put(section, KEY_ACTIVE, info.getActive());
            }

            ini.store();

        } catch (Exception e) {
            System.out.println("Settings.saveTabs() - Exception: " + e.getMessage());
        }

    }


    private static File getOrCreateSettingsFile(String iniFileName) {
        String filePath = getSettingsFilePath(iniFileName);
        File f = new File(filePath);
        try {
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
        } catch (Exception e) {
            System.out.println("Settings.getOrCreateSettingsFile() - Exception: " + e.getMessage());
        }
        return f;
    }


    private static String getSettingsFilePath(String iniFileName) {
        String settingsDir = getSettingsDir();
        Path filePath  = Paths.get(settingsDir, iniFileName);
//        System.out.println("Settings.getSettingsFilePath(): " + filePath);
        return filePath.toString();
    }


    private static String getSettingsDir() {
        // https://github.com/harawata/appdirs
        AppDirs appDirs = AppDirsFactory.getInstance();
        // On Windows: C:\Users\me\AppData\Local\com.marcfearby\Magnificat
        // On macOS:   /Users/me/Library/Application Support/Magnificat
        // On Linux:   /home/me/.local/share/Magnificat/
        return appDirs.getUserDataDir("Magnificat", null, "com.marcfearby");
    }
}
