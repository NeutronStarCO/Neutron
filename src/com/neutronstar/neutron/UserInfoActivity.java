package com.neutronstar.neutron;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;

import com.neutronstar.neutron.NeutronContract.NeutronUser;
import com.neutronstar.neutron.NeutronContract.TAG;
import com.neutronstar.neutron.NeutronContract.USER;

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
	private TextView tvId;
	private RelativeLayout rlChangename;

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
		tvId = (TextView)findViewById(R.id.user_info_id_content);
		rlChangename = (RelativeLayout)findViewById(R.id.user_change_name);
		// 初始化让界面成为新建立一个用户的形式
		switch(usage)
		{
		case MainTabFamily.TAG_ADD:
			ivAvatar.setImageDrawable(getResources().getDrawable(R.drawable.mini_avatar_shadow));
			tvName.setHint(getResources().getString(R.string.user_info_hint_name));
			tvGender.setHint(getResources().getString(R.string.user_info_hint_gender));
			tvBirthday.setHint(getResources().getString(R.string.user_info_hint_birthday));
			tvRelation.setHint(getResources().getString(R.string.user_info_hint_relation));
			tvUserType.setHint(getResources().getString(R.string.user_info_hint_usertype));
			tvId.setHint(getResources().getString(R.string.user_info_hint_id));
			break;
		}
		rlChangename.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserInfoActivity.this,changename.class);
				Bundle bundle = new Bundle();
				
				bundle.putString("name", "name");
				intent.putExtras(bundle);
				startActivityForResult(intent,2);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(requestCode){
			case 1:
				String result = data.getExtras().getString("");
			case 2:
				String changedName = data.getExtras().getString("name");
				tvName.setText(changedName);
		}
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
		cv.put(NeutronUser.COLUMN_NAME_ID, 3);
		cv.put(NeutronUser.COLUMN_NAME_NAME, "詹姆斯·卡梅隆");
		cv.put(NeutronUser.COLUMN_NAME_GENDER, "male");
		cv.put(NeutronUser.COLUMN_NAME_BIRTHDAY, "1954-08-16");
		cv.put(NeutronUser.COLUMN_NAME_RELATION, USER.father);
		cv.put(NeutronUser.COLUMN_NAME_TYPE, USER.registered);
		cv.put(NeutronUser.COLUMN_NAME_AVATAR, baos.toByteArray());
		cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.normal);
		long result = db.insert(NeutronUser.TABLE_NAME, null, cv); 
		
		bl.putInt("id", 3);
		bl.putString("name", "詹姆斯·卡梅隆");
		bl.putString("gender", "male");
		bl.putString("birthday", "1954-08-16");
		bl.putInt("relation", USER.father);
		bl.putParcelable("avatar", bitmap);
		bl.putInt("usertype", USER.registered);
		intent.putExtras(bl);
		UserInfoActivity.this.setResult(RESULT_OK, intent);
		UserInfoActivity.this.finish();
	}
	
	public void cancel_back(View v)
	{
		this.finish();
	}

}
