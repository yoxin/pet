package com.example.administrator.pet;
import java.util.Calendar;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.demo.floatwindowdemo.R;

public class AlarmActivity extends Activity implements OnClickListener{

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	AlarmManager am;
	private Button bt_back,bt_save,bt_time,bt_date;
	private EditText et_task,et_addition;
	private TextView tv_date,tv_time;
	private Calendar c;
	private MyDatabaseHelper dbhelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.alarmset);

		init();
		setListener();


	}

	private void init(){
		//创建数据库打开帮助类对象
		dbhelper=new MyDatabaseHelper(AlarmActivity.this, "alarm_task_manager1",null, 1);

		c=Calendar.getInstance();

		System.out.println(c.get(Calendar.HOUR_OF_DAY));
		System.out.println(c.get(Calendar.MINUTE));
		tv_date = (TextView)findViewById(R.id.alarm_et_date);
		tv_time = (TextView)findViewById(R.id.alarm_et_time);
		bt_back=(Button)findViewById(R.id.alarmset_bt_back);
		bt_save=(Button)findViewById(R.id.alarmset_bt_save);
		bt_time = (Button)findViewById(R.id.alarm_bt_time);
		bt_date = (Button)findViewById(R.id.alarm_bt_date);
		et_task=(EditText)findViewById(R.id.alarmset_et_task);
		et_addition=(EditText)findViewById(R.id.alarmset_et_remark);
		tv_date.setText(c.get(Calendar.YEAR)+"年"+(c.get(Calendar.MONTH)+1)+"月"+c.get(Calendar.DAY_OF_MONTH)+"日");
	}

	private void setListener(){
		bt_back.setOnClickListener(this);
		bt_save.setOnClickListener(this);
		bt_time.setOnClickListener(this);
		bt_date.setOnClickListener(this);
	}
	@Override
	public void onClick(View button) {
		// TODO Auto-generated method stub
		switch(button.getId()){
			case R.id.alarm_bt_time:
				new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						c.setTimeInMillis(System.currentTimeMillis());
						c.set(Calendar.HOUR_OF_DAY, hourOfDay);
						c.set(Calendar.MINUTE, minute);
						c.set(Calendar.SECOND, 0);
						c.set(Calendar.MILLISECOND, 0);
						tv_time.setText(hourOfDay + ": " + minute);

					}
				},c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
				break;
			case R.id.alarm_bt_date:
				new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
				{
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
										  int dayOfMonth)
					{
						c.set(Calendar.YEAR , year);
						c.set(Calendar.MONTH , monthOfYear);
						c.set(Calendar.DAY_OF_MONTH ,dayOfMonth);
						tv_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日");
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
				break;
			case R.id.alarmset_bt_back:
				//返回上一个Activity的逻辑代码
				AlarmActivity.this.finish();
				break;
			case R.id.alarmset_bt_save:
			/*闹钟功能实现
			 *1.AlarmManager注册开启闹钟，发送广播
			 *2.保存闹钟事件至数据库
			 */
				if(tv_time.getText().toString().equals("")){
					Toast.makeText(AlarmActivity.this, "请设定时间", Toast.LENGTH_SHORT).show();
				}
				else{
					preferences=this.getSharedPreferences("Counter", MODE_PRIVATE);
					editor=preferences.edit();
					int count=preferences.getInt("count", 1);
					String task=et_task.getText().toString();
					String addition=et_addition.getText().toString();
					String date=c.get(Calendar.YEAR)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.DAY_OF_MONTH);
					String time=c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
					Bundle bundle=new Bundle();
					//封装要传送的数据
					bundle.putString("task", task);
					bundle.putString("addition", addition);
					bundle.putString("date", date);
					bundle.putString("time", time);
					Intent intent=new Intent(AlarmActivity.this,AlarmReceiver.class);
					intent.putExtras(bundle);
					//创建PendingIntent对象
					PendingIntent pi=PendingIntent.getBroadcast(this, count, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					//获取系统闹钟服务
					am=(AlarmManager)getSystemService(ALARM_SERVICE);
					//开启闹钟
					System.out.println(c.getTimeInMillis());
					am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
					c.setTimeInMillis(System.currentTimeMillis());
					System.out.println(c.getTimeInMillis());
					//count自增，存入数据，提交修改
					count++;
					editor.putInt("count", count);
					editor.commit();
					//把数据插入数据库
					dbhelper.getReadableDatabase().execSQL("insert into alarm_task values(null,?,?,?,?,"+count+")", new String[]{task,addition,date,time});
					Toast.makeText(AlarmActivity.this, "闹钟设置成功！", Toast.LENGTH_SHORT).show();
					this.finish();
				}

				break;
			default:break;

		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		//退出程序时关闭SQLiteDatabase
		if(dbhelper !=null){
			dbhelper.close();
		}
	}
}