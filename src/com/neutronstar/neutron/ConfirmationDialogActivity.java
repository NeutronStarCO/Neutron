package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ConfirmationDialogActivity extends Activity {
	private Intent intent;
	private Bundle bl;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmation_dialog);
		intent = this.getIntent();
		bl = intent.getExtras();
	}
	
	public void confirm(View view)
	{
		bl.putBoolean("confirmation", true);
		intent.putExtras(bl);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}
}
