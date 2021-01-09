package com.marcfearby;

public class magnificat {

    // If the main class extends 'javafx.application.Application' then the uber jar won't work.
    // See this: https://github.com/javafxports/openjdk-jfx/issues/236
    public static void main(String[] args) {
        App.startup(args);
    }
}
