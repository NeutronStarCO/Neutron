package com.neutronstar.neutron.push;

import com.baidu.android.pushservice.PushConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import com.baidu.android.pushservice.PushConstants;
import android.util.Log;

public class PushMessageReceiver extends BroadcastReceiver {

	public static final String TAG = PushMessageReceiver.class.getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG,">>> Receive intent: \r\n" + intent);
		if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			String message = intent.getExtras().getString(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
			
			Log.i(TAG, "onMessage:" + message);
			Log.d(TAG, "EXTRA_EXTRA=" + intent.getStringExtra(PushConstants.EXTRA_EXTRA));
		}
		else if(intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
			
			final String method = intent.getStringExtra(PushConstants.EXTRA_METHOD);
			int errorCode = intent.getIntExtra(PushConstants.EXTRA_ERROR_CODE, PushConstants.ERROR_SUCCESS);
			String content = "";
			if(intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT) != null) {
				content = new String(intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));
			}
			
		}
		else if(intent.getAction().equals(PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) {
			Log.d(TAG, "intent=" + intent.toUri(0));
			Log.d(TAG, "" + intent.getStringExtra(PushConstants.EXTRA_EXTRA));
		}
	}

}
