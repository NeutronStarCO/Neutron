package com.neutronstar.neutron.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.neutronstar.neutron.NeutronContract.CONSTANT;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronDbHelper;

public class User {
	private NeutronDbHelper ndb;
	private int id;
	private String name;
	private Date birthday;
	private String gender;
	private boolean hasUser = false;
	private int relation;
	private int type;
	
	public User(Context context, int userid) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");		
		ndb = NeutronDbHelper.GetInstance(context);
		id = userid;
		SQLiteDatabase db = ndb.getReadableDatabase();
		
		//// 从数据库得到用户的基础数据
		String[] projection = {
			    NeutronUser.COLUMN_NAME_ID,
			    NeutronUser.COLUMN_NAME_NAME,
			    NeutronUser.COLUMN_NAME_GENDER,
			    NeutronUser.COLUMN_NAME_BIRTHDAY,
			    NeutronUser.COLUMN_NAME_RELATION,
			    NeutronUser.COLUMN_NAME_TYPE
			    };
		String selection = "" + NeutronUser.COLUMN_NAME_ID + "=" + userid
				+ " AND " + NeutronUser.COLUMN_NAME_TAG + "=" + TAG.normal;
		Cursor cur = db.query(
				NeutronUser.TABLE_NAME,  // The table to query
			    projection,                // The columns to return
			    selection,                 // The columns for the WHERE clause selection
			    null,                      // The values for the WHERE clause selectionArgs
			    null,                      // don't group the rows
			    null,                      // don't filter by row groups
			    null                  	// The sort order
			    );
		String timestamp = "";
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					id = cur.getInt(cur.getColumnIndex(NeutronUser.COLUMN_NAME_ID));
					name = cur.getString(cur.getColumnIndex(NeutronUser.COLUMN_NAME_NAME));
					gender = cur.getString(cur.getColumnIndex(NeutronUser.COLUMN_NAME_GENDER));
					timestamp = cur.getString(cur.getColumnIndex(NeutronUser.COLUMN_NAME_BIRTHDAY));
					relation = cur.getInt(cur.getColumnIndex(NeutronUser.COLUMN_NAME_RELATION));
					type = cur.getInt(cur.getColumnIndex(NeutronUser.COLUMN_NAME_TYPE));
				} while (cur.moveToNext());
				try {
					if(timestamp.equalsIgnoreCase(""))
						birthday = null;
					else
						birthday = sDateFormat.parse(timestamp);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				hasUser = true;
			}
			else{
				hasUser = false;
			}
		}
	}
	
	public String getName() {return name;}
	public boolean hasUser() { return hasUser; }
	public boolean isMale() {return gender=="male"? true:false;}
	public String getGender() {return gender;}
	public int getID(){return id;}
	public int getAge(){
		Calendar cBirth =  Calendar.getInstance();
		cBirth.setTime(birthday);
		if(null == birthday)
			return CONSTANT.TypicalAge;
		else
			return Calendar.getInstance().get(Calendar.YEAR) - cBirth.get(Calendar.YEAR);

	}

	public int getRelation(){return relation;}
	public int getType(){return type;}

}
