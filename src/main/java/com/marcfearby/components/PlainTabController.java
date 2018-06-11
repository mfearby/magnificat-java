package com.marcfearby.components;

import com.marcfearby.models.TabInfo;
import com.marcfearby.widgets.FilesTableController;
import com.marcfearby.widgets.FolderTreeController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;
import java.nio.file.Path;

public class PlainTabController extends TabController {

    @FXML private Tab tab;
    @FXML private TreeView tree;
    private TabPaneController tabPane;
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


    public void init(TabPaneController tabPane, TabInfo info) {
        this.tabPane = tabPane;
        this.info = info;
        treeController.init(this, info.getRoot());
        tableController.init(this, info.getRoot());

        tab.setText(getTabTitle());
    }


    public void selectFolder(Path directory) {
        tableController.selectFolder(directory);
    }


    public void addTab(Path path) {
        tabPane.addTab(path);
    }


    public TabInfo getTabInfo() {
        return info;
    }


    public String getTabTitle() {
        return info.getRoot().getFileName().toString();
    }


    private void saveTabInfo() {
        tabPane.saveTabInfo();
    }


    public void setRoot(Path root) {
        info.setRoot(root);
        saveTabInfo();
    }

}
