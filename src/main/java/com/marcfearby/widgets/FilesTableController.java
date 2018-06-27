package com.marcfearby.widgets;

import com.marcfearby.interfaces.PlainTabHandler;
import com.marcfearby.interfaces.PlaylistProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
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

    @FXML private TableView<Path> table;
    @FXML private TableColumn<Path, String> colName;
    @FXML private TableColumn<Path, String> colSize;
    @FXML private TableColumn<Path, String> colModified;
    @FXML private TableColumn<Path, String> colType;
    private PlainTabHandler tabHandler;
    private Path currentTrack = null;
    private boolean currentTrackWasChosen = false;
    private final Locale currentLocale = Locale.getDefault();


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
            TableRow<Path> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    this.currentTrack = row.getItem();
                    currentTrackWasChosen = true;
                    // This will trigger the player to call getNextTrack() and play whatever it gets
                    tabHandler.becomePlaylistProvider(true);
                }
            });
            return row;
        });

        colName.setCellValueFactory((TableColumn.CellDataFeatures<Path, String> param) -> {
            return new SimpleStringProperty(param.getValue().getFileName().toString());
        });

        colSize.setCellValueFactory((TableColumn.CellDataFeatures<Path, String> param) -> {
            String fancy;
            try {
                long size = Files.size(param.getValue());
                fancy = FileUtils.byteCountToDisplaySize(size);
            } catch (IOException e) {
                fancy = e.getMessage();
            }
            return new SimpleStringProperty(fancy);
        });

        colModified.setCellValueFactory((TableColumn.CellDataFeatures<Path, String> param) -> {
            // FULL: Sunday, 20 April 2014 1:01:54 PM AEST
            // LONG: 20 April 2014 1:01:54 PM
            // MEDIUM: 20/04/2014 1:01:54 PM
            // SHORT: 20/04/14 1:01 PM
            String fancy;
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, currentLocale);
            try {
                FileTime time = Files.getLastModifiedTime(param.getValue());
                fancy = formatter.format(time.toMillis());
            } catch (IOException e) {
                fancy = e.getMessage();
            }
            return new SimpleStringProperty(fancy);
        });

        colType.setCellValueFactory((TableColumn.CellDataFeatures<Path, String> param) -> {
            String ext = "";
            int i = param.getValue().getFileName().toString().lastIndexOf('.');
            if (i > 0) ext = param.getValue().getFileName().toString().substring(i + 1);
            return new SimpleStringProperty(ext.toUpperCase());
        });
    }


    @Override
    public Path getNextTrack() {
        ObservableList<Path> items = table.getItems();

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
    public Path getPreviousTrack() {
        ObservableList<Path> items = table.getItems();

        if (items.isEmpty())
            return null;

        int currentIndex = items.indexOf(currentTrack);
        int previousIndex = currentIndex - 1 >= 0 ? --currentIndex : currentIndex;
        currentTrack = items.get(previousIndex);
        return currentTrack;
    }


    @Override
    public Path getRandomTrack() {
        // todo implement random track selection
        return null;
    }


    /**
     * Called whenever a new folder is selected in the TreeView
     * @param directory The path containing music files from which the TableView will be repopulated
     */
    public void selectFolder(Path directory) {
        ObservableList<Path> data = FXCollections.observableArrayList();

        if (Files.isDirectory(directory)) {
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory, "*.{mp3,m4a,flac}")) {
                for (Path path : dirStream) {
                    if (Files.isRegularFile(path) && !path.getFileName().startsWith(".")) {
                        data.add(path);
                    }
                }
            } catch (IOException e) {
                System.out.println("FilesTableController.selectFolder(): " + e);
            }
        }

        // Created a sorted list (which is also sorted by default)
        SortedList<Path> sortedData = new SortedList<>(data.sorted());
        // Allow the user can change the sort order themselves with the column headers
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);
    }

}