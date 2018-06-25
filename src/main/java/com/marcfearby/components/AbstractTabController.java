package com.marcfearby.components;

import com.marcfearby.interfaces.PlaylistProvider;
import com.marcfearby.models.TabInfo;

abstract public class AbstractTabController {

    /**
     * Get the vital statistics for a tab
     * @return Object containing all the particulars about a tab
     */
    abstract public TabInfo getTabInfo();


    /**
     * Get the title for this tab, based on the folder name (or search parameters for a FANCY tab in a future release)
     * @return The title for the tab
     */
    abstract public String getTabTitle();


    /**
     * Tell the PlayerHandler that the current tab wants to take over and start playing music
     * @param startPlaying True to start playback immediately
     */
    abstract void becomePlaylistProvider(boolean startPlaying);

}
