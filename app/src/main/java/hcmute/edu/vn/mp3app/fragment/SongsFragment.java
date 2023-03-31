package hcmute.edu.vn.mp3app.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hcmute.edu.vn.mp3app.MainActivity;
import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.SongRVAdapter;
import hcmute.edu.vn.mp3app.UploadSong;
import hcmute.edu.vn.mp3app.ViewRecycleAdapter;
import hcmute.edu.vn.mp3app.model.Song;
import hcmute.edu.vn.mp3app.model.item_recycleview;
import hcmute.edu.vn.mp3app.service.Mp3Service;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button bt_upload_song;

    public static RecyclerView rv_song;

    private SongRVAdapter adapter;
    private ArrayList<Song> songArrayList;

    private Song songs;

    private boolean isPlaying;

    public static ImageView imgSong, imgPlayOrPause, imgPrev, imgNext, imgClear;

    private TextView tvTitleSong, tvSingerSong;

    private RelativeLayout layout_bottom;

    private MainActivity mainActivity;
    private int selectedIndex;

    private String fileName;

    public SongsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SongsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SongsFragment newInstance(String param1, String param2) {
        SongsFragment fragment = new SongsFragment();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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


    @SuppressLint({"Range", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        // Broadcast Manager
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));

        // Anh Xa
        bt_upload_song = view.findViewById(R.id.bt_upload_song);
        songArrayList = new ArrayList<>();
//        songArrayList =
        rv_song = view.findViewById(R.id.rv_song);

        layout_bottom = mainActivity.findViewById(R.id.layout_bottom_main);
        imgSong = mainActivity.findViewById(R.id.img_song_main);
        imgPlayOrPause = mainActivity.findViewById(R.id.img_play_or_pause_main);
        imgClear = mainActivity.findViewById(R.id.img_clear_main);
        tvTitleSong = mainActivity.findViewById(R.id.tv_title_main);
        tvSingerSong = mainActivity.findViewById(R.id.tv_singer_main);
        imgPrev = mainActivity.findViewById(R.id.img_prev_main);
        imgNext = mainActivity.findViewById(R.id.img_next_main);
        selectedIndex = SongRVAdapter.currentSongIndex;


        // Firebase Reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        // Button Click
        bt_upload_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UploadSong.class);
                startActivity(intent);
            }
        });

        return view;
    }

//    private List<item_recycleview> getListSong(){
//        List<item_recycleview> list = new ArrayList<>();
//
//        list.add(new item_recycleview(R.drawable.dodo,"Song one","Alibaba"));//
//        return list;
//    }
//
//    private void setAnimation(int animResource){
//        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(mainActivity,animResource);
//        rclView.setLayoutAnimation(layoutAnimationController);
//    }

//    public void clickStartMusic(int i) {
//        Song song = new Song(songArrayList.get(i).getIndex(), songArrayList.get(i).getTitle(), songArrayList.get(i).getSinger(), songArrayList.get(i).getImage(), songArrayList.get(i).getResource());
//
//        selectedIndex = i;
//        song.setIndex(i);
//        Intent intent = new Intent(getActivity(), Mp3Service.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("object_song", song);
//        intent.putExtras(bundle);
//
//        getActivity().startService(intent);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
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
//
        }
    }

    private void updateInfo() {
        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/images%2F"+songs.getTitle()+".jpg?alt=media&token=35d08226-cbd8-4a61-a3f9-19e33caeb0cfv";
        Glide.with(getActivity())
                .load(imageUrl)
                .into(imgSong);

        tvTitleSong.setText(songs.getTitle());
        tvSingerSong.setText(songs.getSinger());
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
                if (selectedIndex >= 0 && selectedIndex < rv_song.getAdapter().getItemCount() - 1) {
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
        Intent intent = new Intent(getActivity(), Mp3Service.class);
        intent.putExtra("action_music", action);

        getActivity().startService(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Songs");
//
//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                MatrixCursor cursor = new MatrixCursor(new String[] {"image", "index", "resource", "singer", "title"});
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Song song = snapshot.getValue(Song.class);
//                    cursor.addRow(new Object[] {song.getImage(), song.getIndex(), song.getResource(), song.getSinger(), song.getTitle()});
//                    songArrayList.add(song);
//                }
//                // You can use the cursor here or return the array list.
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle the error here.
//            }
//        });

        DatabaseReference rootRef = database.getReferenceFromUrl("https://mp3app-ddd42-default-rtdb.firebaseio.com/");
        DatabaseReference projectDetailsRef = rootRef.child("Songs/");
        projectDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Get songs from firebase
                    songs = dataSnapshot.getValue(Song.class);
                    songArrayList.add(songs);
                    adapter = new SongRVAdapter(songArrayList, getActivity());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                    rv_song.setLayoutManager(linearLayoutManager);

                    // setting our adapter to recycler view.
                    rv_song.setAdapter(adapter);
                    updateInfo();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }
}