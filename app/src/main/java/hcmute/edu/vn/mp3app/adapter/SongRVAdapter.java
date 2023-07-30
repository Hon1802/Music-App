package hcmute.edu.vn.mp3app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hcmute.edu.vn.mp3app.Global;
import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.activity.MainActivity;
import hcmute.edu.vn.mp3app.activity.Player;
import hcmute.edu.vn.mp3app.activity.PlaylistActivity;
import hcmute.edu.vn.mp3app.fragment.FavoriteFragment;
import hcmute.edu.vn.mp3app.fragment.PlaylistsFragment;
import hcmute.edu.vn.mp3app.fragment.SongsFragment;
import hcmute.edu.vn.mp3app.model.Playlist;
import hcmute.edu.vn.mp3app.model.Song;
import hcmute.edu.vn.mp3app.service.Mp3Service;

public class SongRVAdapter extends RecyclerView.Adapter<SongRVAdapter.ViewHolder> {

    public static ArrayList<Song> songArrayList;

    private Context context;
    public static TextView tv_songName, tv_singerName;
    public static ImageView img_song, img_plus;
    private AdapterView.OnItemClickListener mListener;
    public static int currentSongIndex;
    public static ImageView imageView;

    public SongRVAdapter(ArrayList<Song> songArrayList, Context context) {
        this.songArrayList = songArrayList;
        this.context = context;
    }

    public SongRVAdapter() {
    }

    @NonNull
    @Override
    public SongRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongRVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Song song = songArrayList.get(position);
        tv_songName.setText(song.getTitle());
        tv_singerName.setText(song.getSinger());

//        int maxLength = 12;
//        if(song.getTitle().trim().length() > maxLength){
//            tv_songName.setText(song.getTitle().trim().substring(0,maxLength) + "...");
//        }
//        else{
//            tv_songName.setText(song.getTitle());
//        }
//
//        if(song.getSinger().trim().length() > maxLength){
//            tv_singerName.setText(song.getSinger().trim().substring(0,maxLength) + "...");
//        }
//        else{
//            tv_singerName.setText(song.getSinger());
//        }

        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/tunebox-d7865.appspot.com/o/images%2F" + song.getTitle() + ".jpg?alt=media&token=35d08226-cbd8-4a61-a3f9-19e33caeb0cfv";
        Glide.with(context)
                .load(imageUrl)
                .into(img_song);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Mp3Service.player != null) {
                    Mp3Service.player.release();
                    Mp3Service.player = null;
                }
                Song song = new Song(songArrayList.get(position).getIndex(), songArrayList.get(position).getTitle(),
                        songArrayList.get(position).getSinger(), songArrayList.get(position).getImage(), songArrayList.get(position).getResource());

                MainActivity.layout_bottom.setVisibility(View.VISIBLE);
                MainActivity.currentIndex = position;
                Player.selectedIndex = position;
                SongsFragment.selectedIndex = position;
                PlaylistsFragment.selectedIndex = position;
                FavoriteFragment.selectedIndex=position;
                PlaylistActivity.selectedIndex = position;
                currentSongIndex = position;


                Intent intent = new Intent(context, Mp3Service.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("object_song", song);
                intent.putExtras(bundle);
                context.startService(intent);

                Intent intent2 = new Intent(context, Player.class);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("object_song", song);

                bundle2.putBoolean("status_player", true);

                intent2.putExtras(bundle2);
                context.startActivity(intent2);

                // Update info on notification
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, Mp3Service.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("object_song", song);
                        intent.putExtras(bundle);
                        context.startService(intent);
                    }
                }, 3000);

            }
        });


        //Add to favourite
        img_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (song != null ) {
                        DatabaseReference reff;
                        reff = FirebaseDatabase.getInstance().getReference().child("Favourite").child("user"+ Global.GlobalUserID);
                        reff.child(String.valueOf(position)).setValue(song);
                        Toast.makeText(context, "Added to Favourite", Toast.LENGTH_SHORT).show();

                        DatabaseReference reff2;
                        reff2 = FirebaseDatabase.getInstance().getReference().child("Songs").child(String.valueOf(song.getIndex()));

                        reff2.setValue(song);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (songArrayList != null) {
            return songArrayList.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our text views.

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views
            tv_songName = itemView.findViewById(R.id.tv_songName_rv);
            tv_singerName = itemView.findViewById(R.id.tv_singerName_rv);
            img_song = itemView.findViewById(R.id.img_song_rv);
            img_plus = itemView.findViewById(R.id.plus);
            imageView = itemView.findViewById(R.id.imageTemp);

        }
    }

}

