package com.marcfearby.interfaces;

import com.marcfearby.models.TrackInfo;

/**
 * Interface for the FilesTableController to supply track information to the PlayerHandler/Controller
 */
public interface PlaylistProvider {

    /**
     * Get the next track in the playlist
     * @return Path to the next track
     */
    TrackInfo getNextTrack();


    /**
     * Get the previous track in the playlist
     * @return Path to the previous track
     */
    TrackInfo getPreviousTrack();


    /**
     * Get a random track from the playlist
     * @return Path to a random track
     */
    TrackInfo getRandomTrack();

}
