package com.example.prfc.Classes;

import android.widget.ImageView;

public class ImageList {
    private ImageView image;
    private String name;

    public ImageList() {
    }

    public ImageView getImage() {
        return image;
    }

    public void setItem(ImageView image) {
        this.image = image;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public ImageList(ImageView image, String name) {
        this.image = image;
        this.name = name;
    }
}
