package com.marcfearby.models;

import java.nio.file.Path;

public class TabInfo {

    public enum TabType { PLAIN, FANCY }

    private TabType type;
    private Path root;
    private boolean active;

    public TabInfo(TabType type, Path root, boolean active) {
        this.type = type;
        this.root = root;
        this.active = active;
    }

    public TabType getType() {
        return type;
    }

    public Path getRoot() {
        return root;
    }

    public void setRoot(Path root) {
        this.root = root;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
