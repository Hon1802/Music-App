package hcmute.edu.vn.mp3app.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import hcmute.edu.vn.mp3app.Global;
import hcmute.edu.vn.mp3app.activity.MainActivity;
import hcmute.edu.vn.mp3app.activity.PlaylistActivity;
import hcmute.edu.vn.mp3app.adapter.PlaylistRVAdapter;
import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.adapter.SongRVAdapter;
import hcmute.edu.vn.mp3app.model.Playlist;
import hcmute.edu.vn.mp3app.model.Song;
import hcmute.edu.vn.mp3app.service.Mp3Service;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Playlist playlist;
    public static RecyclerView rv_playlist;
    private PlaylistRVAdapter adapter;
    private ArrayList<Playlist> playlistArrayList = new ArrayList<Playlist>();
    private Song songs;
    private boolean isPlaying;
    public static ImageView imgSong, imgPlayOrPause, imgPrev, imgNext, imgClear, bt_created_playlist;
    public static TextView tvTitleSong, tvSingerSong;
    private RelativeLayout layout_bottom;
    private MainActivity mainActivity;
    public static int selectedIndex;


    public PlaylistsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistsFragment newInstance(String param1, String param2) {
        PlaylistsFragment fragment = new PlaylistsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        } else {
            throw new RuntimeException("MainActivity is required for this fragment");
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
//            playlist = (Playlist) bundle.get("object_playlist");
            songs = (Song) bundle.get("object_song");
            isPlaying = bundle.getBoolean("status_player");
            int actionMusic = bundle.getInt("action_music");

            handleLayoutMusic(actionMusic);

        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @SuppressLint({"Range", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);

        bt_created_playlist = view.findViewById(R.id.btn_playlist);
        rv_playlist = view.findViewById(R.id.rv_playlist);
        layout_bottom = mainActivity.findViewById(R.id.layout_bottom_main);
        imgSong = mainActivity.findViewById(R.id.img_song_main);
        imgPlayOrPause = mainActivity.findViewById(R.id.img_play_or_pause_main);
        imgClear = mainActivity.findViewById(R.id.img_clear_main);
        tvTitleSong = mainActivity.findViewById(R.id.tv_title_main);
        tvSingerSong = mainActivity.findViewById(R.id.tv_singer_main);
        imgPrev = mainActivity.findViewById(R.id.img_prev_main);
        imgNext = mainActivity.findViewById(R.id.img_next_main);
        if(songs != null){
            selectedIndex = MainActivity.currentIndex;
        }
//        Toast.makeText(mainActivity, "Index: "+selectedIndex, Toast.LENGTH_SHORT).show();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));

        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Playlist/user"+ Global.GlobalUserID);

        bt_created_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int count = (int) dataSnapshot.getChildrenCount();
                        Random random = new Random();
                        int randomNumber = random.nextInt(999);

                        playlist = new Playlist(randomNumber,"New",null);
                        reff.child(String.valueOf(randomNumber)).setValue(playlist);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors that may occur
                    }
                });
                Toast.makeText(getActivity(), "Created Playlist Successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReferenceFromUrl("https://mp3app-ddd42-default-rtdb.firebaseio.com/");
        DatabaseReference projectDetailsRef = rootRef.child("Playlist/user"+ Global.GlobalUserID);
        projectDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                playlistArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Get songs from firebase
                    Playlist playlist = dataSnapshot.getValue(Playlist.class);
                    playlistArrayList.add(playlist);
                    adapter = new PlaylistRVAdapter(playlistArrayList, getActivity());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                    rv_playlist.setLayoutManager(linearLayoutManager);
                    // setting our adapter to recycler view.
                    rv_playlist.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    updateInfo();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }
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

    private void updateInfo() {
        if(Mp3Service.player != null && songs != null){
            String imageUrl = "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/images%2F"+songs.getTitle()+".jpg?alt=media&token=35d08226-cbd8-4a61-a3f9-19e33caeb0cfv";
            Glide.with(mainActivity)
                    .load(imageUrl)
                    .into(imgSong);
            tvTitleSong.setText(songs.getTitle());
            tvSingerSong.setText(songs.getSinger());
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
                    songs = new Song(selectedIndex, SongRVAdapter.songArrayList.get(selectedIndex).getTitle(), SongRVAdapter.songArrayList.get(selectedIndex).getSinger(), SongRVAdapter.songArrayList.get(selectedIndex).getImage(), SongRVAdapter.songArrayList.get(selectedIndex).getResource());
                    updateInfo();
                    sendActionToService(Mp3Service.ACTION_PREV);
                }
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedIndex >= 0 && selectedIndex < PlaylistRVAdapter.playlistArrayList.get(PlaylistActivity.currentPlaylistIndex).getArrayList().stream().count() - 1) {
                    selectedIndex++;
                    songs = new Song(selectedIndex, SongRVAdapter.songArrayList.get(selectedIndex).getTitle(), SongRVAdapter.songArrayList.get(selectedIndex).getSinger(), SongRVAdapter.songArrayList.get(selectedIndex).getImage(), SongRVAdapter.songArrayList.get(selectedIndex).getResource());
                    updateInfo();
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

    private void setStatusPlayOrPause() {
        if (isPlaying) {
            imgPlayOrPause.setImageResource(R.drawable.ic_pause);
        } else {
            imgPlayOrPause.setImageResource(R.drawable.ic_play);
        }
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(mainActivity, Mp3Service.class);
        intent.putExtra("action_music", action);

        mainActivity.startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(songs!=null){
            selectedIndex=Mp3Service.currentSongIndex;
        }
        updateInfo();
//        Toast.makeText(mainActivity, "Index on resume: "+selectedIndex, Toast.LENGTH_SHORT).show();
    }
}