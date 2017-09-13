package com.xiaole.xiaolerobot.application;
/*
* Copyright 2009 Cedric Priscal
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


import android.util.Log;

import com.xiaole.xiaolerobot.common.CCPAppManager;
import com.xiaole.xiaolerobot.util.serialport.SerialPort;
import com.xiaole.xiaolerobot.util.serialport.SerialPortFinder;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

public class Application extends android.app.Application {

    private static Application instance;
    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CCPAppManager.setContext(instance);
    //FileAccessor.initFileAccess();
    //setChattingContactId();
    //initImageLoader();
    //CrashHandler.getInstance().init(this);
    //SDKInitializer.initialize(instance);
    //CrashReport.initCrashReport(getApplicationContext(), "900050687", true);
    }


    /**
     * 单例，返回一个实例
     * @return
     */
    public static Application getInstance() {
        if (instance == null) {
            Log.d("TIEJIANG", "[Application] instance is null.");
        }
        Log.d("TIEJIANG", "[ECApplication] return instance succeed.");
        return instance;
    }

    public SerialPort getSerialPort()
            throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            /* Read serial port parameters */

//            String packageName = getPackageName();
//            SharedPreferences sp = getSharedPreferences(packageName + "_preferences", MODE_PRIVATE);
//            String path = sp.getString("DEVICE", "");

//            String path = "/dev/ttyS0";

            String path = "/dev/ttyS2";
//            Log.d("TIEJIANG", "Application---DEVICE path= " + path);
//            int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

            int baudrate = 9600;
//            int baudrate = 115200;
//            Log.d("TIEJIANG", "Application---DEVICE baudrate= " + baudrate);
			/* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}