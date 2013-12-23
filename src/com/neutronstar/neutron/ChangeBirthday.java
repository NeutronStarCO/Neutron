package com.neutronstar.neutron;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;

public class ChangeBirthday extends Activity{

	private EditText etBirthday;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_birthday);
		etBirthday = (EditText)findViewById(R.id.etBirthday);
		DatePicker dp = (DatePicker)findViewById(R.id.dpBirthday);
		
		Bundle bundle = this.getIntent().getExtras();
		String birthday = bundle.getString("birthday");

		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		if(birthday == "")
		{
			etBirthday.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
		}
		else
		{
			etBirthday.setText(birthday);
		}
		
		dp.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				etBirthday.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
			}
			
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
	    	save(getRootView(this));
	    } else if(keyCode == KeyEvent.KEYCODE_MENU) {
	        //监控/拦截菜单键
	    } else if(keyCode == KeyEvent.KEYCODE_HOME) {
	        //由于Home键为系统键，此处不能捕获，需要重写onAttachedToWindow()
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	private static View getRootView(Activity context)
	{
		return ((ViewGroup)context.findViewById(android.R.id.content)).getChildAt(0);
	}
	
	public void save(View v) {
		Intent intent = new Intent();
		Bundle bl = new Bundle();
		bl.putInt("usage", MainTabFamily.TAG_QUERY);
		bl.putString("birthday", etBirthday.getText().toString());
		intent.putExtras(bl);
		ChangeBirthday.this.setResult(RESULT_OK,intent);
		this.finish();
	}

	public void cancel_back(View v) { // 标题栏 返回按钮
		this.finish();
	}

}
