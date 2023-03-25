package hcmute.edu.vn.mp3app.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import hcmute.edu.vn.mp3app.MainActivity;
import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.model.Song;
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

    public ArrayAdapter<String> mAdapter;
    public static ListView lv_song;

    private Song songs;

    private boolean isPlaying;

    private ImageView imgSong, imgPlayOrPause, imgPrev, imgNext, imgClear;

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
            if (bundle == null){
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
        lv_song = view.findViewById(R.id.lv_song);
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        lv_song.setAdapter(mAdapter);
        layout_bottom = mainActivity.findViewById(R.id.layout_bottom_main);
        imgSong = mainActivity.findViewById(R.id.img_song_main);
        imgPlayOrPause = mainActivity.findViewById(R.id.img_play_or_pause_main);
        imgClear = mainActivity.findViewById(R.id.img_clear_main);
        tvTitleSong = mainActivity.findViewById(R.id.tv_title_main);
        tvSingerSong = mainActivity.findViewById(R.id.tv_singer_main);
        imgPrev = mainActivity.findViewById(R.id.img_prev_main);
        imgNext = mainActivity.findViewById(R.id.img_next_main);
        selectedIndex = MainActivity.currentIndex;

        // Firebase Reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        // Button Click
        bt_upload_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new Intent to open the file dialog
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/mpeg"); // Set the MIME type MP3

                // Show the file chooser dialog
                startActivityForResult(Intent.createChooser(intent, "Select MP3 File"), 123);
            }
        });

        return view;
    }

    private void clickStartMusic(int i)
    {
        Song song = new Song(i, lv_song.getItemAtPosition(i).toString(), "HoangLam", R.drawable.dodo, "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/audios%2F"+lv_song.getItemAtPosition(i).toString()+".mp3?alt=media&token=59760146-3398-4dd7-83a5-a00ebfef5b48");

        selectedIndex = i;
        song.setIndex(i);
        Intent intent = new Intent(getActivity(), Mp3Service.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", song);
        intent.putExtras(bundle);

        getActivity().startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    private void handleLayoutMusic(int action){
        switch (action){
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
    private void updateInfo(){
        imgSong.setImageResource(songs.getImage());
        tvTitleSong.setText(songs.getTitle());
        tvSingerSong.setText(songs.getSinger());
    }
    private void showInfoSong(){
        if (songs == null){
            return;
        }

        updateInfo();

        imgPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying){
                    sendActionToService(Mp3Service.ACTION_PAUSE);
                } else {
                    sendActionToService(Mp3Service.ACTION_RESUME);
                }
            }
        });

        imgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedIndex > 0){
                    selectedIndex--;
                    songs = new Song(selectedIndex, lv_song.getItemAtPosition(selectedIndex).toString(), "HoangLam", R.drawable.dodo, "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/audios%2F"+lv_song.getItemAtPosition(selectedIndex).toString()+".mp3?alt=media&token=59760146-3398-4dd7-83a5-a00ebfef5b48");
                    updateInfo();
                    sendActionToService(Mp3Service.ACTION_PREV);
                }
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedIndex >= 0 && selectedIndex < lv_song.getCount() - 1){
                    selectedIndex++;
                    songs = new Song(selectedIndex, lv_song.getItemAtPosition(selectedIndex).toString(), "HoangLam", R.drawable.dodo, "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/audios%2F"+lv_song.getItemAtPosition(selectedIndex).toString()+".mp3?alt=media&token=59760146-3398-4dd7-83a5-a00ebfef5b48");
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

    private void setStatusPlayOrPause(){
        if (isPlaying){
            imgPlayOrPause.setImageResource(R.drawable.ic_pause);
        } else {
            imgPlayOrPause.setImageResource(R.drawable.ic_play);
        }
    }

    private void sendActionToService(int action){
        Intent intent = new Intent(getActivity(), Mp3Service.class);
        intent.putExtra("action_music", action);

        getActivity().startService(intent);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://mp3app-ddd42.appspot.com");
        StorageReference mp3Ref = storageRef.child("audios/");
        mp3Ref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                StringBuilder sb = new StringBuilder();
                for (StorageReference item : listResult.getItems()) {
                    if (item.getName().endsWith(".mp3")) {
                        sb.append(item.getName().substring(0, item.getName().length() - 4)).append("\n");
                        mAdapter.add(sb.toString());
                        sb.setLength(0);
                    }
                }
            }
        });

        lv_song.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clickStartMusic(i);
                layout_bottom.setVisibility(View.VISIBLE);
            }
        });
    }

    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK) {
            // Get the URI of the selected file
            Uri uri = data.getData();


            Context context = getActivity();
            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            // Add name of file MP3 to ListView
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
            }

            // Do something with the selected file
            // e.g. play the MP3 file
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://mp3app-ddd42.appspot.com");
            StorageReference mp3Ref = storageRef.child("audios/"+fileName);

            UploadTask uploadTask = mp3Ref.putStream(stream);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), "Upload Successfully!", Toast.LENGTH_SHORT).show();
                    // Add the file name to the ListView
                    if (fileName != null) {
                        fileName = fileName.substring(0,fileName.length() - 4);
                        mAdapter.add(fileName);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    Toast.makeText(getActivity(), "Uploading", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }}