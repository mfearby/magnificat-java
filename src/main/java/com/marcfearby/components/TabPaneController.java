package com.marcfearby.components;

import com.marcfearby.interfaces.TabPaneHandler;
import com.marcfearby.utils.Global;
import com.marcfearby.utils.Settings;
import com.marcfearby.models.TabInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TabPaneController implements Initializable, TabPaneHandler {

    @FXML private TabPane tabs;
    private ArrayList<AbstractTabController> tabControllers;


    public TabPaneController() {
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
            // Get the current home folder according to the file system in effect
            Path home = Global.getUserHomeFolder();
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
            ctrl.init(info, this);

            tab.setOnCloseRequest(event -> {
                // Don't allow the last tab to be closed
                if (tabs.getTabs().size() == 1) {
                    event.consume();
                } else {
                    tabControllers.remove(ctrl);
                    saveTabInfos();
                }
            });

            tabs.getTabs().add(tab);

            if (info.getActive())
                tabs.getSelectionModel().select(tab);

        } catch (Exception e) {
            System.out.println("TabPaneController.addTab(): " + e);
        }
    }


    @Override
    public void addTab(Path path) {
        TabInfo info = new TabInfo(TabInfo.TabType.PLAIN, path, true);
        addTab(info);
    }


    @Override
    public void saveTabInfos() {
        List<TabInfo> tabs = tabControllers.stream()
                .map(AbstractTabController::getTabInfo)
                .collect(Collectors.toList());

        Settings.saveTabs(tabs);
    }

}
