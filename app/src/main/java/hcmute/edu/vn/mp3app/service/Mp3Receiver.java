package hcmute.edu.vn.mp3app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Mp3Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int actionMusic = intent.getIntExtra("action_music", 0);

        Intent intentService = new Intent(context, Mp3Service.class);
        intentService.putExtra("action_music", actionMusic);

        context.startService(intentService);
    }
}
