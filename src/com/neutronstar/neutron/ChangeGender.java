package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
		
		Bundle bundle = this.getIntent().getExtras();
		String gender = bundle.getString("gender");
//		tvGender.setText(gender);
		
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
		bl.putString("gender", tvGender.getText().toString());
		intent.putExtras(bl);
		ChangeGender.this.setResult(RESULT_OK,intent);
		this.finish();
	}
	
	public void cancel_back(View v) { // 标题栏 返回按钮
		this.finish();
	}
	

}
