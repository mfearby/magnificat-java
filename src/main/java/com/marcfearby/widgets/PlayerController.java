package com.marcfearby.widgets;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerController implements Initializable {

    @FXML public HBox player;
    @FXML public Button playButton;
    @FXML public Button pauseButton;
    @FXML public Button backButton;
    @FXML public Button stopButton;
    @FXML public Button forwardButton;
    @FXML public Label progressLabel;
    @FXML public Label remainingLabel;
    @FXML public Button muteButton;
    @FXML public Button unmuteButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
}
