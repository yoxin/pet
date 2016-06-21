package com.example.administrator.pet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.demo.floatwindowdemo.R;

import java.lang.reflect.Field;

public class FloatWindowSmallView extends LinearLayout {

    /**
     * 记录小悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录小悬浮窗的高度
     */
    public static int viewHeight;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;

    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    /**
     * 记录当前手指是否按下
     */
    private boolean isPressed;

    //记录宠物当前是否贴边隐藏
    private boolean isHide;

    //宠物控件
    private ImageView img;

    //宠物图像的宽高
    private int imgWidth;

    private int imgHeigth;

    /**
     * 获取屏幕的高度宽度
    */
    private int screenHeight,screenWidth;

    //动画的定义，定义成全局以便在事件回调的时候能对动画进行操作
    private ValueAnimator anim;
    private AnimationDrawable runFrame;
    private ValueAnimator runAnim;

    private String title,text,task,date,time,addition;

    private  static final boolean isLeft  = true;
    private  static final boolean isWeChat  = true;
    private  static final boolean isAlarm  = false;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    public FloatWindowSmallView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
        View view = findViewById(R.id.small_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        img = (ImageView) findViewById(R.id.img_small);
        imgWidth = img.getLayoutParams().width;
        imgHeigth = img.getLayoutParams().height;
        sharedPreferences = context.getSharedPreferences("pet", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getBoolean("isFirstOn", true)){
            img.setBackgroundResource(R.drawable.pika_window);
        }
        else if(sharedPreferences.getBoolean("isSecondOn", true)){
            img.setBackgroundResource(R.drawable.kong_window);
        }


        //获取屏幕大小
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - getStatusBarHeight();

        LocalBroadcastManager.getInstance(context).registerReceiver(onNotice, new IntentFilter("Msg"));
        LocalBroadcastManager.getInstance(context).registerReceiver(onAlarm, new IntentFilter("Alarm"));
        isHide = false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                isPressed = true;
                if(anim!=null){
                    anim.cancel();
                    runAnim.cancel();
                    runFrame.stop();
                }
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                updateViewStatus();
                break;
            case MotionEvent.ACTION_MOVE:
                isHide = false;
                if(anim!=null){
                    anim.cancel();
                    runAnim.cancel();
                    runFrame.stop();
                }
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                updateViewStatus();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                isPressed = false;
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                    openBigWindow();
                }
                else if (xInScreen < screenWidth*1/4){  //当在这个位置放手时宠物贴边隐藏
                    hideLeft();
                }
                else if(xInScreen > screenWidth*3/4){
                    hideRight();
                }
                else {
                    isHide = false;
                    startAnimation();
                    updateViewStatus();
                }
                break;
            default:
                break;
        }
        return true;
    }



    private void hideLeft() {
        isHide = true;
        if(sharedPreferences.getBoolean("isFirstOn", true)){
            img.setBackgroundResource(R.drawable.pika_hide_left);
        }
        else if(sharedPreferences.getBoolean("isSecondOn", true)){
            img.setBackgroundResource(R.drawable.kong_hide_left);
        }

        mParams.x = 0;
        mParams.y = (int)yInScreen;
        windowManager.updateViewLayout(this, mParams);
    }

    private void hideRight() {
        isHide = true;
        if(sharedPreferences.getBoolean("isFirstOn", true)){
            img.setBackgroundResource(R.drawable.pika_hide_right);
        }
        else if(sharedPreferences.getBoolean("isSecondOn", true)){
            img.setBackgroundResource(R.drawable.kong_hide_right);
        }

        mParams.x = screenWidth ;
        mParams.y = (int)yInScreen;
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 更新View的显示状态，判断是否拎起。
     */
    private void updateViewStatus() {
        if(isHide&&isPressed) {
            if(xInScreen>screenWidth*1/2){
                if(sharedPreferences.getBoolean("isFirstOn", true)){
                    img.setBackgroundResource(R.drawable.pika_show_right);
                }
                else if(sharedPreferences.getBoolean("isSecondOn", true)){
                    img.setBackgroundResource(R.drawable.kong_show_right);
                }
            }

            else{
                if(sharedPreferences.getBoolean("isFirstOn", true)){
                    img.setBackgroundResource(R.drawable.pika_show_left);
                }
                else if(sharedPreferences.getBoolean("isSecondOn", true)){
                    img.setBackgroundResource(R.drawable.kong_show_left);
                }
            }

        }
        else if (isPressed&&!isHide ) {
            if(sharedPreferences.getBoolean("isFirstOn", true)){
                img.setBackgroundResource(R.drawable.pika_take);
            }
            else if(sharedPreferences.getBoolean("isSecondOn", true)){
                img.setBackgroundResource(R.drawable.kong_take);
            }

            mParams.x = (int)(xInScreen -2* imgWidth/3);
            mParams.y = (int)yInScreen;
            windowManager.updateViewLayout(this, mParams);

        } else if (!isPressed) {
            if(sharedPreferences.getBoolean("isFirstOn", true)){
                img.setBackgroundResource(R.drawable.pika_window);
            }
            else if(sharedPreferences.getBoolean("isSecondOn", true)){
                img.setBackgroundResource(R.drawable.kong_window);
            }

        }
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - imgWidth/3*2);
        mParams.y = (int) (yInScreen );
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 打开大悬浮窗，同时关闭小悬浮窗。
     */
    private void openBigWindow() {
        MyWindowManager.createBigWindow(getContext());
        MyWindowManager.removeSmallWindow(getContext());
    }

    private void startAnimation(){
        float startY = yInScreen;
        float endY = yInScreen +200f;
        if(yInScreen+200.0+imgHeigth>screenHeight){
            endY = screenHeight - imgHeigth;
        }

        final float startX = mParams.x;
        final float endX ;

        //判断下落位置
        if(xInScreen > screenWidth/2) {
            endX = screenWidth - imgWidth;
        }
        else{
            endX = 0 ;
        }


        anim = ValueAnimator.ofObject(new animEvaluator(), startY, endY);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mParams.y = (int)(float) animation.getAnimatedValue();
                windowManager.updateViewLayout(FloatWindowSmallView.this, mParams);
            }
        });
        //当下落结束时播放帧动画
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                //帧动画播放
                if(endX == 0){
                    if(sharedPreferences.getBoolean("isFirstOn", true)){
                        img.setBackgroundResource(R.drawable.pika_walk_left);
                    }
                    else if(sharedPreferences.getBoolean("isSecondOn", true)){
                        img.setBackgroundResource(R.drawable.kong_walk_left);
                    }
                }

                else{
                    if(sharedPreferences.getBoolean("isFirstOn", true)){
                        img.setBackgroundResource(R.drawable.pika_walk_right);
                    }
                    else if(sharedPreferences.getBoolean("isSecondOn", true)){
                        img.setBackgroundResource(R.drawable.kong_walk_right);
                    }
                }

                runFrame = (AnimationDrawable) img.getBackground();
                runFrame.start();

                //属性动画控制位移
                runAnim = ValueAnimator.ofObject(new animEvaluator(), startX, endX);
                runAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mParams.x = (int) (float) animation.getAnimatedValue();

                        windowManager.updateViewLayout(FloatWindowSmallView.this, mParams);
                    }
                });
                runAnim.setDuration(1000);
                runAnim.start();
                runAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        runFrame.stop();
                        if(sharedPreferences.getBoolean("isFirstOn", true)){
                            img.setBackgroundResource(R.drawable.pika_window);
                        }
                        else if(sharedPreferences.getBoolean("isSecondOn", true)){
                            img.setBackgroundResource(R.drawable.kong_window);
                        }

                    }
                });
            }
        });
        anim.setInterpolator(new AccelerateInterpolator(4f));
        anim.setDuration(500);
        anim.start();
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    /**
     * 监听微信消息
     */
    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            title = intent.getStringExtra("title");
            text = intent.getStringExtra("text");
            Log.d("title", title);
            Log.d("text",text);
            if(anim!=null){
                anim.cancel();
                runAnim.cancel();
                runFrame.stop();
            }
            if(mParams.x>screenWidth/2){
                MyWindowManager.createMSGWindow(context, "  "+text, screenWidth, mParams.y,isWeChat, false);
            }
            else {
                MyWindowManager.createMSGWindow(context, "  "+text, 0, mParams.y,isWeChat, isLeft);
            }

            System.out.println(mParams.y);
            MyWindowManager.removeSmallWindow(context);
            MyWindowManager.removeBigWindow(context);
            new Thread(new MyThread()).start();
        }
    };

    private Handler msghandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == 0x123){
                MyWindowManager.removeMSGWindow(getContext());
            }
        }
    };

    public class MyThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                Thread.sleep(2000);
                Message message = new Message();
                message.what = 0x123;
                msghandler.sendMessage(message);// 发送消息
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 监听闹钟消息
     */
    private BroadcastReceiver onAlarm= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            task = intent.getStringExtra("task");
            date = intent.getStringExtra("date");
            time = intent.getStringExtra("time");
            addition = intent.getStringExtra("addition");
            Log.d("task", task);
            Log.d("time",time);
            if(anim!=null){
                anim.cancel();
                runAnim.cancel();
                runFrame.stop();
            }
            if(mParams.x>screenWidth/2){
                MyWindowManager.createMSGWindow(context, date+" "+time+'\n'+task+'\n'+addition, screenWidth, mParams.y,isAlarm, false);
            }
            else {
                MyWindowManager.createMSGWindow(context, date+" "+time+'\n'+task+'\n'+addition, 0, mParams.y,isAlarm, isLeft);
            }

            System.out.println(mParams.y);
            MyWindowManager.removeSmallWindow(context);
            MyWindowManager.removeBigWindow(context);
            new Thread(new MyThread()).start();
        }
    };
}