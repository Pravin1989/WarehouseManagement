package com.amiablecore.warehouse;

public class ItemObject {
    private int id;
    private String title;
    private String subtitle;

    public ItemObject(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public ItemObject(int id, String title, String subtitle) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
