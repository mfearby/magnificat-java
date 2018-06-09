package com.marcfearby.models;

import java.io.File;

public class TabInfo {

    public enum TabType { PLAIN, FANCY }

    private TabType type;
    private File root;
    private boolean active;

    public TabInfo(TabType type, File root, boolean active) {
        this.type = type;
        this.root = root;
        this.active = active;
    }

    public TabType getType() {
        return type;
    }

    public File getRoot() {
        return root;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
