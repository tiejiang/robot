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
import android.view.View;
import android.widget.Button;

import com.xiaole.xiaolerobot.R;
import com.xiaole.xiaolerobot.ui.activity.base.SerialPortActivity;

import java.io.IOException;

public class UartTestActivity extends SerialPortActivity implements View.OnClickListener{

    //serial port parameter
    SendingThread mSendingThread;
//    byte[] mBuffer = {(byte) 0x55, (byte) 0x56};
    //-----                  S             K            nul         soh           er           er            nl
    byte[] mBuffer = {(byte) 0x53, (byte) 0x4B, (byte) 0x00, (byte) 0x01, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};
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

        if (mSerialPort != null) {
            mSendingThread = new SendingThread();
            mSendingThread.start();
        }
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
                mHandler.obtainMessage(0, mBuffer).sendToTarget();
                break;

        }
    }

    //serial port sending thread
//    private class SendingThread extends Thread {
//        @Override
//        public void run() {
////            int i = 0;
////            while (i < 10) {
//            Looper.prepare();
//            mHandler = new Handler(){
//                @Override
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//                    switch (msg.what){
//                        case 0:
//                            Log.d("TIEJIANG", (String) msg.obj);
//                            try {
//                                if (mOutputStream != null) {
//                                    mOutputStream.write(((String) msg.obj).getBytes());
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
//
////                i ++;
////            }
//        }
//    }

    //serial port sending thread
    private class SendingThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    if (mOutputStream != null) {
                        mOutputStream.write(mBuffer);
                    } else {
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
