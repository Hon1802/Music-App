package hcmute.edu.vn.mp3app.model;

import java.io.Serializable;

public class Song implements Serializable {
    private int index;
    private String title;
    private String singer;
    private int image;
    private String resource;

    public Song(int index, String title, String singer, int image, String resource) {
        this.index = index;
        this.title = title;
        this.singer = singer;
        this.image = image;
        this.resource = resource;
    }
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
