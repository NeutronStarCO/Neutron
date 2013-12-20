package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class ChangeGender extends Activity{
	TextView tvGender;
	RadioGroup rgGender;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_gender);
		
		tvGender = (TextView)findViewById(R.id.tvGender);
		rgGender = (RadioGroup)findViewById(R.id.rgGender);
		
		rgGender.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				int radioBtnId = arg0.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton)findViewById(radioBtnId);
				tvGender.setText(rb.getText());
			}
			
		});	
	}
	
	public void save(View v) {
		Intent intent = new Intent();
		intent.putExtra("gender", tvGender.getText().toString());
		ChangeGender.this.setResult(-1,intent);
		this.finish();
	}
	
	public void cancel_back(View v) { // 标题栏 返回按钮
		this.finish();
	}
	

}
