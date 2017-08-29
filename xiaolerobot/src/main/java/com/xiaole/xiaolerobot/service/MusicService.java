package com.xiaole.xiaolerobot.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.xiaole.xiaolerobot.util.Constant;

import static com.xiaole.xiaolerobot.ui.activity.MenuActivity.mStateManagementHandler;

/**
 * Created by yinyu-tiejiang on 17-8-29.
 */

public class MusicService extends Service implements MediaPlayer.OnCompletionListener{

    MediaPlayer mMediaPlayer;
    private String musicUrl;

    @Override
    public IBinder onBind(Intent intent) {
//        Log.d("TIEJIANG", "MusicService---onBind");
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d("TIEJIANG", "MusicService---onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Log.d("TIEJIANG", "MusicService---onStartCommand");
        musicUrl = intent.getStringExtra("music_url");
        Log.d("TIEJIANG", "MusicService---onStartCommand" + " musicUrl= " + musicUrl);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(musicUrl);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (!mMediaPlayer.isPlaying()) {
//            mMediaPlayer.start();
//        }
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(this);
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d("TIEJIANG", "MusicService---onCompletion" + " music play end");
        mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_DANCE_MUSIC_END);
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
