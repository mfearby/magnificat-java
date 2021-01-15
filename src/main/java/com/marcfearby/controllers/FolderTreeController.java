package com.marcfearby.controllers;

import com.marcfearby.interfaces.FolderTreeHandler;
import com.marcfearby.interfaces.PlainTabHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private TreeItem<Path> selectedItem = null;

    public FolderTreeController() { }


    public void init(Path directory, FolderTreeHandler treeHandler, PlainTabHandler tabHandler) {
        this.treeHandler = treeHandler;
        this.tabHandler = tabHandler;
        setupTree(directory);
    }


    private void setupTree(Path directory) {
        tree.setCellFactory(param -> new TreeCell<>() {
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
                        FileTreeItem fi = (FileTreeItem) getTreeItem();
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
                        selectedItem = newValue;
                        treeHandler.treePathSelected(newValue);
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

            root.addEventHandler(TreeItem.branchExpandedEvent(), this::branchToggled);
            root.addEventHandler(TreeItem.branchCollapsedEvent(), this::branchToggled);
        } catch (Exception e) {
            System.out.println("FolderTreeController.setRoot(): " + e);
        }
    }


    /**
     * Handle the expand and collapse events for TreeView nodes
     * @param event The TreeItem.TreeModificationEvent object
     */
    private void branchToggled(TreeItem.TreeModificationEvent event)
    {
        // Warning suppression added for this unchecked cast
        TreeItem<Path> item = event.getSource();
        boolean isSelected = selectedItem.equals(item);

        if (isSelected)
            treeHandler.toggleSelectedTreePath(item);
    }


    /**
     * Expand the relevant TreeView nodes to reveal the given sub-folder (or as far down as it can)
     * @param target The Path to the sub-folder
     * @param expanded Whether or not the sub-folder node should be expanded (true) or collapsed (false)
     */
    public void expandPath(String target, boolean expanded) {
        FileTreeItem<Path> root = (FileTreeItem<Path>)tree.getRoot();
        FileTreeItem<Path> found = root.expandPath(target, expanded);
        if (found != null) {
            tree.getSelectionModel().select(found);
        }
    }

}
