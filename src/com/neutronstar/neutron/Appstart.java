package com.neutronstar.neutron;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
import android.util.Log;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.neutronstar.neutron.push.Utils;
import com.neutron.server.persistence.model.T_user;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.SERVER;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronContract.USER;

public class Appstart extends Activity {
	public static Appstart instance = null;
	private final int requestCode = 1;
	private NeutronDbHelper ndb;
	T_user localUser; 
	T_user remoteUser;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstart);
		instance = this;
		ndb = NeutronDbHelper.GetInstance(this);
		
		//初始化百度推送通道
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY, 
				Utils.getMetaValue(Appstart.this, "api_key"));
		
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
			checkLocalUser("passcode", localUser);	// 获取服务器对应id用户信息
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
	
	private void checkLocalUser(String strServlet, T_user user)
	{
		String strUrl = SERVER.Address + "/" + strServlet;	
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new CheckUserOnRemoteSideTask().execute(strUrl);
        } else {
        	Toast toast = Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG );
			toast.show();
        }
	}
	
	private class CheckUserOnRemoteSideTask extends AsyncTask<String, Void, String> 
	{
		String state = "";
		@SuppressWarnings("unchecked")
		@Override
		protected String doInBackground(String... params) {
			String strUrl = params[0];
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
				paraList.add("isvalid");
		        paraList.add(localUser);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		  
		        ObjectInputStream ois = new ObjectInputStream(urlConn.getInputStream());  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        state = (String)paraList.get(0);
		        Log.d("isSucceed", state);
		        Log.d("id", localUser.gettUserId().toString());
		        Log.d("passcode", localUser.gettUserPasscode());
            } catch (Exception e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
			return null;
		}
		 
		protected void onPostExecute(String result) 
		{
			if(state.equals("valid"))	// 已存在用户登录成功，转入主页面
			{				
				new Handler().postDelayed(new Runnable() {
					public void run() {
						Intent intent = new Intent(Appstart.this, MainNeutron.class);
						Bundle bl = new Bundle();
						bl.putInt("userid", localUser.gettUserId());
						intent.putExtras(bl);
						startActivity(intent);
						Appstart.this.finish();
					}
				}, 1000);
			} 	
			else if(state.equals("novalid"))	// 转入登录注册页面
			{
				new Handler().postDelayed(new Runnable() {
					public void run() {
						Intent intent = new Intent(Appstart.this, Welcome.class);
						startActivity(intent);
						Appstart.this.finish();
					}
				}, 1000);
			}
			else // 没有正常返回，退出或者重新连接	
			{				
				Intent intent = new Intent(Appstart.this, ConfirmationDialogActivity.class);
				Bundle bl = new Bundle();
				bl.putInt("tag", ConfirmationDialogActivity.TAG_CONNECT_FAILED);
				intent.putExtras(bl);
				startActivityForResult(intent, requestCode);
			}
		}
	 }
	 
	 protected void onActivityResult(int requestCode, int resultCode, Intent data)
	 {
		 switch(resultCode){
		 case RESULT_OK:
			Bundle bl = data.getExtras();
			boolean confirmation = bl.getBoolean("confirmation");
			if(confirmation)
			{
				checkLocalUser("passcode", localUser);
			}
			else
			{
				this.finish();
			}
		 }
	 }

}
