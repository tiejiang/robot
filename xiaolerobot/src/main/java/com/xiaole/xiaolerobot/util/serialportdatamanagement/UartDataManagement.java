package com.xiaole.xiaolerobot.util.serialportdatamanagement;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.xiaole.xiaolerobot.application.Application;
import com.xiaole.xiaolerobot.instancefractory.InstanceHelper;
import com.xiaole.xiaolerobot.util.Constant;
import com.xiaole.xiaolerobot.util.serialport.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import static com.xiaole.xiaolerobot.ui.activity.MenuActivity.mStateManagementHandler;

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

    private static byte[] uplinkBaseCommand = {(byte) 0x53, (byte) 0x4B, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};
    private static byte[] uplinkBatteryBaseCommand = {(byte) 0x53, (byte) 0x4B, (byte) 0x02, (byte) 0x00, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};
    private static byte[] uplinkOtherCommand = {(byte) 0x53, (byte) 0x4B, (byte) 0x01, (byte) 0x00, (byte) 0x0D, (byte) 0x0D, (byte) 0x0A};

    protected Application mApplication;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    public static Handler mDataSendHandler;
    private SendingThread mSendingThread;
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
            //ｕａｒｔ调用
            mSerialPort = mApplication.getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
//            DisplayError(R.string.error_security);
            Log.d("TIEJIANG", "You do not have read/write permission to the serial port.");
        }
        //关闭ｕａｒｔ时对应的ｉｏ异常关闭
        catch (IOException e) {
            Log.d("TIEJIANG", "unknown error");
        }
        catch (InvalidParameterException e) {
//            DisplayError(R.string.error_configuration);
            Log.d("TIEJIANG", "Please configure your serial port first.");
        }
        if (mSerialPort != null) {
            mSendingThread = new SendingThread();
            mSendingThread.start();
        }
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
//            while (!isInterrupted()) {
            while (true) {
                Log.d("TIEJIANG", "UartDataManagement---ReadThread");
                int size;
                try {
                    byte[] buffer = new byte[32];
                    if (mInputStream == null){
                        return;
                    }
                    size = mInputStream.read(buffer);
//                    size = mInputStream.read();
                    Log.d("TIEJIANG", "UartDataManagement---ReadThread---read"+" size= ");
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                    Thread.sleep(1000);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Log.d("TIEJIANG", "UartDataManagement---IOException");
                    return;
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                    Log.d("TIEJIANG", "UartDataManagement---InterruptedException");
                }
            }
        }
    }

    //uart receive data from mcu
    protected void onDataReceived(byte[] buffer, int size) {

//        Log.d("TIEJIANG", "UartDataManagement---onDataReceived");
        if (buffer[0] == uplinkBaseCommand[0] && buffer[1] == uplinkBaseCommand[1]){
            Log.d("TIEJIANG", "UartDataManagement---onDataReceived");
            if (buffer[2] == uplinkBatteryBaseCommand[2]){
                dealElectricQuantity(buffer);
            }else if (buffer[2] == uplinkOtherCommand[2]){
                dealOtherUplinkCommand(buffer);
            }
        }else {
            Log.d("TIEJIANG", "UartDataManagement---onDataReceived but not active");
        }
    }

    /**
     * function: deal electric quantity
     *
     * */
    public String dealElectricQuantity(byte[] battery_data_array){

        byte batteryValue = battery_data_array[3];
        String batterValueString = "";
//        int batteryValueInt = Integer.parseInt(String.valueOf(batteryValue), 16);
        batterValueString = String.valueOf(batteryValue);
        Log.d("TIEJIANG", "UartDataManagement---dealElectricQuantity"+" batterValueString= "+batterValueString);
        mStateManagementHandler.obtainMessage(Constant.BATTERY_VALUE_STATE, batterValueString).sendToTarget();
        return batterValueString;
    }

    /**
     * function: deal uplink command except battery quantity
     *
     * */
    public void dealOtherUplinkCommand(byte[] uplink_command){

        if (uplink_command[3] == Constant.FULLY_CHARGED){
            InstanceHelper.mMenuActivity.musicPlayBinder.playStateMusic(Constant.BATTERY_CHARGING_MUSIC[1]);
        }else if (uplink_command[3] == Constant.LOW_BATTERY){
            InstanceHelper.mMenuActivity.musicPlayBinder.playStateMusic(Constant.BATTERY_CHARGING_MUSIC[3]);
        }else if (uplink_command[3] == Constant.TOUCH_TO_AWAKEN){
            InstanceHelper.mMenuActivity.sendBroadcastToGuangjia(Constant.LEXIN_AWAKEN_XIAOLE);
        }else if (uplink_command[3] == Constant.TOUCH_TO_CONNNECT_NET){
            InstanceHelper.mMenuActivity.sendBroadcastToGuangjia(Constant.LEXIN_CONNECT_TO_NET);
        }

    }

    //将实际指令填充到mBaseCommandBuffer里面
    public byte[] fillCommand(byte[] command){
        mBaseCommandBuffer[2] = command[0];
        mBaseCommandBuffer[3] = command[1];

        return mBaseCommandBuffer;
    }

    //使用主线程发送指令到下位机
    public void sendCommand(byte[] command){

        try {
            if (mOutputStream != null) {
                mOutputStream.write(command);
                Log.d("TIEJIANG", "UI thread---FINAL STEP --- send command to MCU");
            } else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    //serial port sending thread
    public class SendingThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            mDataSendHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Log.d("TIEJIANG", "other thread---FINAL STEP --- handle message");
                    byte[] buffer = (byte[]) msg.obj;

                    //test code begin
//                    while (true){
//                        try {
//                            if (mOutputStream != null) {
//                                mOutputStream.write(abc);
//                                Log.d("TIEJIANG", "other thread---FINAL STEP --- send command to MCU");
//                            } else {
//                                return;
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            return;
//                        }
//                    }
                    //test code end


                    switch (msg.what){
                        case 0:
                            try {
                                if (mOutputStream != null) {
                                    mOutputStream.write(buffer);
                                    Log.d("TIEJIANG", "other thread---FINAL STEP --- send command to MCU");
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
        }
    }

    //关闭串口,线程等
    public void close(){
        if (mReadThread != null) mReadThread.interrupt();
        mApplication.closeSerialPort();
        mSerialPort = null;
    }
}
