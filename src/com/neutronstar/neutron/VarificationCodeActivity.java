package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class VarificationCodeActivity extends Activity {
	public static final int TAG_LOGIN = 1;
	public static final int TAG_SIGN_IN = 2;
	private Intent intent;
	private Bundle bl;
	private int tag;
	private EditText etVarificationCode;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_varification_code);
		intent = this.getIntent();
		bl = intent.getExtras();
		tag = bl.getInt("tag");
		etVarificationCode = (EditText) findViewById(R.id.varification_code_code);
	}
	
	public void back(View view)
	{
		this.finish();
	}
	
	public void next(View view)
	{
//		if(etVarificationCode.length() == 6)
		{
			Intent intent = new Intent();
			switch(tag)
			{
			case TAG_LOGIN:		
				intent.setClass(VarificationCodeActivity.this, Login.class);
				startActivity(intent);
				break;
			case TAG_SIGN_IN:
				intent.setClass(VarificationCodeActivity.this, SignUpInfoActivity.class);
				startActivity(intent);
				break;
			}
			this.finish();
		}
//		else
		{
			Toast toast = Toast.makeText(this, "验证码应为6位数字.", Toast.LENGTH_LONG );
			toast.show();
		}		
	}
}
