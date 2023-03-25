package hcmute.edu.vn.mp3app.model;

public class item_recycleview {
    private int resourceId;
    private String name;
    private String author;

    public item_recycleview(int resourceId, String name, String author) {
        this.resourceId = resourceId;
        this.name = name;
        this.author = author;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
