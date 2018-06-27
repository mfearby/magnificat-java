package com.marcfearby.widgets;

import com.marcfearby.interfaces.PlayerHandler;
import com.marcfearby.interfaces.PlaylistProvider;
import com.marcfearby.interfaces.TabPaneHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class PlayerController implements Initializable, PlayerHandler {

    @FXML private Button playPauseButton;
    @FXML private Label progressLabel;
    @FXML private Label remainingLabel;
    @FXML private Button soundButton;
    @FXML private Slider volumeSlider;
    @FXML private Label trackTitle;
    @FXML private Slider timeSlider;

    private final Image playImage = new Image(getClass().getResourceAsStream("/icons/tango/media-playback-start.png"));
    private final Image pauseImage = new Image(getClass().getResourceAsStream("/icons/tango/media-playback-pause.png"));
    private final Image mutedImage = new Image(getClass().getResourceAsStream("/icons/tango/audio-volume-muted.png"));
    private final Image unmutedImage = new Image(getClass().getResourceAsStream("/icons/tango/audio-volume-high.png"));

    private TabPaneHandler tabPaneHandler = null;
    private boolean audible = false;
    private MediaPlayer mp;
    private Duration duration;
    private boolean repeat = false;
    private boolean atEndOfMedia = false;
    private double volumeBeforeMuted = 1.0;
    private PlaylistProvider playlistProvider = null;

    // See: https://docs.oracle.com/javase/8/javafx/media-tutorial/playercontrol.htm

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setPlayingIcon(false);
        this.setAudibleIcon(true);

        timeSlider.valueProperty().addListener(ov -> {
            if (timeSlider.isValueChanging()) {
                if (mp != null) {
                    // multiply duration by the percentage calculated based on the slider position
                    mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
            }
        });

        volumeSlider.valueProperty().addListener(ov -> {
            if (volumeSlider.isValueChanging() && mp != null) {
                double volume = volumeSlider.getValue() / 100.0;
                mp.setVolume(volume);
                volumeBeforeMuted = volume;
                setAudibleIcon(volume > 0);
            }
        });
    }


    public void init(TabPaneHandler tabPaneHandler) {
        this.tabPaneHandler = tabPaneHandler;
    }


    @FXML
    public void timeSliderMouseClicked(MouseEvent event) {
        handleSliderClickEvent(event, timeSlider);
    }


    @FXML
    public void volumeSliderMouseClicked(MouseEvent event) {
        handleSliderClickEvent(event, volumeSlider);
    }


    @FXML
    public void togglePlayPause(ActionEvent event) {
        // Most likely the user just started the app and pressed Play and didn't double-click on a track
        if (mp == null) {
            playNext();
            return;
        }

        Status status = mp.getStatus();
        if (status == Status.UNKNOWN  || status == Status.HALTED) {
            return;
        }

        if (status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {
            // rewind the track if we're sitting at the end
            if (atEndOfMedia) {
                mp.seek(mp.getStartTime());
                atEndOfMedia = false;
            }

            mp.play();
            this.setPlayingIcon(true);
        } else {
            mp.pause();
            this.setPlayingIcon(false);
        }
    }


    @FXML
    private void stopPlaying(ActionEvent event) {
        if (mp == null) return;
        mp.stop();
        setPlayingIcon(false);
    }


    @FXML
    public void toggleSound(ActionEvent event) {
        this.setAudibleIcon(!audible);
        double vol = audible ? volumeBeforeMuted : 0;
        mp.setVolume(vol);
    }


    @FXML
    public void goBack(ActionEvent event) {
        Path track = getPlaylistProvider().getPreviousTrack();
        playFile(track);
    }


    @FXML
    public void goForward(ActionEvent event) {
        playNext();
    }


    private void playNext() {
        Path track = getPlaylistProvider().getNextTrack();
        playFile(track);
    }


    @Override
    public void setPlaylistProvider(PlaylistProvider playlistProvider, boolean startPlaying) {
        this.playlistProvider = playlistProvider;

        if (startPlaying)
            playNext();
    }


    /**
     * Get the current PlaylistProvider; if there is none, ask the TabPane to get the
     * active/selected tab to becomePlaylistProvider()
     * @return The current PlaylistProvider
     */
    private PlaylistProvider getPlaylistProvider() {
        // This will make sure that the active tab becomes the playlist provider
        if (playlistProvider == null)
            tabPaneHandler.activatePlaylistProvider();

        return playlistProvider;
    }


    /**
     * Start playing a new track (and stop playing the current track, if any)
     * @param track The path to the new track to play
     */
    private void playFile(Path track) {
        // Kill the previous object if a track is currently playing/paused
        if (mp != null)
            mp.dispose();

        Media media = new Media(track.toUri().toString());
        mp = new MediaPlayer(media);

        mp.play();
        setPlayingIcon(true);
        // Use the existing volume level for the new player object!
        mp.setVolume(volumeSlider.getValue() / 100);

        String name = track.getFileName().toString();
        if (name.indexOf(".") > 0) name = name.substring(0, name.lastIndexOf("."));
        trackTitle.setText(name);

        mp.currentTimeProperty().addListener(ov -> updateValues());

        mp.setOnReady(() -> {
            duration = mp.getMedia().getDuration();
            updateValues();
        });

        mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);

        mp.setOnEndOfMedia(() -> {
            if (!repeat) {
                atEndOfMedia = true;
            }
            playNext();
        });
    }


    /**
     * Toggle the main play/pause icon according to the user's action
     * @param playing True to show the paused icon (i.e., it's currently playing),
     *                False to show the Play icon (i.e., it's currently not playing)
     */
    private void setPlayingIcon(boolean playing) {
        // Icon displayed should be opposite (if playing, then show pause, and vice versa)
        Image img = playing ? pauseImage : playImage;
        playPauseButton.setGraphic(new ImageView(img));
    }


    /**
     * Toggle the mute icon according to the user's action
     * @param audible False to show the muted icon, True to show a normal sound icon
     */
    private void setAudibleIcon(boolean audible) {
        this.audible = audible;
        Image img = audible ? unmutedImage : mutedImage;
        soundButton.setGraphic(new ImageView(img));
    }


//    private void setVolume(Double volume) {
//        volumeSlider.setValue(volume);
//    }


    /**
     * Update the sliders according to current values from the MediaPlayer
     */
    private void updateValues() {
        Platform.runLater(() -> {
            Duration currentTime = mp.getCurrentTime();
            timeSlider.setDisable(duration.isUnknown());
            updateLabels(currentTime, duration);

            if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
                timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
            }

            if (!volumeSlider.isValueChanging()) {
                volumeSlider.setValue((int)Math.round(mp.getVolume() * 100));
            }
        });
    }


    /**
     * Update the labels in the player to show the current progress in the MediaPlayer
     * @param elapsed The time index in the track at which the player is currently located
     * @param duration The amount of time remaining until the track has finished playing
     */
    private void updateLabels(Duration elapsed, Duration duration) {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);

        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }

        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        String progress = "00:00";
        String remainder = "-00:00";

        if (duration.greaterThan(Duration.ZERO)) {
            int intRemainder = (int)Math.floor(duration.toSeconds()) - intElapsed;
            int remainingHours = intRemainder  / (60 * 60);

            if (remainingHours > 0) {
                intRemainder -= remainingHours * 60 * 60;
            }

            int remainingMinutes = intRemainder / 60;
            int remainingSeconds = intRemainder - remainingHours * 60 * 60 - remainingMinutes * 60;
            if (remainingSeconds < 0) remainingSeconds = 0;

            if (remainingHours > 0) {
                progress = String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
                remainder = String.format("-%d:%02d:%02d", remainingHours, remainingMinutes, remainingSeconds);
            } else {
                progress = String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
                remainder = String.format("-%02d:%02d", remainingMinutes, remainingSeconds);
            }
        }

        progressLabel.setText(progress);
        remainingLabel.setText(remainder);
    }


    /**
     * Allow the user to click anywhere in the slider (not just the circle grabber)
     * Source: https://stackoverflow.com/a/48819784/4036688
     */
    private void handleSliderClickEvent(MouseEvent event, Slider slider) {
        slider.setValueChanging(true);
        double value = (event.getX() / slider.getWidth()) * slider.getMax();
        slider.setValue(value);
        slider.setValueChanging(false);
    }


}
