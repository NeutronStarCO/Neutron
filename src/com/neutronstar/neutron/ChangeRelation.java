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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ChangeRelation extends Activity{

	private Spinner spRelation;
	private TextView tvRelation;
	private ArrayAdapter adapter;
	
//	private String[] strRelation = getResources().getStringArray(R.array.relations);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_relation);
		
		tvRelation = (TextView)findViewById(R.id.tvRelation);
		spRelation = (Spinner)findViewById(R.id.spRelation);
		
		adapter = ArrayAdapter.createFromResource(this, R.array.relations, android.R.layout.simple_spinner_item);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spRelation.setAdapter(adapter);
		
		Log.i("relation","setAdapter");
		
		spRelation.setOnItemSelectedListener(new SpinnerXMLSelectedListener());
		
		spRelation.setVisibility(View.VISIBLE);
	}
	
	class SpinnerXMLSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			tvRelation.setText(adapter.getItem(arg2).toString());			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
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
		bl.putString("relation", tvRelation.getText().toString());
		intent.putExtras(bl);
		ChangeRelation.this.setResult(RESULT_OK,intent);
		this.finish();
	}

	public void cancel_back(View v) { // 标题栏 返回按钮
		Log.i("cancel", "cancel");
		this.finish();
	}

}
