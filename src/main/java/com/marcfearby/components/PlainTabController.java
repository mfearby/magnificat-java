package com.marcfearby.components;

import com.marcfearby.widgets.FolderTreeController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

public class PlainTabController implements Initializable {

    @FXML private TreeView tree;
    @FXML private FolderTreeController treeController;


    public PlainTabController() {

    }

    // This won't be called if the class implements Initializable
    public void initialize() {
        System.out.println("PlainTabController.initialize()");
    }

    // This method (with params) will only be called if the class implements Initializable
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("PlainTabController.initialize(location, resources)");
    }



}
