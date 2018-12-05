package com.example.prfc.Classes;

import android.net.Uri;
import android.widget.ImageView;

public class ImageList {
    private Uri uri;
    private String name;

    public ImageList() {
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public ImageList(Uri uri, String name) {
        this.uri = uri;
        this.name = name;
    }
}
