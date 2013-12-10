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
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.neutronstar.neutron.NeutronContract.NeutronAcceleration;

public class NeutronService extends Service {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	float currentAcceleration = 0;
	float maxAcceleration = 0;
	private Timer updateTimer;

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
		db.insert(NeutronAcceleration.TABLE_NAME, null, cv);

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

			// 消去原有的重力引起的压力
			currentAcceleration = Math.abs((float) (a - calibration));
			Log.i("sensor", "\n currentAcceleration " + currentAcceleration);
			sensorManager.unregisterListener(sensorEventListener);
		}
	};

}
