package com.neutronstar.neutron;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.neutron.server.persistence.model.T_user;
import com.neutron.server.persistence.model.T_userExample;
import com.neutronstar.neutron.NeutronContract.SERVER;

public class ChangeText extends Activity {
	
	public static final int TAG_CHANGE_NAME = 1;
	public static final int TAG_CHANGE_PHONE_NUMBER = 2;	
	private Intent intent;
	private Bundle bl;
	private int tag;
	private EditText etText;
	private Button btnSave;
	private Button btnCancel;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_name);
		intent = this.getIntent();
		bl = intent.getExtras();
		etText = (EditText)findViewById(R.id.change_text);
		btnSave = (Button)findViewById(R.id.change_btn_save);
		btnCancel = (Button)findViewById(R.id.change_btn_cancel);
		tag = bl.getInt("tag");
		((TextView)findViewById(R.id.change_title)).setText(getResources().getStringArray(R.array.change_title)[tag]);
		String text = bl.getString("text");
		etText.setText(text);
		etText.addTextChangedListener(new TextWatcher(){			
			public void onTextChanged(CharSequence s,int start, int before,	int count)
			{
				btnSave.setClickable(true);
			}
			
			public void afterTextChanged(Editable arg0) {}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
		});
		switch(tag){
		case TAG_CHANGE_NAME:
			
			break;
		case TAG_CHANGE_PHONE_NUMBER:
			etText.setInputType(InputType.TYPE_CLASS_PHONE);
			break;
		}		
	}

	
	public void save(View v) {
		switch(tag){
		case TAG_CHANGE_NAME:
			Intent intent = new Intent();
			Bundle bl = new Bundle();
			bl.putString("text", etText.getText().toString());
			intent.putExtras(bl);
			ChangeText.this.setResult(RESULT_OK,intent);
			ChangeText.this.finish();
			break;
		case TAG_CHANGE_PHONE_NUMBER:
			String strUrl = SERVER.Address + "/" + "login";	
	        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected()) {
	            new GetUserOnRemoteSideTask().execute(strUrl);
	        } else {
	        	Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG ).show();
	        }
			break;
		}
	}

	public void cancel_back(View v) { // 标题栏 返回按钮
		this.finish();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private class GetUserOnRemoteSideTask extends AsyncTask<String, Void, String> 
	{
		String state = "";
		ArrayList<T_user> users;
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
				ObjectOutputStream oos = new ObjectOutputStream(urlConn.getOutputStream());  
				
				ArrayList<Serializable> paraList = new ArrayList<Serializable>();
				paraList.add("queryWithCriteria");
				T_userExample userExample = new T_userExample();
	    		userExample.createCriteria()
	    		.andTUserAreacodeEqualTo(bl.getString("idd"))
	    		.andTUserPhonenumberEqualTo(etText.getText().toString());				
		        paraList.add(userExample);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		  
		        ObjectInputStream ois = new ObjectInputStream(urlConn.getInputStream());  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        state = (String)paraList.get(0);
		        users = (ArrayList<T_user>) paraList.get(1);
            } catch (Exception e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
			return null;
		}
		
		protected void onPostExecute(String result) 
		{
			if(state.equals("ok"))
			{	
				if(users.size() == 1)
				{
					Intent intent = new Intent();
					Bundle bl = new Bundle();
					bl.putString("text", etText.getText().toString());
					bl.putSerializable("user", users.get(0));
					intent.putExtras(bl);
					ChangeText.this.setResult(RESULT_OK,intent);
					ChangeText.this.finish();
				}
				else if(users.size() == 0)
				{
					Toast.makeText(ChangeText.this,	"No user registered this number.", Toast.LENGTH_LONG).show();
				}
				
			} 	
			else // 没有正常返回，退出或者重新连接	
			{	
				Toast.makeText(ChangeText.this,	"No ok returned.", Toast.LENGTH_LONG).show();
			}
		}
		
	}
}
