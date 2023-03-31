package hcmute.edu.vn.mp3app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.io.IOException;
import java.time.temporal.Temporal;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.mp3app.fragment.SongsFragment;
import hcmute.edu.vn.mp3app.model.Song;
import hcmute.edu.vn.mp3app.service.Mp3Service;

public class Player extends AppCompatActivity {

    private TextView tv_title, tv_singer, seek_begin, seek_end;
    private ImageView img_prev, img_play_or_pause, img_next;
    public static SeekBar seekBar;
    private Song songs;
    private boolean isPlaying;
    private int selectedIndex;
    private ImageView img_autoPlay;
    private boolean autoPlay;
    public static CircleImageView mCircleImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_player);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));

        // Anh Xa
        tv_title = findViewById(R.id.tv_title_player);
        tv_singer = findViewById(R.id.tv_singer_player);
        img_prev = findViewById(R.id.img_prev_player);
        img_play_or_pause = findViewById(R.id.img_play_or_pause_player);
        img_next = findViewById(R.id.img_next_player);
        seekBar = findViewById(R.id.seek_bar);
        seek_begin = findViewById(R.id.seek_begin);
        seek_end = findViewById(R.id.seek_end);
        img_autoPlay = findViewById(R.id.img_autoPlay);

        //rotation
        mCircleImage = findViewById(R.id.img_song_player);

        // Handle Music
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            songs = (Song) bundle.getSerializable("object_song");
            isPlaying = bundle.getBoolean("status_player");
            selectedIndex = SongRVAdapter.currentSongIndex;
            Toast.makeText(this, "Index: "+selectedIndex, Toast.LENGTH_SHORT).show();
            showInfoSong();
            setStatusPlayOrPause();
            StartAnimation();
        }

        onChanged();

        img_autoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(autoPlay){
                    autoPlay = false;
                    Toast.makeText(Player.this, "AutoPlay off", Toast.LENGTH_SHORT).show();
                }
                else {
                    autoPlay = true;
                    Toast.makeText(Player.this, "AutoPlay on", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        }
    }

    private void updateInfo() {
        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/images%2F"+songs.getTitle()+".jpg?alt=media&token=35d08226-cbd8-4a61-a3f9-19e33caeb0cfv";
        if (!Player.this.isFinishing ()){
            // Load the image using Glide
            Glide.with(getApplicationContext())
                    .load(imageUrl)
                    .into(mCircleImage);
        }
        tv_title.setText(songs.getTitle());
        tv_singer.setText(songs.getSinger());
        seekBar.setProgress(Mp3Service.player.getCurrentPosition());
        seekBar.setMax(Mp3Service.player.getDuration());

        // Set duration
        int durationInt = Mp3Service.player.getDuration();
        String durationString = convertDurationToString(durationInt);
        seek_end.setText(durationString);
        seek_begin.setText(convertDurationToString(Mp3Service.player.getCurrentPosition()));
        setStatusPlayOrPause();
    }

    // Touch Seekbar change activity
    private void onChanged(){
        final Handler handler = new Handler();
        Player.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(Mp3Service.player != null) {
                    updateInfo();
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void showInfoSong() {
        if (songs == null) {
            return;
        }


        updateInfo();

        img_play_or_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    StopAnimation();
                    sendActionToService(Mp3Service.ACTION_PAUSE);
                } else {
                    StartAnimation();
                    sendActionToService(Mp3Service.ACTION_RESUME);
                }
            }
        });

        img_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedIndex > 0) {
                    selectedIndex--;
                    songs = new Song(SongRVAdapter.songArrayList.get(selectedIndex).getIndex(), SongRVAdapter.songArrayList.get(selectedIndex).getTitle(),
                            SongRVAdapter.songArrayList.get(selectedIndex).getSinger(), SongRVAdapter.songArrayList.get(selectedIndex).getImage(), SongRVAdapter.songArrayList.get(selectedIndex).getResource());
                    updateInfo();
                    sendActionToService(Mp3Service.ACTION_PREV);
                }
            }
        });

        img_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedIndex >= 0 && selectedIndex < SongsFragment.rv_song.getAdapter().getItemCount() - 1) {
                    selectedIndex++;
                    songs = new Song(selectedIndex, SongRVAdapter.songArrayList.get(selectedIndex).getTitle(), SongRVAdapter.songArrayList.get(selectedIndex).getSinger(), SongRVAdapter.songArrayList.get(selectedIndex).getImage(), SongRVAdapter.songArrayList.get(selectedIndex).getResource());
                    updateInfo();
                    sendActionToService(Mp3Service.ACTION_NEXT);
                }
            }
        });
        MainActivity.currentIndex = selectedIndex;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    Mp3Service.player.seekTo(progress);
                    updateInfo();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Mp3Service.player.pause();
                updateInfo();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBars) {
                if(isPlaying)
                {
                    Mp3Service.player.start();
                    updateInfo();
                }
            }
        });

        Mp3Service.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                seekBar.setProgress(0);
                isPlaying = false;
                StopAnimation();
                updateInfo();
                if (autoPlay){
                    Mp3Service.player.start();
                    isPlaying = true;
                }
                else{
                    sendActionToService(Mp3Service.ACTION_PAUSE);
                }
            }
        });
    }

    private String convertDurationToString(int durationInMillis) {
        int seconds = (durationInMillis / 1000) % 60;
        int minutes = (durationInMillis / (1000 * 60)) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(this, Mp3Service.class);
        intent.putExtra("action_music", action);

        startService(intent);
    }

    private void setStatusPlayOrPause() {
        if (isPlaying) {
            img_play_or_pause.setImageResource(R.drawable.ic_pause);
        } else {
            img_play_or_pause.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        Glide.with(getApplicationContext()).pauseRequests();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Glide.with(this).pauseRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this).resumeRequests();
    }

    //rotation
    public static void StartAnimation(){
       Runnable runnable = new Runnable() {
           @Override
           public void run() {
                mCircleImage.animate().rotationBy(360).withEndAction(this).setDuration(5000).setInterpolator(new LinearInterpolator()).start();
           }
       };
        mCircleImage.animate().rotationBy(360).withEndAction(runnable).setDuration(5000).setInterpolator(new LinearInterpolator()).start();
    }

    public static void StopAnimation(){
        mCircleImage.animate().cancel();
    }
}
