package com.neutronstar.neutron;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ChangeAvatar extends Activity implements OnClickListener{

	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESIZE_REQUEST_CODE = 2;
	
	private static final String IMAGE_FILE_NAME = "header.jpg";
	
	private ImageView ivAvatar;
	private RelativeLayout rlAvatar;
	private String[] items = new String[] {"选择本地照片", "拍照"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_change_avatar);
		setupViews();
	}
	
	private void setupViews()
	{
		ivAvatar = (ImageView)findViewById(R.id.user_info_avatar_content_1);
		rlAvatar = (RelativeLayout)findViewById(R.id.user_change_avatar_1);
		final Button selectBtn1 = (Button)findViewById(R.id.btn_selectimage);
		final Button selectBtn2 = (Button)findViewById(R.id.btn_takephoto);
		selectBtn1.setOnClickListener(this);
		selectBtn2.setOnClickListener(this);
		rlAvatar.setOnClickListener(this);
	}
	
	public void save(View v) {

		this.finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_selectimage:
			Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
			galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
			galleryIntent.setType("image/*");
			startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);
			break;
		case R.id.btn_takephoto:
			if (isSdcardExisting()) {
				Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
				cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
				startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
			} else {
				Toast.makeText(v.getContext(), "请插入sd卡", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.user_change_avatar_1:
			showDialog();
			break;
		}
	}
	
	private void showDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this).setTitle("更换头像").setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch(which) {
				case 0:
					Toast.makeText(ChangeAvatar.this,"Hello 0", Toast.LENGTH_LONG).show();
					break;
				case 1:
					Toast.makeText(ChangeAvatar.this,"Hello 1", Toast.LENGTH_LONG).show();
					break;
				}
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode != RESULT_OK){
			return;
		}
		else {
			switch(requestCode){
			case IMAGE_REQUEST_CODE:
				resizeImage(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (isSdcardExisting()){
					resizeImage(getImageUri());
				}
				else{
					Toast.makeText(ChangeAvatar.this, "未找到存储卡", Toast.LENGTH_LONG).show();
				}
				break;
			case RESIZE_REQUEST_CODE:
				if (data != null){
					showResizeImage(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
	    	this.finish();
	    } else if(keyCode == KeyEvent.KEYCODE_MENU) {
	        //监控/拦截菜单键
	    } else if(keyCode == KeyEvent.KEYCODE_HOME) {
	        //由于Home键为系统键，此处不能捕获，需要重写onAttachedToWindow()
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	private static View getRootView(Activity context)
	{
		return ((ViewGroup)context.findViewById(android.R.id.content)).getChildAt(0);
	}
	
	private boolean isSdcardExisting() {
		final String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		else {
			return false;
			}
	}
	
	public void resizeImage(Uri uri){
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESIZE_REQUEST_CODE);
	}
	
	private void showResizeImage(Intent data){
		Bundle extras = data.getExtras();
		if (extras != null){
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			ivAvatar.setImageDrawable(drawable);
		}
	}
	
	private Uri getImageUri(){
		return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME));
	}

}
