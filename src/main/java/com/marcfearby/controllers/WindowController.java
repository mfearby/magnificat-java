package com.marcfearby.controllers;

import com.marcfearby.App;
import com.marcfearby.models.AppSettings;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class WindowController implements Initializable {

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


    // This is called from App.start()
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

        // This style of loading resources works within the IDE, testing, AND the uber jar.
        // Although I can still use getClass().getResourceAsStream() elsewhere but not here?! Weird.
        primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("/icons/magnificat.png")));

//        setupMenus();

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

    // TODO Add menus for all operating systems eventually (for now, just add Magnificat->Quit for macOS)
    // Turns out I didn't need this since jpackage takes care of the main App->Quit menu :-)
//    private void setupMenus() {
//        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
//
//        if (os.contains("mac") || os.contains("darwin")) {
//            Menu menu = new Menu("Magnificat");
//            MenuItem quit = new MenuItem("Quit");
//            quit.setOnAction(e -> Platform.exit());
//            quit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.META_DOWN));
//            menu.getItems().add(quit);
//
//            MenuBar menuBar = new MenuBar(menu);
//            menuBar.setUseSystemMenuBar(true);
//
//            BorderPane root = (BorderPane)primaryStage.getScene().getRoot();
//            root.setBottom(menuBar); // I'm not using the bottom region. If I do one day, then this will break!
//        }
//    }

}
