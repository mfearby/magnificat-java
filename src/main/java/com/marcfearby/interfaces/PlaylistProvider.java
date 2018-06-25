package com.marcfearby.interfaces;

import java.nio.file.Path;

public interface PlaylistProvider {

    /**
     * Get the next track in the playlist
     * @return Path to the next track
     */
    Path getNextTrack();


    /**
     * Get the previous track in the playlist
     * @return Path to the previous track
     */
    Path getPreviousTrack();


    /**
     * Get a random track from the playlist
     * @return Path to a random track
     */
    Path getRandomTrack();

}
