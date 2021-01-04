package com.marcfearby.controllers;

import com.marcfearby.Testing;
import com.marcfearby.interfaces.PlayerHandler;
import com.marcfearby.interfaces.PlaylistProvider;
import com.marcfearby.interfaces.TabPaneHandler;
import com.marcfearby.models.TabInfo;
import com.marcfearby.models.TrackInfo;
import com.marcfearby.utils.Global;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlainTabControllerTest  extends ApplicationTest {

    private TrackInfo restoredTrack = null;

    @BeforeClass
    public static void beforeClass() {
        Testing.setupTestFileSystem(false); // load mp3 blobs into file handles
    }


    @Override
    public void init() throws Exception {
        FxToolkit.registerStage(Stage::new);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/PlainTabView.fxml"));
        Tab tab = loader.load();
        PlainTabController ctrl = loader.getController();

        // Call to beforeClass() above will make sure this is the Jimfs file system
        FileSystem fs = Global.getFileSystem();

        Path home = fs.getPath(Testing.TESTING_PATH_MUSIC);

        class DummyPlayer implements PlayerHandler {
            @Override
            public void setPlaylistProvider(PlaylistProvider playlistProvider, boolean startPlaying) {
                // There is no PlayerHandler in this test class, so getNextTrack() won't otherwise be called
                restoredTrack = playlistProvider.getNextTrack();
            }
            @Override
            public void togglePlayPause() { }
        }

        class DummyTabPane implements TabPaneHandler {
            @Override
            public void addTab(Path path, boolean becomePlaylistProvider) { }
            @Override
            public void saveTabInfos(TabInfo updateTab) { }
            @Override
            public void activatePlaylistProvider() { }
            @Override
            public void setActiveTabController(AbstractTabController controller) { }
            @Override
            public void removeColourFromOtherTabInfos(TabInfo info) { }
        }

        TabInfo info = new TabInfo(TabInfo.TabType.PLAIN, home, true);
        info.setIsPlaylistProvider(true);
        info.setCurrentTrack(home.resolve("Haydn.mp3"));

        ctrl.init(info, new DummyTabPane(), new DummyPlayer());

        // Can't add a Tab to the Scene directly :-\
        TabPane dummyTabPane = new TabPane();
        dummyTabPane.getTabs().add(tab);

        stage.setScene(new Scene(dummyTabPane, 800, 600));
        stage.show();
        // Bring to front so that any test robots will work with this window
        stage.toFront();
        WaitForAsyncUtils.waitForFxEvents();
    }


    @Before
    public void setUp() {

    }


    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[] {});
        release(new MouseButton[] {});
    }


    @Test
    public void restores_current_track() {
        assertTrue(restoredTrack != null);
        // the nextTrack() should be Haydn if it was "restored" properly
        assertEquals("Haydn", restoredTrack.artistProperty().getValue());
    }

}
