package com.marcfearby.widgets;

import com.marcfearby.components.PlainTabController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
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

public class FilesTableController implements Initializable {

    private PlainTabController tab;
    private final Locale currentLocale = Locale.getDefault();
    @FXML private TableView<Path> table;
    @FXML private TableColumn<Path, String> colName;
    @FXML private TableColumn<Path, String> colSize;
    @FXML private TableColumn<Path, String> colModified;
    @FXML private TableColumn<Path, String> colType;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
    }


    private void setupTable() {
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


    public void init(PlainTabController tab, Path directory) {
        this.tab = tab;
        selectFolder(directory);
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
                System.out.println("FilesTableController.selectFolder() - Exception: " + e.getMessage());
            }
        }

        table.setItems(data);
    }

}
