package com.neutronstar.neutron;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronContract.USER;

public class Appstart extends Activity {
	private NeutronDbHelper ndb;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstart);
		ndb = NeutronDbHelper.GetInstance(this);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ��������
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN); //ȫ����ʾ
		// Toast.makeText(getApplicationContext(), "���ӣ��úñ��У�",
		// Toast.LENGTH_LONG).show();
		// overridePendingTransition(R.anim.hyperspace_in,
		// R.anim.hyperspace_out);
		
		TelephonyManager phoneMgr=(TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE); 
		Log.d("model",Build.MODEL); //�ֻ��ͺ�  
		Log.d("phonenumble",phoneMgr.getSubscriberId());//�����绰���� 
		Log.d("area",phoneMgr.getNetworkCountryIso());//�����绰���� 
		Log.d("SDK",Build.VERSION.SDK);//SDK�汾��  
		Log.d("RELEASE",Build.VERSION.RELEASE);//Firmware/OS �汾�� 
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(Appstart.this, Welcome.class);
				startActivity(intent);
				Appstart.this.finish();
			}
		}, 1000);
	}
	
	private int testStoredUser()
	{
		// �ӱ������ݿ�ȡ���û���Ϣ�����û�б����û���ת����ʼҳ 0
		SQLiteDatabase db = ndb.getReadableDatabase();
		int id = 0;
		String passcode = "";
		String phoneNumber = "";
		String areaCode = "";
		int serverId = 0;
		String serverPasscode = "";
		String serverPhoneNumber = "";
		String serverAreaCode = "";
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
				do {
					id = cur.getInt(cur.getColumnIndex(NeutronUser.COLUMN_NAME_ID));
					passcode = cur.getString(cur.getColumnIndex(NeutronUser.COLUMN_NAME_PASSCODE));
				} while (cur.moveToNext());
			}
			else{
				return 0;
			}
		}
		
		// ����userid��ȡ��֤�������ϵ���Ϣ
		String strUrl = "http://";
		URL url = null;
		try{
			url = new URL(strUrl);
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setRequestMethod("POST");
			urlConn.setUseCaches(false);
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConn.setRequestProperty("Charset", "utf-8");
			
			urlConn.connect();
			
			DataOutputStream dop = new DataOutputStream(urlConn.getOutputStream());
			dop.writeBytes("userid=" + URLEncoder.encode(String.valueOf(id),"utf-8"));
			dop.flush();
			dop.close();
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String result = "";
			String readLine = null;
			while((readLine = bufferedReader.readLine()) != null)
			{
				result += readLine;
			}
			bufferedReader.close();
			urlConn.disconnect();
			
			Log.d("result", URLDecoder.decode(result, "utf-8"));
			
		}catch (IOException e){
			e.printStackTrace();
		}
		
		
		// û�и��û���ת���¼ע��ҳ��1
		if(id != serverId) 
			return 1;
		else if(id == serverId && passcode == serverPasscode && phoneNumber == serverPhoneNumber && areaCode == serverAreaCode)
			return 2;
		else
			return 1;
		
		// ����һ��ת���ѵ�¼ҳ�� 2���Ա��û�id��phonenumber��passcode��
		
		// ���߲�һ��ת���¼ע��ҳ�� 1
		
	}
}