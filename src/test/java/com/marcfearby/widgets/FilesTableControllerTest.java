package com.marcfearby.widgets;

import com.marcfearby.interfaces.PlainTabHandler;
import com.marcfearby.utils.Global;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.ResourceBundle;
import static org.junit.Assert.*;

public class FilesTableControllerTest extends ApplicationTest {

    private FilesTableController ctrl;
    private TableView<Path> table;
    private Boolean becomePlaylistProviderStartPlaying;

    @Override
    public void init() throws Exception {
        FxToolkit.registerStage(Stage::new);
    }


    @Override
    public void start(Stage stage) throws Exception {
        Global.setTestMode();

        ResourceBundle bundle = ResourceBundle.getBundle("fxml.widgets.FilesTableView");
        URL location = getClass().getResource("/fxml/widgets/FilesTableView.fxml");
        FXMLLoader loader = new FXMLLoader(location, bundle);
        table = loader.load();
        ctrl = loader.getController();

        FileSystem fs = Global.getFileSystem();
        Path home = fs.getPath(Global.TESTING_PATH_MUSIC);

        class TabHandler implements PlainTabHandler {
            @Override
            public void changeTabRoot(Path path) { }
            @Override
            public void addTab(Path path) { }
            @Override
            public void becomePlaylistProvider(boolean startPlaying) {
                becomePlaylistProviderStartPlaying = startPlaying;
            }
        }

        ctrl.init(home, new TabHandler());

        stage.setScene(new Scene(table, 800, 600));
        stage.show();
        // Bring to front so that any test robots will work with this window
        stage.toFront();
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Before
    public void setUp() {
        becomePlaylistProviderStartPlaying = null;
    }


    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[] {});
        release(new MouseButton[] {});
    }


    @Test
    public void table_not_null() {
        assertNotNull(table);
    }


    @Test
    public void get_next_track() {
        String expected = Global.TESTING_MUSIC_FILE_1;
        Path next = ctrl.getNextTrack().getPath(); // First track should be Bach
        assertEquals(expected, next.toString());
    }


    @Test
    public void get_next_track_after_changing_sort() {
        String expected = Global.TESTING_MUSIC_FILE_3;
        clickOn("Name");                                // Change sort to descending order
        Path next = ctrl.getNextTrack().getPath(); // Previous track should now be Tchaikovsky
        assertEquals(expected, next.toString());
    }


    @Test
    public void get_previous_track() {
        String expected = Global.TESTING_MUSIC_FILE_1;
        ctrl.getNextTrack();                                 // Advance from Bach to Haydn
        Path next = ctrl.getPreviousTrack().getPath();  // Previous track should be Bach again
        assertEquals(expected, next.toString());
    }


    @Test
    public void get_previous_track_after_changing_sort() {
        String expected = Global.TESTING_MUSIC_FILE_3;
        ctrl.getNextTrack();                                 // First track should be Bach
        ctrl.getNextTrack();                                 // Advance from Bach to Haydn
        clickOn("Name");                                     // Change sort to descending order
        Path next = ctrl.getPreviousTrack().getPath();  // Previous track should now be Tchaikovsky
        assertEquals(expected, next.toString());
    }


    @Test
    public void double_click_then_get_next_track() {
        doubleClickOn("Haydn.mp3"); // Magic string will do for now
        assertNotNull(becomePlaylistProviderStartPlaying);
        assertEquals(true, becomePlaylistProviderStartPlaying);
        String expected = Global.TESTING_MUSIC_FILE_2;
        Path next = ctrl.getNextTrack().getPath(); // Next track should be Haydn (not Tchaikovsky!)
        assertEquals(expected, next.toString());
    }

}