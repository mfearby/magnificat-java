package com.marcfearby.components;

import com.marcfearby.WindowController;
import com.marcfearby.widgets.FolderTreeController;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import java.io.File;

public class PlainTabController {

    @FXML private TreeView tree;
    @FXML private FolderTreeController treeController;


    public PlainTabController() {

    }


    public void init(WindowController window, File path) {
        System.out.println("PlainTabController.init()");
        treeController.init(window, path);
    }

}
