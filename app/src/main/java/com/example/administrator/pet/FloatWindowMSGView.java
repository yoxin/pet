package com.example.administrator.pet;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demo.floatwindowdemo.R;

public class FloatWindowMSGView extends RelativeLayout {
    /**
     * 记录大悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录大悬浮窗的高度
     */
    public static int viewHeight;

    private WindowManager windowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    private View view;

    private TextView content,title;

    private ImageView img;

    SharedPreferences sharedPreferences;




    public FloatWindowMSGView(final Context context ,String msg ,boolean isWeChat ,boolean isLeft) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        sharedPreferences = context.getSharedPreferences("pet", Context.MODE_PRIVATE);
        if(isLeft){
            LayoutInflater.from(context).inflate(R.layout.float_msg_left, this);
            img =(ImageView) findViewById(R.id.img_msg_left);
            view = findViewById(R.id.msg_layout_left);
            title = (TextView)findViewById(R.id.msg_title_left);
            content = (TextView)findViewById(R.id.msg_content_left);
            if(sharedPreferences.getBoolean("isFirstOn", true)){
                img.setBackgroundResource(R.drawable.pika_msg_left);
            }
            else if(sharedPreferences.getBoolean("isSecondOn", true)){
                img.setBackgroundResource(R.drawable.kong_msg_left);
            }
        }
        else{
            LayoutInflater.from(context).inflate(R.layout.float_msg_right, this);
            img =(ImageView) findViewById(R.id.img_msg_right);
            view = findViewById(R.id.msg_layout_right);
            title = (TextView)findViewById(R.id.msg_title_right);
            content = (TextView)findViewById(R.id.msg_content_right);
            if(sharedPreferences.getBoolean("isFirstOn", true)){
                img.setBackgroundResource(R.drawable.pika_msg_right);
            }
            else if(sharedPreferences.getBoolean("isSecondOn", true)){
                img.setBackgroundResource(R.drawable.kong_msg_right);
            }
        }



        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;


        if(isWeChat){
            title.setText("微信消息");
        }
        else{
            title.setText("闹钟提醒");
        }
        content.setText(msg);
    }


    public void setParams(WindowManager.LayoutParams params,int x , int y){
        mParams = params;
        mParams.x=x;
        mParams.y=y;
        windowManager.updateViewLayout(this, mParams);

    }


}
