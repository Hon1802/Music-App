package hcmute.edu.vn.mp3app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.activity.AddSongActivity;
import hcmute.edu.vn.mp3app.activity.PlaylistActivity;
import hcmute.edu.vn.mp3app.model.Playlist;
import hcmute.edu.vn.mp3app.service.Mp3Service;

public class PlaylistRVAdapter extends RecyclerView.Adapter<PlaylistRVAdapter.ViewHolder>{

    public static ArrayList<Playlist> playlistArrayList;
    private Context context;
    public static TextView tv_playlistName;
//    public static ImageView img_song;
    private ClickListener mListener;

    public static int currentPlaylistIndex;
    
    private ImageView add_song_to_playlist;
    public interface ClickListener {
        void onItemClick(int position);
    }
    public PlaylistRVAdapter(ArrayList<Playlist> playlistArrayList, Context context) {
        this.playlistArrayList = playlistArrayList;
        this.context = context;
    }

    public PlaylistRVAdapter() {
    }

    public PlaylistRVAdapter(ClickListener  listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public PlaylistRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistRVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Playlist playlist = playlistArrayList.get(position);
        tv_playlistName.setText(playlist.getName_playlist());
//        holder.itemView.findViewById(R.id.add_playlist);
//        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/images%2F"+song.getTitle()+".jpg?alt=media&token=35d08226-cbd8-4a61-a3f9-19e33caeb0cfv";
//        Glide.with(context)
//                .load(imageUrl)
//                .into(img_song);
//        add_song_to_playlist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // add song to playlist
//                currentPlaylistIndex = position;
//                Intent intent = new Intent(context, AddSongActivity.class);
//                context.startActivity(intent);
//            }
//        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPlaylistIndex = position;
                PlaylistActivity.currentPlaylistIndex = currentPlaylistIndex;
                Intent intent = new Intent(context, PlaylistActivity.class);
                context.startActivity(intent);
            }
        });

    }
    public int getItemCount() {
        if(playlistArrayList != null){
            return playlistArrayList.size();
        }
        return 0;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our text views.

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views
            tv_playlistName = itemView.findViewById(R.id.tv_playlistName_rv);
//            add_song_to_playlist = itemView.findViewById(R.id.add_playlist);

        }
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(context, Mp3Service.class);
        intent.putExtra("action_music", action);
        context.startService(intent);
    }

}
