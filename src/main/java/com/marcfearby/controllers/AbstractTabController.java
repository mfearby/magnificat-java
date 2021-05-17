package com.marcfearby.controllers;

abstract public class AbstractTabController {

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


    /**
     * Set the tab to be coloured (indicating that it's the current PlaylistProvider)
     * @param coloured True to highlight it with a colour, False to set it back to normal
     */
    abstract void setTabColoured(boolean coloured);


    /**
     * Tell the tab controller to update the TabInfo object with the current track position.
     * TabPaneController.saveTabInfos() is called when the application exits, and it will
     * call this method before finally saving tabs.ini
     */
    abstract void updateTrackPositionBeforeExit();

}
