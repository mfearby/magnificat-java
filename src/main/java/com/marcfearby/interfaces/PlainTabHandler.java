package com.marcfearby.interfaces;

import java.nio.file.Path;

public interface PlainTabHandler {

    void changeTabRoot(Path path);

    void addTab(Path path);

}
