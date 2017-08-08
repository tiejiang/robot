/*
 * Copyright 2011 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.xiaole.xiaolerobot.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.xiaole.xiaolerobot.R;
import com.xiaole.xiaolerobot.ui.activity.base.SerialPortActivity;

import java.io.IOException;

public class UartTestActivity extends SerialPortActivity implements View.OnClickListener{

    //serial port parameter
    SendingThread mSendingThread;
    // test buffer           S             K            nul         soh           er           er            nl
    byte[] mBuffer = {(byte) 0x53, (byte) 0x4B, (byte) 0x00, (byte) 0x01, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};
    //BaseBuffer: mBaseBuffer[2],mBaseBuffer[3] should be replaced by zhe real command
    private byte[] mBaseCommandBuffer = {(byte) 0x53, (byte) 0x4B, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};
    private byte[] forward = {(byte) 0x00, (byte) 0x01};
    private byte[] back = {(byte) 0x00, (byte) 0x02};
    private byte[] turnLeft = {(byte) 0x00, (byte) 0x03};
    private byte[] turnRight = {(byte) 0x00, (byte) 0x04};
    private byte[] bodyStop = {(byte) 0x00, (byte) 0x05};
    private byte[] turnHeadLeft = {(byte) 0x00, (byte) 0x06};
    private byte[] turnHeadRight = {(byte) 0x00, (byte) 0x07};              //头右转
    private byte[] nod = {(byte) 0x00, (byte) 0x025};                       //点头
    private byte[] shakeHead = {(byte) 0x00, (byte) 0x26};                  //摇头
    private byte[] shakeHeadStop = {(byte) 0x00, (byte) 0x08};              //摇头停
    private byte[] shakeHeadToMiddle = {(byte) 0x00, (byte) 0x09};          //摇头回中
    private byte[] lookUp = {(byte) 0x00, (byte) 0x0A};                     //抬头
    private byte[] lookDown = {(byte) 0x00, (byte) 0x0B};                   //低头
    private byte[] nodStop = {(byte) 0x00, (byte) 0x0C};                    //点头停
    private byte[] nodToMiddle = {(byte) 0x00, (byte) 0x0D};                //点头回中
    private byte[] turnOnLeftEye = {(byte) 0x00, (byte) 0x0E};              //左眼睛开
    private byte[] turnOnRightEye = {(byte) 0x00, (byte) 0x0F};             //右眼睛开
    private byte[] turnOffLeftEye = {(byte) 0x00, (byte) 0x10};             //左眼睛关
    private byte[] turnOffRihtEye = {(byte) 0x00, (byte) 0x11};             //右眼睛关
    private byte[] powerOnAction = {(byte) 0x00, (byte) 0x3B};              //电源开机动作
    private byte[] lookLeft = {(byte) 0x00, (byte) 0x28};                   //看左边
    private byte[] lookRight = {(byte) 0x00, (byte) 0x27};                  //看右边
    private byte[] cycleOnce = {(byte) 0x00, (byte) 0x29};                  //看左边
    private byte[] danceModeOne = {(byte) 0x00, (byte) 0x2A};               //舞蹈１
    private byte[] randomActionModeOne = {(byte) 0x00, (byte) 0x2D};        //随机动作1
    private byte[] turnBack = {(byte) 0x00, (byte) 0x37};                   //转身
    private byte[] kongfuModeOne = {(byte) 0x00, (byte) 0x62};              //功夫１
    private byte[] leftWheelForward = {(byte) 0x00, (byte) 0x67};           //左轮前进
    private byte[] rigtWheelForward = {(byte) 0x00, (byte) 0x68};           //右轮前进
    private byte[] headsetLightsOn = {(byte) 0x00, (byte) 0x6B};            //耳机灯闪亮
    private byte[] eyeLightsOn = {(byte) 0x00, (byte) 0x6C};                //眼睛灯闪亮
    private byte[] leftWheelBack = {(byte) 0x00, (byte) 0x6F};              //左轮后退
    private byte[] rigtWheelBack = {(byte) 0x00, (byte) 0x70};              //右轮后退
    private byte[] headToMiddle = {(byte) 0x00, (byte) 0x71};               //头部回中
    private byte[] H3ControlHeadToRight = {(byte) 0x00, (byte) 0x72};       //IPC控头右
    private byte[] H3ControlHeadToLeft = {(byte) 0x00, (byte) 0x73};        //IPC控头左
    private byte[] H3ControlHeadToLookUp = {(byte) 0x00, (byte) 0x74};      //IPC控抬头
    private byte[] H3ControlHeadToLookDown = {(byte) 0x00, (byte) 0x75};    //IPC控低头
    private byte[] H3ControlBodyToLeft = {(byte) 0x00, (byte) 0x76};        //IPC控身体左转
    private byte[] H3ControlBodyToRight = {(byte) 0x00, (byte) 0x77};       //IPC控身体右转
    private byte[] H3ControlForward = {(byte) 0x00, (byte) 0x78};           //IPC控前进
    private byte[] H3Controlback = {(byte) 0x00, (byte) 0x79};              //IPC控后退
    private byte[] H3ControlContinuousLeft = {(byte) 0x00, (byte) 0x7A};    //IPC控连续左转
    private byte[] H3ControlContinuousRight = {(byte) 0x00, (byte) 0x7B};   //IPC控连续右转
    private byte[] H3TurnOnConversation = {(byte) 0x00, (byte) 0x7C};       //IPC打开对话功能
    private byte[] H3TurnOffConversation = {(byte) 0x00, (byte) 0x7D};      //IPC关闭对话功能
    private byte[] doubleEyeOn = {(byte) 0x00, (byte) 0x82};                //双眼睛开
    private byte[] doubleEyeOff = {(byte) 0x00, (byte) 0x83};               //双眼睛关
    private byte[] soundOn = {(byte) 0x00, (byte) 0x84};                    //声音开
    private byte[] soundOff = {(byte) 0x00, (byte) 0x85};                   //声音关



    private Button mButtonForward;
    private Button mButtonBack;
    private Button mButtonLeft;
    private Button mButtonRight;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sending01010101);
        mButtonForward = (Button)findViewById(R.id.forward);
        mButtonBack = (Button)findViewById(R.id.back);
        mButtonLeft = (Button)findViewById(R.id.left);
        mButtonRight = (Button)findViewById(R.id.right);
        mButtonForward.setOnClickListener(this);
        mButtonBack.setOnClickListener(this);
        mButtonLeft.setOnClickListener(this);
        mButtonRight.setOnClickListener(this);

//        if (mSerialPort != null) {
//            mSendingThread = new SendingThread();
//            mSendingThread.start();
//        }
//        mBuffer = new byte[1024];
//        Arrays.fill(mBuffer, (byte) 0x55);   // 0x55 --> U
//        Arrays.fill(mBuffer, (byte) 0x56);   // 0x56 --> V
        if (mSerialPort != null) {
            mSendingThread = new SendingThread();
            mSendingThread.start();
        }
    }

    //serial port receive data
    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        // ignore incoming data
//        runOnUiThread(new Runnable() {
//            public void run() {
//                if (mReception != null) {
//                    mReception.append(new String(buffer, 0, size));
//                }
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.forward:
//                mSendingThread.start();
                mHandler.obtainMessage(0, fillCommand(forward)).sendToTarget();
                break;
            case R.id.back:
                mHandler.obtainMessage(0, fillCommand(back)).sendToTarget();
                break;
            case R.id.left:
                mHandler.obtainMessage(0, fillCommand(turnLeft)).sendToTarget();
                break;
            case R.id.right:
                mHandler.obtainMessage(0, fillCommand(turnRight)).sendToTarget();
                break;

        }
    }

    //将实际指令填充到mBaseCommandBuffer里面
    public byte[] fillCommand(byte[] command){
        mBaseCommandBuffer[2] = command[0];
        mBaseCommandBuffer[3] = command[1];

        return mBaseCommandBuffer;
    }
    //serial port sending thread
    private class SendingThread extends Thread {
        @Override
        public void run() {
//            int i = 0;
//            while (i < 10) {
            Looper.prepare();
            mHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    byte[] buffer = (byte[]) msg.obj;
                    switch (msg.what){
                        case 0:
                            try {
                                if (mOutputStream != null) {
                                    mOutputStream.write(buffer);
                                } else {
                                    return;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                            break;
//                        case 1:
////                            Log.d("TIEJIANG", (buffer)message.obj);
//                            break;
                    }

                }
            };
            Looper.loop();

//                i ++;
//            }
        }
    }

}
