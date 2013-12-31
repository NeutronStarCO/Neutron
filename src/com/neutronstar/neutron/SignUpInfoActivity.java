package com.neutronstar.neutron;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.neutron.server.persistence.model.T_user;
import com.neutronstar.neutron.NeutronContract.SERVER;

public class SignUpInfoActivity extends Activity {
	
	private Intent intent;
	private Bundle bl;
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	private TextView tvName;
	private TextView tvBirthday;
	private T_user user;
	private Calendar c = Calendar.getInstance();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_info);
		intent = this.getIntent();
		bl = intent.getExtras();
		user = new T_user();
		user.settUserAreacode(bl.getString("IDD"));
		user.settUserPhonenumber(bl.getString("phonenumber"));
		user.settUserPasscode(bl.getString("passcode"));
		
		spinner = (Spinner) findViewById(R.id.sign_up_info_spinner);
		tvName = (TextView) findViewById(R.id.sign_up_info_name);
		tvBirthday = (TextView) findViewById(R.id.sign_up_info_birthday);
		adapter = new ArrayAdapter<String>(this, R.layout.simple_text_item, R.id.simple_text_item);
		adapter.add(getResources().getString(R.string.male));
		adapter.add(getResources().getString(R.string.female));
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		
		tvBirthday.setOnFocusChangeListener(new OnFocusChangeListener(){

			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == true) {  
                    new DatePickerDialog(SignUpInfoActivity.this, new DatePickerDialog.OnDateSetListener() {  
                        @Override  
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
                        {  
                        	c.set(Calendar.YEAR, year);
                        	c.set(Calendar.MONTH, monthOfYear);
                        	c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        }  
                    }, 1961, 9, 27).show();                 
		        }
		    }
			
		});
	}
	
	public void back(View view)
	{
		this.finish();
	}
	
	public void next(View view)
	{
		if(tvName.length() == 0)
		{
			user.settUserName(tvName.getHint().toString());
			Toast.makeText(this, "�ðɣ���Ҳ�����»�������ݣ���ӭ����", Toast.LENGTH_LONG ).show();
		}			
		else 
			user.settUserName(tvName.getText().toString());
		user.settUserBirth(dateFormat.format(c.getTime()));
		user.settUserGender(spinner.getSelectedItemId()==0? 1:0);
		addNewUser("login", user);	
	}
	
	
	class SpinnerSelectedListener implements OnItemSelectedListener {   
		
	    public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	        // An item was selected. You can retrieve the selected item using
	        // parent.getItemAtPosition(pos)
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	    }

	}
	
	private void addNewUser(String strServlet, T_user user)
	{
		String strUrl = SERVER.Address + "/" + strServlet;	
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new AddNewUserTask().execute(strUrl);
        } else {
        	Toast toast = Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG );
			toast.show();
        }
	}
	
	private class AddNewUserTask extends AsyncTask<String, Void, Integer> 
	{
		String state = "";
		int userid = 0;
		@Override
		protected Integer doInBackground(String... params) {
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
		        paraList.add("add");
		        paraList.add(user);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		  
		        ObjectInputStream ois = new ObjectInputStream(urlConn.getInputStream());  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        state = (String)paraList.get(0);
		        Log.d("AddNewUserTask", state);
		        userid = Integer.valueOf((String)paraList.get(1));
		        Log.d("userid", ""+userid);
		        
           } catch (Exception e) {
               e.printStackTrace();
           }
			return userid;
		}
		
		protected void onPostExecute(Integer result) 
		{
			Intent intent = new Intent();
			Bundle bundle  = new Bundle();
			if(state.equals("ok"))
			{
				intent.setClass(SignUpInfoActivity.this, MainNeutron.class);
				bundle.putInt("userid", userid);
				intent.putExtras(bundle);		Log.d("post userid", ""+userid);
				startActivity(intent);
				setResult(RESULT_FIRST_USER, new Intent());
				finish();
			}
			else
			{
				new AlertDialog.Builder(SignUpInfoActivity.this)
                .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                .setTitle("OOPS")
                .setMessage("We cannot make a new user for you!")
                .create().show();
			}

			
		}
		
	}

}
