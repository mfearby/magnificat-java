package com.marcfearby.widgets;

import com.marcfearby.components.PlainTabController;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import com.marcfearby.models.FileTreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;

import java.io.File;

/**
 * Controller for the folder tree browser
 *
 * @author Marc Fearby
 */
public class FolderTreeController {

    // https://docs.oracle.com/javafx/2/ui_controls/tree-view.htm
    @FXML private TreeView<File> tree;
    private PlainTabController tab;
    private final Image closedImage = new Image(getClass().getResourceAsStream("/icons/tango/folder.png"));
    private final Image openImage = new Image(getClass().getResourceAsStream("/icons/tango/folder-open.png"));
    private final Image refreshImage = new Image(getClass().getResourceAsStream("/icons/tango/view-refresh.png"));
    private final Image newTabImage = new Image(getClass().getResourceAsStream("/icons/tango/tab-new.png"));


    public FolderTreeController() {

    }


    public void init(PlainTabController tab, File directory) {
        this.tab = tab;
        setupTree(directory);
    }


    private void setupTree(File directory) {
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

                    MenuItem m = new MenuItem("Refresh", new ImageView(refreshImage));
                    m.setOnAction(event -> {
                        FileTreeItem fi = (FileTreeItem)getTreeItem();
                        fi.refresh();
                    });

                    MenuItem o = new MenuItem("Open in new tab", new ImageView(newTabImage));
                    o.setOnAction(event -> addTab(item));

                    if (item == tree.getRoot().getValue()) {
                        MenuItem f = new MenuItem("Select folder...", new ImageView(openImage));
                        f.setOnAction(event -> {
                            DirectoryChooser chooser = new DirectoryChooser();
                            File selectedDir = chooser.showDialog(tree.getScene().getWindow());
                            if (selectedDir != null) {
                                setRoot(selectedDir);
                            }
                        });
                        setContextMenu(new ContextMenu(m, o, f));
                    } else {
                        setContextMenu(new ContextMenu(m, o));
                    }

                }
            }
        });

        tree.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        File item = newValue.getValue();
                        selectFolder(item);
                    }
                });

        setRoot(directory);
    }


    private void setRoot(File directory) {
        try {
            FileTreeItem<File> root = new FileTreeItem<>(directory);
            tree.setRoot(root);
            root.setExpanded(true);
            tree.getSelectionModel().selectFirst();
            // Update the tab controller's TabInfo object with the new root directory
            tab.setRoot(directory);
        } catch (Exception e) {
            System.out.println("FolderTreeController.setRoot() - Exception: " + e.getMessage());
        }
    }


    private void addTab(File path) {
        tab.addTab(path);
    }


    private void selectFolder(File directory) {
        tab.selectFolder(directory);
    }

}
