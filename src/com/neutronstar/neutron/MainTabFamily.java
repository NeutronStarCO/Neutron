package com.neutronstar.neutron;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.neutronstar.neutron.NeutronContract.NeutronGroupTesting;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.model.FamilyMemberEntity;
import com.neutronstar.neutron.model.FamilyMemberEntityAdapter;
import com.neutronstar.neutron.model.HistoryEntity;
import com.neutronstar.neutron.model.HistoryEntityAdapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;

public class MainTabFamily extends Activity implements OnTabActivityResultListener {
	public static MainTabFamily instance = null;
	private NeutronDbHelper ndb;
	private ListView fmListView;
	private FamilyMemberEntityAdapter fmAdapter;
	private List<FamilyMemberEntity> fmDataArrays = new ArrayList<FamilyMemberEntity>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_family);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		instance = this;
		fmListView = (ListView) findViewById(R.id.maintab_family_listview);
		
		ndb = NeutronDbHelper.GetInstance(this);
		SQLiteDatabase db = ndb.getReadableDatabase();
		String[] projection = {
				NeutronUser.COLUMN_NAME_ID,
				NeutronUser.COLUMN_NAME_NAME,
				NeutronUser.COLUMN_NAME_GENDER,
				NeutronUser.COLUMN_NAME_BIRTHDAY,
				NeutronUser.COLUMN_NAME_RELATION,
				NeutronUser.COLUMN_NAME_AVATAR,
				NeutronUser.COLUMN_NAME_TYPE
			    };
		String selection = NeutronUser.COLUMN_NAME_TAG + "=?";
		String[] whereValue = {String.valueOf(TAG.normal)}; 
		Cursor cur = db.query(
				NeutronUser.TABLE_NAME,  		// The table to query
			    projection,                				// The columns to return
			    selection,                 				// The columns for the WHERE clause selection
			    whereValue,                      				// The values for the WHERE clause selectionArgs
			    null,                      				// Group the rows
			    null,                      				// Filter by row groups
			    null                				// The sort order
			    );

		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					FamilyMemberEntity entity = new FamilyMemberEntity();
					byte[] in = cur.getBlob(cur.getColumnIndex(NeutronUser.COLUMN_NAME_AVATAR));
					Bitmap bmpout = BitmapFactory.decodeByteArray(in, 0, in.length); 
					entity.setAvatar(bmpout);
					entity.setId(cur.getInt(cur.getColumnIndex(NeutronUser.COLUMN_NAME_ID)));
					entity.setName(cur.getString(cur.getColumnIndex(NeutronUser.COLUMN_NAME_NAME)));
					entity.setGender(cur.getString(cur.getColumnIndex(NeutronUser.COLUMN_NAME_GENDER)));					
					try {
						entity.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(cur.getString(cur.getColumnIndex(NeutronUser.COLUMN_NAME_BIRTHDAY))));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					entity.setRelation(cur.getInt(cur.getColumnIndex(NeutronUser.COLUMN_NAME_RELATION)));
					entity.setType(cur.getInt(cur.getColumnIndex(NeutronUser.COLUMN_NAME_TYPE)));

					fmDataArrays.add(entity);
				} while (cur.moveToNext());
			}
		}
		fmAdapter = new FamilyMemberEntityAdapter(this, fmDataArrays);
		fmListView.setAdapter(fmAdapter);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		
	}

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) 
	{
		onActivityResult(requestCode, resultCode, data);
	}

}