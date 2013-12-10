package com.neutronstar.neutron;

import java.io.ByteArrayOutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.neutronstar.neutron.NeutronContract.NeutronAcceleration;
import com.neutronstar.neutron.NeutronContract.NeutronGroupTesting;
import com.neutronstar.neutron.NeutronContract.NeutronRMRIndex;
import com.neutronstar.neutron.NeutronContract.NeutronRecord;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronContract.USER;
import com.neutronstar.neutron.model.User;

public class NeutronDbHelper extends SQLiteOpenHelper {
	public static NeutronDbHelper mInstance = null;
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NEUTRON";
    
    private static final String REAL_TYPE = " REAL";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BLOG_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_TABLE_ACCELERATION =
    	    "CREATE TABLE IF NOT EXISTS " + NeutronAcceleration.TABLE_NAME + " (" +
    	    NeutronAcceleration.COLUMN_NAME_ACCELERATION + REAL_TYPE + COMMA_SEP +
    	    NeutronAcceleration.COLUMN_NAME_TIMESTAMP + TEXT_TYPE + " )";
    
    private static final String SQL_CREATE_TABLE_RMR_INDEX = 
    		"CREATE TABLE IF NOT EXISTS " + NeutronRMRIndex.TABLE_NAME + " ("+ 
    		NeutronRMRIndex.COLUMN_NAME_RMRINDEX + REAL_TYPE + COMMA_SEP +  
    		NeutronRMRIndex.COLUMN_NAME_DATESTAMP + TEXT_TYPE+")";

    private static final String SQL_CREATE_TABLE_RECORD =
    	    "CREATE TABLE IF NOT EXISTS " + NeutronRecord.TABLE_NAME + " (" +
    	    NeutronRecord.COLUMN_NAME_USERID + INTEGER_TYPE + COMMA_SEP +
    	    NeutronRecord.COLUMN_NAME_ITEM + INTEGER_TYPE + COMMA_SEP +
    	    NeutronRecord.COLUMN_NAME_VALUE + REAL_TYPE + COMMA_SEP +
    	    NeutronRecord.COLUMN_NAME_DATETIME + TEXT_TYPE + COMMA_SEP +
    	    NeutronRecord.COLUMN_NAME_TIMESTAMP + TEXT_TYPE + COMMA_SEP +
    	    NeutronRecord.COLUMN_NAME_GROUPTESTID + INTEGER_TYPE + COMMA_SEP +
    		NeutronRecord.COLUMN_NAME_TAG + INTEGER_TYPE + " )";
    
    private static final String SQL_CREATE_TABLE_USER =
    	    "CREATE TABLE IF NOT EXISTS " + NeutronUser.TABLE_NAME + " (" +
    	    NeutronUser.COLUMN_NAME_ID + INTEGER_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_BIRTHDAY + TEXT_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_RELATION + INTEGER_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_TYPE + INTEGER_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_AVATAR + BLOG_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_TAG + INTEGER_TYPE + " )";
    
    private static final String SQL_CREATE_TABLE_GROUPTESTING =
    	    "CREATE TABLE IF NOT EXISTS " + NeutronGroupTesting.TABLE_NAME + " (" +
    	    NeutronGroupTesting.COLUMN_NAME_USERID + INTEGER_TYPE + COMMA_SEP +		
    	    NeutronGroupTesting.COLUMN_NAME_GROUP + INTEGER_TYPE + COMMA_SEP +
    	    NeutronGroupTesting.COLUMN_NAME_DATETIME + TEXT_TYPE + COMMA_SEP +
    	    NeutronGroupTesting.COLUMN_NAME_TESTINGINST + TEXT_TYPE + COMMA_SEP +
    		NeutronGroupTesting.COLUMN_NAME_TAG + INTEGER_TYPE + " )";
    
    private static final String SQL_INSERT_TEST_USER =
    	    "INSERT INTO " + NeutronUser.TABLE_NAME + " values (1, '郭成','male', '1980-04-20', 1, 1, 0)";

	public NeutronDbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public NeutronDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	public static NeutronDbHelper GetInstance(Context context) {
		if (mInstance == null) {
			mInstance = new NeutronDbHelper(context);
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE_ACCELERATION);
		db.execSQL(SQL_CREATE_TABLE_RMR_INDEX);
		db.execSQL(SQL_CREATE_TABLE_RECORD);
		db.execSQL(SQL_CREATE_TABLE_USER);
		db.execSQL(SQL_CREATE_TABLE_GROUPTESTING);
		
		// 添加一个初始化的用户
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Bitmap bitmap = ((BitmapDrawable) MainNeutron.instance.getResources().getDrawable(R.drawable.mini_avatar_shadow)).getBitmap();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); 
		ContentValues cv = new ContentValues(); 
		cv.put(NeutronUser.COLUMN_NAME_ID, 1);
		cv.put(NeutronUser.COLUMN_NAME_NAME, "阿凡达");
		cv.put(NeutronUser.COLUMN_NAME_GENDER, "male");
		cv.put(NeutronUser.COLUMN_NAME_BIRTHDAY, "1980-04-20");
		cv.put(NeutronUser.COLUMN_NAME_RELATION, USER.me);
		cv.put(NeutronUser.COLUMN_NAME_TYPE, USER.registered);
		cv.put(NeutronUser.COLUMN_NAME_AVATAR, baos.toByteArray());
		cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.normal);
		long result = db.insert(NeutronUser.TABLE_NAME, null, cv); 
		
		// 添加另一个初始化用户用于测试家庭成员功能
		baos = new ByteArrayOutputStream();
		bitmap = ((BitmapDrawable) MainNeutron.instance.getResources().getDrawable(R.drawable.xiaohei)).getBitmap();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); 
		cv = new ContentValues(); 
		cv.put(NeutronUser.COLUMN_NAME_ID, 2);
		cv.put(NeutronUser.COLUMN_NAME_NAME, "小白");
		cv.put(NeutronUser.COLUMN_NAME_GENDER, "male");
		cv.put(NeutronUser.COLUMN_NAME_BIRTHDAY, "1983-01-15");
		cv.put(NeutronUser.COLUMN_NAME_RELATION, USER.brother);
		cv.put(NeutronUser.COLUMN_NAME_TYPE, USER.subregister);
		cv.put(NeutronUser.COLUMN_NAME_AVATAR, baos.toByteArray());
		cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.normal);
		result = db.insert(NeutronUser.TABLE_NAME, null, cv); 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
/*		db.execSQL("ALTER TABLE "+ NeutronRecord.TABLE_NAME + " RENAME TO "+ NeutronRecord.TABLE_NAME + "temp");
		db.execSQL(SQL_CREATE_TABLE_RECORD);
		db.execSQL("INSERT INTO " + NeutronRecord.TABLE_NAME + " SELECT " + 
				NeutronRecord.COLUMN_NAME_USERID + "," + 
				NeutronRecord.COLUMN_NAME_ITEM + "," +
				NeutronRecord.COLUMN_NAME_VALUE + "," +
				NeutronRecord.COLUMN_NAME_DATETIME + "," +
				NeutronRecord.COLUMN_NAME_TIMESTAMP + "," +
				" null" + " from " + NeutronRecord.TABLE_NAME + "temp");
		db.execSQL("drop table if exists " + NeutronRecord.TABLE_NAME + "temp");
		db.execSQL(SQL_CREATE_TABLE_GROUPTESTING);
		db.execSQL("INSERT INTO " + NeutronGroupTesting.TABLE_NAME + " values (1001, '1980-04-20 12:13:15.123','清华大学医院')");
*/		
//		db.execSQL("drop table if exists " + NeutronGroupTesting.TABLE_NAME);
//		db.execSQL(SQL_CREATE_TABLE_GROUPTESTING);

	}

}
