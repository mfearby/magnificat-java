package com.marcfearby.controllers;
import com.marcfearby.App;
import com.marcfearby.Testing;
import com.marcfearby.utils.Settings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;
import org.testfx.framework.junit.ApplicationTest;
import static org.junit.Assert.assertTrue;

public class WindowControllerTest extends ApplicationTest {

    @BeforeClass
    public static void beforeClass() {
        Testing.setupTestFileSystem(false); // load mp3 blobs into file handles
        Settings.setTestMode();

        String settings = String.join("\n",
                "[tab0]",
                "path = " + Testing.TESTING_PATH_MUSIC,
                "type = PLAIN",
                "active = true",
                "selected = " + Testing.TESTING_PATH_MUSIC,
                "expanded = true",
                "playlistprovider = true",
                "");

        Settings sets =  Settings.getInstance();
        sets.setTestTabSettings(settings);
    }

    @Override
    public void init() throws Exception {
        FxToolkit.registerStage(Stage::new);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // The root component is a BorderPane
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/Window.fxml"));
        Parent window = loader.load();
        WindowController ctrl = loader.getController();
        ctrl.init(stage, window);

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
    public void show_window() {
        // keep it open so I can interact with the test file system for a minute, although this isn't
        // going to be that useful because javafx.scene.media.Media can't open files from Jimfs :-/
        //sleep(100000);
        assertTrue(true);
    }

}
