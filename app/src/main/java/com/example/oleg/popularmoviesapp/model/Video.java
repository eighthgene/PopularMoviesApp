package com.example.oleg.popularmoviesapp.model;

public class Video {
    private String id;
    private String key;
    private String language;
    private String name;
    private String site;
    private int size;
    private String type;

    public Video(String id, String key, String language, String name, String site, int size, String type) {
        this.id = id;
        this.key = key;
        this.language = language;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}
