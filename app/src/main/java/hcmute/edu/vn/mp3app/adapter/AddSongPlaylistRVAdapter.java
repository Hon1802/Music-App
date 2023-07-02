package hcmute.edu.vn.mp3app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hcmute.edu.vn.mp3app.Global;
import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.activity.PlaylistActivity;
import hcmute.edu.vn.mp3app.model.Playlist;
import hcmute.edu.vn.mp3app.model.Song;
import hcmute.edu.vn.mp3app.service.Mp3Service;

public class AddSongPlaylistRVAdapter extends RecyclerView.Adapter<AddSongPlaylistRVAdapter.ViewHolder>{

    public static ArrayList<Song> songArrayList;

    private Context context;
    public static TextView tv_songName, tv_singerName;
    public static ImageView img_song, img_plus;
    private ClickListener mListener;
    public static int currentSongIndex;

    public interface ClickListener {
        void onItemClick(int position);
    }
    public AddSongPlaylistRVAdapter(ArrayList<Song> songArrayList, Context context) {
        this.songArrayList = songArrayList;
        this.context = context;
    }

    public AddSongPlaylistRVAdapter(ClickListener  listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public AddSongPlaylistRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_add_song_to_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddSongPlaylistRVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Song song = songArrayList.get(position);
        tv_songName.setText(song.getTitle());
        tv_singerName.setText(song.getSinger());
        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/images%2F"+song.getTitle()+".jpg?alt=media&token=35d08226-cbd8-4a61-a3f9-19e33caeb0cfv";
        Glide.with(context)
                .load(imageUrl)
                .into(img_song);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reff;
                int playlist_index = PlaylistRVAdapter.currentPlaylistIndex;
                reff = FirebaseDatabase.getInstance().getReference().child("Playlist/user"+ Global.GlobalUserID).child(String.valueOf(PlaylistActivity.currentPlaylistIndex));
                if (song != null){
                    PlaylistRVAdapter.playlistArrayList.get(playlist_index).getArrayList().add(song);
                    Toast.makeText(context, "Added to playlist", Toast.LENGTH_SHORT).show();
                }

                Playlist playlist = new Playlist(PlaylistActivity.currentPlaylistIndex, PlaylistRVAdapter.playlistArrayList.get(PlaylistRVAdapter.currentPlaylistIndex).getName_playlist(),PlaylistRVAdapter.playlistArrayList.get(playlist_index).getArrayList());
                Map<String, Object> updates = new HashMap<>();
                updates.put("arrayList", playlist.getArrayList());
                reff.updateChildren(updates);
            }
        });
        currentSongIndex = position;
    }
    @Override
    public int getItemCount() {
        if(songArrayList != null){
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
        }
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(context, Mp3Service.class);
        intent.putExtra("action_music", action);

        context.startService(intent);
    }

}
