package com.neutronstar.neutron.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.neutronstar.neutron.NeutronContract.CONSTANT;
import com.neutronstar.neutron.NeutronContract.ITEM;
import com.neutronstar.neutron.NeutronContract.NeutronRecord;
import com.neutronstar.neutron.NeutronDbHelper;


public class BMIModel {
	private NeutronDbHelper ndb;
	User user;
	private double weight;
	private Date wDay;
	private boolean isWeightRecorded = false; 
	private double height;
	private Date hDay;
	private boolean isHeightRecorded = false; 
	private double BMI;
	private double[] BMIStandard = {18.5,	22,	24,	25,	30,	40};
	private String[] measure = {"underweight", "normal weight", "ideal weight","overweight","obese","dangerously overweight"};

	public BMIModel(Context context, User u) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
		ndb = NeutronDbHelper.GetInstance(context);
		user = u;
		
		SQLiteDatabase db = ndb.getReadableDatabase();
			
		// 从数据库得到体重，若没有则设置为标准值
		String[] projection = {
				NeutronRecord.COLUMN_NAME_VALUE,
				NeutronRecord.COLUMN_NAME_DATETIME
			    };
		String selection = "" + NeutronRecord.COLUMN_NAME_ITEM + "=" + ITEM.weight;
		String sortOrder =
				"datetime(" + NeutronRecord.COLUMN_NAME_DATETIME + ") DESC";
		Cursor cur = db.query(
				NeutronRecord.TABLE_NAME,  // The table to query
			    projection,                // The columns to return
			    selection,                 // The columns for the WHERE clause selection
			    null,                      // The values for the WHERE clause selectionArgs
			    null,                      // don't group the rows
			    null,                      // don't filter by row groups
			    sortOrder,                 // The sort order
			    "1"							// limit
			    );
		String timestamp = "";
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					weight = cur.getDouble(cur.getColumnIndex(NeutronRecord.COLUMN_NAME_VALUE));
					timestamp = cur.getString(cur.getColumnIndex(NeutronRecord.COLUMN_NAME_DATETIME));
				} while (cur.moveToNext());
				try {
					wDay = sDateFormat.parse(timestamp);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				isWeightRecorded = true;
			}
			else{
				weight = CONSTANT.TypicalWeight;
				wDay = new Date();
				isWeightRecorded = false;
			}
		}
		// 从数据库得到身高，若没有则设置为标准值
		String[] columns = {
				NeutronRecord.COLUMN_NAME_VALUE,
				NeutronRecord.COLUMN_NAME_DATETIME
			    };
		selection = "" + NeutronRecord.COLUMN_NAME_ITEM + "=" + ITEM.height;
		sortOrder =
				"datetime(" + NeutronRecord.COLUMN_NAME_DATETIME + ") DESC";
		cur = db.query(
				NeutronRecord.TABLE_NAME,  // The table to query
				columns,                // The columns to return
				selection,                 // The columns for the WHERE clause selection
			    null,                      // The values for the WHERE clause selectionArgs
			    null,                      // don't group the rows
			    null,                      // don't filter by row groups
			    sortOrder,                  // The sort order
			    "1"
			    );
		timestamp = "";
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					height = cur.getDouble(cur.getColumnIndex(NeutronRecord.COLUMN_NAME_VALUE));
					timestamp = cur.getString(cur.getColumnIndex(NeutronRecord.COLUMN_NAME_DATETIME));
				} while (cur.moveToNext());
				try {
					hDay = sDateFormat.parse(timestamp);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				isHeightRecorded = true;
			}
			else{
				height = CONSTANT.TypicalHeight;
				hDay = new Date();
				isHeightRecorded = false;
			}
		}		
		BMI = weight / height * 100 /height * 100;
		Log.d("after rmr", "" + BMI);
	}
	
	public String getMeasure(double bmi){
		if(bmi<BMIStandard[0]) return measure[0];
		else if((bmi>=BMIStandard[0])&&(bmi<BMIStandard[1])) return measure[1];
		else if((bmi>=BMIStandard[1])&&(bmi<BMIStandard[2])) return measure[2];
		else if((bmi>=BMIStandard[2])&&(bmi<BMIStandard[3])) return measure[1];
		else if((bmi>=BMIStandard[3])&&(bmi<BMIStandard[4])) return measure[3];
		else if((bmi>=BMIStandard[4])&&(bmi<BMIStandard[5])) return measure[4];
		else return measure[5];
	}
	
	public int getMeasureLevel(){
		if(BMI<BMIStandard[0]) return 0;
		else if((BMI>=BMIStandard[0])&&(BMI<BMIStandard[1])) return 1;
		else if((BMI>=BMIStandard[1])&&(BMI<BMIStandard[2])) return 2;
		else if((BMI>=BMIStandard[2])&&(BMI<BMIStandard[3])) return 1;
		else if((BMI>=BMIStandard[3])&&(BMI<BMIStandard[4])) return 3;
		else if((BMI>=BMIStandard[4])&&(BMI<BMIStandard[5])) return 4;
		else return 5;
	}

	public double getWeight(){return weight;}
	public boolean isWeightRecorded(){return isWeightRecorded;}
	
	public void setWeight(double w){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SQLiteDatabase db = ndb.getWritableDatabase();
		weight = w;
		wDay = new Date();
		isWeightRecorded = true;
		String timestamp = sDateFormat.format(wDay);
		ContentValues cv = new ContentValues();
		cv.put(NeutronRecord.COLUMN_NAME_USERID, user.getID());
		cv.put(NeutronRecord.COLUMN_NAME_ITEM, ITEM.weight);
		cv.put(NeutronRecord.COLUMN_NAME_VALUE, w);
		cv.put(NeutronRecord.COLUMN_NAME_DATETIME, sDateFormat.format(wDay));
		cv.put(NeutronRecord.COLUMN_NAME_TIMESTAMP, timestamp);
		db.insert(NeutronRecord.TABLE_NAME, null, cv);
		BMI = weight / height * 100 /height * 100;
	}
	
	public double getHeight(){return height;}
	public boolean isHeightRecorded(){return isHeightRecorded;}
	
	public void setHeight(double h){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SQLiteDatabase db = ndb.getWritableDatabase();
		height = h;
		hDay = new Date();
		isHeightRecorded = true;
		String timestamp = sDateFormat.format(hDay);
		ContentValues cv = new ContentValues();
		cv.put(NeutronRecord.COLUMN_NAME_USERID, user.getID());
		cv.put(NeutronRecord.COLUMN_NAME_ITEM, ITEM.height);
		cv.put(NeutronRecord.COLUMN_NAME_VALUE, h);
		cv.put(NeutronRecord.COLUMN_NAME_DATETIME, sDateFormat.format(hDay));
		cv.put(NeutronRecord.COLUMN_NAME_TIMESTAMP, timestamp);
		db.insert(NeutronRecord.TABLE_NAME, null, cv);
		BMI = weight / height * 100 /height * 100;
	}
	
	public double getBMI(){ return BMI;}
	

}
