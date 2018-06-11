package com.marcfearby;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/window.fxml"));
        Parent window = loader.load();
//        WindowController ctrl = loader.getController();

        primaryStage.setTitle("Magnificat");
        primaryStage.setScene(new Scene(window, 1024, 768));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
