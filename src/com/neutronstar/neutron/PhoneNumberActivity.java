package com.neutronstar.neutron;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.neutron.server.persistence.model.T_user;
import com.neutronstar.neutron.NeutronContract.SERVER;

public class PhoneNumberActivity extends Activity {
	public static final int TAG_LOGIN = 1;
	public static final int TAG_SIGN_IN = 2;
	private final int requestCode = 1;
	private int tag;
	private TextView tvPhoneNumber;
	private Button tvIDDbutton;
	private TextView tvIDD;
	private CheckBox cbTerms;
	private TextView tvOthers;
	private String IDDCountry[];
	private String IDD[];
	private PopupWindow pwIDD;
	private T_user localUser;
	private T_user remoteUser;
	private String passcode;
	private TelephonyManager tm;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_number);
		Intent intent = this.getIntent();
		Bundle bl = intent.getExtras();
		tag = bl.getInt("tag");
		tvPhoneNumber = (TextView) findViewById(R.id.phone_number_number);
		tvPhoneNumber.requestFocus();
		tvIDDbutton = (Button) findViewById(R.id.phone_number_IDDbutton);
		tvIDD = (TextView) findViewById(R.id.phone_number_IDD);
		tvOthers = (TextView) findViewById(R.id.phone_number_others);
		cbTerms = (CheckBox) findViewById(R.id.phone_number_terms);
		IDDCountry = getResources().getStringArray(R.array.idd_country);
		IDD = getResources().getStringArray(R.array.idd);
		tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		
		switch(tag)
		{
		case TAG_LOGIN:
			tvOthers.setVisibility(View.GONE);
			break;
		case TAG_SIGN_IN:
			break;
		}	
	}
	
	public void back(View view)
	{
		this.finish();
	}

	public void next(View view)
	{
		if(!cbTerms.isChecked())
		{
			Toast toast = Toast.makeText(this, "您还没有同意用户协议", Toast.LENGTH_LONG );
			toast.show();
		}
		else if(tvPhoneNumber.getText().length() <= 8)
		{
			Toast toast = Toast.makeText(this, "你确信您的手机号码少于8位吗？", Toast.LENGTH_LONG );
			toast.show();
		}
		else
		{
			Intent intent = new Intent(PhoneNumberActivity.this, ConfirmationDialogActivity.class);
			Bundle bl = new Bundle();
			bl.putInt("tag", ConfirmationDialogActivity.TAG_PHONE_NUMBER);
			bl.putString("IDD", tvIDD.getText().toString());
			bl.putString("phonenumber", tvPhoneNumber.getText().toString());
			intent.putExtras(bl);
			startActivityForResult(intent, requestCode);
		}
	}
	
	public void chooseIDD(View view)
	{
		int x = getWindowManager().getDefaultDisplay().getWidth() / 4;
		int y = getWindowManager().getDefaultDisplay().getHeight() / 5;
		showPopupWindow(x, y);
	}
	
	public void showPopupWindow(int x, int y) 
	{
		
		LinearLayout llIDD = (LinearLayout) LayoutInflater.from(PhoneNumberActivity.this).inflate(
				R.layout.tab_today_dialog, null);
		ListView lvIDD = (ListView) llIDD.findViewById(R.id.tab_today_dialog);
		lvIDD.setAdapter(new ArrayAdapter<String>(PhoneNumberActivity.this,
				R.layout.simple_text_item, R.id.simple_text_item, IDDCountry));
		
		pwIDD = new PopupWindow(PhoneNumberActivity.this);
		pwIDD.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
		pwIDD.setHeight(getWindowManager().getDefaultDisplay().getHeight() / 2);
		pwIDD.setOutsideTouchable(true);
		pwIDD.setFocusable(true);
		pwIDD.setContentView(llIDD);

		pwIDD.showAtLocation(findViewById(R.id.phone_number_linearlayout), Gravity.LEFT | Gravity.TOP, x, y);
		
		lvIDD.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				tvIDDbutton.setText(IDDCountry[arg2]);
				tvIDD.setText(IDD[arg2]);
				pwIDD.dismiss();
				pwIDD = null;
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{	
		switch(resultCode){
		case RESULT_OK:
			Bundle bl = data.getExtras();
			boolean confirmation = bl.getBoolean("confirmation");
			if(confirmation)
			{
				localUser = new T_user();
				localUser.settUserAreacode(tvIDD.getText().toString());
				localUser.settUserPhonenumber(tvPhoneNumber.getText().toString());
				getPasscode("passcode", localUser);
			}
			else
			{
				// Nothing to do!
			}
			break;
		case RESULT_FIRST_USER:
			this.setResult(RESULT_FIRST_USER, new Intent());
			this.finish();
			break;
		}
	}
	
	private void getPasscode(String strServlet, T_user user)
	{
		String strUrl = SERVER.Address + "/" + strServlet;	
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new GetPasscodeTask().execute(strUrl);
        } else {
        	Toast toast = Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG );
			toast.show();
        }
	}
	
	private class GetPasscodeTask extends AsyncTask<String, Void, String> 
	{
		String state = "";
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
		        paraList.add("getpasscode");
		        paraList.add(localUser);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		  
		        ObjectInputStream ois = new ObjectInputStream(urlConn.getInputStream());  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        state = (String)paraList.get(0);
		        remoteUser = (T_user)paraList.get(1);
		        passcode = remoteUser.gettUserPasscode();
		        
           } catch (Exception e) {
               e.printStackTrace();
           }
			return passcode;
		}
		
		protected void onPostExecute(String result) 
		{
			Intent intent = new Intent();
			Bundle bundle  = new Bundle();
			if(state.equals("new"))
			{
				switch(tag)
				{
				case TAG_LOGIN:
					new AlertDialog.Builder(PhoneNumberActivity.this)
                    .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                    .setTitle("电话号码错误")
                    .setMessage("您输入的号码尚未注册，请确认号码后再获取验证码！")
                    .create().show();
					break;
				case TAG_SIGN_IN:
					intent = new Intent(PhoneNumberActivity.this, VarificationCodeActivity.class);
					bundle.putInt("tag", VarificationCodeActivity.TAG_SIGN_IN);
					bundle.putString("IDD", tvIDD.getText().toString());
					bundle.putString("phonenumber", tvPhoneNumber.getText().toString());
					bundle.putString("passcode", result);
					intent.putExtras(bundle);
					startActivityForResult(intent, 0);
					break;
				}
			}
			else if (state.equals("registered"))
			{
				switch(tag)
				{
				case TAG_LOGIN:	
					if(tm.getDeviceId().equals(remoteUser.gettUserImei()) || tm.getSubscriberId().equals(remoteUser.gettUserImsi()))
					{
						intent = new Intent(PhoneNumberActivity.this, VarificationCodeActivity.class);
						bundle.putInt("tag", VarificationCodeActivity.TAG_LOGIN);
						bundle.putString("IDD", tvIDD.getText().toString());
						bundle.putString("phonenumber", tvPhoneNumber.getText().toString());
						bundle.putString("passcode", result);
						bundle.putInt("userid", remoteUser.gettUserId());
						bundle.putSerializable("t_user", remoteUser);
						intent.putExtras(bundle);
						startActivityForResult(intent, 0);
					}
					else
					{
						new AlertDialog.Builder(PhoneNumberActivity.this)
	                    .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
	                    .setTitle("设备不对")
	                    .setMessage("您未使用注册的电话或sim卡来登录注册用户！")
	                    .create().show();
					}
					
					break;
				case TAG_SIGN_IN:
					new AlertDialog.Builder(PhoneNumberActivity.this)
                    .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                    .setTitle("电话号码错误")
                    .setMessage("您输入的号码已经注册，请确认号码后再获取验证码！")
                    .create().show();
					break;
				}
			}
			else
			{
				new AlertDialog.Builder(PhoneNumberActivity.this)
                .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                .setTitle("failed to get passcode")
                .setMessage("failed to get passcode!")
                .create().show();
			}
		}
	}
}
