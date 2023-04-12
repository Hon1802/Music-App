package hcmute.edu.vn.mp3app.activity;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.adapter.PlaylistRVAdapter;
import hcmute.edu.vn.mp3app.model.Playlist;

public class Created_Playlist extends AppCompatActivity{
    private Button bt_upload ;
    public static EditText playlistName;
    private String stplaylistName;

    Playlist playlist;
    DatabaseReference reff;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);
        bt_upload = findViewById(R.id.crt_playlist);
        playlistName = (EditText) findViewById(R.id.playlistName);

        PlaylistRVAdapter playlistRVAdapter = new PlaylistRVAdapter();
        //firebase
        reff = FirebaseDatabase.getInstance().getReference().child("Playlist");

        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               playlist = new Playlist(PlaylistRVAdapter.currentPlaylistIndex,playlistName.getText().toString().trim(),null);
                reff.child(String.valueOf(playlistRVAdapter.getItemCount())).setValue(playlist);
                Toast.makeText(Created_Playlist.this, "Created Playlist Successfully!", Toast.LENGTH_SHORT).show();
                Toast.makeText(Created_Playlist.this, playlist.getName_playlist(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Created_Playlist.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
