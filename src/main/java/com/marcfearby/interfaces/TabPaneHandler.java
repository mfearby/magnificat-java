package com.marcfearby.interfaces;

import com.marcfearby.components.AbstractTabController;
import java.nio.file.Path;

public interface TabPaneHandler {

    void addTab(Path path);

    void saveTabInfos();


    /**
     * Called by the PlayerController when it has no PlaylistProvider to request
     * the selected/active tab controller to become the playlist provider
     */
    void activatePlaylistProvider();


    /**
     * Called when a tab becomes the active tab
     * @param controller The controller of the active tab
     */
    void setActiveTabController(AbstractTabController controller);

    //void addTab(/* whatever parameters I dream up for a FancyTab */)

}
