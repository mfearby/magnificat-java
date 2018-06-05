package com.marcfearby.widgets;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import com.marcfearby.models.FileTreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the folder tree browser
 *
 * @author Marc Fearby
 * @see <a href="https://docs.oracle.com/javafx/2/ui_controls/tree-view.htm">Using JavaFX UI Controls - 13 Tree View</a>
 */
public class FolderTreeController implements Initializable {

    @FXML private TreeView<File> tree;
    private final Image closedImage = new Image(getClass().getResourceAsStream("/icons/tango/folder.png"));
    private final Image openImage = new Image(getClass().getResourceAsStream("/icons/tango/folder-open.png"));

    public FolderTreeController() {

    }

    public void initialize(URL location, ResourceBundle resources) {
        setupTree();
    }


    private void setupTree() {
        tree.setCellFactory(param -> new TreeCell<File>() {
            @Override
            public void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                    setGraphic(null);
                } else {
                    setText(item.getName());
                    Image img = this.getTreeItem().isExpanded() ? openImage : closedImage;
                    ImageView imageView = new ImageView(img);
                    setGraphic(imageView);
                }
            }
        });

        tree.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        File item = newValue.getValue();
                        System.out.println("Selected node: " + item.getName());
                    }
                });

        try {
            File home = new File(System.getProperty("user.home"));
            FileTreeItem<File> root = new FileTreeItem<>(home);
            tree.setRoot(root);
            root.setExpanded(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
