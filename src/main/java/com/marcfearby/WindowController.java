package com.marcfearby;

import com.marcfearby.components.PlainTabController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WindowController implements Initializable {

    @FXML private TabPane tabs;


    public WindowController() {

    }


    public void initialize(URL location, ResourceBundle resources) {
        File home = new File(System.getProperty("user.home"));
        addTab(home);
    }


    public void addTab(File path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/PlainTabView.fxml"));
            Parent root = loader.load();
            PlainTabController ctrl = loader.getController();
            ctrl.init(this, path);

            Tab tab = new Tab(path.getName());
            tab.setClosable(true);
            tab.setContent(root);

            tab.setOnCloseRequest(event -> {
                // Don't allow the last tab to be closed
                if (tabs.getTabs().size() == 1)
                    event.consume();
            });

            tabs.getTabs().add(tab);
            tabs.getSelectionModel().select(tab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
