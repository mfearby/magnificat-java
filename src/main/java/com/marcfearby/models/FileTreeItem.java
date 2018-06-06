package com.marcfearby.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import java.io.File;
import java.util.Arrays;

/**
 * This class extends TreeItem so that the child nodes can be populated dynamically as needed
 *
 * @author Marc Fearby
 * @see <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/control/TreeItem.html">TreeItem</a>
 */
public class FileTreeItem<T> extends TreeItem<File> implements Comparable<FileTreeItem> {

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


    public FileTreeItem(File file) {
        super(file);
    }


    @Override public ObservableList<TreeItem<File>> getChildren() {
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
            File f = this.getValue();
            isLeaf = f.isFile() || !hasSubFolders(f);
        }
        return isLeaf;
    }


    private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
        File f = TreeItem.getValue();

        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                Arrays.sort(files);
                ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();

                for (File child : files) {
                    if (child.isDirectory() && !child.getName().startsWith(".")) {
                        FileTreeItem<File> node = new FileTreeItem<>(child);
                        children.add(node);
                    }
                }

                return children;
            }
        }

        return FXCollections.emptyObservableList();
    }


    private boolean hasSubFolders(File dir) {
        boolean answer = false;
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File child : files) {
                    if (child.isDirectory() && !child.getName().startsWith(".")) {
                        answer = true;
                        break;
                    }
                }
            }
        }
        return answer;
    }


    @Override
    public int compareTo(FileTreeItem file) {
        File f = (File)file.getValue();
        return this.getValue().getName().compareTo(f.getName());
    }

}
