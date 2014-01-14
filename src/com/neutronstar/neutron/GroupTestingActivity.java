package com.neutronstar.neutron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.neutronstar.neutron.NeutronContract.ITEM;
import com.neutronstar.neutron.NeutronContract.NeutronRecord;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.model.GroupTestingEntity;
import com.neutronstar.neutron.model.GroupTestingEntityAdapter;
import com.neutronstar.neutron.model.HistoryEntityAdapter;


public class GroupTestingActivity extends Activity {
	
	private NeutronDbHelper ndb;
	private Bundle bl;
	private Intent intent;
	private int item;
	private int userid;
	private int grouptestid;
	private String datetime;
	private TextView title;
	private ListView mListView;
	private GroupTestingEntityAdapter gtAdapter;
	private List<GroupTestingEntity> gtDataArrays = new ArrayList<GroupTestingEntity>();
	
	private String[] nameArray = new String[] { "白细胞数 / WBC（4.0-10.0）"};
	private double[] valueArray = new double[] {6.79};
	private String[] dimensionArray = new String[] { "K /ul"};
	
	private String[] nameList; 
	private String[] dimensionList;
	private SparseIntArray Pos2Item;
	private HashMap<Integer, Double> item2Value;
	

	private final static int COUNT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_testing);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		ndb = NeutronDbHelper.GetInstance(this);
		title		= (TextView)findViewById(R.id.group_testing_listview_title);
		mListView = (ListView)findViewById(R.id.group_testing_listview);
		intent=this.getIntent();
		bl=intent.getExtras();
		item = bl.getInt("item");
		userid = bl.getInt("userid");
		grouptestid = bl.getInt("rowid");
		datetime = bl.getString("datetime");
		Pos2Item = new SparseIntArray();
		String[] titleList = getResources().getStringArray(R.array.group_testing_title);
		SQLiteDatabase db = ndb.getReadableDatabase();
		item2Value = new HashMap<Integer, Double>();
		String[] projection = {
				NeutronRecord.COLUMN_NAME_ITEM,
				NeutronRecord.COLUMN_NAME_VALUE
			    };
		String selection = "" + NeutronRecord.COLUMN_NAME_USERID + "=" + userid + 
				" AND " + NeutronRecord.COLUMN_NAME_GROUPTESTID + "=" + grouptestid +
				" AND " + NeutronRecord.COLUMN_NAME_TAG + "=" + TAG.normal;
		Cursor cur = db.query(
				NeutronRecord.TABLE_NAME,  // The table to query
			    projection,                // The columns to return
			    selection,                 // The columns for the WHERE clause selection
			    null,                      // The values for the WHERE clause selectionArgs
			    null,                      // don't group the rows
			    null,                      // don't filter by row groups
			    null                // The sort order
			    );
		int testItem = 0;
		double value = 0;
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					testItem = cur.getInt(cur.getColumnIndex(NeutronRecord.COLUMN_NAME_ITEM));
					value = cur.getDouble(cur.getColumnIndex(NeutronRecord.COLUMN_NAME_VALUE));
					item2Value.put(testItem, value);
				} while (cur.moveToNext());
			}
		}
		switch(item)
		{
		case ITEM.g_bloodTest:
			title.setText(titleList[0]);
			Pos2Item.append(0, ITEM.B_RBC);
			Pos2Item.append(1, ITEM.B_WBC);
			Pos2Item.append(2, ITEM.B_HGB);
			Pos2Item.append(2, ITEM.B_HCT);
			Pos2Item.append(2, ITEM.B_PLT);
			Pos2Item.append(2, ITEM.B_PCT);
			nameList = getResources().getStringArray(R.array.blood_testing_name_list);
			dimensionList = getResources().getStringArray(R.array.blood_testing_dimension_list);
			break;
		case ITEM.g_urineTest:
			title.setText(titleList[1]);
			Pos2Item.append(0, ITEM.U_SG);
			Pos2Item.append(1, ITEM.U_PH);	
			nameList = getResources().getStringArray(R.array.urine_testing_name_list);
			dimensionList = getResources().getStringArray(R.array.urine_testing_dimension_list);
			break;
		case ITEM.g_bloodPressure:
			title.setText(titleList[2]);
			Pos2Item.append(0, ITEM.BP_X);
			Pos2Item.append(1, ITEM.BP_Y);
			Pos2Item.append(2, ITEM.heart_rate);
			nameList = getResources().getStringArray(R.array.blood_pressure_name_list);
			dimensionList = getResources().getStringArray(R.array.blood_pressure_dimension_list);
			break;
		}
		
		for (int i = 0; i < nameList.length; i++) {
			GroupTestingEntity entity = new GroupTestingEntity();
			entity.setName(nameList[i]);
			if(null != item2Value.get(Pos2Item.get(i))) entity.setValue(item2Value.get(Pos2Item.get(i)));
			entity.setDimension(dimensionList[i]);
			gtDataArrays.add(entity);
		}

		gtAdapter = new GroupTestingEntityAdapter(this, gtDataArrays);
		mListView.setAdapter(gtAdapter);
		
/*		mListView.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> parentView, View view, int position,
					long rowCount) {
				Intent intent = new Intent(GroupTestingActivity.this, NewRecordActivity.class);
				Bundle bl = new Bundle();
				bl.putInt("userid", userid);
				bl.putInt("item", Pos2Item.get(position));
				if(gtDataArrays.get(position).hasValue())
					bl.putDouble("value", gtDataArrays.get(position).getValue());
				bl.putInt("rowid", grouptestid);
				bl.putString("datetime", datetime);
				intent.putExtras(bl);
				startActivity(intent);				
			}			
		});
*/		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parentView, View view,
					int position, long rowCount) {
				Intent intent = new Intent(GroupTestingActivity.this, ModifyRecordActivity.class);
				Bundle bl = new Bundle();
				bl.putInt("position",position);
				bl.putInt("userid", userid);
				bl.putInt("item", Pos2Item.get(position));
				bl.putDouble("value", gtDataArrays.get(position).getValue());
				bl.putBoolean("hasValue", gtDataArrays.get(position).hasValue());
				bl.putInt("rowid", grouptestid);
				bl.putString("datetime", datetime);
				intent.putExtras(bl);
				startActivityForResult(intent, 2);
				return false;
			}
			
		});

	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode != 0 && requestCode == 2)
		{	
			int position;
			Bundle bl= data.getExtras();
			switch (resultCode)
			{
			case 1:
				break;
			case 2:
				position = bl.getInt("position");
				gtDataArrays.get(position).setValue(Double.parseDouble(bl.getString("value")));
				gtAdapter = new GroupTestingEntityAdapter(this, gtDataArrays);
				mListView.setAdapter(gtAdapter);
				break;
			case 3:
				position = bl.getInt("position");
				gtDataArrays.get(position).setHasValue(false);
				gtAdapter = new GroupTestingEntityAdapter(this, gtDataArrays);
				mListView.setAdapter(gtAdapter);
				break;
			}		
		}
	}

}
