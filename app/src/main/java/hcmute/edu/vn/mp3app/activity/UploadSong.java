package hcmute.edu.vn.mp3app.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.fragment.SongsFragment;
import hcmute.edu.vn.mp3app.model.Song;

public class UploadSong extends AppCompatActivity {
    private Button bt_upload, bt_upload_img, bt_upload_music;
    public static TextView tv_music;
    public static EditText et_songName, et_singer_Name;
    public static ImageView img_song_upload;
    private String musicName;
    public static InputStream streamImg;
    public static String imgName;
    private Song song;

    @SuppressLint("MissingInflatedId")
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
        et_singer_Name = findViewById(R.id.et_singerName);
        img_song_upload = findViewById(R.id.img_songUpload);

        bt_upload_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new Intent to open the file dialog
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/mpeg"); // Set the MIME type MP3

                // Show the file chooser dialog
                startActivityForResult(Intent.createChooser(intent, "Select MP3 File"), 123);
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

        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                song = new Song(SongsFragment.rv_song.getAdapter().getItemCount(), et_songName.getText().toString(), et_singer_Name.getText().toString(),
                        "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/images%2F"+et_songName.getText().toString()+".jpg?alt=media&token=bf500a20-f243-4123-a6e5-85fead1c805b", "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/audios%2F"+et_songName.getText().toString()+
                        ".mp3?alt=media&token=59760146-3398-4dd7-83a5-a00ebfef5b48");
                myRef.child("Songs").push().setValue(song);
                Toast.makeText(UploadSong.this, "Upload Successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UploadSong.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }

    void imageChooser() {
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 456);
    }

    // Choose music
    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK) {

            Uri uri = data.getData();
            Context context = UploadSong.this;
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                musicName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
            }

            InputStream stream = null;
            try {
                stream = context.getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://mp3app-ddd42.appspot.com");
            StorageReference mp3Ref = storageRef.child("audios/"+musicName);

            UploadTask uploadTask = mp3Ref.putStream(stream);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(UploadSong.this, "Upload Successfully!", Toast.LENGTH_SHORT).show();
                    // Add the file name to the TextView
                    if(musicName.endsWith(".mp3")){
                        musicName = musicName.substring(0, musicName.length() -4);
                    }
                    tv_music.setText(musicName);
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
                    Toast.makeText(UploadSong.this, "Uploading", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (requestCode == 456 && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            if (null != selectedImageUri) {
                // update the preview image in the layout
                Context context = UploadSong.this;
                Cursor cursor = context.getContentResolver().query(selectedImageUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    imgName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    cursor.close();
                }
                if(imgName.endsWith(".jpg")){
                    imgName = et_songName.getText().toString()+".jpg";
                }
                else if(imgName.endsWith(".png")){
                    imgName = et_songName.getText().toString()+".jpg";
                }

                try {
                    streamImg = context.getContentResolver().openInputStream(selectedImageUri);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://mp3app-ddd42.appspot.com");
                StorageReference mp3Ref = storageRef.child("images/"+imgName);

                UploadTask uploadTask = mp3Ref.putStream(streamImg);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        img_song_upload.setImageURI(selectedImageUri);
                        Toast.makeText(UploadSong.this, "Upload Successfully!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(UploadSong.this, "Uploading", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }
}