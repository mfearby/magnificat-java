package com.marcfearby.models;

import java.nio.file.Path;

public class TabInfo {

    public enum TabType { PLAIN, FANCY }

    private TabType type;
    private Path root;
    private boolean active;
    private String selectedTreePath = null;
    private boolean expanded = false;
    private boolean isPlaylistProvider = false;

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


    public String getSelectedTreePath() {
        return this.selectedTreePath;
    }
    public void setSelectedTreePath(String path) {
        this.selectedTreePath = path;
    }


    public boolean getExpanded() {
        return this.expanded;
    }
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }


    public boolean getIsPlaylistProvider() {
        return this.isPlaylistProvider;
    }
    public void setIsPlaylistProvider(boolean isProvider) {
        this.isPlaylistProvider = isProvider;
    }

}
