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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.neutron.server.persistence.model.T_user;
import com.neutronstar.neutron.NeutronContract.NeutronAcceleration;
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
	private double lowAcc;
	private final double FILTERING_VALUE = 0.8;
	
	T_user localUser = null; 

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
				// 先测试一分钟上传一次的情况
			}
		}, 0, 60000);
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
		
		// 第一步 从数据库中取到 UPLOADTAG 为0的值（未上传的数据）
		NeutronDbHelper ndb = NeutronDbHelper.GetInstance(this);
		SQLiteDatabase db = ndb.getReadableDatabase();
		String[] projection = {
			    NeutronAcceleration.COLUMN_NAME_ACCELERATION,
				NeutronAcceleration.COLUMN_NAME_TIMESTAMP
			    };
		String selection = "" + NeutronAcceleration.COLUMN_NAME_UPLOADTAG + "=" + 0;
		Cursor cur = db.query(
				NeutronAcceleration.TABLE_NAME,  // The table to query
			    projection,                // The columns to return
			    selection,                 // The columns for the WHERE clause selection
			    null,                      // The values for the WHERE clause selectionArgs
			    null,                      // don't group the rows
			    null,                      // don't filter by row groups
			    null                  	// The sort order
			    );
		
		
		ArrayList<Serializable> paraList = new ArrayList<Serializable>();
		Acceleration acc = null;
		if (cur != null) 
		{
			if (cur.moveToFirst())
			{
				do 
				{
					acc = new Acceleration();
					acc.setAcceleration(cur.getDouble(cur.getColumnIndex(NeutronAcceleration.COLUMN_NAME_ACCELERATION)));
					acc.setTimestamp(cur.getString(cur.getColumnIndex(NeutronAcceleration.COLUMN_NAME_TIMESTAMP)));
					paraList.add(acc);
				}while (cur.moveToNext());
			}
			else
			{
				acc = null;
			}
		}
		String acce = "";
		Iterator iterator = paraList.iterator();
		acc = null;
		while(iterator.hasNext())
		{
			acc = (Acceleration)iterator.next();
			acce += acc.getAcceleration() + acc.getTimestamp();
		}
		// 第二步  将数据上传至服务器
		sendAcceleration("", 1, acce);
		
	}

	private void sendAcceleration(String strServlet, int id, String acce)
	{
		String strUrl = SERVER.Address + "/" + strServlet;	
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new SendAccelerationTask().execute(strUrl, String.valueOf(id), acce);
        } else {
        	Toast toast = Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG );
			toast.show();
        }
	}
	
	 private class SendAccelerationTask extends AsyncTask<String, Void, String> 
	 {

		@Override
		protected String doInBackground(String... params) {
			String strUrl = params[0];
			int id = Integer.valueOf(params[1]);
			String acce = params[2];
			try {
				 URL url = new URL(strUrl);
			     HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			     urlConn.setReadTimeout(10000 /* milliseconds */);
			     urlConn.setConnectTimeout(15000 /* milliseconds */);
			     urlConn.setDoInput(true);
			     urlConn.setDoOutput(true);
			     urlConn.setRequestMethod("POST");
			     urlConn.setUseCaches(false);
				urlConn.setRequestProperty("Content-Type", "application/x-java-serialized-object");
				urlConn.connect();
				OutputStream outStrm = urlConn.getOutputStream();  
		        ObjectOutputStream oos = new ObjectOutputStream(outStrm);  
		        
		        ArrayList<Serializable> paraList = new ArrayList<Serializable>();
		        paraList.add("sendacceleration");
		        paraList.add(id);
		        paraList.add(acce);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		  
		        ObjectInputStream ois = new ObjectInputStream(urlConn.getInputStream());  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        String isSucceed = (String)paraList.get(0);
		        Log.d("isSucceed", isSucceed);

            } catch (Exception e) {
                return "Unable to send acceleration to the server. The server may be invalid.";
            }
			return null;
		}
		 
		protected void onPostExecute(String result) 
		{
			if(result == "")
			{
				Toast toast = Toast.makeText(NeutronService.this, "Upload Acceleration Succeed.", Toast.LENGTH_LONG );
				toast.show();
			}
			else
			{
				Toast toast = Toast.makeText(NeutronService.this, "Upload Acceleration Failed.", Toast.LENGTH_LONG );
				toast.show();
			}
			
//			if(null != remoteUser)
//			{
				// 已存在用户登录成功，转入主页面
//				if(localUser.gettUserId().equals(remoteUser.gettUserId())
//						&& localUser.gettUserPasscode().equals(remoteUser.gettUserPasscode()))
//				{
//					
//				}
//					new Handler().postDelayed(new Runnable() {
//						public void run() {
//							Intent intent = new Intent(Appstart.this, MainNeutron.class);
//							Bundle bl = new Bundle();
//							bl.putInt("id", localUser.gettUserId());
//							intent.putExtras(bl);
//							startActivity(intent);
//							Appstart.this.finish();
//						}
//					}, 1000);
//				else 
//				{
//					
//				}
				//转入登录注册页面1
//					new Handler().postDelayed(new Runnable() {
//						public void run() {
//							Intent intent = new Intent(Appstart.this, Welcome.class);
//							startActivity(intent);
//							Appstart.this.finish();
//						}
//					}, 1000);
//			} else 
//			{
//				
//			}
			//转入登录注册页面1
//				new Handler().postDelayed(new Runnable() {
//					public void run() {
//						Intent intent = new Intent(Appstart.this, Welcome.class);
//						startActivity(intent);
//						Appstart.this.finish();
//					}
//				}, 1000);
		}
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
