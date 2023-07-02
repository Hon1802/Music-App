package hcmute.edu.vn.mp3app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.adapter.PlaylistRVAdapter;
import hcmute.edu.vn.mp3app.adapter.SongPlaylistRVAdapter;
import hcmute.edu.vn.mp3app.adapter.SongRVAdapter;
import hcmute.edu.vn.mp3app.fragment.PlaylistsFragment;
import hcmute.edu.vn.mp3app.model.Playlist;
import hcmute.edu.vn.mp3app.model.Song;
import hcmute.edu.vn.mp3app.service.Mp3Service;

public class PlaylistActivity extends AppCompatActivity{
    private RecyclerView rv_add_song;
    private SongPlaylistRVAdapter adapter;
    private ArrayList<Song> songArrayList;
    private Song songs;
    private ImageView img_add_song_to_playlist;
    private boolean isPlaying;
    private RelativeLayout layout_bottom;
    private MainActivity mainActivity;
    public static int selectedIndex;
    public static ImageView imgSong, imgPlayOrPause, imgPrev, imgNext, imgClear;
    public static TextView tvTitleSong, tvSingerSong;
    public static int currentPlaylistIndex;


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            songs = (Song) bundle.get("object_song");
            isPlaying = bundle.getBoolean("status_player");
            int actionMusic = bundle.getInt("action_music");

            handleLayoutMusic(actionMusic);
        }
    };
    private void handleLayoutMusic(int action) {
        switch (action) {
            case Mp3Service.ACTION_START:
                showInfoSong();
                setStatusPlayOrPause();
                break;
            case Mp3Service.ACTION_PAUSE:
                setStatusPlayOrPause();
                break;
            case Mp3Service.ACTION_RESUME:
                setStatusPlayOrPause();
                break;
            case Mp3Service.ACTION_PREV:
                showInfoSong();
                break;
            case Mp3Service.ACTION_NEXT:
                showInfoSong();
                break;
            case Mp3Service.ACTION_CLEAR:
                layout_bottom.setVisibility(View.GONE);
                break;
        }
    }
    private void sendActionToService(int action) {
        Intent intent = new Intent(this, Mp3Service.class);
        intent.putExtra("action_music", action);

        startService(intent);
    }
    private void updateInfo() {
        if(Mp3Service.player != null){
            String imageUrl = "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/images%2F"+songs.getTitle()+".jpg?alt=media&token=35d08226-cbd8-4a61-a3f9-19e33caeb0cfv";
            Glide.with(this)
                    .load(imageUrl)
                    .into(imgSong);
            tvTitleSong.setText(songs.getTitle());
            tvSingerSong.setText(songs.getSinger());
        }

    }
    private void setStatusPlayOrPause() {
        if (isPlaying) {
            imgPlayOrPause.setImageResource(R.drawable.ic_pause);
        } else {
            imgPlayOrPause.setImageResource(R.drawable.ic_play);
        }
    }
    private void showInfoSong() {
        if (songs == null) {
            return;
        }
        updateInfo();
        imgPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    sendActionToService(Mp3Service.ACTION_PAUSE);
                } else {
                    sendActionToService(Mp3Service.ACTION_RESUME);
                }
            }
        });

        imgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedIndex > 0) {
                    selectedIndex--;
                    songs = new Song(selectedIndex, songArrayList.get(selectedIndex).getTitle(), songArrayList.get(selectedIndex).getSinger(), songArrayList.get(selectedIndex).getImage(), songArrayList.get(selectedIndex).getResource());
                    sendActionToService(Mp3Service.ACTION_PREV);
                }
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedIndex >= 0 && selectedIndex < PlaylistRVAdapter.playlistArrayList.get(currentPlaylistIndex).getArrayList().stream().count() - 1) {
                    selectedIndex++;
                    songs = new Song(selectedIndex, songArrayList.get(selectedIndex).getTitle(), songArrayList.get(selectedIndex).getSinger(), songArrayList.get(selectedIndex).getImage(), songArrayList.get(selectedIndex).getResource());
                    sendActionToService(Mp3Service.ACTION_NEXT);
                }
            }
        });

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendActionToService(Mp3Service.ACTION_CLEAR);
            }
        });
        MainActivity.currentIndex = selectedIndex;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        //Anh Xa
        mainActivity = new MainActivity();
        rv_add_song = findViewById(R.id.rv_song_playlist);
        songArrayList = new ArrayList<>();
        img_add_song_to_playlist = findViewById(R.id.img_add_song_to_playlist);
        layout_bottom = MainActivity.layout_bottom;
        imgSong = MainActivity.img_song;
        imgPlayOrPause = MainActivity.bt_play_or_pause;
        imgClear = MainActivity.bt_clear;
        tvTitleSong = MainActivity.tvTitleSong;
        tvSingerSong = MainActivity.tvSingerSong;
        imgPrev = MainActivity.bt_prev;
        imgNext = MainActivity.bt_next;

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));


//        rv_add_song.setAdapter(adapter);
        adapter = new SongPlaylistRVAdapter(songArrayList, PlaylistActivity.this);
//        adapter.setOnItemClickListener(this);



        img_add_song_to_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaylistActivity.this, AddSongActivity.class);
                startActivity(intent);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReferenceFromUrl("https://mp3app-ddd42-default-rtdb.firebaseio.com/");
        DatabaseReference projectDetailsRef = rootRef.child("Songs/");
        projectDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Load song

                    int playlist_index = PlaylistRVAdapter.currentPlaylistIndex;
                    songArrayList = PlaylistRVAdapter.playlistArrayList.get(playlist_index).getArrayList();
                    adapter = new SongPlaylistRVAdapter(songArrayList, PlaylistActivity.this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlaylistActivity.this, RecyclerView.VERTICAL, false);
                    rv_add_song.setLayoutManager(linearLayoutManager);

                    // setting our adapter to recycler view.
                    rv_add_song.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlaylistActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.currentIndex = selectedIndex;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReferenceFromUrl("https://mp3app-ddd42-default-rtdb.firebaseio.com/");
        DatabaseReference projectDetailsRef = rootRef.child("Songs/");
        projectDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Load song

                    int playlist_index = PlaylistRVAdapter.currentPlaylistIndex;
                    songArrayList = PlaylistRVAdapter.playlistArrayList.get(playlist_index).getArrayList();
                    adapter = new SongPlaylistRVAdapter(songArrayList, PlaylistActivity.this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlaylistActivity.this, RecyclerView.VERTICAL, false);
                    rv_add_song.setLayoutManager(linearLayoutManager);

                    // setting our adapter to recycler view.
                    rv_add_song.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlaylistActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        MainActivity.currentIndex = selectedIndex;
    }

//    @Override
//    public void onItemClick(int position) {
//
//    }
//
//    @Override
//    public void onWhatEverClick(int position) {
//        Toast.makeText(mainActivity, "Whatever click!!!!", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onDeleteClick(int position) {
//        Song selectedSong = songArrayList.get(position);
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Songs").child(String.valueOf(selectedSong.getIndex()));
//        // Delete the node
//        databaseReference.removeValue()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        // Node deleted successfully
//                        // Handle success case here
//                        Toast.makeText(mainActivity, "Delete success", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Failed to delete the node
//                        // Handle failure case here
//                    }
//                });
//    }

}