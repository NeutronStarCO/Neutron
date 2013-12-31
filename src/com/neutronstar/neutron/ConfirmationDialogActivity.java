package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ConfirmationDialogActivity extends Activity {
	public static final int TAG_PHONE_NUMBER = 1;
	public static final int TAG_CONNECT_FAILED = 2;
	private Intent intent;
	private Bundle bl;
	private int tag;
	private TextView tvTitle;
	private TextView tvMessage;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmation_dialog);
		intent = this.getIntent();
		bl = intent.getExtras();
		tag = bl.getInt("tag");
		tvTitle = (TextView) findViewById(R.id.confirmation_dialog_title);
		tvMessage = (TextView) findViewById(R.id.confirmation_dialog_message);
		String[] strs;
		switch(tag){
		case TAG_PHONE_NUMBER:
			strs = getResources().getStringArray(R.array.confirmation_dialog_phone_number);
			String phonenumber = bl.getString("phonenumber");
			String areacode = bl.getString("IDD");
			tvTitle.setText(strs[0]);
			tvMessage.setText(strs[1] + areacode + " " + phonenumber);
			break;
		case TAG_CONNECT_FAILED:
			strs = getResources().getStringArray(R.array.confirmation_dialog_connect_failed);
			tvTitle.setText(strs[0]);
			tvMessage.setText(strs[1]);
			break;
		}		
	}
	
	public void confirm(View view)
	{
		bl.putBoolean("confirmation", true);
		intent.putExtras(bl);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}
	
	public void cancel(View view)
	{
		bl.putBoolean("confirmation", false);
		intent.putExtras(bl);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}
}
