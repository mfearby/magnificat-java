package com.marcfearby.interfaces;

import java.nio.file.Path;

public interface PlainTabHandler {

    /**
     * Inform the Tab of the path change and trigger saveTabInfos()
     * @param path The new root path for the child TreeView component
     */
    void changeTabRoot(Path path);


    /**
     * Pass a request to the TabPaneController to add a new tab
     * @param path The root path for the new tab
     */
    void addTab(Path path);


    /**
     * Instruct the tab to take over the Player, set its title to bold, and trigger saveTabInfos()
     * @param startPlaying True to start playback immediately
     */
    void becomePlaylistProvider(boolean startPlaying);


    /**
     * Pass on an instruction to the PlayerHander that playback state should be toggled
     */
    void togglePlayPause();


    void saveCurrentTrack(Path track);
}
