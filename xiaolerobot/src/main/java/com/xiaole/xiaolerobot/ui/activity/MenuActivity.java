package com.xiaole.xiaolerobot.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.Formatter;
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
import com.xiaole.xiaolerobot.util.serialportdatamanagement.UartDataManagement;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.id;
import static com.xiaole.xiaolerobot.util.serialportdatamanagement.UartDataManagement.mDataSendHandler;

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
    private SharedPreferences mYTXSharedPreferences;
    private static Handler mYTXInitHandler;

    /**
     * nickName, contactID 接收方昵称,ＩＤ
     * **/
    private String nickName = "";
    private String contactID = "";

    private String[] ytxID = new String[2];

    String mobile = "20170717";
    String pass = "";
    String appKey = "8aaf070858cd982e0158e21ff0000cee";
    String token = "ca8bdec6e6ed3cc369b8122a1c19306d";
    ECInitParams.LoginAuthType mLoginAuthType = ECInitParams.LoginAuthType.NORMAL_AUTH;
    private UartDataManagement mUartManagement = UartDataManagement.getUartInstance();

//    private LexinApplicationReceiver mLexinApplicationReceiver;
    private static final String LEXIN_ACTION = "ACTION_LEXIN_TO_YINYU";
//    private SendingThread mSendingThread;
//    private Handler mDataSendHandler;
    //BaseBuffer: mBaseBuffer[2],mBaseBuffer[3] should be replaced by zhe real command
    private byte[] mBaseCommandBuffer = {(byte) 0x53, (byte) 0x4B, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};

    private ArrayList<HashMap<String, Object>> myMediaList = new ArrayList<HashMap<String, Object>>();

    //处理系统运行状态　和　语音转写指令　的Handler
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
                    String musicPath = (String) myMediaList.get(11).get("musicFileUrl");
                    Log.d(Constant.TAG, "musicPath= " + musicPath);
                    new StateMusicMediaPlayer(musicPath).playStateMusic();
                    break;
//                case Constant.XIAOLE_FORWARD:
//                    mDataSendHandler.obtainMessage(0, fillCommand(Constant.forward)).sendToTarget();
//                    Log.d("TIEJIANG", "MenuActivity---mStateManagementHandler" + "forward command send to MCU");
//                    break;
//                case Constant.XIAOLE_BACK:
//                    mDataSendHandler.obtainMessage(0, fillCommand(Constant.back)).sendToTarget();
//                    Log.d("TIEJIANG", "MenuActivity---mStateManagementHandler" + "forward command send to MCU");
//                    break;
//                case Constant.XIAOLE_LEFT:
//                    mDataSendHandler.obtainMessage(0, fillCommand(Constant.turnLeft)).sendToTarget();
//                    Log.d("TIEJIANG", "MenuActivity---mStateManagementHandler" + "forward command send to MCU");
//                    break;
//                case Constant.XIAOLE_RIGHT:
//                    mDataSendHandler.obtainMessage(0, fillCommand(Constant.turnRight)).sendToTarget();
//                    Log.d("TIEJIANG", "MenuActivity---mStateManagementHandler" + "forward command send to MCU");
//                    break;
//                case Constant.XIAOLE_UP:
//                    mDataSendHandler.obtainMessage(0, fillCommand(Constant.lookUp)).sendToTarget();
//                    Log.d("TIEJIANG", "MenuActivity---mStateManagementHandler" + "forward command send to MCU");
//                    break;
//                case Constant.XIAOLE_DOWN:
//                    mDataSendHandler.obtainMessage(0, fillCommand(Constant.lookDown)).sendToTarget();
//                    Log.d("TIEJIANG", "MenuActivity---mStateManagementHandler" + "forward command send to MCU");
//                    break;
//                case Constant.XIAOLE_DANCE:
//                    mDataSendHandler.obtainMessage(0, fillCommand(Constant.danceModeOne)).sendToTarget();
//                    Log.d("TIEJIANG", "MenuActivity---mStateManagementHandler" + "forward command send to MCU");
//                    break;
//
//                default: //默认身体回复初始位置
//                    mDataSendHandler.obtainMessage(0, fillCommand(Constant.bodyStop)).sendToTarget();
//                    break;

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_menu);

        new Thread(new UDPRunnable()).start();


        ytxID = getYTXID();
        if (ytxID[0] != null && ytxID[1] != null){
            initYTX(ytxID[1]);
            nickName = ytxID[0];
            contactID = ytxID[0];
            Log.d("TIEJIANG", "MenuActivity---onCreat contactID= " + ytxID[0]);
        }else {
            mYTXInitHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    String[] idMsg = (String[])msg.obj;
                    Log.d("TIEJIANG", "msg= " + idMsg[0] + ", " + idMsg[1]);
                    if (msg.what == 1){
                        initYTX(idMsg[1]);
                        nickName = idMsg[0];
                        contactID = idMsg[0];
                    }
                }
            };
        }

        mButtonMonitor = (Button)findViewById(R.id.btn_monitor);
        mButtonRobotDistribute = (Button)findViewById(R.id.btn_remote_control);
        mButtonDisplay = (Button)findViewById(R.id.btn_audio);
        uart_test = (Button)findViewById(R.id.uart_test);
//        test = (Button)findViewById(R.id.test);

        //使用应用内广播测试应用间广播是否能够收到
//        test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //发送广播
//                String broadcastIntent = "ACTION_LEXIN_TO_YINYU";
//                Intent intent = new Intent(broadcastIntent);
//                intent.putExtra("MESSAGE", "turn_left");
//                MenuActivity.this.sendBroadcast(intent);
//            }
//        });

        mButtonMonitor.setOnClickListener(this);
        mButtonRobotDistribute.setOnClickListener(this);
        mButtonDisplay.setOnClickListener(this);

        //register broadcastreceiver
//        mLexinApplicationReceiver = new LexinApplicationReceiver();
//        IntentFilter mIntentFilter = new IntentFilter();
//        mIntentFilter.addAction(LEXIN_ACTION);
//        registerReceiver(mLexinApplicationReceiver, mIntentFilter);

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
//        if (mLexinApplicationReceiver != null){
//            unregisterReceiver(mLexinApplicationReceiver);
//            Log.d("TIEJIANG", "MenuActivity---onDestory---mLexinApplicationReceiver---unregist");
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IMChattingHelper.setOnMessageReportCallback(this);
        //视频结束后让应用退到后台
        boolean movetoback = moveTaskToBack(true);
        Log.d("TIEJIANG", "whether activity goto back or moved = " + movetoback);
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
//                Intent mRemoteCtrIntent = new Intent();
//                mRemoteCtrIntent.setClass(MenuActivity.this,RemoteControlCommandActivity.class);
//                startActivity(mRemoteCtrIntent);
                break;
            case R.id.btn_audio:    //VOIP audio
                Toast.makeText(MenuActivity.this,"btn_audio",Toast.LENGTH_SHORT).show();
                CCPAppManager.callVoIPAction(MenuActivity.this, ECVoIPCallManager.CallType.VOICE,
                        nickName, contactID,false);
                finish();
                break;
            case R.id.uart_test:
//                Intent mIntent = new Intent();
//                mIntent.setClass(MenuActivity.this, UartTestActivity.class);
//                startActivity(mIntent);
                break;

        }
    }

    public void initYTX(String mobile){

        //save app key/ID and contact number etc. and init rong-lian-yun SDK
        ClientUser clientUser = new ClientUser(mobile);
        clientUser.setAppKey(appKey);
        clientUser.setAppToken(token);
        clientUser.setLoginAuthType(mLoginAuthType);
        clientUser.setPassword(pass);
        CCPAppManager.setClientUser(clientUser);
        SDKCoreHelper.init(MenuActivity.this, ECInitParams.LoginMode.FORCE_LOGIN);
        IMChattingHelper.setOnMessageReportCallback(MenuActivity.this);
        Log.d("TIEJIANG", "MenuActivity---initYTX" + " mobile= " + mobile);
    }

    // 获得当前ＩＰ
    public String currentIP() {
        try {
            WifiManager wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            return Formatter.formatIpAddress(ip);
        } catch (Exception ex) {
        }

        return null;
    }

    //接受移动端发送的ＵＤＰ广播
    class UDPRunnable implements Runnable{

        @Override
        public void run() {
            DatagramSocket ds = null;
            try{
                byte[] buf = new byte[1024];
                //发送数据的packet
                DatagramPacket dp_send = null;
                //服务端在21230端口监听接收到的数据
                ds = new DatagramSocket(21230);
                //接收从客户端发送过来的数据
                DatagramPacket dp_receive = new DatagramPacket(buf, 1024);
                Log.d("TIEJIANG", "MenuActivity---UDPRunnable" + " server is on，waiting for client to send data");
                boolean f = true;
                boolean isHandSignal = false;
                while(f){
                    //服务器端接收来自客户端的数据
                    ds.receive(dp_receive);
                    Log.d("TIEJIANG", "server received data from client：");
                    String str_receive = new String(dp_receive.getData(),0,dp_receive.getLength());
                    Log.d("TIEJIANG", "MenuActivity---UDPRunnable" + " str_receive= " + str_receive);

                    isHandSignal = analysisJSONData(str_receive);
                    if (isHandSignal){
                        String handShakeSend = handleJSON("handed", currentIP());
                        dp_send = new DatagramPacket(handShakeSend.getBytes(),handShakeSend.length(),dp_receive.getAddress(),21240);
                    }else {
                        String xiaoleSetting = handleJSON("IDSetted", currentIP());
                        dp_send = new DatagramPacket(xiaoleSetting.getBytes(),xiaoleSetting.length(),dp_receive.getAddress(),21240);
                    }

                    ds.send(dp_send);
                    dp_receive.setLength(1024); //数据"清零"
                }
            }catch (IOException e){
                e.printStackTrace();
                Log.d("TIEJIANG", "MenuActivity---UDPRunnable" + " IOException");
            }finally {
                if (ds != null){
                    ds.close();
                }
            }

        }
    }

    public String handleJSON(String state, String host_ip){

        String jsonString = "";
        try{
            JSONObject json = new JSONObject();
            json.put("state", state);
            json.put("name", "XiaoleServer");
            json.put("hostip", host_ip);

            jsonString = json.toString();

        }catch (JSONException e){
            e.printStackTrace();

        }
        return jsonString;

    }

    /**
     * return :
     *  true : YTX ID setted
     *  false : handed with mobile side
     * */
    public boolean analysisJSONData(String json_string){

        String JSONString = json_string;
        boolean isIDSet = false;
        if (JSONString == null){
            return false;
        }
        try{

            JSONObject parseH3json = new JSONObject(JSONString);
            String wanted = parseH3json.getString("wanted");
            final String hostip = parseH3json.getString("Clientip");
            String name = parseH3json.getString("name");
            String clientContent = parseH3json.getString("ClientContent");

            Log.d("TIEJIANG", "MenuActivity---analysisJSONData"
                    +" wanted= "+wanted+", hostip= "+hostip+", name= "+name+", clientContent= "+clientContent);

            if (wanted.equals("sendYTXID") && name.equals("XiaoleClient") && hostip != null) {
                String[] ytxID = clientContent.split(",");
                //存储移动端发来的云通讯ＩＤ(并根据此ＩＤ开始初始化云通讯)
                saveYTXID(ytxID);
                isIDSet = true;

            }else if (wanted.equals("search") && name.equals("XiaoleClient") && hostip != null){

                isIDSet = false;
            }
        }catch (JSONException e){
            e.printStackTrace();
            Log.d("TIEJIANG", "JSONException");
        }
        Log.d("TIEJIANG", "MenuActivity---analysisJSONData" + " isIDSet= " + isIDSet);
        return isIDSet;
    }

    public String[] getYTXID(){

        String[] YTXID = new String[2];
        SharedPreferences sp = getSharedPreferences(Constant.USER_MESSAGE, Context.MODE_PRIVATE);
        String mobileXiaoLe = sp.getString(Constant.MOBILE_ID, "0");
        String H3XiaoLe = sp.getString(Constant.H3_ID, "1");
        YTXID[0] = mobileXiaoLe;
        YTXID[1] = H3XiaoLe;

        return YTXID;
    }

    //save YTXID
    public void saveYTXID(String[] ytx_id){

        boolean isSaved = false;
        String[] id = getYTXID();
        Log.d("TIEJIANG", " MenuActivity---saveYTXID id[0]= " + id[0] + ", id[1]= " + id[1]);
        if (id[0].equals("0") && id[1].equals("1")){
            mYTXSharedPreferences = getSharedPreferences(Constant.USER_MESSAGE, Context.MODE_PRIVATE);
            SharedPreferences.Editor mEditor = mYTXSharedPreferences.edit();
            mEditor.putString(Constant.MOBILE_ID, ytx_id[0]);
            mEditor.putString(Constant.H3_ID, ytx_id[1]);
            isSaved = mEditor.commit();
        }else{

        }
        Log.d("TIEJIANG", " MenuActivity---saveYTXID isSaved= " + isSaved);
        if (isSaved){
            //存储成功
            mYTXInitHandler.obtainMessage(1, ytx_id).sendToTarget();
        }else {
            mYTXInitHandler.sendEmptyMessage(0); //存储失败
        }

    }

    //接受乐新smart应用的广播--->已修改为单独一个类的静态广播
//    class LexinApplicationReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d("TIEJIANG", "LexinApplicationReceiver---onReceive");
//            //广播接受
//            if (intent.getAction().equals(LEXIN_ACTION)){
//                String receiveStr = intent.getStringExtra("MESSAGE").toString().trim();
//                Log.d("TIEJIANG", "LexinApplicationReceiver---receiveStr = " + receiveStr);
//                if (receiveStr.equals("forward")){
//                    mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_FORWARD);
//                }else if(receiveStr.equals("back")){
//                    mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_BACK);
//                }else if(receiveStr.equals("turn_left")){
//                    mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_LEFT);
//                }else if(receiveStr.equals("turn_right")){
//                    mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_RIGHT);
//                }else if(receiveStr.equals("look_up")){
//                    mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_UP);
//                }else if(receiveStr.equals("look_down")){
//                    mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_DOWN);
//                }else if(receiveStr.equals("dance")){
//                    mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_DANCE);
//                }
//
//            }

//        }
//    }

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
            Log.d("TIEJIANG", "MenuActivity---send to MCU---forward");
//            mUartManagement.sendCommand(mUartManagement.fillCommand(Constant.H3ControlForward));
            mDataSendHandler.obtainMessage(0, fillCommand(Constant.H3ControlForward)).sendToTarget();
        }else if(controlCommand.equals(Constant.H3_XIAOLE_BACK)){
//            mUartManagement.sendCommand(mUartManagement.fillCommand(Constant.H3Controlback));
            mDataSendHandler.obtainMessage(0, fillCommand(Constant.H3Controlback)).sendToTarget();
            Log.d("TIEJIANG", "MenuActivity---send to MCU---back");
        }else if(controlCommand.equals(Constant.H3_XIAOLE_LEFT)){
//            mUartManagement.sendCommand(mUartManagement.fillCommand(Constant.H3ControlBodyToLeft));
            mDataSendHandler.obtainMessage(0, fillCommand(Constant.H3ControlBodyToLeft)).sendToTarget();
            Log.d("TIEJIANG", "MenuActivity---send to MCU---turn_left");
        }else if(controlCommand.equals(Constant.H3_XIAOLE_RIGHT)){
//            mUartManagement.sendCommand(mUartManagement.fillCommand(Constant.H3ControlBodyToRight));
            mDataSendHandler.obtainMessage(0, fillCommand(Constant.H3ControlBodyToRight)).sendToTarget();
            Log.d("TIEJIANG", "MenuActivity---send to MCU---turn_right");
        }else if (controlCommand.equals(Constant.HAND_SHAKE)){  //握手信号
            handleSendTextMessage(Constant.HAND_OK);
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
            Log.d("TIEJIANG", "[MenuActivity-onPushMessage]" + "i :" + i + ", message = " + message);// add by tiejiang
        }
        Log.d("TIEJIANG", "[MenuActivity-onPushMessage]" + ",sessionId :" + sessionId);// add by tiejiang
        //for test
//        handleSendTextMessage(message + "callback");
        //for test end

        analysisCommand(message);
    }

    /**
     * 处理文本发送方法事件通知
     * @param text
     */
    public void handleSendTextMessage(CharSequence text) {
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
        msg.setTo(contactID); // attenionthis number is not the login number! / modified by tiejiang
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
            Log.d("TIEJIANG", "[MenuActivity]-handleSendTextMessage" + ", txt = " + text);// add by tiejiang
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
            Log.d("TIEJIANG", "[MenuActivity]-SendECMessage");// add by tiejiang
            rowId = IMChattingHelper.sendECMessage(msg);

            //}
            // 通知列表刷新
            //msg.setId(rowId);
            //notifyIMessageListView(msg);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TIEJIANG", "[MenuActivity]-send failed");// add by tiejiang
        }
    }
}
