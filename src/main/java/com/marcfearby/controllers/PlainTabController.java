package com.marcfearby.controllers;

import com.marcfearby.interfaces.*;
import com.marcfearby.models.TabInfo;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;

public class PlainTabController extends AbstractTabController implements FolderTreeHandler, PlainTabHandler {

    @FXML private Tab tab;
    @FXML private TreeView tree;
    @FXML private SplitPane splitter;
    private TabPaneHandler tabPaneHandler;
    private PlayerHandler playerHandler;
    @FXML private FolderTreeController treeController;
    @FXML private FilesTableController tableController;
    private TabInfo info;

    public PlainTabController() {

    }


    /**
     * Fires when the selected/active tab in the TabPane changes
     */
    @FXML
    public void onSelectionChanged(Event e) {
        info.setActive(tab.isSelected());

        // Save the tabs only for the active tab (the deactivated tab's event will be called before this one)
        if (tab.isSelected())
            saveTabInfos();

        // Because TabPane.getSelectionModel().getSelectedItem() doesn't let me get the controller
        tabPaneHandler.setActiveTabController(this);
    }


    @SuppressWarnings("WeakerAccess")
    public void init(TabInfo info, TabPaneHandler tabPaneHandler, PlayerHandler playerHandler) {
        this.info = info;
        this.tabPaneHandler = tabPaneHandler;
        this.playerHandler = playerHandler;

        SplitPane.setResizableWithParent(tree, false);
        SplitPane.Divider div = splitter.getDividers().get(0);
        // Restore the splitter position from last time (or use default)
        div.setPosition(info.getDiv1Position());

        // Save before init() which triggers saveTabInfos() & overrides it with getRoot() before expandPath() below
        String selectedTreePath = info.getSelectedTreePath();
        boolean expanded = info.getExpanded();

        treeController.init(info.getRoot(), this, this);
        tableController.init(info.getRoot(), this);

        tab.setText(getTabTitle());

        if (selectedTreePath != null)
            treeController.expandPath(selectedTreePath, expanded);

        if (info.getIsPlaylistProvider()) {
            Path restoreTrack = info.getCurrentTrack();
            if (restoreTrack != null) {
                tableController.restoreCurrentTrack(restoreTrack);
            } else {
                becomePlaylistProvider(false);
            }
        }

        div.positionProperty().addListener((observable, oldValue, newValue) -> handlerSplitterResize());
    }


    @Override
    public void treePathSelected(TreeItem<Path> item) {
        tableController.selectFolder(item.getValue());
        saveSelectedItem(item);
    }


    @Override
    public void toggleSelectedTreePath(TreeItem<Path> item) {
        saveSelectedItem(item);
    }


    @Override
    public void addTab(Path path) {
        tabPaneHandler.addTab(path, false);
    }


    @Override
    public void changeTabRoot(Path path) {
        info.setRoot(path);
        saveTabInfos();
        tab.setText(getTabTitle());
    }


    @Override
    public void becomePlaylistProvider(boolean startPlaying) {
        // Take over the player which will cause it to call tableController.getNextTrack()
        playerHandler.setPlaylistProvider(tableController, startPlaying);
        info.setIsPlaylistProvider(true);
        // this call is needed to colour the tab on app startup
        setTabColoured(true);
        tabPaneHandler.removeColourFromOtherTabInfos(info);
        saveTabInfos();
    }


    @Override
    public void togglePlayPause() {
        playerHandler.togglePlayPause();
    }


    @Override
    public String getTabTitle() {
        return info.getRoot().getFileName().toString();
    }


    @Override
    public void setTabColoured(boolean coloured) {
        if (coloured) {
            tab.getStyleClass().remove("inactiveTab");
            addClassIfNotExists("activeTab");
        } else {
            tab.getStyleClass().remove("activeTab");
            addClassIfNotExists("inactiveTab");
        }
    }


    @Override
    public void saveCurrentTrack(Path track) {
        info.setCurrentTrack(track);
        saveTabInfos();
    }


    /**
     * Add the class only if it isn't applied already (or else it'll
     * be added multiple times and will appear buggy to the user!)
     * @param className The name of the CSS class to apply to the tab
     */
    private void addClassIfNotExists(String className) {
        int i = tab.getStyleClass().indexOf(className);
        if (i < 0)
            tab.getStyleClass().add(className);
    }


    /**
     * Save information about the selected folder node in the TreeView
     * @param item The folder item from the TreeView
     */
    private void saveSelectedItem(TreeItem<Path> item) {
        info.setSelectedTreePath(item.getValue().toString());
        info.setExpanded(item.isExpanded());
        saveTabInfos();
    }


    /**
     * Save all the information about this tab to the settings file
     */
    private void saveTabInfos() {
        tabPaneHandler.saveTabInfos(info);
    }



    private Timer timer = null;
    private TimerTask task = null;

    private void handlerSplitterResize() {
        if (timer != null) {
            task.cancel();
            timer.cancel();
        }

        timer = new Timer();

        task = new TimerTask() {
            public void run() {
                saveSplitterPosition();
            }
        };

        // Basic debouncing to save only the last call to this method (without using RxJava)
        timer.schedule(task, 500);
    }

    private void saveSplitterPosition() {
        task.cancel();
        timer.cancel();

        SplitPane.Divider div = splitter.getDividers().get(0);
        info.setDiv1Position(div.getPosition());

        saveTabInfos();
    }

}
