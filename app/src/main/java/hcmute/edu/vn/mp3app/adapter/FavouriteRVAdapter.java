package hcmute.edu.vn.mp3app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import hcmute.edu.vn.mp3app.Global;
import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.activity.MainActivity;
import hcmute.edu.vn.mp3app.activity.Player;
import hcmute.edu.vn.mp3app.activity.PlaylistActivity;
import hcmute.edu.vn.mp3app.fragment.FavoriteFragment;
import hcmute.edu.vn.mp3app.fragment.PlaylistsFragment;
import hcmute.edu.vn.mp3app.fragment.SongsFragment;
import hcmute.edu.vn.mp3app.model.Song;
import hcmute.edu.vn.mp3app.service.Mp3Service;

public class FavouriteRVAdapter extends RecyclerView.Adapter<FavouriteRVAdapter.ViewHolder>{
    public static ArrayList<Song> songArrayList;

    private Context context;
    public static TextView tv_songName, tv_singerName;
    public static ImageView img_song, img_plus;
    private AdapterView.OnItemClickListener mListener;
    public static int currentSongIndex;

    public FavouriteRVAdapter(ArrayList<Song> songArrayList, Context context) {
        this.songArrayList = songArrayList;
        this.context = context;
    }

    public FavouriteRVAdapter() {
    }

    @NonNull
    @Override
    public FavouriteRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_favourite, parent, false);
        return new FavouriteRVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteRVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Song song = songArrayList.get(position);
        tv_songName.setText(song.getTitle());
        tv_singerName.setText(song.getSinger());
        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/images%2F" + song.getTitle() + ".jpg?alt=media&token=35d08226-cbd8-4a61-a3f9-19e33caeb0cfv";
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
        currentSongIndex = position;


        //Add to favourite
        img_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (song != null ) {
                    // Remove Favourite
                    DatabaseReference reff;
                    reff = FirebaseDatabase.getInstance().getReference().child("Favourite").child("user"+ Global.GlobalUserID).child(String.valueOf(song.getIndex()));

                    // Delete song from favourite recyclerView
                    songArrayList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, songArrayList.size());

                    reff.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Child node deleted successfully
                                Toast.makeText(context, "Removed from Favourite", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to delete child node
                            Toast.makeText(context, "Delete failed!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    DatabaseReference reff2;
                    reff2 = FirebaseDatabase.getInstance().getReference().child("Songs").child(String.valueOf(song.getIndex()));

                    reff2.setValue(song);
                    img_plus.setImageResource(R.drawable.ic_like);
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
            img_plus = itemView.findViewById(R.id.unFavourite);

        }
    }

}
