package com.neutronstar.neutron;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neutron.server.persistence.model.T_relation;
import com.neutron.server.persistence.model.T_user;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.SERVER;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronContract.USER;
import com.neutronstar.neutron.model.FamilyMemberEntity;

public class UserInfoActivity extends Activity {
	public static final int TAG_ADD_USER = 1;
	public static final int TAG_MODIFY_USER = 2;
	private NeutronDbHelper ndb;
	private Intent intent;
	private Bundle bl;
	private int tag;
	private T_user storedUser;
	private ImageView ivAvatar;
	private TextView tvName;
	private TextView tvGender;
	private TextView tvBirthday;
	private TextView tvRelation;
	private TextView tvUserType;
	private TextView tvIDD;
	private TextView tvIDDName;
	private TextView tvPhoneNumber;
	private RelativeLayout rlChangeAvatar;
	private RelativeLayout rlChangeName;
	private RelativeLayout rlChangeGender;
	private RelativeLayout rlChangeBirthday;
	private Calendar c = Calendar.getInstance();
	private RelativeLayout rlChangeRelation;
	private RelativeLayout rlChangeUsertype;
	private RelativeLayout rlChangeIDD;
	private RelativeLayout rlChangePhoneNumber;
	private Button btSave;
	private FamilyMemberEntity fme;
	private FamilyMemberEntity afterModified;
	
	private PopupWindow pwIDD;
	
	private static final int AVATAR_REQUEST_CODE = 1;
	private static final int NAME_REQUEST_CODE = 2;
	private static final int RELATION_REQUEST_CODE = 5;
	private static final int PHONE_NUMBER_REQUEST_CODE = 6;
	
	private static final int IMAGE_REQUEST_CODE = 10;
	private static final int CAMERA_REQUEST_CODE = 11;
	private static final int RESIZE_REQUEST_CODE = 12;
	
	private static final String IMAGE_FILE_NAME = "header.jpg";
	private String[] items = new String[] {"选择本地图片", "拍照"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		// 初始化数据库工具
		ndb = NeutronDbHelper.GetInstance(this);
		// 获得传递数据
		intent = this.getIntent();
		bl = intent.getExtras();
		tag = bl.getInt("tag");
		ivAvatar = (ImageView)findViewById(R.id.user_info_avatar_content);
		tvName = (TextView)findViewById(R.id.user_info_name_content);
		tvGender = (TextView)findViewById(R.id.user_info_gender_content);
		tvBirthday = (TextView)findViewById(R.id.user_info_birthday_content);
		tvRelation = (TextView)findViewById(R.id.user_info_relation_content);
		tvUserType = (TextView)findViewById(R.id.user_info_usertype_content);
		tvIDD = (TextView)findViewById(R.id.user_info_idd_content);
		tvIDDName = (TextView)findViewById(R.id.user_info_idd_name);
		tvPhoneNumber = (TextView)findViewById(R.id.user_info_phonenumber_content);
		rlChangeUsertype = (RelativeLayout)findViewById(R.id.user_change_usertype);
		rlChangeIDD = (RelativeLayout)findViewById(R.id.user_change_idd);
		rlChangePhoneNumber = (RelativeLayout)findViewById(R.id.user_change_phone_number);
		rlChangeAvatar = (RelativeLayout)findViewById(R.id.user_change_avatar);
		rlChangeName = (RelativeLayout)findViewById(R.id.user_change_name);
		rlChangeGender = (RelativeLayout)findViewById(R.id.user_change_gender);
		rlChangeBirthday = (RelativeLayout)findViewById(R.id.user_change_birthday);
		rlChangeRelation = (RelativeLayout)findViewById(R.id.user_change_relation);
		btSave = (Button)findViewById(R.id.user_info_save);
		
		// 初始化让界面成为新建立一个用户的形式
		switch(tag)
		{
		case UserInfoActivity.TAG_ADD_USER:
			rlChangeUsertype.setBackgroundResource(R.drawable.preference_single_item);
			rlChangeIDD.setVisibility(View.GONE);
			rlChangePhoneNumber.setVisibility(View.GONE);
			rlChangeAvatar.setVisibility(View.GONE);
			rlChangeName.setVisibility(View.GONE);
			rlChangeGender.setVisibility(View.GONE);
			rlChangeBirthday.setVisibility(View.GONE);
			rlChangeRelation.setVisibility(View.GONE);
			btSave.setEnabled(false);
			break;
		case UserInfoActivity.TAG_MODIFY_USER:
			fme = (FamilyMemberEntity) bl.getSerializable("family_member_entity");
			((TextView)findViewById(R.id.user_info_title)).setText(fme.getName());
			tvUserType.setText(getResources().getStringArray(R.array.user_type)[fme.getType()]);
			tvIDD.setText(fme.getIDD());
			tvPhoneNumber.setText(fme.getPhoneNumber());
			ivAvatar.setImageBitmap(fme.getAvatar());
			tvName.setText(fme.getName());
			tvGender.setText(fme.getGender()==0 ? getResources().getString(R.string.female):getResources().getString(R.string.male));
			tvBirthday.setText(new SimpleDateFormat(getResources().getString(R.string.dateformat_birthday)).format(fme.getBirthday()));
			tvRelation.setText(getResources().getStringArray(R.array.relations)[fme.getRelation()]);
			if(fme.getType() == USER.registered && fme.getRelation() == USER.me)
			{				
				rlChangeUsertype.setEnabled(false);
				rlChangeIDD.setEnabled(false);
				rlChangePhoneNumber.setEnabled(false);
				rlChangeRelation.setEnabled(false);
			}
			else if(fme.getType() == USER.registered && fme.getRelation() != USER.me)
			{
				rlChangeUsertype.setEnabled(false);
				rlChangeIDD.setEnabled(false);
				rlChangePhoneNumber.setEnabled(false);
				rlChangeAvatar.setEnabled(false);
				rlChangeName.setEnabled(false);
				rlChangeGender.setEnabled(false);
				rlChangeBirthday.setEnabled(false);
				btSave.setEnabled(false);
			}
			else if(fme.getType() == USER.subregister)
			{
				rlChangeUsertype.setBackgroundResource(R.drawable.preference_single_item);
				rlChangeUsertype.setEnabled(false);
				rlChangeIDD.setVisibility(View.GONE);
				rlChangePhoneNumber.setVisibility(View.GONE);
			}
			break;
		}
		
		ivAvatar.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ivAvatar.setDrawingCacheEnabled(true);
				Bitmap bitmap = Bitmap.createBitmap(ivAvatar.getDrawingCache());
				ivAvatar.setDrawingCacheEnabled(false);
				
				Intent intent = new Intent(UserInfoActivity.this, Avatar.class);
				Bundle bl = new Bundle();
				bl.putParcelable("avatar", bitmap);
				intent.putExtras(bl);
				startActivity(intent);
			}
		});
		
		rlChangeUsertype.setOnClickListener(new View.OnClickListener() {
			String[] userType = getResources().getStringArray(R.array.user_type);
			public void onClick(View arg0) 
			{
				int x = getWindowManager().getDefaultDisplay().getWidth() / 4;
				int y = getWindowManager().getDefaultDisplay().getHeight() / 5;
				
				LinearLayout llIDD = (LinearLayout) LayoutInflater.from(UserInfoActivity.this).inflate(
						R.layout.tab_today_dialog, null);
				ListView lvIDD = (ListView) llIDD.findViewById(R.id.tab_today_dialog);
				lvIDD.setAdapter(new ArrayAdapter<String>(UserInfoActivity.this,
						R.layout.simple_text_item, R.id.simple_text_item, userType));
				
				pwIDD = new PopupWindow(UserInfoActivity.this);
				pwIDD.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
				pwIDD.setHeight(LayoutParams.WRAP_CONTENT);
				pwIDD.setOutsideTouchable(true);
				pwIDD.setFocusable(true);
				pwIDD.setContentView(llIDD);

				pwIDD.showAtLocation(findViewById(R.id.user_info_usertype_content), Gravity.LEFT | Gravity.TOP, x, y);
				
				lvIDD.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						tvUserType.setText(userType[arg2]);
						switch(tag){
						case UserInfoActivity.TAG_ADD_USER:
							switch(arg2){
							case 0:
								rlChangeUsertype.setBackgroundResource(R.drawable.preference_first_item);
								rlChangeIDD.setVisibility(View.VISIBLE);
								rlChangePhoneNumber.setVisibility(View.VISIBLE);
								tvPhoneNumber.setText("");
								rlChangeAvatar.setVisibility(View.GONE);
								rlChangeName.setVisibility(View.GONE);
								rlChangeGender.setVisibility(View.GONE);
								rlChangeBirthday.setVisibility(View.GONE);
								rlChangeRelation.setVisibility(View.GONE);	
								btSave.setEnabled(false);
								break;
							case 1:
								rlChangeUsertype.setBackgroundResource(R.drawable.preference_single_item);
								rlChangeIDD.setVisibility(View.GONE);
								rlChangePhoneNumber.setVisibility(View.GONE);
								rlChangeAvatar.setVisibility(View.VISIBLE);
								rlChangeAvatar.setClickable(true);
								ivAvatar.setImageDrawable(getResources().getDrawable(R.drawable.andy_lau));
								rlChangeName.setVisibility(View.VISIBLE);
								rlChangeName.setClickable(true);
								tvName.setText("");
								rlChangeGender.setVisibility(View.VISIBLE);
								rlChangeGender.setClickable(true);
								tvGender.setText("");
								rlChangeBirthday.setVisibility(View.VISIBLE);
								tvBirthday.setText("");
								rlChangeRelation.setVisibility(View.VISIBLE);
								tvRelation.setText("");
								btSave.setEnabled(false);
								break;
							}
							break;
						case UserInfoActivity.TAG_MODIFY_USER:
							break;
						}						
						pwIDD.dismiss();
						pwIDD = null;
					}
				});					
			}
		});
		
		rlChangeIDD.setOnClickListener(new View.OnClickListener() {
			String[] IDDCountry = getResources().getStringArray(R.array.idd_country);
			String[] IDD = getResources().getStringArray(R.array.idd);
			public void onClick(View view) 
			{
				int x = getWindowManager().getDefaultDisplay().getWidth() / 4;
				int y = getWindowManager().getDefaultDisplay().getHeight() / 5;
				
				LinearLayout llIDD = (LinearLayout) LayoutInflater.from(UserInfoActivity.this).inflate(
						R.layout.tab_today_dialog, null);
				ListView lvIDD = (ListView) llIDD.findViewById(R.id.tab_today_dialog);
				lvIDD.setAdapter(new ArrayAdapter<String>(UserInfoActivity.this,
						R.layout.simple_text_item, R.id.simple_text_item, IDDCountry));
				
				pwIDD = new PopupWindow(UserInfoActivity.this);
				pwIDD.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
				pwIDD.setHeight(getWindowManager().getDefaultDisplay().getHeight() / 2);
				pwIDD.setOutsideTouchable(true);
				pwIDD.setFocusable(true);
				pwIDD.setContentView(llIDD);

				pwIDD.showAtLocation(findViewById(R.id.user_info_idd_content), Gravity.LEFT | Gravity.TOP, x, y);
				
				lvIDD.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						tvIDDName.setText(IDDCountry[arg2]);
						tvIDD.setText(IDD[arg2]);
						pwIDD.dismiss();
						pwIDD = null;
					}
				});				
			}
		});
				
		rlChangePhoneNumber.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(UserInfoActivity.this,ChangeText.class);
				Bundle bundle = new Bundle();
				bundle.putInt("tag", ChangeText.TAG_CHANGE_PHONE_NUMBER);
				bundle.putString("text", tvPhoneNumber.getText() != null? tvPhoneNumber.getText().toString():"");
				bundle.putString("idd", tvIDD.getText().toString());
				intent.putExtras(bundle);
				startActivityForResult(intent,PHONE_NUMBER_REQUEST_CODE);
			}
		});
		
		rlChangeAvatar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog();
			}
		});
		
		rlChangeName.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(UserInfoActivity.this,ChangeText.class);
				Bundle bundle = new Bundle();	
				bundle.putString("text", tvName.getText() != null? tvName.getText().toString():"");
				bundle.putInt("tag", ChangeText.TAG_CHANGE_NAME);
				intent.putExtras(bundle);
				startActivityForResult(intent,NAME_REQUEST_CODE);
			}
		});
		
		rlChangeGender.setOnClickListener(new View.OnClickListener() {
			String[] gender = getResources().getStringArray(R.array.gender);
			public void onClick(View arg0) {
				int x = getWindowManager().getDefaultDisplay().getWidth() / 4;
				int y = getWindowManager().getDefaultDisplay().getHeight() / 5;
				
				LinearLayout llIDD = (LinearLayout) LayoutInflater.from(UserInfoActivity.this).inflate(
						R.layout.tab_today_dialog, null);
				ListView lvIDD = (ListView) llIDD.findViewById(R.id.tab_today_dialog);
				lvIDD.setAdapter(new ArrayAdapter<String>(UserInfoActivity.this,
						R.layout.simple_text_item, R.id.simple_text_item, gender));
				
				pwIDD = new PopupWindow(UserInfoActivity.this);
				pwIDD.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
				pwIDD.setHeight(LayoutParams.WRAP_CONTENT);
				pwIDD.setOutsideTouchable(true);
				pwIDD.setFocusable(true);
				pwIDD.setContentView(llIDD);

				pwIDD.showAtLocation(findViewById(R.id.user_info_gender_content), Gravity.LEFT | Gravity.TOP, x, y);
				
				lvIDD.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						tvGender.setText(gender[arg2]);
						pwIDD.dismiss();
						pwIDD = null;
					}
				});				
			}
		});
		
		rlChangeBirthday.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				new DatePickerDialog(UserInfoActivity.this, new DatePickerDialog.OnDateSetListener() {  					 
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
                    {  
                    	c.set(Calendar.YEAR, year);
                    	c.set(Calendar.MONTH, monthOfYear);
                    	c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    	tvBirthday.setText(new SimpleDateFormat(getResources().getString(R.string.dateformat_birthday)).format(c.getTime()));
                    }  
                }, 1961, 9, 27).show();				
			}
		});
		
		rlChangeRelation.setOnClickListener(new View.OnClickListener() {			
			String[] relations = getResources().getStringArray(R.array.relations);
			public void onClick(View arg0) {
				int x = getWindowManager().getDefaultDisplay().getWidth() / 4;
				int y = getWindowManager().getDefaultDisplay().getHeight() / 5;
				
				LinearLayout llIDD = (LinearLayout) LayoutInflater.from(UserInfoActivity.this).inflate(
						R.layout.tab_today_dialog, null);
				ListView lvIDD = (ListView) llIDD.findViewById(R.id.tab_today_dialog);
				lvIDD.setAdapter(new ArrayAdapter<String>(UserInfoActivity.this,
						R.layout.simple_text_item, R.id.simple_text_item, relations));
				
				pwIDD = new PopupWindow(UserInfoActivity.this);
				pwIDD.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
				pwIDD.setHeight(LayoutParams.WRAP_CONTENT);
				pwIDD.setOutsideTouchable(true);
				pwIDD.setFocusable(true);
				pwIDD.setContentView(llIDD);

				pwIDD.showAtLocation(findViewById(R.id.user_info_gender_content), Gravity.LEFT | Gravity.TOP, x, y);
				
				lvIDD.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						tvRelation.setText(arg2 !=0 ? relations[arg2]:"");
						btSave.setEnabled(arg2 != 0);
						pwIDD.dismiss();
						pwIDD = null;
					}
				});					
			}
		});
	}
	
	private void showDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this).setTitle("更换头像").setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch(which) 
				{
				case 0:
					Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
					galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
					galleryIntent.setType("image/*");
					startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);
					break;
				case 1:
					if (isSdcardExisting()) 
					{
						Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
						cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
						cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
						startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
					} 
					else 
					{
						Toast.makeText(UserInfoActivity.this, "请插入sd卡", Toast.LENGTH_LONG).show();
					}
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
	
	private boolean isSdcardExisting() {
		final String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) 
		{
			return true;
		}
		else 
		{
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
		if (extras != null)
		{
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			ivAvatar.setImageDrawable(drawable);
		}
	}
	
	private Uri getImageUri(){
		return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME));
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode != RESULT_OK)
		{
			return;
		}
		else
		{
			switch(requestCode)
			{
				case AVATAR_REQUEST_CODE:
					break;			
				case PHONE_NUMBER_REQUEST_CODE:
					String changedPhoneNumber = data.getExtras().getString("text");
					tvPhoneNumber.setText(changedPhoneNumber);
					storedUser = (T_user)data.getExtras().getSerializable("user");
					rlChangeAvatar.setVisibility(View.VISIBLE);
					rlChangeAvatar.setClickable(false);
					byte[] in = storedUser.gettUserPicture();
					ivAvatar.setImageBitmap(BitmapFactory.decodeByteArray(in, 0, in.length));
					rlChangeName.setVisibility(View.VISIBLE);
					rlChangeName.setClickable(false);
					tvName.setText(storedUser.gettUserName());
					rlChangeGender.setVisibility(View.VISIBLE);
					rlChangeGender.setClickable(false);
					tvGender.setText(getResources().getStringArray(R.array.gender)[storedUser.gettUserGender()]);
					rlChangeBirthday.setVisibility(View.GONE);
					tvBirthday.setText(storedUser.gettUserBirth());
					rlChangeRelation.setVisibility(View.VISIBLE);
					break;
				case NAME_REQUEST_CODE:
					String changedName = data.getExtras().getString("text");
					tvName.setText(changedName);
					break;
				case RELATION_REQUEST_CODE:
					String changeRelation = data.getExtras().getString("relation");
					tvRelation.setText(changeRelation);
					break;
				case IMAGE_REQUEST_CODE:
					resizeImage(data.getData());
					break;
				case CAMERA_REQUEST_CODE:
					if (isSdcardExisting())
					{
						resizeImage(getImageUri());
					}
					else
					{
						Toast.makeText(UserInfoActivity.this, "未找到存储卡", Toast.LENGTH_LONG).show();
					}
					break;
				case RESIZE_REQUEST_CODE:
					if (data != null)
					{
						showResizeImage(data);
					}
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void save(View v)
	{
		int usertype = Arrays.asList(getResources().getStringArray(R.array.user_type)).indexOf(tvUserType.getText());
		if((usertype == USER.registered && tvPhoneNumber.getText().length() == 0)
			|| tvName.getText().length() == 0
			|| tvGender.getText().length() == 0
			|| tvBirthday.getText().length() == 0
			|| tvRelation.getText().length() == 0)
		{
			Toast.makeText(UserInfoActivity.this, "个人信息不全！", Toast.LENGTH_LONG).show();
		}
		else
		{
			switch(tag) 
			{
			case UserInfoActivity.TAG_ADD_USER:
				int id = -1;
				if(usertype == USER.registered)
					id = storedUser.gettUserId();
				// 如果本地已经存在则不能保存			
				SQLiteDatabase db = ndb.getReadableDatabase();
				String[] projection = {
					    NeutronUser.COLUMN_NAME_ID
					    };
				String selection = "" + NeutronUser.COLUMN_NAME_ID + "=" + id
						+ " AND " + NeutronUser.COLUMN_NAME_TAG + "=" + TAG.normal;
				Cursor cur = db.query(
						NeutronUser.TABLE_NAME,  // The table to query
					    projection,                // The columns to return
					    selection,                 // The columns for the WHERE clause selection
					    null,                      // The values for the WHERE clause selectionArgs
					    null,                      // don't group the rows
					    null,                      // don't filter by row groups
					    null                  		// The sort order
					    );
				if (cur != null) {
					if (cur.moveToFirst()) {
						Toast.makeText(UserInfoActivity.this, "家人已经存在了哦！", Toast.LENGTH_LONG).show();
					}
					else
					{
						int relation = Arrays.asList(getResources().getStringArray(R.array.relations)).indexOf(tvRelation.getText());
						int userType = Arrays.asList(getResources().getStringArray(R.array.user_type)).indexOf(tvUserType.getText());
						NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
				        if (networkInfo != null && networkInfo.isConnected()) 
				        {
				        	if(userType == USER.registered)
				        	{
				        		new AddNewRelationTask().execute(SERVER.Address + "/" + "relation", String.valueOf(storedUser.gettUserId()) );
				        	}
				        	else
				        	{
				        		new AddNewUserTask().execute(SERVER.Address + "/" + "login"
				        				);
				        	}
				        	
				        } else 
				        {
				        	Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG ).show();
				        }
						
					}
				}
				// 1、存储远程用户，更新远程t_user表和t_relation表
				// 2、得到新增用户id，插入本地数据库
				break;
			case UserInfoActivity.TAG_MODIFY_USER:
				afterModified = fme;
				afterModified.setType(Arrays.asList(getResources().getStringArray(R.array.user_type)).indexOf(tvUserType.getText()));
				afterModified.setIDD(tvIDD.getText().toString());
				afterModified.setPhoneNumber(tvPhoneNumber.getText().toString());
				ivAvatar.setDrawingCacheEnabled(true);
				Bitmap bitmap = ivAvatar.getDrawingCache();	
				afterModified.setAvatar(bitmap);
				ivAvatar.setDrawingCacheEnabled(false);				
				afterModified.setName(tvName.getText().toString());
				afterModified.setGender(Arrays.asList(getResources().getStringArray(R.array.gender)).indexOf(tvGender.getText()));
				afterModified.setBirthday(c.getTime());
				afterModified.setRelation(Arrays.asList(getResources().getStringArray(R.array.relations)).indexOf(tvRelation.getText()));
				new UpdateRelationTask().execute(SERVER.Address + "/" + "relation");
//				if( fme.getType() == afterModified.getType()
//						&& fme.getIDD().equals(afterModified.getIDD())
//						&& fme.getPhoneNumber().equals(afterModified.getPhoneNumber())
//						&& fme.getAvatar().equals(afterModified.getAvatar())
//						&& fme.getName().equals(afterModified.getName())
//						&& fme.getGender() == afterModified.getGender()
//						&& fme.getBirthday().equals(afterModified.getBirthday())
//						&& fme.getRelation() == afterModified.getRelation()
//						)
//				{
//					Log.d("Completely the same", "-------");
//					bl.putSerializable("fme_after_modified", fme);
//					intent.putExtras(bl);
//					UserInfoActivity.this.setResult(RESULT_OK, intent);
//					UserInfoActivity.this.finish();
//					return ;
//				}
//				if(fme.getRelation() != afterModified.getRelation())
//				{
//					new UpdateRelationTask().execute(SERVER.Address + "/" + "relation");
//				}
				
				break;
			}	
		}
	}
	
	public void cancel_back(View v)
	{
		this.finish();
	}
	
	public void avatar_enlarge(View v)
	{
		Intent intent = new Intent();
		intent.setClass(UserInfoActivity.this, Avatar.class);
		startActivity(intent);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private class AddNewRelationTask extends AsyncTask<String, Void, String> 
	{
		String state = "";
		T_relation tRelation; 
		int relation;
		int slaveId;
		int type;
		protected String doInBackground(String... params) {
			String strUrl = params[0];
			type = Arrays.asList(getResources().getStringArray(R.array.user_type)).indexOf(tvUserType.getText());
			relation = Arrays.asList(getResources().getStringArray(R.array.relations)).indexOf(tvRelation.getText());
			tRelation = new T_relation(); 
			tRelation.settRelationMasterId(UserInfoActivity.this.getIntent().getExtras().getInt("userid"));
			slaveId = Integer.valueOf(params[1]);
			tRelation.settRelationSalveId(slaveId);
			tRelation.settRelationRelation(relation);
			if(type == USER.subregister)
				tRelation.settRelationConfirmtag(String.valueOf(TAG.accepted));
			else
				tRelation.settRelationConfirmtag(String.valueOf(TAG.offered));
			tRelation.settRelationDeltag(String.valueOf(TAG.normal));
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
		        paraList.add(tRelation);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		  
		        ObjectInputStream ois = new ObjectInputStream(urlConn.getInputStream());  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        state = (String)paraList.get(0);
			} catch (Exception e) {
	               e.printStackTrace();
	           }
			return null;
		}
		
		
		protected void onPostExecute(String result) 
		{
			T_user user = new T_user();
			if(state.equals("ok"))
			{
				if(type == USER.registered)
				{
					SQLiteDatabase db = ndb.getWritableDatabase();
					ContentValues cv = new ContentValues();
					cv.put(NeutronUser.COLUMN_NAME_ID, storedUser.gettUserId());
					cv.put(NeutronUser.COLUMN_NAME_NAME, storedUser.gettUserName());
					cv.put(NeutronUser.COLUMN_NAME_GENDER, storedUser.gettUserGender());
					cv.put(NeutronUser.COLUMN_NAME_BIRTHDAY, storedUser.gettUserBirth());
					cv.put(NeutronUser.COLUMN_NAME_RELATION, relation);
					cv.put(NeutronUser.COLUNM_NAME_RELATION_TAG, TAG.offered);
					cv.put(NeutronUser.COLUMN_NAME_IDD, storedUser.gettUserAreacode());
					cv.put(NeutronUser.COLUMN_NAME_PHONE_NUMBER, storedUser.gettUserPhonenumber());
					cv.put(NeutronUser.COLUMN_NAME_TYPE, storedUser.gettUserRegtag());
					cv.put(NeutronUser.COLUMN_NAME_AVATAR, storedUser.gettUserPicture());
					cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.normal);
					db.insert(NeutronUser.TABLE_NAME, null, cv); 
					user = storedUser;
					bl.putInt("relation_tag", TAG.offered);
				}
				else
				{
					SQLiteDatabase db = ndb.getWritableDatabase();
					ContentValues cv = new ContentValues();
					cv.put(NeutronUser.COLUMN_NAME_ID, slaveId);
					cv.put(NeutronUser.COLUMN_NAME_NAME, tvName.getText().toString());					
					cv.put(NeutronUser.COLUMN_NAME_GENDER, Arrays.asList(getResources().getStringArray(R.array.gender)).indexOf(tvGender.getText()));
					cv.put(NeutronUser.COLUMN_NAME_BIRTHDAY, new SimpleDateFormat(getResources().getString(R.string.dateformat_birthday_in_database)).format(c.getTime()));
					cv.put(NeutronUser.COLUMN_NAME_RELATION, relation);
					cv.put(NeutronUser.COLUNM_NAME_RELATION_TAG, TAG.accepted);
					cv.put(NeutronUser.COLUMN_NAME_TYPE, USER.subregister);
					ivAvatar.setDrawingCacheEnabled(true);
					Bitmap bitmap = ivAvatar.getDrawingCache();			
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					ivAvatar.setDrawingCacheEnabled(false);
					cv.put(NeutronUser.COLUMN_NAME_AVATAR, baos.toByteArray());
					cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.normal);
					db.insert(NeutronUser.TABLE_NAME, null, cv); 
					user.settUserId(slaveId);
					user.settUserName(tvName.getText().toString());
					user.settUserGender(Arrays.asList(getResources().getStringArray(R.array.gender)).indexOf(tvGender.getText()));
					user.settUserBirth(new SimpleDateFormat(getResources().getString(R.string.dateformat_birthday_in_database)).format(c.getTime()));
					user.settUserRegtag(USER.subregister);
					user.settUserPicture(baos.toByteArray());
					user.settUserDeltag(String.valueOf(TAG.normal));
					bl.putInt("relation_tag", TAG.accepted);
				}
				bl.putInt("relation", relation);
				bl.putSerializable("t_user", user);
				intent.putExtras(bl);
				UserInfoActivity.this.setResult(RESULT_OK, intent);
				UserInfoActivity.this.finish();
			}
		}
		
	}
	
	private class AddNewUserTask extends AsyncTask<String, Void, String> 
	{
		String state = "";
		int userid = 0;
		T_user tUser; 
		protected String doInBackground(String... params) {
			String strUrl = params[0];
			tUser = new T_user(); 
			tUser.settUserName(tvName.getText().toString());
			tUser.settUserGender(Arrays.asList(getResources().getStringArray(R.array.gender)).indexOf(tvGender.getText()));
			tUser.settUserBirth(new SimpleDateFormat(getResources().getString(R.string.dateformat_birthday_in_database)).format(c.getTime()));
			tUser.settUserRegtag(USER.subregister);
			ivAvatar.setDrawingCacheEnabled(true);
			Bitmap bitmap = ivAvatar.getDrawingCache();			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			ivAvatar.setDrawingCacheEnabled(false);
			tUser.settUserPicture(baos.toByteArray());
			tUser.settUserAvatar("PNG");
			
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
		        paraList.add(tUser);
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
			return null;
		}
		
		
		protected void onPostExecute(String result) 
		{
			if(state.equals("ok"))
			{
				// 插入关系数据
				new AddNewRelationTask().execute(SERVER.Address + "/" + "relation", String.valueOf(userid));
			}
		}		
	}
	
	private class UpdateRelationTask extends AsyncTask<String, Void, String> 
	{
		String state = "";
		T_relation tRelation; 
		protected String doInBackground(String... params) {
			String strUrl = params[0];
			tRelation = new T_relation(); 
			tRelation.settRelationMasterId(UserInfoActivity.this.getIntent().getExtras().getInt("userid"));
			tRelation.settRelationSalveId(afterModified.getId());
			tRelation.settRelationRelation(afterModified.getRelation());
			tRelation.settRelationConfirmtag(String.valueOf(afterModified.getRelationTag()));
			tRelation.settRelationDeltag(String.valueOf(TAG.normal));
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
		        paraList.add("updateWithMS");
		        paraList.add(tRelation);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		  
		        ObjectInputStream ois = new ObjectInputStream(urlConn.getInputStream());  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        state = (String)paraList.get(0);
			} catch (Exception e) {
	               e.printStackTrace();
	           }
			return null;
		}
		
		
		protected void onPostExecute(String result) 
		{
			T_user user = new T_user();
			if(state.equals("ok"))
			{			
				SQLiteDatabase db = ndb.getWritableDatabase();
				ContentValues cv = new ContentValues();				
				cv.put(NeutronUser.COLUMN_NAME_RELATION, afterModified.getRelation());
				String whereClause = NeutronUser.COLUMN_NAME_ID + "=?";
				String[] whereArgs = {String.valueOf(afterModified.getId())};
				db.update(NeutronUser.TABLE_NAME, cv, whereClause, whereArgs);

				new UpdateUserTask().execute(SERVER.Address + "/" + "login");
			}
		}	
	}
	
	private class UpdateUserTask extends AsyncTask<String, Void, String> 
	{
		String state = "";
		T_user tUser; 
		protected String doInBackground(String... params) {
			String strUrl = params[0];
			tUser = new T_user(); 
			tUser.settUserId(afterModified.getId());
			tUser.settUserName(afterModified.getName());
			tUser.settUserGender(afterModified.getGender());
			tUser.settUserBirth(new SimpleDateFormat(getResources().getString(R.string.dateformat_birthday_in_database)).format(afterModified.getBirthday()));
			tUser.settUserRegtag(afterModified.getType());
			ivAvatar.setDrawingCacheEnabled(true);
			Bitmap bitmap = ivAvatar.getDrawingCache();			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			ivAvatar.setDrawingCacheEnabled(false);
			tUser.settUserPicture(baos.toByteArray());
			tUser.settUserAvatar("PNG");
			tUser.settUserDeltag(String.valueOf(TAG.normal));
			
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
		        paraList.add("updateWithBlob");
		        paraList.add(tUser);
		        oos.writeObject(paraList);  
		        oos.flush();  
		        oos.close();  
		  
		        ObjectInputStream ois = new ObjectInputStream(urlConn.getInputStream());  
		        paraList = (ArrayList<Serializable>)ois.readObject();
		        state = (String)paraList.get(0);
			} catch (Exception e) {
	               e.printStackTrace();
	           }
			return null;
		}
		
		
		protected void onPostExecute(String result) 
		{
			T_user user = new T_user();
			if(state.equals("ok"))
			{			
				SQLiteDatabase db = ndb.getWritableDatabase();
				ContentValues cv = new ContentValues();
				cv.put(NeutronUser.COLUMN_NAME_NAME, afterModified.getName());					
				cv.put(NeutronUser.COLUMN_NAME_GENDER, afterModified.getGender());
				cv.put(NeutronUser.COLUMN_NAME_BIRTHDAY, new SimpleDateFormat(getResources().getString(R.string.dateformat_birthday_in_database)).format(afterModified.getBirthday()));
				cv.put(NeutronUser.COLUMN_NAME_RELATION, afterModified.getRelation());
				ivAvatar.setDrawingCacheEnabled(true);
				Bitmap bitmap = ivAvatar.getDrawingCache();			
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				ivAvatar.setDrawingCacheEnabled(false);
				cv.put(NeutronUser.COLUMN_NAME_AVATAR, baos.toByteArray());
				String whereClause = NeutronUser.COLUMN_NAME_ID + "=?";
				String[] whereArgs = {String.valueOf(afterModified.getId())};
				db.update(NeutronUser.TABLE_NAME, cv, whereClause, whereArgs); 				

				bl.putSerializable("fme_after_modified", afterModified);
				intent.putExtras(bl);
				UserInfoActivity.this.setResult(RESULT_OK, intent);
				UserInfoActivity.this.finish();
			}
		}	
	}

}
