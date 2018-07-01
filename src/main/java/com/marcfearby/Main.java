package com.marcfearby;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/window.fxml"));
        Parent window = loader.load();
        WindowController ctrl = loader.getController();
        ctrl.init(primaryStage, window);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
