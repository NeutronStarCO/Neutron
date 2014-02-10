package com.neutronstar.neutron.push;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.pushservice.PushConstants;
import com.neutronstar.neutron.Appstart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import com.baidu.android.pushservice.PushConstants;
import android.util.Log;
import android.widget.Toast;

public class PushMessageReceiver extends BroadcastReceiver {

	public static final String TAG = PushMessageReceiver.class.getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG,">>> Receive intent: \r\n" + intent);
		if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			// 获取消息内容
			String message = intent.getExtras().getString(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
			
			Log.i(TAG, "onMessage:" + message);
			Log.d(TAG, "EXTRA_EXTRA=" + intent.getStringExtra(PushConstants.EXTRA_EXTRA));
			Intent responseIntent = null;
	        responseIntent = new Intent(Utils.ACTION_MESSAGE);
	        responseIntent.putExtra(Utils.EXTRA_MESSAGE, message);
	        responseIntent.setClass(context, Appstart.class);
	        responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        context.startActivity(responseIntent);
		}
		else if(intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
			
			final String method = intent.getStringExtra(PushConstants.EXTRA_METHOD);
			int errorCode = intent.getIntExtra(PushConstants.EXTRA_ERROR_CODE, PushConstants.ERROR_SUCCESS);
			String content = "";
			if(intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT) != null) {
				content = new String(intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));
			}
			
			 // 用户在此自定义处理消息,以下代码为demo界面展示用
            Log.d(TAG, "onMessage: method : " + method);
            Log.d(TAG, "onMessage: result : " + errorCode);
            Log.d(TAG, "onMessage: content : " + content);
            Toast.makeText(
                    context,
                    "method : " + method + "\n result: " + errorCode
                            + "\n content = " + content, Toast.LENGTH_SHORT)
                    .show();

            Intent responseIntent = null;
            responseIntent = new Intent(Utils.ACTION_RESPONSE);
            responseIntent.putExtra(Utils.RESPONSE_METHOD, method);
            responseIntent.putExtra(Utils.RESPONSE_ERRCODE,
                    errorCode);
            responseIntent.putExtra(Utils.RESPONSE_CONTENT, content);
            responseIntent.setClass(context, Appstart.class);
            responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(responseIntent);
		}
		else if(intent.getAction().equals(PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) {
			Log.d(TAG, "intent=" + intent.toUri(0));
			  // 自定义内容的json串
            String customData = intent
                    .getStringExtra(PushConstants.EXTRA_EXTRA);
			Log.d(TAG, "" + intent.getStringExtra(PushConstants.EXTRA_EXTRA));
			
			 if (customData == null || "".equals(customData)) {
	                return;
	            }

	            Intent aIntent = new Intent();
	            aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            aIntent.setClass(
	                    context,
	                    DemoApp.class);
	            String title = intent
	                    .getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);
	            aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_TITLE, title);
	            String content = intent
	                    .getStringExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT);
	            aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT, content);

	            String detailContent = "";
	            try {
	                org.json.JSONObject json = new JSONObject(customData);
	                detailContent = json.getString("detailContent");
	            } catch (JSONException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
//	            // 保存在数据库
//	            NotifyDao dao = new NotifyDao();
//	            int notifyId = dao.saveNotify(title, content, detailContent);
//	            // 向消息详细页发送内容
//	            aIntent.putExtra("notify_id", notifyId);

	            context.startActivity(aIntent);
			
		}
	}

}
