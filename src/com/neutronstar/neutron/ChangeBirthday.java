package com.neutronstar.neutron;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
		
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		etBirthday.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
		
		dp.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				etBirthday.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
			}
			
		});
	}
	
	public void save(View v) {
		Intent intent = new Intent();
		intent.putExtra("birthday", etBirthday.getText().toString());
		ChangeBirthday.this.setResult(-1,intent);
		this.finish();
	}

	public void cancel_back(View v) { // 标题栏 返回按钮
		this.finish();
	}

}
