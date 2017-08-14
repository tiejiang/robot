package com.xiaole.xiaolerobot.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.xiaole.xiaolerobot.R;
import com.xiaole.xiaolerobot.common.CCPAppManager;
import com.xiaole.xiaolerobot.core.ClientUser;
import com.xiaole.xiaolerobot.ui.helper.IMChattingHelper;
import com.xiaole.xiaolerobot.ui.helper.SDKCoreHelper;
import com.xiaole.xiaolerobot.util.Constant;
import com.xiaole.xiaolerobot.util.mediaplay.StateMusicMediaPlayer;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/21.
 */

public class MenuActivity extends
//        SerialPortActivity
        Activity
        implements IMChattingHelper.OnMessageReportCallback, View.OnClickListener {

//    public static final String TAG
    private Button mButtonMonitor;
    private Button mButtonRobotDistribute;
    private Button mButtonDisplay;
    private Button uart_test;
    private Button test;

    private String nickName = "71707102";
    private String contactID = "71707102";

    String mobile = "20170717";
    String pass = "";
    String appKey = "8aaf070858cd982e0158e21ff0000cee";
    String token = "ca8bdec6e6ed3cc369b8122a1c19306d";
    ECInitParams.LoginAuthType mLoginAuthType = ECInitParams.LoginAuthType.NORMAL_AUTH;

    private LexinApplicationReceiver mLexinApplicationReceiver;
    private static final String LEXIN_ACTION = "ACTION_LEXIN_TO_YINYU";
//    private SendingThread mSendingThread;
    private Handler mDataSendHandler;
    //BaseBuffer: mBaseBuffer[2],mBaseBuffer[3] should be replaced by zhe real command
    private byte[] mBaseCommandBuffer = {(byte) 0x53, (byte) 0x4B, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};

    private ArrayList<HashMap<String, Object>> myMediaList = new ArrayList<HashMap<String, Object>>();

    //处理系统运行状态的Handler
    public Handler mStateManagementHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //开机广播收到后，不能立即从ｓｄｃａｒｄ当中读取媒体内容播放，因为此时的ｓｄｃａｒｄ内容尚未读出
                case Constant.BOOT_COMPLETED_FROM_BROADCASTRECEIVER:

                    break;
                //ｓｄｃａｒｄ卡媒体内容加载完毕（才能进行所有媒体库内容播放）
                case Constant.SEARCH_MEDIASOURCE_COMPLETED_FROM_SDCARD:
                    String musicPath = (String) myMediaList.get(12).get("musicFileUrl");
                    Log.d(Constant.TAG, "musicPath= " + musicPath);
                    new StateMusicMediaPlayer(musicPath).playStateMusic();
                    break;
                case Constant.XIAOLE_FORWARD:
                    mDataSendHandler.obtainMessage(0, fillCommand(Constant.forward)).sendToTarget();
                    break;

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_menu);

        //save app key/ID and contact number etc. and init rong-lian-yun SDK
        ClientUser clientUser = new ClientUser(mobile);
        clientUser.setAppKey(appKey);
        clientUser.setAppToken(token);
        clientUser.setLoginAuthType(mLoginAuthType);
        clientUser.setPassword(pass);
        CCPAppManager.setClientUser(clientUser);
        SDKCoreHelper.init(MenuActivity.this, ECInitParams.LoginMode.FORCE_LOGIN);
        IMChattingHelper.setOnMessageReportCallback(MenuActivity.this);

        mButtonMonitor = (Button)findViewById(R.id.btn_monitor);
        mButtonRobotDistribute = (Button)findViewById(R.id.btn_remote_control);
        mButtonDisplay = (Button)findViewById(R.id.btn_audio);
        uart_test = (Button)findViewById(R.id.uart_test);
        test = (Button)findViewById(R.id.test);

        //使用应用内广播测试应用间广播是否能够收到
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发送广播
                String broadcastIntent = "ACTION_LEXIN_TO_YINYU";
                Intent intent = new Intent(broadcastIntent);
                intent.putExtra("FORWARD", "forward");
                MenuActivity.this.sendBroadcast(intent);
            }
        });

        mButtonMonitor.setOnClickListener(this);
        mButtonRobotDistribute.setOnClickListener(this);
        mButtonDisplay.setOnClickListener(this);

        //register broadcastreceiver
        mLexinApplicationReceiver = new LexinApplicationReceiver();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(LEXIN_ACTION);
        registerReceiver(mLexinApplicationReceiver, mIntentFilter);

        uart_test.setOnClickListener(this);
        //start search sdcard source thread
        new Thread(new mMediaSearchThread()).start();

        //start uart/serialport thread
//        if (mSerialPort != null) {
//            mSendingThread = new SendingThread();
//            mSendingThread.start();
//        }

        // for test/debug
//        closePort();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLexinApplicationReceiver != null){
            unregisterReceiver(mLexinApplicationReceiver);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_monitor:    //VOIP  video
                Toast.makeText(MenuActivity.this,"monitor",Toast.LENGTH_SHORT).show();
                CCPAppManager.callVoIPAction(MenuActivity.this, ECVoIPCallManager.CallType.VIDEO,
                        nickName, contactID,false);
                finish();
                break;
            case R.id.btn_remote_control:    //VOIP IM
                Toast.makeText(MenuActivity.this,"robot_remote_control",Toast.LENGTH_SHORT).show();
                Intent mRemoteCtrIntent = new Intent();
                mRemoteCtrIntent.setClass(MenuActivity.this,RemoteControlCommandActivity.class);
                startActivity(mRemoteCtrIntent);
                break;
            case R.id.btn_audio:    //VOIP audio
                Toast.makeText(MenuActivity.this,"btn_audio",Toast.LENGTH_SHORT).show();
                CCPAppManager.callVoIPAction(MenuActivity.this, ECVoIPCallManager.CallType.VOICE,
                        nickName, contactID,false);
                finish();
                break;
            case R.id.uart_test:
                Intent mIntent = new Intent();
                mIntent.setClass(MenuActivity.this, UartTestActivity.class);
                startActivity(mIntent);
                break;

        }
    }

    class LexinApplicationReceiver extends BroadcastReceiver {

        public Handler mStateManagementHandler;
        @Override
        public void onReceive(Context context, Intent intent) {

            //广播接受
            if (intent.getAction().equals(LEXIN_ACTION)){
                Log.d("TIEJIANG", "onReceive---intent.getExtras = " + intent.getStringExtra("FORWARD").toString());
                if (intent.getStringExtra("FORWARD").equals("forward")){
                    Log.d("TIEJIANG", "LEXING BROADCASTRECEIVER---" + intent.getStringExtra("FORWARD").toString());
//                    mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_FORWARD);
                }

//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("提示")
//                        .setMessage("收到广佳ＡＰＰ应用的广播 value= " + intent.getStringExtra("FORWARD").toString())
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//                AlertDialog dialog = (AlertDialog) builder.create();
//                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                dialog.show();

            }

        }
    }

    //for test/debug
//    public void closePort(){
//        mApplication.closeSerialPort();
//        mSerialPort = null;
//    }

    //将实际指令填充到mBaseCommandBuffer里面
    public byte[] fillCommand(byte[] command){
        mBaseCommandBuffer[2] = command[0];
        mBaseCommandBuffer[3] = command[1];

        return mBaseCommandBuffer;
    }
    //serial port sending thread
//    private class SendingThread extends Thread {
//        @Override
//        public void run() {
//            Looper.prepare();
//            mDataSendHandler = new Handler(){
//                @Override
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//                    byte[] buffer = (byte[]) msg.obj;
//                    switch (msg.what){
//                        case 0:
//                            try {
//                                if (mOutputStream != null) {
//                                    mOutputStream.write(buffer);
//                                } else {
//                                    return;
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                return;
//                            }
//                            break;
////                        case 1:
//////                            Log.d("TIEJIANG", (buffer)message.obj);
////                            break;
//                    }
//
//                }
//            };
//            Looper.loop();
//        }
//    }

    //uart receive data from mcu
//    @Override
//    protected void onDataReceived(byte[] buffer, int size) {
//        //保留作为单片机上发电池状态信息
////        runOnUiThread(new Runnable() {
////            public void run() {
////                if (mReception != null) {
////                    mReception.append(new String(buffer, 0, size));
////                }
////            }
////        });
//    }

    //搜索ＳＤ卡媒体内容
    class mMediaSearchThread implements Runnable{

        @Override
        public void run() {
            myMediaList = scanAllAudioFiles();
//            for (int i=0; i<myMediaList.size(); i++){
//                Log.d(Constant.TAG, "path List= " + myMediaList.get(i).get("musicFileUrl"));
//            }
            Message mMessage = new Message();
            mMessage.what = Constant.SEARCH_MEDIASOURCE_COMPLETED_FROM_SDCARD;
            mMessage.obj = "searchMediaCompleted";
            mStateManagementHandler.sendMessage(mMessage);
        }
    }
    public ArrayList<HashMap<String, Object>> scanAllAudioFiles(){
        //生成动态集合，用于存储数据
        ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();

        //查询媒体数据库
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
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

    //解析移动端发送的指令
    public void analysisCommand(String command){

        String controlCommand = command;
        if (controlCommand == null){
            Log.d(Constant.TAG, "command is invalid");
            return;
        }
        if (controlCommand.equals(Constant.H3_XIAOLE_FORWARD)){
            mDataSendHandler.obtainMessage(0, fillCommand(Constant.H3ControlForward)).sendToTarget();
        }else if(controlCommand.equals(Constant.H3_XIAOLE_BACK)){
            mDataSendHandler.obtainMessage(0, fillCommand(Constant.H3Controlback)).sendToTarget();
        }else if(controlCommand.equals(Constant.H3_XIAOLE_LEFT)){
            mDataSendHandler.obtainMessage(0, fillCommand(Constant.H3ControlBodyToLeft)).sendToTarget();
        }else if(controlCommand.equals(Constant.H3_XIAOLE_RIGHT)){
            mDataSendHandler.obtainMessage(0, fillCommand(Constant.H3ControlBodyToRight)).sendToTarget();
        }


    }


    @Override
    public void onMessageReport(ECError error, ECMessage message) {

    }

    @Override
    public void onPushMessage(String sessionId, List<ECMessage> msgs) {
        int msgsSize = msgs.size();
        String message = " ";
        for (int i = 0; i < msgsSize; i++){
            message = ((ECTextMessageBody) msgs.get(i).getBody()).getMessage();
            Log.d("TIEJIANG", "[MainActivity-onPushMessage]" + "i :" + i + ", message = " + message);// add by tiejiang
        }
        Log.d("TIEJIANG", "[MainActivity-onPushMessage]" + ",sessionId :" + sessionId);// add by tiejiang
        //for test
//        handleSendTextMessage(message + "callback");
        analysisCommand(message.trim());
    }

    /**
     * 处理文本发送方法事件通知
     * @param text
     */
    public static void handleSendTextMessage(CharSequence text) {
        if(text == null) {
            return ;
        }
        if(text.toString().trim().length() <= 0) {
//canotSendEmptyMessage();
            return ;
        }
// 组建一个待发送的ECMessage
        ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
// 设置消息接收者
//msg.setTo(mRecipients);
        msg.setTo("71707102"); // attenionthis number is not the login number! / modified by tiejiang
        ECTextMessageBody msgBody=null;
        Boolean isBQMMMessage=false;
        String emojiNames = null;
//if(text.toString().contains(CCPChattingFooter2.TXT_MSGTYPE)&& text.toString().contains(CCPChattingFooter2.MSG_DATA)){
//try {
//JSONObject jsonObject = new JSONObject(text.toString());
//String emojiType=jsonObject.getString(CCPChattingFooter2.TXT_MSGTYPE);
//if(emojiType.equals(CCPChattingFooter2.EMOJITYPE) || emojiType.equals(CCPChattingFooter2.FACETYPE)){//说明是含有BQMM的表情
//isBQMMMessage=true;
//emojiNames=jsonObject.getString(CCPChattingFooter2.EMOJI_TEXT);
//}
//} catch (JSONException e) {
//e.printStackTrace();
//}
//}
        if (isBQMMMessage) {
            msgBody = new ECTextMessageBody(emojiNames);
            msg.setBody(msgBody);
            msg.setUserData(text.toString());
        } else {
// 创建一个文本消息体，并添加到消息对象中
            msgBody = new ECTextMessageBody(text.toString());
            msg.setBody(msgBody);
            Log.d("TIEJIANG", "[RemoteControlCommandActivity]-handleSendTextMessage" + ", txt = " + text);// add by tiejiang
        }

//String[] at = mChattingFooter.getAtSomeBody();
//msgBody.setAtMembers(at);
//mChattingFooter.clearSomeBody();
        try {
// 发送消息，该函数见上
            long rowId = -1;
//if(mCustomerService) {
//rowId = CustomerServiceHelper.sendMCMessage(msg);
//} else {
            Log.d("TIEJIANG", "[RemoteControlCommandActivity]-SendECMessage");// add by tiejiang
            rowId = IMChattingHelper.sendECMessage(msg);

//}
// 通知列表刷新
//msg.setId(rowId);
//notifyIMessageListView(msg);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TIEJIANG", "[RemoteControlCommandActivity]-send failed");// add by tiejiang
        }
    }
}
