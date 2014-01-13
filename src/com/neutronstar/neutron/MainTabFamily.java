package com.neutronstar.neutron;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.neutron.server.persistence.model.T_user;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.model.FamilyMemberEntity;
import com.neutronstar.neutron.model.FamilyMemberEntityAdapter;

public class MainTabFamily extends Activity implements OnTabActivityResultListener {
	public static MainTabFamily instance = null;
	public static final int TAG_ADD = 1;
	public static final int TAG_DELETE = 2;
	public static final int TAG_QUERY = 3;
	public static final int TAG_MODIFY = 4;
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
				NeutronUser.COLUMN_NAME_IDD,
				NeutronUser.COLUMN_NAME_PHONE_NUMBER,
				NeutronUser.COLUMN_NAME_RELATION,
				NeutronUser.COLUNM_NAME_RELATION_TAG,
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
					entity.setGender(cur.getInt(cur.getColumnIndex(NeutronUser.COLUMN_NAME_GENDER)));					
					try {
						entity.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(cur.getString(cur.getColumnIndex(NeutronUser.COLUMN_NAME_BIRTHDAY))));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					entity.setIDD(cur.getString(cur.getColumnIndex(NeutronUser.COLUMN_NAME_IDD)));
					entity.setPhoneNumber(cur.getString(cur.getColumnIndex(NeutronUser.COLUMN_NAME_PHONE_NUMBER)));
					entity.setRelation(cur.getInt(cur.getColumnIndex(NeutronUser.COLUMN_NAME_RELATION)));
					entity.setRelationTag(cur.getInt(cur.getColumnIndex(NeutronUser.COLUNM_NAME_RELATION_TAG)));
					entity.setType(cur.getInt(cur.getColumnIndex(NeutronUser.COLUMN_NAME_TYPE)));

					fmDataArrays.add(entity);
				} while (cur.moveToNext());
			}
		}
		fmAdapter = new FamilyMemberEntityAdapter(this, fmDataArrays);
		fmListView.setAdapter(fmAdapter);
		
		fmListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parentView, View view,
					int position, long rowCount) {
				Intent intent =  
						new Intent(MainTabFamily.this, UserInfoActivity.class);
				Bundle bl = new Bundle();
				bl.putInt("tag", UserInfoActivity.TAG_MODIFY_USER);
				bl.putSerializable("family_member_entity", fmDataArrays.get(position));
				intent.putExtras(bl);
				getParent().startActivityForResult(intent,MainTabFamily.TAG_MODIFY);
				
			}});
		
		fmListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parentView, View view,
					int position, long rowCount) {
				Intent intent = new Intent(MainTabFamily.this, DeleteActivity.class);
				Bundle bl = new Bundle();
				bl.putInt("position",position);
				bl.putInt("id", fmDataArrays.get(position).getId());
				bl.putString("name", fmDataArrays.get(position).getName());
				intent.putExtras(bl);
				getParent().startActivityForResult(intent, MainTabFamily.TAG_DELETE);
				return false;
			}});
	}
	
	public void addFamilyMember(View view)
	{
		Intent intent = new Intent(MainTabFamily.this, UserInfoActivity.class);
		Bundle bl = new Bundle();
		bl.putInt("tag", UserInfoActivity.TAG_ADD_USER);
		bl.putInt("userid", this.getIntent().getExtras().getInt("userid"));
		Log.d("MainTabFamily--", "" + this.getIntent().getExtras().getInt("userid"));
		intent.putExtras(bl);
		getParent().startActivityForResult(intent,MainTabFamily.TAG_ADD);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{	
		Bundle bl;
		switch(resultCode){
		case RESULT_OK:
			switch(requestCode){
			case MainTabFamily.TAG_ADD:
				bl = data.getExtras();
				T_user user = (T_user)bl.getSerializable("t_user");
				FamilyMemberEntity entity = new FamilyMemberEntity();				
				byte[] in = user.gettUserPicture();
				Bitmap avatar = BitmapFactory.decodeByteArray(in, 0, in.length);
				entity.setAvatar(avatar);
				entity.setId(user.gettUserId());
				entity.setName(user.gettUserName());
				entity.setGender(user.gettUserGender());
				try {
					entity.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(user.gettUserBirth()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				entity.setRelation(bl.getInt("relation"));
				entity.setRelationTag(bl.getInt("relation_tag"));
				entity.setIDD(user.gettUserAreacode());
				entity.setPhoneNumber(user.gettUserPhonenumber());
				entity.setType(user.gettUserRegtag());
				fmDataArrays.add(entity);
				fmAdapter = new FamilyMemberEntityAdapter(this, fmDataArrays);
				fmListView.setAdapter(fmAdapter);
				break;
			case MainTabFamily.TAG_DELETE:
				bl = data.getExtras();
				int position = bl.getInt("position");
				fmDataArrays.remove(position);
				fmAdapter = new FamilyMemberEntityAdapter(this, fmDataArrays);
				fmListView.setAdapter(fmAdapter);
				break;
			}
			break;
		default:
			break;
				
		}
	}

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) 
	{
		onActivityResult(requestCode, resultCode, data);
	}
	

}
