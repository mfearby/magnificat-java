package com.marcfearby.utils;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

/* This is probably a naughty name for a class, but what are you gonna do? ;-) */
public class Global {

    public static boolean isTesting = false;
    private static String testingHome = "/Users/marc";

    /**
     * Get the current file system in effect (default for normal use or Jimfs for testing)
     * @return A FileSystem object
     */
    public static FileSystem getFileSystem() {
        // I should look into dependency injection one day (perhaps with Dagger 2?)
        if (isTesting) {
            FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
            setupTestFileSystem(fs);
            return fs;
        } else {
            return FileSystems.getDefault();
        }
    }


    public static Path getUserHomeFolder() {
        FileSystem fs = getFileSystem();
        // Note: Don't use Paths.get() because it is hard wired to the default file system
        if (isTesting) {
            return fs.getPath(testingHome);
        } else {
            return fs.getPath(System.getProperty("user.home"));
        }
    }


    private static void setupTestFileSystem(FileSystem fs) {
        try {
            Path dir = fs.getPath(testingHome, "Music");
            Files.createDirectories(dir);

            Path test = dir.resolve("test.mp3");
            Files.createFile(test);

            Path more = dir.resolveSibling("Other/Whatever");
            Files.createDirectories(more);
//                Files.write(test, ImmutableList.of("asdf"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
