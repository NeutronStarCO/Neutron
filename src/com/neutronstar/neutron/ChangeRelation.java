package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
	
	public void save(View v) {
		Intent intent = new Intent();
		intent.putExtra("relation", tvRelation.getText().toString());
		ChangeRelation.this.setResult(-1,intent);
		this.finish();
	}

	public void cancel_back(View v) { // 标题栏 返回按钮
		Log.i("cancel", "cancel");
		this.finish();
	}

}
