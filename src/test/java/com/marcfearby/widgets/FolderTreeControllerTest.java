package com.marcfearby.widgets;

import com.marcfearby.interfaces.FolderTreeHandler;
import com.marcfearby.interfaces.PlainTabHandler;
import com.marcfearby.utils.Global;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import static org.junit.Assert.*;

public class FolderTreeControllerTest extends ApplicationTest {

    private TreeView<Path> tree;
    private Path receivedPath = null;

    @Override
    public void init() throws Exception {
        FxToolkit.registerStage(Stage::new);
    }


    @Override
    public void start(Stage stage) throws Exception {
        Global.isTesting = true;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/widgets/FolderTreeView.fxml"));
        tree = loader.load();
        FolderTreeController ctrl = loader.getController();

        class TreeHandler implements FolderTreeHandler {
            @Override
            public void selectTreePath(Path path) {
                receivedPath = path;
            }
        }

        class TabHandler implements PlainTabHandler {
            @Override
            public void changeTabRoot(Path path) {
            }
            @Override
            public void addTab(Path path) {
                receivedPath = path;
            }
        }

        FileSystem fs = Global.getFileSystem();
        Path home = fs.getPath("/Users/marc");

        ctrl.init(home, new TreeHandler(), new TabHandler());

        stage.setScene(new Scene(tree, 800, 600));
        stage.show();
        // Bring to front so that any test robots will work with this window
        stage.toFront();
        WaitForAsyncUtils.waitForFxEvents();
    }


    @Before
    public void setUp() {
        receivedPath = null;
    }


    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[] {});
        release(new MouseButton[] {});
    }


    @Test
    public void tree_not_null() {
        assertNotNull(tree);
    }


    @Test
    public void tree_root_path_is_correct() {
        TreeItem<Path> root = tree.getRoot();
        assertEquals("Tree root path has an unexpected value", "/Users/marc", root.getValue().toString());
    }


    @Test
    public void select_root_node() {
        clickOn("Music");
        assertNotNull(receivedPath);
        assertEquals("Path received for selected tree node is incorrect", "/Users/marc/Music", receivedPath.toString());
    }


    @Test
    public void right_click_open_in_new_tab() {
        clickOn("Music", MouseButton.SECONDARY);
        clickOn("Open in new tab");
        assertNotNull(receivedPath);
        assertEquals("Path received for new tab is incorrect", "/Users/marc/Music", receivedPath.toString());
    }


//    // Not sure how to test this yet since it uses a javafx.stage.DirectoryChooser
//    @Test
//    public void change_root_node() {
//        clickOn("Music", MouseButton.SECONDARY);
//        clickOn("Select folder...");
//        assertNotNull(receivedPath);
//        assertEquals("Path received for new tree root is incorrect", "/Users/marc/Music", receivedPath.toString());
//    }

}