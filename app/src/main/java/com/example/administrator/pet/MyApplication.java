package com.example.administrator.pet;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.morgoo.droidplugin.PluginHelper;

/**
 * Created by zhengyoxin on 16-6-14.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        // 这里必须在super.onCreate方法之后，顺序不能变
        PluginHelper.getInstance().applicationOnCreate(getBaseContext());
        AVOSCloud.useAVCloudCN();
        // U need your AVOS key and so on to run the code.
        AVOSCloud.initialize(this,
                "Nbal01127iFLYK645Lq6K2rD-gzGzoHsz",
                "vvTyOpjs38gBldOjKd3jG63W");
    }

    @Override
    protected void attachBaseContext(Context base) {
        PluginHelper.getInstance().applicationAttachBaseContext(base);
        super.attachBaseContext(base);
    }
}
