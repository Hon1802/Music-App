package hcmute.edu.vn.mp3app.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.provider.OpenableColumns;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import hcmute.edu.vn.mp3app.Global;
import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.UserDAO;
import hcmute.edu.vn.mp3app.activity.ChangePasswordActivity;
import hcmute.edu.vn.mp3app.activity.LoginActivity;
import hcmute.edu.vn.mp3app.activity.MainActivity;
import hcmute.edu.vn.mp3app.activity.UploadSong;
import hcmute.edu.vn.mp3app.adapter.SongRVAdapter;
import hcmute.edu.vn.mp3app.model.Song;
import hcmute.edu.vn.mp3app.model.User;
import hcmute.edu.vn.mp3app.service.Mp3Service;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ActivityResultLauncher<Intent> launcherImg;
    private ImageView img_avatar, img_done, img_cancel;
    private EditText et_name;
    private TextView tv_changePassword;
    private SongRVAdapter songRVAdapter;
    private TextView tv_logOut;
    private User user;
    private UserDAO userDAO;
    private Uri imageUri;
    private String imgName;
    private InputStream streamImg;
    private Song songs;
    private boolean isPlaying;
    private RelativeLayout layout_bottom;
    private MainActivity mainActivity;
    public static int selectedIndex;
    public static ImageView imgSong, imgPlayOrPause, imgPrev, imgNext, imgClear;
    public static TextView tvTitleSong, tvSingerSong;
    public static ArrayList<Song> songArrayList;


    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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


    @SuppressLint({"MissingInflatedId", "Range"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        img_avatar = view.findViewById(R.id.img_avatar);
        et_name = view.findViewById(R.id.et_name);
        tv_changePassword = view.findViewById(R.id.tv_changePassword);
        tv_logOut = view.findViewById(R.id.tv_logOut);
        img_done = view.findViewById(R.id.img_done);
        img_cancel = view.findViewById(R.id.img_cancel);
        layout_bottom = mainActivity.findViewById(R.id.layout_bottom_main);
        imgSong = mainActivity.findViewById(R.id.img_song_main);
        imgPlayOrPause = mainActivity.findViewById(R.id.img_play_or_pause_main);
        imgClear = mainActivity.findViewById(R.id.img_clear_main);
        tvTitleSong = mainActivity.findViewById(R.id.tv_title_main);
        tvSingerSong = mainActivity.findViewById(R.id.tv_singer_main);
        imgPrev = mainActivity.findViewById(R.id.img_prev_main);
        imgNext = mainActivity.findViewById(R.id.img_next_main);
        songRVAdapter = new SongRVAdapter();
        if(songs!= null){
            selectedIndex = MainActivity.currentIndex;
        }


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));




        userDAO = new UserDAO(getContext());

        user = userDAO.getUserByID(Global.GlobalUserID);

        if(user!=null){
            Glide.with(getContext())
                    .load(user.getAvatar())
                    .into(img_avatar);
//            int maxLength = 8; // Maximum length of characters allowed
//
//            InputFilter[] filters = new InputFilter[1];
//            filters[0] = new InputFilter.LengthFilter(maxLength);
//            et_name.setFilters(filters);

            et_name.setText(user.getName());
        }

        tv_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        tv_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });


        img_done.setVisibility(View.GONE);
        img_cancel.setVisibility(View.GONE);

        // Attach an OnClickListener to the EditText
        et_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the TextView and show the EditText
                img_done.setVisibility(View.VISIBLE);
                img_cancel.setVisibility(View.VISIBLE);

                // Enable editing when clicked
                et_name.setFocusableInTouchMode(true);
                et_name.setFocusable(true);
            }
        });

        img_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user.setName(et_name.getText().toString().trim());

                userDAO.updateUser(user);
                img_done.setVisibility(View.GONE);
                img_cancel.setVisibility(View.GONE);


            }
        });

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_done.setVisibility(View.GONE);
                img_cancel.setVisibility(View.GONE);
                et_name.setText(user.getName());
                et_name.clearFocus();
            }
        });

        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });


        launcherImg = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {

                            Uri selectedImageUri = data.getData();
                            imageUri = selectedImageUri;
                            if (null != selectedImageUri) {
                                // update the preview image in the layout
                                Context context = getActivity();
                                Cursor cursor = context.getContentResolver().query(selectedImageUri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    imgName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    cursor.close();
                                }
                                if(imgName.endsWith(".png")){
                                    imgName = imgName.substring(0, imgName.length()-4) +".jpg";
                                }

                                try {
                                    streamImg = context.getContentResolver().openInputStream(selectedImageUri);
                                } catch (FileNotFoundException e) {
                                    throw new RuntimeException(e);
                                }

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://tunebox-d7865.appspot.com");
                                StorageReference mp3Ref = storageRef.child("images/"+imgName);

                                UploadTask uploadTask = mp3Ref.putStream(streamImg);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get the download URL
                                        mp3Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri downloadUri) {
                                                // Delete old avatar
                                                if(!user.getAvatar().equals("https://firebasestorage.googleapis.com/v0/b/tunebox-d7865.appspot.com/o/images%2Favatar.png?alt=media&token=722dfe7b-0cd9-4d08-9e32-a7193f3cff10")){
                                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                                    StorageReference storageRef = storage.getReferenceFromUrl(user.getAvatar());

                                                    // Delete the file
                                                    storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // File deleted successfully
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Failed to delete the file
                                                        }
                                                    });
                                                }

                                                // Update user avatar with the download URL
                                                user.setAvatar(downloadUri.toString());
                                                userDAO.updateUser(user);

                                                // Update the ImageView with the selected image
                                                img_avatar.setImageURI(selectedImageUri);
                                                Toast.makeText(context, "Update Successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                        Toast.makeText(context, "Updating", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }
                });

        return view;
    }

    private void logOut(){
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        launcherImg.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(getActivity(), Mp3Service.class);
        intent.putExtra("action_music", action);

        getActivity().startService(intent);
    }
    private void updateInfo() {
        if(Mp3Service.player != null && songs != null){
            String imageUrl = "https://firebasestorage.googleapis.com/v0/b/tunebox-d7865.appspot.com/o/images%2F"+songs.getTitle()+".jpg?alt=media&token=35d08226-cbd8-4a61-a3f9-19e33caeb0cfv";
            Glide.with(mainActivity)
                    .load(imageUrl)
                    .into(imgSong);
            int maxLength = 7;
            if(songs.getTitle().trim().length() > maxLength){
                tvTitleSong.setText(songs.getTitle().trim().substring(0,maxLength) + "...");
            }
            else{
                tvTitleSong.setText(songs.getTitle());
            }

            if(songs.getSinger().trim().length() > maxLength){
                tvSingerSong.setText(songs.getSinger().trim().substring(0,maxLength) + "...");
            }
            else{
                tvSingerSong.setText(songs.getSinger());
            }
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
                    songs = new Song(selectedIndex, SongRVAdapter.songArrayList.get(selectedIndex).getTitle(), SongRVAdapter.songArrayList.get(selectedIndex).getSinger(), SongRVAdapter.songArrayList.get(selectedIndex).getImage(), SongRVAdapter.songArrayList.get(selectedIndex).getResource());
                    updateInfo();
                    sendActionToService(Mp3Service.ACTION_PREV);
                }
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedIndex >= 0 && selectedIndex < songRVAdapter.getItemCount()-1) {
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        if (songs != null) {
            selectedIndex = Mp3Service.currentSongIndex;
        }
        super.onResume();
    }
}