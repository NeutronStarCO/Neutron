package com.neutronstar.neutron;

import java.io.ByteArrayOutputStream;
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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.neutron.server.persistence.model.T_user;
import com.neutronstar.neutron.NeutronContract.GENDER;
import com.neutronstar.neutron.NeutronContract.NeutronGroupTesting;
import com.neutronstar.neutron.NeutronContract.NeutronRecord;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.SERVER;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronContract.USER;

public class SignUpInfoActivity extends Activity {
	
	private Intent intent;
	private Bundle bl;
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	private ImageView ivAvatar;
	private TextView tvName;
	private TextView tvBirthday;
	private T_user user;
	private Calendar c = Calendar.getInstance();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private NeutronDbHelper ndb;
	private TelephonyManager tm;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_info);
		ndb = NeutronDbHelper.GetInstance(this);
		intent = this.getIntent();
		bl = intent.getExtras();
		user = new T_user();
		user.settUserAreacode(bl.getString("IDD"));
		user.settUserPhonenumber(bl.getString("phonenumber"));
		user.settUserPasscode(bl.getString("passcode"));
		tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		
		spinner = (Spinner) findViewById(R.id.sign_up_info_spinner);
		ivAvatar = (ImageView) findViewById(R.id.sign_up_info_avatar);
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
			Toast.makeText(this, "好吧，你也叫刘德华，算你狠，欢迎您！", Toast.LENGTH_LONG ).show();
		}			
		else 
			user.settUserName(tvName.getText().toString());
		user.settUserBirth(dateFormat.format(c.getTime()));
		user.settUserGender(spinner.getSelectedItemId() == GENDER.male? GENDER.male:GENDER.female);
		user.settUserRegtag(USER.registered);
		user.settUserDeltag(String.valueOf(TAG.normal));
		user.settUserImei(tm.getDeviceId());
		user.settUserImsi(tm.getSubscriberId());
		user.settUserRegdate(Calendar.getInstance().getTime());
		ivAvatar.setDrawingCacheEnabled(true);
		Bitmap bitmap = ivAvatar.getDrawingCache();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		ivAvatar.setDrawingCacheEnabled(false);
		user.settUserPicture(baos.toByteArray());
		user.settUserAvatar("PNG");
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
			    urlConn.setRequestProperty("Accept-Charset", "utf-8");  
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
		        userid = (Integer)paraList.get(1);
		        
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
				// 清除本地数据表数据
				SQLiteDatabase db = ndb.getWritableDatabase();
				db.execSQL("Delete from " + NeutronUser.TABLE_NAME);
				db.execSQL("Delete from " + NeutronRecord.TABLE_NAME);
				db.execSQL("Delete from " + NeutronGroupTesting.TABLE_NAME);
				// 插入新数据
				ContentValues cv = new ContentValues(); 
				cv.put(NeutronUser.COLUMN_NAME_ID, result);
				cv.put(NeutronUser.COLUMN_NAME_NAME, user.gettUserName());
				cv.put(NeutronUser.COLUMN_NAME_GENDER, user.gettUserGender());
				cv.put(NeutronUser.COLUMN_NAME_BIRTHDAY, user.gettUserBirth());
				cv.put(NeutronUser.COLUMN_NAME_IDD, user.gettUserAreacode());
				cv.put(NeutronUser.COLUMN_NAME_PHONE_NUMBER, user.gettUserPhonenumber());
				cv.put(NeutronUser.COLUMN_NAME_RELATION, USER.me);
				cv.put(NeutronUser.COLUNM_NAME_RELATION_TAG, TAG.accepted);
				cv.put(NeutronUser.COLUMN_NAME_TYPE, USER.registered);
				cv.put(NeutronUser.COLUMN_NAME_PASSCODE, user.gettUserPasscode());
				cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.normal);
				cv.put(NeutronUser.COLUMN_NAME_AVATAR, user.gettUserPicture());
				db.insert(NeutronUser.TABLE_NAME, null, cv); 
				
				intent.setClass(SignUpInfoActivity.this, MainNeutron.class);
				bundle.putInt("userid", result);
				intent.putExtras(bundle);
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
