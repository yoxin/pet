package com.example.administrator.pet;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.demo.floatwindowdemo.R;

public class AlarmReceiver extends BroadcastReceiver {

    static final int NOTIFICATION_ID = 0x123;
    NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        //收到闹钟Intent,解封数据
        Bundle bundle=intent.getExtras();
        String task=bundle.getString("task");
        String addition=bundle.getString("addition");
        String date=bundle.getString("date");
        String time=bundle.getString("time");

        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);

        if (mKeyguardManager.inKeyguardRestrictedInputMode()) {
            // keyguard on
            NotificationManager notifyMgr= (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            //【2】设置通知。PendingIntent表示延后触发，是在用户下来状态栏并点击通知时触发，触发时PendingIntent发送intent，本例为打开浏览器到指定页面。
            Intent intent1 = new Intent();
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent1, 0);
            Notification notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setTicker("闹钟提醒")
                    .setContentTitle(task)
                    .setContentText(addition)
                    .setContentIntent(pi)
                    .build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL; //点击后删除，如果是FLAG_NO_CLEAR则不删除，FLAG_ONGOING_EVENT用于某事正在进行，例如电话，具体查看参考。
            //【3】发送通知到通知管理器。第一个参数是这个通知的唯一标识，通过这个id可以在以后cancel通知，更新通知（发送一个具有相同id的新通知）。这个id在应用中应该是唯一的。
            notifyMgr.notify(NOTIFICATION_ID, notification);

        }
        else{
            //封装Intent,以便发送局部广播
            Intent msgrcv = new Intent("Alarm");
            msgrcv.putExtra("task", task);
            msgrcv.putExtra("addition", addition);
            msgrcv.putExtra("date", date);
            msgrcv.putExtra("time", time);
            System.out.println("闹钟");
            //发送局部广播
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
        }

    }

}