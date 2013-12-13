package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.neutronstar.neutron.weibosso.WBAuthActivity;

public class Welcome extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
	}

	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if(requestCode==requestCode){
	        if(resultCode==2){
	            setTitle("Cancel****");
	        }else 
	            if(resultCode==1){
//	              String  Name=data.getStringExtra("username");
	            Bundle bundle = data.getExtras();
	            String Name = bundle.getString("username");
	            TextView mText = (TextView) findViewById(R.id.yy_test);
	    		mText.setText(Name);
	        }
	    }
	}

	   
	public void welcome_login(View v) {
		Intent intent = new Intent();
		intent.setClass(Welcome.this, Login.class);
		startActivity(intent);
		// this.finish();
		
	}

	//add by yy. 微博SSO认证
	public void welcome_login_weibo(View v) {
		Intent intent = new Intent();
		intent.setClass(Welcome.this, WBAuthActivity.class);
		startActivity(intent);
		
//		TextView mText = (TextView) findViewById(R.id.yy_test);
//		mText.setText("Step Two: fry egg");
		
//		this.finish();
	}
	
	public void welcome_register(View v) {
		Intent intent = new Intent();
		intent.setClass(Welcome.this, MainNeutron.class);
		startActivity(intent);
		// this.finish();
	}

}
