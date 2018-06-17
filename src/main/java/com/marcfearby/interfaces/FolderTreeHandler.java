package com.marcfearby.interfaces;

import javafx.scene.control.TreeItem;
import java.nio.file.Path;

public interface FolderTreeHandler {

    void selectTreePath(TreeItem<Path> item);

    void toggleSelectedTreePath(TreeItem<Path> item);

}
