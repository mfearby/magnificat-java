package com.marcfearby.interfaces;

import javafx.scene.control.TreeItem;
import java.nio.file.Path;

public interface FolderTreeHandler {

    /**
     * Fired whenever a folder node in the TreeView is selected
     * @param item The folder that was just selected
     */
    void treePathSelected(TreeItem<Path> item);


    /**
     * Fired whenever a folder node in the TreeView is collapsed or expanded
     * @param item The folder that was just toggled (use .isExpanded() to determine which way)
     */
    void toggleSelectedTreePath(TreeItem<Path> item);

}