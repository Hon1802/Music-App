package hcmute.edu.vn.mp3app.service;

import static hcmute.edu.vn.mp3app.service.Mp3Application.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import hcmute.edu.vn.mp3app.MainActivity;
//import hcmute.edu.vn.mp3app.Player;
import hcmute.edu.vn.mp3app.Player;
import hcmute.edu.vn.mp3app.R;
import hcmute.edu.vn.mp3app.SongRVAdapter;
import hcmute.edu.vn.mp3app.UploadSong;
import hcmute.edu.vn.mp3app.fragment.SongsFragment;
import hcmute.edu.vn.mp3app.model.Song;

public class Mp3Service extends Service {
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_PREV = 3;
    public static final int ACTION_NEXT = 4;
    public static final int ACTION_CLEAR = 5;

    public static final int ACTION_START = 6;



    public boolean isPlaying;
    public static MediaPlayer player;
    private String title;
    private String singer;
    private int currentSongIndex;
    public static Song songs;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Create Bundle to handle object song
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            Song song = (Song) bundle.get("object_song"); // key

            if (song != null){
                songs = song;
                startMusic(song);

                sendNotification(song);
            }
        }

        int actionMusic = intent.getIntExtra("action_music", 0);
        handleActionMusic(actionMusic);

        return START_NOT_STICKY;
    }

    private void startMusic(Song song) {
        if (player == null){
            player = MediaPlayer.create(getApplicationContext(), Uri.parse("https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/audios%2F"+song.getTitle()+
                    ".mp3?alt=media&token=59760146-3398-4dd7-83a5-a00ebfef5b48"));
        }
        player.start();
        isPlaying = true;
        sendActionToActivity(ACTION_START);
    }

    private void handleActionMusic(int action){
        switch (action){
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
            case ACTION_PREV:
                prevMusic();
                break;
            case ACTION_NEXT:
                nextMusic();
                break;
            case ACTION_CLEAR:
                stopSelf();
                sendActionToActivity(ACTION_CLEAR);
                break;
        }
    }

    public void pauseMusic(){
        if (player != null && isPlaying){
            player.pause();
            isPlaying = false;
            Player.StopAnimation();
            sendNotification(songs);
            sendActionToActivity(ACTION_PAUSE);
        }
    }

    public void resumeMusic(){
        if (player != null && isPlaying == false){
            player.start();
            isPlaying = true;
            Player.StartAnimation();
            sendNotification(songs);
            sendActionToActivity(ACTION_RESUME);
        }
    }

    // Stop player to start new song
    private void stopPlayer(){
        player.stop();
        player.release();
        player = null;
    }

    private void prevMusic(){
        if (player != null){
            stopPlayer();
            currentSongIndex = SongRVAdapter.currentSongIndex;
            if (currentSongIndex > 0) {
                currentSongIndex--;
            }
            changeMusic();
            sendNotification(songs);
            sendActionToActivity(ACTION_PREV);
        }
    }

    private void nextMusic(){
        if (player != null){
            stopPlayer();
            currentSongIndex = SongRVAdapter.currentSongIndex;
            if (currentSongIndex >=0 && currentSongIndex < SongsFragment.rv_song.getAdapter().getItemCount() - 1) {
                currentSongIndex++;
            }
            changeMusic();
            sendNotification(songs);
            sendActionToActivity(ACTION_NEXT);
        }
    }
    private void changeMusic(){
        title = SongRVAdapter.songArrayList.get(currentSongIndex).getTitle();
        singer = SongRVAdapter.songArrayList.get(currentSongIndex).getSinger();

        songs = new Song(currentSongIndex, title, singer, "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/images%2F"
                +title+".jpg?alt=media&token=35d08226-cbd8-4a61-a3f9-19e33caeb0cfv",
                "https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/audios%2F"+title+
                ".mp3?alt=media&token=59760146-3398-4dd7-83a5-a00ebfef5b48");
        if(player == null){
            player = MediaPlayer.create(getApplicationContext(), Uri.parse("https://firebasestorage.googleapis.com/v0/b/mp3app-ddd42.appspot.com/o/audios%2F"+title+
                    ".mp3?alt=media&token=59760146-3398-4dd7-83a5-a00ebfef5b48"));
        }
        if (isPlaying == true){
            player.start();
        }
    }
    private void sendNotification(Song song) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), MainActivity.img_song.getDrawable().getConstantState().hashCode());

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.media_notification);
        remoteViews.setTextViewText(R.id.tv_title, song.getTitle());
        remoteViews.setTextViewText(R.id.tv_singer, song.getSinger());


//        remoteViews.setImageViewBitmap(R.id.img_song, drawableToBitmap(MainActivity.img_song.getDrawable()));

        remoteViews.setImageViewResource(R.id.img_play_or_pause, R.drawable.ic_pause);
        remoteViews.setImageViewResource(R.id.img_next, R.drawable.ic_next);
        remoteViews.setImageViewResource(R.id.img_prev, R.drawable.ic_prev);


        if (isPlaying){
            remoteViews.setOnClickPendingIntent(R.id.img_play_or_pause, getPendingIntent(this, ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.img_play_or_pause, R.drawable.ic_pause);
        } else {
            remoteViews.setOnClickPendingIntent(R.id.img_play_or_pause, getPendingIntent(this, ACTION_RESUME));
            remoteViews.setImageViewResource(R.id.img_play_or_pause, R.drawable.ic_play);
        }

        remoteViews.setOnClickPendingIntent(R.id.img_prev, getPendingIntent(this, ACTION_PREV));
        remoteViews.setOnClickPendingIntent(R.id.img_next, getPendingIntent(this, ACTION_NEXT));

        remoteViews.setOnClickPendingIntent(R.id.img_clear, getPendingIntent(this, ACTION_CLEAR));

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();

        startForeground(1, notification);
    }

    private PendingIntent getPendingIntent(Context context, int action){
        Intent intent = new Intent(this, Mp3Receiver.class);
        intent.putExtra("action_music", action);

        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(player != null){
            player.release();
            player = null;
        }
    }

    public void sendActionToActivity(int action){
        Intent intent = new Intent("send_data_to_activity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", songs);
        bundle.putBoolean("status_player", isPlaying);
        bundle.putInt("action_music", action);

        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}