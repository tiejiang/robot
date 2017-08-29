package com.xiaole.xiaolerobot.util.mediaplay;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import com.xiaole.xiaolerobot.util.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.xiaole.xiaolerobot.ui.activity.MenuActivity.mStateManagementHandler;

/**
 * Created by yinyu-tiejiang on 17-8-8.
 */

public class StateMusicMediaPlayer {

    private String filePath = "";
    private Context mContext;
    MediaPlayer mMediaPlayer;

    public StateMusicMediaPlayer(){

    }
    public StateMusicMediaPlayer(MediaPlayer mediaPlayer){
        this.mMediaPlayer = mediaPlayer;

    }

    public StateMusicMediaPlayer(Context context){

        this.mContext = context;
    }

    public StateMusicMediaPlayer(String path){
        this.filePath = path;
    }

    public void playStateMusic(){

        new Thread(new Runnable() {
            @Override
            public void run() {
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
        }).start();

    }

    public void playDanceMusic(final String url){

        new Thread(new Runnable() {
            @Override
            public void run() {
//                mMediaPlayer = new MediaPlayer();
                try{
                    mMediaPlayer.setDataSource(url);
                    mMediaPlayer.prepare();
                }catch (IOException e){
                    e.printStackTrace();
                }
//                if (!mMediaPlayer.isPlaying()){
//                    mMediaPlayer.start();
//                }
                mMediaPlayer.start();
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                    }
                });
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_DANCE_MUSIC_END);
                        mMediaPlayer.release();

                    }
                });

            }
        }).start();
    }

    public ArrayList<HashMap<String, Object>> scanAllAudioFiles(){

        //生成动态集合，用于存储数据
        ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();
        //查询媒体数据库
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //遍历媒体数据库
        if(cursor.moveToFirst()){

            while (!cursor.isAfterLast()) {

                //歌曲编号
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //歌曲名
                String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                String author = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if(size>1024*50){//如果文件大小大于50K，将该文件信息存入到map集合中
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("musicId", id);
                    map.put("musicTitle", tilte);
                    map.put("musicFileUrl", url);
                    map.put("music_file_name", tilte);
                    map.put("music_author",author);
                    map.put("music_url",url);
                    map.put("music_duration",duration);
                    mylist.add(map);
                }
                cursor.moveToNext();
            }
        }
        //返回存储数据的集合
        return mylist;
    }

    public void stopMediaPlayer(){
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer = null;
    }

}
