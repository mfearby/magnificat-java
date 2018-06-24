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
    private int currentIndex = -1;
    private final Locale currentLocale = Locale.getDefault();


    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
    }


    public void init(Path directory, PlainTabHandler tabHandler) {
        selectFolder(directory);
        this.tabHandler = tabHandler;
    }


    @SuppressWarnings("CodeBlock2Expr")
    private void setupTable() {
        table.setRowFactory(param -> {
            TableRow<Path> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    this.currentIndex = row.getIndex() - 1;
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

        // todo - this needs work now that the user can change the sort order in the table
        int i = currentIndex + 1 < items.size() ? ++currentIndex : currentIndex;
        return items.get(i);
    }


    @Override
    public Path getPreviousTrack() {
        ObservableList<Path> items = table.getItems();

        if (items.isEmpty())
            return null;

        // todo - this needs work now that the user can change the sort order in the table
        int i = currentIndex - 1 >= 0 ? --currentIndex : currentIndex;
        return items.get(i);
    }


    @Override
    public Path getRandomTrack() {
        // todo implement random track selection
        return null;
    }


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