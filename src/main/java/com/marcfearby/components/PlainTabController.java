package com.marcfearby.components;

import com.marcfearby.WindowController;
import com.marcfearby.models.TabInfo;
import com.marcfearby.widgets.FilesTableController;
import com.marcfearby.widgets.FolderTreeController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;
import java.io.File;

public class PlainTabController extends TabController {

    @FXML private Tab tab;
    @FXML private TreeView tree;
    private WindowController window;
    @FXML private FolderTreeController treeController;
    @FXML private FilesTableController tableController;
    private TabInfo info;


    public PlainTabController() {

    }

    @FXML
    public void onSelectionChanged(Event e) {
        info.setActive(tab.isSelected());
        // Save the tabs only for the active tab (the deactivated tab's event will be called before this one)
        if (tab.isSelected())
            saveTabInfo();
    }


    public void init(WindowController window, TabInfo info) {
        this.window = window;
        this.info = info;
        treeController.init(this, info.getRoot());
        tableController.init(this, info.getRoot());

        tab.setText(getTabTitle());
    }


    public void selectFolder(File directory) {
        tableController.selectFolder(directory);
    }


    public void addTab(File path) {
        window.addTab(path);
    }


    public TabInfo getTabInfo() {
        return info;
    }


    public String getTabTitle() {
        return info.getRoot().getName();
    }


    private void saveTabInfo() {
        window.saveTabInfo();
    }


    public void setRoot(File root) {
        info.setRoot(root);
        saveTabInfo();
    }

}
