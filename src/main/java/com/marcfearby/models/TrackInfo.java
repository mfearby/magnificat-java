package com.marcfearby.models;

import javafx.beans.property.*;
import java.io.File;
import java.nio.file.Path;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

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

    private int totalSeconds = -1;

    public TrackInfo(Path path) {
        this.path = path;
        addMp3TagInformation();
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


    private void addMp3TagInformation() {
        String path = this.path.toString();

        if (path.isEmpty())
            return;

        try {
            File f = new File(path);
            AudioFile af = AudioFileIO.read(f);
            Tag tag = af.getTag();
            AudioHeader h = af.getAudioHeader();

            this.genre.set(tag.getFirst(FieldKey.GENRE));
            this.artist.set(tag.getFirst(FieldKey.ARTIST));
            this.album.set(tag.getFirst(FieldKey.ALBUM));
            this.title.set(tag.getFirst(FieldKey.TITLE));

            this.totalSeconds = h.getTrackLength();
            String formattedLength = String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
            this.time.set(formattedLength);

            this.tracknumof.set(tag.getFirst(FieldKey.TRACK) + " of " + tag.getFirst(FieldKey.TRACK_TOTAL));

        } catch (Exception e) {
            System.out.println("TrackInfo.addMp3TagInformation(): " + e.getMessage());
        }
    }

}
