package com.neutronstar.neutron;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.neutron.server.persistence.model.T_user;
import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronContract.USER;
import com.neutronstar.neutron.model.FamilyMemberEntity;

public class UserInfoActivity extends Activity {
	private NeutronDbHelper ndb;
	private Intent intent;
	private Bundle bl;
	private int usage;
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
		usage = bl.getInt("usage");
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
		btSave.setEnabled(false);
		
		// 初始化让界面成为新建立一个用户的形式
		switch(usage)
		{
		case MainTabFamily.TAG_ADD:
			rlChangeUsertype.setBackgroundResource(R.drawable.preference_single_item);
			rlChangeIDD.setVisibility(View.GONE);
			rlChangePhoneNumber.setVisibility(View.GONE);
			rlChangeAvatar.setVisibility(View.GONE);
			rlChangeName.setVisibility(View.GONE);
			rlChangeGender.setVisibility(View.GONE);
			rlChangeBirthday.setVisibility(View.GONE);
			rlChangeRelation.setVisibility(View.GONE);
			break;
		case MainTabFamily.TAG_MODIFY:
			FamilyMemberEntity fme = (FamilyMemberEntity) bl.getSerializable("family_member_entity");
			((TextView)findViewById(R.id.user_info_title)).setText(fme.getName());
			ivAvatar.setImageBitmap(fme.getAvatar());
			tvName.setText(fme.getName());
			tvGender.setText(fme.getGender()==0 ? getResources().getString(R.string.female):getResources().getString(R.string.male));
			tvBirthday.setText(new SimpleDateFormat(getResources().getString(R.string.dateformat_birthday)).format(fme.getBirthday()));
			tvRelation.setText(getResources().getStringArray(R.array.relations)[fme.getRelation()]);
			tvUserType.setText(getResources().getStringArray(R.array.user_type)[fme.getType()]);
			tvIDD.setText(fme.getIDD());
			tvPhoneNumber.setText(fme.getPhoneNumber());
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
						switch(arg2){
						case 0:
							rlChangeUsertype.setBackgroundResource(R.drawable.preference_first_item);
							rlChangeIDD.setVisibility(View.VISIBLE);
							rlChangePhoneNumber.setVisibility(View.VISIBLE);
							rlChangeAvatar.setVisibility(View.GONE);
							rlChangeName.setVisibility(View.GONE);
							rlChangeGender.setVisibility(View.GONE);
							rlChangeBirthday.setVisibility(View.GONE);
							rlChangeRelation.setVisibility(View.GONE);													
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
					T_user user = (T_user)data.getExtras().getSerializable("user");
					rlChangeAvatar.setVisibility(View.VISIBLE);
					rlChangeAvatar.setClickable(false);
					byte[] in = user.gettUserPicture();
					Log.d("byte length", "" + in.length);
					ivAvatar.setImageBitmap(BitmapFactory.decodeByteArray(in, 0, in.length));
					rlChangeName.setVisibility(View.VISIBLE);
					rlChangeName.setClickable(false);
					tvName.setText(user.gettUserName());
					rlChangeGender.setVisibility(View.VISIBLE);
					rlChangeGender.setClickable(false);
					tvGender.setText(getResources().getStringArray(R.array.gender)[user.gettUserGender()]);
					rlChangeBirthday.setVisibility(View.GONE);
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
		if(tvName.getText().length() != 0
				&& tvGender.getText().length() != 0
				&& tvBirthday.getText().length() != 0
				&& tvRelation.getText().length() != 0)
		{
			Toast.makeText(UserInfoActivity.this, "个人信息不全！", Toast.LENGTH_LONG).show();
		}
		else
		{
			// 1、存储远程用户，更新远程t_user表和t_relation表
			// 2、得到新增用户id，插入本地数据库
		}
		SQLiteDatabase db = ndb.getWritableDatabase();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ivAvatar.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(ivAvatar.getDrawingCache());
		ivAvatar.setDrawingCacheEnabled(false);
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); 
		ContentValues cv = new ContentValues(); 
		
		String name = tvName.getText().toString();
		int gender = tvGender.getText().toString().equals(getResources().getString(R.string.male))? 1:0;
		String birthday = tvBirthday.getText().toString();
		int relation = findRelation(tvRelation.getText().toString());
						
		cv.put(NeutronUser.COLUMN_NAME_ID, 5);
		cv.put(NeutronUser.COLUMN_NAME_NAME, name);
		cv.put(NeutronUser.COLUMN_NAME_GENDER, gender);
		cv.put(NeutronUser.COLUMN_NAME_BIRTHDAY, birthday);
		cv.put(NeutronUser.COLUMN_NAME_RELATION, relation);
		cv.put(NeutronUser.COLUMN_NAME_TYPE, USER.registered);
		cv.put(NeutronUser.COLUMN_NAME_AVATAR, baos.toByteArray());
		cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.normal);
		long result = db.insert(NeutronUser.TABLE_NAME, null, cv); 
		
		bl.putInt("id", 4);
		bl.putString("name", name);
		bl.putInt("gender", gender);
		bl.putString("birthday", birthday);
		bl.putInt("relation", relation);
		bl.putParcelable("avatar", bitmap);
		bl.putInt("usertype", USER.registered);
		intent.putExtras(bl);
		UserInfoActivity.this.setResult(RESULT_OK, intent);
		UserInfoActivity.this.finish();
	}
	
	//return 关系的数值
	public int findRelation(String relation) {
		int result = 0;
		if(relation.equals("兄弟"))
		{
			result = USER.brother;
		}
		else if (relation.equals("女儿"))
		{
			result = USER.daughter;
		}
		else if(relation.equals("父亲"))
		{
			result = USER.father;
		}
		else if(relation.equals("朋友"))
		{
			result = USER.friends;
		}
		else if(relation.equals("奶奶"))
		{
			result = USER.grandma;
		}
		else if(relation.equals("爷爷"))
		{
			result = USER.grandpa;
		}
		else if(relation.equals("母亲"))
		{
			result = USER.mother;
		}
		else if(relation.equals("丈夫"))
		{
			result = USER.husband;
		}
		else if(relation.equals("姐妹"))
		{
			result = USER.sister;
		}
		else if(relation.equals("妻子"))
		{
			result = USER.wife;
		}

		return result;
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

}
