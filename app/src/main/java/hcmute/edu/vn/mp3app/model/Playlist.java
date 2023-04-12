package hcmute.edu.vn.mp3app.model;
import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {
    private int playlist_index;
    private String name_playlist;
    private ArrayList<Song> arrayList = new ArrayList<Song>();

    public ArrayList<Song> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<Song> arrayList) {
        this.arrayList = arrayList;
    }
    public String getName_playlist() {
        return name_playlist;
    }

    public void setName_playlist(String name_playlist) {
        this.name_playlist = name_playlist;

    }

    public int getPlaylist_index() {
        return playlist_index;
    }

    public void setPlaylist_index(int playlist_index) {
        this.playlist_index = playlist_index;
    }

    public Playlist(int playlist_index, String name_playlist, ArrayList<Song> arrayList) {
        this.playlist_index = playlist_index;
        this.name_playlist = name_playlist;
        this.arrayList = arrayList;
    }
    public Playlist() {
    }
}
