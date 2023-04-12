package hcmute.edu.vn.mp3app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.adapter.SongPlaylistRVAdapter;
import hcmute.edu.vn.mp3app.adapter.SongRVAdapter;
import hcmute.edu.vn.mp3app.model.Song;

public class AddSongActivity extends AppCompatActivity {
    private RecyclerView rv_add_song;
    private SongPlaylistRVAdapter adapter;
    private ArrayList<Song> songArrayList;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        //Anh Xa
        rv_add_song = findViewById(R.id.rv_add_song);
        songArrayList = new ArrayList<>();

        // Load song
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReferenceFromUrl("https://mp3app-ddd42-default-rtdb.firebaseio.com/");
        DatabaseReference projectDetailsRef = rootRef.child("Songs/");
        projectDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                songArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Get songs from firebase
                    Song song = dataSnapshot.getValue(Song.class);
                    songArrayList.add(song);
                    adapter = new SongPlaylistRVAdapter(songArrayList, AddSongActivity.this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddSongActivity.this, RecyclerView.VERTICAL, false);
                    rv_add_song.setLayoutManager(linearLayoutManager);

                    // setting our adapter to recycler view.
                    rv_add_song.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddSongActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }
}