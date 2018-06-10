package com.marcfearby;

import com.marcfearby.Utils.Settings;
import com.marcfearby.components.PlainTabController;
import com.marcfearby.components.TabController;
import com.marcfearby.models.TabInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class WindowController implements Initializable {

    @FXML private TabPane tabs;

    private ArrayList<TabController> tabControllers;


    public WindowController() {
        tabControllers = new ArrayList<>();
    }


    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<TabInfo> tabs = Settings.getTabs();

        // Restore the user's previous tabs
        if (tabs.size() > 0) {
            for (TabInfo info : tabs) {
                addTab(info);
            }
        } else {
            // This is the first run or the settings file failed to load
            File home = new File(System.getProperty("user.home"));
            addTab(home);
        }

        // Set this to true so that settings can be saved from now on
        Settings.appLoaded = true;
    }


    private void addTab(TabInfo info) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/PlainTabView.fxml"));
            Tab tab = loader.load();
            PlainTabController ctrl = loader.getController();

            tabControllers.add(ctrl);
            ctrl.init(this, info);

            tab.setOnCloseRequest(event -> {
                // Don't allow the last tab to be closed
                if (tabs.getTabs().size() == 1) {
                    event.consume();
                } else {
                    tabControllers.remove(ctrl);
                    saveTabInfo();
                }
            });

            tabs.getTabs().add(tab);

            if (info.getActive())
                tabs.getSelectionModel().select(tab);

        } catch (Exception e) {
            System.out.println("WindowController.addTab() - Exception: " + e.getMessage());
        }
    }


    public void addTab(File path) {
        TabInfo info = new TabInfo(TabInfo.TabType.PLAIN, path, true);
        addTab(info);
    }


    public void saveTabInfo() {
        List<TabInfo> tabs = tabControllers.stream()
                .map(TabController::getTabInfo)
                .collect(Collectors.toList());

        Settings.saveTabs(tabs);
    }





}
