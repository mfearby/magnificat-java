package com.marcfearby.Utils;

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
        File file = getOrCreateSettingsFile(TABS_INI);
        String settings = readSettingsFile(file);
        return getTabsFromSettings(settings);
    }


    public static ArrayList<TabInfo> getTabsFromSettings(String settings) {
        ArrayList<TabInfo> tabs = new ArrayList<>();

        try {
            // http://ini4j.sourceforge.net/index.html
            StringReader sr = new StringReader(settings);
            Wini ini = new Wini(sr);

            for (String section : ini.keySet()) {
                File file = new File(ini.get(section, KEY_PATH));
                // Allow a tab to be resurrected only if its root directory still exists
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


    public static void saveTabs(List<TabInfo> tabs) {
        // Don't save settings whilst the app is initialising (and triggering tab onSelectionChanged events)
        if (!appLoaded) return;

        File settingsFile = getOrCreateSettingsFile(TABS_INI);
        try {
            FileWriter fw = new FileWriter(settingsFile);
            StringWriter sw = getNewSettings(tabs);
            fw.write(sw.toString());
            fw.close();
        } catch (Exception e) {
            System.out.println("Settings.saveTabs() - Exception: " + e.getMessage());
        }
    }


    public static StringWriter getNewSettings(List<TabInfo> tabs) {
        StringWriter contents = new StringWriter();

        try {
            // http://ini4j.sourceforge.net/index.html
            Wini ini = new Wini();

            for (int i = 0; i < tabs.size(); i++) {
                TabInfo info = tabs.get(i);
                String section = "tab" + i;
                ini.put(section, KEY_PATH, info.getRoot().getPath());
                ini.put(section, KEY_TYPE, info.getType().name());
                ini.put(section, KEY_ACTIVE, info.getActive());
            }

            ini.store(contents);
        } catch (Exception e) {
            System.out.println("Settings.getNewSettings() - Exception: " + e.getMessage());
        }

        return contents;
    }


    private static String readSettingsFile(File file)
    {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file.getPath())))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                sb.append(line).append("\n");
            }
        }
        catch (IOException e)
        {
            System.out.println("Settings.readSettingsFile() - Exception: " + e.getMessage());
        }

        return sb.toString();
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
