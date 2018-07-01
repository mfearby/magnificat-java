package com.marcfearby.components;

import com.marcfearby.interfaces.PlayerHandler;
import com.marcfearby.interfaces.TabPaneHandler;
import com.marcfearby.utils.Global;
import com.marcfearby.models.TabInfo;
import com.marcfearby.utils.Settings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TabPaneController implements TabPaneHandler {

    @FXML private TabPane tabs;
    private PlayerHandler playerHandler;
    private AbstractTabController activeTabController;
    private boolean tabsLoaded;
    private ArrayList<TabInfo> infos;

    public TabPaneController() {
        infos = new ArrayList<>();
    }


    public void init(PlayerHandler playerHandler) {
        this.playerHandler = playerHandler;

        ArrayList<TabInfo> tabs = Settings.getInstance().getTabs();

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

        // Settings can be saved from now on
        tabsLoaded = true;
    }


    private void addTab(TabInfo info) {
        try {
//            Locale.setDefault(new Locale("in", "ID")); // test Indonesian
//            Locale.setDefault(new Locale("en", "AU")); // test Australian
            ResourceBundle bundle = ResourceBundle.getBundle("fxml.widgets.FilesTableView");
            URL location = getClass().getResource("/fxml/components/PlainTabView.fxml");
            FXMLLoader loader = new FXMLLoader(location, bundle);
            Tab tab = loader.load();
            PlainTabController tabController = loader.getController();

            info.setController(tabController);
            infos.add(info);

            tabController.init(info, this, playerHandler);

            tab.setOnCloseRequest(event -> {
                // Don't allow the last tab to be closed
                if (tabs.getTabs().size() == 1) {
                    event.consume();
                } else {
                    infos.remove(info);
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
    public void removeColourFromOtherTabInfos(TabInfo info) {
        for (TabInfo obj : infos) {
            if (obj != info) {
                obj.setIsPlaylistProvider(false);
                obj.getController().setTabColoured(false);
            }
        }
    }


    @Override
    public void saveTabInfos() {
        // Don't save whilst the app is initialising (and triggering tab onSelectionChanged events)
        if (tabsLoaded)
            Settings.getInstance().saveTabs(infos);
    }


    @Override
    public void setActiveTabController(AbstractTabController controller) {
        this.activeTabController = controller;
    }


    @Override
    public void activatePlaylistProvider() {
        activeTabController.becomePlaylistProvider(false);
    }

}
