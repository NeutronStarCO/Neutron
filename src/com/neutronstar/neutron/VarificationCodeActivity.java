package com.neutronstar.neutron;

import com.neutronstar.neutron.NeutronContract.SERVER;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class VarificationCodeActivity extends Activity {
	public static final int TAG_LOGIN = 1;
	public static final int TAG_SIGN_IN = 2;
	private Intent intent;
	private Bundle bl;
	private int tag;
	private String phonenumber;
	private String IDD;
	private EditText etVarificationCode;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_varification_code);
		intent = this.getIntent();
		bl = intent.getExtras();
		tag = bl.getInt("tag");
		IDD = bl.getString("IDD");
		phonenumber = bl.getString("phonenumber");
		etVarificationCode = (EditText) findViewById(R.id.varification_code_code);
		getRemoteVarificationCode("getpasscode", IDD, phonenumber);
	}
	
	private void getRemoteVarificationCode(String strServlet, String IDD, String phonenumber)
	{
		String strUrl = SERVER.Address + "/" + strServlet;	
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
//            new GetRemoteVarificationCodeTask().execute(strUrl, IDD, phonenumber);
        } else {
        	Toast toast = Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG );
			toast.show();
        }
	}
	
	public void back(View view)
	{
		this.finish();
	}
	
	public void next(View view)
	{
//		if(etVarificationCode.length() == 6)
		{
			Intent intent = new Intent();
			switch(tag)
			{
			case TAG_LOGIN:		
				intent.setClass(VarificationCodeActivity.this, Login.class);
				startActivityForResult(intent, 0);
				break;
			case TAG_SIGN_IN:
				intent.setClass(VarificationCodeActivity.this, SignUpInfoActivity.class);
				startActivityForResult(intent, 0);
				break;
			}
		}
//		else
		{
			Toast toast = Toast.makeText(this, "验证码应为6位数字.", Toast.LENGTH_LONG );
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
}
