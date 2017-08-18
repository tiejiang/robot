package com.xiaole.xiaolerobot.util.serialportdatamanagement;

import android.util.Log;

import com.xiaole.xiaolerobot.application.Application;
import com.xiaole.xiaolerobot.util.serialport.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

/**
 * Created by yinyu-tiejiang on 17-8-8.
 */

public class UartDataManagement {

    //BaseBuffer: mBaseBuffer[2],mBaseBuffer[3] should be replaced by zhe real command
    private byte[] mBaseCommandBuffer = {(byte) 0x53, (byte) 0x4B, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};
    // test code
    public static byte[] abc = {(byte) 0x53, (byte) 0x4B, (byte) 0x00, (byte) 0x01, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};
    public static byte[] abcd = {(byte) 0x53, (byte) 0x4B, (byte) 0x00, (byte) 0x02, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};
    public static byte[] abce = {(byte) 0x53, (byte) 0x4B, (byte) 0x00, (byte) 0x03, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};
    public static byte[] abcf = {(byte) 0x53, (byte) 0x4B, (byte) 0x00, (byte) 0x04, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};

    protected Application mApplication;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private static UartDataManagement uartInstance = null;

    public static UartDataManagement getUartInstance(){
        if (uartInstance == null){
            uartInstance = new UartDataManagement();
        }
        return uartInstance;
    }

    public UartDataManagement(){

        mApplication = Application.getInstance();
        try {
            mSerialPort = mApplication.getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
//            DisplayError(R.string.error_security);
            Log.d("TIEJIANG", "You do not have read/write permission to the serial port.");
        } catch (IOException e) {
            Log.d("TIEJIANG", "unknown error");
        } catch (InvalidParameterException e) {
//            DisplayError(R.string.error_configuration);
            Log.d("TIEJIANG", "Please configure your serial port first.");
        }
    }


    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    //uart receive data from mcu
    protected void onDataReceived(byte[] buffer, int size) {
        //保留作为单片机上发电池状态信息
//        runOnUiThread(new Runnable() {
//            public void run() {
//                if (mReception != null) {
//                    mReception.append(new String(buffer, 0, size));
//                }
//            }
//        });
    }

    //将实际指令填充到mBaseCommandBuffer里面
    public byte[] fillCommand(byte[] command){
        mBaseCommandBuffer[2] = command[0];
        mBaseCommandBuffer[3] = command[1];

        return mBaseCommandBuffer;
    }

    //主线程发送指令到下位机
    public void sendCommand(byte[] command){

        try {
            if (mOutputStream != null) {
                mOutputStream.write(command);
                Log.d("TIEJIANG", "FINAL STEP --- send command to MCU");
            } else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

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

    //关闭串口,线程等
    public void close(){
        if (mReadThread != null) mReadThread.interrupt();
        mApplication.closeSerialPort();
        mSerialPort = null;
    }
}
