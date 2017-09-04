package com.xiaole.xiaolerobot.util;

import java.util.Vector;

public class Constant {
    public static final String TAG = "TIEJIANG";
    public static final String PREF_PASSWORD_VALUE = "input_password_value";
    public static final String PREF_EMAIL_VALUE = "input_email_value";
    public static final String ACTIVITY_RESULT = "result";
    
//    public static final int REQUEST_CODE = 1;
//    public static final int ERROE_NUM = 5;

    //云通讯ＩＤ
    //    String appKey = "8aaf070858cd982e0158e21ff0000cee";
//    String token = "ca8bdec6e6ed3cc369b8122a1c19306d";

    public static final String appKey = "8a216da85e0e48b2015e1c039ba4056e";
    public static final String token = "4b07cb81f3a95ba0c183181ff04cb394";


    public static final int BOOT_COMPLETED_FROM_BROADCASTRECEIVER = 0;
    public static final int SEARCH_MEDIASOURCE_COMPLETED_FROM_SDCARD = 1;

    //用户的云通信ＩＤ
    public static final String USER_MESSAGE = "user_data";
    public static final String MOBILE_ID = "mobile_id";
    public static final String H3_ID = "h3_id";

    //移动端通过云通讯ＩＭ发来的握手信号
    public static final String HAND_SHAKE = "YTXHandshake";
    public static final String HAND_OK = "YTXHanded";

    //移动端通过云通讯ＩＭ发来的拍照信号
    public static final String TAKE_PHOTO = "take_photo";

    //移动端发送到小乐的控制指令－底盘控制
    public static final String H3_XIAOLE_FORWARD = "mobile_forward";
    public static final String H3_XIAOLE_BACK = "mobile_back";
    public static final String H3_XIAOLE_LEFT = "mobile_turn_left";
    public static final String H3_XIAOLE_RIGHT = "mobile_turn_right";

    //移动端发送到小乐的控制指令－头部控制
    public static final String H3_TURN_HEAD_UP = "mobile_turn_up";
    public static final String H3_TURN_HEAD_DOWN = "mobile_turn_down";
    public static final String H3_TURN_HEAD_LEFT = "mobile_turn_head_left";
    public static final String H3_TURN_HEAD_RIGHT = "mobile_turn_head_right";

    //广佳ＡＰＰ发送的广播指令
    public static final int XIAOLE_FORWARD = 10;
    public static final int XIAOLE_BACK = 20;
    public static final int XIAOLE_LEFT = 30;
    public static final int XIAOLE_RIGHT = 40;
    public static final int XIAOLE_UP = 50;
    public static final int XIAOLE_DOWN = 60;
    public static final int XIAOLE_DANCE_BEGIN = 70;
    public static final int XIAOLE_DANCE_MUSIC_END = 80;
    public static final String CONNECT_NET = "connect_net"; //开始联网
    public static final String CONNECT_NET_END = "connect_net_ok";//结束联网

    //小乐ＡＰＰ发广佳的广播
    public static final String LEXING_ACTION = "ACTION_YINYU_TO_LEXIN";
    public static final String PHOTO_TAKE = "photo_take";

    //通过MenuActivity启动拍照Activity的回调识别码
    public static final int REQUEST_CODE_PHOTO_TAKE = 0;

    //开机音乐　状态音乐/提示音
    public static String[] POWER_ON_MUSIC =
            {
                    "hello_waiting_for_you",
                    "xiaole_get_up",
                    "boring_play_with_me",
                    "xiaole_is_starting",
                    "hello_master"
            };

    //运行提示音乐-没有交互的时候播放(暂不使用－－－目前无法获得广佳的交互回调)
    public static String[] RUNTIME_STATE_MUSIC =
            {
                    "i_will_be_have_a_rest",
                    "listen_to_my_story",
                    "lets_sing_together",
                    "master_i_was_behaved",
                    "master_lets_learning"
            };

    //摸屁股提示音
    public static String[] TOUCH_HIP_MUSIC =
            {
                    "dont_touch_hips"
            };

    //充电相关提示
    public static String[] BATTERY_CHARGING_MUSIC =
            {
                    "low_battery_charging_soon",
                    "charging_over",
                    "begin_charging",
                    "charging_me_soon_or_i_will_die"
            };

    //向下位机发送的指令集
    public static byte[] forward = {(byte) 0x00, (byte) 0x01};
    public static byte[] back = {(byte) 0x00, (byte) 0x02};
    public static byte[] turnLeft = {(byte) 0x00, (byte) 0x03};
    public static byte[] turnRight = {(byte) 0x00, (byte) 0x04};
    public static byte[] bodyStop = {(byte) 0x00, (byte) 0x05};                   //身停
    public static byte[] turnHeadLeft = {(byte) 0x00, (byte) 0x06};
    public static byte[] turnHeadRight = {(byte) 0x00, (byte) 0x07};              //头右转
    public static byte[] nod = {(byte) 0x00, (byte) 0x025};                       //点头
    public static byte[] shakeHead = {(byte) 0x00, (byte) 0x26};                  //摇头
    public static byte[] shakeHeadStop = {(byte) 0x00, (byte) 0x08};              //摇头停
    public static byte[] shakeHeadToMiddle = {(byte) 0x00, (byte) 0x09};          //摇头回中
    public static byte[] lookUp = {(byte) 0x00, (byte) 0x0A};                     //抬头
    public static byte[] lookDown = {(byte) 0x00, (byte) 0x0B};                   //低头
    public static byte[] nodStop = {(byte) 0x00, (byte) 0x0C};                    //点头停
    public static byte[] nodToMiddle = {(byte) 0x00, (byte) 0x0D};                //点头回中
    public static byte[] turnOnLeftEye = {(byte) 0x00, (byte) 0x0E};              //左眼睛开
    public static byte[] turnOnRightEye = {(byte) 0x00, (byte) 0x0F};             //右眼睛开
    public static byte[] turnOffLeftEye = {(byte) 0x00, (byte) 0x10};             //左眼睛关
    public static byte[] turnOffRihtEye = {(byte) 0x00, (byte) 0x11};             //右眼睛关
    public static byte[] powerOnAction = {(byte) 0x00, (byte) 0x3B};              //电源开机动作
    public static byte[] lookLeft = {(byte) 0x00, (byte) 0x28};                   //看左边
    public static byte[] lookRight = {(byte) 0x00, (byte) 0x27};                  //看右边
    public static byte[] cycleOnce = {(byte) 0x00, (byte) 0x29};                  //看左边
    public static byte[] danceModeOne = {(byte) 0x00, (byte) 0x2A};               //舞蹈１
    public static byte[] randomActionModeOne = {(byte) 0x00, (byte) 0x2D};        //随机动作1
    public static byte[] turnBack = {(byte) 0x00, (byte) 0x37};                   //转身
    public static byte[] kongfuModeOne = {(byte) 0x00, (byte) 0x62};              //功夫１
    public static byte[] leftWheelForward = {(byte) 0x00, (byte) 0x67};           //左轮前进
    public static byte[] rigtWheelForward = {(byte) 0x00, (byte) 0x68};           //右轮前进
    public static byte[] headsetLightsOn = {(byte) 0x00, (byte) 0x6B};            //耳机灯闪亮
    public static byte[] eyeLightsOn = {(byte) 0x00, (byte) 0x6C};                //眼睛灯闪亮
    public static byte[] leftWheelBack = {(byte) 0x00, (byte) 0x6F};              //左轮后退
    public static byte[] rigtWheelBack = {(byte) 0x00, (byte) 0x70};              //右轮后退
    public static byte[] headToMiddle = {(byte) 0x00, (byte) 0x71};               //头部回中
    public static byte[] H3ControlHeadToRight = {(byte) 0x00, (byte) 0x72};       //IPC控头右
    public static byte[] H3ControlHeadToLeft = {(byte) 0x00, (byte) 0x73};        //IPC控头左
    public static byte[] H3ControlHeadToLookUp = {(byte) 0x00, (byte) 0x74};      //IPC控抬头
    public static byte[] H3ControlHeadToLookDown = {(byte) 0x00, (byte) 0x75};    //IPC控低头
    public static byte[] H3ControlBodyToLeft = {(byte) 0x00, (byte) 0x76};        //IPC控身体左转
    public static byte[] H3ControlBodyToRight = {(byte) 0x00, (byte) 0x77};       //IPC控身体右转
    public static byte[] H3ControlForward = {(byte) 0x00, (byte) 0x78};           //IPC控前进
    public static byte[] H3Controlback = {(byte) 0x00, (byte) 0x79};              //IPC控后退
    public static byte[] H3ControlContinuousLeft = {(byte) 0x00, (byte) 0x7A};    //IPC控连续左转
    public static byte[] H3ControlContinuousRight = {(byte) 0x00, (byte) 0x7B};   //IPC控连续右转
    public static byte[] H3TurnOnConversation = {(byte) 0x00, (byte) 0x7C};       //IPC打开对话功能
    public static byte[] H3TurnOffConversation = {(byte) 0x00, (byte) 0x7D};      //IPC关闭对话功能
    public static byte[] doubleEyeOn = {(byte) 0x00, (byte) 0x82};                //双眼睛开
    public static byte[] doubleEyeOff = {(byte) 0x00, (byte) 0x83};               //双眼睛关
    public static byte[] soundOn = {(byte) 0x00, (byte) 0x84};                    //声音开
    public static byte[] soundOff = {(byte) 0x00, (byte) 0x85};                   //声音关


    public static Vector<String> mNotEncryptionApp;
    public static void filter(){
    	mNotEncryptionApp = new Vector<String>();
        mNotEncryptionApp.add("com.android.contacts");
    	mNotEncryptionApp.add("com.android.settings");
    }
    
}
