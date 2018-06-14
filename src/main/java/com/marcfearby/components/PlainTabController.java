package com.marcfearby.components;

import com.marcfearby.interfaces.FolderTreeHandler;
import com.marcfearby.interfaces.PlainTabHandler;
import com.marcfearby.interfaces.TabPaneHandler;
import com.marcfearby.models.TabInfo;
import com.marcfearby.widgets.FilesTableController;
import com.marcfearby.widgets.FolderTreeController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import java.nio.file.Path;

public class PlainTabController extends AbstractTabController implements FolderTreeHandler, PlainTabHandler {

    @FXML private Tab tab;
    @FXML private TreeView tree;
    private TabPaneHandler tabPaneHandler;
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
            saveTabInfos();
    }


    public void init(TabInfo info, TabPaneHandler tabPaneHandler) {
        this.tabPaneHandler = tabPaneHandler;
        this.info = info;

        // Save before init() which triggers saveTabInfos() & overrides it with getRoot() before expandPath() below
        String selectedTreePath = info.getSelectedTreePath();

        treeController.init(info.getRoot(), this, this);
        tableController.init(info.getRoot());

        tab.setText(getTabTitle());

        if (selectedTreePath != null)
            treeController.expandPath(selectedTreePath);
    }


    /**
     * Received from the TreeView child component whenever a new folder is selected
     * @param item
     */
    @Override
    public void selectTreePath(TreeItem<Path> item) {
        tableController.selectFolder(item.getValue());

        info.setSelectedTreePath(item.getValue().toString());
        saveTabInfos();
    }


    @Override
    public void addTab(Path path) {
        tabPaneHandler.addTab(path);
    }


    @Override
    public void changeTabRoot(Path path) {
        info.setRoot(path);
        saveTabInfos();
    }


    private void saveTabInfos() {
        tabPaneHandler.saveTabInfos();
    }


    public TabInfo getTabInfo() {
        return info;
    }


    public String getTabTitle() {
        return info.getRoot().getFileName().toString();
    }

}
