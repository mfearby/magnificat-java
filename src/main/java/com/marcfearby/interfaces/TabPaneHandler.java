package com.marcfearby.interfaces;

import com.marcfearby.controllers.AbstractTabController;
import com.marcfearby.models.TabInfo;

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
     * Called when a tab becomes the active/selected tab (not when it becomes the PlaylistHandler!)
     * @param controller The controller of the active tab
     */
    void setActiveTabController(AbstractTabController controller);


    /**
     * Loop through all TabInfo objects and remove coloured styling from the previous PlaylistProvider (if any)
     * @param info The info object for the tab which has just taken over as the Player
     */
    void removeColourFromOtherTabInfos(TabInfo info);


    //void addTab(/* whatever parameters I dream up for a FancyTab */)

}
