package com.marcfearby.models;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import javafx.beans.property.*;
import java.nio.file.Path;

public class TrackInfo {

    private Path path;
    private final IntegerProperty playing = new SimpleIntegerProperty(-1);
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty artist = new SimpleStringProperty();
    private final StringProperty album = new SimpleStringProperty();
    private final StringProperty genre = new SimpleStringProperty();
    private final StringProperty comments = new SimpleStringProperty();
    private final StringProperty tracknumof = new SimpleStringProperty();
    private final StringProperty time = new SimpleStringProperty();

    private long totalSeconds = -1;

    public TrackInfo(Path path) {
        this.path = path;
        addMp3TagInformation(path);
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


    public StringProperty genreProperty() {
        return genre;
    }

    public StringProperty artistProperty() {
        return artist;
    }

    public StringProperty albumProperty() {
        return album;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty tracknumofProperty() {
        return tracknumof;
    }

    public StringProperty timeProperty() {
        return time;
    }


    private void addMp3TagInformation(Path path) {
        if (path.toString().isEmpty())
            return;

        try {
            // Opening the file using its Path instead of passing a string makes it compatible with my test cases
            Mp3File mp3file  = new Mp3File(path);

            this.totalSeconds = mp3file.getLengthInSeconds();
            String formattedLength = String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
            this.time.set(formattedLength);

            if (mp3file.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                this.genre.set(id3v2Tag.getGenreDescription());
                this.artist.set(id3v2Tag.getArtist());
                this.album.set(id3v2Tag.getAlbum());
                this.title.set(id3v2Tag.getTitle());
                this.tracknumof.set(id3v2Tag.getTrack());
                this.comments.set(id3v2Tag.getComment());

            } else if (mp3file.hasId3v1Tag()) {
                ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                this.genre.set(id3v1Tag.getGenreDescription());
                this.artist.set(id3v1Tag.getArtist());
                this.album.set(id3v1Tag.getAlbum());
                this.title.set(id3v1Tag.getTitle());
                this.tracknumof.set(id3v1Tag.getTrack());
                this.comments.set(id3v1Tag.getComment());
            }

        } catch (Exception e) {
            System.out.println("TrackInfo.addMp3TagInformation(): " + e.getMessage());
        }
    }

}
