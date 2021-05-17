package com.marcfearby.controllers;

import com.marcfearby.interfaces.PlayerHandler;
import com.marcfearby.interfaces.PlaylistProvider;
import com.marcfearby.interfaces.TabPaneHandler;
import com.marcfearby.models.AppSettings;
import com.marcfearby.models.TrackInfo;
import javafx.application.Platform;
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
import java.util.Timer;
import java.util.TimerTask;
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
    private boolean repeat = false; // TODO link this to a toggle button
    private boolean atEndOfMedia = false;
    private double volumeBeforeMuted = 1.0;
    private double currentVolume = 1.0;
    private PlaylistProvider playlistProvider = null;
    private AppSettings appSettings;
    private TrackInfo currentTrack = null;

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
            if (volumeSlider.isValueChanging()) {
                setCurrentVolume(volumeSlider.getValue() / 100.0);

                if (currentVolume > 0)
                    volumeBeforeMuted = currentVolume;
            }
        });
    }


    public void init(TabPaneHandler tabPaneHandler) {
        this.tabPaneHandler = tabPaneHandler;
        appSettings = AppSettings.getInstance();
        setCurrentVolume(appSettings.getVolume());
        volumeBeforeMuted = appSettings.getVolumeBeforeMute();
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
    public void togglePlayback() {
        togglePlayPause();
    }


    @FXML
    public void stopPlaying() {
        doStopPlaying();
    }


    @FXML
    public void goBack() {
        // This is simpler than goForward() because I'm not allowing this method to go to the previous folder, and the user
        // also can't play in reverse order. (if they want to play in reverse, they have to sort tracks accordingly and just play forward in the usual manner).
        TrackInfo track = getPlaylistProvider().getPreviousTrack();
        playNewTrack(track);
    }


    @FXML
    public void goForward() {
        doPlayNext(false);
    }


    @Override
    public void setPlaylistProvider(PlaylistProvider playlistProvider, boolean startPlaying) {
        this.playlistProvider = playlistProvider;

        if (startPlaying)
            doPlayNext(false);
    }


    @Override
    public void togglePlayPause() {
        // Most likely the user has just started the app, pressed Play, and didn't double-click on a track...
        // Or, the end of the playlist has been reached and 'repeat' is false, so start again from the beginning again.
        if (mp == null) {
            doPlayNext(true);
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
        } else {
            mp.pause();
        }
    }


    @FXML
    public void toggleSound() {
        this.setAudibleIcon(!audible);

        if (currentVolume > 0)
            volumeBeforeMuted = currentVolume;

        double vol = audible ? volumeBeforeMuted : 0;

        // If the user is unmuting and it's still 0, set it to 50% so that it's not 'stuck' on mute
        if (audible && vol == 0)
            vol = 0.5;

        setCurrentVolume(vol);
    }


    public int getTrackPosition() {
        if (mp == null) return 0;
        Duration currentTime = mp.getCurrentTime();
        return (int)Math.floor(currentTime.toSeconds());
    }



    ////////////////////
    // INTERNAL LOGIC //
    ////////////////////


    private void doPlayNext(boolean forceRepeat) {
        TrackInfo track = getPlaylistProvider().getNextTrack();

        if (track == null && repeat || forceRepeat) {
            track = getPlaylistProvider().getFirstTrack();
        }

        if (track == null) {
            doStopPlaying();
        } else {
            playNewTrack(track);
        }
    }



    /**
     * Start playing a new track (and stop playing the previous one, if any)
     * @param track The TrackInfo object for the new track to be played
     */
    private void playNewTrack(TrackInfo track) {
        // Kill the previous object if a track is currently playing/paused
        if (mp != null)
            mp.dispose();

        if (currentTrack != null)
            currentTrack.setPlaying(-1);

        currentTrack = track;
        currentTrack.setPlaying(1);

        Path path = currentTrack.getPath();
        Media media = new Media(path.toUri().toString());
        mp = new MediaPlayer(media);

        mp.setOnPlaying(() -> setPlayingIcon(true));
        mp.setOnPaused(() -> setPlayingIcon(false));
        mp.setOnStopped(this::doStopPlaying);

        int seconds = track.startPlayingAtSeconds().getValue();
        if (seconds > 0) {
            track.startPlayingAtSeconds().setValue(0);
            // Begin playback at this point; I had no luck with various attempts at mp.seek() here.
            mp.setStartTime(Duration.seconds(seconds));
        }

        mp.play();

        // Clear the start time or else the user won't be able to move the slider before this point
        mp.setOnPlaying(() -> mp.setStartTime(Duration.ZERO));

        atEndOfMedia = false;
        // Use the existing volume level for the new player object!
        mp.setVolume(currentVolume);

        String name = path.getFileName().toString();
        if (name.indexOf(".") > 0) name = name.substring(0, name.lastIndexOf("."));
        trackTitle.setText(name);

        mp.currentTimeProperty().addListener(ov -> updateDurationValues());

        mp.setOnReady(() -> {
            duration = mp.getMedia().getDuration();
            updateDurationValues();
        });

        mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);

        mp.setOnEndOfMedia(() -> {
            if (!repeat) {
                atEndOfMedia = true;
            }
            doPlayNext(false);
        });
    }


    private void doStopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.dispose();
            mp = null;
        }
        // remove playing icon from first table column
        if (currentTrack != null) currentTrack.setPlaying(-1);
        currentTrack = null;
        trackTitle.setText("Magnificat");
        timeSlider.setValue(0);
        setPlayingIcon(false);
    }


    /**
     * Get the current PlaylistProvider; if there isn't one, ask the TabPane
     * to get the active/selected tab to becomePlaylistProvider()
     * @return The current PlaylistProvider
     */
    private PlaylistProvider getPlaylistProvider() {
        // This will make sure that the active tab becomes the playlist provider
        if (playlistProvider == null)
            tabPaneHandler.activatePlaylistProvider();

        return playlistProvider;
    }


    private void setCurrentVolume(Double volume) {
        this.currentVolume = volume;
        volumeSlider.setValue(volume * 100); // this doesn't cause an endless loop

        if (mp != null)
            mp.setVolume(currentVolume);

        setAudibleIcon(currentVolume > 0);
        saveSettings();
    }





    /////////////////////////////////////////
    // UPDATE INTERFACE LABELS AND BUTTONS //
    /////////////////////////////////////////


    /**
     * Toggle the main play/pause icon according to the user's action
     * @param playing True to show the paused icon (i.e., it's currently playing),
     *                False to show the Play icon (i.e., it's currently not playing)
     */
    private void setPlayingIcon(boolean playing) {
        // Icon displayed should be opposite (if playing, then show pause, and vice versa)
        Image img = playing ? pauseImage : playImage;
        playPauseButton.setGraphic(new ImageView(img));
        if (currentTrack != null) {
            int status = playing ? 1 : 0;
            currentTrack.setPlaying(status);
        }
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


    /**
     * Update the time/progress slider according to current values from the MediaPlayer
     */
    private void updateDurationValues() {
        Platform.runLater(() -> {
            Duration currentTime = Duration.ZERO;

            // If the player has stopped entirely, set the duration to zero
            if (mp == null) {
                duration = Duration.ZERO;
            } else {
                currentTime = mp.getCurrentTime();
            }

            timeSlider.setDisable(duration.isUnknown());
            updateLabels(currentTime, duration);

            if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
                timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
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





    private Timer timer = null;
    private TimerTask task = null;

    // This is debounced to prevent the slider from saving lots of times as it moves
    private void saveSettings() {
        cancelTimer();
        timer = new Timer();

        task = new TimerTask() {
            public void run() {
                cancelTimer();
                appSettings.setVolume(currentVolume);
                appSettings.setVolumeBeforeMute(volumeBeforeMuted);
                appSettings.save();
            }
        };

        // Basic debouncing to save only the last call to this method (without using RxJava)
        timer.schedule(task, 500);
    }

    private void cancelTimer() {
        if (timer != null) {
            task.cancel();
            timer.cancel();
        }
    }

}