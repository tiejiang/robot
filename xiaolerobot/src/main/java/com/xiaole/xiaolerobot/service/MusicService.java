package com.xiaole.xiaolerobot.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.xiaole.xiaolerobot.util.Constant;

import static com.xiaole.xiaolerobot.ui.activity.MenuActivity.mStateManagementHandler;

/**
 * Created by yinyu-tiejiang on 17-8-29.
 */

public class MusicService extends Service{

//    MediaPlayer mMediaPlayer;
    private final MediaPlayer mStateMediaPlayer = new MediaPlayer();
    private final MediaPlayer mDanceMediaPlayer = new MediaPlayer();
//    private String musicUrl;
    private MusicPlayBinder mMusicPlayBinder = new MusicPlayBinder();

    @Override
    public IBinder onBind(Intent intent) {
//        Log.d("TIEJIANG", "MusicService---onBind");

        return mMusicPlayBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d("TIEJIANG", "MusicService---onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Log.d("TIEJIANG", "MusicService---onStartCommand");
//        musicUrl = intent.getStringExtra("music_url");
//        Log.d("TIEJIANG", "MusicService---onStartCommand" + " musicUrl= " + musicUrl);
//        mMediaPlayer = new MediaPlayer();
//        try {
//            mMediaPlayer.setDataSource(musicUrl);
//            mMediaPlayer.prepare();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
////        if (!mMediaPlayer.isPlaying()) {
////            mMediaPlayer.start();
////        }
//        mMediaPlayer.start();
//        mMediaPlayer.setOnCompletionListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    public class MusicPlayBinder extends Binder{

        public void playStateMusic(String music_url){

            try{
                mStateMediaPlayer.setDataSource(music_url);
                mStateMediaPlayer.prepare();
            }catch (Exception e){
                e.printStackTrace();
            }
            mStateMediaPlayer.start();
            mStateMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mStateMediaPlayer.stop();

                }
            });

        }

        public void playDanceMusic(String music_url){

            try{
                mDanceMediaPlayer.setDataSource(music_url);
                mDanceMediaPlayer.prepare();
            }catch (Exception e){
                e.printStackTrace();
            }
            mDanceMediaPlayer.start();
            mDanceMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mDanceMediaPlayer.stop();
                    mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_DANCE_MUSIC_END);
                }
            });
        }

        public void stopPlay(){

            mDanceMediaPlayer.stop();
        }
    }

//    private MediaPlayer getStateMusicPlayer(){
//
//        if (mStateMediaPlayer == null){
//            mStateMediaPlayer = new MediaPlayer();
//        }
//        return mStateMediaPlayer;
//    }
//
//    private MediaPlayer getDanceMusicPlayer(){
//
//        if (mDanceMediaPlayer == null){
//            mDanceMediaPlayer = new MediaPlayer();
//        }
//        return mDanceMediaPlayer;
//    }
}
