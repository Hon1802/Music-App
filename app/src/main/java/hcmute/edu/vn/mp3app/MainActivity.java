package hcmute.edu.vn.mp3app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import hcmute.edu.vn.mp3app.fragment.SongsFragment;
import hcmute.edu.vn.mp3app.model.Song;
import hcmute.edu.vn.mp3app.service.Mp3Service;

public class MainActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    public static TextView tvTitleSong, tvSingerSong;

    private RelativeLayout layout_bottom;

    public ImageView bt_prev, bt_next, bt_play_or_pause, bt_clear;
    private boolean isPlaying;
    private Song songs;
    public static int currentIndex;

    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Anh Xa
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        tvSingerSong = findViewById(R.id.tv_singer_main);
        tvTitleSong = findViewById(R.id.tv_title_main);
        layout_bottom = findViewById(R.id.layout_bottom_main);
        bt_prev = findViewById(R.id.img_prev_main);
        bt_play_or_pause = findViewById(R.id.img_play_or_pause_main);
        bt_next = findViewById(R.id.img_next_main);
        bt_clear = findViewById(R.id.img_clear_main);


        layout_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songs = new Song(currentIndex, tvTitleSong.getText().toString(),tvSingerSong.getText().toString(),R.drawable.dodo, "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/audios%2F"+tvTitleSong.getText().toString()+
                        ".mp3?alt=media&token=59760146-3398-4dd7-83a5-a00ebfef5b48");

                // Check if playing is true or false
                isPlayingOrNot();

                // Send data to Player Activity by Bundle
                Intent intent = new Intent(MainActivity.this, Player.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("object_song", songs);

                bundle.putBoolean("status_player", isPlaying);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    private void isPlayingOrNot(){
        ImageView pauseImg = new ImageView(MainActivity.this);
        pauseImg.setImageResource(R.drawable.ic_pause);

        int resId1 = bt_play_or_pause.getDrawable().getConstantState().hashCode();
        int resId2 = pauseImg.getDrawable().getConstantState().hashCode();

        if (resId1 == resId2) {
            isPlaying = true;
        } else {
            isPlaying = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}