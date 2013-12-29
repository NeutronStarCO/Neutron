package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangeName extends Activity {
	
	EditText etName;
	Button btnSave;
	Button btnCancel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_name);
		etName = (EditText)findViewById(R.id.et_changed_name);
		btnSave = (Button)findViewById(R.id.btn_change_name_save);
		btnCancel = (Button)findViewById(R.id.btn_cancel_change_name);
		
		Bundle bundle = this.getIntent().getExtras();
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK) { //���/����/���η��ؼ�
	    	save(getRootView(this));
	    } else if(keyCode == KeyEvent.KEYCODE_MENU) {
	        //���/���ز˵���
	    } else if(keyCode == KeyEvent.KEYCODE_HOME) {
	        //����Home��Ϊϵͳ�����˴����ܲ�����Ҫ��дonAttachedToWindow()
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
		bl.putString("name", etName.getText().toString());
		intent.putExtras(bl);
		ChangeName.this.setResult(RESULT_OK,intent);
		this.finish();
	}

	public void cancel_back(View v) { // ������ ���ذ�ť
		// Ŀǰbtn_Cancel��ť��ӵķ���Ҳ��save������cancel_back����Ϊ��ʹ��cancelҲ��Ҫ���ش�һ��ֵ
		this.finish();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
}
