package com.neutronstar.neutron;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.neutron.server.persistence.model.T_user;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.SERVER;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronContract.USER;

public class Appstart extends Activity {
	public static Appstart instance = null;
	private NeutronDbHelper ndb;
	T_user localUser; 
	T_user remoteUser;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstart);
		instance = this;
		ndb = NeutronDbHelper.GetInstance(this);
		
		TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);

		// 从本地数据库取得用户信息，如果没有本地用户，转入起始页 
		localUser = getLocalUser();
		if(null==localUser)
		{
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(Appstart.this, Welcome.class);
					startActivity(intent);
					Appstart.this.finish();
				}
			}, 1000);
		}
		else
		{
			localUser.settUserImei(tm.getDeviceId());		// IMEI 设备号
			localUser.settUserImsi(tm.getSubscriberId());	// IMSI 国际移动用户识别码(International Mobile Subscriber Identity)
			getRemoteUser("login", localUser.gettUserId());	// 获取服务器对应id用户信息
			Log.d("IMEI", localUser.gettUserImei());
			Log.d("UserId", "" +localUser.gettUserId());
		}
	}
	
	
	private T_user getLocalUser()
	{
		T_user user = null;
		SQLiteDatabase db = ndb.getReadableDatabase();
		String[] projection = {
			    NeutronUser.COLUMN_NAME_ID,
			    NeutronUser.COLUMN_NAME_PASSCODE
			    };
		String selection = "" + NeutronUser.COLUMN_NAME_RELATION + "=" + USER.me
				+ " AND " + NeutronUser.COLUMN_NAME_TAG + "=" + TAG.normal;
		Cursor cur = db.query(
				NeutronUser.TABLE_NAME,  // The table to query
			    projection,                // The columns to return
			    selection,                 // The columns for the WHERE clause selection
			    null,                      // The values for the WHERE clause selectionArgs
			    null,                      // don't group the rows
			    null,                      // don't filter by row groups
			    null                  		// The sort order
			    );
		if (cur != null) {
			if (cur.moveToFirst()) {
				user = new T_user();
				do {
					user.settUserId(cur.getInt(cur.getColumnIndex(NeutronUser.COLUMN_NAME_ID)));
					user.settUserPasscode(cur.getString(cur.getColumnIndex(NeutronUser.COLUMN_NAME_PASSCODE)));
				} while (cur.moveToNext());
			}else{
				user = null;
			}
		}
		return user;
	}
	
	private void getRemoteUser(String strServlet, int id)
	{
		String strUrl = SERVER.Address + "/" + strServlet;	
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new GetRemoteUserTask().execute(strUrl, String.valueOf(id));
        } else {
        	Toast toast = Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG );
			toast.show();
        }
	}
	
	 private class GetRemoteUserTask extends AsyncTask<String, Void, String> 
	 {

		@Override
		protected String doInBackground(String... params) {
			String strUrl = params[0];
			int id = Integer.valueOf(params[1]);
			InputStream is = null;
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
		        paraList.add("query");
		        paraList.add(localUser);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		        Log.d("isSucceed", "0");
		        is = urlConn.getInputStream();
		        ObjectInputStream ois = new ObjectInputStream(is);  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        String isSucceed = (String)paraList.get(0);
		        Log.d("isSucceed", "1");
		        remoteUser = (T_user)paraList.get(1);
		        if(null == remoteUser)
		        	Log.d("isSucceed", "gotzero");
		        Log.d("isSucceed", "isSucceed="+isSucceed);
		        Log.d("name-", remoteUser.gettUserName());

            } catch (Exception e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
			return null;
		}
		 
		protected void onPostExecute(String result) 
		{
			if(null != remoteUser)
			{
				// 已存在用户登录成功，转入主页面
				if(localUser.gettUserId().equals(remoteUser.gettUserId())
						&& localUser.gettUserPasscode().equals(remoteUser.gettUserPasscode())
	//					&& localUser.gettUserImei().equals(remoteUser.gettUserImei())
	//					&& localUser.gettUserImsi()==remoteUser.gettUserImsi()
						)
					new Handler().postDelayed(new Runnable() {
						public void run() {
							Intent intent = new Intent(Appstart.this, MainNeutron.class);
							Bundle bl = new Bundle();
							bl.putInt("id", localUser.gettUserId());
							intent.putExtras(bl);
							startActivity(intent);
							Appstart.this.finish();
						}
					}, 1000);
				else //转入登录注册页面1
					new Handler().postDelayed(new Runnable() {
						public void run() {
							Intent intent = new Intent(Appstart.this, Welcome.class);
							startActivity(intent);
							Appstart.this.finish();
						}
					}, 1000);
			} else //转入登录注册页面1
				new Handler().postDelayed(new Runnable() {
					public void run() {
						Intent intent = new Intent(Appstart.this, Welcome.class);
						startActivity(intent);
						Appstart.this.finish();
					}
				}, 1000);
		}
	 }

}
