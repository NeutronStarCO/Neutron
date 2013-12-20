package com.neutronstar.neutron;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
	private RelativeLayout rlChangeName;
	private RelativeLayout rlChangeGender;
	private RelativeLayout rlChangeBirthday;
	private RelativeLayout rlChangeRelation;

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
		rlChangeName = (RelativeLayout)findViewById(R.id.user_change_name);
		rlChangeGender = (RelativeLayout)findViewById(R.id.user_change_gender);
		rlChangeBirthday = (RelativeLayout)findViewById(R.id.user_change_birthday);
		rlChangeRelation = (RelativeLayout)findViewById(R.id.user_change_relation);
		
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
		rlChangeName.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserInfoActivity.this,ChangeName.class);
				Bundle bundle = new Bundle();
				String name = tvName.getText().toString();
				Log.i("tvName",name);
				bundle.putString("name", name);
				intent.putExtras(bundle);
				startActivityForResult(intent,2);
			}
		});
		rlChangeGender.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserInfoActivity.this,ChangeGender.class);
				startActivityForResult(intent,3);
			}
		});
		rlChangeBirthday.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserInfoActivity.this,ChangeBirthday.class);
				startActivityForResult(intent,4);
			}
		});
		rlChangeRelation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserInfoActivity.this,ChangeRelation.class);
				startActivityForResult(intent,5);
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
			case 3:
				String changeGender = data.getExtras().getString("gender");
				tvGender.setText(changeGender);
			case 4:
				String changeBirthday = data.getExtras().getString("birthday");
				tvBirthday.setText(changeBirthday);
			case 5:
				String changeRelation = data.getExtras().getString("relation");
				tvRelation.setText(changeRelation);
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
		
		String name = tvName.getText().toString();
		String gender = tvGender.getText().toString();
		String birthday = tvBirthday.getText().toString();
		int relation = findRelation(tvRelation.getText().toString());
						
		cv.put(NeutronUser.COLUMN_NAME_ID, 4);
		cv.put(NeutronUser.COLUMN_NAME_NAME, name);
		cv.put(NeutronUser.COLUMN_NAME_GENDER, gender);
		cv.put(NeutronUser.COLUMN_NAME_BIRTHDAY, birthday);
		cv.put(NeutronUser.COLUMN_NAME_RELATION, relation);
		cv.put(NeutronUser.COLUMN_NAME_TYPE, USER.registered);
		cv.put(NeutronUser.COLUMN_NAME_AVATAR, baos.toByteArray());
		cv.put(NeutronUser.COLUMN_NAME_TAG, TAG.normal);
		long result = db.insert(NeutronUser.TABLE_NAME, null, cv); 
		
		bl.putInt("id", 3);
		bl.putString("name", name);
		bl.putString("gender", "male");
		bl.putString("birthday", "1954-08-16");
		bl.putInt("relation", USER.father);
		bl.putParcelable("avatar", bitmap);
		bl.putInt("usertype", USER.registered);
		intent.putExtras(bl);
		UserInfoActivity.this.setResult(RESULT_OK, intent);
		UserInfoActivity.this.finish();
	}
	
	//return 关系的数值
	public int findRelation(String relation) {
		int result = 0;
		if(relation == "兄弟")
		{
			result = USER.brother;
		}
		else if (relation == "女儿")
		{
			result = USER.daughter;
		}
		else if(relation == "父亲")
		{
			result = USER.father;
		}
		else if(relation == "朋友")
		{
			result = USER.friends;
		}
		else if(relation == "奶奶")
		{
			result = USER.grandma;
		}
		else if(relation == "爷爷")
		{
			result = USER.grandpa;
		}
		else if(relation == "母亲")
		{
			result = USER.mother;
		}
		else if(relation == "丈夫")
		{
			result = USER.husband;
		}
		else if(relation == "姐妹")
		{
			result = USER.sister;
		}
		else if(relation == "妻子")
		{
			result = USER.wife;
		}

		return result;
	}
	
	public void cancel_back(View v)
	{
		this.finish();
	}

}
