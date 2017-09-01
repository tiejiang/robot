package com.xiaole.xiaolerobot.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xiaole.xiaolerobot.util.Constant;
import com.xiaole.xiaolerobot.util.serialportdatamanagement.UartDataManagement;

import static com.xiaole.xiaolerobot.ui.activity.MenuActivity.mStateManagementHandler;
import static com.xiaole.xiaolerobot.util.serialportdatamanagement.UartDataManagement.mDataSendHandler;

/**
 * Created by yinyu-tiejiang on 17-8-15.
 */

public class LexinApplicationReceiver extends BroadcastReceiver {

    private static final String LEXIN_ACTION = "ACTION_LEXIN_TO_YINYU";
    private UartDataManagement mUartDataManagement = UartDataManagement.getUartInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TIEJIANG", "LexinApplicationReceiver---onReceive");
        //广播接受
        if (intent.getAction().equals(LEXIN_ACTION)){
            String receiveStr = intent.getStringExtra("MESSAGE").toString().trim();
            Log.d("TIEJIANG", "LexinApplicationReceiver---receiveStr = " + receiveStr);
            if (receiveStr.equals("forward")){
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.forward)).sendToTarget();
                Log.d("TIEJIANG", "LexinApplicationReceiver---forward");
            }else if(receiveStr.equals("back")){
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.back)).sendToTarget();
                Log.d("TIEJIANG", "LexinApplicationReceiver---back");
            }else if(receiveStr.equals("turn_left")){
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.turnLeft)).sendToTarget();
                Log.d("TIEJIANG", "LexinApplicationReceiver---turn_left");
            }else if(receiveStr.equals("turn_right")){
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.turnRight)).sendToTarget();
                Log.d("TIEJIANG", "LexinApplicationReceiver---turn_right");
            }else if(receiveStr.equals("look_up")){
                Log.d("TIEJIANG", "LexinApplicationReceiver---look_up");
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.lookUp)).sendToTarget();
            }else if(receiveStr.equals("look_down")){
                Log.d("TIEJIANG", "LexinApplicationReceiver---look_down");
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.lookDown)).sendToTarget();
            }else if(receiveStr.equals("dance")){
                Log.d("TIEJIANG", "LexinApplicationReceiver---dance");
                mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_DANCE_BEGIN);
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.danceModeOne)).sendToTarget();
            }else if (receiveStr.equals(Constant.CONNECT_NET)){
                //暂未使用
                mStateManagementHandler.obtainMessage(1, Constant.CONNECT_NET).sendToTarget();
            }else if (receiveStr.equals(Constant.CONNECT_NET_END)){
                //暂未使用
                mStateManagementHandler.obtainMessage(1, Constant.CONNECT_NET_END).sendToTarget();
            }

        }

    }
}
