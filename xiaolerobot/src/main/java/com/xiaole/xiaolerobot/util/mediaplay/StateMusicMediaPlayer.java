package com.xiaole.xiaolerobot.util.mediaplay;

import android.media.MediaPlayer;

/**
 * Created by yinyu-tiejiang on 17-8-8.
 */

public class StateMusicMediaPlayer {

    private String filePath = "";

    public StateMusicMediaPlayer(String path){
        this.filePath = path;
    }

    public void playStateMusic(){
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
//            String file_path = "/sdcard/qqmusic/song/daoxiang.mp3";
            //File file = new File(Environment.getExternalStorageDirectory(), "music.mp3");
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
//            flag = true;
//            refreshTimepos();
        }

    }

}
