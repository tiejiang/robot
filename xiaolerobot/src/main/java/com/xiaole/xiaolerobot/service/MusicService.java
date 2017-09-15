package com.xiaole.xiaolerobot.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.xiaole.xiaolerobot.util.Constant;

import static com.xiaole.xiaolerobot.ui.activity.MenuActivity.mStateManagementHandler;

/**
 * Created by yinyu-tiejiang on 17-8-29.
 */

public class MusicService extends Service{

//    MediaPlayer mMediaPlayer;
    MediaPlayer mStateMediaPlayer;
    MediaPlayer mDanceMediaPlayer;
//    private String musicUrl;
    private MusicPlayBinder mMusicPlayBinder = new MusicPlayBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("TIEJIANG", "MusicService---onBind"+" mMusicPlayBinder= "+mMusicPlayBinder);

        return mMusicPlayBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d("TIEJIANG", "MusicService---onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    public class MusicPlayBinder extends Binder{

        public void playStateMusic(String music_name){

//            Log.d("TIEJIANG", "MusicService---MusicPlayBinder" + " mStateMediaPlayer= " + mStateMediaPlayer);
            try{
                mStateMediaPlayer = new MediaPlayer();
//                if (music_url.contains("hello_waiting_for_you")){   //开机后ＡＰＰ启动音乐
                    AssetFileDescriptor fd = getAssets().openFd(music_name+".mp3");
                    mStateMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
//                }
//                else {                     //其他状态的音乐
//                    mStateMediaPlayer.setDataSource(music_url);
//                }
                mStateMediaPlayer.prepare();
            }catch (Exception e){
                e.printStackTrace();
            }
//            mStateMediaPlayer.start();
            mStateMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mStateMediaPlayer.start();
                }
            });

            mStateMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mStateMediaPlayer.stop();
                    mStateMediaPlayer.release();

                }
            });

        }

        public void playDanceMusic(String music_url){

            try{
                mDanceMediaPlayer = new MediaPlayer();
                mDanceMediaPlayer.setDataSource(music_url);
                mDanceMediaPlayer.prepare();
            }catch (Exception e){
                e.printStackTrace();
                Log.d("TIEJIANG", "playDanceMusic---PLAY---EXCEPTION");
            }
            Log.d("TIEJIANG", "playDanceMusic---PLAY");
//            mDanceMediaPlayer.start();
            mDanceMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mDanceMediaPlayer.start();
                    Log.d("TIEJIANG", "playDanceMusic---PLAY---PREPARED");
                }
            });

            mDanceMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mDanceMediaPlayer.stop();
                    mDanceMediaPlayer.release();
                    mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_DANCE_MUSIC_END);

                }
            });
        }

        public boolean isDanceMusicPlay(){

            if (mDanceMediaPlayer != null){
                if (mDanceMediaPlayer.isPlaying()){
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }

        public void stopPlay(){

            if (mDanceMediaPlayer != null){
                mDanceMediaPlayer.stop();
            }

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
