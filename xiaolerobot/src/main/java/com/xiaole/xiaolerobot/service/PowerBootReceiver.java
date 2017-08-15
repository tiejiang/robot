package com.xiaole.xiaolerobot.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaole.xiaolerobot.ui.activity.MenuActivity;

import static android.content.Intent.ACTION_BOOT_COMPLETED;


/**
 * Created by yinyu-tiejiang on 17-8-8.
 */

public class PowerBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_BOOT_COMPLETED)){
            // 启动应用首界面
            Intent actIntent = new Intent(context.getApplicationContext(), MenuActivity.class);
            actIntent.setAction("android.intent.action.MAIN");
            actIntent.addCategory("android.intent.category.LAUNCHER");
            actIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(actIntent);
            //不要在下面添加任何代码－－－２６１７
//            // 启动应用，参数为需要自动启动的应用的包名
//            Intent serIntent= new Intent(context, BootService.class);
//            serIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startService(serIntent);
//            Log.v("TAG", "开机程序自动启动.....");
        }
    }
}
