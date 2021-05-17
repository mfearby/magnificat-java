package com.marcfearby.controllers;

import com.marcfearby.Testing;
import com.marcfearby.interfaces.PlayerHandler;
import com.marcfearby.interfaces.PlaylistProvider;
import com.marcfearby.utils.Settings;
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
import java.util.List;
import static org.junit.Assert.*;

public class TabPaneControllerTest extends ApplicationTest {

    private TabPane tabs;
    private TabPaneController ctrl;

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
                "[tab1]",
                "path = " + Testing.TESTING_PATH_OTHER,
                "type = PLAIN",
                "active = false",
                "selected = " + Testing.TESTING_PATH_WHATEVER,
                "expanded = true",
                "playlistprovider = false",
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/TabPaneView.fxml"));
        tabs = loader.load();
        ctrl = loader.getController();

        class DummyHandler implements PlayerHandler {
            @Override
            public void setPlaylistProvider(PlaylistProvider playlistProvider, boolean startPlaying) { }
            @Override
            public void togglePlayPause() { }
            @Override
            public int getTrackPosition() {
                return 0;
            }
        }

        ctrl.init(new DummyHandler());

        stage.setScene(new Scene(tabs, 800, 600));
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
    public void test_tab_bold_state_on_startup() {
        Tab tab = tabs.getTabs().get(0);
        List<String> tab0 = tab.getStyleClass();
        //System.out.println("tab0 Classes: " + Arrays.toString(tab0.toArray()));

        assertTrue(tab0.contains("activeTab"));
        assertFalse(tab0.contains("inactiveTab"));

        // Neither of these classes should be applied to a tab which isn't controlling the player at startup
        List<String> tab1 = tabs.getTabs().get(1).getStyleClass();
        assertFalse(tab1.contains("inactiveTab"));
        assertFalse(tab1.contains("activeTab"));
    }


    @Test
    public void test_remove_bold_from_other_tab() {
        // This doesn't work anymore
        //clickOn("Other"); // select the second tab

        // Found this but it doesn't work (at least not now in Jan 2021): https://github.com/TestFX/TestFX/issues/634#issuecomment-431956100
        //clickOn(lookup(".tab-pane > .tab-header-area > .headers-region > .tab").nth(1).query());

        // Not ideal but it'll do
        tabs.getSelectionModel().select(1);

        doubleClickOn("Vaughan Williams");

        List<String> tab0 = tabs.getTabs().get(0).getStyleClass();
        assertTrue(tab0.contains("inactiveTab"));
        assertFalse(tab0.contains("activeTab"));

        List<String> tab1 = tabs.getTabs().get(1).getStyleClass();
        assertTrue(tab1.contains("activeTab"));
        assertFalse(tab1.contains("inactiveTab"));
    }

}