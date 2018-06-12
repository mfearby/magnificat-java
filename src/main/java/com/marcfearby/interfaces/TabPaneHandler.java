package com.marcfearby.interfaces;

import java.nio.file.Path;

public interface TabPaneHandler {

    void addTab(Path path);

    void saveTabInfos();

    //void addTab(/* whatever parameters I dream up for a FancyTab */)

}
