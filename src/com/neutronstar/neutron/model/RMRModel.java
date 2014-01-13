package com.neutronstar.neutron.model;

import java.text.ParseException; 
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.widget.Toast;

import com.neutron.server.persistence.model.T_accdata;
import com.neutronstar.neutron.NeutronContract.CONSTANT;
import com.neutronstar.neutron.NeutronContract.NeutronAcceleration;
import com.neutronstar.neutron.NeutronContract.NeutronRMRIndex;
import com.neutronstar.neutron.NeutronContract.NeutronRMRValue;
import com.neutronstar.neutron.NeutronDbHelper;

public class RMRModel {
	NeutronDbHelper ndb;
	User user;
	private double weight = 60;
	private double height = 170;
	private int age = 25;
	private double RMRPerDay = 0;
	private double RMRIndexToday = 1.2;
//	private static double TypicalWeight = 60;
//	private static double TypicalHeight = 170;
//	private static int TypicalAge = 25;
	private static double TypicalRMRIndex = 1.2;
	private static double RMRIndex[] = { 1, 1.03, 1.06, 1.09, 1.12, 1.15, 1.18,
			1.2, 1.225, 1.25, 1.275, 1.3, 1.325, 1.35, 1.375, 1.4, 1.425, 1.45,
			1.475, 1.5, 1.525, 1.55, 1.575, 1.6, 1.625, 1.65, 1.675, 1.7,
			1.725, 1.75, 1.775, 1.8, 1.825, 1.85, 1.875, 1.9 };
	private static double TypicalExercise[] = { 0, 5, 10, 15, 20, 25, 30, 35,
			40, 45, 50, 55, 60, 65, 70, 90, 110, 130, 150, 170, 190, 210, 230,
			250, 270, 290, 310, 330, 350, 370, 390, 410, 430, 450, 470, 490 };

	private double sumHour[];

	public RMRModel(Context context, User u) {
		user = u;
		BMIModel bmi = new BMIModel(context, user);
		weight = bmi.getWeight();
		height = bmi.getHeight();
		age = user.getAge();
		initial(weight, height, age, context);
	}

	public RMRModel(Context context) {
		initial(CONSTANT.TypicalWeight, CONSTANT.TypicalHeight, CONSTANT.TypicalAge, context);
	}

	private void initial(double weight, double height, int age, Context context) {
		ndb = NeutronDbHelper.GetInstance(context);
		this.weight = weight;
		this.height = height;
		this.age = age;
		this.RMRIndexToday = getTodayRMRIndex();
		this.RMRPerDay = (10 * weight + 6.25 * height - 5 * age + 5)
				* RMRIndexToday;
		
	}

	public double getTodayRMRIndex() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calDate = Calendar.getInstance();
		String today = sDateFormat.format(calDate.getTime());
		calDate.add(Calendar.DATE, -1);
		String yesterday = sDateFormat.format(calDate.getTime());
		SQLiteDatabase db = ndb.getWritableDatabase();
		String sql = "select * from "
				+ NeutronRMRIndex.TABLE_NAME + " order by date("
				+ NeutronRMRIndex.COLUMN_NAME_DATESTAMP + ") desc limit 1";
		Cursor cur = db.rawQuery(sql, null);
		double index = 0;
		String date = "";
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					index = cur.getDouble(0);
					date = cur.getString(1);
				} while (cur.moveToNext());
			} else {
				index = TypicalRMRIndex;
				ContentValues cv = new ContentValues();
				cv.put(NeutronRMRIndex.COLUMN_NAME_RMRINDEX, index);
				cv.put(NeutronRMRIndex.COLUMN_NAME_DATESTAMP, today);
				db.insert(NeutronRMRIndex.TABLE_NAME, null, cv);
			}
		}

		if (date.equals(yesterday)) {
			sql = "select total(" +NeutronAcceleration.COLUMN_NAME_ACCELERATION+ "/(" + 
					NeutronAcceleration.COLUMN_NAME_ACCELERATION + "+ 4)) from " + NeutronAcceleration.TABLE_NAME	+ " where date("+ 
					NeutronAcceleration.COLUMN_NAME_TIMESTAMP +")=date('" + 
					yesterday + "')";
			cur = db.rawQuery(sql, null);
			double sum = 0;
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						sum = cur.getDouble(0);
					} while (cur.moveToNext());
				}
			}
			sum = sum * 500 * weight / CONSTANT.TypicalWeight * 5 / 86400;
			index = calRMRIndex(sum, index);
			ContentValues cv = new ContentValues();
			cv.put(NeutronRMRIndex.COLUMN_NAME_RMRINDEX, index);
			cv.put(NeutronRMRIndex.COLUMN_NAME_DATESTAMP, today);
			db.insert(NeutronRMRIndex.TABLE_NAME, null, cv);

		}
		return index;
	}

	public double calRMRIndex(double exerciseTodayP, double indexToday) {
		double exerciseToday = exerciseTodayP /weight * CONSTANT.TypicalWeight;
		int level = 0;
		int nextLevel = 0;
		if (indexToday <= 1)
			level = 0;
		else if (indexToday >= 1.9)
			level = RMRIndex.length - 1;
		else {
			for (int i = 0; i < RMRIndex.length - 1; i++) {
				if ((RMRIndex[i] < indexToday)
						&& (indexToday <= RMRIndex[i + 1]))
					level = i + 1;
			}
		}
		if (level == RMRIndex.length - 1)
			nextLevel = exerciseToday >= TypicalExercise[level] ? level
					: level - 1;
		else if (level == 0)
			nextLevel = exerciseToday >= TypicalExercise[1] ? 1
					: 0;
		else {
			if (exerciseToday >= TypicalExercise[level + 1])
				nextLevel = level + 1;
			else if (exerciseToday < TypicalExercise[level])
				nextLevel = level - 1;
			else
				nextLevel = level;
		}
		return RMRIndex[nextLevel];
	}

	public double getRMRPerDay() {
		return this.RMRPerDay;
	}

	public double getRMRPerHour() {
		return this.RMRPerDay / 24;
	}

	public double getExerciseCostRate(double acceleration) {
		return 500 * acceleration / (acceleration + 4) / CONSTANT.TypicalWeight * weight;
	}

	public double getCurrentTotalCost() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String today = sDateFormat.format(new java.util.Date());
		SQLiteDatabase db = ndb.getReadableDatabase();
		String sql = "select total(" +NeutronAcceleration.COLUMN_NAME_ACCELERATION+ "/(" + 
				NeutronAcceleration.COLUMN_NAME_ACCELERATION + "+ 4)) from " + NeutronAcceleration.TABLE_NAME	+ " where date("+ 
				NeutronAcceleration.COLUMN_NAME_TIMESTAMP +")=date('" + 
				today + "')";
		Cursor cur = db.rawQuery(sql, null);
		double sum = 0;
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					sum = cur.getDouble(0);
				} while (cur.moveToNext());
			}
		}
		Calendar curDate = Calendar.getInstance();
		Calendar startOfTodayDate = new GregorianCalendar(
				curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH),
				curDate.get(Calendar.DATE), 0, 0, 0);
		int s = (int) (curDate.getTimeInMillis() - startOfTodayDate
				.getTimeInMillis()) / 1000;
		sum *= 500 * weight / CONSTANT.TypicalWeight * 5 / s;
		sum += RMRPerDay * s / 86400;
		return sum;

	}

	
	public double[] getCurrentHourCosts(Context context) {
		NeutronDbHelper ndb = NeutronDbHelper.GetInstance(context);
		SQLiteDatabase db = ndb.getReadableDatabase();
		String[] projection = { NeutronRMRValue.COLUMN_NAME_RMRVALUE,
				NeutronRMRValue.COLUMN_NAME_DATESTAMP };
		String limit = "" + 24 ;
		String order = NeutronRMRValue.COLUMN_NAME_DATESTAMP + " desc";
		Cursor cur = db.query(NeutronRMRValue.TABLE_NAME, // The table to
																// query
				projection, // The columns to return
				null, // The columns for the WHERE clause selection
				null, // The values for the WHERE clause selectionArgs
				null, // don't group the rows
				null, // don't filter by row groups
				order, // The sort order
				limit
				);
		sumHour = new double[24];
		int h = 0;
		if (cur != null) 
		{
			if (cur.moveToFirst()) {
				do {
					sumHour[h] = cur.getDouble(cur
							.getColumnIndex(NeutronRMRValue.COLUMN_NAME_RMRVALUE)) ;
					++ h;
				} while (cur.moveToNext());
			} 
		}
		else{
			
			for (int i = 0; i < 24; ++i) {
				sumHour[i] = 0;
			}
		}
		for(;h < 24; ++h) 
		{
			sumHour[h] = 0;
		}
		return sumHour;
	}
	
//	public double[] getCurrentHourCosts(Context context) {
//		NeutronDbHelper ndb = NeutronDbHelper.GetInstance(context);
//		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		String today = sDateFormat.format(new java.util.Date());
//		SQLiteDatabase db = ndb.getReadableDatabase();
//		String sql = "select total(" +NeutronAcceleration.COLUMN_NAME_ACCELERATION+ "/(" + 
//				NeutronAcceleration.COLUMN_NAME_ACCELERATION + "+ 4)), CAST(strftime('%H',"+
//				NeutronAcceleration.COLUMN_NAME_TIMESTAMP + ") AS INTEGER) from " + NeutronAcceleration.TABLE_NAME	+ " where date("+ 
//				NeutronAcceleration.COLUMN_NAME_TIMESTAMP + ")=date('" + 
//				today + "')group by strftime('%Y%m%d %H', " +
//				NeutronAcceleration.COLUMN_NAME_TIMESTAMP + ")  order by CAST(strftime('%H',"+
//				NeutronAcceleration.COLUMN_NAME_TIMESTAMP + ") AS INTEGER)";
//		Cursor cur = db.rawQuery(sql, null);
//		sumHour = new double[24];
//		if (cur != null) {
//			if (cur.moveToFirst()) {
//				do {
//					sumHour[cur.getInt(1)] = cur.getDouble(0) * 500 * weight
//							/ CONSTANT.TypicalWeight * 5 / 3600;
//				} while (cur.moveToNext());
//			}
//		}
//		for (int i = 0; i < (new java.util.Date().getHours()); i++) {
//			sumHour[i] += getRMRPerHour();
//		}
//		for (int i = (new java.util.Date().getHours()); i < 24; i++) {
//			sumHour[i] = 0;
//		}
//		return sumHour;
//
//	}


}
