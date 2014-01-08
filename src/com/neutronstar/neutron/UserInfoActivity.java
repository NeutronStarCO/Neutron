package com.neutronstar.neutron;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
	private RelativeLayout rlChangeRelation;
	private RelativeLayout rlChangeUsertype;
	private RelativeLayout rlChangeIDD;
	private RelativeLayout rlChangePhoneNumber;
	
	private PopupWindow pwIDD;
	
	private static final int AVATAR_REQUEST_CODE = 1;
	private static final int NAME_REQUEST_CODE = 2;
	private static final int GENDER_REQUEST_CODE = 3;
	private static final int BIRTHDAY_REQUEST_CODE = 4;
	private static final int RELATION_REQUEST_CODE = 5;
	private static final int IMAGE_REQUEST_CODE = 10;
	private static final int CAMERA_REQUEST_CODE = 11;
	private static final int RESIZE_REQUEST_CODE = 12;
	
	private static final String IMAGE_FILE_NAME = "header.jpg";
	private String[] items = new String[] {"ѡ�񱾵�ͼƬ", "����"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		// ��ʼ�����ݿ⹤��
		ndb = NeutronDbHelper.GetInstance(this);
		// ��ô�������
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
		
		// ��ʼ���ý����Ϊ�½���һ���û�����ʽ
		switch(usage)
		{
		case MainTabFamily.TAG_ADD:
			ivAvatar.setImageDrawable(getResources().getDrawable(R.drawable.xiaohei_big));
			tvName.setHint(getResources().getString(R.string.user_info_hint_name));
			tvGender.setHint(getResources().getString(R.string.user_info_hint_gender));
			tvBirthday.setHint(getResources().getString(R.string.user_info_hint_birthday));
			tvRelation.setHint(getResources().getString(R.string.user_info_hint_relation));
			tvUserType.setHint(getResources().getString(R.string.user_info_hint_usertype));
			tvIDD.setHint(getResources().getString(R.string.user_info_hint_idd));
			tvPhoneNumber.setHint(getResources().getString(R.string.user_info_hint_phone_number));
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
				pwIDD.setHeight(getWindowManager().getDefaultDisplay().getHeight() / 2);
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
							rlChangeIDD.setVisibility(View.GONE);
							break;
						case 1:
							rlChangeIDD.setVisibility(View.VISIBLE);
						}
						pwIDD.dismiss();
						pwIDD = null;
					}
				});	
				
			}
		});
		
		rlChangeAvatar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog();
//				Intent intent = new Intent(UserInfoActivity.this,ChangeAvatar.class);
//				startActivityForResult(intent,1);
			}
		});
		
		rlChangeName.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserInfoActivity.this,ChangeName.class);
				Bundle bundle = new Bundle();
				String name = "";
				if (tvName.getText() != null)
				{
					name = tvName.getText().toString();
				}	
				bundle.putString("name", name);
				intent.putExtras(bundle);
				startActivityForResult(intent,NAME_REQUEST_CODE);
			}
		});
		rlChangeGender.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserInfoActivity.this,ChangeGender.class);
				Bundle bundle = new Bundle();
				String gender = tvGender.getText().toString();
				bundle.putString("gender", gender);
				intent.putExtras(bundle);
				startActivityForResult(intent,GENDER_REQUEST_CODE);
			}
		});
		rlChangeBirthday.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserInfoActivity.this,ChangeBirthday.class);
				Bundle bundle = new Bundle();
				String birthday = "";
				if (!TextUtils.isEmpty(tvBirthday.getText()))
				{
					birthday = tvBirthday.getText().toString();
				}				
				bundle.putString("birthday", birthday);
				intent.putExtras(bundle);
				startActivityForResult(intent,BIRTHDAY_REQUEST_CODE);
			}
		});
		rlChangeRelation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserInfoActivity.this,ChangeRelation.class);
				Bundle bundle = new Bundle();
				String relation = tvRelation.getText().toString();
				int position = findRelation(relation);
				bundle.putString("relation", relation);
				bundle.putInt("pos", position);
				intent.putExtras(bundle);
				startActivityForResult(intent,RELATION_REQUEST_CODE);				
			}
		});
	}
	
	private void showDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this).setTitle("����ͷ��").setItems(items, new DialogInterface.OnClickListener() {
			
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
						Toast.makeText(UserInfoActivity.this, "�����sd��", Toast.LENGTH_LONG).show();
					}
					break;
				}
			}
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
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
//					String result = data.getExtras().getString("");
					break;
				case NAME_REQUEST_CODE:
					String changedName = data.getExtras().getString("name");
					tvName.setText(changedName);
					break;
				case GENDER_REQUEST_CODE:
					String changeGender = data.getExtras().getString("gender");
					tvGender.setText(changeGender);
					break;
				case BIRTHDAY_REQUEST_CODE:
					String changeBirthday = data.getExtras().getString("birthday");
					tvBirthday.setText(changeBirthday);
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
						Toast.makeText(UserInfoActivity.this, "δ�ҵ��洢��", Toast.LENGTH_LONG).show();
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
		SQLiteDatabase db = ndb.getWritableDatabase();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		Bitmap bitmap = ((BitmapDrawable) MainNeutron.instance.getResources().getDrawable(R.drawable.avatar_male)).getBitmap();
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
	
	//return ��ϵ����ֵ
	public int findRelation(String relation) {
		int result = 0;
		if(relation.equals("�ֵ�"))
		{
			result = USER.brother;
		}
		else if (relation.equals("Ů��"))
		{
			result = USER.daughter;
		}
		else if(relation.equals("����"))
		{
			result = USER.father;
		}
		else if(relation.equals("����"))
		{
			result = USER.friends;
		}
		else if(relation.equals("����"))
		{
			result = USER.grandma;
		}
		else if(relation.equals("үү"))
		{
			result = USER.grandpa;
		}
		else if(relation.equals("ĸ��"))
		{
			result = USER.mother;
		}
		else if(relation.equals("�ɷ�"))
		{
			result = USER.husband;
		}
		else if(relation.equals("����"))
		{
			result = USER.sister;
		}
		else if(relation.equals("����"))
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
