package com.example.administrator.pet;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demo.floatwindowdemo.R;


import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private FirstFragment fg1;
    private SecondFragment fg2;
    private ThirdFragment fg3;

    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private MyFragmentPagerAdapter mAdapter;

    private RelativeLayout first_fragment;
    private RelativeLayout second_fragment;
    private RelativeLayout third_fragment;

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;

    private TextView text1;
    private TextView text2;
    private TextView text3;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private int Gray = 0xFF999999;
    public FragmentManager fManager;

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fManager = getSupportFragmentManager();
        initViewPager();
        initViews();
        initSharedPreferences();
        setTab(0);
        if(!isEnabled()){
            new AlertDialog.Builder(MainActivity.this).setTitle("是否开启Notification access")//设置对话框标题

                    .setMessage("若想启动微信消息提醒则开启")//设置显示的内容

                    .setPositiveButton("开启", new DialogInterface.OnClickListener() {//添加确定按钮


                        @Override

                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                            // TODO Auto-generated method stub

                            startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));

                        }

                    }).setNegativeButton("取消",new DialogInterface.OnClickListener() {//添加返回按钮



                @Override

                public void onClick(DialogInterface dialog, int which) {//响应事件

                    // TODO Auto-generated method stub


                }

            }).show();//在按键响应事件中显示此对话框
        }
//        startFloatWindow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
//                startService(intent);
//                finish();
//            }
//        });
    }

    private void initViews() {
        mPager = (ViewPager) findViewById(R.id.mPager);
        first_fragment = (RelativeLayout) findViewById(R.id.first_fragment);
        second_fragment = (RelativeLayout) findViewById(R.id.second_fragment);
        third_fragment = (RelativeLayout) findViewById(R.id.third_fragment);
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);
        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(this);
        first_fragment.setOnClickListener(this);
        second_fragment.setOnClickListener(this);
        third_fragment.setOnClickListener(this);
    }

    private void initViewPager()
    {
        fragmentsList = new ArrayList<Fragment>();
        fg1 = new FirstFragment();
        fg2 = new SecondFragment();
        fg3 = new ThirdFragment();
        fragmentsList.add(fg1);
        fragmentsList.add(fg2);
        fragmentsList.add(fg3);
        mAdapter = new MyFragmentPagerAdapter(fManager,fragmentsList);
    }

    private void initSharedPreferences(){
        sharedPreferences = getSharedPreferences("pet", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //第一次登陆时的初始化
        if(!sharedPreferences.contains("times")){
            editor.putInt("times",1);
            editor.putString("name1", "皮卡");
            editor.putString("name2","鳄鱼");
            editor.putBoolean("isSecondUnlock",true);
            editor.putBoolean("isFirstOn",false);
            editor.putBoolean("isSecondOn",false);
            //设置界面的初始化
            editor.putBoolean("show", true);
            editor.putBoolean("always", true);
            editor.putBoolean("on",false);
            editor.putBoolean("set",true);
            editor.putBoolean("time",false);
            editor.commit();
            editor.commit();
        }

    }

    public void clearChioce()
    {
        image1.setImageResource(R.drawable.home);
        text1.setTextColor(Gray);
        image2.setImageResource(R.drawable.clock);
        text2.setTextColor(Gray);
        image3.setImageResource(R.drawable.setting);
        text3.setTextColor(Gray);

    }
    public void setTab(int num)
    {
        switch (num) {
            case R.id.first_fragment:case 0:
                image1.setImageResource(R.drawable.home_focus);
                text1.setTextColor(Color.parseColor("#02a9f5"));
                mPager.setCurrentItem(0);
                break;
            case R.id.second_fragment:case 1:
                image2.setImageResource(R.drawable.clock_focus);
                text2.setTextColor(Color.parseColor("#02a9f5"));
                mPager.setCurrentItem(1);
                break;
            case R.id.third_fragment:case 2:
                image3.setImageResource(R.drawable.setting_focus);
                text3.setTextColor(Color.parseColor("#02a9f5"));
                mPager.setCurrentItem(2);
                break;
            default:
                break;
        }

    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        clearChioce();
        setTab(v.getId());
    }
    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

        if(arg0 == 2)
        {
            int i = mPager.getCurrentItem();
            clearChioce();
            setTab(i);
        }
    }
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub

    }

    //判断是否开启了Notification access
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}