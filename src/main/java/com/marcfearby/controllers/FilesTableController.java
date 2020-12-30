package com.marcfearby.controllers;

import com.marcfearby.interfaces.PlainTabHandler;
import com.marcfearby.interfaces.PlaylistProvider;
import com.marcfearby.models.TrackInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class FilesTableController implements Initializable, PlaylistProvider {

    @FXML private TableView<TrackInfo> table;
    @FXML private TableColumn<TrackInfo, Integer> colPlaying;
    @FXML private TableColumn<TrackInfo, String> colName;
    @FXML private TableColumn<TrackInfo, String> colGenre;
    @FXML private TableColumn<TrackInfo, String> colArtist;
    @FXML private TableColumn<TrackInfo, String> colAlbum;
    @FXML private TableColumn<TrackInfo, String> colTitle;
    @FXML private TableColumn<TrackInfo, String> colTime;
    @FXML private TableColumn<TrackInfo, String> colTrackNumOf;
    @FXML private TableColumn<TrackInfo, String> colSize;
    @FXML private TableColumn<TrackInfo, String> colModified;
    @FXML private TableColumn<TrackInfo, String> colType;
    private PlainTabHandler tabHandler;
    private TrackInfo currentTrack = null;
    private boolean currentTrackWasChosen = false;
    private final Locale currentLocale = Locale.getDefault();
    private final Image pausedImage = new Image(getClass().getResourceAsStream("/icons/tango/media-playback-pause.png"));
    private final Image playingImage = new Image(getClass().getResourceAsStream("/icons/tango/audio-volume-high.png"));


    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
    }


    public void init(Path directory, PlainTabHandler tabHandler) {
        selectFolder(directory);
        this.tabHandler = tabHandler;

        // todo - Save user changes to the sorted column
        // Show the user that the Name column is sorted in ascending order by default.
        // The data is already sorted in selectFolder() below but that doesn't show the triangle.
        colName.setSortType(TableColumn.SortType.ASCENDING);
        table.getSortOrder().clear();
        table.getSortOrder().add(colName);
    }


    @SuppressWarnings("CodeBlock2Expr")
    private void setupTable() {
        table.setRowFactory(param -> {
            TableRow<TrackInfo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    setCurrentTackAndPlay(row.getItem());
                }
            });
            return row;
        });

        // Expects to find a public 'playingProperty' on the TrackInfo object
        colPlaying.setCellValueFactory(new PropertyValueFactory<>("playing"));

        colPlaying.setCellFactory(param -> {
            return new TableCell<TrackInfo, Integer>() {
                private ImageView img = new ImageView();
                @Override
                protected void updateItem(Integer playing, boolean empty) {
                    super.updateItem(playing, empty);
                    this.setStyle("-fx-padding: 3 0 0 5;");
                    if (empty || playing < 0) {
                        setGraphic(null);
                        return;
                    } else if (playing == 0) {
                        img.setImage(pausedImage);
                    } else {
                        img.setImage(playingImage);
                    }
                    setGraphic(img);
                }
            };
        });

        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));

        colArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));

        colAlbum.setCellValueFactory(new PropertyValueFactory<>("album"));

        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));

        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        colTrackNumOf.setCellValueFactory(new PropertyValueFactory<>("tracknumof"));

        colName.setCellValueFactory((TableColumn.CellDataFeatures<TrackInfo, String> param) -> {
            return new SimpleStringProperty(param.getValue().getPath().getFileName().toString());
        });

        colSize.setCellValueFactory((TableColumn.CellDataFeatures<TrackInfo, String> param) -> {
            String fancy;
            try {
                long size = Files.size(param.getValue().getPath());
                fancy = FileUtils.byteCountToDisplaySize(size);
            } catch (IOException e) {
                fancy = e.getMessage();
            }
            return new SimpleStringProperty(fancy);
        });

        colModified.setCellValueFactory((TableColumn.CellDataFeatures<TrackInfo, String> param) -> {
            // FULL: Sunday, 20 April 2014 1:01:54 PM AEST
            // LONG: 20 April 2014 1:01:54 PM
            // MEDIUM: 20/04/2014 1:01:54 PM
            // SHORT: 20/04/14 1:01 PM
            String fancy;
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, currentLocale);
            try {
                FileTime time = Files.getLastModifiedTime(param.getValue().getPath());
                fancy = formatter.format(time.toMillis());
            } catch (IOException e) {
                fancy = e.getMessage();
            }
            return new SimpleStringProperty(fancy);
        });

        colType.setCellValueFactory((TableColumn.CellDataFeatures<TrackInfo, String> param) -> {
            String ext = "";
            Path p = param.getValue().getPath();
            int i = p.getFileName().toString().lastIndexOf('.');
            if (i > 0) ext = p.getFileName().toString().substring(i + 1);
            return new SimpleStringProperty(ext.toUpperCase());
        });


        table.setOnKeyPressed(e -> {
            String key = e.getCode().toString();
            if (key.equals("SPACE")) {
                tabHandler.togglePlayPause();
            } else if (key.equals("ENTER")) {
                setCurrentTackAndPlay(table.getSelectionModel().getSelectedItem());
            }
        });
    }


    private void setCurrentTackAndPlay(TrackInfo track) {
        currentTrack = track;
        currentTrackWasChosen = true;
        // This will trigger the player to call getNextTrack() and play whatever it gets
        tabHandler.becomePlaylistProvider(true);
    }


    @Override
    public TrackInfo getNextTrack() {
        ObservableList<TrackInfo> items = table.getItems();

        if (items.isEmpty())
            return null;

        // If the user double-clicked on a row, return that instead of finding the next track
        if (currentTrackWasChosen) {
            currentTrackWasChosen = false;
            return currentTrack;
        }

        // Get the index of the current track (respective of the current sort in the TableView)
        int currentIndex = items.indexOf(currentTrack);
        int nextIndex = currentIndex + 1 < items.size() ? ++currentIndex : currentIndex;
        currentTrack = items.get(nextIndex);
        return currentTrack;
    }


    @Override
    public TrackInfo getPreviousTrack() {
        ObservableList<TrackInfo> items = table.getItems();

        if (items.isEmpty())
            return null;

        int currentIndex = items.indexOf(currentTrack);
        int previousIndex = currentIndex - 1 >= 0 ? --currentIndex : currentIndex;
        currentTrack = items.get(previousIndex);
        return currentTrack;
    }


    @Override
    public TrackInfo getRandomTrack() {
        // todo implement random track selection
        return null;
    }


    /**
     * Called whenever a new folder is selected in the TreeView
     * @param directory The path containing music files from which the TableView will be repopulated
     */
    public void selectFolder(Path directory) {
        ObservableList<TrackInfo> data = FXCollections.observableArrayList();

        if (Files.isDirectory(directory)) {
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory, "*.{mp3,m4a,flac}")) {
                for (Path path : dirStream) {
                    if (Files.isRegularFile(path) && !path.getFileName().startsWith(".")) {
                        data.add(new TrackInfo(path));
                    }
                }
            } catch (IOException e) {
                System.out.println("FilesTableController.selectFolder(): " + e);
            }
        }

        // Created a sorted list (which is also sorted by default)
        SortedList<TrackInfo> sortedData = new SortedList<>(data.sorted());
        // Allow the user can change the sort order themselves with the column headers
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);
    }

}