package com.marcfearby.widgets;

import com.marcfearby.interfaces.FolderTreeHandler;
import com.marcfearby.interfaces.PlainTabHandler;
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
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controller for the folder tree browser
 *
 * @author Marc Fearby
 */
public class FolderTreeController {

    // https://docs.oracle.com/javafx/2/ui_controls/tree-view.htm
    @FXML private TreeView<Path> tree;

    private final Image closedImage = new Image(getClass().getResourceAsStream("/icons/tango/folder.png"));
    private final Image openImage = new Image(getClass().getResourceAsStream("/icons/tango/folder-open.png"));
    private final Image refreshImage = new Image(getClass().getResourceAsStream("/icons/tango/view-refresh.png"));
    private final Image newTabImage = new Image(getClass().getResourceAsStream("/icons/tango/tab-new.png"));

    private FolderTreeHandler treeHandler;
    private PlainTabHandler tabHandler;


    public FolderTreeController() { }


    public void init(Path directory, FolderTreeHandler treeHandler, PlainTabHandler tabHandler) {
        this.treeHandler = treeHandler;
        this.tabHandler = tabHandler;
        setupTree(directory);
    }


    private void setupTree(Path directory) {
        tree.setCellFactory(param -> new TreeCell<Path>() {
            @Override
            public void updateItem(Path item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText("");
                    setGraphic(null);

                } else {
                    setText(item.getFileName().toString());

                    Image img = this.getTreeItem().isExpanded() ? openImage : closedImage;
                    ImageView imageView = new ImageView(img);
                    setGraphic(imageView);

                    MenuItem m = new MenuItem("Refresh", new ImageView(refreshImage));
                    m.setOnAction(event -> {
                        FileTreeItem fi = (FileTreeItem)getTreeItem();
                        fi.refresh();
                    });

                    MenuItem o = new MenuItem("Open in new tab", new ImageView(newTabImage));
                    o.setOnAction(event -> tabHandler.addTab(item));

                    if (item == tree.getRoot().getValue()) {
                        MenuItem f = new MenuItem("Select folder...", new ImageView(openImage));
                        f.setOnAction(event -> {
                            DirectoryChooser chooser = new DirectoryChooser();
                            File selectedDir = chooser.showDialog(tree.getScene().getWindow());
                            if (selectedDir != null) {
                                Path selectedPath = Paths.get(selectedDir.getPath());
                                setRoot(selectedPath);
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
                        Path path = newValue.getValue();
                        treeHandler.selectTreePath(path);
                    }
                });

        setRoot(directory);
    }


    private void setRoot(Path directory) {
        try {
            FileTreeItem<Path> root = new FileTreeItem<>(directory);
            tree.setRoot(root);
            root.setExpanded(true);
            tree.getSelectionModel().selectFirst();
            // Update the tab controller's TabInfo object with the new root directory
            tabHandler.changeTabRoot(directory);
        } catch (Exception e) {
            System.out.println("FolderTreeController.setRoot(): " + e);
        }
    }


}
