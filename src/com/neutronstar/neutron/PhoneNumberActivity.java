package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneNumberActivity extends Activity {
	public static final int TAG_LOGIN = 1;
	public static final int TAG_SIGN_IN = 2;
	private final int requestCode = 1;
	private int tag;
	private TextView tvPhoneNumber;
	private TextView tvAreaCode;
	private CheckBox cbTerms;
	private TextView tvOthers;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_number);
		Intent intent = this.getIntent();
		Bundle bl = intent.getExtras();
		tag = bl.getInt("tag");
		tvPhoneNumber = (TextView) findViewById(R.id.phone_number_number);
		tvPhoneNumber.requestFocus();
		tvAreaCode = (TextView) findViewById(R.id.phone_number_areacode);
		tvOthers = (TextView) findViewById(R.id.phone_number_others);
		cbTerms = (CheckBox) findViewById(R.id.phone_number_terms);
		switch(tag)
		{
		case TAG_LOGIN:
			tvOthers.setVisibility(View.GONE);
			break;
		case TAG_SIGN_IN:
			break;
		}	
	}
	
	public void back(View view)
	{
		this.finish();
	}

	public void next(View view)
	{
		if(!cbTerms.isChecked())
		{
			Toast toast = Toast.makeText(this, "您还没有同意用户协议", Toast.LENGTH_LONG );
			toast.show();
		}
//		else if(tvPhoneNumber.getText().length() <= 8)
//		{
//			Toast toast = Toast.makeText(this, "你的手机号码少于8位", Toast.LENGTH_LONG );
//			toast.show();
//		}
		else
		{
			Intent intent = new Intent(PhoneNumberActivity.this, ConfirmationDialogActivity.class);
			Bundle bl = new Bundle();
			bl.putInt("tag", ConfirmationDialogActivity.TAG_PHONE_NUMBER);
			bl.putString("areacode", tvAreaCode.getText().toString());
			bl.putString("phonenumber", tvPhoneNumber.getText().toString());
			intent.putExtras(bl);
			startActivityForResult(intent, requestCode);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{	
		switch(resultCode){
		case RESULT_OK:
			Bundle bl = data.getExtras();
			boolean confirmation = bl.getBoolean("confirmation");
			if(confirmation)
			{
				Log.d("return", String.valueOf(confirmation));
				Intent intent = new Intent();
				Bundle bundle  = new Bundle();
				switch(tag)
				{
				case TAG_LOGIN:
					intent = new Intent(PhoneNumberActivity.this, VarificationCodeActivity.class);
					bundle.putInt("tag", VarificationCodeActivity.TAG_LOGIN);
					bundle.putString("areacode", tvAreaCode.getText().toString());
					bundle.putString("phonenumber", tvPhoneNumber.getText().toString());
					intent.putExtras(bundle);
					startActivity(intent);
					break;
				case TAG_SIGN_IN:
					intent = new Intent(PhoneNumberActivity.this, VarificationCodeActivity.class);
					bundle.putInt("tag", VarificationCodeActivity.TAG_SIGN_IN);
					bundle.putString("areacode", tvAreaCode.getText().toString());
					bundle.putString("phonenumber", tvPhoneNumber.getText().toString());
					intent.putExtras(bundle);
					startActivity(intent);
					break;
				}
			}
			
			break;
		}
	}
}
