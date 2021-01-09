package com.marcfearby;

import com.marcfearby.controllers.WindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/Window.fxml"));
        Parent window = loader.load();
        WindowController ctrl = loader.getController();
        ctrl.init(stage, window);
    }

    // This isn't called main because that's in magnificat.java so that I can get a working uber-jar
    public static void startup(String[] args) {
        launch();
    }

}