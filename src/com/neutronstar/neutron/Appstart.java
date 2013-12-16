package com.neutronstar.neutron;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.neutron.server.persistence.model.T_user;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.SERVER;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronContract.USER;

@SuppressLint("NewApi")
public class Appstart extends Activity {
	public static Appstart instance = null;
	private NeutronDbHelper ndb;
	private int id;
	T_user localUser; 
	T_user remoteUser;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstart);
		instance = this;
		ndb = NeutronDbHelper.GetInstance(this);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(Appstart.this, Welcome.class);
				startActivity(intent);
				Appstart.this.finish();
			}
		}, 1000);
//		testStoredUser();
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
		
//		getRemoteUser("login", 1);
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
			    null                  	// The sort order
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
//		String strUrl = SERVER.address + "/" + strServlet;
		String strUrl = "http://www.sina.com.cn/";
        ConnectivityManager connMgr = (ConnectivityManager) 
            getSystemService(this.CONNECTIVITY_SERVICE);
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
			     Log.d("strUrl", strUrl);
				urlConn.connect();
		        int response = urlConn.getResponseCode();
		        Log.d("----", "The response is: " + response);
		        is = urlConn.getInputStream();

            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
			return null;
		}
		 
	 }

	
	private void testStoredUser()
	{	
		T_user user = new T_user(); 		
		
		// 根据userid获取验证服务器上的信息
		String strUrl = SERVER.address + "/login";
		Log.d("strUrl", strUrl);
		URL url = null;
		try{
			url = new URL(strUrl);
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setRequestMethod("POST");
			urlConn.setUseCaches(false);
			urlConn.setRequestProperty("Content-Type", "application/x-java-serialized-object");
			
			urlConn.connect();
			
			ObjectOutputStream oos = new ObjectOutputStream(urlConn.getOutputStream());
//			user.settUserDeltag(String.valueOf(TAG.normal));
			user.settUserId(id);
			ArrayList<Serializable> paraList = new ArrayList<Serializable>();
			paraList.add("query");
	        paraList.add(user);
			oos.writeObject(paraList);
			oos.flush();
			oos.close();
			
			InputStream inStrm = urlConn.getInputStream(); 
	        ObjectInputStream ois = new ObjectInputStream(inStrm); 
	        paraList = (ArrayList<Serializable>)ois.readObject();
			String isSucceed = (String)paraList.get(0);
			user = (T_user)paraList.get(1);
			urlConn.disconnect();
			if(isSucceed.equals("ok"))
				Log.d("isSucceed", "ok");
			else
				Log.d("isSucceed", "error");
			
			Log.d("user", user.gettUserPasscode());
			ois.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
/*		// 已存在用户登录成功，转入主页面
		if(id == user.gettUserId() && passcode == user.gettUserPasscode())
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(Appstart.this, MainNeutron.class);
					Bundle bl = new Bundle();
					bl.putInt("id", id);
					intent.putExtras(bl);
					startActivity(intent);
					Appstart.this.finish();
				}
			}, 1000);
		else //转入登录注册页面1
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(Appstart.this, Welcome.class);
					startActivity(intent);
					Appstart.this.finish();
				}
			}, 1000);
*/		
	}
}
