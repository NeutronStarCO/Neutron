package com.neutronstar.neutron;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.neutron.server.persistence.model.T_user;
import com.neutronstar.neutron.NeutronContract.SERVER;

public class VarificationCodeActivity extends Activity {
	public static final int TAG_LOGIN = 1;
	public static final int TAG_SIGN_IN = 2;
	private Intent intent;
	private Bundle bl;
	private int tag;
	private String phonenumber;
	private String IDD;
	private String passcode;
	private int userid;
	private EditText etVarificationCode;
	private T_user user;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_varification_code);
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
				user.settUserAreacode(IDD);
				user.settUserPhonenumber(phonenumber);
				user.settUserPasscode(passcode);
				user.settUserId(userid);
				login("login", user);
				// 更新服务器该用户的passcode
				// 传递userid到MainNeutron
//				bl.putString("userid", userid);
//				intent.putExtras(bl);
//				intent.setClass(VarificationCodeActivity.this, MainNeutron.class);
//				startActivityForResult(intent, 0);
				
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
        	Toast toast = Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG );
			toast.show();
        }
	}
	
	private class LoginTask extends AsyncTask<String, Void, Integer> 
	{
		String state = "";

		@Override
		protected Integer doInBackground(String... params) {
			String strUrl = params[0];
			int result = 0;
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
		        result = (Integer)paraList.get(1);
		        Log.d("LoginTask", state);	        
           } catch (Exception e) {
               e.printStackTrace();
           }
			return result;
		}
		
		protected void onPostExecute(String result) 
		{
			Intent intent = new Intent();
			Bundle bundle  = new Bundle();
			if(state.equals("ok"))
			{
				bundle.putInt("userid", userid);
				intent.putExtras(bl);
				intent.setClass(VarificationCodeActivity.this, MainNeutron.class);
				startActivityForResult(intent, 0);
				setResult(RESULT_FIRST_USER, new Intent());
				finish();
			}
		}
	}
}
