package com.neutronstar.neutron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class UserInfoActivity extends Activity {
	private NeutronDbHelper ndb;
	private Intent intent;
	private Bundle bl;
	private int usage;

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
		// 初始化让界面成为新建立一个用户的形式
		switch(usage)
		{
		case MainTabFamily.TAG_ADD:
			((TextView)findViewById(R.id.user_info_gender_content)).setText("male");
		}
	}

}
