package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class changename extends Activity {
	
	EditText etName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_name);
		etName = (EditText)findViewById(R.id.et_changed_name);
	}
	
	public void save(View v) {
		Intent intent = new Intent();
		intent.putExtra("name", etName.getText().toString());
		changename.this.setResult(-1,intent);
		this.finish();
	}

	public void cancel_back(View v) { // 标题栏 返回按钮
		this.finish();
	}
}
