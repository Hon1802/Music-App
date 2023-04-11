package hcmute.edu.vn.mp3app;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import hcmute.edu.vn.mp3app.fragment.SongsFragment;
import hcmute.edu.vn.mp3app.model.Playlist;
import hcmute.edu.vn.mp3app.model.Song;
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
        //firebase
        reff = FirebaseDatabase.getInstance().getReference().child("Playlist");

        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               playlist = new Playlist(playlistName.getText().toString().trim(),null);
                reff.push().setValue(playlist);
                Toast.makeText(Created_Playlist.this, "Created Playlist Successfully!", Toast.LENGTH_SHORT).show();
                Toast.makeText(Created_Playlist.this, playlist.getName_playlist(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Created_Playlist.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
