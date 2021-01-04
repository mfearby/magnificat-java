package com.marcfearby.utils;

import com.marcfearby.models.TabInfo;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import org.ini4j.Wini;
import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.Files;
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

    private static Settings instance;

    private Settings() { }

    static {
        try {
            instance = new Settings();
        } catch(Exception e) {
            throw new RuntimeException("Exception occured whilst trying to create the 'Settings' singleton instance");
        }
    }

    public static Settings getInstance() {
        return instance;
    }

    private static final String SETTINGS_INI = "settings.ini";
    private static final String TABS_INI = "tabs.ini";
    // "path" is the root folder for the Folder Tree
    private static final String KEY_PATH = "path";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ACTIVE = "active";
    // "selected" is the highlighted folder in the tree (i.e., the one which is the root for the Files Table)
    private static final String KEY_SELECTED_PATH = "selected";
    private static final String KEY_EXPANDED_PATH = "expanded";
    private static final String KEY_PLAYLIST_PROVIDER = "playlistprovider";
    private static final String KEY_DIV_1 = "div1";
    private static final String KEY_CURRENT_TRACK = "track";

    private boolean testMode;
    private String testAppSettings;
    private String testTabSettings;


    public static void setTestMode() {
        instance.testMode = true;
    }


    public String getTestAppSettings() {
        return this.testAppSettings;
    }
    public void setTestAppSettings(String testAppSettings) {
        this.testAppSettings = testAppSettings;
    }


    public String getTestTabSettings() {
        return this.testTabSettings;
    }
    public void setTestTabSettings(String settings) {
        this.testTabSettings = settings;
    }


    /**
     * Load application settings from settings.ini (or testAppSettings if applicable)
     * @return A string which can be read by org.ini4j.Wini
     */
    public String getAppSettings() {
        if (testMode) {
            return testAppSettings;
        } else {
            Path file = getOrCreateSettingsFile(SETTINGS_INI);
            return readSettingsFile(file);
        }
    }


    public void saveAppSettings(String settings) {
        if (testMode) {
            testAppSettings = settings;
        } else {
            writeSettingsToFile(settings, SETTINGS_INI);
        }
    }



    public ArrayList<TabInfo> getTabs() {
        String settings;

        if (testMode) {
            settings = testTabSettings;

        } else {
            Path file = getOrCreateSettingsFile(TABS_INI);
            settings = readSettingsFile(file);
        }

        return getTabsFromSettings(settings);
    }


    @SuppressWarnings("WeakerAccess")
    public static ArrayList<TabInfo> getTabsFromSettings(String settings) {
        ArrayList<TabInfo> tabs = new ArrayList<>();

        if (settings == null || settings.isEmpty())
            return tabs;

        try {
            // http://ini4j.sourceforge.net/index.html
            StringReader sr = new StringReader(settings);
            Wini ini = new Wini(sr);

            for (String section : ini.keySet()) {
                FileSystem fs = Global.getFileSystem();
                String path = ini.get(section, KEY_PATH);
                Path dirPath = fs.getPath(path);

                // Allow a tab to be resurrected only if its root directory still exists
                if (Files.isDirectory(dirPath)) {
                    TabInfo.TabType type = TabInfo.TabType.valueOf(ini.get(section, KEY_TYPE));
                    boolean active = Boolean.parseBoolean(ini.get(section, KEY_ACTIVE));
                    TabInfo info = new TabInfo(type, dirPath, active);

                    String selectedValue = ini.get(section, KEY_SELECTED_PATH);
                    if (selectedValue != null) {
                        Path selected = fs.getPath(selectedValue);
                        info.setSelectedTreePath(selected.toString());

                        String currentTrack = ini.get(section, KEY_CURRENT_TRACK);
                        Path currentTrackPath = selected.resolve(currentTrack);
                        info.setCurrentTrack(currentTrackPath);
                    }

                    boolean expanded = Boolean.parseBoolean(ini.get(section, KEY_EXPANDED_PATH));
                    info.setExpanded(expanded);

                    boolean handler = Boolean.parseBoolean(ini.get(section, KEY_PLAYLIST_PROVIDER));
                    info.setIsPlaylistProvider(handler);

                    double div1 = Helper.getDoubleOrDefault(ini.get(section, KEY_DIV_1), 0.25);
                    info.setDiv1Position(div1);

                    tabs.add(info);
                }
            }
        } catch (Exception e) {
            System.out.println("Settings.getTabs(): " + e);
            e.printStackTrace();
        }

        return tabs;
    }


    public void saveTabs(List<TabInfo> tabs) {
        String settings = getTabSettingsString(tabs).toString();

        if (testMode) {
            testTabSettings = settings;
        } else {
            writeSettingsToFile(settings, TABS_INI);
        }
    }


    private void writeSettingsToFile(String settings, String fileName) {
        Path settingsFile = getOrCreateSettingsFile(fileName);
        try {
            FileWriter fw = new FileWriter(settingsFile.toString());
            fw.write(settings);
            fw.close();
        } catch (Exception e) {
            System.out.println("Settings.writeSettingsToFile(\"" + fileName + "\"): " + e);
        }
    }


    @SuppressWarnings("WeakerAccess")
    public static StringWriter getTabSettingsString(List<TabInfo> tabs) {
        StringWriter contents = new StringWriter();

        try {
            // http://ini4j.sourceforge.net/index.html
            Wini ini = new Wini();

            for (int i = 0; i < tabs.size(); i++) {
                TabInfo info = tabs.get(i);
                String section = "tab" + i;
                ini.put(section, KEY_PATH, info.getRoot());
                ini.put(section, KEY_TYPE, info.getType().name());
                ini.put(section, KEY_ACTIVE, info.getActive());
                ini.put(section, KEY_SELECTED_PATH, info.getSelectedTreePath());
                ini.put(section, KEY_EXPANDED_PATH, info.getExpanded());
                ini.put(section, KEY_PLAYLIST_PROVIDER, info.getIsPlaylistProvider());
                ini.put(section, KEY_DIV_1, info.getDiv1Position());

                String currentTrack = "";
                if (info.getCurrentTrack() != null) currentTrack = info.getCurrentTrack().getFileName().toString();
                ini.put(section, KEY_CURRENT_TRACK, currentTrack);
            }

            ini.store(contents);
        } catch (Exception e) {
            System.out.println("Settings.getTabSettingsString(): " + e);
        }

        return contents;
    }


    private static String readSettingsFile(Path file)
    {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file.toString())))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                sb.append(line).append("\n");
            }
        }
        catch (IOException e)
        {
            System.out.println("Settings.readSettingsFile(): " + e);
        }

        return sb.toString();
    }


    private static Path getOrCreateSettingsFile(String iniFileName) {
        String filePath = getSettingsFilePath(iniFileName);
        Path p = Paths.get(filePath);
        try {
            if (!Files.exists(p)) {
                Files.createDirectories(p.getParent());
                Files.createFile(p);
            }
        } catch (Exception e) {
            System.out.println("Settings.getOrCreateSettingsFile(): " + e);
        }
        return p;
    }


    private static String getSettingsFilePath(String iniFileName) {
        String settingsDir = getSettingsDir();
        Path filePath  = Paths.get(settingsDir, iniFileName);
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
