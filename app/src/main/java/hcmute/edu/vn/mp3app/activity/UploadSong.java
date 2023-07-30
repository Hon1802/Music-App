package hcmute.edu.vn.mp3app.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import hcmute.edu.vn.mp3app.Global;
import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.UserDAO;
import hcmute.edu.vn.mp3app.adapter.SongRVAdapter;
import hcmute.edu.vn.mp3app.fragment.SongsFragment;
import hcmute.edu.vn.mp3app.model.Song;
import hcmute.edu.vn.mp3app.model.User;

public class UploadSong extends AppCompatActivity {
    private ActivityResultLauncher<Intent> launcherImg;
    private ActivityResultLauncher<Intent> launcherMp3;
    private Button bt_upload, bt_upload_img, bt_upload_music;
    public static TextView tv_music;
    public static EditText et_songName;
    public static ImageView img_song_upload;
    private String musicName;
    public static String imgName;
    private Song song;
    private SongRVAdapter songRVAdapter;
    private InputStream streamMp3;
    private InputStream streamImg;
    private Uri imageUri;
    private String singerName;
    private User user;
    private UserDAO userDAO;

    @SuppressLint({"MissingInflatedId", "Range"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_song);

        // Anh Xa
        bt_upload = findViewById(R.id.bt_upload);
        bt_upload_img = findViewById(R.id.bt_upload_img);
        bt_upload_music = findViewById(R.id.bt_upload_music);
        tv_music = findViewById(R.id.tv_song);
        et_songName = findViewById(R.id.et_songName);
        img_song_upload = findViewById(R.id.img_songUpload);
        songRVAdapter = new SongRVAdapter();

        userDAO = new UserDAO(getApplicationContext());
        user = userDAO.getUserByID(Global.GlobalUserID);
        if (user != null) {
            singerName = user.getName();
        }

        bt_upload_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp3Chooser();
            }
        });

        bt_upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        img_song_upload.setTag(et_songName.getText().toString());

        int index;
        if (SongsFragment.rv_song.getAdapter() == null) {
            index = 0;
        } else {
            index = SongsFragment.rv_song.getAdapter().getItemCount();
        }
        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                song = new Song(index, et_songName.getText().toString().trim(), singerName.trim(),
                        "https://firebasestorage.googleapis.com/v0/b/tunebox-d7865.appspot.com/o/images%2F" + et_songName.getText().toString().trim() + ".jpg?alt=media&token=bf500a20-f243-4123-a6e5-85fead1c805b", "https://firebasestorage.googleapis.com/v0/b/tunebox-d7865.appspot.com/o/audios%2F" + et_songName.getText().toString().trim() +
                        "?alt=media&token=59760146-3398-4dd7-83a5-a00ebfef5b48");
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://tunebox-d7865.appspot.com");
                StorageReference mp3Ref = storageRef.child("audios/" + musicName.trim());

                UploadTask uploadTask = mp3Ref.putStream(streamMp3);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Add the file name to the TextView
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadSong.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    }
                });

                StorageReference mp3Ref2 = storageRef.child("images/" + imgName.trim());

                UploadTask uploadTask2 = mp3Ref2.putStream(streamImg);
                uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Add the file name to the TextView
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadSong.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    }
                });


                myRef.child("Songs").child(String.valueOf(songRVAdapter.getItemCount())).setValue(song);
                Toast.makeText(UploadSong.this, "Upload Successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UploadSong.this, MainActivity.class);
                startActivity(intent);

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
                                Context context = UploadSong.this;
                                Cursor cursor = context.getContentResolver().query(selectedImageUri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    imgName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    cursor.close();
                                }
                                if (imgName.endsWith(".jpg")) {
                                    imgName = et_songName.getText().toString().trim() + ".jpg";
                                } else if (imgName.endsWith(".png")) {
                                    imgName = et_songName.getText().toString().trim() + ".jpg";
                                }

                                try {
                                    streamImg = context.getContentResolver().openInputStream(selectedImageUri);
                                } catch (FileNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                                img_song_upload.setImageURI(selectedImageUri);

                            }
                        }
                    }
                });

        launcherMp3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            Context context = UploadSong.this;
                            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                musicName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                cursor.close();
                            }

                            try {
                                streamMp3 = context.getContentResolver().openInputStream(uri);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                            if (musicName.endsWith(".mp3")) {
                                musicName = musicName.trim().substring(0, musicName.length() - 4);
                            }
                            tv_music.setText(musicName.trim());
                        }
                    }
                }
        );
    }

    void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        launcherImg.launch(Intent.createChooser(intent, "Select Picture"));
    }

    void mp3Chooser() {
        Intent intent = new Intent();
        intent.setType("audio/mpeg");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        launcherMp3.launch(Intent.createChooser(intent, "Select MP3 File"));
    }
}