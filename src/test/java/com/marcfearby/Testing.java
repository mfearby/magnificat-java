package com.marcfearby;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;

import com.marcfearby.utils.Global;

public class Testing {

    public static String TESTING_PATH_HOME = Global.TESTING_PATH_HOME;
    public static String TESTING_PATH_MUSIC = TESTING_PATH_HOME + "/Music";
    public static String TESTING_MUSIC_FILE_BACH = TESTING_PATH_MUSIC + "/Bach.mp3";
    public static String TESTING_MUSIC_FILE_HAYDN = TESTING_PATH_MUSIC + "/Haydn.mp3";
    public static String TESTING_MUSIC_FILE_TCHAIKOVSKY = TESTING_PATH_MUSIC + "/Tchaikovsky.mp3";
    public static String TESTING_PATH_OTHER = TESTING_PATH_HOME + "/Other";
    public static String TESTING_PATH_WHATEVER = TESTING_PATH_OTHER + "/Whatever";
    public static String TESTING_MUSIC_FILE_VAUGHAN_WILLIAMS = TESTING_PATH_WHATEVER + "/Vaughan_Williams.mp3";


    public static FileSystem getTestFileSystem() {
        Global.setTestMode(); // set this in case it hasn't been set before
        FileSystem fs = Global.getFileSystem();
        setupTestFileSystem(fs);
        return fs;
    }


    public static void setupTestFileSystem(FileSystem fs) {
        try {
            Path dir = fs.getPath(TESTING_PATH_MUSIC);
            Files.createDirectories(dir);

            Path bach = Files.createFile(dir.resolve(TESTING_MUSIC_FILE_BACH));
            loadTestMp3(bach, "Bach.mp3");

            Path haydn = Files.createFile(dir.resolve(TESTING_MUSIC_FILE_HAYDN));
            loadTestMp3(haydn, "Haydn.mp3");

            Path tchaikovsky = Files.createFile(dir.resolve(TESTING_MUSIC_FILE_TCHAIKOVSKY));
            loadTestMp3(tchaikovsky, "Tchaikovsky.mp3");

            Path more = fs.getPath(TESTING_PATH_WHATEVER);
            Files.createDirectories(more);

            Path vaughanWilliams = Files.createFile(more.resolve(TESTING_MUSIC_FILE_VAUGHAN_WILLIAMS));
            loadTestMp3(vaughanWilliams, "Vaughan_Williams.mp3");

//                Files.write(test, ImmutableList.of("asdf"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void loadTestMp3(Path target, String testResourceName) {
        URL mp3 = App.class.getResource("/mp3/" + testResourceName);
//        System.out.println(mp3);

        try (InputStream stream = new FileInputStream(mp3.getPath())) {
            long bytes = Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
//            System.out.println("Bytes copied: " + bytes + " to " + target.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
