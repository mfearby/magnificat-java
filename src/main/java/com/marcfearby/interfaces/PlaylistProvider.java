package com.marcfearby.interfaces;

import java.nio.file.Path;

public interface PlaylistProvider {

    Path getNextTrack();

    Path getPreviousTrack();

    Path getRandomTrack();

}
