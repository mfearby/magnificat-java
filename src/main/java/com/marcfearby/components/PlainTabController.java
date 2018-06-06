package com.marcfearby.components;

import com.marcfearby.WindowController;
import com.marcfearby.widgets.FilesTableController;
import com.marcfearby.widgets.FolderTreeController;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import java.io.File;

public class PlainTabController {

    @FXML private TreeView tree;
    private WindowController window;
    @FXML private FolderTreeController treeController;
    @FXML private FilesTableController tableController;


    public PlainTabController() {

    }


    public void init(WindowController window, File path) {
        this.window = window;
        treeController.init(this, path);
        tableController.init(this, path);
    }


    public void selectFolder(File directory) {
        tableController.selectFolder(directory);
    }


    public void addTab(File path) {
        window.addTab(path);
    }


}
