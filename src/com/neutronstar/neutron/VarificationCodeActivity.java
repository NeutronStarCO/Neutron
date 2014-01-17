package com.neutronstar.neutron;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.neutron.server.persistence.model.T_relation;
import com.neutron.server.persistence.model.T_relationExample;
import com.neutron.server.persistence.model.T_user;
import com.neutron.server.persistence.model.T_userExample;
import com.neutronstar.neutron.NeutronContract.NeutronGroupTesting;
import com.neutronstar.neutron.NeutronContract.NeutronRecord;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.SERVER;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronContract.USER;

public class VarificationCodeActivity extends Activity {
	public static final int TAG_LOGIN = 1;
	public static final int TAG_SIGN_IN = 2;
	private NeutronDbHelper ndb;
	private Intent intent;
	private Bundle bl;
	private int tag;
	private String phonenumber;
	private String IDD;
	private String passcode;
	private int userid;
	private EditText etVarificationCode;
	private T_user user;
	private T_userExample uerEx;
	private ArrayList<T_relation> alRelation;
	private ArrayList<T_user> alUser;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_varification_code);
		ndb = NeutronDbHelper.GetInstance(this);
		intent = this.getIntent();
		bl = intent.getExtras();
		tag = bl.getInt("tag");
		IDD = bl.getString("IDD");
		phonenumber = bl.getString("phonenumber");
		passcode = bl.getString("passcode");
		userid = bl.getInt("userid");
		etVarificationCode = (EditText) findViewById(R.id.varification_code_code);
		etVarificationCode.setText(passcode);
	}
	
	
	public void back(View view)
	{
		this.finish();
	}
	
	public void next(View view)
	{
		if(passcode.equals(etVarificationCode.getText().toString()))
		{
			Intent intent = new Intent();
			Bundle bl = new Bundle();
			bl.putString("phonenumber", phonenumber);
			bl.putString("IDD", IDD);
			bl.putString("passcode", etVarificationCode.getText().toString());
			intent.putExtras(bl);
			switch(tag)
			{
			case TAG_LOGIN:	
				user = (T_user)this.getIntent().getExtras().getSerializable("t_user");
				user.settUserAreacode(IDD);
				user.settUserPhonenumber(phonenumber);
				user.settUserPasscode(passcode);
				user.settUserId(userid);
				user.settUserRegtag(USER.registered);
				login("login", user);
				break;
			case TAG_SIGN_IN:
				bl.putString("phonenumber", phonenumber);
				bl.putString("IDD", IDD);
				bl.putString("passcode", etVarificationCode.getText().toString());
				intent.putExtras(bl);
				intent.setClass(VarificationCodeActivity.this, SignUpInfoActivity.class);
				startActivityForResult(intent, 0);
				break;
			}
		}
		else
		{
			Toast toast = Toast.makeText(this, "验证码错误！", Toast.LENGTH_LONG );
			toast.show();
		}		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{	
		switch(resultCode){
		case RESULT_OK:
			break;
		case RESULT_FIRST_USER:
			this.setResult(RESULT_FIRST_USER, new Intent());
			this.finish();
			break;
		}
	}
	
	private void login(String strServlet, T_user user)
	{
		String strUrl = SERVER.Address + "/" + strServlet;	
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new LoginTask().execute(strUrl);
        } else {
        	Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG ).show();
        }
	}
	
	private class LoginTask extends AsyncTask<String, Void, String> 
	{
		String state = "";

		@Override
		protected String doInBackground(String... params) {
			String strUrl = params[0];
			String result = "";
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
		        paraList.add("update");
		        paraList.add(user);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		  
		        ObjectInputStream ois = new ObjectInputStream(urlConn.getInputStream());  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        state = (String)paraList.get(0);
		        result = user.gettUserPasscode();	        
           } catch (Exception e) {
               e.printStackTrace();
           }
			return result;
		}
		
		protected void onPostExecute(String result) 
		{
			if(state.equals("ok"))
			{
				// 清除本地数据库数据，插入本地数据库用户
				SQLiteDatabase db = ndb.getWritableDatabase();
				db.execSQL("Delete from " + NeutronUser.TABLE_NAME);
				db.execSQL("Delete from " + NeutronRecord.TABLE_NAME);
				db.execSQL("Delete from " + NeutronGroupTesting.TABLE_NAME);
				
				ContentValues cv = new ContentValues(); 
				cv.put(NeutronUser.COLUMN_NAME_ID, user.gettUserId());
				cv.put(NeutronUser.COLUMN_NAME_NAME, user.gettUserName());
				cv.put(NeutronUser.COLUMN_NAME_GENDER, user.gettUserGender());
				cv.put(NeutronUser.COLUMN_NAME_BIRTHDAY, user.gettUserBirth());
				cv.put(NeutronUser.COLUMN_NAME_IDD, user.gettUserAreacode());
				cv.put(NeutronUser.COLUMN_NAME_PHONE_NUMBER, user.gettUserPhonenumber());
				cv.put(NeutronUser.COLUMN_NAME_RELATION, USER.me);
				cv.put(NeutronUser.COLUNM_NAME_RELATION_TAG, TAG.accepted);
				cv.put(NeutronUser.COLUMN_NAME_TYPE, USER.registered);
				cv.put(NeutronUser.COLUMN_NAME_PASSCODE, result);
				cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.normal);
				cv.put(NeutronUser.COLUMN_NAME_AVATAR, user.gettUserPicture());
				db.insert(NeutronUser.TABLE_NAME, null, cv); 
				
				// 1、查远程关系表，得到所有对应关系
				new getRelationTask().execute(SERVER.Address + "/" + "relation");
				
				// 2、由关系查所有对应，插入本地数据库			
			}
			else
			{
				new AlertDialog.Builder(VarificationCodeActivity.this)
                .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                .setTitle("OOPS")
                .setMessage("We cannot login the user for you!")
                .create().show();
			}
		}
	}
	
	private class getRelationTask extends AsyncTask<String, Void, String> 
	{
		String state = "";
		T_relationExample tRelationEx;
		protected String doInBackground(String... params) {
			String strUrl = params[0];
			tRelationEx = new T_relationExample();
			tRelationEx.createCriteria()
				.andTRelationMasterIdEqualTo(user.gettUserId())
				.andTRelationDeltagEqualTo(String.valueOf(TAG.normal));
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
		        paraList.add("queryWithCriteria");
		        paraList.add(tRelationEx);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		  
		        ObjectInputStream ois = new ObjectInputStream(urlConn.getInputStream());  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        state = (String)paraList.get(0);
		        alRelation = (ArrayList<T_relation>) paraList.get(1);	
		        Log.d("alRelation.size", "" + alRelation.size());
           } catch (Exception e) {
               e.printStackTrace();
           }
			return null;
		}
		
		protected void onPostExecute(String result) 
		{
			if(state.equals("ok"))
			{
				if(alRelation.size() >= 1)
				{
					uerEx = new T_userExample();
					List<Integer> listInt = new ArrayList<Integer>();
					Iterator<T_relation> iterator = alRelation.iterator();
					while(iterator.hasNext())
					{
						listInt.add(iterator.next().gettRelationSalveId());
					}
					uerEx.createCriteria().andTUserIdIn(listInt).andTUserDeltagEqualTo(String.valueOf(TAG.normal));
					new getUserListTask().execute(SERVER.Address + "/" + "login");
				}
				else 
				{					
					Intent intent = new Intent();
					Bundle bundle  = new Bundle();
					bundle.putInt("userid", user.gettUserId());
					intent.putExtras(bl);
					intent.setClass(VarificationCodeActivity.this, MainNeutron.class);
					startActivityForResult(intent, 0);
					setResult(RESULT_FIRST_USER, new Intent());
					finish();
				}
				
			}
		}
	}
	
	private class getUserListTask extends AsyncTask<String, Void, String> 
	{
		String state = "";
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
		        paraList.add("queryWithCriteria");
		        paraList.add(uerEx);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		  
		        ObjectInputStream ois = new ObjectInputStream(urlConn.getInputStream());  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        state = (String)paraList.get(0);
		        alUser = (ArrayList<T_user>) paraList.get(1);	  
		        Log.d("alUser.size", "" + alUser.size());
           } catch (Exception e) {
               e.printStackTrace();
           }
			return null;
		}
		
		protected void onPostExecute(String result) 
		{
			if(state.equals("ok"))
			{
				Intent intent = new Intent();
				Bundle bundle  = new Bundle();
				Iterator<T_user> iteratorUser = alUser.iterator();
				while(iteratorUser.hasNext())
				{
					T_user tempUser = iteratorUser.next();
					SQLiteDatabase db = ndb.getWritableDatabase();					
					ContentValues cv = new ContentValues(); 
					cv.put(NeutronUser.COLUMN_NAME_ID, tempUser.gettUserId());
					cv.put(NeutronUser.COLUMN_NAME_NAME, tempUser.gettUserName());
					cv.put(NeutronUser.COLUMN_NAME_GENDER, tempUser.gettUserGender());
					cv.put(NeutronUser.COLUMN_NAME_BIRTHDAY, tempUser.gettUserBirth());
					cv.put(NeutronUser.COLUMN_NAME_IDD, tempUser.gettUserAreacode());
					cv.put(NeutronUser.COLUMN_NAME_PHONE_NUMBER, tempUser.gettUserPhonenumber());
					cv.put(NeutronUser.COLUMN_NAME_TYPE, tempUser.gettUserRegtag());
					cv.put(NeutronUser.COLUMN_NAME_TAG, tempUser.gettUserDeltag());
					cv.put(NeutronUser.COLUMN_NAME_AVATAR, tempUser.gettUserPicture());
					Iterator<T_relation> iteratorRelation = alRelation.iterator();
					while(iteratorRelation.hasNext())
					{
						T_relation tempRelation = iteratorRelation.next();
						if(tempRelation.gettRelationSalveId().equals(tempUser.gettUserId()))
						{
							cv.put(NeutronUser.COLUMN_NAME_RELATION, tempRelation.gettRelationRelation());
							cv.put(NeutronUser.COLUNM_NAME_RELATION_TAG, tempRelation.gettRelationConfirmtag());
						}
					}					
					db.insert(NeutronUser.TABLE_NAME, null, cv); 
				}
				
				bundle.putInt("userid", user.gettUserId());
				intent.putExtras(bl);
				intent.setClass(VarificationCodeActivity.this, MainNeutron.class);
				startActivityForResult(intent, 0);
				setResult(RESULT_FIRST_USER, new Intent());
				finish();
			}
		}
	}
}
