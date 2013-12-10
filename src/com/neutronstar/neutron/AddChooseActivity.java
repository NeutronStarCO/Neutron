package com.neutronstar.neutron;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddChooseActivity extends Activity {
	private static final int REQ_CODE_MANUAL_INPUT = 1;
	private static final int REQ_CODE_TAKE_PIC = 2;
	private static final int REQ_CODE_CHOOSE_PIC = 3;

	private Intent intent;
	private Bundle bl;
	private Uri photoUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_choose);
		intent = this.getIntent();
		bl = intent.getExtras();
	}
	
	public void takePicture(View v){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		startActivityForResult(intent, REQ_CODE_TAKE_PIC);
	}
	
	public void choosePicture(View v){ 
		Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQ_CODE_CHOOSE_PIC);
	}

	
	public void manualInput(View v){
		Intent intent = new Intent(AddChooseActivity.this, NewRecordActivity.class);
		intent.putExtras(bl);
		startActivityForResult(intent,REQ_CODE_MANUAL_INPUT);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == REQ_CODE_MANUAL_INPUT && resultCode == RESULT_OK)
		{
			bl= data.getExtras();
			intent.putExtras(bl);
			AddChooseActivity.this.setResult(RESULT_OK, intent);
			AddChooseActivity.this.finish();
		}
		if(requestCode == REQ_CODE_CHOOSE_PIC && resultCode == RESULT_OK)
		{
			Uri uri = data.getData();
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);
			cursor.moveToFirst();
			String path =  cursor.getString(1); 
			Log.d("path:", path);
		}
		if(requestCode == REQ_CODE_TAKE_PIC && resultCode == RESULT_OK)
		{
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {  
					Log.v("TestFile", "SD card is not avaiable/writeable right now."); 
				} 
			else{
				Bundle bundle = data.getExtras(); 
				// 获取相机返回的数据，并转换为Bitmap图片格式 
				Bitmap bitmap = (Bitmap) bundle.get("data");
				FileOutputStream b = null; 
				String str = null; 
				Date date = null; 
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				date = new Date(System.currentTimeMillis()); 
				str = format.format(date); 
				String fileName = Environment.getExternalStorageDirectory().toString() 
						+ "/"+ getResources().getString(R.string.file_root_dir) 
						+ "/" + str + ".jpg"; 
				File photo = new File(fileName);				
				if(!photo.getParentFile().isDirectory())
					photo.getParentFile().mkdirs();;
				if(!photo.exists())
				{
					try 
					{
						photo.createNewFile();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				try 
				{ 
					b = new FileOutputStream(fileName); 
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件 
				} catch (FileNotFoundException e) 
				{ 
					e.printStackTrace(); 
				} finally 
				{ 
					try { 
						b.flush(); 
						b.close(); 
					} catch (IOException e) { 
						e.printStackTrace(); 
					} 
				} 
			}
		}
		this.finish();
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

}
