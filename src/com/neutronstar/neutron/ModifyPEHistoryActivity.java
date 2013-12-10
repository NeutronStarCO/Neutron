package com.neutronstar.neutron;

import com.neutronstar.neutron.NeutronContract.NeutronGroupTesting;
import com.neutronstar.neutron.NeutronContract.NeutronRecord;
import com.neutronstar.neutron.NeutronContract.TAG;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class ModifyPEHistoryActivity extends Activity {
	private NeutronDbHelper ndb;
	private int userid;
	private int item;
	private int rowid;
	private String datetime;
	private String testingInstitution;
	private TextView tvText;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_pehistory);
		ndb = NeutronDbHelper.GetInstance(this);
		Intent intent=this.getIntent();
		Bundle bl = intent.getExtras();
		position = bl.getInt("position");
		userid = bl.getInt("userid");
		item = bl.getInt("item");
		rowid = bl.getInt("rowid");
		datetime = bl.getString("datetime");
		testingInstitution = bl.getString("testingInstitution");
		tvText = (TextView)findViewById(R.id.modify_PEHistory_text);
		tvText.setText(datetime + "   " + testingInstitution);
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
	
	public void cancelButton(View v) {
		Bundle bl = new Bundle();
		Intent intent = this.getIntent();
		intent.putExtras(bl);
		ModifyPEHistoryActivity.this.setResult(1, intent);
		this.finish();
	}
	
	public void modifyButton(View v) {
		Intent intent = new Intent(ModifyPEHistoryActivity.this, NewRecordActivity.class);
		Bundle bl = new Bundle();
		bl.putInt("userid", userid);
		bl.putInt("item", item);
		bl.putInt("rowid", rowid);
		bl.putString("datetime", datetime);
		bl.putString("testingInstitution", testingInstitution);
		bl.putBoolean("modify", true);
		intent.putExtras(bl);
		startActivityForResult(intent,1);
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
			ModifyPEHistoryActivity.this.setResult(2, intent);
			ModifyPEHistoryActivity.this.finish();
			break;
		}
	}
	
	public void deleteButton(View v) {
		SQLiteDatabase db = ndb.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(NeutronGroupTesting.COLUMN_NAME_TAG, TAG.delete);
		String whereClause = "rowid=?";
		String[] whereArgs = {String.valueOf(rowid)};  
		db.update(NeutronGroupTesting.TABLE_NAME, cv, whereClause, whereArgs);
		
		cv = new ContentValues();
		cv.put(NeutronRecord.COLUMN_NAME_TAG, TAG.delete);
		whereClause = NeutronRecord.COLUMN_NAME_GROUPTESTID + "=?";
		String[] whereArgs0 = {String.valueOf(rowid)};  
		db.update(NeutronRecord.TABLE_NAME, cv, whereClause, whereArgs0);
		
		Bundle bl= new Bundle();
		bl.putInt("position", position);
		Intent intent = this.getIntent();
		intent.putExtras(bl);
		ModifyPEHistoryActivity.this.setResult(3, intent);
		ModifyPEHistoryActivity.this.finish();
	}

}
