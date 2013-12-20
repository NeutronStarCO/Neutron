package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangeName extends Activity {
	
	EditText etName;
	Button btnSave;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_name);
		etName = (EditText)findViewById(R.id.et_changed_name);
		btnSave = (Button)findViewById(R.id.btn_change_name_save);
		Bundle bundle = new Bundle();
		String name = bundle.getString("name");
		etName.setText(name);
		etName.addTextChangedListener(new TextWatcher(){
			
			@Override
			public void onTextChanged(CharSequence s,int start, int before,
					int count){
				btnSave.setClickable(true);
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void save(View v) {
		Intent intent = new Intent();
		intent.putExtra("name", etName.getText().toString());
		ChangeName.this.setResult(-1,intent);
		this.finish();
	}

	public void cancel_back(View v) { // 标题栏 返回按钮
		Log.i("cancel", "cancel");
		this.finish();
	}
}
