/**
 * 
 */
package com.neutronstar.neutron;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.neutron.server.persistence.model.T_accdata;
import com.neutron.server.persistence.model.T_rmr;
import com.neutron.server.persistence.model.T_user;
import com.neutronstar.neutron.NeutronContract.NeutronAcceleration;
import com.neutronstar.neutron.NeutronContract.NeutronRMRValue;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.SERVER;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronContract.USER;
import com.neutronstar.neutron.model.Acceleration;

public class NeutronService extends Service {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	double currentAcceleration = 0;
	float maxAcceleration = 0;
	private Timer updateTimer;
	private Timer uploadTimer;
	private Timer deleteTimer;
	private Timer downloadRMRTimer;
	private double lowAcc;
	private final double FILTERING_VALUE = 0.8;

	T_user localUser = null;
	T_accdata accData = null;
	T_rmr rmr = null;
	ArrayList<Serializable> alAccData = null;
	ArrayList<T_rmr> alRMRValue = null;
	int countRMR = 24;

	public NeutronService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		localUser = getLocalUser();
		updateTimer = new Timer("gForceUpdateService");
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				refreshAccelerometer();
			}
		}, 0, 5000);

		SecureRandom random = new SecureRandom();
		int d1 = random.nextInt(10) * 1000;
		alAccData = new ArrayList<Serializable>();
		uploadTimer = new Timer("gForceUploadService");
		uploadTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				uploadAcceleration("data", alAccData);
				// 先测试一分钟上传一次的情况
			}
		}, 0, 600000);
		
		deleteTimer = new Timer("gForceDeleteService");
		deleteTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				deleteAccelerationHistory();
			}
			
		}, 0, 3600000);
		
		downloadRMRTimer = new Timer("gForceGetRMRService");
		downloadRMRTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				downloadRMRValue("data", countRMR, alRMRValue);
			}
			
		}, 0, 60000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sensorManager.unregisterListener(sensorEventListener);
	}
	
	@SuppressLint("SimpleDateFormat")
	private void deleteAccelerationHistory()
	{
		//删除本地1天前的数据
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());
		cal.add(Calendar.DATE, -1);
		String date = sDateFormat.format(cal.getTime());
		
		NeutronDbHelper ndb = NeutronDbHelper.GetInstance(this);
		SQLiteDatabase db = ndb.getWritableDatabase();
		String[] args = { "1", date };
		int delNum = db.delete(NeutronAcceleration.TABLE_NAME, "uploadtag=? and timestamp<?", args);
		if(delNum >= 0)
		{
			Log.v("Delete Succeed", "delete " + delNum + " lines data.");
		}		
	}
	
	private void refreshAccelerometer() {

		sensorManager.registerListener(sensorEventListener, accelerometer,
				SensorManager.SENSOR_DELAY_FASTEST);
		String currentG = currentAcceleration / SensorManager.STANDARD_GRAVITY
				+ "Gs";
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		String date = sDateFormat.format(new java.util.Date());
		NeutronDbHelper ndb = NeutronDbHelper.GetInstance(this);
		SQLiteDatabase db = ndb.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(NeutronAcceleration.COLUMN_NAME_ACCELERATION,
				currentAcceleration);
		cv.put(NeutronAcceleration.COLUMN_NAME_TIMESTAMP, date);
		cv.put(NeutronAcceleration.COLUMN_NAME_UPLOADTAG, 0);
		db.insert(NeutronAcceleration.TABLE_NAME, null, cv);

	}

	
	private void downloadRMRValue(String strServlet,
			int countRMR, ArrayList<T_rmr> alRMRValue) 
	{
		String strUrl = SERVER.Address + "/" + strServlet;
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			Looper.prepare();
			new DownloadRMRValueTask().execute(strUrl);
			Looper.loop();
		} else {
			Toast toast = Toast.makeText(this,
					"No network connection available.", Toast.LENGTH_LONG);
			toast.show();
		}
	}
	
	private class DownloadRMRValueTask extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String strUrl = params[0];
			String result = "error";
			try {
				URL url = new URL(strUrl);
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				urlConn.setReadTimeout(10000 /* milliseconds */);
				urlConn.setConnectTimeout(15000 /* milliseconds */);
				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);
				urlConn.setRequestMethod("POST");
				urlConn.setUseCaches(false);
				urlConn.setRequestProperty("Content-Type",
						"application/x-java-serialized-object");
				urlConn.connect();
				OutputStream outStrm = urlConn.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(outStrm);

				ArrayList<Serializable> paraList = new ArrayList<Serializable>();
				paraList.add("getrmrbynum");
				paraList.add(localUser.gettUserId());
				paraList.add(countRMR);
				oos.writeObject(paraList);
				oos.flush();
				oos.close();

				ObjectInputStream ois = new ObjectInputStream(
						urlConn.getInputStream());
				paraList = (ArrayList<Serializable>) ois.readObject();
				result = (String) paraList.get(0);
				if(result.equals("ok"))
				{
					alRMRValue = new ArrayList<T_rmr>((ArrayList<T_rmr>)paraList.get(1));
				}
				Log.v("result", result);

			} catch (Exception e) {
				Log.v("exception", e.getMessage());
			}
			return result;
		}
		
		
		@Override
		protected void onPostExecute(String result) {
			if (result.equals("ok")) {
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss.SSS");
				NeutronDbHelper ndb = NeutronDbHelper
						.GetInstance(NeutronService.this);
				SQLiteDatabase db = ndb.getWritableDatabase();
				db.beginTransaction();
				if(alRMRValue != null)
				{
					
					Iterator<T_rmr> iterator = alRMRValue.iterator();
					while(iterator.hasNext())
					{
						T_rmr r = iterator.next();
						String date = sDateFormat.format(r.gettRmrDatetime());
						double rmrValue = 80 + r.gettRmrValue();
						ContentValues cv = new ContentValues();
						cv.put(NeutronRMRValue.COLUMN_NAME_RMRVALUE, rmrValue);
						cv.put(NeutronRMRValue.COLUMN_NAME_DATESTAMP, date);
						db.insertWithOnConflict(NeutronRMRValue.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
					}
					alRMRValue = null;
					db.setTransactionSuccessful();
					db.endTransaction();
					//
					Toast toast = Toast.makeText(NeutronService.this,
							"Download RMRValue Succeed.", Toast.LENGTH_LONG);
					toast.show();
				}
				else
				{
					Toast toast = Toast.makeText(NeutronService.this,
							"Download RMRValue Succeed, but no update.", Toast.LENGTH_LONG);
					toast.show();
				}
			} else {
				Toast toast = Toast.makeText(NeutronService.this,
						"Download RMRValue Failed.", Toast.LENGTH_LONG);
				toast.show();
			}
		}
	}
	
	private void uploadAcceleration(String strServlet,
			ArrayList<Serializable> alAccData) {

		// 第一步 从数据库中取到 UPLOADTAG 为0的值（未上传的数据）
		NeutronDbHelper ndb = NeutronDbHelper.GetInstance(this);
		SQLiteDatabase db = ndb.getReadableDatabase();
		String[] projection = { NeutronAcceleration.COLUMN_NAME_ACCELERATION,
				NeutronAcceleration.COLUMN_NAME_TIMESTAMP };
		String selection = "" + NeutronAcceleration.COLUMN_NAME_UPLOADTAG + "="
				+ 0;
		String limit = "" + 1000;
		Cursor cur = db.query(NeutronAcceleration.TABLE_NAME, // The table to
																// query
				projection, // The columns to return
				selection, // The columns for the WHERE clause selection
				null, // The values for the WHERE clause selectionArgs
				null, // don't group the rows
				null, // don't filter by row groups
				null, // The sort order
				limit
				);

		accData = null;
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					accData = new T_accdata();
					accData.settAccdataUserid(localUser.gettUserId());
					accData.settAccdataValue(cur.getDouble(cur
							.getColumnIndex(NeutronAcceleration.COLUMN_NAME_ACCELERATION)));

					String strDate = cur
							.getString(cur
									.getColumnIndex(NeutronAcceleration.COLUMN_NAME_TIMESTAMP));
					Date dt = new Date();

					SimpleDateFormat sDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss.SSS");
					try {
						dt = sDateFormat.parse(strDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					accData.settAccdataDatatime(dt);
					alAccData.add(accData);
				} while (cur.moveToNext());
			} else {
				Looper.prepare();
				Toast toast = Toast.makeText(this,
						"Everything up to date.", Toast.LENGTH_LONG);
				toast.show();
				Looper.loop();
				return;
			}
		}
		// 第二步 将数据上传至服务器
		String strUrl = SERVER.Address + "/" + strServlet;
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new UploadAccelerationTask().execute(strUrl);
		} else {
			Looper.prepare();
			Toast toast = Toast.makeText(this,
					"No network connection available.", Toast.LENGTH_LONG);
			toast.show();
			Looper.loop();
		}
	}

	private class UploadAccelerationTask extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String strUrl = params[0];
			String result = "error";
			try {
				URL url = new URL(strUrl);
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				urlConn.setReadTimeout(10000 /* milliseconds */);
				urlConn.setConnectTimeout(15000 /* milliseconds */);
				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);
				urlConn.setRequestMethod("POST");
				urlConn.setUseCaches(false);
				urlConn.setRequestProperty("Content-Type",
						"application/x-java-serialized-object");
				urlConn.connect();
				OutputStream outStrm = urlConn.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(outStrm);

				ArrayList<Serializable> paraList = new ArrayList<Serializable>();
				paraList.add("upload");
				paraList.add(alAccData);
				oos.writeObject(paraList);
				oos.flush();
				oos.close();

				ObjectInputStream ois = new ObjectInputStream(
						urlConn.getInputStream());
				paraList = (ArrayList<Serializable>) ois.readObject();
				result = (String) paraList.get(0);
				Log.d("result", result);

			} catch (Exception e) {
				Log.d("exception", e.getMessage());
			}
			
			alAccData.clear();
			return result;
		}

		protected void onPostExecute(String result) {
			if (result.equals("ok")) {
				NeutronDbHelper ndb = NeutronDbHelper
						.GetInstance(NeutronService.this);
				SQLiteDatabase db = ndb.getWritableDatabase();
				ContentValues cv = new ContentValues();
				cv.put("uploadtag", 1);
				String[] args = { "0" };
				db.update(NeutronAcceleration.TABLE_NAME, cv, "uploadtag=?",
						args);
				Toast toast = Toast.makeText(NeutronService.this,
						"Upload Acceleration Succeed.", Toast.LENGTH_LONG);
				toast.show();
			} else {
				Toast toast = Toast.makeText(NeutronService.this,
						"Upload Acceleration Failed.", Toast.LENGTH_LONG);
				toast.show();
			}
		}
	}

	private T_user getLocalUser() {
		T_user user = null;
		NeutronDbHelper ndb = NeutronDbHelper.GetInstance(this);
		SQLiteDatabase db = ndb.getReadableDatabase();
		String[] projection = { NeutronUser.COLUMN_NAME_ID,
				NeutronUser.COLUMN_NAME_PASSCODE };
		String selection = "" + NeutronUser.COLUMN_NAME_RELATION + "="
				+ USER.me + " AND " + NeutronUser.COLUMN_NAME_TAG + "="
				+ TAG.normal;
		Cursor cur = db.query(NeutronUser.TABLE_NAME, // The table to query
				projection, // The columns to return
				selection, // The columns for the WHERE clause selection
				null, // The values for the WHERE clause selectionArgs
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
				);
		if (cur != null) {
			if (cur.moveToFirst()) {
				user = new T_user();
				do {
					user.settUserId(cur.getInt(cur
							.getColumnIndex(NeutronUser.COLUMN_NAME_ID)));
					user.settUserPasscode(cur.getString(cur
							.getColumnIndex(NeutronUser.COLUMN_NAME_PASSCODE)));
				} while (cur.moveToNext());
			} else {
				user = null;
			}
		}
		return user;
	}

	private final SensorEventListener sensorEventListener = new SensorEventListener() {
		// 系统设置的重力加速度标准值，设备在水平静止的情况下就承受这个压力，所以默认Y轴方向的加速度值为STANDARD_GRAVITY
		double calibration = SensorManager.STANDARD_GRAVITY;

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];

			// 计算三个方向的加速度
			double a = Math.round(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)
					+ Math.pow(z, 2)));
			double acc = Math.abs((float) (a - calibration));
			lowAcc = lowAcc * FILTERING_VALUE + acc * (1.0f - FILTERING_VALUE);

			// 消去原有的重力引起的压力
			currentAcceleration = Math.abs(acc - lowAcc);
			Log.i("sensor", "\n Service currentAcceleration "
					+ currentAcceleration);
			sensorManager.unregisterListener(sensorEventListener);
		}
	};

}
