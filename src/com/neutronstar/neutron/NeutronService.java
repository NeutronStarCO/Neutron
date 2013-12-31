/**
 * 
 */
package com.neutronstar.neutron;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.neutronstar.neutron.NeutronContract.NeutronAcceleration;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.TAG;

public class NeutronService extends Service {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	double currentAcceleration = 0;
	float maxAcceleration = 0;
	private Timer updateTimer;
	private double lowAcc;
	private final double FILTERING_VALUE = 0.8;

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
		updateTimer = new Timer("gForceUpdateService");
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				refreshAccelerometer();
			}
		}, 0, 5000);
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				uploadAcceleration();
			}
		}, 0, 5000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sensorManager.unregisterListener(sensorEventListener);
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
		cv.put(NeutronAcceleration.COLUMN_NAME_ACCELERATION, currentAcceleration);
		cv.put(NeutronAcceleration.COLUMN_NAME_TIMESTAMP, date);
		cv.put(NeutronAcceleration.COLUMN_NAME_UPLOADTAG, 0);
		db.insert(NeutronAcceleration.TABLE_NAME, null, cv);

	}
	
	private void uploadAcceleration() {
		NeutronDbHelper ndb = NeutronDbHelper.GetInstance(this);
		SQLiteDatabase db = ndb.getReadableDatabase();
		
		
		//// 从数据库得到用户的基础数据
		String[] projection = {
			    NeutronUser.COLUMN_NAME_ID,
			    NeutronUser.COLUMN_NAME_NAME,
			    NeutronUser.COLUMN_NAME_GENDER,
			    NeutronUser.COLUMN_NAME_BIRTHDAY,
			    NeutronUser.COLUMN_NAME_RELATION,
			    NeutronUser.COLUMN_NAME_AVATAR,
			    NeutronUser.COLUMN_NAME_TYPE
			    };
//		String selection = "" + NeutronUser.COLUMN_NAME_ID + "=" + userid
//				+ " AND " + NeutronUser.COLUMN_NAME_TAG + "=" + TAG.normal;
//		Cursor cur = db.query(
//				NeutronUser.TABLE_NAME,  // The table to query
//			    projection,                // The columns to return
//			    selection,                 // The columns for the WHERE clause selection
//			    null,                      // The values for the WHERE clause selectionArgs
//			    null,                      // don't group the rows
//			    null,                      // don't filter by row groups
//			    null                  	// The sort order
//			    );
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
			lowAcc = lowAcc * FILTERING_VALUE + acc * ( 1.0f - FILTERING_VALUE );

			// 消去原有的重力引起的压力
			currentAcceleration = Math.abs(acc - lowAcc);
			Log.i("sensor", "\n Service currentAcceleration " + currentAcceleration);
			sensorManager.unregisterListener(sensorEventListener);
		}
	};

}
