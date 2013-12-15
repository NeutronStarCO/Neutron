package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class PhoneNumberActivity extends Activity {
	private final int requestCode = 1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_number);
	}

	public void next(View view)
	{
		Intent intent = new Intent(PhoneNumberActivity.this, ConfirmationDialogActivity.class);
		Bundle bl = new Bundle();
		intent.putExtras(bl);
		startActivityForResult(intent, requestCode);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{	
		switch(resultCode){
		case RESULT_OK:
			Bundle bl = data.getExtras();
			boolean confirmation = bl.getBoolean("confirmation");
			Log.d("return", String.valueOf(confirmation));
			break;
		}
	}
}
