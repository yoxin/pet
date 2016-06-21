package com.luna.anytime;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

public class AnyTimeApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		AVOSCloud.useAVCloudCN();
		// U need your AVOS key and so on to run the code.
		AVOSCloud.initialize(this,
				"Nbal01127iFLYK645Lq6K2rD-gzGzoHsz",
				"vvTyOpjs38gBldOjKd3jG63W");
	}
}
