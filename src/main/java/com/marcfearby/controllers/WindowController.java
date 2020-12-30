package com.marcfearby.controllers;

import com.marcfearby.controllers.TabPaneController;
import com.marcfearby.models.AppSettings;
import com.marcfearby.controllers.PlayerController;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
//import javafx.scene.layout.HBox;
//import javafx.scene.control.TabPane;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class WindowController implements Initializable {

//    @FXML private HBox player;
//    @FXML private TabPane tabs;

    // https://stackoverflow.com/questions/12543487/javafx-nested-controllers-fxml-include
    @FXML private PlayerController playerController;
    @FXML private TabPaneController tabsController;
    private Stage primaryStage;
    private AppSettings appSettings;

    public WindowController() { }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Pass a reference to the player so that tabs can call playFile()
        tabsController.init(playerController);
        // Pass a reference to the tabs handler so that the player can ask for a PlaylistProvider if it's null
        playerController.init(tabsController);
    }


    public void init(Stage primaryStage, Parent window) {
        this.primaryStage = primaryStage;

        appSettings = AppSettings.getInstance();

        primaryStage.setTitle("Magnificat");
        primaryStage.setScene(new Scene(window, appSettings.getWindowWidth(), appSettings.getWindowHeight()));

        if (appSettings.hasNoPosition()) {
            primaryStage.centerOnScreen();
        } else {
            primaryStage.setX(appSettings.getWindowX());
            primaryStage.setY(appSettings.getWindowY());
        }

        // Show window after X/Y coordinates have been set (otherwise the user will see it move)
        primaryStage.show();

        ChangeListener<Number> listener = (observable, oldValue, newValue) -> saveSettings();

        primaryStage.widthProperty().addListener(listener);
        primaryStage.heightProperty().addListener(listener);
        primaryStage.xProperty().addListener(listener);
        primaryStage.yProperty().addListener(listener);
    }


    private Timer timer = null;
    private TimerTask task = null;

    private void saveSettings() {
        cancelTimer();
        timer = new Timer();

        task = new TimerTask() {
            public void run() {
                cancelTimer();

                appSettings.setWindowHeight(primaryStage.getHeight());
                appSettings.setWindowWidth(primaryStage.getWidth());

                appSettings.setWindowX(primaryStage.getX());
                appSettings.setWindowY(primaryStage.getY());

                appSettings.save();
            }
        };

        // Basic debouncing to save only the last call to this method (without using RxJava)
        timer.schedule(task, 500);
    }


    private void cancelTimer() {
        if (timer != null) {
            task.cancel();
            timer.cancel();
        }
    }

}
