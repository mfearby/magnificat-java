package com.marcfearby.interfaces;

public interface PlayerHandler {

    /**
     * Set a reference to the PlaylistProvider that is taking over the player
     * @param playlistProvider The object responsible for fulfilling getNextTrack() etc
     * @param startPlaying True if the player should begin playback immediately
     */
    void setPlaylistProvider(PlaylistProvider playlistProvider, boolean startPlaying);


    /**
     * Toggle the playback of media
     */
    void togglePlayPause();

}
