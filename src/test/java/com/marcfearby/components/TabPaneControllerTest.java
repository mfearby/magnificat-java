package com.marcfearby.components;

import com.marcfearby.interfaces.PlayerHandler;
import com.marcfearby.interfaces.PlaylistProvider;
import com.marcfearby.utils.Global;
import com.marcfearby.utils.Settings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import java.util.List;
import static org.junit.Assert.*;

public class TabPaneControllerTest extends ApplicationTest {

    private TabPane tabs;
    private TabPaneController ctrl;

    @Override
    public void init() throws Exception {
        FxToolkit.registerStage(Stage::new);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Global.setTestMode();
        Settings.setTestMode();

        String settings = String.join("\n",
            "[tab0]",
            "path = " + Global.TESTING_PATH_MUSIC,
            "type = PLAIN",
            "active = true",
            "selected = " + Global.TESTING_PATH_MUSIC,
            "expanded = true",
            "playlistprovider = true",
            "[tab1]",
            "path = " + Global.TESTING_PATH_OTHER,
            "type = PLAIN",
            "active = false",
            "selected = " + Global.TESTING_PATH_WHATEVER,
            "expanded = true",
            "playlistprovider = false",
            "");

        Settings.getInstance().setTestTabSettings(settings);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/TabPaneView.fxml"));
        tabs = loader.load();
        ctrl = loader.getController();

        class Handler implements PlayerHandler {
            @Override
            public void setPlaylistProvider(PlaylistProvider playlistProvider, boolean startPlaying) {

            }

            @Override
            public void togglePlayPause() {

            }
        }

        ctrl.init(new Handler());

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
        List<String> tab0 = tabs.getTabs().get(0).getStyleClass();
        assertTrue(tab0.contains("activeTab"));
        assertFalse(tab0.contains("inactiveTab"));

        // Neither of these classes should be applied to a tab which isn't controlling the player at startup
        List<String> tab1 = tabs.getTabs().get(1).getStyleClass();
        assertFalse(tab1.contains("inactiveTab"));
        assertFalse(tab1.contains("activeTab"));
    }


    @Test
    public void test_remove_bold_from_other_tab() {
        clickOn("Other"); // select the second tab
        doubleClickOn("Wagner.mp3"); // Magic string will do for now

        List<String> tab0 = tabs.getTabs().get(0).getStyleClass();
        assertTrue(tab0.contains("inactiveTab"));
        assertFalse(tab0.contains("activeTab"));

        List<String> tab1 = tabs.getTabs().get(1).getStyleClass();
        assertTrue(tab1.contains("activeTab"));
        assertFalse(tab1.contains("inactiveTab"));
    }

}