package com.marcfearby.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class extends TreeItem so that the child nodes can be populated dynamically as needed
 *
 * @author Marc Fearby
 */
public class FileTreeItem<T> extends TreeItem<Path> implements Comparable<FileTreeItem> {

    // https://docs.oracle.com/javafx/2/api/javafx/scene/control/TreeItem.html

    // We cache whether the File is a leaf or not. A File is a leaf if
    // it is not a directory and does not have any files contained within
    // it. We cache this as isLeaf() is called often, and doing the
    // actual check on File is expensive.
    private boolean isLeaf;

    // We do the children and leaf testing only once, and then set these
    // booleans to false so that we do not check again during this
    // run. A more complete implementation may need to handle more
    // dynamic file system situations (such as where a folder has files
    // added after the TreeView is shown). Again, this is left as an
    // exercise for the reader.
    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;


    public FileTreeItem(Path file) {
        super(file);
    }


    @Override public ObservableList<TreeItem<Path>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;
            // First getChildren() call, so we actually go off and
            // determine the children of the File contained in this TreeItem.
            refresh();
        }
        return super.getChildren();
    }


    public void refresh() {
        super.getChildren().setAll(buildChildren(this));
    }


    @Override public boolean isLeaf() {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false;
            Path path = this.getValue();
            isLeaf = Files.isRegularFile(path) || !hasSubFolders(path);
        }
        return isLeaf;
    }


    private ObservableList<TreeItem<Path>> buildChildren(TreeItem<Path> TreeItem) {
        Path p = TreeItem.getValue();

        if (Files.isDirectory(p)) {
            ObservableList<TreeItem<Path>> children = FXCollections.observableArrayList();

            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(p)) {
                for (Path path : dirStream) {
                    if (Files.isDirectory(path) && !path.getFileName().startsWith(".")) {
                        FileTreeItem<Path> node = new FileTreeItem<>(path);
                        children.add(node);
                    }
                }
            } catch (IOException e) {
                System.out.println("FileTreeItem.buildChildren() - Exception: " + e.getMessage());
            }

            return children;
        }

        return FXCollections.emptyObservableList();
    }


    private boolean hasSubFolders(Path dir) {
        boolean answer = false;
        if (Files.isDirectory(dir)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir)) {
                for (Path path : directoryStream) {
                    if (Files.isDirectory(path) && !path.getFileName().startsWith(".")) {
                        answer = true;
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("FileTreeItem.hasSubFolders() - Exception: " + e.getMessage());
            }
        }
        return answer;
    }


    @Override
    public int compareTo(FileTreeItem file) {
        Path f = (Path)file.getValue();
        return this.getValue().getFileName().compareTo(f.getFileName());
    }

}
