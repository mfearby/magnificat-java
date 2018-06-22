package com.marcfearby;

import com.marcfearby.components.TabPaneController;
import com.marcfearby.widgets.PlayerController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.control.TabPane;
import java.net.URL;
import java.util.ResourceBundle;

public class WindowController implements Initializable {

    @FXML private HBox player;
    @FXML private TabPane tabs;

    // https://stackoverflow.com/questions/12543487/javafx-nested-controllers-fxml-include
    @FXML private PlayerController playerController;
    @FXML private TabPaneController tabsController;

    public WindowController() { }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Pass a reference to the player so that tabs can call playFile()
        tabsController.init(playerController);
    }


}
