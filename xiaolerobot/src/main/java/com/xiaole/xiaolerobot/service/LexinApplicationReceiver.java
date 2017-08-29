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
//                mUartDataManagement.sendCommand(mUartDataManagement.fillCommand(Constant.forward));
//                mUartDataManagement.sendCommand(UartDataManagement.abc);
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.forward)).sendToTarget();
                Log.d("TIEJIANG", "LexinApplicationReceiver---forward");
            }else if(receiveStr.equals("back")){
//                mUartDataManagement.sendCommand(mUartDataManagement.fillCommand(Constant.back));
//                mUartDataManagement.sendCommand(UartDataManagement.abcd);
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.back)).sendToTarget();
                Log.d("TIEJIANG", "LexinApplicationReceiver---back");
//                mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_BACK);
            }else if(receiveStr.equals("turn_left")){
//                mUartDataManagement.sendCommand(mUartDataManagement.fillCommand(Constant.turnLeft));
//                mUartDataManagement.sendCommand(UartDataManagement.abce);
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.turnLeft)).sendToTarget();
                Log.d("TIEJIANG", "LexinApplicationReceiver---turn_left");
//                mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_LEFT);
            }else if(receiveStr.equals("turn_right")){
//                mUartDataManagement.sendCommand(mUartDataManagement.fillCommand(Constant.turnRight));
//                mUartDataManagement.sendCommand(UartDataManagement.abcf);
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.turnRight)).sendToTarget();
                Log.d("TIEJIANG", "LexinApplicationReceiver---turn_right");
//                mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_RIGHT);
            }else if(receiveStr.equals("look_up")){
//                mUartDataManagement.sendCommand(mUartDataManagement.fillCommand(Constant.lookUp));
                Log.d("TIEJIANG", "LexinApplicationReceiver---look_up");
//                mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_UP);
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.lookUp)).sendToTarget();
            }else if(receiveStr.equals("look_down")){
//                mUartDataManagement.sendCommand(mUartDataManagement.fillCommand(Constant.lookDown));
                Log.d("TIEJIANG", "LexinApplicationReceiver---look_down");
//                mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_DOWN);
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.lookDown)).sendToTarget();
            }else if(receiveStr.equals("dance")){
//                mUartDataManagement.sendCommand(mUartDataManagement.fillCommand(Constant.danceModeOne));
                Log.d("TIEJIANG", "LexinApplicationReceiver---dance");
//                mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_DANCE);
                mStateManagementHandler.sendEmptyMessage(Constant.XIAOLE_DANCE_BEGIN);
                mDataSendHandler.obtainMessage(0, mUartDataManagement.fillCommand(Constant.danceModeOne)).sendToTarget();
            }

        }

    }
}
