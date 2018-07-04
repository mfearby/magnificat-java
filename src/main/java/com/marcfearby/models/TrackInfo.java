package com.marcfearby.models;

import javafx.beans.property.*;

import java.nio.file.Path;

public class TrackInfo {

    private Path path;
    private final IntegerProperty playing = new SimpleIntegerProperty(-1);

    public TrackInfo(Path path) {
        this.path = path;
    }


    public Path getPath() {
        return path;
    }
    public void setPath(Path path) {
        this.path = path;
    }


    // This is effectively the getter, used to determine the playing/paused/empty icon beside each track
    public IntegerProperty playingProperty() {
        return playing;
    }
    public final void setPlaying(int playing) {
        playingProperty().set(playing);
    }

}
