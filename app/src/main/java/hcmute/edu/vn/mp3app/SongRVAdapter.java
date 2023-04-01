package hcmute.edu.vn.mp3app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import hcmute.edu.vn.mp3app.fragment.SongsFragment;
import hcmute.edu.vn.mp3app.model.Song;
import hcmute.edu.vn.mp3app.service.Mp3Service;

public class SongRVAdapter extends RecyclerView.Adapter<SongRVAdapter.ViewHolder>{

    public static ArrayList<Song> songArrayList;
    private Context context;
    public static TextView tv_songName, tv_singerName;
    public static ImageView img_song;
    private ClickListener mListener;
    public static int currentSongIndex;

    public interface ClickListener {
        void onItemClick(int position);
    }
    public SongRVAdapter(ArrayList<Song> songArrayList, Context context) {
        this.songArrayList = songArrayList;
        this.context = context;
    }

    public SongRVAdapter(ClickListener  listener){
        mListener = listener;
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
        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/images%2F"+song.getTitle()+".jpg?alt=media&token=35d08226-cbd8-4a61-a3f9-19e33caeb0cfv";
        Glide.with(context)
                .load(imageUrl)
                .into(img_song);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Mp3Service.player != null){
                    Mp3Service.player.release();
                    Mp3Service.player = null;
                }
                Song song = new Song(songArrayList.get(position).getIndex(), songArrayList.get(position).getTitle(),
                        songArrayList.get(position).getSinger(), songArrayList.get(position).getImage(), songArrayList.get(position).getResource());

                MainActivity.layout_bottom.setVisibility(View.VISIBLE);
                MainActivity.currentIndex = position;
                Player.selectedIndex = position;
                SongsFragment.selectedIndex = position;

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
        }
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(context, Mp3Service.class);
        intent.putExtra("action_music", action);

        context.startService(intent);
    }

}
