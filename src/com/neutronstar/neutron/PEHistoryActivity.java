package com.neutronstar.neutron;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.neutronstar.neutron.NeutronContract.ITEM;
import com.neutronstar.neutron.NeutronContract.NeutronGroupTesting;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.model.HistoryEntity;
import com.neutronstar.neutron.model.HistoryEntityAdapter;

public class PEHistoryActivity extends Activity {
	
	private NeutronDbHelper ndb;
	private Bundle bl;
	private Intent intent;
	private int item;
	private int userid;
	private TextView title;
	private Button mBtnBack;
	private ListView mListView;
	private HistoryEntityAdapter hAdapter;
	private List<HistoryEntity> hDataArrays = new ArrayList<HistoryEntity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phical_exam_history);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		ndb = NeutronDbHelper.GetInstance(this);
		title		= (TextView)findViewById(R.id.peh_title);
		mListView = (ListView) findViewById(R.id.listview);
		mBtnBack = (Button) findViewById(R.id.btn_back);
//		mBtnBack.setOnClickListener(this);
		
		intent=this.getIntent();
		//获取Intent中的Bundle数据
		bl=intent.getExtras();
		item = bl.getInt("item");
		userid = bl.getInt("userid");
		String[] titleList = getResources().getStringArray(R.array.peh_history_title);
		switch(item)
		{
		case ITEM.g_bloodTest:
			title.setText(titleList[0]);					
			break;
		case ITEM.g_urineTest:
			title.setText(titleList[1]);		
			break;
		case ITEM.g_bloodPressure:
			title.setText(titleList[2]);
			break;
		}
		
		SQLiteDatabase db = ndb.getReadableDatabase();
		
		// 从数据库得到历次检测记录
		String[] projection = {
				"rowid",
				NeutronGroupTesting.COLUMN_NAME_DATETIME,
				NeutronGroupTesting.COLUMN_NAME_TESTINGINST
			    };
		String selection = "" + NeutronGroupTesting.COLUMN_NAME_GROUP + "=" + item 
				+" AND " + NeutronGroupTesting.COLUMN_NAME_TAG + "=" + TAG.normal;
		String sortOrder =
				"datetime(" + NeutronGroupTesting.COLUMN_NAME_DATETIME + ") DESC";
		Cursor cur = db.query(
				NeutronGroupTesting.TABLE_NAME,  // The table to query
			    projection,                // The columns to return
			    selection,                 // The columns for the WHERE clause selection
			    null,                      // The values for the WHERE clause selectionArgs
			    null,                      // don't group the rows
			    null,                      // don't filter by row groups
			    sortOrder                // The sort order
			    );
		int rowid = 0;
		String timestamp = "";
		String testingInstitution = "";
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					rowid = cur.getInt(cur.getColumnIndex("rowid"));
					timestamp = cur.getString(cur.getColumnIndex(NeutronGroupTesting.COLUMN_NAME_DATETIME));
					testingInstitution = cur.getString(cur.getColumnIndex(NeutronGroupTesting.COLUMN_NAME_TESTINGINST));
					HistoryEntity entity = new HistoryEntity();
					entity.setRowid(rowid);
					entity.setDate(timestamp);
					entity.setTestingInstitution(testingInstitution);
					hDataArrays.add(entity);
				} while (cur.moveToNext());
			}
		}

		hAdapter = new HistoryEntityAdapter(this, hDataArrays);
		mListView.setAdapter(hAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> parentView, View view, int position,
					long rowCount) {
				Intent intent = new Intent(PEHistoryActivity.this, GroupTestingActivity.class);
				Bundle bl = new Bundle();
				bl.putInt("userid", userid);
				bl.putInt("item", item);
				bl.putInt("rowid", hDataArrays.get(position).getRowid());
				bl.putString("datetime", hDataArrays.get(position).getDate());
				bl.putString("testingInstitution", hDataArrays.get(position).getTestingInstitution());
				intent.putExtras(bl);
				startActivity(intent);
				
			}
			
		});
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parentView, View view,
					int position, long rowCount) {
				Intent intent = new Intent(PEHistoryActivity.this, ModifyPEHistoryActivity.class);
				Bundle bl = new Bundle();
				bl.putInt("position",position);
				bl.putInt("userid", userid);
				bl.putInt("item", item);
				bl.putInt("rowid", hDataArrays.get(position).getRowid());
				bl.putString("datetime", hDataArrays.get(position).getDate());
				bl.putString("testingInstitution", hDataArrays.get(position).getTestingInstitution());
				intent.putExtras(bl);
				startActivityForResult(intent, 2);
				return true;
			}
			
		});
		
		
	} 
	
	public void cancel_back(View v) { // 标题栏 返回按钮
		this.finish();
	}
	
	public void btn_add(View v) { // 标题栏 返回按钮
		Intent intent = new Intent(PEHistoryActivity.this, AddChooseActivity.class);
		Bundle bl = new Bundle();
		bl.putInt("userid", userid);
		bl.putInt("item", item);
//		bl.putString("title", title.getText().toString());
		intent.putExtras(bl);
		startActivityForResult(intent,1);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode != 0)
		{
			String value;
			String timestamp;
			int position;
			Bundle bl= data.getExtras();
			switch(requestCode)
			{
			case 1:
				bl= data.getExtras();
				long rowid = bl.getLong("rowid");
				value=bl.getString("value");
				timestamp = bl.getString("datetime");
				HistoryEntity entity = new HistoryEntity();
				entity.setRowid((int)rowid);
				entity.setDate(timestamp);
				entity.setTestingInstitution(value);
				hDataArrays.add(entity);
				hAdapter = new HistoryEntityAdapter(this, hDataArrays);
				mListView.setAdapter(hAdapter);
				break;
			case 2:
				switch (resultCode)
				{
				case 1:
					break;
				case 2:
					value =bl.getString("value");
					timestamp = bl.getString("datetime");
					position = bl.getInt("position");
					hDataArrays.get(position).setTestingInstitution(value);
					hDataArrays.get(position).setDate(timestamp);
					hAdapter = new HistoryEntityAdapter(this, hDataArrays);
					mListView.setAdapter(hAdapter);
					break;
				case 3:
					position = bl.getInt("position");
					hDataArrays.remove(position);
					hAdapter = new HistoryEntityAdapter(this, hDataArrays);
					mListView.setAdapter(hAdapter);
					break;
				}
				break;
			}
		}
		
	}

}
