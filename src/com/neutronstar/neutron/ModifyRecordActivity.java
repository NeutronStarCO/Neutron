package com.neutronstar.neutron;

import com.neutronstar.neutron.NeutronContract.NeutronGroupTesting;
import com.neutronstar.neutron.NeutronContract.NeutronRecord;
import com.neutronstar.neutron.NeutronContract.TAG;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ModifyRecordActivity extends Activity {
	private NeutronDbHelper ndb;
	private int userid;
	private int item;
	private int rowid;
	private String datetime;
	private boolean hasValue;
	private double value;
	private int position;
	private TextView tvText;
	private Button modifyButton;
	private Button deleteButton;
	private Intent intent;
	private Bundle bl;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_record);
		ndb = NeutronDbHelper.GetInstance(this);
		intent=this.getIntent();
		bl = intent.getExtras();
		position = bl.getInt("position");
		userid = bl.getInt("userid");
		item = bl.getInt("item");
		rowid = bl.getInt("rowid");
		datetime = bl.getString("datetime");
		hasValue = bl.getBoolean("hasValue");
		value = bl.getDouble("value");
		tvText = (TextView)findViewById(R.id.modify_record_text);
		tvText.setText(datetime + "   " + value);
		modifyButton = (Button)findViewById(R.id.modify_record_modify);
		deleteButton = (Button)findViewById(R.id.modify_record_delete);
		if(hasValue)
			modifyButton.setText("modify");
		else
		{
			modifyButton.setText("new");
			deleteButton.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
	
	public void cancelButton(View v) {
		Bundle bl = new Bundle();
		Intent intent = this.getIntent();
		intent.putExtras(bl);
		ModifyRecordActivity.this.setResult(1, intent);
		this.finish();
	}
	
	public void deleteButton(View v) {
		SQLiteDatabase db = ndb.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(NeutronRecord.COLUMN_NAME_TAG, TAG.delete);
		String whereClause = NeutronRecord.COLUMN_NAME_USERID + "=? AND "
				+ NeutronRecord.COLUMN_NAME_GROUPTESTID + "=? AND "
				+ NeutronRecord.COLUMN_NAME_ITEM + "=? AND "
				+ NeutronRecord.COLUMN_NAME_TAG + "=?";
		String[] whereArgs = {String.valueOf(userid), String.valueOf(rowid), String.valueOf(item), String.valueOf(TAG.normal)};  
		db.update(NeutronRecord.TABLE_NAME, cv, whereClause, whereArgs);
		Bundle bl= new Bundle();
		bl.putInt("position", position);
		Intent intent = this.getIntent();
		intent.putExtras(bl);
		ModifyRecordActivity.this.setResult(3, intent);
		ModifyRecordActivity.this.finish();
	}
	
	
	public void modifyButton(View v) {
//		if(!hasValue)
		{
			Intent intent = new Intent(ModifyRecordActivity.this, NewRecordActivity.class);
			bl.putBoolean("modify", true);
			intent.putExtras(bl);
			startActivityForResult(intent,1);
		}			
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (resultCode)
		{
		case RESULT_OK:
			Bundle bl= data.getExtras();
			bl.putInt("position", position);
			Intent intent = this.getIntent();
			intent.putExtras(bl);
			ModifyRecordActivity.this.setResult(2, intent);
			ModifyRecordActivity.this.finish();
			break;
		}
	}

}
