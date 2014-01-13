package com.neutronstar.neutron;

import java.io.ByteArrayOutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.neutronstar.neutron.NeutronContract.GENDER;
import com.neutronstar.neutron.NeutronContract.NeutronAcceleration;
import com.neutronstar.neutron.NeutronContract.NeutronGroupTesting;
import com.neutronstar.neutron.NeutronContract.NeutronRMRIndex;
import com.neutronstar.neutron.NeutronContract.NeutronRMRValue;
import com.neutronstar.neutron.NeutronContract.NeutronRecord;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronContract.USER;

public class NeutronDbHelper extends SQLiteOpenHelper {
	public static NeutronDbHelper mInstance = null;
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NEUTRON";
    
    private static final String REAL_TYPE = " REAL ";
    private static final String TEXT_TYPE = " TEXT ";
    private static final String INTEGER_TYPE = " INTEGER ";
    private static final String BLOG_TYPE = " BLOB ";
    private static final String COMMA_SEP = ",";
    private static final String UNIQUE = " UNIQUE ";

    private static final String SQL_CREATE_TABLE_ACCELERATION =
    	    "CREATE TABLE IF NOT EXISTS " + NeutronAcceleration.TABLE_NAME + " (" +
    	    NeutronAcceleration.COLUMN_NAME_ACCELERATION + REAL_TYPE + COMMA_SEP +
    	    NeutronAcceleration.COLUMN_NAME_TIMESTAMP + TEXT_TYPE + COMMA_SEP +
    	    NeutronAcceleration.COLUMN_NAME_UPLOADTAG + INTEGER_TYPE + " )";
    
    private static final String SQL_DEL_TABLE_ACCELERATION = 
    		"DROP TABLE IF EXISTS " + NeutronAcceleration.TABLE_NAME;
    
    private static final String SQL_ALTER_TABLE_ACCELERATION = 
    		"ALTER TABLE " + NeutronAcceleration.TABLE_NAME + " ADD " + NeutronAcceleration.COLUMN_NAME_UPLOADTAG +
    		INTEGER_TYPE;
    
    private static final String SQL_CREATE_TABLE_RMR_INDEX = 
    		"CREATE TABLE IF NOT EXISTS " + NeutronRMRIndex.TABLE_NAME + " ("+ 
    		NeutronRMRIndex.COLUMN_NAME_RMRINDEX + REAL_TYPE + COMMA_SEP +  
    		NeutronRMRIndex.COLUMN_NAME_DATESTAMP + TEXT_TYPE + " )";
    
    private static final String SQL_CREATE_TABLE_RMR_VALUE = 
    		"CREATE TABLE IF NOT EXISTS " + NeutronRMRValue.TABLE_NAME + " (" +
    		NeutronRMRValue.COLUMN_NAME_RMRVALUE + REAL_TYPE + COMMA_SEP +
    		NeutronRMRValue.COLUMN_NAME_DATESTAMP + TEXT_TYPE + UNIQUE + COMMA_SEP +
    		NeutronRMRValue.COLUMN_NAME_TAG + INTEGER_TYPE + " )";

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
    	    NeutronUser.COLUMN_NAME_GENDER + INTEGER_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_BIRTHDAY + TEXT_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_IDD + TEXT_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_PHONE_NUMBER + TEXT_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_RELATION + INTEGER_TYPE + COMMA_SEP +
    	    NeutronUser.COLUNM_NAME_RELATION_TAG + INTEGER_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_TYPE + INTEGER_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_AVATAR + BLOG_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_PASSCODE + TEXT_TYPE + COMMA_SEP +
    	    NeutronUser.COLUMN_NAME_TAG + INTEGER_TYPE + " )";
    
    private static final String SQL_CREATE_TABLE_GROUPTESTING =
    	    "CREATE TABLE IF NOT EXISTS " + NeutronGroupTesting.TABLE_NAME + " (" +
    	    NeutronGroupTesting.COLUMN_NAME_USERID + INTEGER_TYPE + COMMA_SEP +		
    	    NeutronGroupTesting.COLUMN_NAME_GROUP + INTEGER_TYPE + COMMA_SEP +
    	    NeutronGroupTesting.COLUMN_NAME_DATETIME + TEXT_TYPE + COMMA_SEP +
    	    NeutronGroupTesting.COLUMN_NAME_TESTINGINST + TEXT_TYPE + COMMA_SEP +
    		NeutronGroupTesting.COLUMN_NAME_TAG + INTEGER_TYPE + " )";

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
		db.execSQL(SQL_CREATE_TABLE_RMR_VALUE);
		db.execSQL(SQL_CREATE_TABLE_RECORD);
		db.execSQL(SQL_CREATE_TABLE_USER);
		db.execSQL(SQL_CREATE_TABLE_GROUPTESTING);
		
//		// 添加一个初始化的用户
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		Bitmap bitmap = ((BitmapDrawable) Appstart.instance.getResources().getDrawable(R.drawable.avatar_male)).getBitmap();
//		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); 
//		ContentValues cv = new ContentValues(); 
//		cv.put(NeutronUser.COLUMN_NAME_ID, 1);
//		cv.put(NeutronUser.COLUMN_NAME_NAME, "杰克·萨利");
//		cv.put(NeutronUser.COLUMN_NAME_GENDER, GENDER.male);
//		cv.put(NeutronUser.COLUMN_NAME_BIRTHDAY, "1980-04-20");
//		cv.put(NeutronUser.COLUMN_NAME_RELATION, USER.me);
//		cv.put(NeutronUser.COLUMN_NAME_TYPE, USER.registered);
//		cv.put(NeutronUser.COLUMN_NAME_AVATAR, baos.toByteArray());
//		cv.put(NeutronUser.COLUMN_NAME_PASSCODE, "123456");
//		cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.normal);
//		long result = db.insert(NeutronUser.TABLE_NAME, null, cv); 
//		
//		// 添加另一个初始化用户用于测试家庭成员功能
//		baos = new ByteArrayOutputStream();
//		bitmap = ((BitmapDrawable) Appstart.instance.getResources().getDrawable(R.drawable.avatar_female)).getBitmap();
//		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); 
//		cv = new ContentValues(); 
//		cv.put(NeutronUser.COLUMN_NAME_ID, 2);
//		cv.put(NeutronUser.COLUMN_NAME_NAME, "奈蒂莉");
//		cv.put(NeutronUser.COLUMN_NAME_GENDER, GENDER.female);
//		cv.put(NeutronUser.COLUMN_NAME_BIRTHDAY, "1983-01-15");
//		cv.put(NeutronUser.COLUMN_NAME_RELATION, USER.wife);
//		cv.put(NeutronUser.COLUMN_NAME_TYPE, USER.subregister);
//		cv.put(NeutronUser.COLUMN_NAME_AVATAR, baos.toByteArray());
//		cv.put(NeutronUser.COLUMN_NAME_PASSCODE, "123456");
//		cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.normal);
//		result = db.insert(NeutronUser.TABLE_NAME, null, cv); 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		if (!checkColumnExist(db, NeutronAcceleration.TABLE_NAME, NeutronAcceleration.COLUMN_NAME_UPLOADTAG))
//		{
//			db.execSQL(SQL_ALTER_TABLE_ACCELERATION);
//		}
		
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
	
	private boolean checkColumnExist(SQLiteDatabase db, String tableName
	        , String columnName) {
	    boolean result = false ;
	    Cursor cursor = null ;
	    try
	    {
	        //查询一行
	        cursor = db.rawQuery( "SELECT * FROM " + tableName + " LIMIT 0"
	            , null );
	        result = cursor != null && cursor.getColumnIndex(columnName) != -1 ;
	    }
	    catch (Exception e)
	    {
	         Log.e("SQLiteDB","checkColumnExists" + e.getMessage()) ;
	    }
	    finally
	    {
	        if(null != cursor && !cursor.isClosed())
	        {
	            cursor.close() ;
	        }
	    }

	    return result ;
	}

}
