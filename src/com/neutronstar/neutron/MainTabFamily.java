package com.neutronstar.neutron;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.neutron.server.persistence.model.T_relation;
import com.neutron.server.persistence.model.T_user;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.SERVER;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronContract.USER;
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
	private int deletePos;
	private int modifyPos;

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
				modifyPos = position;
				Intent intent =  
						new Intent(MainTabFamily.this, UserInfoActivity.class);
				Bundle bl = new Bundle();
				bl.putInt("tag", UserInfoActivity.TAG_MODIFY_USER);
				bl.putInt("userid", MainTabFamily.this.getIntent().getExtras().getInt("userid"));
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
				deletePos = bl.getInt("position");
				// 删除关系
				new DeleteRelationTask().execute(SERVER.Address + "/" + "relation"
						, String.valueOf(MainTabFamily.this.getIntent().getExtras().getInt("userid"))
						, String.valueOf(fmDataArrays.get(deletePos).getId()));
				break;
			case MainTabFamily.TAG_MODIFY:
				// 更新位置为modifyPos的entity
				bl = data.getExtras();
				FamilyMemberEntity afterModified  = (FamilyMemberEntity)bl.getSerializable("fme_after_modified");
				fmDataArrays.remove(modifyPos);
				fmDataArrays.add(modifyPos, afterModified);
				fmAdapter = new FamilyMemberEntityAdapter(this, fmDataArrays);
				fmListView.setAdapter(fmAdapter);
				break;
			}
			break;
		default:
			break;
				
		}
	}

	public void onTabActivityResult(int requestCode, int resultCode, Intent data) 
	{
		onActivityResult(requestCode, resultCode, data);
	}
	

	private class DeleteRelationTask extends AsyncTask<String, Void, String> 
	{
		String state = "";
		int result;
		T_relation tRelation;
		protected String doInBackground(String... params) {
			String strUrl = params[0];
			tRelation = new T_relation(); 
			tRelation.settRelationMasterId(Integer.valueOf(params[1]));Log.d("MasterId","" + tRelation.gettRelationMasterId());
			tRelation.settRelationSalveId(Integer.valueOf(params[2]));Log.d("SalveId","" + tRelation.gettRelationSalveId());
			try {
				URL url = new URL(strUrl);
			    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			    urlConn.setReadTimeout(10000 /* milliseconds */);
			    urlConn.setConnectTimeout(15000 /* milliseconds */);
			    urlConn.setDoInput(true);
			    urlConn.setDoOutput(true);
			    urlConn.setRequestMethod("POST");
			    urlConn.setUseCaches(false);
			    urlConn.setRequestProperty("Accept-Charset", "utf-8");  
				urlConn.setRequestProperty("Content-Type", "application/x-java-serialized-object");
				urlConn.connect();
				OutputStream outStrm = urlConn.getOutputStream();  
		        ObjectOutputStream oos = new ObjectOutputStream(outStrm);  
		        
		        ArrayList<Serializable> paraList = new ArrayList<Serializable>();
		        paraList.add("delete");
		        paraList.add(tRelation);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		  
		        ObjectInputStream ois = new ObjectInputStream(urlConn.getInputStream());  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        state = (String)paraList.get(0);
		        result = (Integer)paraList.get(1); 
		        
			} catch (Exception e) {
	               e.printStackTrace();
	           }
			return null;
		}
		
		protected void onPostExecute(String result) 
		{
			T_user user = new T_user();
			if(state.equals("delOk"))
			{
				if(fmDataArrays.get(deletePos).getType() == USER.subregister)
				{
					//  子用户则删除用户
					new DeleteUserTask().execute(SERVER.Address + "/" + "login"
							, String.valueOf(fmDataArrays.get(deletePos).getId()));
				}
				else
				{
					// 删除本地数据库用户
					SQLiteDatabase db = ndb.getWritableDatabase();
					ContentValues cv = new ContentValues();
					cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.delete);
					String whereClause = NeutronUser.COLUMN_NAME_ID +"=? AND " + NeutronUser.COLUMN_NAME_NAME + "=?";
					String[] whereArgs = { String.valueOf(fmDataArrays.get(deletePos).getId()), fmDataArrays.get(deletePos).getName() };  
					db.update(NeutronUser.TABLE_NAME, cv, whereClause, whereArgs);
					// 删除页面
					fmDataArrays.remove(deletePos);
					fmAdapter = new FamilyMemberEntityAdapter(MainTabFamily.this, fmDataArrays);
					fmListView.setAdapter(fmAdapter);
				}
			}
		}		
	}
	
	private class DeleteUserTask extends AsyncTask<String, Void, String> 
	{
		String state = "";
		int result;
		T_user tUser;
		protected String doInBackground(String... params) {
			String strUrl = params[0];
			tUser = new T_user(); 
			tUser.settUserId(Integer.valueOf(params[1]));Log.d("params[1]userid","" + params[1]);
			try {
				URL url = new URL(strUrl);
			    HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			    urlConn.setReadTimeout(10000 /* milliseconds */);
			    urlConn.setConnectTimeout(15000 /* milliseconds */);
			    urlConn.setDoInput(true);
			    urlConn.setDoOutput(true);
			    urlConn.setRequestMethod("POST");
			    urlConn.setUseCaches(false);
			    urlConn.setRequestProperty("Accept-Charset", "utf-8");  
				urlConn.setRequestProperty("Content-Type", "application/x-java-serialized-object");
				urlConn.connect();
				OutputStream outStrm = urlConn.getOutputStream();  
		        ObjectOutputStream oos = new ObjectOutputStream(outStrm);  
		        
		        ArrayList<Serializable> paraList = new ArrayList<Serializable>();
		        paraList.add("delete");
		        paraList.add(tUser);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		  
		        ObjectInputStream ois = new ObjectInputStream(urlConn.getInputStream());  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        state = (String)paraList.get(0);
		        result = (Integer)paraList.get(1); 
		        
			} catch (Exception e) {
	               e.printStackTrace();
	           }
			return null;
		}
		
		protected void onPostExecute(String result) 
		{
			if(state.equals("ok"))
			{				
				// 删除本地数据库用户
				SQLiteDatabase db = ndb.getWritableDatabase();
				ContentValues cv = new ContentValues();
				cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.delete);
				String whereClause = NeutronUser.COLUMN_NAME_ID +"=? AND " + NeutronUser.COLUMN_NAME_NAME + "=?";
				String[] whereArgs = { String.valueOf(fmDataArrays.get(deletePos).getId()), fmDataArrays.get(deletePos).getName() };  
				db.update(NeutronUser.TABLE_NAME, cv, whereClause, whereArgs);
				// 删除页面
				fmDataArrays.remove(deletePos);
				fmAdapter = new FamilyMemberEntityAdapter(MainTabFamily.this, fmDataArrays);
				fmListView.setAdapter(fmAdapter);

			}
		}
	}
}
