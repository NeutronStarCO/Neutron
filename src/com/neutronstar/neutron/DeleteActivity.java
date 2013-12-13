package com.neutronstar.neutron;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.TAG;

public class DeleteActivity extends Activity {
	private NeutronDbHelper ndb;
	private int position;
	private int id;
	private String name;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete);
		ndb = NeutronDbHelper.GetInstance(this);
		Intent intent=this.getIntent();
		Bundle bl = intent.getExtras();
		position = bl.getInt("position");
		id = bl.getInt("id");
		name = bl.getString("name");
	}
	
	public void cancelButton(View view)
	{
		DeleteActivity.this.finish();
	}

	public void deleteButton(View view)
	{
		SQLiteDatabase db = ndb.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.delete);
		String whereClause = NeutronUser.COLUMN_NAME_ID +"=? AND " + NeutronUser.COLUMN_NAME_NAME + "=?";
		String[] whereArgs = { String.valueOf(id), name };  
		db.update(NeutronUser.TABLE_NAME, cv, whereClause, whereArgs);
		
		Bundle bl= new Bundle();
		bl.putInt("position", position);
		Intent intent = this.getIntent();
		intent.putExtras(bl);
		DeleteActivity.this.setResult(RESULT_OK, intent);
		DeleteActivity.this.finish();
	}
}
