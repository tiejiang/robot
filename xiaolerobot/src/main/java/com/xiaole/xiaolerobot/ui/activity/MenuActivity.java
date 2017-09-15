package com.xiaole.xiaolerobot.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.xiaole.xiaolerobot.R;
import com.xiaole.xiaolerobot.camera.MyCameraActivity;
import com.xiaole.xiaolerobot.common.CCPAppManager;
import com.xiaole.xiaolerobot.core.ClientUser;
import com.xiaole.xiaolerobot.instancefractory.InstanceHelper;
import com.xiaole.xiaolerobot.service.MusicService;
import com.xiaole.xiaolerobot.ui.helper.IMChattingHelper;
import com.xiaole.xiaolerobot.ui.helper.SDKCoreHelper;
import com.xiaole.xiaolerobot.util.Constant;
import com.xiaole.xiaolerobot.util.DemoUtils;
import com.xiaole.xiaolerobot.util.mediaplay.StateMusicMediaPlayer;
import com.xiaole.xiaolerobot.util.serialportdatamanagement.UartDataManagement;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.im.ECImageMessageBody;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import static android.R.id.message;
import static com.xiaole.xiaolerobot.instancefractory.InstanceHelper.mMenuActivity;
import static com.xiaole.xiaolerobot.ui.helper.SDKCoreHelper.logout;
import static com.xiaole.xiaolerobot.util.DemoUtils.getRandomInt;
import static com.xiaole.xiaolerobot.util.serialportdatamanagement.UartDataManagement.mDataSendHandler;
import static com.yuntongxun.ecsdk.core.ea.a.I;
import static com.yuntongxun.ecsdk.core.ea.a.v;
import static com.yuntongxun.ecsdk.core.setup.h.m;

/**
 * Created by Administrator on 2016/11/21.
 */

public class MenuActivity extends
//        SerialPortActivity
        Activity
        implements IMChattingHelper.OnMessageReportCallback, View.OnClickListener{

//    public static final String TAG
    private Button mButtonMonitor;
    private Button mButtonRobotDistribute;
    private Button mButtonDisplay;
    private Button uart_test;
    private Button test;
    private SharedPreferences mYTXSharedPreferences;
    private static Handler mYTXInitHandler;
    private Context mMenuActivityContext;
//    private int mDancingSongNum = 0; //the number of the dancing song search from sdcard

    // nickName, contactID 接收方昵称,ＩＤ
    private String nickName = "";
    private String contactID = "";
    private String[] ytxID = new String[2];
    private String batteryValue = "00";  //whole situation battery value
    String pass = "";
    ECInitParams.LoginAuthType mLoginAuthType = ECInitParams.LoginAuthType.NORMAL_AUTH;
    private UartDataManagement mUartManagement = UartDataManagement.getUartInstance();
    //for code block test
    private int flag = 0;
    private static byte[] uplinkBatteryCommand = {(byte) 0x53, (byte) 0x4B, (byte) 0x02, (byte) 0x0a, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};
    private static byte[] uplinkLowbattery = {(byte) 0x53, (byte) 0x4B, (byte) 0x01, (byte) 0x05, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};
    private static byte[] uplinkConnectNet = {(byte) 0x53, (byte) 0x4B, (byte) 0x01, (byte) 0x38, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};
    private static byte[] uplinkAwakenAndStopMusic = {(byte) 0x53, (byte) 0x4B, (byte) 0x01, (byte) 0x01, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};

//    private LexinApplicationReceiver mLexinApplicationReceiver;
    private static final String LEXIN_ACTION = "ACTION_LEXIN_TO_YINYU";
//    private SendingThread mSendingThread;
//    private Handler mDataSendHandler;
    //BaseBuffer: mBaseBuffer[2],mBaseBuffer[3] should be replaced by zhe real command
    private byte[] mBaseCommandBuffer = {(byte) 0x53, (byte) 0x4B, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};

    private Vector mDanceVector = new Vector();
//    private Vector mWarningToneVector = new Vector();
    private ArrayList<HashMap<String, Object>> myMediaList = new ArrayList<HashMap<String, Object>>();
//    private boolean isGuangJiaAPPConnectNetBegin = false;  //广佳ＡＰＰ是否处于联网模式
    //处理系统运行状态　和　语音转写指令　的Handler
    public static Handler mStateManagementHandler;
    public MusicService.MusicPlayBinder musicPlayBinder;
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            musicPlayBinder = (MusicService.MusicPlayBinder)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_menu);
        InstanceHelper.mMenuActivity = this;

        //bind service
        Intent bindServiceIntent = new Intent(this, MusicService.class);
        boolean isBind = bindService(bindServiceIntent, mServiceConnection, this.BIND_AUTO_CREATE);
        Log.d("TIEJIANG", "MenuActivity---onCreate "+" isBind= "+isBind);
        mMenuActivityContext = this;
        //check and copy database to the dir
//        new DatabaseCreate(this).createDb();
        //refresh the database
        //~~~

        new Thread(new UDPRunnable()).start();

        //test code begin
//        mDataSendHandler.obtainMessage(0, mUartManagement.fillCommand(Constant.forward)).sendToTarget();
        //test code end

        ytxID = getYTXID();
        if (ytxID[0] != null && ytxID[1] != null){
            initYTX(ytxID[1]);
            nickName = ytxID[0];
            contactID = ytxID[0];
            Log.d("TIEJIANG", "MenuActivity---onCreat contactID= " + ytxID[0]);
        }

//        IMChattingHelper.setOnMessageReportCallback(this);
        //若收到ＩＤ不同，则重新初始化云通讯
//        mYTXInitHandler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == 1){
//                    String[] idMsg = (String[])msg.obj;
//                    Log.d("TIEJIANG", "msg= " + idMsg[0] + ", " + idMsg[1]);
//                    if (msg.what == 1){
//                        initYTX(idMsg[1]);
//                        nickName = idMsg[0];
//                        contactID = idMsg[0];
//                    }
//                }else if (msg.what == 0){
//
//
//                }
//
//            }
//        };

        mButtonMonitor = (Button)findViewById(R.id.btn_monitor);
        mButtonRobotDistribute = (Button)findViewById(R.id.btn_remote_control);
        mButtonDisplay = (Button)findViewById(R.id.btn_audio);
        uart_test = (Button)findViewById(R.id.uart_test);
        test = (Button)findViewById(R.id.test);

        //test code begin 使用应用内广播测试应用间广播是否能够收到
        test.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mDataSendHandler.obtainMessage(0, mUartManagement.fillCommand(Constant.forward)).sendToTarget();
//                mUartManagement.dealOtherUplinkCommand(uplinkAwaken);
//                switch (flag){
//                    case 0:
//                        sendBroadcastToGuangjia("test_broadcast");
//                        flag = 1;
//                        break;
//                    case 1:
//                        mUartManagement.dealElectricQuantity(uplinkBatteryCommand);
//                        flag = 2;
//                        break;
//                    case 2:
//                        mUartManagement.dealOtherUplinkCommand(uplinkLowbattery);
//                        flag = 3;
//                        break;
//                    case 3:
//                        mUartManagement.dealOtherUplinkCommand(uplinkAwakenAndStopMusic);
//                        flag = 4;
//                        break;
//                    case 4:
//                        mUartManagement.dealOtherUplinkCommand(uplinkConnectNet);
//                        flag = 0;
//                        break;
//                }

                //发送广播
//                String broadcastIntent = "ACTION_LEXIN_TO_YINYU"; //小乐ＡＰＰ收
//                String broadcastIntent = Constant.LEXING_ACTION;    //广佳ＡＰＰ收
//                Intent intent = new Intent(broadcastIntent);
//                intent.putExtra("MESSAGE", "test_broadcast");
//                MenuActivity.this.sendBroadcast(intent);
            }
        });
        //test code end

        mButtonMonitor.setOnClickListener(this);
        mButtonRobotDistribute.setOnClickListener(this);
        mButtonDisplay.setOnClickListener(this);

        //register broadcastreceiver
//        mLexinApplicationReceiver = new LexinApplicationReceiver();
//        IntentFilter mIntentFilter = new IntentFilter();
//        mIntentFilter.addAction(LEXIN_ACTION);
//        registerReceiver(mLexinApplicationReceiver, mIntentFilter);

        uart_test.setOnClickListener(this);

        dealSystemRunTimeMission();
        //启动后即开始ｓｄｃａｒｄ媒体搜索（在bindService之后执行，因为bind之后才能够获得service实例去播放音乐）
        //start search sdcard source thread
        new Thread(new mMediaSearchThread()).start();

        //start uart/serialport thread
//        if (mSerialPort != null) {
//            mSendingThread = new SendingThread();
//            mSendingThread.start();
//        }

        // for test/debug
//        closePort();
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Date   startDate   =   new Date(System.currentTimeMillis());
                while (musicPlayBinder == null){

                }
//                Date endDate = new Date(System.currentTimeMillis());
//                long time = endDate.getTime() - startDate.getTime();
//                Log.d("TIEJIANG", "WHILE musicPlayerBinder == null"+" time= "+time);
                musicPlayBinder.playStateMusic(selectStateMusic(Constant.POWER_ON_MUSIC));
            }
        }).start();

        //test code
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    Thread.sleep(5000);
//                    restartXiaoleApp();
//                }catch (InterruptedException e){
//
//                }
//            }
//        }).start();
        //send broadcast to guangjia app move task to back
        sendBroadcastToGuangjia(Constant.MOVE_TASK_TO_BACK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mLexinApplicationReceiver != null){
//            unregisterReceiver(mLexinApplicationReceiver);
//            Log.d("TIEJIANG", "MenuActivity---onDestory---mLexinApplicationReceiver---unregist");
//        }
        if (mDanceVector != null){
            mDanceVector.removeAllElements();
        }
//        if(mWarningToneVector != null){
//            mWarningToneVector.removeAllElements();
//        }
        //unbind service
        unbindService(mServiceConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        IMChattingHelper.setOnMessageReportCallback(this);
        //视频结束后让应用退到后台
//        boolean movetoback = moveTaskToBack(true);
//        Log.d("TIEJIANG", "whether activity goto back or moved = " + movetoback);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_monitor:    //VOIP  video
                Toast.makeText(MenuActivity.this,"monitor",Toast.LENGTH_SHORT).show();
                CCPAppManager.callVoIPAction(MenuActivity.this, ECVoIPCallManager.CallType.VIDEO,
                        nickName, contactID,false);
                Log.d("TIEJIANG", "MenuActivity--- onClick" + " nickName= " + nickName + " contactID= " + contactID);
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

    /**
     * function: send broadcast to guagnjia-lexin app
     *
     * */
    public void sendBroadcastToGuangjia(String message){
        //发送广播
        String broadcastIntent = Constant.LEXING_ACTION;    //广佳ＡＰＰ收
        Intent intent = new Intent(broadcastIntent);
        intent.putExtra("MESSAGE", message);
        MenuActivity.this.sendBroadcast(intent);
    }

    /**
     * function: when received command from mobile side,
     * MenuActivity start MyCameraActivity to take photo,
     * and when the photo is taked and saved to system folder,
     * this method will be called to get the photo url and other
     * messages.
     * */
    public void onPhotoTakeAndSave() {

        Log.d("TIEJIANG", "MenuActivity---onPhotoTakeAndSaved");
    }

    /**
     * function: select music in the array
     * @return String: the music name
     * */
    private String selectStateMusic(String[] music_array){

        String[] tempString = music_array;
        int musicIndex = 0;
        if (tempString.length < 1){
            return null;
        }
        musicIndex = tempString.length;
        int random = DemoUtils.getRandomInt(musicIndex);
        return tempString[random];

    }


    /**
     * function: deal the system runtime missions
     *
     * */
    public void dealSystemRunTimeMission(){

        mStateManagementHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String message = (String)msg.obj;
                switch (msg.what){
                    //开机广播收到后，不能立即从ｓｄｃａｒｄ当中读取媒体内容播放，因为此时的ｓｄｃａｒｄ内容尚未读出
                    case Constant.BOOT_COMPLETED_FROM_BROADCASTRECEIVER:

                        break;
                    //ｓｄｃａｒｄ卡媒体内容加载完毕（才能进行所有媒体库内容播放）
                    case Constant.SEARCH_MEDIASOURCE_COMPLETED_FROM_SDCARD:
                        if (message.equals("searchMediaFailed")){
                            musicPlayBinder.playStateMusic(Constant.SDCARD_SEARCH_FAILED);
                        }else if (message.equals("searchMediaCompleted")){
//                            String musicPath = (String) myMediaList.get(10).get("musicFileUrl");
//                            Log.d("TIEJIANG", "musicPath= " + musicPath);
//                            Log.d("TIEJIANG", "MenuActivity---mStateManagementHandler" + " sdcard over musicPlayBinder= " + musicPlayBinder);
//                        musicPlayBinder.playStateMusic(musicPath); //此处会概率性出现musicPlayBinder为空的情况，尚未找到原因
                            //when the sdcard media source is loaded then to get the detail data
                            getDancingSongList(myMediaList);
//                            getWarningToneList(myMediaList);
                        }
                        break;
                    case Constant.XIAOLE_DANCE_BEGIN:
                        //从语音解析到跳舞指令后开始音乐播放（get the random song from vector）
                        int mDancingSongNum = getRandomInt(mDanceVector.size());
                        String musicUrl = (String)mDanceVector.get(mDancingSongNum);
                        Log.d("TIEJIANG", "MenuActivity---mStateManagementHandler" + " musicUrl= " + musicUrl+" \n musicPlayBinder= "+musicPlayBinder);
                        musicPlayBinder.playDanceMusic(musicUrl);

                        break;
                    case Constant.XIAOLE_DANCE_MUSIC_END:
//                        mDataSendHandler.obtainMessage(0, mUartManagement.fillCommand(Constant.bodyStop)).sendToTarget();
                        mDataSendHandler.obtainMessage(0, mUartManagement.fillCommand(Constant.headToMiddle)).sendToTarget();
                        break;
                    case Constant.BATTERY_VALUE_STATE:
                        batteryValue = message;
                        break;

                }
                // judge guangjia app is start to connect net or not
//                if (message.equals(Constant.CONNECT_NET)){
//                    isGuangJiaAPPConnectNetBegin = true;
//                }else if (message.equals(Constant.CONNECT_NET_END)){
//                    isGuangJiaAPPConnectNetBegin = false;
//                }
            }
        };
    }

    /**
     * function: play music in service
     *
     * */
//    public void playMusic(String url){
////        Log.d("TIEJIANG", "MenuActivity---playMusic");
//        Intent intent = new Intent(MenuActivity.this, MusicService.class);
//        intent.putExtra("music_url", url);
//        startService(intent);
//    }

    /**
     * function: stop play music(stop service)
     * */
//    public void stopPlayMusic(){
//        Intent stopIntent = new Intent(this, MusicService.class);
//        stopService(stopIntent);
//    }

    /**
     * function: get the dancing song list from myMediaList
     * @return int: the length of vector
     * */
    public int getDancingSongList(ArrayList<HashMap<String, Object>> arrayList){

        String tempUrlString = "";
        if (arrayList.isEmpty()){
            return 0;
        }
        for (int i=0; i<arrayList.size(); i++){
            tempUrlString = (String)arrayList.get(i).get("musicFileUrl");
            Log.d("TIEJIANG", "MenuActivity---getDancingSongList" + " tempUrlString= " + tempUrlString);
            if (tempUrlString.contains("dancingSong")){
//                Log.d("TIEJIANG", "MenuActivity---getDancingSongList" + " tempUrlString－－－dancingSong= " + tempUrlString);

                mDanceVector.add(tempUrlString);

            }
        }
        return mDanceVector.size();
    }

    /**
     * get the warning song list from myMediaList
     * */
//    public void getWarningToneList(ArrayList<HashMap<String, Object>> arrayList){
//
//        String tempUrlString = "";
//        if (arrayList.isEmpty()){
//            return;
//        }
//        for (int i=0; i<arrayList.size(); i++){
//            tempUrlString = (String)arrayList.get(i).get("musicFileUrl");
//            if (tempUrlString.contains("warningTone")){
//                mWarningToneVector.add(tempUrlString);
//            }
//        }
//    }

    public void initYTX(String mobile){

        //save app key/ID and contact number etc. and init rong-lian-yun SDK
        ClientUser clientUser = new ClientUser(mobile);
        clientUser.setAppKey(Constant.appKey);
        clientUser.setAppToken(Constant.token);
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
                int jsonSignal = 0;
                while(f){
                    //服务器端接收来自客户端的数据
                    ds.receive(dp_receive);
//                    Log.d("TIEJIANG", "server received data from client：");
                    String str_receive = new String(dp_receive.getData(),0,dp_receive.getLength());
                    Log.d("TIEJIANG", "MenuActivity---UDPRunnable" + " str_receive= " + str_receive);

                    jsonSignal = analysisJSONData(str_receive);
                    if (jsonSignal == 2){
                        String handShakeSend = handleJSON("local_handed"+","+batteryValue, currentIP());
                        dp_send = new DatagramPacket(handShakeSend.getBytes(),handShakeSend.length(),dp_receive.getAddress(),21240);
                    }else if(jsonSignal == 1) {
                        String xiaoleSetting = handleJSON("IDSetted"+","+batteryValue, currentIP());
                        dp_send = new DatagramPacket(xiaoleSetting.getBytes(),xiaoleSetting.length(),dp_receive.getAddress(),21240);
                    }else if (jsonSignal == 3){
                        String localCommandSend = handleJSON("commandSended", currentIP());
                        dp_send = new DatagramPacket(localCommandSend.getBytes(),localCommandSend.length(),dp_receive.getAddress(),21240);
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

    /**
     * function: package json string
     * @return json string
     * */
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
     * old version:
     *      true : YTX ID setted
     *      false : handed with mobile side
     * now new version:
     *      0: nothing
     *      1:YTX ID setted
     *      2:handed with mobile side
     *      3:local control command from mobile side
     * */
    public int analysisJSONData(String json_string){

        String JSONString = json_string;
        int jsonResult = 0;
        if (JSONString == null){
            return 0;
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
                jsonResult = 1;

            }else if (wanted.equals("search") && name.equals("XiaoleClient") && hostip != null){

                jsonResult = 2;
            }else if(wanted.equals("sendLocalControlCommand") && name.equals("XiaoleClient") && hostip != null){
                jsonResult = 3;
                //解析指令（并通过串口向下发送）
                analysisCommand(clientContent);
            }
        }catch (JSONException e){
            e.printStackTrace();
            Log.d("TIEJIANG", "JSONException");
        }
        Log.d("TIEJIANG", "MenuActivity---analysisJSONData" + " jsonResult= " + jsonResult);
        return jsonResult;
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
        final String YTXH3ID = ytx_id[1];
        Log.d("TIEJIANG", " MenuActivity---saveYTXID id[0]= " + id[0] + ", id[1]= " + id[1]);
        //没有存储　或者　之前有存储，但是（由于移动端重新安装了ＡＰＰ导致重新生成了ＩＤ）和Ｈ３平台不一致，也要重新存储
        if ((id[0].equals("0") && id[1].equals("1")) || (id[0].length() > 5 && !id[0].equals(ytx_id[0]))){

            mYTXSharedPreferences = getSharedPreferences(Constant.USER_MESSAGE, Context.MODE_PRIVATE);
            SharedPreferences.Editor mEditor = mYTXSharedPreferences.edit();
            mEditor.putString(Constant.MOBILE_ID, ytx_id[0]);
            mEditor.putString(Constant.H3_ID, ytx_id[1]);
            isSaved = mEditor.commit();
        }
//        Log.d("TIEJIANG", " MenuActivity---saveYTXID isSaved= " + isSaved);
        if (isSaved){
            //注销当前登录
            logout(false);
            ECDevice.logout(new ECDevice.OnLogoutListener() {
                @Override
                public void onLogout() {
//                    Log.d("TIEJIANG", " MenuActivity---saveYTXID old id logout");
                    //存储成功且旧的ＩＤ已经退出，初始化和登录新的云通讯ＩＤ
                    initYTX(YTXH3ID);
                }
            });
            //重新启动ＡＰＰ使新的登录id生效 重新启动之后，在onCreate里面重新初始化云通讯
//            restartXiaoleApp();
//            Log.d("TIEJIANG", " MenuActivity---saveYTXID isSaved= " + isSaved + "　initYTX()");
//            mYTXInitHandler.obtainMessage(1, ytx_id).sendToTarget();
        }
    }

    /**
     * function: restart xiaole app to refresh YTX new ID
     * make the new YTX ID works
     * */
    public void restartXiaoleApp(){

        final Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Log.d("TIEJIANG", " MenuActivity---restartXiaoleApp");
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
            myMediaList = new StateMusicMediaPlayer(mMenuActivityContext).scanAllAudioFiles();
//            for (int i=0; i<myMediaList.size(); i++){
//                Log.d(Constant.TAG, "path List= " + myMediaList.get(i).get("musicFileUrl"));
//            }
            Message mMessage = new Message();
            mMessage.what = Constant.SEARCH_MEDIASOURCE_COMPLETED_FROM_SDCARD;
            if (myMediaList.size() == 0){
                mMessage.obj = "searchMediaFailed";
                mStateManagementHandler.sendMessage(mMessage);
            }else{
                mMessage.obj = "searchMediaCompleted";
                mStateManagementHandler.sendMessage(mMessage);
            }
        }
    }


    //解析移动端发送的指令（包括通过云通讯ＩＭ和ＵＤＰ发送的两种形式指令）
    public void analysisCommand(String command){

        String controlCommand = command;
        if (controlCommand == null){
            Log.d(Constant.TAG, "command is invalid");
            return;
        }
        //底盘控制指令解析
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
        }
        //头部控制指令解析
        if (controlCommand.equals(Constant.H3_TURN_HEAD_UP)){
            mDataSendHandler.obtainMessage(0, fillCommand(Constant.H3ControlHeadToLookUp)).sendToTarget();
        }else if (controlCommand.equals(Constant.H3_TURN_HEAD_DOWN)){
            mDataSendHandler.obtainMessage(0, fillCommand(Constant.H3ControlHeadToLookDown)).sendToTarget();
        }else if (controlCommand.equals(Constant.H3_TURN_HEAD_LEFT)){
            mDataSendHandler.obtainMessage(0, fillCommand(Constant.H3ControlHeadToLeft)).sendToTarget();
        }else if (controlCommand.equals(Constant.H3_TURN_HEAD_RIGHT)){
            mDataSendHandler.obtainMessage(0, fillCommand(Constant.H3ControlHeadToRight)).sendToTarget();
        }
        //握手信号解析　//  && !isGuangJiaAPPConnectNetBegin
        if (controlCommand.equals(Constant.HAND_SHAKE)){
            handleSendTextMessage(Constant.HAND_OK + "," + batteryValue);
            Log.d("TIEJIANG", "MenuActivity---send to mobile---handed");
        }
        //解析移动端拍照指令（通过云通讯IM发送）
        if (controlCommand.equals(Constant.TAKE_PHOTO)){

            Log.d("TIEJIANG", "MenuActivity---analysisCommand　take photo");
            Intent mIntent = new Intent(this, MyCameraActivity.class);
            startActivityForResult(mIntent, 0);
//            String broadcastIntent = Constant.LEXING_ACTION;    //广佳ＡＰＰ收
//            Intent intent = new Intent(broadcastIntent);
//            intent.putExtra("MESSAGE", Constant.PHOTO_TAKE);
//            MenuActivity.this.sendBroadcast(intent);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 0:
                String photoUrlTemp = data.getStringExtra("photo_url");
                Log.d("TIEJIANG", "MenuActivity---onActivityResult"+" photoUrl= "+photoUrlTemp);
                break;
            case 1:
                String photoUrl = data.getStringExtra("photo_url");
                Log.d("TIEJIANG", "MenuActivity---onActivityResult"+" photoUrl= "+photoUrl);
                handleSendImageMessage(photoUrl);
                break;
        }
    }

    /**
     * callbacke for MyCameraActivity
     * not used now !!!
     * instead of onActivityResult method
     * */
    public void getPhotoCallback(){

//        InstanceHelper.mMyCameraActivity.isSavedPhotos(new TakePhotoAndSave() {
//            @Override
//            public void onPhotoTakeAndSave(String photo_url) {
//                Log.d("TIEJIANG", "MenuActivity---getPhotoCallback");
//            }
//        });
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
        analysisCommand(message);
    }

    private void analysisYTXData(String message){

     if (message.equals(Constant.HAND_SHAKE)){  //握手信号
            handleSendTextMessage(Constant.HAND_OK);
        }
    }

    /**
     * 处理文本发送方法事件通知
     * @param text
     */
    public void handleSendTextMessage(CharSequence text) {
        if(text == null) {
            return ;
        }
//        if(text.toString().trim().length() <= 0) {
        //canotSendEmptyMessage();
//            return ;
//        }
        // 组建一个待发送的ECMessage
        ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
        // 设置消息接收者
        //msg.setTo(mRecipients);
        msg.setTo(contactID); // attenionthis number is not the login number! / modified by tiejiang
        ECTextMessageBody msgBody=null;
            // 创建一个文本消息体，并添加到消息对象中
            msgBody = new ECTextMessageBody(text.toString());
            msg.setBody(msgBody);
            Log.d("TIEJIANG", "[MenuActivity]-handleSendTextMessage" + ", txt = " + text);// add by tiejiang
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

    /**
     * 处理图片发送方法事件通知
     * @param text
     */
    public void handleSendImageMessage(String img_path) {
        if(img_path == null) {
            return ;
        }
//        if(text.toString().trim().length() <= 0) {
        //canotSendEmptyMessage();
//            return ;
//        }
        // 组建一个待发送的ECMessage
        ECMessage msg = ECMessage.createECMessage(ECMessage.Type.IMAGE);
        // 设置消息接收者
        //msg.setTo(mRecipients);
        msg.setTo(contactID); // attenionthis number is not the login number! / modified by tiejiang
        ECImageMessageBody mECImageMessageBody = null;
        String imgString = img_path;
        //send img to mobile side
        if (imgString != null){
            mECImageMessageBody = new ECImageMessageBody();
            String imgFileName = imgString.split("/")[5];
//            String imgFileExt = imgFileName.split("\\.")[1];
            String imgFileExt = DemoUtils.getExtensionName(imgFileName);
            Log.d("TIEJIANG", "[MenuActivity]-handleSendTextMessage"
                    +", imgString = "+imgString+", imgFileName= "+imgFileName+", imgFileExt= "+imgFileExt);
            mECImageMessageBody.setFileName(imgFileName);
            mECImageMessageBody.setFileExt(imgFileExt);
            mECImageMessageBody.setLocalUrl(imgString);
            msg.setBody(mECImageMessageBody);
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
            Log.d("TIEJIANG", "[MenuActivity]-SendECMessage");
            rowId = IMChattingHelper.sendECMessage(msg);

            //}
            // 通知列表刷新
            //msg.setId(rowId);
            //notifyIMessageListView(msg);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TIEJIANG", "[MenuActivity]-send failed");
        }
    }
}
