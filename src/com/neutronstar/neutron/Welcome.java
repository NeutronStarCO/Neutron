package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.neutronstar.neutron.weibosso.WBAuthActivity;

public class Welcome extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
	}

	public void welcome_login_weibo(View v) {
		Intent intent = new Intent();
		intent.setClass(Welcome.this, WBAuthActivity.class);
		startActivity(intent);
		
		System.out.println("here,here");
	}
	
	public void login(View v) {
		Intent intent = new Intent();
		Bundle bl = new Bundle();
		bl.putInt("tag", PhoneNumberActivity.TAG_LOGIN);
		intent.putExtras(bl);
		intent.setClass(Welcome.this, PhoneNumberActivity.class);
		startActivity(intent);		
//		Intent intent = new Intent();
//		intent.setClass(Welcome.this, MainNeutron.class);
//		startActivity(intent);
	}

	
	public void sign_in(View v) {
		Intent intent = new Intent();
		Bundle bl = new Bundle();
		bl.putInt("tag", PhoneNumberActivity.TAG_SIGN_IN);
		intent.putExtras(bl);
		intent.setClass(Welcome.this, PhoneNumberActivity.class);
		startActivity(intent);	
	}
}
